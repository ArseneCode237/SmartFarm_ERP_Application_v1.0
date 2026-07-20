package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.reproduction.EvenementReproductionRequest;
import com.reseau_partage.animaux.dto.reproduction.EvenementReproductionResponse;
import com.reseau_partage.animaux.dto.reproduction.MiseBasRequest;
import com.reseau_partage.animaux.dto.reproduction.SevrageRequest;
import com.reseau_partage.animaux.exception.ConfigEspeceIntrouvableException;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.animaux.exception.StatutReproducteurInvalideException;
import com.reseau_partage.animaux.mapper.EvenementReproductionMapper;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.ConfigEspece;
import com.reseau_partage.core.entities.EvenementReproduction;
import com.reseau_partage.core.entities.MouvementAnimal;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutGestation;
import com.reseau_partage.core.entities.StatutReproducteur;
import com.reseau_partage.core.entities.TypeMouvement;
import com.reseau_partage.core.entities.TypeReproduction;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.ConfigEspeceRepository;
import com.reseau_partage.core.repository.EvenementReproductionRepository;
import com.reseau_partage.core.repository.MouvementAnimalRepository;

@Service
public class ReproductionService {

    private final EvenementReproductionRepository reproductionRepository;
    private final AnimalRepository animalRepository;
    private final ConfigEspeceRepository configEspeceRepository;
    private final MouvementAnimalRepository mouvementRepository;
    private final EvenementReproductionMapper mapper;

