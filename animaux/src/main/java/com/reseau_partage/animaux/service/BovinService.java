package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.bovin.BandeBovinResponse;
import com.reseau_partage.animaux.dto.bovin.BovinRequest;
import com.reseau_partage.animaux.dto.bovin.BovinResponse;
import com.reseau_partage.animaux.dto.bovin.AnimalBovinLieResponse;
import com.reseau_partage.animaux.dto.bovin.ControleLaitierResponse;
import com.reseau_partage.animaux.dto.bovin.GenealogieBovinResponse;
import com.reseau_partage.animaux.dto.bovin.SaillieBovinResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.ProfilBovin;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutReproductifBovin;
import com.reseau_partage.core.entities.StatutStructure;
import com.reseau_partage.core.entities.Structure;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.ControleLaitierRepository;
import com.reseau_partage.core.repository.ProfilBovinRepository;
import com.reseau_partage.core.repository.SaillieBovinRepository;
import com.reseau_partage.core.repository.StructureRepository;
import com.reseau_partage.core.repository.VelageBovinRepository;

@Service
public class BovinService {

    private static final Set<CategorieBovine> FEMALE_CATEGORIES = Set.of(
            CategorieBovine.VACHE_LAITIERE, CategorieBovine.VACHE_ALLAITANTE, CategorieBovine.GENISSE);
    private static final Set<CategorieBovine> MALE_CATEGORIES = Set.of(
            CategorieBovine.TAUREAU_REPRODUCTEUR, CategorieBovine.TAURILLON);
    private static final Map<StatutReproductifBovin, Set<StatutReproductifBovin>> TRANSITIONS = Map.of(
            StatutReproductifBovin.VIDE, Set.of(StatutReproductifBovin.EN_CHALEUR, StatutReproductifBovin.SAILLIE,
                    StatutReproductifBovin.REFORME),
            StatutReproductifBovin.EN_CHALEUR, Set.of(StatutReproductifBovin.SAILLIE,
                    StatutReproductifBovin.VIDE, StatutReproductifBovin.REFORME),
            StatutReproductifBovin.SAILLIE, Set.of(StatutReproductifBovin.GESTATION,
                    StatutReproductifBovin.VIDE, StatutReproductifBovin.REFORME),
            StatutReproductifBovin.GESTATION, Set.of(StatutReproductifBovin.PRE_VELAGE,
                    StatutReproductifBovin.TARISSEMENT, StatutReproductifBovin.POST_PARTUM,
                    StatutReproductifBovin.VIDE, StatutReproductifBovin.REFORME),
            StatutReproductifBovin.PRE_VELAGE, Set.of(StatutReproductifBovin.POST_PARTUM,
                    StatutReproductifBovin.LACTATION, StatutReproductifBovin.REFORME),
            StatutReproductifBovin.POST_PARTUM, Set.of(StatutReproductifBovin.LACTATION,
                    StatutReproductifBovin.VIDE, StatutReproductifBovin.REFORME),
            StatutReproductifBovin.LACTATION, Set.of(StatutReproductifBovin.TARISSEMENT,
                    StatutReproductifBovin.SAILLIE, StatutReproductifBovin.REFORME),
            StatutReproductifBovin.TARISSEMENT, Set.of(StatutReproductifBovin.GESTATION,
                    StatutReproductifBovin.PRE_VELAGE, StatutReproductifBovin.POST_PARTUM,
                    StatutReproductifBovin.VIDE, StatutReproductifBovin.REFORME),
            StatutReproductifBovin.REFORME, Set.of());

    private final AnimalRepository animalRepository;
    private final StructureRepository structureRepository;
    private final ProfilBovinRepository profilRepository;
    private final SaillieBovinRepository saillieRepository;
    private final VelageBovinRepository velageRepository;
    private final ControleLaitierRepository controleRepository;
    private final BandeBovinService bandeBovinService;
    private final SaillieBovinService saillieService;
    private final ControleLaitierService controleLaitierService;

    public BovinService(AnimalRepository animalRepository, StructureRepository structureRepository,
            ProfilBovinRepository profilRepository, SaillieBovinRepository saillieRepository,
            VelageBovinRepository velageRepository, ControleLaitierRepository controleRepository,
            BandeBovinService bandeBovinService, SaillieBovinService saillieService,
            ControleLaitierService controleLaitierService) {
        this.animalRepository = animalRepository;
        this.structureRepository = structureRepository;
        this.profilRepository = profilRepository;
        this.saillieRepository = saillieRepository;
        this.velageRepository = velageRepository;
        this.controleRepository = controleRepository;
        this.bandeBovinService = bandeBovinService;
        this.saillieService = saillieService;
        this.controleLaitierService = controleLaitierService;
    }

