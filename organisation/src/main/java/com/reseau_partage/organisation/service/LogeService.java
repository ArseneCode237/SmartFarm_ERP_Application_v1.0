package com.reseau_partage.organisation.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Batiment;
import com.reseau_partage.core.entities.Loge;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.Structure;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.LogeRepository;
import com.reseau_partage.core.repository.StructureRepository;
import com.reseau_partage.organisation.dto.LogeRequest;
import com.reseau_partage.organisation.dto.LogeResponse;
import com.reseau_partage.organisation.exception.ConflictException;
import com.reseau_partage.organisation.exception.ResourceNotFoundException;

@Service
@Transactional
public class LogeService {

    private final LogeRepository logeRepository;
    private final StructureRepository structureRepository;
    private final BandeRepository bandeRepository;
    private final AnimalRepository animalRepository;

    public LogeService(LogeRepository logeRepository,
            StructureRepository structureRepository,
            BandeRepository bandeRepository,
            AnimalRepository animalRepository) {
        this.logeRepository = logeRepository;
        this.structureRepository = structureRepository;
        this.bandeRepository = bandeRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional(readOnly = true)
    public List<LogeResponse> listByBatiment(Long batimentId) {
        // Vérifie que le bâtiment existe
        getBatimentEntity(batimentId);
        return logeRepository.findByBatimentId(batimentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LogeResponse getById(Long logeId) {
        return toResponse(getLogeEntity(logeId));
    }

    public LogeResponse create(LogeRequest request) {
        // Vérification de l'unicité du code
        if (logeRepository.existsByCode(request.code().trim().toUpperCase())) {
            throw new ConflictException("Une loge portant ce code existe déjà : " + request.code());
        }
        // Vérifier que le bâtiment existe et est bien un bâtiment
        Batiment batiment = getBatimentEntity(request.batimentId());

        Loge loge = new Loge();
        applyToEntity(loge, request, batiment);
        loge = logeRepository.save(loge);

        // Affecter les animaux/bande si fournis
        affecterContenu(loge, request.bandeId(), request.animauxIds());
        return toResponse(logeRepository.save(loge));
    }

    public LogeResponse update(Long logeId, LogeRequest request) {
        Loge loge = getLogeEntity(logeId);
        Batiment batiment = getBatimentEntity(request.batimentId());

        // Vérification de l'unicité du code (si changé)
        if (!loge.getCode().equalsIgnoreCase(request.code().trim()) &&
                logeRepository.existsByCode(request.code().trim().toUpperCase())) {
            throw new ConflictException("Une loge portant ce code existe déjà : " + request.code());
        }

        applyToEntity(loge, request, batiment);
        // Réaffecter la bande et les animaux
        affecterContenu(loge, request.bandeId(), request.animauxIds());
        return toResponse(logeRepository.save(loge));
    }

    public void delete(Long logeId) {
        Loge loge = getLogeEntity(logeId);
        // Détacher les animaux de cette loge avant suppression
        List<Animal> animaux = animalRepository.findByLogeId(logeId);
        for (Animal animal : animaux) {
            animal.setLoge(null);
            animalRepository.save(animal);
        }
        // Détacher la bande
        loge.setBande(null);
        logeRepository.delete(loge);
    }

    /**
     * Récupère les données d'une loge existante pour pré-remplir un nouveau
     * formulaire.
     * Le code est préfixé avec "COP-" (doit être modifié par l'utilisateur pour
     * rester unique).
     * Le nom est préfixé avec "Copie - ".
     * La bande et les animaux affectés ne sont PAS renvoyés pour éviter les
     * conflits.
     */
    @Transactional(readOnly = true)
    public LogeRequest duplicate(Long logeId) {
        Loge loge = getLogeEntity(logeId);
        String nomCopie = "Copie - " + loge.getNom();
        String codeCopie = "COP-" + loge.getCode();

        return new LogeRequest(
                codeCopie,
                nomCopie,
                loge.getDescription(),
                loge.getCapaciteMaxAnimaux(),
                loge.getSuperficieM2(),
                loge.getBatiment().getId(),
                null, // bande non incluse : doit être réaffectée
                null // animaux non inclus : doivent être réaffectés
        );
    }

    /**
     * Affecter une bande entière à une loge.
     * Vérifie que l'effectif de la bande ne dépasse pas la capacité max de la loge.
     */
    public LogeResponse affecterBande(Long logeId, Long bandeId) {
        Loge loge = getLogeEntity(logeId);
        Bande bande = bandeRepository.findById(bandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bande", bandeId));

        int effectif = bande.getEffectifActuel() != null ? bande.getEffectifActuel() : 0;
        if (effectif > loge.getCapaciteMaxAnimaux()) {
            throw new IllegalArgumentException(
                    "L'effectif de la bande (" + effectif +
                            ") dépasse la capacité maximale de la loge (" + loge.getCapaciteMaxAnimaux() + ").");
        }

        loge.setBande(bande);
        // Retirer la bande de toutes les autres loges
        List<Loge> autresLoges = logeRepository.findByBandeId(bandeId);
        for (Loge autre : autresLoges) {
            if (!autre.getId().equals(loge.getId())) {
                autre.setBande(null);
                logeRepository.save(autre);
            }
        }
        return toResponse(logeRepository.save(loge));
    }

    /**
     * Retirer la bande actuellement affectée à une loge.
     */
    public LogeResponse retirerBande(Long logeId) {
        Loge loge = getLogeEntity(logeId);
        loge.setBande(null);
        return toResponse(logeRepository.save(loge));
    }

    /**
     * Affecter des animaux individuels à une loge.
     * Vérifie que la capacité max n'est pas dépassée.
     */
    public LogeResponse affecterAnimaux(Long logeId, List<Long> animauxIds) {
        Loge loge = getLogeEntity(logeId);
        if (animauxIds == null || animauxIds.isEmpty()) {
            return toResponse(loge);
        }

        // Compter animaux déjà affectés (sans compter ceux qu'on va remplacer)
        long countActuels = animalRepository.countByLogeId(logeId);
        // Vérification de la capacité
        if (countActuels + animauxIds.size() > loge.getCapaciteMaxAnimaux()) {
            throw new IllegalArgumentException(
                    "L'ajout de ces animaux dépasse la capacité maximale de la loge (" +
                            loge.getCapaciteMaxAnimaux() + "). Animaux actuellement : " + countActuels +
                            ", Tentative d'ajout : " + animauxIds.size());
        }

        for (Long animalId : animauxIds) {
            Animal animal = animalRepository.findById(animalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));
            if (animal.getStatut() != StatutAnimal.ACTIF) {
                throw new IllegalArgumentException(
                        "Impossible d'affecter l'animal id=" + animalId +
                                " : son statut est " + animal.getStatut() + " (seul ACTIF est autorisé).");
            }
            animal.setLoge(loge);
            animalRepository.save(animal);
        }
        return toResponse(logeRepository.save(loge));
    }

    /**
     * Retirer des animaux d'une loge (leur loge devient null).
     */
    public LogeResponse retirerAnimaux(Long logeId, List<Long> animauxIds) {
        if (animauxIds == null || animauxIds.isEmpty()) {
            return getById(logeId);
        }
        Loge loge = getLogeEntity(logeId);
        for (Long animalId : animauxIds) {
            Animal animal = animalRepository.findById(animalId)
                    .orElseThrow(() -> new ResourceNotFoundException("Animal", animalId));
            if (animal.getLoge() != null && animal.getLoge().getId().equals(logeId)) {
                animal.setLoge(null);
                animalRepository.save(animal);
            }
        }
        return toResponse(logeRepository.save(loge));
    }

    // ──────────────────── PRIVÉS ────────────────────

    private Batiment getBatimentEntity(Long structureId) {
        Structure s = structureRepository.findById(structureId)
                .orElseThrow(() -> new ResourceNotFoundException("Structure (bâtiment)", structureId));
        if (!(s instanceof Batiment batiment)) {
            throw new IllegalArgumentException(
                    "La structure id=" + structureId + " n'est pas un bâtiment (" +
                            (s != null ? s.getClass().getSimpleName() : "null") + ").");
        }
        return batiment;
    }

    private Loge getLogeEntity(Long logeId) {
        return logeRepository.findById(logeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loge", logeId));
    }

    private void applyToEntity(Loge loge, LogeRequest r, Batiment batiment) {
        loge.setCode(r.code().trim().toUpperCase());
        loge.setNom(r.nom().trim());
        loge.setDescription(r.description());
        loge.setCapaciteMaxAnimaux(r.capaciteMaxAnimaux());
        loge.setSuperficieM2(r.superficieM2());
        loge.setBatiment(batiment);
    }

    private void affecterContenu(Loge loge, Long bandeId, List<Long> animauxIds) {
        // Si on a une bande, l'affecter
        if (bandeId != null) {
            affecterBande(loge.getId(), bandeId);
        } else {
            loge.setBande(null);
        }
        // Si on a des animaux, les affecter
        if (animauxIds != null && !animauxIds.isEmpty()) {
            affecterAnimaux(loge.getId(), animauxIds);
        }
    }

    private LogeResponse toResponse(Loge loge) {
        List<Animal> animaux = animalRepository.findByLogeId(loge.getId());
        long countAnimaux = animaux.size();
        int countBande = (loge.getBande() != null && loge.getBande().getEffectifActuel() != null)
                ? loge.getBande().getEffectifActuel()
                : 0;
        int total = (int) Math.max(countAnimaux, countBande);
        Integer capacite = loge.getCapaciteMaxAnimaux();
        Double taux = (capacite == null || capacite == 0) ? null : (total * 100.0 / capacite);

        Batiment batiment = loge.getBatiment();
        Bande bande = loge.getBande();
        Long siteId = batiment.getSite() != null ? batiment.getSite().getId() : null;
        String siteNom = batiment.getSite() != null ? batiment.getSite().getNom() : null;
        Long fermeId = batiment.getSite() != null && batiment.getSite().getFerme() != null
                ? batiment.getSite().getFerme().getId()
                : null;
        String fermeNom = batiment.getSite() != null && batiment.getSite().getFerme() != null
                ? batiment.getSite().getFerme().getNom()
                : null;

        return new LogeResponse(
                loge.getId(),
                loge.getCode(),
                loge.getNom(),
                loge.getDescription(),
                loge.getCapaciteMaxAnimaux(),
                loge.getSuperficieM2(),
                batiment.getId(),
                batiment.getNom(),
                siteId,
                siteNom,
                fermeId,
                fermeNom,
                bande != null ? bande.getId() : null,
                bande != null ? bande.getNom() : null,
                bande != null ? bande.getEffectifActuel() : null,
                (int) countAnimaux,
                animaux.stream().map(this::animalSummary).toList(),
                taux,
                loge.getDateCreation(),
                loge.getDateModification());
    }

    private Map<String, Object> animalSummary(Animal animal) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", animal.getId());
        m.put("codeUnique", animal.getCodeUnique());
        m.put("codeBoucle", animal.getCodeBoucle());
        m.put("codeRfid", animal.getCodeRfid());
        m.put("nom", animal.getNom());
        m.put("espece", animal.getEspece());
        m.put("sexe", animal.getSexe());
        m.put("statut", animal.getStatut());
        m.put("dateNaissance", animal.getDateNaissance());
        m.put("poidsActuelKg", animal.getPoidsActuelKg());
        m.put("bandeId", animal.getBande() != null ? animal.getBande().getId() : null);
        m.put("bandeNom", animal.getBande() != null ? animal.getBande().getNom() : null);
        m.put("structureId", animal.getStructure() != null ? animal.getStructure().getId() : null);
        m.put("structureNom", animal.getStructure() != null ? animal.getStructure().getNom() : null);
        return m;
    }
}
