package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.misebas.MiseBaRequest;
import com.reseau_partage.animaux.dto.misebas.MiseBaResponse;
import com.reseau_partage.animaux.dto.misebas.SevrageRequest;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.MiseBas;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.MouvementAnimal;
import com.reseau_partage.core.entities.ProfilPorcin;
import com.reseau_partage.core.entities.Provenance;
import com.reseau_partage.core.entities.Saillie;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutReproductifPorcin;
import com.reseau_partage.core.entities.StatutSaillie;
import com.reseau_partage.core.entities.TypeMouvement;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.ConfigEspeceRepository;
import com.reseau_partage.core.repository.MiseBaRepository;
import com.reseau_partage.core.repository.MouvementAnimalRepository;
import com.reseau_partage.core.repository.ProfilPorcinRepository;
import com.reseau_partage.core.repository.SaillieRepository;

@Service
public class MiseBaService {

    /** Durée de lactation standard en jours si aucune config disponible. */
    private static final int DUREE_LACTATION_DEFAUT = 21;
    /** Jours estimés avant retour en chaleur post-sevrage. */
    private static final int JOURS_RETOUR_CHALEUR_POST_SEVRAGE = 5;

    private final MiseBaRepository miseBaRepository;
    private final SaillieRepository saillieRepository;
    private final AnimalRepository animalRepository;
    private final ProfilPorcinRepository profilPorcinRepository;
    private final BandeRepository bandeRepository;
    private final MouvementAnimalRepository mouvementRepository;
    private final ConfigEspeceRepository configEspeceRepository;

    public MiseBaService(MiseBaRepository miseBaRepository,
                         SaillieRepository saillieRepository,
                         AnimalRepository animalRepository,
                         ProfilPorcinRepository profilPorcinRepository,
                         BandeRepository bandeRepository,
                         MouvementAnimalRepository mouvementRepository,
                         ConfigEspeceRepository configEspeceRepository) {
        this.miseBaRepository       = miseBaRepository;
        this.saillieRepository      = saillieRepository;
        this.animalRepository       = animalRepository;
        this.profilPorcinRepository = profilPorcinRepository;
        this.bandeRepository        = bandeRepository;
        this.mouvementRepository    = mouvementRepository;
        this.configEspeceRepository = configEspeceRepository;
    }

