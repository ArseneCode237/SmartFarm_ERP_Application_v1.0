package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.reseau_partage.animaux.dto.pesee.BilanBandeItem;
import com.reseau_partage.animaux.dto.pesee.BilanPeseesResponse;
import com.reseau_partage.animaux.dto.pesee.BilanTrancheResponse;
import com.reseau_partage.animaux.dto.pesee.IndicateursBandeResponse;
import com.reseau_partage.animaux.dto.pesee.PeseeRequest;
import com.reseau_partage.animaux.dto.pesee.PeseeResponse;
import com.reseau_partage.animaux.dto.pesee.PrevisionSortieResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.animaux.mapper.PeseeMapper;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.ConfigEspece;
import com.reseau_partage.core.entities.CourbeCroissanceReference;
import com.reseau_partage.core.entities.PeriodeBilan;
import com.reseau_partage.core.entities.Pesee;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.ConfigEspeceRepository;
import com.reseau_partage.core.repository.CourbeCroissanceReferenceRepository;
import com.reseau_partage.core.repository.PeseeRepository;

@Service
public class PeseeService {

    private static final BigDecimal SEUIL_SOUS_PERFORMANCE = new BigDecimal("-10");
    private static final int HOMOGENEITE_DECIMALES = 2;

    private final PeseeRepository peseeRepository;
    private final AnimalRepository animalRepository;
    private final BandeRepository bandeRepository;
    private final ConfigEspeceRepository configEspeceRepository;
    private final CourbeCroissanceReferenceRepository courbeRepository;
    private final PeseeMapper mapper;

    public PeseeService(PeseeRepository peseeRepository, AnimalRepository animalRepository, BandeRepository bandeRepository,
                        ConfigEspeceRepository configEspeceRepository, CourbeCroissanceReferenceRepository courbeRepository,
                        PeseeMapper mapper) {
        this.peseeRepository = peseeRepository;
        this.animalRepository = animalRepository;
        this.bandeRepository = bandeRepository;
        this.configEspeceRepository = configEspeceRepository;
        this.courbeRepository = courbeRepository;
        this.mapper = mapper;
    }

