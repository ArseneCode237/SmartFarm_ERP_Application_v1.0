package com.reseau_partage.animaux.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.porcin.PorcinRequest;
import com.reseau_partage.animaux.dto.porcin.PorcinResponse;
import com.reseau_partage.animaux.dto.porcin.PorcinUpdateRequest;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.MouvementAnimal;
import com.reseau_partage.core.entities.ProfilPorcin;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutReproducteur;
import com.reseau_partage.core.entities.StatutReproductifPorcin;
import com.reseau_partage.core.entities.StatutStructure;
import com.reseau_partage.core.entities.Structure;
import com.reseau_partage.core.entities.TypeMouvement;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.MiseBaRepository;
import com.reseau_partage.core.repository.MouvementAnimalRepository;
import com.reseau_partage.core.repository.ProfilPorcinRepository;
import com.reseau_partage.core.repository.SaillieRepository;
import com.reseau_partage.core.repository.StructureRepository;

@Service
public class PorcinService {

    // Transitions autorisées : clé = statut actuel, valeurs = statuts cibles acceptés
    private static final java.util.Map<StatutReproductifPorcin, Set<StatutReproductifPorcin>> TRANSITIONS =
            java.util.Map.of(
                    StatutReproductifPorcin.COCHETTE,
                        Set.of(StatutReproductifPorcin.EN_ATTENTE_SAILLIE),
                    StatutReproductifPorcin.EN_ATTENTE_SAILLIE,
                        Set.of(StatutReproductifPorcin.SAILLIE),
                    StatutReproductifPorcin.SAILLIE,
                        Set.of(StatutReproductifPorcin.GESTATION, StatutReproductifPorcin.EN_CHALEUR),
                    StatutReproductifPorcin.GESTATION,
                        Set.of(StatutReproductifPorcin.PRE_MISE_BAS, StatutReproductifPorcin.EN_CHALEUR),
                    StatutReproductifPorcin.PRE_MISE_BAS,
                        Set.of(StatutReproductifPorcin.MISE_BAS),
                    StatutReproductifPorcin.MISE_BAS,
                        Set.of(StatutReproductifPorcin.LACTATION),
                    StatutReproductifPorcin.LACTATION,
                        Set.of(StatutReproductifPorcin.SEVRAGE),
                    StatutReproductifPorcin.SEVRAGE,
                        Set.of(StatutReproductifPorcin.EN_CHALEUR),
                    StatutReproductifPorcin.EN_CHALEUR,
                        Set.of(StatutReproductifPorcin.SAILLIE),
                    StatutReproductifPorcin.VERRAT,
                        Set.of(StatutReproductifPorcin.REFORME)
            );

    private final AnimalRepository animalRepository;
    private final StructureRepository structureRepository;
    private final ProfilPorcinRepository profilPorcinRepository;
    private final MouvementAnimalRepository mouvementRepository;
    private final SaillieRepository saillieRepository;
    private final MiseBaRepository miseBaRepository;
    private final SaillieService saillieService;
    private final MiseBaService miseBaService;

    public PorcinService(AnimalRepository animalRepository,
                         StructureRepository structureRepository,
                         ProfilPorcinRepository profilPorcinRepository,
                         MouvementAnimalRepository mouvementRepository,
                         SaillieRepository saillieRepository,
                         MiseBaRepository miseBaRepository,
                         @Lazy SaillieService saillieService,
                         @Lazy MiseBaService miseBaService) {
        this.animalRepository       = animalRepository;
        this.structureRepository    = structureRepository;
        this.profilPorcinRepository = profilPorcinRepository;
        this.mouvementRepository    = mouvementRepository;
        this.saillieRepository      = saillieRepository;
        this.miseBaRepository       = miseBaRepository;
        this.saillieService         = saillieService;
        this.miseBaService          = miseBaService;
    }

