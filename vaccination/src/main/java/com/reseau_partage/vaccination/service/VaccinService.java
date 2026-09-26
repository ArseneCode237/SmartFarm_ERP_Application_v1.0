package com.reseau_partage.vaccination.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.Vaccin;
import com.reseau_partage.core.repository.VaccinRepository;
import com.reseau_partage.vaccination.dto.vaccin.VaccinRequest;
import com.reseau_partage.vaccination.dto.vaccin.VaccinResponse;
import com.reseau_partage.vaccination.exception.ResourceNotFoundException;

@Service
public class VaccinService {
    private final VaccinRepository repository;

    public VaccinService(VaccinRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public VaccinResponse creer(VaccinRequest request) {
        Vaccin vaccin = new Vaccin();
        appliquer(vaccin, request);
        return toResponse(repository.save(vaccin));
    }

    @Transactional(readOnly = true)
    public List<VaccinResponse> listerActifs() {
        return repository.findByActifTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<VaccinResponse> listerParEspece(Espece espece) {
        return repository.findByEspecesCompatiblesContaining(espece).stream()
                .filter(vaccin -> Boolean.TRUE.equals(vaccin.getActif()))
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VaccinResponse get(Long id) {
        return toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vaccin", id)));
    }

    @Transactional
    public VaccinResponse modifier(Long id, VaccinRequest request) {
        Vaccin vaccin = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vaccin", id));
        appliquer(vaccin, request);
        return toResponse(repository.save(vaccin));
    }

    @Transactional
    public VaccinResponse desactiver(Long id) {
        Vaccin vaccin = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vaccin", id));
        vaccin.setActif(false);
        return toResponse(repository.save(vaccin));
    }

    private void appliquer(Vaccin vaccin, VaccinRequest request) {
        vaccin.setNom(request.nom());
        vaccin.setFabricant(request.fabricant());
        vaccin.setNumeroAmm(request.numeroAmm());
        vaccin.setTypeVaccin(request.typeVaccin());
        vaccin.setEspecesCompatibles(request.especesCompatibles());
        vaccin.setMaladiesCiblees(request.maladiesCiblees());
        vaccin.setVoieAdministration(request.voieAdministration());
        vaccin.setDoseMl(request.doseMl());
        vaccin.setAgePremiereDoseJours(request.agePremiereDoseJours());
        vaccin.setIntervalleRappelJours(request.intervalleRappelJours());
        vaccin.setNombreDosesProtocole(request.nombreDosesProtocole());
        vaccin.setDelaiAttenteAbattageJours(request.delaiAttenteAbattageJours());
        vaccin.setTemperatureConservationMin(request.temperatureConservationMin());
        vaccin.setTemperatureConservationMax(request.temperatureConservationMax());
        vaccin.setDureeValiditeApresOuvertureHeures(request.dureeValiditeApresOuvertureHeures());
        vaccin.setNotes(request.notes());
        if (vaccin.getActif() == null) vaccin.setActif(true);
    }

    private VaccinResponse toResponse(Vaccin vaccin) {
        return new VaccinResponse(vaccin.getId(), vaccin.getNom(), vaccin.getFabricant(), vaccin.getNumeroAmm(),
                vaccin.getTypeVaccin(), vaccin.getEspecesCompatibles(), vaccin.getMaladiesCiblees(),
                vaccin.getVoieAdministration(), vaccin.getDoseMl(), vaccin.getAgePremiereDoseJours(),
                vaccin.getIntervalleRappelJours(), vaccin.getNombreDosesProtocole(), vaccin.getDelaiAttenteAbattageJours(),
                vaccin.getTemperatureConservationMin(), vaccin.getTemperatureConservationMax(),
                vaccin.getDureeValiditeApresOuvertureHeures(), vaccin.getActif(), vaccin.getNotes(), vaccin.getDateCreation());
    }
}
