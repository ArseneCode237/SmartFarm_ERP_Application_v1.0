package com.reseau_partage.vaccination.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.core.entities.EtapePlanVaccination;
import com.reseau_partage.core.entities.PlanVaccination;
import com.reseau_partage.core.entities.Vaccin;
import com.reseau_partage.core.entities.VaccinationBande;
import com.reseau_partage.core.entities.enumtypes.StatutVaccination;
import com.reseau_partage.core.entities.enumtypes.TypeVaccination;
import com.reseau_partage.core.repository.PlanVaccinationRepository;
import com.reseau_partage.core.repository.VaccinRepository;
import com.reseau_partage.core.repository.VaccinationBandeRepository;
import com.reseau_partage.vaccination.client.AnimauxClient;
import com.reseau_partage.vaccination.client.NotificationClient;
import com.reseau_partage.vaccination.dto.bande.VaccinationBandeRequest;
import com.reseau_partage.vaccination.dto.bande.VaccinationBandeResponse;
import com.reseau_partage.vaccination.exception.ResourceNotFoundException;

@Service
public class VaccinationBandeService {
    private final VaccinationBandeRepository repository;
    private final VaccinRepository vaccinRepository;
    private final PlanVaccinationRepository planRepository;
    private final AnimauxClient animauxClient;
    private final NotificationClient notificationClient;

    public VaccinationBandeService(VaccinationBandeRepository repository, VaccinRepository vaccinRepository,
            PlanVaccinationRepository planRepository, AnimauxClient animauxClient, NotificationClient notificationClient) {
        this.repository = repository;
        this.vaccinRepository = vaccinRepository;
        this.planRepository = planRepository;
        this.animauxClient = animauxClient;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public VaccinationBandeResponse enregistrer(VaccinationBandeRequest request) {
        Map<String, Object> bande = animauxClient.getBande(request.bandeId());
        Vaccin vaccin = vaccin(request.vaccinId());
        EspeceData espece = espece(bande);
        verifierCompatibilite(vaccin, espece.value());
        LocalDate date = request.dateVaccination() == null ? LocalDate.now() : request.dateVaccination();
        BigDecimal dose = request.doseMlParTete() == null ? vaccin.getDoseMl() : request.doseMlParTete();
        VaccinationBande entity = new VaccinationBande();
        entity.setBandeId(request.bandeId()); entity.setBandeNom(text(bande, "nom")); entity.setEspece(espece.value());
        entity.setFermeId(request.fermeId()); entity.setVaccin(vaccin); entity.setNumeroLotVaccin(request.numeroLotVaccin());
        entity.setDateExpirationLot(request.dateExpirationLot()); entity.setTypeVaccination(request.typeVaccination() == null ? TypeVaccination.PREVENTIVE : request.typeVaccination());
        entity.setNumeroDoseDansProtocole(request.numeroDoseDansProtocole()); entity.setDateVaccination(date);
        entity.setAgeBandeJoursAuMoment(age(dateEntree(bande), date)); entity.setVoieAdministration(request.voieAdministration() == null ? vaccin.getVoieAdministration() : request.voieAdministration());
        entity.setDoseMlParTete(dose); entity.setNbAnimauxVaccines(request.nbAnimauxVaccines());
        entity.setDoseTotaleMl(dose == null ? null : dose.multiply(BigDecimal.valueOf(request.nbAnimauxVaccines())));
        entity.setStatut(StatutVaccination.EFFECTUEE); entity.setDateProchaineRappel(rappel(date, vaccin));
        entity.setDateFinDelaiAttente(delai(date, vaccin)); entity.setVeterinaireNom(request.veterinaireNom());
        entity.setOperateurNom(request.operateurNom()); entity.setNotes(request.notes());
        VaccinationBande saved = repository.save(entity);
        if (saved.getDateProchaineRappel() != null) notificationClient.sendAlert("RAPPEL_VACCIN_BANDE", "HAUTE", "Rappel de vaccination de la bande " + saved.getBandeNom(), saved.getBandeId());
        return response(saved);
    }

    @Transactional
    public List<VaccinationBandeResponse> appliquerPlan(Long bandeId, Long planId) {
        Map<String, Object> bande = animauxClient.getBande(bandeId);
        PlanVaccination plan = planRepository.findById(planId).orElseThrow(() -> new ResourceNotFoundException("PlanVaccination", planId));
        if (!plan.getEspece().equals(espece(bande).value())) throw new IllegalArgumentException("Le plan n'est pas compatible avec l'espèce de la bande.");
        LocalDate entree = dateEntree(bande);
        return plan.getEtapes().stream().map(etape -> planifier(bandeId, bande, plan, etape, entree)).map(repository::save).map(this::response).toList();
    }

    private VaccinationBande planifier(Long bandeId, Map<String, Object> bande, PlanVaccination plan, EtapePlanVaccination etape, LocalDate entree) {
        VaccinationBande entity = new VaccinationBande();
        entity.setBandeId(bandeId); entity.setBandeNom(text(bande, "nom")); entity.setEspece(plan.getEspece()); entity.setFermeId(plan.getFermeId());
        entity.setVaccin(etape.getVaccin()); entity.setDateVaccination(entree.plusDays(etape.getAgeCibleJours())); entity.setAgeBandeJoursAuMoment(etape.getAgeCibleJours());
        entity.setVoieAdministration(etape.getVoieAdministration()); entity.setDoseMlParTete(etape.getDoseMl()); entity.setStatut(StatutVaccination.PLANIFIEE);
        entity.setPlanVaccinationId(plan.getId()); entity.setEtapePlanId(etape.getId()); entity.setNbAnimauxVaccines(intValue(bande, "effectifActuel"));
        return entity;
    }

    @Transactional(readOnly = true) public List<VaccinationBandeResponse> historique(Long bandeId) { return repository.findByBandeIdOrderByDateVaccinationDesc(bandeId).stream().map(this::response).toList(); }
    @Transactional(readOnly = true) public Map<String, Object> statut(Long bandeId) { List<VaccinationBandeResponse> historique = historique(bandeId); return Map.of("bandeId", bandeId, "nombreVaccinations", historique.size(), "dernieresVaccinations", historique); }
    @Transactional(readOnly = true) public List<VaccinationBandeResponse> rappels(Long fermeId, int horizon) { return repository.findRappelsAVenir(fermeId, LocalDate.now(), LocalDate.now().plusDays(horizon)).stream().map(this::response).toList(); }
    @Transactional(readOnly = true) public List<VaccinationBandeResponse> planifiees(Long fermeId) { return repository.findByFermeIdAndStatut(fermeId, StatutVaccination.PLANIFIEE).stream().map(this::response).toList(); }
    @Transactional(readOnly = true) public List<VaccinationBandeResponse> toutesParFerme(Long fermeId) { return repository.findByFermeIdOrderByDateVaccinationDesc(fermeId).stream().map(this::response).toList(); }
    @Transactional(readOnly = true) public List<VaccinationBandeResponse> delaiAttente(Long bandeId) { return repository.findDelaisAttenteActifs(bandeId, LocalDate.now()).stream().map(this::response).toList(); }
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void notifierPlanificationsEnRetard() {
        repository.findByStatutAndDateVaccinationBefore(StatutVaccination.PLANIFIEE, LocalDate.now()).forEach(value -> {
            value.setStatut(StatutVaccination.RETARDEE);
            repository.save(value);
            notificationClient.sendAlert("VACCINATION_PLANIFIEE_EN_RETARD", "HAUTE", "Vaccination planifiée en retard pour la bande " + value.getBandeNom(), value.getBandeId());
        });
    }
    @Transactional public VaccinationBandeResponse annuler(Long id) { VaccinationBande value = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("VaccinationBande", id)); if (value.getStatut() != StatutVaccination.PLANIFIEE) throw new IllegalStateException("Seule une vaccination planifiée peut être annulée."); value.setStatut(StatutVaccination.ANNULEE); return response(repository.save(value)); }