    /** Crée un porcin individuel (achat, naissance interne, don). */
    @Transactional
    public PorcinResponse create(PorcinRequest request) {
        Structure structure = structureRepository.findById(request.structureId())
                .orElseThrow(() -> new ResourceNotFoundException("Structure", request.structureId()));

        if (structure.getStatut() != StatutStructure.ACTIF) {
            throw new IllegalArgumentException("La structure doit être active.");
        }

        Animal animal = new Animal();
        animal.setEspece(Espece.PORC);
        animal.setNom(request.nom());
        animal.setRace(request.race());
        animal.setSexe(request.sexe());
        animal.setCodeBoucle(request.codeBoucle());
        animal.setCodeRfid(request.codeRfid());
        animal.setDateNaissance(request.dateNaissance());
        animal.setDateEntree(request.dateEntree());
        animal.setPoidsEntreeKg(request.poidsEntreeKg());
        animal.setPoidsActuelKg(request.poidsEntreeKg());
        animal.setModeSuivi(ModeSuivi.INDIVIDUEL);
        animal.setStructure(structure);
        animal.setProvenance(request.provenance());
        animal.setFournisseurNom(request.fournisseurNom());
        animal.setPrixUnitaire(request.prixUnitaire());
        animal.setNotes(request.notes());
        animal.setStatut(StatutAnimal.ACTIF);
        animal.setCodeUnique(genererCodeUnique());

        // Généalogie
        if (request.mereId() != null) {
            animal.setMere(animalRepository.findById(request.mereId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal (mère)", request.mereId())));
        }
        if (request.pereId() != null) {
            animal.setPere(animalRepository.findById(request.pereId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal (père)", request.pereId())));
        }

        animalRepository.save(animal);

        // Mouvement d'entrée
        MouvementAnimal mouvement = new MouvementAnimal();
        mouvement.setAnimal(animal);
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setDateMouvement(request.dateEntree());
        mouvement.setStructureOrigine(structure);
        mouvement.setMotif("Entrée individuelle — " + request.provenance());
        mouvementRepository.save(mouvement);

        // Profil porcin
        ProfilPorcin profil = new ProfilPorcin();
        profil.setAnimal(animal);
        profil.setStatutReproductif(
                request.sexe() == Sexe.FEMELLE
                        ? StatutReproductifPorcin.COCHETTE
                        : StatutReproductifPorcin.VERRAT);
        profilPorcinRepository.save(profil);

        return toResponse(animal);
    }

    /** Liste les porcins actifs, avec filtre optionnel par statut reproductif. */
    @Transactional(readOnly = true)
    public List<PorcinResponse> list(StatutReproductifPorcin statutReproductif, Long fermeId) {
        List<Animal> animaux = animalRepository.findByEspeceAndStatut(Espece.PORC, StatutAnimal.ACTIF);
        return animaux.stream()
                .filter(a -> fermeId == null
                        || (a.getStructure() != null
                            && a.getStructure().getSite() != null
                            && a.getStructure().getSite().getFerme() != null
                            && a.getStructure().getSite().getFerme().getId().equals(fermeId)))
                .filter(a -> {
                    if (statutReproductif == null) return true;
                    ProfilPorcin p = profilPorcinRepository.findByAnimalId(a.getId()).orElse(null);
                    return p != null && p.getStatutReproductif() == statutReproductif;
                })
                .map(this::toResponse)
                .toList();
    }

    /** Fiche complète d'un porcin. */
    @Transactional(readOnly = true)
    public PorcinResponse get(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        if (animal.getEspece() != Espece.PORC) {
            throw new IllegalArgumentException("Cet animal n'est pas un porc.");
        }
        return toResponse(animal);
    }

    /** Met à jour les informations modifiables d'un porc déjà créé. */
    @Transactional
    public PorcinResponse update(Long id, PorcinUpdateRequest request) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        if (animal.getEspece() != Espece.PORC) {
            throw new IllegalArgumentException("Cet animal n'est pas un porc.");
        }
        if (animal.getStatut() == StatutAnimal.REFORME) {
            throw new IllegalArgumentException("Impossible de modifier un porc réformé.");
        }

        if (request.nom() != null) {
            animal.setNom(request.nom());
        }
        if (request.codeBoucle() != null) {
            animal.setCodeBoucle(request.codeBoucle());
        }
        if (request.codeRfid() != null) {
            animal.setCodeRfid(request.codeRfid());
        }
        if (request.sexe() != null) {
            animal.setSexe(request.sexe());
        }
        if (request.race() != null) {
            animal.setRace(request.race());
        }
        if (request.dateNaissance() != null) {
            animal.setDateNaissance(request.dateNaissance());
        }
        if (request.dateEntree() != null) {
            animal.setDateEntree(request.dateEntree());
        }
        if (request.poidsEntreeKg() != null) {
            animal.setPoidsEntreeKg(request.poidsEntreeKg());
        }
        if (request.poidsActuelKg() != null) {
            animal.setPoidsActuelKg(request.poidsActuelKg());
        }
        if (request.provenance() != null) {
            animal.setProvenance(request.provenance());
        }
        if (request.fournisseurNom() != null) {
            animal.setFournisseurNom(request.fournisseurNom());
        }
        if (request.prixUnitaire() != null) {
            animal.setPrixUnitaire(request.prixUnitaire());
        }
        if (request.notes() != null) {
            animal.setNotes(request.notes());
        }

        animalRepository.save(animal);
        return toResponse(animal);
    }

    /** Change le statut reproductif selon la matrice de transitions. */
    @Transactional
    public PorcinResponse changerStatut(Long id, StatutReproductifPorcin nouveauStatut) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        ProfilPorcin profil = profilPorcinRepository.findByAnimalId(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfilPorcin", id));

        StatutReproductifPorcin actuel = profil.getStatutReproductif();

        // REFORME est toujours autorisé depuis n'importe quel statut
        if (nouveauStatut != StatutReproductifPorcin.REFORME) {
            Set<StatutReproductifPorcin> cibles = TRANSITIONS.getOrDefault(actuel, Set.of());
            if (!cibles.contains(nouveauStatut)) {
                throw new IllegalArgumentException(
                        "Transition non autorisée pour le porc id=" + id + " : " + actuel + " → " + nouveauStatut
                        + ". Transitions autorisées depuis " + actuel + " : " + cibles + ".");
            }
        }

        profil.setStatutReproductif(nouveauStatut);
        if (nouveauStatut == StatutReproductifPorcin.REFORME) {
            animal.setStatutReproducteur(StatutReproducteur.REFORME);
            animalRepository.save(animal);
        }
        profilPorcinRepository.save(profil);
        return toResponse(animal);
    }

    /** Transférer l'animal vers une autre structure. */
    @Transactional
    public PorcinResponse transferer(Long id, Long structureDestId, String motif) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        if (animal.getStatut() != StatutAnimal.ACTIF) {
            throw new IllegalArgumentException("Seuls les animaux actifs peuvent être transférés.");
        }
        Structure dest = structureRepository.findById(structureDestId)
                .orElseThrow(() -> new ResourceNotFoundException("Structure", structureDestId));

        MouvementAnimal mouvement = new MouvementAnimal();
        mouvement.setAnimal(animal);
        mouvement.setTypeMouvement(TypeMouvement.TRANSFERT);
        mouvement.setDateMouvement(LocalDate.now());
        mouvement.setStructureOrigine(animal.getStructure());
        mouvement.setStructureDestination(dest);
        mouvement.setMotif(motif != null ? motif : "Transfert individuel");
        mouvementRepository.save(mouvement);

        animal.setStructure(dest);
        animalRepository.save(animal);
        return toResponse(animal);
    }

    /** Réformer définitivement un animal. */
    @Transactional
    public void reformer(Long id, String motif) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        animal.setStatut(StatutAnimal.REFORME);
        animal.setDateSortie(LocalDate.now());
        animal.setMotifSortie(motif);
        animalRepository.save(animal);

        profilPorcinRepository.findByAnimalId(id).ifPresent(p -> {
            p.setStatutReproductif(StatutReproductifPorcin.REFORME);
            profilPorcinRepository.save(p);
        });
    }

    /** Historique reproductif complet d'une truie (saillies + portées + stats). */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> carriere(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        if (animal.getEspece() != Espece.PORC) {
            throw new IllegalArgumentException("Cet animal n'est pas un porc.");
        }

        ProfilPorcin profil = profilPorcinRepository.findByAnimalId(id).orElse(null);

        // Saillies triées du plus récent au plus ancien
        List<com.reseau_partage.animaux.dto.saillie.SaillieResponse> saillies =
                saillieRepository.findByTruieIdOrderByDateSaillieDesc(id)
                        .stream()
                        .map(saillieService::toResponse)
                        .toList();

        // Portées triées du plus récent au plus ancien
        List<com.reseau_partage.animaux.dto.misebas.MiseBaResponse> portees =
                miseBaRepository.findByTruieIdOrderByDateMiseBasReelleDesc(id)
                        .stream()
                        .map(miseBaService::toResponse)
                        .toList();

        java.util.Map<String, Object> carriere = new java.util.LinkedHashMap<>();
        carriere.put("animal", toResponse(animal));
        carriere.put("nbSailliesTotal", saillies.size());
        carriere.put("nbPorteesTotal",
                profil != null && profil.getNbPorteesTotal() != null ? profil.getNbPorteesTotal() : 0);
        carriere.put("nbPorceletsTotalNesVivants",
                profil != null && profil.getNbPorceletsTotalNesVivants() != null ? profil.getNbPorceletsTotalNesVivants() : 0);
        carriere.put("nbPorceletsTotalSevres",
                profil != null && profil.getNbPorceletsTotalSevres() != null ? profil.getNbPorceletsTotalSevres() : 0);
        carriere.put("moyNesVivantsParPortee",
                profil != null ? profil.getMoyNesVivantsParPortee() : null);
        carriere.put("moyPoidsSevrageKg",
                profil != null ? profil.getMoyPoidsSevrageKg() : null);
        carriere.put("moyDureeLactationJours",
                profil != null ? profil.getMoyDureeLactationJours() : null);
        carriere.put("saillies", saillies);
        carriere.put("portees",  portees);
        return carriere;
    }

    /** Dashboard KPIs reproduction d'une ferme. */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> dashboard(Long fermeId) {        java.util.Map<String, Object> kpis = new java.util.LinkedHashMap<>();
        kpis.put("fermeId", fermeId);
        kpis.put("avgNesVivantsParPortee",
                profilPorcinRepository.avgNesVivantsParPorteeByFerme(fermeId));
        // Nombre de truies par statut
        java.util.Map<String, Long> parStatut = new java.util.TreeMap<>();
        for (StatutReproductifPorcin s : StatutReproductifPorcin.values()) {
            long count = profilPorcinRepository.findByStatutReproductif(s).stream()
                    .filter(p -> p.getAnimal().getStructure() != null
                            && p.getAnimal().getStructure().getSite() != null
                            && p.getAnimal().getStructure().getSite().getFerme() != null
                            && p.getAnimal().getStructure().getSite().getFerme().getId().equals(fermeId))
                    .count();
            if (count > 0) parStatut.put(s.name(), count);
        }
        kpis.put("truiesParStatut", parStatut);
        kpis.put("truiesDisponiblesSaillie",
                profilPorcinRepository.findTruiesDisponiblesSaillie(fermeId).size());
        return kpis;
    }

    /** Construit la réponse complète d'un porcin. Accessible depuis ExtractionBandeService. */
    public PorcinResponse toResponse(Animal animal) {
        ProfilPorcin profil = profilPorcinRepository.findByAnimalId(animal.getId()).orElse(null);
        int ageJours = animal.getDateNaissance() != null
                ? (int) ChronoUnit.DAYS.between(animal.getDateNaissance(), LocalDate.now()) : 0;

        return new PorcinResponse(
                animal.getId(),
                animal.getCodeUnique(),
                animal.getCodeBoucle(),
                animal.getCodeRfid(),
                animal.getNom(),
                animal.getRace(),
                animal.getSexe(),
                animal.getDateNaissance(),
                animal.getDateEntree(),
                ageJours,
                animal.getPoidsEntreeKg(),
                animal.getPoidsActuelKg(),
                animal.getStatut(),
                animal.getStructure() != null ? animal.getStructure().getId() : null,
                animal.getStructure() != null ? animal.getStructure().getNom() : null,
                animal.getStructure() != null && animal.getStructure().getSite() != null
                        ? animal.getStructure().getSite().getNom() : null,
                animal.getProvenance(),
                animal.getFournisseurNom(),
                animal.getPrixUnitaire(),
                // Profil reproductif
                profil != null ? profil.getStatutReproductif() : null,
                profil != null ? profil.getDateDebutStatutActuel() : null,
                profil != null ? profil.getNbPorteesTotal() : null,
                profil != null ? profil.getNbPorceletsTotalNesVivants() : null,
                profil != null ? profil.getNbPorceletsTotalSevres() : null,
                profil != null ? profil.getMoyNesVivantsParPortee() : null,
                profil != null ? profil.getMoyPoidsSevrageKg() : null,
                profil != null ? profil.getDateMiseBasPrevue() : null,
                profil != null ? profil.getDateSevragePrevu() : null,
                profil != null ? profil.getDateRetourChaleurEstimee() : null,
                null, // saillieActive — enrichi par SaillieService si besoin
                profil != null && profil.getBandeOrigine() != null
                        ? profil.getBandeOrigine().getId() : null,
                profil != null && profil.getBandeOrigine() != null
                        ? profil.getBandeOrigine().getNom() : null,
                profil != null ? profil.getDateExtractionBande() : null,
                profil != null ? profil.getPoidsSelectionKg() : null,
                animal.getDateCreation()
        );
    }

    private String genererCodeUnique() {
        long seq = animalRepository.count() + 1;
        return String.format("SF-PO-%05d", seq);
    }
}
