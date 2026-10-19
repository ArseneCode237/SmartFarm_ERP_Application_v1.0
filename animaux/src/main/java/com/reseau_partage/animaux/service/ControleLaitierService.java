package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.bovin.BilanLactationResponse;
import com.reseau_partage.animaux.dto.bovin.ControleLaitierRequest;
import com.reseau_partage.animaux.dto.bovin.ControleLaitierResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.ControleLaitier;
import com.reseau_partage.core.entities.ProfilBovin;
import com.reseau_partage.core.entities.StatutReproductifBovin;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.ControleLaitierRepository;
import com.reseau_partage.core.repository.ProfilBovinRepository;

@Service
public class ControleLaitierService {

    private static final BigDecimal SEUIL_PRODUCTION = new BigDecimal("0.80");
    private static final BigDecimal SEUIL_MAMMITE_MILLIERS = new BigDecimal("200");

    private final AnimalRepository animalRepository;
    private final ProfilBovinRepository profilRepository;
    private final ControleLaitierRepository controleRepository;

    public ControleLaitierService(AnimalRepository animalRepository,
            ProfilBovinRepository profilRepository, ControleLaitierRepository controleRepository) {
        this.animalRepository = animalRepository;
        this.profilRepository = profilRepository;
        this.controleRepository = controleRepository;
    }

