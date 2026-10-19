package com.reseau_partage.animaux.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.bovin.VeauNouveauNeRequest;
import com.reseau_partage.animaux.dto.bovin.VelageBovinRequest;
import com.reseau_partage.animaux.dto.bovin.VelageBovinResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.ProfilBovin;
import com.reseau_partage.core.entities.SaillieBovin;
import com.reseau_partage.core.entities.StatutReproductifBovin;
import com.reseau_partage.core.entities.StatutSaillie;
import com.reseau_partage.core.entities.VelageBovin;
import com.reseau_partage.core.repository.ProfilBovinRepository;
import com.reseau_partage.core.repository.SaillieBovinRepository;
import com.reseau_partage.core.repository.VelageBovinRepository;

@Service
public class VelageBovinService {

    private final SaillieBovinRepository saillieRepository;
    private final VelageBovinRepository velageRepository;
    private final ProfilBovinRepository profilRepository;
    private final BovinService bovinService;

    public VelageBovinService(SaillieBovinRepository saillieRepository,
            VelageBovinRepository velageRepository, ProfilBovinRepository profilRepository,
            BovinService bovinService) {
        this.saillieRepository = saillieRepository;
        this.velageRepository = velageRepository;
        this.profilRepository = profilRepository;
        this.bovinService = bovinService;
    }

    @Transactional
    public VelageBovinResponse create(VelageBovinRequest request) {
        SaillieBovin saillie = saillieRepository.findById(request.saillieId())
                .orElseThrow(() -> new ResourceNotFoundException("Saillie bovine", request.saillieId()));
        Animal vache = saillie.getVache();
        ProfilBovin profil = profilRepository.findByAnimalId(vache.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profil bovin", vache.getId()));
        if (saillie.getStatut() != StatutSaillie.CONFIRMEE) {
            throw new IllegalArgumentException("Le vêlage doit correspondre à une gestation confirmée.");
        }
        if (velageRepository.findBySaillieId(saillie.getId()).isPresent()) {
            throw new IllegalArgumentException("Un vêlage est déjà enregistré pour cette saillie.");
        }
        if (profil.getStatutReproductif() != StatutReproductifBovin.GESTATION
                && profil.getStatutReproductif() != StatutReproductifBovin.PRE_VELAGE
                && profil.getStatutReproductif() != StatutReproductifBovin.TARISSEMENT) {
            throw new IllegalArgumentException("La vache doit être en gestation, en pré-vêlage ou tarie.");
        }
        if (profil.getCategorieBovine() == CategorieBovine.GENISSE) {
            if (request.categoriePostVelage() != CategorieBovine.VACHE_LAITIERE
                    && request.categoriePostVelage() != CategorieBovine.VACHE_ALLAITANTE) {
                throw new IllegalArgumentException(
                        "Une génisse ayant vêlé doit être classée VACHE_LAITIERE ou VACHE_ALLAITANTE.");
            }
        } else if (request.categoriePostVelage() != null
                && request.categoriePostVelage() != profil.getCategorieBovine()) {
            throw new IllegalArgumentException("La catégorie post-vêlage ne correspond pas à celle de la vache.");
        }
        LocalDate dateVelage = request.dateVelageReel().toLocalDate();
        if (dateVelage.isBefore(saillie.getDateSaillie())
                || request.nbVeauxMortNes() == null || request.veauxVivants() == null
                || (request.veauxVivants().isEmpty() && request.nbVeauxMortNes() == 0)) {
            throw new IllegalArgumentException("Les résultats du vêlage sont invalides.");
        }

        List<Long> calfIds = request.veauxVivants().stream()
                .map(veau -> createCalf(vache, saillie, veau, dateVelage))
                .toList();

        VelageBovin velage = new VelageBovin();
        velage.setSaillie(saillie);
        velage.setVache(vache);
        velage.setNumeroVelage(profil.getNbLactationsTotal() + 1);
        velage.setDateVelageReel(request.dateVelageReel());
        velage.setTypeVelage(request.typeVelage());
        velage.setNbVeauxVivants(calfIds.size());
        velage.setNbVeauxMortNes(request.nbVeauxMortNes());
        velage.setRetentionPlacenta(Boolean.TRUE.equals(request.retentionPlacenta()));
        velage.setFievreDeLait(Boolean.TRUE.equals(request.fievreDeLait()));
        velage.setDateDebutLactation(dateVelage);
        velage.setVeterinaireNom(request.veterinaireNom());
        velage.setOperateurNom(request.operateurNom());
        velage.setNotes(request.notes());
        velage.setVeauIds(calfIds);
        if (!calfIds.isEmpty()) {
            velage.setVeauId(calfIds.get(0));
        }
        velage = velageRepository.save(velage);

        if (request.categoriePostVelage() != null) {
            profil.setCategorieBovine(request.categoriePostVelage());
        }
        profil.setStatutReproductif(profil.getCategorieBovine() == CategorieBovine.VACHE_LAITIERE
                ? StatutReproductifBovin.LACTATION : StatutReproductifBovin.POST_PARTUM);
        profil.setNumeroLactationActuelle(profil.getNumeroLactationActuelle() + 1);
        profil.setNbLactationsTotal(profil.getNbLactationsTotal() + 1);
        profil.setNbVeauxTotal(profil.getNbVeauxTotal() + calfIds.size());
        profil.setDateDebutLactationActuelle(dateVelage);
        profil.setProductionLaitCumulKg(java.math.BigDecimal.ZERO);
        profil.setDateVelagePrevu(null);
        profil.setDateTarissementPrevu(null);
        profil.setSaillieActive(null);
        return toResponse(velage);
    }

    @Transactional(readOnly = true)
    public List<VelageBovinResponse> historique(Long vacheId) {
        return velageRepository.findByVacheIdOrderByDateVelageReelDesc(vacheId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VelageBovinResponse get(Long id) {
        VelageBovin velage = velageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vêlage bovin", id));
        return toResponse(velage);
    }

    private Long createCalf(Animal mother, SaillieBovin saillie, VeauNouveauNeRequest calf, LocalDate date) {
        return bovinService.createNewborn(mother, saillie.getTaureau(), calf.sexe(),
                calf.poidsNaissanceKg(), date).getId();
    }

    private VelageBovinResponse toResponse(VelageBovin velage) {
        return new VelageBovinResponse(velage.getId(), velage.getVache().getId(),
                velage.getSaillie().getId(), velage.getNumeroVelage(), velage.getDateVelageReel(),
                velage.getTypeVelage(), velage.getNbVeauxVivants(), velage.getNbVeauxMortNes(),
                List.copyOf(velage.getVeauIds()), velage.getRetentionPlacenta(), velage.getFievreDeLait());
    }
}
