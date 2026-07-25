package com.reseau_partage.animaux.service;

import com.reseau_partage.animaux.dto.declaration.DeclarationAnimalRequest;
import com.reseau_partage.animaux.dto.declaration.DeclarationAnimalResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.*;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.DeclarationAnimalHistoriqueRepository;
import com.reseau_partage.core.repository.DeclarationAnimalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeclarationAnimalService {

    private final DeclarationAnimalRepository declarationAnimalRepository;
    private final DeclarationAnimalHistoriqueRepository historiqueRepository;
    private final AnimalRepository animalRepository;

    public DeclarationAnimalService(DeclarationAnimalRepository declarationAnimalRepository, DeclarationAnimalHistoriqueRepository historiqueRepository, AnimalRepository animalRepository) {
        this.declarationAnimalRepository = declarationAnimalRepository;
        this.historiqueRepository = historiqueRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional
    public DeclarationAnimalResponse creerDeclaration(DeclarationAnimalRequest request, Long utilisateurId, String utilisateurNom) {
        Animal animal = animalRepository.findById(request.animalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal introuvable avec l'id : " + request.animalId()));

        validerAnimalActif(animal);
        validerTypeMotif(request.type(), request.motif());

        if (request.type() == TypeDeclaration.VENTE) {
            validerChampsVente(request);
        }

        DeclarationAnimal declaration = new DeclarationAnimal();
        declaration.setAnimalId(animal.getId());
        declaration.setAnimalCodeUnique(animal.getCodeUnique());
        declaration.setAnimalCodeBoucle(animal.getCodeBoucle());
        declaration.setEspece(animal.getEspece().name());
        declaration.setFermeId(animal.getStructure().getSite().getFerme().getId());
        declaration.setUtilisateurId(utilisateurId);
        declaration.setUtilisateurNom(utilisateurNom);
        declaration.setType(request.type());
        declaration.setMotif(request.motif());
        declaration.setDateDeclaration(request.dateDeclaration());
        declaration.setPoidsKg(request.poidsKg());
        declaration.setPrixParKg(request.prixParKg() != null ? request.prixParKg() : false);
        declaration.setPrixUnitaire(request.prixUnitaire());
        declaration.setNomAcheteur(request.nomAcheteur());
        declaration.setTelephoneAcheteur(request.telephoneAcheteur());
        declaration.setLocaliteAcheteur(request.localiteAcheteur());
        declaration.setObservations(request.observations());
        declaration.setSource(SourceDeclaration.MANUEL);
        declaration.setStatut(StatutDeclaration.ACTIF);

        declaration = declarationAnimalRepository.save(declaration);

        mettreAJourAnimal(animal, request);

        sauvegarderHistorique(declaration, ActionHistorique.CREATION, null, utilisateurId, utilisateurNom);

        return toResponse(declaration);
    }

    @Transactional
    public DeclarationAnimalResponse annulerDeclaration(Long declarationId, String motifAnnulation, Long utilisateurId, String utilisateurNom) {
        DeclarationAnimal declaration = declarationAnimalRepository.findById(declarationId)
                .orElseThrow(() -> new ResourceNotFoundException("Declaration introuvable avec l'id : " + declarationId));

        if (declaration.getStatut() == StatutDeclaration.ANNULE) {
            throw new IllegalArgumentException("Cette déclaration est déjà annulée");
        }

        Map<String, Object> anciennesValeurs = toMap(declaration);

        declaration.setStatut(StatutDeclaration.ANNULE);
        declaration.setMotifAnnulation(motifAnnulation);
        declaration.setDateAnnulation(LocalDateTime.now());
        declaration.setUtilisateurAnnulationId(utilisateurId);
        declarationAnimalRepository.save(declaration);

        Animal animal = animalRepository.findById(declaration.getAnimalId()).orElseThrow();
        annulerEffetSurAnimal(animal, declaration);

        sauvegarderHistorique(declaration, ActionHistorique.ANNULATION, anciennesValeurs, utilisateurId, utilisateurNom);

        return toResponse(declaration);
    }

    public Page<DeclarationAnimalResponse> listerParAnimal(Long animalId, Pageable pageable) {
        return declarationAnimalRepository.findByAnimalIdAndStatut(animalId, StatutDeclaration.ACTIF, pageable).map(this::toResponse);
    }

    public Page<DeclarationAnimalResponse> listerToutes(Long fermeId, TypeDeclaration type, StatutDeclaration statut, java.time.LocalDate dateDebut, java.time.LocalDate dateFin, Pageable pageable) {
        return declarationAnimalRepository.findAllFiltered(fermeId, type, statut, dateDebut, dateFin, pageable).map(this::toResponse);
    }

    public List<DeclarationAnimalHistorique> getHistorique(Long declarationId) {
        return historiqueRepository.findByDeclarationAnimalIdOrderByDateActionDesc(declarationId);
    }

    private void validerAnimalActif(Animal animal) {
        if (animal.getStatut() != StatutAnimal.ACTIF) {
            throw new IllegalArgumentException("Impossible de déclarer sur un animal inactif : " + animal.getCodeUnique());
        }
    }

    private void validerTypeMotif(TypeDeclaration type, MotifDeclaration motif) {
        Map<TypeDeclaration, List<MotifDeclaration>> regles = Map.of(
                TypeDeclaration.MORT, List.of(
                        MotifDeclaration.MALADIE,
                        MotifDeclaration.ACCIDENT,
                        MotifDeclaration.PREDATEUR,
                        MotifDeclaration.CAUSE_INCONNUE,
                        MotifDeclaration.MORT_NEE,
                        MotifDeclaration.ASPHYXIE,
                        MotifDeclaration.CHALEUR_EXCESSIVE),
                TypeDeclaration.VENTE, List.of(
                        MotifDeclaration.PARTICULIER,
                        MotifDeclaration.GROSSISTE,
                        MotifDeclaration.ABATTOIR,
                        MotifDeclaration.BOUCHERIE,
                        MotifDeclaration.ELEVEUR,
                        MotifDeclaration.RESTAURATION),
                TypeDeclaration.REFORME, List.of(
                        MotifDeclaration.PRODUCTIVITE,
                        MotifDeclaration.AGE,
                        MotifDeclaration.BLESSURE,
                        MotifDeclaration.MALADIE_CHRONIQUE,
                        MotifDeclaration.DEFAUT_MORPHOLOGIQUE)
        );

        if (!regles.get(type).contains(motif)) {
            throw new IllegalArgumentException("Motif " + motif + " incompatible avec le type " + type);
        }
    }

    private void validerChampsVente(DeclarationAnimalRequest request) {
        if (request.prixUnitaire() == null) {
            throw new IllegalArgumentException("Le prix unitaire est obligatoire pour une vente");
        }
        if (request.nomAcheteur() == null || request.nomAcheteur().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'acheteur est obligatoire pour une vente");
        }
    }

    @Transactional
    private void mettreAJourAnimal(Animal animal, DeclarationAnimalRequest request) {
        animal.setDateDerniereDeclaration(request.dateDeclaration());

        switch (request.type()) {
            case MORT:
                animal.setStatut(StatutAnimal.MORT);
                animal.setDateSortie(request.dateDeclaration());
                animal.setCauseMort(request.motif().name());
                break;
            case VENTE:
                animal.setStatut(StatutAnimal.VENDU);
                animal.setDateSortie(request.dateDeclaration());
                animal.setMotifSortie(request.motif().name());
                break;
            case REFORME:
                animal.setStatut(StatutAnimal.REFORME);
                animal.setDateSortie(request.dateDeclaration());
                animal.setMotifSortie(request.motif().name());
                break;
        }

        animalRepository.save(animal);
    }

    @Transactional
    private void annulerEffetSurAnimal(Animal animal, DeclarationAnimal declaration) {
        animal.setDateDerniereDeclaration(null);

        animal.setStatut(StatutAnimal.ACTIF);
        animal.setDateSortie(null);
        animal.setCauseMort(null);
        animal.setMotifSortie(null);

        animalRepository.save(animal);
    }

    private void sauvegarderHistorique(DeclarationAnimal declaration, ActionHistorique action, Map<String, Object> anciennesValeurs, Long utilisateurId, String utilisateurNom) {
        DeclarationAnimalHistorique historique = new DeclarationAnimalHistorique();
        historique.setDeclarationAnimalId(declaration.getId());
        historique.setAction(action);
        historique.setAnciennesValeurs(anciennesValeurs);
        historique.setNouvellesValeurs(toMap(declaration));
        historique.setUtilisateurId(utilisateurId);
        historique.setUtilisateurNom(utilisateurNom);
        historiqueRepository.save(historique);
    }

    private Map<String, Object> toMap(DeclarationAnimal d) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", d.getType());
        map.put("motif", d.getMotif());
        map.put("dateDeclaration", d.getDateDeclaration());
        map.put("prixUnitaire", d.getPrixUnitaire());
        map.put("montantTotal", d.getMontantTotal());
        map.put("statut", d.getStatut());
        return map;
    }

    private DeclarationAnimalResponse toResponse(DeclarationAnimal d) {
        return new DeclarationAnimalResponse(
                d.getId(),
                d.getAnimalId(),
                d.getAnimalCodeUnique(),
                d.getAnimalCodeBoucle(),
                d.getEspece(),
                d.getFermeId(),
                d.getType(),
                d.getMotif(),
                d.getDateDeclaration(),
                d.getPoidsKg(),
                d.getPrixParKg(),
                d.getPrixUnitaire(),
                d.getMontantTotal(),
                d.getNomAcheteur(),
                d.getTelephoneAcheteur(),
                d.getLocaliteAcheteur(),
                d.getObservations(),
                d.getSource(),
                d.getStatut(),
                d.getUtilisateurNom(),
                d.getDateCreation(),
                d.getDateModification()
        );
    }
}
