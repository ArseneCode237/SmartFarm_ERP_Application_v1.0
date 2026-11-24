package com.reseau_partage.animaux.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.bande.BandeRequest;
import com.reseau_partage.animaux.dto.bande.BandeResponse;
import com.reseau_partage.animaux.dto.bovin.BandeBovinRequest;
import com.reseau_partage.animaux.dto.bovin.BandeBovinResponse;
import com.reseau_partage.animaux.dto.bovin.ProfilBandeBovinRequest;
import com.reseau_partage.animaux.dto.bovin.ProfilBandeBovinResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ProfilBandeBovin;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.ProfilBandeBovinRepository;

/**
 * Gestion du mode "bande bovine" (troupeau d'engraissement / allaitant / laitier collectif).
 * Réutilise {@link BandeService} pour la bande générique et n'ajoute que le profil bovin.
 */
@Service
public class BandeBovinService {

    private final BandeService bandeService;
    private final BandeRepository bandeRepository;
    private final ProfilBandeBovinRepository profilRepository;

    public BandeBovinService(BandeService bandeService, BandeRepository bandeRepository,
            ProfilBandeBovinRepository profilRepository) {
        this.bandeService = bandeService;
        this.bandeRepository = bandeRepository;
        this.profilRepository = profilRepository;
    }

    /** Crée la bande bovine (espèce BOVIN) et son profil bovin de façon atomique. */
    @Transactional
    public BandeBovinResponse creerBandeBovine(BandeBovinRequest request) {
        BandeRequest bandeRequest = request.bande();
        if (bandeRequest.espece() != Espece.BOVIN) {
            throw new IllegalArgumentException("Une bande bovine doit avoir l'espèce BOVIN.");
        }

        BandeResponse bande = bandeService.create(bandeRequest);
        Bande entity = bandeRepository.findById(bande.id())
                .orElseThrow(() -> new ResourceNotFoundException("Bande", bande.id()));

        ProfilBandeBovin profil = new ProfilBandeBovin();
        profil.setBande(entity);
        appliquer(profil, request.profil());
        profilRepository.save(profil);

        return new BandeBovinResponse(bandeService.get(entity.getId()), toResponse(profil));
    }

    @Transactional(readOnly = true)
    public BandeBovinResponse getProfil(Long bandeId) {
        ProfilBandeBovin profil = profilRepository.findByBandeId(bandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil bande bovin", bandeId));
        return new BandeBovinResponse(bandeService.get(bandeId), toResponse(profil));
    }

    /** Toutes les bandes bovines d'une ferme. */
    @Transactional(readOnly = true)
    public List<BandeBovinResponse> parFerme(Long fermeId) {
        return profilRepository.findByFermeId(fermeId).stream()
                .map(p -> new BandeBovinResponse(bandeService.get(p.getBande().getId()), toResponse(p)))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean existeProfilBande(Long bandeId) {
        return profilRepository.findByBandeId(bandeId).isPresent();
    }

    /** Vérifie qu'une bande existe et est bien bovine (garde-fou des opérations bovines). */
    @Transactional(readOnly = true)
    public void verifierBandeBovine(Long bandeId) {
        Bande bande = bandeRepository.findById(bandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bande", bandeId));
        if (bande.getEspece() != Espece.BOVIN) {
            throw new IllegalArgumentException("La bande " + bandeId + " n'est pas une bande bovine.");
        }
    }

    private void appliquer(ProfilBandeBovin profil, ProfilBandeBovinRequest req) {
        if (req == null) {
            return;
        }
        profil.setTypeProductionBovin(req.typeProductionBovin());
        profil.setRace(req.race());
        profil.setPoidsCibleSortieKg(req.poidsCibleSortieKg());
        profil.setAgeCibleAbattageMois(req.ageCibleAbattageMois());
        profil.setGmqCibleG(req.gmqCibleG());
        profil.setFcrCible(req.fcrCible());
        profil.setDureeEngraissementJours(req.dureeEngraissementJours());
        profil.setDateAbattagePrevue(req.dateAbattagePrevue());
        profil.setRendementCarcassePct(req.rendementCarcassePct());
    }

    private ProfilBandeBovinResponse toResponse(ProfilBandeBovin p) {
        return new ProfilBandeBovinResponse(
                p.getBande() == null ? null : p.getBande().getId(),
                p.getTypeProductionBovin(), p.getRace(), p.getPoidsCibleSortieKg(),
                p.getAgeCibleAbattageMois(), p.getGmqCibleG(), p.getFcrCible(),
                p.getDureeEngraissementJours(), p.getAlimentConsommeTotalKg(),
                p.getDateAbattagePrevue(), p.getDateAbattageReelle(), p.getRendementCarcassePct(),
                p.getDateCreation(), p.getDateModification());
    }
}