    public ReproductionService(EvenementReproductionRepository reproductionRepository, AnimalRepository animalRepository,
                               ConfigEspeceRepository configEspeceRepository, MouvementAnimalRepository mouvementRepository,
                               EvenementReproductionMapper mapper) {
        this.reproductionRepository = reproductionRepository;
        this.animalRepository = animalRepository;
        this.configEspeceRepository = configEspeceRepository;
        this.mouvementRepository = mouvementRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EvenementReproductionResponse enregistrerSaillie(EvenementReproductionRequest request) {
        Animal femelle = animalRepository.findById(request.femelleId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", request.femelleId()));
        if (femelle.getStatut() != StatutAnimal.ACTIF) {
            throw new IllegalArgumentException("La femelle doit etre active pour une saillie.");
        }
        if (femelle.getStatutReproducteur() == null || femelle.getStatutReproducteur() != StatutReproducteur.ACTIF) {
            throw new StatutReproducteurInvalideException("La femelle doit avoir le statut reproducteur ACTIF.");
        }
        Animal male = null;
        if (request.maleId() != null) {
            male = animalRepository.findById(request.maleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal", request.maleId()));
            if (male.getEspece() != femelle.getEspece()) {
                throw new IllegalArgumentException("Le male doit etre de la meme espece que la femelle.");
            }
        }

        ConfigEspece config = configEspeceRepository.findByEspece(femelle.getEspece()).orElse(null);
        if (config == null) {
            throw new ConfigEspeceIntrouvableException("Aucune configuration pour l'espece " + femelle.getEspece());
        }

        EvenementReproduction evenement = new EvenementReproduction();
        evenement.setFemelle(femelle);
        evenement.setMale(male);
        evenement.setType(request.type() != null ? request.type() : TypeReproduction.SAILLIE);
        evenement.setDateSaillie(request.dateSaillie() != null ? request.dateSaillie() : LocalDate.now());
        evenement.setStatut(StatutGestation.EN_ATTENTE);
        evenement.setNotes(request.notes());

        LocalDate dateMiseBasPrevue = evenement.getDateSaillie();
        if (config.getDureeGestationJours() != null) {
            dateMiseBasPrevue = evenement.getDateSaillie().plusDays(config.getDureeGestationJours());
        }
        evenement.setDateMiseBasPrevue(dateMiseBasPrevue);
        if (config.getDureeSevrageJours() != null) {
            evenement.setDateSevragePrevu(dateMiseBasPrevue.plusDays(config.getDureeSevrageJours()));
        }

        femelle.setStatutReproducteur(StatutReproducteur.EN_GESTATION);
        animalRepository.save(femelle);
        reproductionRepository.save(evenement);
        return mapper.toResponse(evenement);
    }

    @Transactional(readOnly = true)
    public List<EvenementReproductionResponse> historiqueAnimal(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal", animalId);
        }
        return reproductionRepository.findByFemelleId(animalId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public EvenementReproductionResponse declarerMiseBas(Long id, MiseBasRequest request) {
        EvenementReproduction evenement = reproductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvenementReproduction", id));
        if (evenement.getStatut() == StatutGestation.MISE_BAS) {
            throw new IllegalArgumentException("Une mise-bas a deja ete declaree pour cet evenement.");
        }
        Animal femelle = evenement.getFemelle();
        evenement.setDateMiseBasReelle(request.dateMiseBasReelle() != null ? request.dateMiseBasReelle() : LocalDate.now());
        evenement.setNombreNesVivants(request.nombreNesVivants());
        evenement.setNombreNesMorts(request.nombreNesMorts());
        evenement.setPoidsMoyenNaissanceKg(request.poidsMoyenNaissanceKg());
        evenement.setStatut(StatutGestation.MISE_BAS);
        evenement.setNotes(request.notes());

        int nesVivants = request.nombreNesVivants() != null ? request.nombreNesVivants() : 0;
        for (int i = 0; i < nesVivants; i++) {
            Animal bebe = new Animal();
            bebe.setEspece(femelle.getEspece());
            bebe.setRace(femelle.getRace());
            bebe.setSouche(femelle.getSouche());
            bebe.setDateEntree(evenement.getDateMiseBasReelle());
            bebe.setDateNaissance(evenement.getDateMiseBasReelle());
            bebe.setPoidsEntreeKg(request.poidsMoyenNaissanceKg());
            bebe.setPoidsActuelKg(request.poidsMoyenNaissanceKg());
            bebe.setModeSuivi(femelle.getModeSuivi());
            bebe.setBande(femelle.getBande());
            bebe.setStructure(femelle.getStructure());
            bebe.setMere(femelle);
            bebe.setPere(evenement.getMale());
            bebe.setProvenance(com.reseau_partage.core.entities.Provenance.NAISSANCE_INTERNE);
            bebe.setStatut(StatutAnimal.ACTIF);
            bebe.setCodeUnique(genererCodeNaissance(femelle.getEspece()));
            animalRepository.save(bebe);
        }

        int nesMorts = request.nombreNesMorts() != null ? request.nombreNesMorts() : 0;
        if (nesMorts > 0) {
            MouvementAnimal deces = new MouvementAnimal();
            deces.setAnimal(femelle);
            deces.setBande(femelle.getBande());
            deces.setTypeMouvement(TypeMouvement.SORTIE_MORT);
            deces.setDateMouvement(evenement.getDateMiseBasReelle());
            deces.setQuantite(nesMorts);
            deces.setStructureOrigine(femelle.getStructure());
            deces.setMotif("Deces a la naissance");
            deces.setOperateurNom(request.operateurNom());
            mouvementRepository.save(deces);
        }

        femelle.setStatutReproducteur(StatutReproducteur.EN_LACTATION);
        animalRepository.save(femelle);
        reproductionRepository.save(evenement);
        return mapper.toResponse(evenement);
    }

    @Transactional
    public EvenementReproductionResponse enregistrerSevrage(Long id, SevrageRequest request) {
        EvenementReproduction evenement = reproductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvenementReproduction", id));
        evenement.setDateSevrageReel(request.dateSevrageReel() != null ? request.dateSevrageReel() : LocalDate.now());
        evenement.setPoidsAuSevrageKg(request.poidsAuSevrageKg());
        if (request.notes() != null) {
            evenement.setNotes(request.notes());
        }
        Animal femelle = evenement.getFemelle();
        femelle.setStatutReproducteur(StatutReproducteur.ACTIF);
        animalRepository.save(femelle);
        reproductionRepository.save(evenement);
        return mapper.toResponse(evenement);
    }

    @Transactional(readOnly = true)
    public List<EvenementReproductionResponse> alertes(int joursHorizon) {
        LocalDate debut = LocalDate.now();
        LocalDate fin = debut.plusDays(joursHorizon);
        return reproductionRepository.findGestationsProchesTerme(debut, fin).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public void changerStatutReproducteur(Long animalId, String statut) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));
        StatutReproducteur nouveau;
        try {
            nouveau = StatutReproducteur.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new StatutReproducteurInvalideException("Statut reproducteur invalide : '" + statut + "'. Valeurs acceptées : " + java.util.Arrays.toString(StatutReproducteur.values()));
        }
        animal.setStatutReproducteur(nouveau);
        animalRepository.save(animal);
    }

    private String genererCodeNaissance(com.reseau_partage.core.entities.Espece espece) {
        String prefixe = "NA";
        long sequence = animalRepository.count() + 1;
        return String.format("SF-%s-%05d", prefixe, sequence);
    }
}