    @Transactional
    public BovinResponse create(BovinRequest request) {
        validateCategorySex(request.categorieBovine(), request.sexe());
        if (request.dateNaissance() != null && request.dateNaissance().isAfter(request.dateEntree())) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être postérieure à l'entrée.");
        }
        if (request.dateNaissance() != null && request.dateNaissance().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être dans le futur.");
        }
        Structure structure = structureRepository.findById(request.structureId())
                .orElseThrow(() -> new ResourceNotFoundException("Structure", request.structureId()));
        if (structure.getStatut() != StatutStructure.ACTIF) {
            throw new IllegalArgumentException("La structure doit être active.");
        }

        Animal animal = new Animal();
        animal.setEspece(Espece.BOVIN);
        animal.setNom(request.nom());
        animal.setRace(request.race().trim());
        animal.setSexe(request.sexe());
        animal.setCodeBoucle(request.codeBoucle());
        animal.setCodeRfid(request.codeRfid());
        animal.setDateNaissance(request.dateNaissance());
        animal.setDateEntree(request.dateEntree());
        animal.setPoidsEntreeKg(request.poidsEntreeKg());
        animal.setPoidsActuelKg(request.poidsEntreeKg());
        animal.setModeSuivi(ModeSuivi.INDIVIDUEL);
        animal.setStructure(structure);
        animal.setStatut(StatutAnimal.ACTIF);
        animal.setCodeUnique(genererCodeUnique());
        animal.setNotes(request.notes());
        animal.setMere(request.mereId() == null ? null : findBovinParent(request.mereId(), Sexe.FEMELLE));
        animal.setPere(request.pereId() == null ? null : findBovinParent(request.pereId(), Sexe.MALE));
        animalRepository.save(animal);