    /**
     * Déclare une mise-bas.
     * Crée les porcelets vivants, enregistre les mort-nés, met à jour le profil de la truie.
     * @Transactional — tout passe ou rien ne passe.
     */
    @Transactional
    public MiseBaResponse declarer(MiseBaRequest request) {
        Animal truie = animalRepository.findById(request.truieId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal (truie)", request.truieId()));

        Saillie saillie = saillieRepository.findById(request.saillieId())
                .orElseThrow(() -> new ResourceNotFoundException("Saillie", request.saillieId()));

        if (saillie.getStatut() != StatutSaillie.CONFIRMEE) {
            throw new IllegalArgumentException(
                    "La saillie doit être CONFIRMEE pour déclarer une mise-bas.");
        }
        if (!saillie.getTruie().getId().equals(truie.getId())) {
            throw new IllegalArgumentException(
                    "La saillie ne correspond pas à cette truie.");
        }
        if (miseBaRepository.findBySaillieId(request.saillieId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Une mise-bas a déjà été déclarée pour cette saillie.");
        }

        ProfilPorcin profil = profilPorcinRepository.findByAnimalId(truie.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ProfilPorcin", truie.getId()));

        // Durée de sevrage depuis la config
        int dureeSevrageJours = configEspeceRepository.findByEspece(Espece.PORC)
                .map(c -> c.getDureeSevrageJours() != null
                        ? c.getDureeSevrageJours() : DUREE_LACTATION_DEFAUT)
                .orElse(DUREE_LACTATION_DEFAUT);

        // Numéro de portée
        int numeroPortee = (int) miseBaRepository.countByTruieId(truie.getId()) + 1;

        // Calcul de l'intervalle depuis la portée précédente
        Integer joursPrecedente = null;
        MiseBas precedente = miseBaRepository
                .findTopByTruieIdOrderByDateMiseBasReelleDesc(truie.getId()).orElse(null);
        if (precedente != null) {
            joursPrecedente = (int) ChronoUnit.DAYS.between(
                    precedente.getDateMiseBasReelle().toLocalDate(),
                    request.dateMiseBasReelle().toLocalDate());
        }

        // Destination des porcelets
        Bande bandeDestination = null;
        if (request.bandeDestinationId() != null) {
            bandeDestination = bandeRepository.findById(request.bandeDestinationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bande",
                            request.bandeDestinationId()));
        }

        // Créer l'entité MiseBas
        LocalDate dateMiseBas = request.dateMiseBasReelle().toLocalDate();
        MiseBas miseBas = new MiseBas();
        miseBas.setSaillie(saillie);
        miseBas.setTruie(truie);
        miseBas.setNumeroPortee(numeroPortee);
        miseBas.setNom(request.nom());
        miseBas.setDateMiseBasReelle(request.dateMiseBasReelle());
        miseBas.setDureeMiseBaMinutes(request.dureeMiseBaMinutes());
        miseBas.setTypeMiseBas(request.typeMiseBas());
        miseBas.setNbNesVivants(request.nbNesVivants());
        miseBas.setNbMortNes(request.nbMortNes() != null ? request.nbMortNes() : 0);
        miseBas.setNbMomifies(request.nbMomifies() != null ? request.nbMomifies() : 0);
        miseBas.setPoidsMoyenNaissanceKg(request.poidsMoyenNaissanceKg());
        miseBas.setPoidsMinNaissanceKg(request.poidsMinNaissanceKg());
        miseBas.setPoidsMaxNaissanceKg(request.poidsMaxNaissanceKg());
        miseBas.setNbPorceletsAllaites(
                request.nbPorceletsAllaites() != null
                        ? request.nbPorceletsAllaites()
                        : request.nbNesVivants());
        miseBas.setDateSevragePrevu(dateMiseBas.plusDays(dureeSevrageJours));
        miseBas.setBandeDestination(bandeDestination);
        miseBas.setJoursdepuisPorteePrecedente(joursPrecedente);
        miseBas.setVeterinaireNom(request.veterinaireNom());
        miseBas.setOperateurNom(request.operateurNom());
        miseBas.setNotes(request.notes());
        miseBaRepository.save(miseBas);

        // Créer les porcelets vivants
        for (int i = 0; i < request.nbNesVivants(); i++) {
            Animal porcelet = new Animal();
            porcelet.setEspece(Espece.PORC);
            porcelet.setRace(truie.getRace());
            porcelet.setDateNaissance(dateMiseBas);
            porcelet.setDateEntree(dateMiseBas);
            porcelet.setPoidsEntreeKg(request.poidsMoyenNaissanceKg());
            porcelet.setPoidsActuelKg(request.poidsMoyenNaissanceKg());
            porcelet.setModeSuivi(bandeDestination != null ? ModeSuivi.BANDE : ModeSuivi.INDIVIDUEL);
            porcelet.setBande(bandeDestination);
            porcelet.setStructure(truie.getStructure());
            porcelet.setMere(truie);
            porcelet.setPere(saillie.getVerrat());
            porcelet.setProvenance(Provenance.NAISSANCE_INTERNE);
            porcelet.setStatut(StatutAnimal.ACTIF);
            porcelet.setCodeUnique(String.format("SF-PO-NA-%05d", animalRepository.count() + 1));
            animalRepository.save(porcelet);
        }

        // Enregistrer les mort-nés comme mouvement SORTIE_MORT
        int nbMortNes = miseBas.getNbMortNes() != null ? miseBas.getNbMortNes() : 0;
        if (nbMortNes > 0) {
            MouvementAnimal deces = new MouvementAnimal();
            deces.setBande(truie.getBande());
            deces.setTypeMouvement(TypeMouvement.SORTIE_MORT);
            deces.setDateMouvement(dateMiseBas);
            deces.setQuantite(nbMortNes);
            deces.setStructureOrigine(truie.getStructure());
            deces.setMotif("Décès à la naissance — portée #" + numeroPortee);
            deces.setOperateurNom(request.operateurNom());
            mouvementRepository.save(deces);
        }

        // Mettre à jour le profil reproductif de la truie
        profil.setStatutReproductif(StatutReproductifPorcin.LACTATION);
        profil.setNumeroPorteeActuelle(numeroPortee);
        profil.setNbPorteesTotal(profil.getNbPorteesTotal() + 1);
        profil.setNbPorceletsTotalNesVivants(
                profil.getNbPorceletsTotalNesVivants() + request.nbNesVivants());
        profil.setNbPorceletsTotalMortNes(
                profil.getNbPorceletsTotalMortNes() + miseBas.getNbMortNes());
        profil.setSaillieActive(null);
        profil.setDateMiseBasPrevue(null);
        profil.setDateSevragePrevu(miseBas.getDateSevragePrevu());
        recalculerMoyennes(profil);
        profilPorcinRepository.save(profil);

        return toResponse(miseBas);
    }

    /** Enregistre le sevrage d'une portée et fait passer la truie EN_CHALEUR. */
    @Transactional
    public MiseBaResponse sevrer(Long miseBaId, SevrageRequest request) {
        MiseBas miseBas = miseBaRepository.findById(miseBaId)
                .orElseThrow(() -> new ResourceNotFoundException("MiseBas", miseBaId));

        if (miseBas.getDateSevrageReel() != null) {
            throw new IllegalArgumentException("Cette portée a déjà été sevrée.");
        }

        miseBas.setDateSevrageReel(request.dateSevrageReel());
        miseBas.setNbSevres(request.nbSevres());
        miseBas.setPoidsMoyenSevrageKg(request.poidsMoyenSevrageKg());
        miseBas.setDureeLactationJours((int) ChronoUnit.DAYS.between(
                miseBas.getDateMiseBasReelle().toLocalDate(), request.dateSevrageReel()));
        if (request.notes() != null) miseBas.setNotes(request.notes());

        // Rattacher les porcelets à la bande destination si fournie
        if (request.bandeDestinationId() != null && miseBas.getBandeDestination() == null) {
            Bande bande = bandeRepository.findById(request.bandeDestinationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bande",
                            request.bandeDestinationId()));
            miseBas.setBandeDestination(bande);
        }
        miseBaRepository.save(miseBas);

        // Mettre à jour le profil reproductif
        ProfilPorcin profil = profilPorcinRepository
                .findByAnimalId(miseBas.getTruie().getId()).orElse(null);
        if (profil != null) {
            profil.setNbPorceletsTotalSevres(
                    profil.getNbPorceletsTotalSevres() + request.nbSevres());
            profil.setStatutReproductif(StatutReproductifPorcin.EN_CHALEUR);
            profil.setDateSevragePrevu(null);
            profil.setDateRetourChaleurEstimee(
                    request.dateSevrageReel().plusDays(JOURS_RETOUR_CHALEUR_POST_SEVRAGE));
            profil.setDateProchainesSailliePrevue(
                    request.dateSevrageReel().plusDays(JOURS_RETOUR_CHALEUR_POST_SEVRAGE));
            recalculerMoyennes(profil);
            profilPorcinRepository.save(profil);
        }

        return toResponse(miseBas);
    }

    /** Toutes les portées d'une truie. */
    @Transactional(readOnly = true)
    public List<MiseBaResponse> parTruie(Long truieId) {
        return miseBaRepository.findByTruieIdOrderByDateMiseBasReelleDesc(truieId)
                .stream().map(this::toResponse).toList();
    }

    /** Détail d'une mise-bas. */
    @Transactional(readOnly = true)
    public MiseBaResponse get(Long id) {
        return toResponse(miseBaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MiseBas", id)));
    }

    /** Mises-bas imminentes dans les 7 prochains jours. */
    @Transactional(readOnly = true)
    public List<com.reseau_partage.core.entities.ProfilPorcin> alertesImminentes() {
        LocalDate debut = LocalDate.now();
        LocalDate fin   = debut.plusDays(7);
        return profilPorcinRepository.findMisesBasProches(debut, fin);
    }

    /** Sevrages à effectuer cette semaine. */
    @Transactional(readOnly = true)
    public List<MiseBaResponse> alertesSevrages() {
        LocalDate debut = LocalDate.now();
        LocalDate fin   = debut.plusDays(7);
        return miseBaRepository.findSevragesAEffectuer(debut, fin)
                .stream().map(this::toResponse).toList();
    }

    // ── Utilitaires privés ────────────────────────────────────────────────────

    private void recalculerMoyennes(ProfilPorcin profil) {
        int nbPortees = profil.getNbPorteesTotal();
        if (nbPortees <= 0) return;

        // Moyenne nés vivants par portée
        profil.setMoyNesVivantsParPortee(
                BigDecimal.valueOf(profil.getNbPorceletsTotalNesVivants())
                        .divide(BigDecimal.valueOf(nbPortees), 2, RoundingMode.HALF_UP));

        // Les autres moyennes sont recalculées à partir des MiseBas réelles
        List<MiseBas> portees = miseBaRepository
                .findByTruieIdOrderByDateMiseBasReelleDesc(profil.getAnimal().getId());

        double moyPoids = portees.stream()
                .filter(m -> m.getPoidsMoyenSevrageKg() != null)
                .mapToDouble(m -> m.getPoidsMoyenSevrageKg().doubleValue())
                .average().orElse(0);
        if (moyPoids > 0) {
            profil.setMoyPoidsSevrageKg(
                    BigDecimal.valueOf(moyPoids).setScale(3, RoundingMode.HALF_UP));
        }

        double moyLactation = portees.stream()
                .filter(m -> m.getDureeLactationJours() != null)
                .mapToInt(MiseBas::getDureeLactationJours)
                .average().orElse(0);
        if (moyLactation > 0) {
            profil.setMoyDureeLactationJours(
                    BigDecimal.valueOf(moyLactation).setScale(1, RoundingMode.HALF_UP));
        }
    }

    public MiseBaResponse toResponse(MiseBas m) {
        return new MiseBaResponse(
                m.getId(),
                m.getSaillie() != null ? m.getSaillie().getId() : null,
                m.getTruie() != null ? m.getTruie().getId() : null,
                m.getTruie() != null ? m.getTruie().getCodeUnique() : null,
                m.getNumeroPortee(),
                m.getDateMiseBasReelle(),
                m.getDureeMiseBaMinutes(),
                m.getTypeMiseBas(),
                m.getNbNesVivants(),
                m.getNbMortNes(),
                m.getNbMomifies(),
                m.getPoidsMoyenNaissanceKg(),
                m.getPoidsMinNaissanceKg(),
                m.getPoidsMaxNaissanceKg(),
                m.getNbPorceletsAllaites(),
                m.getDateSevragePrevu(),
                m.getDateSevrageReel(),
                m.getNbSevres(),
                m.getPoidsMoyenSevrageKg(),
                m.getDureeLactationJours(),
                m.getBandeDestination() != null ? m.getBandeDestination().getId() : null,
                m.getBandeDestination() != null ? m.getBandeDestination().getNom() : null,
                m.getJoursdepuisPorteePrecedente(),
                m.getVeterinaireNom(),
                m.getOperateurNom(),
                m.getNotes(),
                m.getDateCreation()
        );
    }
}
