package com.reseau_partage.animaux.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.alerte.AlerteResponse;
import com.reseau_partage.animaux.dto.pesee.PeseeResponse;
import com.reseau_partage.animaux.dto.reproduction.EvenementReproductionResponse;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.ConfigEspece;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.ConfigEspeceRepository;
import com.reseau_partage.core.repository.MouvementAnimalRepository;

@Service
public class AlerteService {

    private final PeseeService peseeService;
    private final ReproductionService reproductionService;
    private final BandeRepository bandeRepository;
    private final ConfigEspeceRepository configEspeceRepository;
    private final MouvementAnimalRepository mouvementRepository;

    public AlerteService(PeseeService peseeService, ReproductionService reproductionService,
                         BandeRepository bandeRepository, ConfigEspeceRepository configEspeceRepository,
                         MouvementAnimalRepository mouvementRepository) {
        this.peseeService = peseeService;
        this.reproductionService = reproductionService;
        this.bandeRepository = bandeRepository;
        this.configEspeceRepository = configEspeceRepository;
        this.mouvementRepository = mouvementRepository;
    }

    @Transactional(readOnly = true)
    public List<AlerteResponse> alertesFerme(Long fermeId, int joursHorizon) {
        List<AlerteResponse> alertes = new ArrayList<>();

        List<PeseeResponse> sousPerformeurs = peseeService.sousPerformeurs(fermeId);
        for (PeseeResponse p : sousPerformeurs) {
            alertes.add(new AlerteResponse("SOUS_PERFORMANCE", "MOYEN",
                    "Animal sous-performant (ecart " + p.ecartCourbeReferencePct() + "%)", p.animalId(), null));
        }

        List<EvenementReproductionResponse> gestations = reproductionService.alertes(joursHorizon);
        for (EvenementReproductionResponse e : gestations) {
            alertes.add(new AlerteResponse("GESTATION_TERME", "ELEVE",
                    "Mise-bas prevue pour la femelle " + e.femelleCode(), e.id(), e.dateMiseBasPrevue()));
        }

        List<Bande> bandes = bandeRepository.findAll().stream()
                .filter(b -> b.getStructure() != null && b.getStructure().getSite() != null
                        && b.getStructure().getSite().getFerme() != null
                        && b.getStructure().getSite().getFerme().getId().equals(fermeId))
                .toList();
        for (Bande b : bandes) {
            if (tauxMortaliteDepasse(b)) {
                alertes.add(new AlerteResponse("MORTALITE", "ELEVE",
                        "Taux de mortalite depasse le seuil pour la bande " + b.getNom(), b.getId(), null));
            }
        }
        return alertes;
    }

    private boolean tauxMortaliteDepasse(Bande bande) {
        if (bande.getEffectifInitial() == null || bande.getEffectifInitial() == 0
                || bande.getEffectifMorts() == null) {
            return false;
        }
        ConfigEspece config = configEspeceRepository.findByEspece(bande.getEspece()).orElse(null);
        if (config == null || config.getSeuilAlerteMortalitePct() == null) {
            return false;
        }
        BigDecimal taux = BigDecimal.valueOf(bande.getEffectifMorts())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(bande.getEffectifInitial()), 2, RoundingMode.HALF_UP);
        return taux.compareTo(BigDecimal.valueOf(config.getSeuilAlerteMortalitePct())) > 0;
    }
}
