package com.reseau_partage.animaux.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.porcin.ExtractionBandeRequest;
import com.reseau_partage.animaux.dto.porcin.ExtractionBandeResponse;
import com.reseau_partage.animaux.exception.QuantiteInvalideException;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.MouvementAnimal;
import com.reseau_partage.core.entities.ProfilPorcin;
import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutBande;
import com.reseau_partage.core.entities.StatutReproductifPorcin;
import com.reseau_partage.core.entities.Structure;
import com.reseau_partage.core.entities.TypeMouvement;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.MouvementAnimalRepository;
import com.reseau_partage.core.repository.ProfilPorcinRepository;
import com.reseau_partage.core.repository.StructureRepository;

@Service
public class ExtractionBandeService {

    private final BandeRepository bandeRepository;
    private final AnimalRepository animalRepository;
    private final StructureRepository structureRepository;
    private final ProfilPorcinRepository profilPorcinRepository;
    private final MouvementAnimalRepository mouvementRepository;
    private final PorcinService porcinService;

    public ExtractionBandeService(BandeRepository bandeRepository,
                                   AnimalRepository animalRepository,
                                   StructureRepository structureRepository,
                                   ProfilPorcinRepository profilPorcinRepository,
                                   MouvementAnimalRepository mouvementRepository,
                                   PorcinService porcinService) {
        this.bandeRepository        = bandeRepository;
        this.animalRepository       = animalRepository;
        this.structureRepository    = structureRepository;
        this.profilPorcinRepository = profilPorcinRepository;
        this.mouvementRepository    = mouvementRepository;
        this.porcinService          = porcinService;
    }

    /**
     * Extrait N animaux d'une bande porcine pour les passer en suivi individuel.
     * Tout passe ou rien ne passe (@Transactional).
     */
    @Transactional
    public ExtractionBandeResponse extraire(ExtractionBandeRequest request) {
        Bande bande = bandeRepository.findById(request.bandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Bande", request.bandeId()));

        if (bande.getStatut() != StatutBande.EN_COURS) {
            throw new IllegalArgumentException("La bande doit être EN_COURS pour extraire des animaux.");
        }
        if (bande.getEspece() != Espece.PORC) {
            throw new IllegalArgumentException("L'extraction est réservée aux bandes de porcs.");
        }

        int nbAExtraire = request.animaux().size();
        if (nbAExtraire > bande.getEffectifActuel()) {
            throw new QuantiteInvalideException(nbAExtraire, bande.getEffectifActuel());
        }

        // Structure de destination par défaut
        Structure structureDefaut = null;
        if (request.structureDestinationId() != null) {
            structureDefaut = structureRepository.findById(request.structureDestinationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Structure",
                            request.structureDestinationId()));
        }

        List<Animal> animauxCrees = new ArrayList<>();

        for (ExtractionBandeRequest.AnimalExtraitDto dto : request.animaux()) {
            // Résoudre la structure de l'animal
            Structure structure = dto.structureId() != null
                    ? structureRepository.findById(dto.structureId())
                            .orElseThrow(() -> new ResourceNotFoundException("Structure", dto.structureId()))
                    : structureDefaut;

            if (structure == null) {
                throw new IllegalArgumentException(
                        "Une structure de destination est requise (globale ou par animal).");
            }

            // Créer l'entité Animal
            Animal animal = new Animal();
            animal.setEspece(Espece.PORC);
            animal.setRace(bande.getRace());
            animal.setSouche(bande.getSouche());
            animal.setSexe(dto.sexe());
            animal.setCodeBoucle(dto.codeBoucle());
            animal.setCodeRfid(dto.codeRfid());
            animal.setDateEntree(request.dateExtraction());
            animal.setPoidsEntreeKg(dto.poidsKg());
            animal.setPoidsActuelKg(dto.poidsKg());
            animal.setModeSuivi(ModeSuivi.INDIVIDUEL);
            animal.setStructure(structure);
            animal.setBande(null);
            animal.setProvenance(Provenance.INTERNE);
            animal.setStatut(StatutAnimal.ACTIF);
            animal.setNotes(dto.notes());
            animal.setCodeUnique(genererCodeUnique());
            animalRepository.save(animal);

            // Créer le ProfilPorcin
            ProfilPorcin profil = new ProfilPorcin();
            profil.setAnimal(animal);
            profil.setStatutReproductif(
                    dto.sexe() == Sexe.FEMELLE
                            ? StatutReproductifPorcin.COCHETTE
                            : StatutReproductifPorcin.VERRAT);
            profil.setBandeOrigine(bande);
            profil.setDateExtractionBande(request.dateExtraction());
            profil.setPoidsSelectionKg(dto.poidsKg());
            profilPorcinRepository.save(profil);

            // Mouvement EXTRACTION sur la bande
            MouvementAnimal mouvement = new MouvementAnimal();
            mouvement.setBande(bande);
            mouvement.setAnimal(animal);
            mouvement.setTypeMouvement(TypeMouvement.SORTIE_REFORME); // extraction = sortie de bande
            mouvement.setDateMouvement(request.dateExtraction());
            mouvement.setQuantite(1);
            mouvement.setStructureOrigine(bande.getStructure());
            mouvement.setStructureDestination(structure);
            mouvement.setMotif("Extraction pour mise en reproduction individuelle");
            mouvementRepository.save(mouvement);

            animauxCrees.add(animal);
        }

        // Décrémenter l'effectif de la bande
        bande.setEffectifActuel(bande.getEffectifActuel() - nbAExtraire);
        bandeRepository.save(bande);

        return new ExtractionBandeResponse(
                bande.getId(),
                bande.getNom(),
                nbAExtraire,
                bande.getEffectifActuel(),
                animauxCrees.stream().map(porcinService::toResponse).toList()
        );
    }

    private String genererCodeUnique() {
        long seq = animalRepository.count() + 1;
        return String.format("SF-PO-%05d", seq);
    }
}