        ProfilBovin profil = new ProfilBovin();
        profil.setAnimal(animal);
        profil.setCategorieBovine(request.categorieBovine());
        profil.setRace(request.race().trim());
        profil.setNumeroBoucle(request.codeBoucle());
        profil.setPoidsCibleAdulteKg(request.poidsCibleAdulteKg());
        profil.setProductionLaitCibleJKg(request.productionLaitCibleJKg());
        if (FEMALE_CATEGORIES.contains(request.categorieBovine())) {
            profil.setStatutReproductif(StatutReproductifBovin.VIDE);
        }
        profilRepository.save(profil);
        return toResponse(animal, profil);
    }

    @Transactional(readOnly = true)
    public List<BovinResponse> list(CategorieBovine categorie, StatutReproductifBovin statut, Long fermeId) {
        return profilRepository.findAll().stream()
                .filter(p -> p.getAnimal().getStatut() == StatutAnimal.ACTIF)
                .filter(p -> categorie == null || p.getCategorieBovine() == categorie)
                .filter(p -> statut == null || p.getStatutReproductif() == statut)
                .filter(p -> fermeId == null || fermeId.equals(fermeId(p.getAnimal())))
                .map(p -> toResponse(p.getAnimal(), p))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BovinResponse get(Long animalId) {
        Animal animal = findAnimal(animalId);
        return toResponse(animal, findProfil(animalId));
    }

    /**
     * Profil bovin selon le mode de suivi : {@code INDIVIDUEL} (animal + profil bovin)
     * ou {@code BANDE} (bande bovine + profil bande bovin).
     */
    @Transactional(readOnly = true)
    public Map<String, Object> profil(Long id) {
        Animal animal = animalRepository.findById(id).orElse(null);
        if (animal != null && animal.getEspece() == Espece.BOVIN) {
            ProfilBovin profil = profilRepository.findByAnimalId(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Profil bovin", id));
            return Map.of("mode", "INDIVIDUEL", "data", toResponse(animal, profil));
        }
        if (bandeBovinService.existeProfilBande(id)) {
            return Map.of("mode", "BANDE", "data", bandeBovinService.getProfil(id));
        }
        throw new ResourceNotFoundException("Bovin ou bande bovine", id);
    }

    @Transactional
    public BovinResponse changerStatut(Long animalId, StatutReproductifBovin nouveauStatut) {
        ProfilBovin profil = findProfil(animalId);
        StatutReproductifBovin actuel = profil.getStatutReproductif();
        if (actuel == null || !TRANSITIONS.getOrDefault(actuel, Set.of()).contains(nouveauStatut)) {
            throw new IllegalArgumentException("Transition de statut bovin invalide : " + actuel + " -> " + nouveauStatut);
        }
        profil.setStatutReproductif(nouveauStatut);
        if (nouveauStatut == StatutReproductifBovin.REFORME) {
            profil.getAnimal().setStatut(StatutAnimal.REFORME);
            profil.getAnimal().setDateSortie(LocalDate.now());
            profil.getAnimal().setMotifSortie("Réforme bovine");
        }
        return toResponse(profil.getAnimal(), profil);
    }

    @Transactional
    public BovinResponse changerCategorie(Long animalId, CategorieBovine categorie) {
        ProfilBovin profil = findProfil(animalId);
        validateCategorySex(categorie, profil.getAnimal().getSexe());
        profil.setCategorieBovine(categorie);
        if (FEMALE_CATEGORIES.contains(categorie) && profil.getStatutReproductif() == null) {
            profil.setStatutReproductif(StatutReproductifBovin.VIDE);
        } else if (!FEMALE_CATEGORIES.contains(categorie)) {
            profil.setStatutReproductif(null);
        }
        return toResponse(profil.getAnimal(), profil);
    }

    @Transactional
    public BovinResponse tarir(Long animalId) {
        ProfilBovin profil = findProfil(animalId);
        if (!Set.of(StatutReproductifBovin.LACTATION, StatutReproductifBovin.SAILLIE,
                StatutReproductifBovin.GESTATION).contains(profil.getStatutReproductif())
                || profil.getDateTarissementPrevu() == null) {
            throw new IllegalArgumentException("Le tarissement nécessite une vache lactante avec un vêlage prévu.");
        }
        if (LocalDate.now().isBefore(profil.getDateTarissementPrevu())) {
            throw new IllegalArgumentException("La date prévue de tarissement n'est pas encore atteinte.");
        }
        profil.setStatutReproductif(StatutReproductifBovin.TARISSEMENT);
        return toResponse(profil.getAnimal(), profil);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> carriere(Long animalId) {
        BovinResponse bovin = get(animalId);
        List<SaillieBovinView> saillies = saillieRepository.findByVacheIdOrderByDateSaillieDesc(animalId).stream()
                .map(s -> new SaillieBovinView(s.getId(), s.getDateSaillie(), s.getStatut().name(),
                        s.getDateVelagePrevu())).toList();
        List<VelageBovinView> velages = velageRepository.findByVacheIdOrderByDateVelageReelDesc(animalId).stream()
                .map(v -> new VelageBovinView(v.getId(), v.getDateVelageReel(), v.getNbVeauxVivants(),
                        v.getNbVeauxMortNes(), v.getVeauIds())).toList();
        List<ControleLaitierView> controles = controleRepository.findByAnimalIdOrderByDateControleAsc(animalId).stream()
                .map(c -> new ControleLaitierView(c.getDateControle(), c.getLaitTotalJourKg(),
                        c.getLaitCumulLactationKg(), c.getNumeroLactation())).toList();
        return Map.of("bovin", bovin, "saillies", saillies, "velages", velages, "controlesLaitiers", controles);
    }

    @Transactional(readOnly = true)
    public GenealogieBovinResponse genealogie(Long animalId) {
        Animal animal = findAnimal(animalId);
        AnimalBovinLieResponse mother = animal.getMere() == null ? null : toLinkedAnimal(animal.getMere());
        AnimalBovinLieResponse father = animal.getPere() == null ? null : toLinkedAnimal(animal.getPere());
        List<AnimalBovinLieResponse> descendants = animalRepository.findDescendants(animalId).stream()
                .filter(descendant -> descendant.getEspece() == Espece.BOVIN)
                .map(this::toLinkedAnimal).toList();
        return new GenealogieBovinResponse(animalId, mother, father, descendants);
    }

    @Transactional(readOnly = true)
    public List<BovinResponse> velagesProches(int jours) {
        validerHorizonAlerte(jours);
        return profilRepository.findVelagesProches(LocalDate.now(), LocalDate.now().plusDays(jours),
                        List.of(StatutReproductifBovin.GESTATION, StatutReproductifBovin.PRE_VELAGE))
                .stream().map(p -> toResponse(p.getAnimal(), p)).toList();
    }

    @Transactional(readOnly = true)
    public List<BovinResponse> tarissementsAEffectuer(int jours) {
        validerHorizonAlerte(jours);
        return profilRepository.findTarissementsProches(LocalDate.now(), LocalDate.now().plusDays(jours),
                        StatutReproductifBovin.LACTATION)
                .stream().map(p -> toResponse(p.getAnimal(), p)).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> dashboard(Long fermeId) {
        List<BovinResponse> bovins = list(null, null, fermeId);
        Map<CategorieBovine, Long> parCategorie = bovins.stream()
                .collect(Collectors.groupingBy(BovinResponse::categorieBovine, Collectors.counting()));
        Map<StatutReproductifBovin, Long> parStatut = bovins.stream()
                .map(BovinResponse::statutReproductif)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(statut -> statut, Collectors.counting()));

        BigDecimal productionLaitCumulKg = bovins.stream()
                .map(BovinResponse::productionLaitCumulKg)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BandeBovinResponse> bandes = bandeBovinService.parFerme(fermeId);
        int effectifBandes = bandes.stream()
                .mapToInt(b -> b.bande().effectifActuel() == null ? 0 : b.bande().effectifActuel()).sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fermeId", fermeId);
        result.put("bovinsActifs", bovins.size());
        result.put("parCategorie", parCategorie);
        result.put("parStatutReproductif", parStatut);
        result.put("productionLaitCumulKg", productionLaitCumulKg);
        result.put("bandesBovines", bandes.size());
        result.put("effectifBandes", effectifBandes);
        result.put("poidsMoyenBandesKg", moyenne(bandes.stream()
                .map(b -> b.bande().poidsMoyenActuelKg()).filter(Objects::nonNull).toList()));
        result.put("gmqMoyenBandesG", moyenne(bandes.stream()
                .map(b -> b.bande().gainMoyenQuotidienG()).filter(Objects::nonNull).toList()));
        result.put("fcrMoyenBandes", moyenne(bandes.stream()
                .map(b -> b.bande().fcrCumule()).filter(Objects::nonNull).toList()));
        return result;
    }

    /**
     * Alertes bovines agrégées : vêlages proches, tarissements à effectuer,
     * échographies de confirmation en retard et chutes de production laitière.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> alertes(Long fermeId, int joursVelage, int joursTarissement) {
        validerHorizonAlerte(joursVelage);
        validerHorizonAlerte(joursTarissement);
        LocalDate today = LocalDate.now();

        List<BovinResponse> velages = profilRepository
                .findVelagesProches(today, today.plusDays(joursVelage),
                        List.of(StatutReproductifBovin.GESTATION, StatutReproductifBovin.PRE_VELAGE))
                .stream()
                .filter(p -> fermeId == null || fermeId.equals(fermeId(p.getAnimal())))
                .map(p -> toResponse(p.getAnimal(), p))
                .toList();

        List<BovinResponse> tarissements = profilRepository
                .findTarissementsProches(today, today.plusDays(joursTarissement),
                        StatutReproductifBovin.LACTATION)
                .stream()
                .filter(p -> fermeId == null || fermeId.equals(fermeId(p.getAnimal())))
                .map(p -> toResponse(p.getAnimal(), p))
                .toList();

        List<SaillieBovinResponse> echos = saillieService.echoesEnRetard().stream()
                .filter(s -> fermeId == null || fermeId.equals(fermeIdParAnimal(s.vacheId())))
                .toList();

        List<ControleLaitierResponse> chutes = fermeId == null
                ? List.of()
                : controleLaitierService.sousPerformeuses(fermeId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fermeId", fermeId);
        result.put("velagesProches", velages);
        result.put("tarissements", tarissements);
        result.put("echosEnRetard", echos);
        result.put("chutesLait", chutes);
        result.put("totalAlertes", velages.size() + tarissements.size() + echos.size() + chutes.size());
        return result;
    }

    private BigDecimal moyenne(List<BigDecimal> valeurs) {
        if (valeurs.isEmpty()) {
            return null;
        }
        BigDecimal somme = valeurs.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return somme.divide(BigDecimal.valueOf(valeurs.size()), 2, RoundingMode.HALF_UP);
    }

    private Long fermeIdParAnimal(Long animalId) {
        return animalRepository.findById(animalId).map(this::fermeId).orElse(null);
    }

    private void validerHorizonAlerte(int jours) {
        if (jours < 0 || jours > 365) {
            throw new IllegalArgumentException("L'horizon d'alerte doit être compris entre 0 et 365 jours.");
        }
    }

    @Transactional
    public Animal createNewborn(Animal mother, Animal father, Sexe sexe, BigDecimal weight, LocalDate birthDate) {
        Animal calf = new Animal();
        calf.setEspece(Espece.BOVIN);
        calf.setSexe(sexe);
        calf.setRace(mother.getRace());
        calf.setDateNaissance(birthDate);
        calf.setDateEntree(birthDate);
        calf.setPoidsEntreeKg(weight);
        calf.setPoidsActuelKg(weight);
        calf.setModeSuivi(ModeSuivi.INDIVIDUEL);
        calf.setStructure(mother.getStructure());
        calf.setStatut(StatutAnimal.ACTIF);
        calf.setMere(mother);
        calf.setPere(father);
        calf.setCodeUnique(genererCodeUnique());
        animalRepository.save(calf);

        ProfilBovin profil = new ProfilBovin();
        profil.setAnimal(calf);
        profil.setCategorieBovine(CategorieBovine.VEAU);
        profil.setRace(calf.getRace());
        profilRepository.save(profil);
        return calf;
    }

    private Animal findAnimal(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        if (animal.getEspece() != Espece.BOVIN) {
            throw new IllegalArgumentException("L'animal " + id + " n'est pas un bovin.");
        }
        return animal;
    }

    private ProfilBovin findProfil(Long animalId) {
        findAnimal(animalId);
        return profilRepository.findByAnimalId(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil bovin", animalId));
    }

    private Animal findBovinParent(Long id, Sexe expectedSex) {
        Animal parent = findAnimal(id);
        if (parent.getSexe() != expectedSex) {
            throw new IllegalArgumentException("Le sexe de l'animal parent " + id + " est incompatible.");
        }
        return parent;
    }

    private void validateCategorySex(CategorieBovine categorie, Sexe sexe) {
        if (FEMALE_CATEGORIES.contains(categorie) && sexe != Sexe.FEMELLE) {
            throw new IllegalArgumentException("La catégorie " + categorie + " requiert un animal femelle.");
        }
        if (MALE_CATEGORIES.contains(categorie) && sexe != Sexe.MALE) {
            throw new IllegalArgumentException("La catégorie " + categorie + " requiert un animal mâle.");
        }
    }

    private Long fermeId(Animal animal) {
        if (animal.getStructure() == null || animal.getStructure().getSite() == null
                || animal.getStructure().getSite().getFerme() == null) {
            return null;
        }
        return animal.getStructure().getSite().getFerme().getId();
    }

    private String genererCodeUnique() {
        String code;
        do {
            code = "SF-BV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (animalRepository.existsByCodeUnique(code));
        return code;
    }

    private BovinResponse toResponse(Animal animal, ProfilBovin profil) {
        return new BovinResponse(animal.getId(), animal.getCodeUnique(), animal.getCodeBoucle(),
                animal.getCodeRfid(), animal.getNom(), animal.getRace(), animal.getSexe(),
                animal.getDateNaissance(), animal.getPoidsActuelKg(), animal.getStatut(),
                animal.getStructure() == null ? null : animal.getStructure().getId(),
                animal.getStructure() == null ? null : animal.getStructure().getNom(),
                animal.getMere() == null ? null : animal.getMere().getId(),
                animal.getPere() == null ? null : animal.getPere().getId(),
                profil.getCategorieBovine(), profil.getStatutReproductif(),
                profil.getNumeroLactationActuelle(), profil.getNbLactationsTotal(), profil.getNbVeauxTotal(),
                profil.getDateVelagePrevu(), profil.getDateTarissementPrevu(),
                profil.getDateDebutLactationActuelle(), profil.getProductionLaitCumulKg(),
                profil.getMoyenneLait305jKg(), profil.getDateCreation());
    }

    private AnimalBovinLieResponse toLinkedAnimal(Animal animal) {
        return new AnimalBovinLieResponse(animal.getId(), animal.getCodeUnique(), animal.getNom(),
                animal.getRace(), animal.getSexe(), animal.getDateNaissance());
    }

    private record SaillieBovinView(Long id, LocalDate date, String statut, LocalDate velagePrevu) {}
    private record VelageBovinView(Long id, java.time.LocalDateTime date, Integer vivants,
            Integer mortnes, List<Long> veauIds) {}
    private record ControleLaitierView(LocalDate date, BigDecimal productionJour,
            BigDecimal cumulLactation, Integer numeroLactation) {}
}
