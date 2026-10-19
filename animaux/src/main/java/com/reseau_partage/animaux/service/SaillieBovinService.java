package com.reseau_partage.animaux.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.bovin.SaillieBovinConfirmationRequest;
import com.reseau_partage.animaux.dto.bovin.SaillieBovinRequest;
import com.reseau_partage.animaux.dto.bovin.SaillieBovinResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ProfilBovin;
import com.reseau_partage.core.entities.SaillieBovin;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutReproductifBovin;
import com.reseau_partage.core.entities.StatutSaillie;
import com.reseau_partage.core.entities.TypeSaillie;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.ProfilBovinRepository;
import com.reseau_partage.core.repository.SaillieBovinRepository;

@Service
public class SaillieBovinService {

    private static final int DUREE_GESTATION_JOURS = 280;
    private static final int DELAI_ECHO_MIN_JOURS = 35;
    private static final int DELAI_ECHO_MAX_JOURS = 45;
    private static final int DELAI_RETOUR_CHALEUR_JOURS = 21;

    private final AnimalRepository animalRepository;
    private final ProfilBovinRepository profilRepository;
    private final SaillieBovinRepository saillieRepository;

    public SaillieBovinService(AnimalRepository animalRepository,
            ProfilBovinRepository profilRepository, SaillieBovinRepository saillieRepository) {
        this.animalRepository = animalRepository;
        this.profilRepository = profilRepository;
        this.saillieRepository = saillieRepository;
    }

    @Transactional
    public SaillieBovinResponse create(SaillieBovinRequest request) {
        Animal vache = findBovin(request.vacheId());
        if (vache.getSexe() != Sexe.FEMELLE || vache.getStatut() != StatutAnimal.ACTIF) {
            throw new IllegalArgumentException("La saillie nécessite une femelle bovine active.");
        }
        ProfilBovin profil = findProfil(vache.getId());
        if (!List.of(StatutReproductifBovin.VIDE, StatutReproductifBovin.EN_CHALEUR,
                StatutReproductifBovin.POST_PARTUM, StatutReproductifBovin.LACTATION)
                .contains(profil.getStatutReproductif())) {
            throw new IllegalArgumentException("La vache doit être disponible, en post-partum ou en lactation pour être saillie.");
        }
        if (request.dateSaillie().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de saillie ne peut pas être dans le futur.");
        }

        Animal taureau = request.taureauId() == null ? null : findBovin(request.taureauId());
        if (request.typeSaillie() == TypeSaillie.NATURELLE && taureau == null) {
            throw new IllegalArgumentException("Une saillie naturelle doit préciser le taureau.");
        }
        if (taureau != null && (taureau.getSexe() != Sexe.MALE || taureau.getStatut() != StatutAnimal.ACTIF
                || findProfil(taureau.getId()).getCategorieBovine() != CategorieBovine.TAUREAU_REPRODUCTEUR)) {
            throw new IllegalArgumentException("Le père doit être un taureau reproducteur actif.");
        }

        SaillieBovin saillie = new SaillieBovin();
        saillie.setVache(vache);
        saillie.setTaureau(taureau);
        saillie.setTypeSaillie(request.typeSaillie());
        saillie.setDateSaillie(request.dateSaillie());
        saillie.setStatutReproductifAvantSaillie(profil.getStatutReproductif());
        saillie.setSemenceRace(request.semenceRace());
        saillie.setSemenceReference(request.semenceReference());
        saillie.setOperateurNom(request.operateurNom());
        saillie.setNotes(request.notes());
        LocalDate velage = request.dateSaillie().plusDays(DUREE_GESTATION_JOURS);
        saillie.setDateVelagePrevu(velage);
        saillie.setDatePreVelagePrevue(velage.minusDays(21));
        saillie.setDateTarissementPrevue(velage.minusDays(60));
        saillie = saillieRepository.save(saillie);

        profil.setSaillieActive(saillie);
        profil.setDateVelagePrevu(saillie.getDateVelagePrevu());
        profil.setDateTarissementPrevu(saillie.getDateTarissementPrevue());
        profil.setDateProchaineSailliePrevue(null);
        profil.setStatutReproductif(StatutReproductifBovin.SAILLIE);
        return toResponse(saillie);
    }