    @Transactional
    public ControleLaitierResponse create(ControleLaitierRequest request) {
        Animal animal = animalRepository.findById(request.animalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", request.animalId()));
        ProfilBovin profil = profilRepository.findByAnimalId(animal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil bovin", animal.getId()));
        if (profil.getCategorieBovine() != CategorieBovine.VACHE_LAITIERE
                || !List.of(StatutReproductifBovin.LACTATION, StatutReproductifBovin.SAILLIE,
                        StatutReproductifBovin.GESTATION)
                        .contains(profil.getStatutReproductif())) {
            throw new IllegalArgumentException("Le contrôle laitier nécessite une vache en lactation.");
        }
        LocalDate startDate = profil.getDateDebutLactationActuelle();
        if (startDate == null || request.dateControle().isBefore(startDate)) {
            throw new IllegalArgumentException("La date du contrôle doit être le jour du vêlage ou après celui-ci.");
        }
        if (profil.getDateTarissementPrevu() != null
                && !request.dateControle().isBefore(profil.getDateTarissementPrevu())) {
            throw new IllegalArgumentException("Aucun contrôle laitier ne peut être saisi après le tarissement prévu.");
        }
        Integer lactation = profil.getNumeroLactationActuelle();
        if (controleRepository.existsByAnimalIdAndNumeroLactationAndDateControle(
                animal.getId(), lactation, request.dateControle())) {
            throw new IllegalArgumentException("Un contrôle laitier existe déjà pour cet animal et cette date.");
        }
        BigDecimal total = sum(request.laitMatinKg(), request.laitMidiKg(), request.laitSoirKg());
        if (request.laitMatinKg() == null && request.laitMidiKg() == null && request.laitSoirKg() == null) {
            throw new IllegalArgumentException("Au moins une traite doit être renseignée.");
        }
        BigDecimal target = request.laitCibleJourKg() == null
                ? profil.getProductionLaitCibleJKg() : request.laitCibleJourKg();
        if (target != null && target.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La production cible doit être supérieure à zéro.");
        }

        ControleLaitier controle = new ControleLaitier();
        controle.setAnimalId(animal.getId());
        controle.setFermeId(fermeId(animal));
        controle.setDateControle(request.dateControle());
        controle.setJoursEnLaitAuMoment((int) ChronoUnit.DAYS.between(startDate, request.dateControle()));
        controle.setNumeroLactation(lactation);
        controle.setLaitMatinKg(request.laitMatinKg());
        controle.setLaitMidiKg(request.laitMidiKg());
        controle.setLaitSoirKg(request.laitSoirKg());
        controle.setLaitTotalJourKg(total);
        controle.setLaitCibleJourKg(target);
        controle.setTauxButyreuxPct(request.tauxButyreuxPct());
        controle.setTauxProteiquePct(request.tauxProteiquePct());
        controle.setCellulesSomatiquesMilliers(request.cellulesSomatiquesMilliers());
        controle.setOperateurNom(request.operateurNom());
        controle.setNotes(request.notes());
        if (target != null) {
            controle.setEcartVsCiblePct(total.subtract(target)
                    .multiply(new BigDecimal("100")).divide(target, 2, RoundingMode.HALF_UP));
        }
        BigDecimal cumul = controleRepository.sumLactationMilk(animal.getId(), lactation).add(total);
        controle.setLaitCumulLactationKg(cumul);
        String alert = detectAlert(controle);
        controle.setTypeAlerte(alert);
        controle.setAlerteGeneree(alert != null);
        controle = controleRepository.save(controle);
        profil.setProductionLaitCumulKg(cumul);
        return toResponse(controle);
    }

    @Transactional
    public List<ControleLaitierResponse> createBatch(List<ControleLaitierRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("La liste de contrôles ne peut pas être vide.");
        }
        return requests.stream().map(this::create).toList();
    }

    @Transactional(readOnly = true)
    public List<ControleLaitierResponse> historique(Long animalId) {
        requireProfile(animalId);
        return controleRepository.findByAnimalIdOrderByDateControleAsc(animalId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ControleLaitierResponse> courbe(Long animalId) {
        return historique(animalId);
    }

    @Transactional(readOnly = true)
    public BilanLactationResponse bilan(Long animalId, Integer numeroLactation) {
        ProfilBovin profil = requireProfile(animalId);
        List<ControleLaitier> controls = controleRepository
                .findByAnimalIdAndNumeroLactationOrderByDateControleDesc(animalId, numeroLactation);
        if (controls.isEmpty()) {
            throw new ResourceNotFoundException("Contrôles de lactation", numeroLactation);
        }
        BigDecimal total = controls.stream().map(ControleLaitier::getLaitTotalJourKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ControleLaitier peak = controls.stream()
                .max(Comparator.comparing(ControleLaitier::getLaitTotalJourKg)).orElseThrow();
        LocalDate start = numeroLactation.equals(profil.getNumeroLactationActuelle())
                ? profil.getDateDebutLactationActuelle() : controls.get(controls.size() - 1).getDateControle();
        ControleLaitier first = controls.get(controls.size() - 1);
        long daysElapsed = first.getJoursEnLaitAuMoment() != null && controls.get(0).getJoursEnLaitAuMoment() != null
                ? controls.get(0).getJoursEnLaitAuMoment() - first.getJoursEnLaitAuMoment() + 1
                : ChronoUnit.DAYS.between(start, controls.get(0).getDateControle()) + 1;
        daysElapsed = Math.max(1, daysElapsed);
        BigDecimal estimate305 = daysElapsed >= 305 ? total : total.multiply(new BigDecimal("305"))
                .divide(BigDecimal.valueOf(daysElapsed), 2, RoundingMode.HALF_UP);
        long onTarget = controls.stream().filter(c -> c.getLaitCibleJourKg() != null
                && c.getLaitTotalJourKg().compareTo(c.getLaitCibleJourKg().multiply(SEUIL_PRODUCTION)) >= 0).count();
        BigDecimal persistence = BigDecimal.valueOf(onTarget).multiply(new BigDecimal("100"))
                .divide(BigDecimal.valueOf(controls.size()), 2, RoundingMode.HALF_UP);
        return new BilanLactationResponse(animalId, numeroLactation, total, estimate305,
                peak.getLaitTotalJourKg(), peak.getDateControle(), persistence, controls.size());
    }

    @Transactional(readOnly = true)
    public List<ControleLaitierResponse> sousPerformeuses(Long fermeId) {
        return controleRepository.findAll().stream()
                .filter(c -> fermeId.equals(c.getFermeId()))
                .collect(java.util.stream.Collectors.groupingBy(ControleLaitier::getAnimalId))
                .values().stream()
                .map(items -> items.stream().max(Comparator.comparing(ControleLaitier::getDateControle)).orElseThrow())
                .filter(c -> c.getEcartVsCiblePct() != null
                        && c.getEcartVsCiblePct().compareTo(new BigDecimal("-20")) < 0)
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal productionFerme(Long fermeId, LocalDate debut, LocalDate fin) {
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit précéder la date de fin.");
        }
        return controleRepository.sumMilkByFarmAndPeriod(fermeId, debut, fin);
    }

    private String detectAlert(ControleLaitier current) {
        if (current.getCellulesSomatiquesMilliers() != null
                && BigDecimal.valueOf(current.getCellulesSomatiquesMilliers()).compareTo(SEUIL_MAMMITE_MILLIERS) > 0) {
            return "MAMMITE_SUSPECTEE";
        }
        if (current.getEcartVsCiblePct() != null
                && current.getEcartVsCiblePct().compareTo(new BigDecimal("-20")) < 0) {
            List<ControleLaitier> previous = controleRepository
                    .findByAnimalIdAndNumeroLactationOrderByDateControleDesc(
                            current.getAnimalId(), current.getNumeroLactation()).stream()
                    .filter(c -> c.getDateControle().isBefore(current.getDateControle()))
                    .toList();
            boolean threeDaysBelowTarget = previous.size() >= 2
                    && previous.get(0).getDateControle().plusDays(1).equals(current.getDateControle())
                    && previous.get(1).getDateControle().plusDays(1).equals(previous.get(0).getDateControle())
                    && belowTarget(previous.get(0))
                    && belowTarget(previous.get(1));
            return threeDaysBelowTarget ? "CHUTE_PRODUCTION" : "SOUS_PERFORMEUR";
        }
        return null;
    }

    private boolean belowTarget(ControleLaitier controle) {
        return controle.getLaitCibleJourKg() != null
                && controle.getLaitTotalJourKg().compareTo(
                        controle.getLaitCibleJourKg().multiply(SEUIL_PRODUCTION)) < 0;
    }

    private ProfilBovin requireProfile(Long animalId) {
        return profilRepository.findByAnimalId(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil bovin", animalId));
    }

    private Long fermeId(Animal animal) {
        if (animal.getStructure() == null || animal.getStructure().getSite() == null
                || animal.getStructure().getSite().getFerme() == null) {
            throw new IllegalArgumentException("Le bovin doit être rattaché à une ferme pour enregistrer son lait.");
        }
        return animal.getStructure().getSite().getFerme().getId();
    }

    private BigDecimal sum(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                total = total.add(value);
            }
        }
        return total;
    }

    private ControleLaitierResponse toResponse(ControleLaitier controle) {
        return new ControleLaitierResponse(controle.getId(), controle.getAnimalId(), controle.getDateControle(),
                controle.getNumeroLactation(), controle.getJoursEnLaitAuMoment(), controle.getLaitMatinKg(),
                controle.getLaitMidiKg(), controle.getLaitSoirKg(), controle.getLaitTotalJourKg(),
                controle.getLaitCibleJourKg(), controle.getEcartVsCiblePct(),
                controle.getLaitCumulLactationKg(), controle.getTypeAlerte());
    }
}
