package com.reseau_partage.animaux.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.dto.config.ConfigEspeceRequest;
import com.reseau_partage.animaux.dto.config.ConfigEspeceResponse;
import com.reseau_partage.animaux.dto.config.CourbeCroissanceRequest;
import com.reseau_partage.animaux.dto.config.TypeEvenementCustomRequest;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.ConfigEspece;
import com.reseau_partage.core.entities.CourbeCroissanceReference;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.TypeEvenementCustom;
import com.reseau_partage.core.repository.ConfigEspeceRepository;
import com.reseau_partage.core.repository.CourbeCroissanceReferenceRepository;
import com.reseau_partage.core.repository.TypeEvenementCustomRepository;

@Service
public class ConfigEspeceService {

    private final ConfigEspeceRepository configEspeceRepository;
    private final CourbeCroissanceReferenceRepository courbeRepository;
    private final TypeEvenementCustomRepository evenementCustomRepository;

    public ConfigEspeceService(ConfigEspeceRepository configEspeceRepository,
                               CourbeCroissanceReferenceRepository courbeRepository,
                               TypeEvenementCustomRepository evenementCustomRepository) {
        this.configEspeceRepository = configEspeceRepository;
        this.courbeRepository = courbeRepository;
        this.evenementCustomRepository = evenementCustomRepository;
    }

    @Transactional(readOnly = true)
    public List<ConfigEspeceResponse> listerConfigs() {
        return configEspeceRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ConfigEspeceResponse getConfig(Espece espece) {
        ConfigEspece config = configEspeceRepository.findByEspece(espece)
                .orElseThrow(() -> new ResourceNotFoundException("ConfigEspece", espece));
        return toResponse(config);
    }

    @Transactional
    public ConfigEspeceResponse modifierConfig(Espece espece, ConfigEspeceRequest request) {
        ConfigEspece config = configEspeceRepository.findByEspece(espece).orElse(null);
        if (config == null) {
            config = new ConfigEspece();
            config.setEspece(espece);
        }
        config.setDureeGestationJours(request.dureeGestationJours());
        config.setAgeMaturiteSexuelleJours(request.ageMaturiteSexuelleJours());
        config.setIntervalleEntreGestationsJours(request.intervalleEntreGestationsJours());
        config.setDureeSevrageJours(request.dureeSevrageJours());
        config.setTaillePorteeMoyenne(request.taillePorteeMoyenne());
        config.setPoidsNaissanceMoyenKg(request.poidsNaissanceMoyenKg());
        config.setPoidsAbattageCibleKg(request.poidsAbattageCibleKg());
        config.setAgeCibleAbattageJours(request.ageCibleAbattageJours());
        config.setFcrCibleMoyen(request.fcrCibleMoyen());
        config.setGmqCibleG(request.gmqCibleG());
        config.setIntervalleVaccinationJours(request.intervalleVaccinationJours());
        config.setDureeQuarantaineJours(request.dureeQuarantaineJours());
        config.setSeuilAlerteMortalitePct(request.seuilAlerteMortalitePct());
        config.setTauxPonteCiblePct(request.tauxPonteCiblePct());
        config.setDureeProductionLaitJours(request.dureeProductionLaitJours());
        config.setProductionLaitJournaliereLitres(request.productionLaitJournaliereLitres());
        config = configEspeceRepository.save(config);
        return toResponse(config);
    }

    @Transactional(readOnly = true)
    public List<CourbeCroissanceReference> courbe(Espece espece) {
        return courbeRepository.findByConfigEspeceEspeceOrderByAgeJoursAsc(espece);
    }

    @Transactional
    public List<CourbeCroissanceReference> ajouterPointsCourbe(List<CourbeCroissanceRequest> requests) {
        List<CourbeCroissanceReference> aSauver = new java.util.ArrayList<>();
        for (CourbeCroissanceRequest req : requests) {
            ConfigEspece config = configEspeceRepository.findById(req.configEspeceId())
                    .orElseThrow(() -> new ResourceNotFoundException("ConfigEspece", req.configEspeceId()));
            CourbeCroissanceReference point = new CourbeCroissanceReference();
            point.setConfigEspece(config);
            point.setRace(req.race());
            point.setAgeJours(req.ageJours());
            point.setPoidsCibleKg(req.poidsCibleKg());
            point.setPoidsMiniKg(req.poidsMiniKg());
            point.setPoidsMaxiKg(req.poidsMaxiKg());
            aSauver.add(point);
        }
        return courbeRepository.saveAll(aSauver);
    }

    @Transactional(readOnly = true)
    public List<TypeEvenementCustom> evenements(Espece espece) {
        return evenementCustomRepository.findByEspeceAndActifTrue(espece);
    }

    @Transactional
    public TypeEvenementCustom creerEvenement(TypeEvenementCustomRequest request) {
        TypeEvenementCustom evenement = new TypeEvenementCustom();
        evenement.setEspece(request.espece());
        evenement.setLibelle(request.libelle());
        evenement.setDescription(request.description());
        evenement.setActif(request.actif() != null ? request.actif() : true);
        return evenementCustomRepository.save(evenement);
    }

    private ConfigEspeceResponse toResponse(ConfigEspece c) {
        return new ConfigEspeceResponse(
                c.getId(), c.getEspece(),
                c.getDureeGestationJours(), c.getAgeMaturiteSexuelleJours(),
                c.getIntervalleEntreGestationsJours(), c.getDureeSevrageJours(),
                c.getTaillePorteeMoyenne(), c.getPoidsNaissanceMoyenKg(),
                c.getPoidsAbattageCibleKg(), c.getAgeCibleAbattageJours(),
                c.getFcrCibleMoyen(), c.getGmqCibleG(),
                c.getIntervalleVaccinationJours(), c.getDureeQuarantaineJours(),
                c.getSeuilAlerteMortalitePct(), c.getTauxPonteCiblePct(),
                c.getDureeProductionLaitJours(), c.getProductionLaitJournaliereLitres());
    }
}