    @Transactional
    public SaillieBovinResponse confirmer(Long saillieId, SaillieBovinConfirmationRequest request) {
        SaillieBovin saillie = findSaillie(saillieId);
        if (saillie.getStatut() != StatutSaillie.EN_ATTENTE) {
            throw new IllegalArgumentException("Seule une saillie en attente peut être confirmée.");
        }
        LocalDate minEcho = saillie.getDateSaillie().plusDays(DELAI_ECHO_MIN_JOURS);
        LocalDate maxEcho = saillie.getDateSaillie().plusDays(DELAI_ECHO_MAX_JOURS);
        if (request.dateConfirmationEcho().isBefore(minEcho) || request.dateConfirmationEcho().isAfter(maxEcho)) {
            throw new IllegalArgumentException("L'échographie de confirmation doit être réalisée entre J+35 et J+45.");
        }
        ProfilBovin profil = findProfil(saillie.getVache().getId());
        saillie.setDateConfirmationEcho(request.dateConfirmationEcho());
        if (request.confirmee()) {
            saillie.setStatut(StatutSaillie.CONFIRMEE);
            profil.setStatutReproductif(StatutReproductifBovin.GESTATION);
            profil.setDateVelagePrevu(saillie.getDateVelagePrevu());
            profil.setDateTarissementPrevu(saillie.getDateTarissementPrevue());
        } else {
            saillie.setStatut(StatutSaillie.ECHEC);
            saillie.setMotifEchec(request.motifEchec());
            profil.setStatutReproductif(saillie.getStatutReproductifAvantSaillie()
                    == StatutReproductifBovin.EN_CHALEUR
                            ? StatutReproductifBovin.VIDE : saillie.getStatutReproductifAvantSaillie());
            profil.setSaillieActive(null);
            profil.setDateVelagePrevu(null);
            profil.setDateTarissementPrevu(null);
            profil.setDateProchaineSailliePrevue(request.dateConfirmationEcho().plusDays(DELAI_RETOUR_CHALEUR_JOURS));
        }
        return toResponse(saillie);
    }

    @Transactional
    public SaillieBovinResponse avorter(Long saillieId, String motif) {
        SaillieBovin saillie = findSaillie(saillieId);
        if (saillie.getStatut() != StatutSaillie.CONFIRMEE) {
            throw new IllegalArgumentException("Seule une gestation confirmée peut être déclarée avortée.");
        }
        saillie.setStatut(StatutSaillie.AVORTEMENT);
        saillie.setMotifEchec(motif);
        ProfilBovin profil = findProfil(saillie.getVache().getId());
        profil.setStatutReproductif(saillie.getStatutReproductifAvantSaillie()
                == StatutReproductifBovin.EN_CHALEUR
                        ? StatutReproductifBovin.VIDE : saillie.getStatutReproductifAvantSaillie());
        profil.setSaillieActive(null);
        profil.setDateVelagePrevu(null);
        profil.setDateTarissementPrevu(null);
        profil.setDateProchaineSailliePrevue(LocalDate.now().plusDays(DELAI_RETOUR_CHALEUR_JOURS));
        return toResponse(saillie);
    }

    @Transactional(readOnly = true)
    public List<SaillieBovinResponse> historique(Long vacheId) {
        findProfil(vacheId);
        return saillieRepository.findByVacheIdOrderByDateSaillieDesc(vacheId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SaillieBovinResponse> echoesEnRetard() {
        return saillieRepository.findByStatutAndDateSaillieLessThanEqual(StatutSaillie.EN_ATTENTE,
                        LocalDate.now().minusDays(DELAI_ECHO_MIN_JOURS))
                .stream().map(this::toResponse).toList();
    }

    private Animal findBovin(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal", id));
        if (animal.getEspece() != Espece.BOVIN) {
            throw new IllegalArgumentException("L'animal " + id + " n'est pas un bovin.");
        }
        return animal;
    }

    private ProfilBovin findProfil(Long id) {
        findBovin(id);
        return profilRepository.findByAnimalId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profil bovin", id));
    }

    private SaillieBovin findSaillie(Long id) {
        return saillieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Saillie bovine", id));
    }

    private SaillieBovinResponse toResponse(SaillieBovin saillie) {
        return new SaillieBovinResponse(saillie.getId(), saillie.getVache().getId(),
                saillie.getTaureau() == null ? null : saillie.getTaureau().getId(),
                saillie.getTypeSaillie(), saillie.getDateSaillie(), saillie.getStatut(),
                saillie.getDateConfirmationEcho(), saillie.getDateVelagePrevu(),
                saillie.getDatePreVelagePrevue(), saillie.getDateTarissementPrevue(), saillie.getMotifEchec());
    }
}