    @Transactional
    public PeseeResponse enregistrerPesee(PeseeRequest request) {
        if (request.animalId() == null && request.bandeId() == null) {
            throw new IllegalArgumentException("Une pesee doit referencer un animal ou une bande.");
        }
        Pesee pesee = new Pesee();
        LocalDate date = request.datePesee() != null ? request.datePesee() : LocalDate.now();
        pesee.setDatePesee(date);
        pesee.setPoidsKg(request.poidsKg());

        if (request.animalId() != null
                && peseeRepository.existsByAnimalIdAndDatePesee(request.animalId(), date)) {
            throw new IllegalArgumentException(
                    "Une pesée de cet animal a déjà été enregistrée le " + date + ". Prochaine pesée possible demain.");
        }
        if (request.bandeId() != null
                && peseeRepository.existsByBandeIdAndDatePesee(request.bandeId(), date)) {
            throw new IllegalArgumentException(
                    "Une pesée de cette bande a déjà été enregistrée le " + date + ". Prochaine pesée possible demain.");
        }

        if (request.animalId() != null) {
            Animal animal = animalRepository.findById(request.animalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal", request.animalId()));
            pesee.setAnimal(animal);
            pesee.setAgeJoursAuMomentPesee(animal.getDateNaissance() != null
                    ? (int) ChronoUnit.DAYS.between(animal.getDateNaissance(), date) : null);
            alimenterPeseeAnimale(pesee, animal, date, request.poidsKg());
            referencerPeseePrecedente(pesee, peseeRepository.findTopByAnimalIdOrderByDatePeseeDesc(request.animalId()).orElse(null));
            animal.setPoidsActuelKg(request.poidsKg());
            animal.setDateDernierePesee(date);
            animalRepository.save(animal);
        } else {
            Bande bande = bandeRepository.findById(request.bandeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bande", request.bandeId()));
            pesee.setBande(bande);
            Pesee precedente = dernierePeseeBande(request.bandeId(), date);
            referencerPeseePrecedente(pesee, precedente);
            alimenterPeseeBande(pesee, bande, request.poidsKg(), precedente);
            bande.setPoidsMoyenActuelKg(request.poidsKg());
            bandeRepository.save(bande);
        }
        pesee.setOperateurNom(request.operateurNom());
        pesee.setNotes(request.notes());
        peseeRepository.save(pesee);
        return mapper.toResponse(pesee);
    }

    @Transactional
    public List<PeseeResponse> peseeCollectiveBande(Long bandeId, PeseeRequest request) {
        Bande bande = bandeRepository.findById(bandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bande", bandeId));
        LocalDate dateCollective = request.datePesee() != null ? request.datePesee() : LocalDate.now();
        if (peseeRepository.existsByBandeIdAndDatePesee(bandeId, dateCollective)) {
            throw new IllegalArgumentException(
                    "Une pesée de cette bande a déjà été enregistrée le " + dateCollective + ". Prochaine pesée possible demain.");
        }
        List<Animal> animaux = animalRepository.findByBandeId(bandeId);
        LocalDate date = dateCollective;
        List<PeseeResponse> resultats = new java.util.ArrayList<>();
        for (Animal animal : animaux) {
            if (animal.getStatut() != com.reseau_partage.core.entities.StatutAnimal.ACTIF) {
                continue;
            }
            Pesee p = new Pesee();
            p.setAnimal(animal);
            p.setBande(bande);
            p.setDatePesee(date);
            p.setPoidsKg(request.poidsKg());
            p.setAgeJoursAuMomentPesee(animal.getDateNaissance() != null
                    ? (int) ChronoUnit.DAYS.between(animal.getDateNaissance(), date) : null);
            alimenterPeseeAnimale(p, animal, date, request.poidsKg());
            referencerPeseePrecedente(p, peseeRepository.findTopByAnimalIdOrderByDatePeseeDesc(animal.getId()).orElse(null));
            animal.setPoidsActuelKg(request.poidsKg());
            animal.setDateDernierePesee(date);
            animalRepository.save(animal);
            peseeRepository.save(p);
            resultats.add(mapper.toResponse(p));
        }
        bande.setPoidsMoyenActuelKg(request.poidsKg());
        bandeRepository.save(bande);
        return resultats;
    }

    @Transactional(readOnly = true)
    public List<PeseeResponse> historiqueAnimal(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal", animalId);
        }
        return peseeRepository.findByAnimalIdOrderByDatePeseeDesc(animalId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PeseeResponse> historiqueBande(Long bandeId) {
        if (!bandeRepository.existsById(bandeId)) {
            throw new ResourceNotFoundException("Bande", bandeId);
        }
        return peseeRepository.findByBandeIdOrderByDatePeseeDesc(bandeId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PeseeResponse> courbeCroissance(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal", animalId);
        }
        return peseeRepository.findByAnimalIdOrderByDatePeseeAsc(animalId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PeseeResponse> sousPerformeurs(Long fermeId) {
        if (fermeId == null) {
            return List.of();
        }
        return peseeRepository.findSousPerformeursByFerme(fermeId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public IndicateursBandeResponse indicateursBande(Long bandeId) {
        Bande bande = bandeRepository.findById(bandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bande", bandeId));
        List<Animal> animaux = animalRepository.findByBandeId(bandeId).stream()
                .filter(a -> a.getStatut() == com.reseau_partage.core.entities.StatutAnimal.ACTIF
                        && a.getPoidsActuelKg() != null)
                .toList();
        if (animaux.isEmpty()) {
            return new IndicateursBandeResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L, bande.getEffectifActuel());
        }
        List<BigDecimal> poids = animaux.stream().map(Animal::getPoidsActuelKg).toList();
        BigDecimal somme = poids.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal moyenne = somme.divide(BigDecimal.valueOf(poids.size()), 4, RoundingMode.HALF_UP);
        BigDecimal variance = poids.stream()
                .map(p -> p.subtract(moyenne).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(poids.size()), 4, RoundingMode.HALF_UP);
        BigDecimal ecartType = BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
        BigDecimal cv = moyenne.compareTo(BigDecimal.ZERO) > 0
                ? ecartType.multiply(BigDecimal.valueOf(100)).divide(moyenne, HOMOGENEITE_DECIMALES, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        long nbSous = animaux.stream()
                .filter(a -> estSousPerformeur(a, bande))
                .count();
        return new IndicateursBandeResponse(
                moyenne.setScale(HOMOGENEITE_DECIMALES, RoundingMode.HALF_UP),
                ecartType.setScale(HOMOGENEITE_DECIMALES, RoundingMode.HALF_UP),
                cv, nbSous, bande.getEffectifActuel());
    }

    @Transactional(readOnly = true)
    public PrevisionSortieResponse prevoirSortie(Long bandeId) {
        Bande bande = bandeRepository.findById(bandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bande", bandeId));
        ConfigEspece config = configEspeceRepository.findByEspece(bande.getEspece()).orElse(null);
        if (config == null || config.getAgeCibleAbattageJours() == null || config.getPoidsAbattageCibleKg() == null) {
            throw new IllegalArgumentException("Configuration espèce manquante pour " + bande.getEspece() + " : ageCibleAbattageJours et poidsAbattageCibleKg requis dans ConfigEspece.");
        }
        List<Pesee> pesees = peseeRepository.findByBandeIdOrderByDatePeseeAsc(bandeId);
        if (pesees.size() < 2) {
            throw new IllegalArgumentException("Au moins deux pesees sont necessaires pour la prevision.");
        }
        Pesee premiere = pesees.get(0);
        Pesee derniere = pesees.get(pesees.size() - 1);
        long joursEcoules = ChronoUnit.DAYS.between(premiere.getDatePesee(), derniere.getDatePesee());
        BigDecimal gainTotal = derniere.getPoidsKg().subtract(premiere.getPoidsKg());
        if (joursEcoules <= 0 || gainTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Croissance insuffisante pour estimer une date de sortie.");
        }
        BigDecimal gainParJour = gainTotal.divide(BigDecimal.valueOf(joursEcoules), 6, RoundingMode.HALF_UP);
        BigDecimal reste = config.getPoidsAbattageCibleKg().subtract(derniere.getPoidsKg());
        long joursRestants = reste.divide(gainParJour, 0, RoundingMode.CEILING).longValue();
        LocalDate datePrevue = derniere.getDatePesee().plusDays(joursRestants);
        return new PrevisionSortieResponse(datePrevue, config.getPoidsAbattageCibleKg(), joursRestants);
    }

    private void alimenterPeseeAnimale(Pesee pesee, Animal animal, LocalDate date, BigDecimal poids) {
        Pesee precedente = peseeRepository.findTopByAnimalIdOrderByDatePeseeDesc(animal.getId()).orElse(null);
        if (precedente != null && precedente.getDatePesee().isBefore(date)) {
            BigDecimal gain = poids.subtract(precedente.getPoidsKg());
            pesee.setGainDepuisDernierePeseeKg(gain);
            long jours = ChronoUnit.DAYS.between(precedente.getDatePesee(), date);
            if (jours > 0) {
                pesee.setGmqG(gain.multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(jours), 3, RoundingMode.HALF_UP));
            }
        }
        calculerEcartReference(pesee, animal.getEspece(), animal.getRace(), pesee.getAgeJoursAuMomentPesee(), poids);
    }

    /**
     * Pesée de bande (poids moyen) : calcule le gain depuis la pesée précédente,
     * le GMQ (g/jour/animal) et met à jour la bande :
     *  - gainMoyenQuotidienG = (poids actuel − poids précédent) × 1000 / jours
     *  - fcrCumule (IC) = (ration journalière × jours) / gain par animal
     */
    private void alimenterPeseeBande(Pesee pesee, Bande bande, BigDecimal poids, Pesee precedente) {
        pesee.setPoidsKg(poids);
        if (precedente == null || precedente.getPoidsKg() == null
                || precedente.getDatePesee() == null || pesee.getDatePesee() == null) {
            return;
        }
        long jours = ChronoUnit.DAYS.between(precedente.getDatePesee(), pesee.getDatePesee());
        if (jours <= 0) return;

        BigDecimal deltaKg = poids.subtract(precedente.getPoidsKg());
        pesee.setGainDepuisDernierePeseeKg(deltaKg.setScale(3, RoundingMode.HALF_UP));

        // GMQ en g/jour/animal
        BigDecimal gmq = deltaKg.multiply(BigDecimal.valueOf(1000))
                .divide(BigDecimal.valueOf(jours), 2, RoundingMode.HALF_UP);
        pesee.setGmqG(gmq);
        bande.setGainMoyenQuotidienG(gmq);

        // IC (indice de consommation) estimé : consommation par animal sur la
        // période divisée par le gain de poids moyen par animal.
        // La ration journalière étant par animal, l'effectif se simplifie.
        if (bande.getRationJournaliereKg() != null && deltaKg.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal consommationParAnimal = bande.getRationJournaliereKg()
                    .multiply(BigDecimal.valueOf(jours));
            bande.setFcrCumule(consommationParAnimal.divide(deltaKg, 2, RoundingMode.HALF_UP));
        }
    }

    private void calculerEcartReference(Pesee pesee, com.reseau_partage.core.entities.Espece espece, String race,
                                        Integer ageJours, BigDecimal poids) {
        if (ageJours == null) {
            return;
        }
        CourbeCroissanceReference ref = courbeRepository.findReference(espece, race, ageJours).orElse(null);
        if (ref == null || ref.getPoidsCibleKg() == null || ref.getPoidsCibleKg().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        BigDecimal ecart = poids.subtract(ref.getPoidsCibleKg())
                .divide(ref.getPoidsCibleKg(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        pesee.setEcartCourbeReferencePct(ecart);
        pesee.setSousPerformeur(ecart.compareTo(SEUIL_SOUS_PERFORMANCE) < 0);
    }

    private boolean estSousPerformeur(Animal animal, Bande bande) {
        Pesee derniere = peseeRepository.findTopByAnimalIdOrderByDatePeseeDesc(animal.getId()).orElse(null);
        return derniere != null && Boolean.TRUE.equals(derniere.getSousPerformeur());
    }

    /**
     * Copie sur la nouvelle pesée la référence du poids précédent :
     * poids et date de la dernière pesée enregistrée (même animal ou même bande).
     */
    private void referencerPeseePrecedente(Pesee pesee, Pesee precedente) {
        if (precedente == null) return;
        pesee.setPoidsPrecedentKg(precedente.getPoidsKg());
        pesee.setDatePeseePrecedente(precedente.getDatePesee());
    }

    /**
     * Dernière pesée de bande antérieure à la date donnée.
     */
    private Pesee dernierePeseeBande(Long bandeId, LocalDate avantDate) {
        return peseeRepository.findByBandeIdOrderByDatePeseeDesc(bandeId).stream()
                .filter(p -> p.getDatePesee() != null && p.getDatePesee().isBefore(avantDate))
                .findFirst()
                .orElse(null);
    }

    // =========================================================================
    // BILANS PAR PÉRIODE (SEMAINE / MOIS / TRIMESTRE / SEMESTRE / ANNUEL)
    // =========================================================================

    /**
     * Génère le bilan des pesées agrégé par tranche de période.
     * Seules les tranches contenant au moins une pesée sont retournées.
     */
    @Transactional(readOnly = true)
    public BilanPeseesResponse genererBilan(PeriodeBilan periode, Long fermeId, Long bandeId, Integer annee) {
        int anneeRef = (annee != null && annee > 1900) ? annee : LocalDate.now().getYear();
        List<Pesee> pesees = peseeRepository.findForBilan(fermeId, bandeId);

        Map<String, List<Pesee>> groupes = new LinkedHashMap<>();
        for (Pesee p : pesees) {
            if (p.getDatePesee() == null) continue;
            if (!dansAnneeReference(p.getDatePesee(), periode, anneeRef)) continue;
            String cle = cleTranche(p.getDatePesee(), periode);
            groupes.computeIfAbsent(cle, k -> new ArrayList<>()).add(p);
        }

        List<BilanTrancheResponse> tranches = new ArrayList<>();
        for (Map.Entry<String, List<Pesee>> entry : groupes.entrySet()) {
            List<Pesee> groupe = entry.getValue();
            LocalDate debut = groupe.get(0).getDatePesee();
            LocalDate fin = debut;
            for (Pesee p : groupe) {
                LocalDate d = p.getDatePesee();
                if (d.isBefore(debut)) debut = d;
                if (d.isAfter(fin)) fin = d;
            }
            tranches.add(construireTranche(entry.getKey(),
                    libelleTranche(debut, periode, anneeRef), debut, fin, groupe));
        }

        return new BilanPeseesResponse(periode.name(), anneeRef, (long) pesees.stream()
                .filter(p -> p.getDatePesee() != null)
                .filter(p -> dansAnneeReference(p.getDatePesee(), periode, anneeRef))
                .count(), tranches);
    }

    /** Filtre les pesées hors fenêtre couverte par le bilan. */
    private boolean dansAnneeReference(LocalDate date, PeriodeBilan periode, int anneeRef) {
        if (periode == PeriodeBilan.ANNUEL) {
            return date.getYear() >= anneeRef - 4 && date.getYear() <= anneeRef;
        }
        return date.getYear() == anneeRef;
    }

    /** Clé de regroupement d'une pesée selon la période. */
    private String cleTranche(LocalDate date, PeriodeBilan periode) {
        return switch (periode) {
            case SEMAINE -> date.getYear() + "-S" + String.format("%02d", date.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()));
            case MOIS -> String.format("%d-%02d", date.getYear(), date.getMonthValue());
            case TRIMESTRE -> date.getYear() + "-T" + ((date.getMonthValue() - 1) / 3 + 1);
            case SEMESTRE -> date.getYear() + "-S" + (date.getMonthValue() <= 6 ? 1 : 2);
            case ANNUEL -> String.valueOf(date.getYear());
        };
    }

    /** Libellé lisible d'une tranche. */
    private String libelleTranche(LocalDate debut, PeriodeBilan periode, int anneeRef) {
        return switch (periode) {
            case SEMAINE -> "Semaine du " + debut.toString();
            case MOIS -> debut.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH)
                    + " " + debut.getYear();
            case TRIMESTRE -> "T" + ((debut.getMonthValue() - 1) / 3 + 1) + " " + debut.getYear();
            case SEMESTRE -> "S" + (debut.getMonthValue() <= 6 ? 1 : 2) + " " + debut.getYear();
            case ANNUEL -> String.valueOf(debut.getYear());
        };
    }

    /** Construit les agrégats d'une tranche à partir de ses pesées (triées ASC). */
    private BilanTrancheResponse construireTranche(String cle, String libelle, LocalDate debut, LocalDate fin,
                                                   List<Pesee> pesees) {
        List<BigDecimal> poids = pesees.stream().map(Pesee::getPoidsKg).filter(java.util.Objects::nonNull).toList();

        BigDecimal min = poids.stream().min(BigDecimal::compareTo).orElse(null);
        BigDecimal max = poids.stream().max(BigDecimal::compareTo).orElse(null);
        BigDecimal moyenne = BigDecimal.ZERO;
        if (!poids.isEmpty()) {
            BigDecimal somme = poids.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            moyenne = somme.divide(BigDecimal.valueOf(poids.size()), 3, RoundingMode.HALF_UP);
        }
        BigDecimal gmq = calculerGmqGroupe(pesees);

        long nbBandes = pesees.stream().map(Pesee::getBande).filter(java.util.Objects::nonNull)
                .map(Bande::getId).distinct().count();

        // Détail par bande
        Map<Long, List<Pesee>> parBande = new LinkedHashMap<>();
        for (Pesee p : pesees) {
            if (p.getBande() == null) continue;
            parBande.computeIfAbsent(p.getBande().getId(), k -> new ArrayList<>()).add(p);
        }
        List<BilanBandeItem> bandes = new ArrayList<>();
        for (Map.Entry<Long, List<Pesee>> e : parBande.entrySet()) {
            List<Pesee> gp = e.getValue();
            Bande b = gp.get(0).getBande();
            List<BigDecimal> pb = gp.stream().map(Pesee::getPoidsKg).filter(java.util.Objects::nonNull).toList();
            bandes.add(new BilanBandeItem(
                    b.getId(),
                    b.getCodeBande(),
                    b.getNom(),
                    (long) gp.size(),
                    pb.isEmpty() ? null
                            : pb.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                                    .divide(BigDecimal.valueOf(pb.size()), 3, RoundingMode.HALF_UP),
                    pb.stream().min(BigDecimal::compareTo).orElse(null),
                    pb.stream().max(BigDecimal::compareTo).orElse(null),
                    calculerGmqGroupe(gp)));
        }

        return new BilanTrancheResponse(cle, libelle, debut, fin,
                (long) pesees.size(), nbBandes,
                poids.isEmpty() ? null : moyenne,
                min, max, gmq, bandes);
    }

    /**
     * GMQ moyen (g/jour) d'un groupe de pesées :
     * moyenne des GMQ stockés si disponibles, sinon recalcul linéaire
     * entre la première et la dernière pesée.
     */
    private BigDecimal calculerGmqGroupe(List<Pesee> pesees) {
        List<BigDecimal> gmqValues = pesees.stream()
                .map(Pesee::getGmqG).filter(java.util.Objects::nonNull).toList();
        if (!gmqValues.isEmpty()) {
            return gmqValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(gmqValues.size()), 2, RoundingMode.HALF_UP);
        }
        if (pesees.size() < 2) return null;
        Pesee first = pesees.get(0);
        Pesee last = pesees.get(pesees.size() - 1);
        long jours = ChronoUnit.DAYS.between(first.getDatePesee(), last.getDatePesee());
        if (jours <= 0 || first.getPoidsKg() == null || last.getPoidsKg() == null) return null;
        return last.getPoidsKg().subtract(first.getPoidsKg())
                .multiply(BigDecimal.valueOf(1000))
                .divide(BigDecimal.valueOf(jours), 2, RoundingMode.HALF_UP);
    }
}
