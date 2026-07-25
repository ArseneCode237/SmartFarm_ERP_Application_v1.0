package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.animal.AnimalRequest;
import com.reseau_partage.animaux.dto.animal.AnimalResponse;
import com.reseau_partage.animaux.exception.IncompatibiliteEspeceException;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.animaux.exception.TransitionStatutInvalideException;
import com.reseau_partage.animaux.mapper.AnimalMapper;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.MouvementAnimal;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutBande;
import com.reseau_partage.core.entities.StatutStructure;
import com.reseau_partage.core.entities.Structure;
import com.reseau_partage.core.entities.TypeMouvement;
import com.reseau_partage.core.entities.AuditAnimal;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.AuditAnimalRepository;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.FermeRepository;
import com.reseau_partage.core.repository.MouvementAnimalRepository;
import com.reseau_partage.core.repository.SiteRepository;
import com.reseau_partage.core.repository.StructureRepository;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final BandeRepository bandeRepository;
    private final MouvementAnimalRepository mouvementRepository;
    private final StructureRepository structureRepository;
    private final SiteRepository siteRepository;
    private final FermeRepository fermeRepository;
    private final AuditAnimalRepository auditRepository;
    private final AnimalMapper animalMapper;

    public AnimalService(AnimalRepository animalRepository, BandeRepository bandeRepository, MouvementAnimalRepository mouvementRepository, StructureRepository structureRepository, SiteRepository siteRepository, FermeRepository fermeRepository, AuditAnimalRepository auditRepository, AnimalMapper animalMapper) {
        this.animalRepository = animalRepository;
        this.bandeRepository = bandeRepository;
        this.mouvementRepository = mouvementRepository;
        this.structureRepository = structureRepository;
        this.siteRepository = siteRepository;
        this.fermeRepository = fermeRepository;
        this.auditRepository = auditRepository;
        this.animalMapper = animalMapper;
    }

    @Transactional
    public AnimalResponse create(AnimalRequest request) {
        Structure structure = structureRepository.findById(request.structureId())
                .orElseThrow(() -> new ResourceNotFoundException("Structure", request.structureId()));
        if (structure.getStatut() != StatutStructure.ACTIF) {
            throw new IllegalArgumentException("La structure doit etre active.");
        }
        if (request.modeSuivi() == ModeSuivi.BANDE && request.bandeId() == null) {
            throw new IllegalArgumentException("bandeId est requis pour un animal en mode BANDE.");
        }
        if (request.modeSuivi() == ModeSuivi.BANDE) {
            Bande bande = bandeRepository.findById(request.bandeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bande", request.bandeId()));
            if (bande.getStatut() != StatutBande.EN_COURS) {
                throw new IllegalArgumentException("La bande doit etre en cours.");
            }
            if (bande.getEspece() != request.espece()) {
                throw new IncompatibiliteEspeceException("L'animal doit avoir la meme espece que la bande.");
            }
        }
        if (request.mereId() != null) {
            Animal mere = animalRepository.findById(request.mereId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal", request.mereId()));
            if (mere.getEspece() != request.espece()) {
                throw new IncompatibiliteEspeceException("La mere doit avoir la meme espece.");
            }
        }
        if (request.pereId() != null) {
            Animal pere = animalRepository.findById(request.pereId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal", request.pereId()));
            if (pere.getEspece() != request.espece()) {
                throw new IncompatibiliteEspeceException("Le pere doit avoir la meme espece.");
            }
        }
        Animal animal = animalMapper.toEntity(request);
        animal.setCodeUnique(genererCodeUnique(request.espece()));
        animal.setStructure(structure);
        animal.setStatut(StatutAnimal.ACTIF);
        if (request.modeSuivi() == ModeSuivi.BANDE && request.bandeId() != null) {
            Bande bande = bandeRepository.findById(request.bandeId()).orElseThrow();
            animal.setBande(bande);
        }
        animalRepository.save(animal);
        enregistrerMouvementEntree(animal);
        return toResponse(animal);
    }

    @Transactional(readOnly = true)
    public Page<AnimalResponse> list(Espece espece, StatutAnimal statut, ModeSuivi modeSuivi, Long structureId, Long siteId, Long fermeId, Long bandeId, Pageable pageable) {
        return animalRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AnimalResponse get(Long id) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        return toResponse(animal);
    }

    @Transactional
    public AnimalResponse update(Long id, AnimalRequest request) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        animal.setRace(request.race());
        animal.setSouche(request.souche());
        animal.setNotes(request.notes());
        animal.setCodeRfid(request.codeRfid());
        animal.setCodeBoucle(request.codeBoucle());
        if (request.structureId() != null && !Objects.equals(request.structureId(), animal.getStructure().getId())) {
            Structure destination = structureRepository.findById(request.structureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure", request.structureId()));
            animal.setStructure(destination);
            enregistrerMouvementTransfert(animal, destination);
        }
        enregistrerAudit(animal, "Modification animal");
        return toResponse(animal);
    }

    private void enregistrerAudit(Animal animal, String motif) {
        enregistrerAuditChamp(animal, "race", animal.getRace());
        enregistrerAuditChamp(animal, "souche", animal.getSouche());
        enregistrerAuditChamp(animal, "codeRfid", animal.getCodeRfid());
        enregistrerAuditChamp(animal, "codeBoucle", animal.getCodeBoucle());
        enregistrerAuditChamp(animal, "notes", animal.getNotes());
        enregistrerAuditChamp(animal, "statut", animal.getStatut() != null ? animal.getStatut().name() : null);
        enregistrerAuditChamp(animal, "statutReproducteur", animal.getStatutReproducteur() != null ? animal.getStatutReproducteur().name() : null);
        enregistrerAuditChamp(animal, "structure", animal.getStructure() != null ? String.valueOf(animal.getStructure().getId()) : null);
    }

    private void enregistrerAuditChamp(Animal animal, String champ, String nouvelleValeur) {
        AuditAnimal audit = new AuditAnimal();
        audit.setAnimal(animal);
        audit.setChampModifie(champ);
        audit.setNouvelleValeur(nouvelleValeur);
        audit.setModifiePar("system");
        audit.setDateModification(java.time.LocalDateTime.now());
        audit.setMotif("Modification animal");
        auditRepository.save(audit);
    }

    @Transactional
    public void declarerSortie(Long id, TypeMouvement typeMouvement, LocalDate dateSortie, BigDecimal poidsKg, BigDecimal prixUnitaire, String motif, String causeMort, String operateurNom) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        if (animal.getStatut() != StatutAnimal.ACTIF) {
            throw new TransitionStatutInvalideException(animal.getStatut());
        }
        animal.setStatut(switch (typeMouvement) {
            case SORTIE_MORT -> StatutAnimal.MORT;
            case SORTIE_VENTE -> StatutAnimal.VENDU;
            case SORTIE_REFORME -> StatutAnimal.REFORME;
            default -> throw new IllegalArgumentException("Type de mouvement invalide pour une sortie. Valeurs acceptées : SORTIE_VENTE, SORTIE_MORT, SORTIE_REFORME.");
        });
        animal.setDateSortie(dateSortie);
        animal.setMotifSortie(motif);
        animal.setCauseMort(typeMouvement == TypeMouvement.SORTIE_MORT ? causeMort : null);
        if (typeMouvement == TypeMouvement.SORTIE_MORT && animal.getBande() != null) {
            Bande bande = animal.getBande();
            bande.setEffectifMorts((bande.getEffectifMorts() == null ? 0 : bande.getEffectifMorts()) + 1);
            bande.setEffectifActuel(bande.getEffectifActuel() - 1);
            if (bande.getEffectifActuel() == 0) {
                bande.setStatut(StatutBande.TERMINEE);
                bande.setDateSortieReelle(dateSortie);
            }
        }
        MouvementAnimal mouvement = new MouvementAnimal();
        mouvement.setAnimal(animal);
        mouvement.setTypeMouvement(typeMouvement);
        mouvement.setDateMouvement(dateSortie);
        mouvement.setStructureOrigine(animal.getStructure());
        mouvement.setPoidsKg(poidsKg);
        mouvement.setPrixUnitaire(prixUnitaire);
        mouvement.setMotif(motif);
        mouvement.setOperateurNom(operateurNom);
        mouvementRepository.save(mouvement);
    }

    @Transactional
    public AnimalResponse transferer(Long id, Long structureDestinationId, String operateurNom, String motif) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        if (animal.getStatut() != StatutAnimal.ACTIF) {
            throw new TransitionStatutInvalideException(animal.getStatut());
        }
        Structure destination = structureRepository.findById(structureDestinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Structure", structureDestinationId));
        animal.setStructure(destination);
        enregistrerMouvementTransfert(animal, destination);
        return toResponse(animal);
    }

    @Transactional(readOnly = true)
    public Map<Long, AnimalResponse> genealogie(Long id) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        Map<Long, AnimalResponse> arbre = new LinkedHashMap<>();
        if (animal.getMere() != null) {
            arbre.put(animal.getMere().getId(), toResponse(animal.getMere()));
            ajouterGrandsParents(animal.getMere(), arbre);
        }
        if (animal.getPere() != null) {
            arbre.put(animal.getPere().getId(), toResponse(animal.getPere()));
            ajouterGrandsParents(animal.getPere(), arbre);
        }
        return arbre;
    }

    @Transactional(readOnly = true)
    public List<MouvementAnimal> historique(Long id) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        return mouvementRepository.findByAnimalId(id);
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> animauxASurveiller(Long fermeId) {
        LocalDate seuil = LocalDate.now().minusDays(7);
        List<Animal> animaux = animalRepository.findAnimauxSansPeseeRecente(seuil);
        return animaux.stream().filter(a -> {
            if (a.getStructure() == null || a.getStructure().getSite() == null || a.getStructure().getSite().getFerme() == null) {
                return false;
            }
            return Objects.equals(a.getStructure().getSite().getFerme().getId(), fermeId);
        }).map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnimalResponse recherche(String query) {
        Animal animal = animalRepository.findByCodeUnique(query).orElse(null);
        if (animal == null) animal = animalRepository.findByCodeRfid(query).orElse(null);
        if (animal == null) {
            List<Animal> parBoucle = animalRepository.findByBandeId(Long.parseLong(query));
            if (!parBoucle.isEmpty()) animal = parBoucle.get(0);
        }
        if (animal == null) {
            throw new ResourceNotFoundException("Animal", query);
        }
        return toResponse(animal);
    }

    private String genererCodeUnique(Espece espece) {
        String prefixe = switch (espece) {
            case POULET, DINDE, CANARD, PINTADE, PIGEON -> "AV";
            case BOVIN -> "BV";
            case OVIN -> "OV";
            case CAPRIN -> "CA";
            case PORC -> "PO";
            case LAPIN -> "LA";
            case TILAPIA, SILURE, CARPE, CREVETTE, CAPITAINE -> "AQ";
            default -> "XX";
        };
        long sequence = animalRepository.count() + 1;
        return String.format("SF-%s-%05d", prefixe, sequence);
    }

    private void enregistrerMouvementEntree(Animal animal) {
        MouvementAnimal mouvement = new MouvementAnimal();
        mouvement.setAnimal(animal);
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setDateMouvement(animal.getDateEntree());
        mouvement.setStructureOrigine(animal.getStructure());
        mouvement.setMotif("Entree initiale");
        mouvementRepository.save(mouvement);
    }

    private void enregistrerMouvementTransfert(Animal animal, Structure destination) {
        MouvementAnimal mouvement = new MouvementAnimal();
        mouvement.setAnimal(animal);
        mouvement.setTypeMouvement(TypeMouvement.TRANSFERT);
        mouvement.setDateMouvement(LocalDate.now());
        mouvement.setStructureOrigine(animal.getStructure());
        mouvement.setStructureDestination(destination);
        mouvement.setMotif("Transfert d'animal");
        mouvementRepository.save(mouvement);
    }

    private void ajouterGrandsParents(Animal parent, Map<Long, AnimalResponse> arbre) {
        if (parent.getMere() != null && !arbre.containsKey(parent.getMere().getId())) {
            arbre.put(parent.getMere().getId(), toResponse(parent.getMere()));
        }
        if (parent.getPere() != null && !arbre.containsKey(parent.getPere().getId())) {
            arbre.put(parent.getPere().getId(), toResponse(parent.getPere()));
        }
    }

    public AnimalResponse toResponsePublic(Animal animal) {
        return animalMapper.toResponse(animal);
    }

    private AnimalResponse toResponse(Animal animal) {
        return animalMapper.toResponse(animal);
    }
}