    private Vaccin vaccin(Long id) { return vaccinRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vaccin", id)); }
    private void verifierCompatibilite(Vaccin vaccin, com.reseau_partage.core.entities.Espece espece) { if (!Boolean.TRUE.equals(vaccin.getActif())) throw new IllegalArgumentException("Le vaccin est désactivé."); if (vaccin.getEspecesCompatibles() != null && !vaccin.getEspecesCompatibles().isEmpty() && !vaccin.getEspecesCompatibles().contains(espece)) throw new IllegalArgumentException("Le vaccin n'est pas compatible avec " + espece); }
    private LocalDate rappel(LocalDate date, Vaccin vaccin) { return vaccin.getIntervalleRappelJours() == null ? null : date.plusDays(vaccin.getIntervalleRappelJours()); }
    private LocalDate delai(LocalDate date, Vaccin vaccin) { return vaccin.getDelaiAttenteAbattageJours() == null ? null : date.plusDays(vaccin.getDelaiAttenteAbattageJours()); }
    private LocalDate dateEntree(Map<String, Object> data) { Object value = data.get("dateEntree"); return value == null ? LocalDate.now() : LocalDate.parse(value.toString()); }
    private int age(LocalDate naissance, LocalDate date) { return (int) ChronoUnit.DAYS.between(naissance, date); }
    private String text(Map<String, Object> data, String key) { return data.get(key) == null ? null : data.get(key).toString(); }
    private Long longValue(Map<String, Object> data, String key) { return data.get(key) == null ? null : Long.valueOf(data.get(key).toString()); }
    private int intValue(Map<String, Object> data, String key) { return data.get(key) == null ? 0 : Integer.parseInt(data.get(key).toString()); }
    private EspeceData espece(Map<String, Object> data) { return new EspeceData(com.reseau_partage.core.entities.Espece.valueOf(text(data, "espece"))); }
    private record EspeceData(com.reseau_partage.core.entities.Espece value) { }
    private VaccinationBandeResponse response(VaccinationBande v) { return new VaccinationBandeResponse(v.getId(), v.getBandeId(), v.getBandeNom(), v.getEspece(), v.getFermeId(), v.getVaccin().getId(), v.getVaccin().getNom(), v.getNumeroLotVaccin(), v.getDateExpirationLot(), v.getTypeVaccination(), v.getNumeroDoseDansProtocole(), v.getDateVaccination(), v.getAgeBandeJoursAuMoment(), v.getVoieAdministration(), v.getDoseMlParTete(), v.getNbAnimauxVaccines(), v.getDoseTotaleMl(), v.getStatut(), v.getDateProchaineRappel(), v.getPlanVaccinationId(), v.getEtapePlanId(), v.getDateFinDelaiAttente(), v.getVeterinaireNom(), v.getOperateurNom(), v.getNotes(), v.getDateCreation()); }
}
