package com.reseau_partage.vaccination.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.core.entities.EtapePlanVaccination;
import com.reseau_partage.core.entities.PlanVaccination;
import com.reseau_partage.core.entities.Vaccin;
import com.reseau_partage.core.repository.PlanVaccinationRepository;
import com.reseau_partage.core.repository.VaccinRepository;
import com.reseau_partage.vaccination.dto.plan.EtapePlanRequest;
import com.reseau_partage.vaccination.dto.plan.PlanVaccinationRequest;
import com.reseau_partage.vaccination.dto.plan.PlanVaccinationResponse;
import com.reseau_partage.vaccination.exception.ResourceNotFoundException;

@Service
public class PlanVaccinationService {
    private final PlanVaccinationRepository planRepository;
    private final VaccinRepository vaccinRepository;

    public PlanVaccinationService(PlanVaccinationRepository planRepository, VaccinRepository vaccinRepository) {
        this.planRepository = planRepository;
        this.vaccinRepository = vaccinRepository;
    }

    @Transactional
    public PlanVaccinationResponse creer(PlanVaccinationRequest request) {
        PlanVaccination plan = new PlanVaccination();
        plan.setActif(true);
        appliquer(plan, request);
        return toResponse(planRepository.save(plan));
    }

    @Transactional(readOnly = true)
    public List<PlanVaccinationResponse> lister(Long fermeId) {
        return planRepository.findByFermeIdAndActifTrue(fermeId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PlanVaccinationResponse get(Long id) {
        return toResponse(planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PlanVaccination", id)));
    }

    @Transactional
    public PlanVaccinationResponse modifier(Long id, PlanVaccinationRequest request) {
        PlanVaccination plan = planRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PlanVaccination", id));
        appliquer(plan, request);
        return toResponse(planRepository.save(plan));
    }

    private void appliquer(PlanVaccination plan, PlanVaccinationRequest request) {
        plan.setNom(request.nom());
        plan.setFermeId(request.fermeId());
        plan.setEspece(request.espece());
        plan.setTypeProduction(request.typeProduction());
        plan.getEtapes().clear();
        for (EtapePlanRequest requestEtape : request.etapes()) {
            Vaccin vaccin = vaccinRepository.findById(requestEtape.vaccinId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vaccin", requestEtape.vaccinId()));
            if (!Boolean.TRUE.equals(vaccin.getActif())) {
                throw new IllegalArgumentException("Le vaccin " + vaccin.getId() + " est désactivé.");
            }
            if (vaccin.getEspecesCompatibles() != null && !vaccin.getEspecesCompatibles().isEmpty()
                    && !vaccin.getEspecesCompatibles().contains(request.espece())) {
                throw new IllegalArgumentException("Le vaccin " + vaccin.getNom() + " n'est pas compatible avec " + request.espece());
            }
            EtapePlanVaccination etape = new EtapePlanVaccination();
            etape.setPlan(plan);
            etape.setVaccin(vaccin);
            etape.setOrdreEtape(requestEtape.ordreEtape());
            etape.setAgeCibleJours(requestEtape.ageCibleJours());
            etape.setToleranceJours(requestEtape.toleranceJours() == null ? 3 : requestEtape.toleranceJours());
            etape.setDoseMl(requestEtape.doseMl() == null ? vaccin.getDoseMl() : requestEtape.doseMl());
            etape.setVoieAdministration(requestEtape.voieAdministration() == null ? vaccin.getVoieAdministration() : requestEtape.voieAdministration());
            etape.setInstructions(requestEtape.instructions());
            plan.getEtapes().add(etape);
        }
    }

    private PlanVaccinationResponse toResponse(PlanVaccination plan) {
        List<PlanVaccinationResponse.EtapeResponse> etapes = plan.getEtapes().stream()
                .sorted((a, b) -> Integer.compare(a.getOrdreEtape(), b.getOrdreEtape()))
                .map(etape -> new PlanVaccinationResponse.EtapeResponse(etape.getId(), etape.getVaccin().getId(),
                        etape.getVaccin().getNom(), etape.getOrdreEtape(), etape.getAgeCibleJours(),
                        etape.getToleranceJours(), etape.getDoseMl(), etape.getVoieAdministration(), etape.getInstructions()))
                .toList();
        return new PlanVaccinationResponse(plan.getId(), plan.getNom(), plan.getFermeId(), plan.getEspece(),
                plan.getTypeProduction(), plan.getActif(), etapes, plan.getDateCreation());
    }
}
