package com.reseau_partage.animaux.service;

import com.reseau_partage.animaux.dto.saillie.SaillieConfirmationRequest;
import com.reseau_partage.animaux.dto.saillie.SaillieRequest;
import com.reseau_partage.animaux.dto.saillie.SaillieResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.*;
import com.reseau_partage.core.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SaillieService {

    /** Durée de gestation porcine en jours (fixe, sauf config spécifique). */
    private static final int DUREE_GESTATION_JOURS = 114;
    /** Jours avant terme pour le transfert en loge maternité. */
    private static final int JOURS_AVANT_TERME_MATERNITE = 7;
    /** Jours estimés avant retour en chaleur post-ECHEC. */
    private static final int JOURS_RETOUR_CHALEUR_ECHEC = 5;

    private final SaillieRepository saillieRepository;
    private final AnimalRepository animalRepository;
    private final ProfilPorcinRepository profilPorcinRepository;
    private final ConfigEspeceRepository configEspeceRepository;

    public SaillieService(SaillieRepository saillieRepository,
                          AnimalRepository animalRepository,
                          ProfilPorcinRepository profilPorcinRepository,
                          ConfigEspeceRepository configEspeceRepository) {
        this.saillieRepository      = saillieRepository;
        this.animalRepository       = animalRepository;
        this.profilPorcinRepository = profilPorcinRepository;
        this.configEspeceRepository = configEspeceRepository;
    }

    /** Enregistre une nouvelle saillie. */
    @Transactional
    public SaillieResponse enregistrer(SaillieRequest request) {
        Animal truie = animalRepository.findById(request.truieId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal (truie)", request.truieId()));

        if (truie.getEspece() != Espece.PORC || truie.getSexe() != Sexe.FEMELLE) {
            throw new IllegalArgumentException("La truie doit être une femelle porcine.");
        }
        if (truie.getStatut() != StatutAnimal.ACTIF) {
            throw new IllegalArgumentException("La truie doit être active.");
        }

        ProfilPorcin profil = profilPorcinRepository.findByAnimalId(request.truieId())
                .orElseThrow(() -> new ResourceNotFoundException("ProfilPorcin", request.truieId()));

        // Vérifier que le statut permet une saillie
        StatutReproductifPorcin statut = profil.getStatutReproductif();
        if (statut != StatutReproductifPorcin.EN_ATTENTE_SAILLIE
                && statut != StatutReproductifPorcin.EN_CHALEUR
                && statut != StatutReproductifPorcin.COCHETTE) {
            throw new IllegalArgumentException(
                    "La truie (id=" + request.truieId() + ", statut=" + statut + ") ne peut pas être saillie. "
                    + "Statuts autorisés : EN_ATTENTE_SAILLIE, EN_CHALEUR, COCHETTE.");
        }

        // Verrat optionnel
        Animal verrat = null;
        if (request.verratId() != null) {
            verrat = animalRepository.findById(request.verratId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal (verrat)", request.verratId()));
            if (verrat.getEspece() != Espece.PORC || verrat.getSexe() != Sexe.MALE) {
                throw new IllegalArgumentException("Le verrat doit être un mâle porcin.");
            }
        }

        // Durée de gestation depuis la config espèce (ou valeur fixe par défaut)
        int dureeGestation = configEspeceRepository.findByEspece(Espece.PORC)
                .map(c -> c.getDureeGestationJours() != null ? c.getDureeGestationJours() : DUREE_GESTATION_JOURS)
                .orElse(DUREE_GESTATION_JOURS);

        // Numérotation carrière
        long nbSaillies = saillieRepository.countByTruieId(request.truieId());
        int numeroSaillie = (int) nbSaillies + 1;
        int numeroPortee  = profil.getNbPorteesTotal() + 1;

        // Dates calculées
        LocalDate dateMiseBasPrevue     = request.dateSaillie().plusDays(dureeGestation);
        LocalDate dateTransfertMaternite = dateMiseBasPrevue.minusDays(JOURS_AVANT_TERME_MATERNITE);

        Saillie saillie = new Saillie();
        saillie.setTruie(truie);
        saillie.setVerrat(verrat);
        saillie.setTypeSaillie(request.typeSaillie());
        saillie.setNumeroSaillieCarriere(numeroSaillie);
        saillie.setNumeroPorteeCorrespondante(numeroPortee);
        saillie.setDateSaillie(request.dateSaillie());
        saillie.setDateDeuxiemeSaillie(request.dateDeuxiemeSaillie());
        saillie.setStatut(StatutSaillie.EN_ATTENTE);
        saillie.setDateMiseBasPrevue(dateMiseBasPrevue);
        saillie.setDateTransfertMaternitePrevue(dateTransfertMaternite);
        saillie.setSemenceFournisseur(request.semenceFournisseur());
        saillie.setSemenceReference(request.semenceReference());
        saillie.setOperateurNom(request.operateurNom());
        saillie.setNotes(request.notes());
        saillieRepository.save(saillie);

        // Mettre à jour le profil
        profil.setStatutReproductif(StatutReproductifPorcin.SAILLIE);
        profil.setSaillieActive(saillie);
        profil.setDateMiseBasPrevue(dateMiseBasPrevue);
        profilPorcinRepository.save(profil);

        return toResponse(saillie);
    }

    /** Confirme ou infirme la gestation après écho à J+28. */
    @Transactional
    public SaillieResponse confirmer(Long saillieId, SaillieConfirmationRequest request) {
        Saillie saillie = saillieRepository.findById(saillieId)
                .orElseThrow(() -> new ResourceNotFoundException("Saillie", saillieId));

        if (saillie.getStatut() != StatutSaillie.EN_ATTENTE) {
            throw new IllegalArgumentException(
                    "Impossible de confirmer la saillie id=" + saillieId + " : statut actuel=" + saillie.getStatut()
                    + ". Seules les saillies EN_ATTENTE peuvent être confirmées.");
        }

        ProfilPorcin profil = profilPorcinRepository.findByAnimalId(saillie.getTruie().getId())
                .orElseThrow(() -> new ResourceNotFoundException("ProfilPorcin",
                        saillie.getTruie().getId()));

        if (request.statut() == StatutSaillie.CONFIRMEE) {
            saillie.setStatut(StatutSaillie.CONFIRMEE);
            saillie.setDateConfirmationEcho(request.dateEcho());
            profil.setStatutReproductif(StatutReproductifPorcin.GESTATION);

        } else if (request.statut() == StatutSaillie.ECHEC) {
            if (request.motifEchec() == null || request.motifEchec().isBlank()) {
                throw new IllegalArgumentException("Le motif d'échec est obligatoire pour une confirmation ECHEC.");
            }
            saillie.setStatut(StatutSaillie.ECHEC);
            saillie.setDateInfirmation(request.dateEcho());
            saillie.setMotifEchec(request.motifEchec());
            profil.setStatutReproductif(StatutReproductifPorcin.EN_CHALEUR);
            profil.setSaillieActive(null);
            profil.setDateMiseBasPrevue(null);
            profil.setDateProchainesSailliePrevue(LocalDate.now().plusDays(JOURS_RETOUR_CHALEUR_ECHEC));
        } else {
            throw new IllegalArgumentException(
                    "Statut invalide pour la confirmation de la saillie id=" + saillieId + " : " + request.statut()
                    + ". Attendu : CONFIRMEE ou ECHEC.");
        }

        if (request.notes() != null) saillie.setNotes(request.notes());
        saillieRepository.save(saillie);
        profilPorcinRepository.save(profil);
        return toResponse(saillie);
    }

    /** Déclare un avortement en cours de gestation. */
    @Transactional
    public SaillieResponse avortement(Long saillieId, String notes) {
        Saillie saillie = saillieRepository.findById(saillieId)
                .orElseThrow(() -> new ResourceNotFoundException("Saillie", saillieId));

        if (saillie.getStatut() != StatutSaillie.CONFIRMEE) {
            throw new IllegalArgumentException(
                    "L'avortement ne peut être déclaré que sur une gestation CONFIRMEE.");
        }

        saillie.setStatut(StatutSaillie.AVORTEMENT);
        if (notes != null) saillie.setNotes(notes);
        saillieRepository.save(saillie);

        ProfilPorcin profil = profilPorcinRepository.findByAnimalId(saillie.getTruie().getId())
                .orElse(null);
        if (profil != null) {
            profil.setStatutReproductif(StatutReproductifPorcin.EN_CHALEUR);
            profil.setSaillieActive(null);
            profil.setDateMiseBasPrevue(null);
            profil.setDateProchainesSailliePrevue(LocalDate.now().plusDays(JOURS_RETOUR_CHALEUR_ECHEC));
            profilPorcinRepository.save(profil);
        }
        return toResponse(saillie);
    }

    /** Historique des saillies d'une truie. */
    @Transactional(readOnly = true)
    public List<SaillieResponse> historiqueParTruie(Long truieId) {
        return saillieRepository.findByTruieIdOrderByDateSaillieDesc(truieId)
                .stream().map(this::toResponse).toList();
    }

    /** Historique et statistiques d'un verrat. */
    @Transactional(readOnly = true)
    public Map<String, Object> statsVerrat(Long verratId) {
        List<SaillieResponse> saillies = saillieRepository
                .findByVerratIdOrderByDateSaillieDesc(verratId)
                .stream().map(this::toResponse).toList();
        long reussies  = saillieRepository.countSailliesReussiesByVerrat(verratId);
        long terminees = saillieRepository.countSailliesTerminesByVerrat(verratId);
        double taux = terminees > 0 ? (double) reussies / terminees * 100 : 0;
        return Map.of(
                "verratId", verratId,
                "totalSaillies", saillies.size(),
                "sailliesReussies", reussies,
                "tauxFertilitePct", Math.round(taux * 10.0) / 10.0,
                "saillies", saillies
        );
    }

    /** Saillies en attente d'écho dont le J+28 est dépassé. */
    @Transactional(readOnly = true)
    public List<SaillieResponse> alertesEchoAttendu() {
        LocalDate seuil = LocalDate.now().minusDays(28);
        return saillieRepository.findSailliesEnAttenteEcho(seuil)
                .stream().map(this::toResponse).toList();
    }

    /** Construit la réponse d'une saillie. */
    public SaillieResponse toResponse(Saillie s) {
        return new SaillieResponse(
                s.getId(),
                s.getTruie() != null ? s.getTruie().getId() : null,
                s.getTruie() != null ? s.getTruie().getCodeUnique() : null,
                s.getVerrat() != null ? s.getVerrat().getId() : null,
                s.getVerrat() != null ? s.getVerrat().getCodeUnique() : null,
                s.getTypeSaillie(),
                s.getNumeroSaillieCarriere(),
                s.getNumeroPorteeCorrespondante(),
                s.getDateSaillie(),
                s.getDateDeuxiemeSaillie(),
                s.getStatut(),
                s.getDateConfirmationEcho(),
                s.getDateInfirmation(),
                s.getMotifEchec(),
                s.getDateMiseBasPrevue(),
                s.getDateTransfertMaternitePrevue(),
                s.getSemenceFournisseur(),
                s.getOperateurNom(),
                s.getNotes(),
                s.getDateCreation()
        );
    }
}
