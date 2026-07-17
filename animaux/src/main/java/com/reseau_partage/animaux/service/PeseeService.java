package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (request.animalId() != null) {
            Animal animal = animalRepository.findById(request.animalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal", request.animalId()));
            pesee.setAnimal(animal);
            pesee.setAgeJoursAuMomentPesee(animal.getDateNaissance() != null
                    ? (int) ChronoUnit.DAYS.between(animal.getDateNaissance(), date) : null);
            alimenterPeseeAnimale(pesee, animal, date, request.poidsKg());
            animal.setPoidsActuelKg(request.poidsKg());
            animal.setDateDernierePesee(date);
            animalRepository.save(animal);
        } else {
            Bande bande = bandeRepository.findById(request.bandeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bande", request.bandeId()));
            pesee.setBande(bande);
            alimenterPeseeBande(pesee, bande, request.poidsKg());
            bande.setPoidsMoyenActuelKg(request.poidsKg());
            bandeRepository.save(bande);
        }
        pesee.setDatePesee(date);
        pesee.setPoidsKg(request.poidsKg());
        pesee.setOperateurNom(request.operateurNom());
        pesee.setNotes(request.notes());
        peseeRepository.save(pesee);
        return mapper.toResponse(pesee);
    }

    @Transactional
    public List<PeseeResponse> peseeCollectiveBande(Long bandeId, PeseeRequest request) {
        Bande bande = bandeRepository.findById(bandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bande", bandeId));
        List<Animal> animaux = animalRepository.findByBandeId(bandeId);
        LocalDate date = request.datePesee() != null ? request.datePesee() : LocalDate.now();
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
            throw new IllegalArgumentException("Configuration insuffisante pour la prevision (age/poids cible).");
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

    private void alimenterPeseeBande(Pesee pesee, Bande bande, BigDecimal poids) {
        // pesée collective : pas de GMQ ni écart individuel
        pesee.setPoidsKg(poids);
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
}
