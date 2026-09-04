package com.reseau_partage.vaccination.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.core.entities.Vaccin;
import com.reseau_partage.core.entities.VaccinationIndividuelle;
import com.reseau_partage.core.repository.VaccinRepository;
import com.reseau_partage.core.repository.VaccinationIndividuelleRepository;
import com.reseau_partage.vaccination.client.AnimauxClient;
import com.reseau_partage.vaccination.client.NotificationClient;
import com.reseau_partage.vaccination.dto.individu.VaccinationIndividuelleRequest;
import com.reseau_partage.vaccination.dto.individu.VaccinationIndividuelleResponse;
import com.reseau_partage.vaccination.exception.ResourceNotFoundException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
public class VaccinationIndividuelleService {
    private final VaccinationIndividuelleRepository repository;
    private final VaccinRepository vaccinRepository;
    private final AnimauxClient animauxClient;
    private final NotificationClient notificationClient;

    public VaccinationIndividuelleService(VaccinationIndividuelleRepository repository, VaccinRepository vaccinRepository,
            AnimauxClient animauxClient, NotificationClient notificationClient) {
        this.repository = repository; this.vaccinRepository = vaccinRepository; this.animauxClient = animauxClient; this.notificationClient = notificationClient;
    }

    @Transactional
    public VaccinationIndividuelleResponse enregistrer(VaccinationIndividuelleRequest request) {
        Map<String, Object> animal = animauxClient.getAnimal(request.animalId());
        Vaccin vaccin = vaccinRepository.findById(request.vaccinId()).orElseThrow(() -> new ResourceNotFoundException("Vaccin", request.vaccinId()));
        String especeText = text(animal, "espece");
        if (!Boolean.TRUE.equals(vaccin.getActif())) throw new IllegalArgumentException("Le vaccin est désactivé.");
        if (vaccin.getEspecesCompatibles() != null && !vaccin.getEspecesCompatibles().isEmpty() && !vaccin.getEspecesCompatibles().stream().anyMatch(value -> value.name().equals(especeText))) throw new IllegalArgumentException("Le vaccin n'est pas compatible avec " + especeText);
        LocalDate date = request.dateVaccination() == null ? LocalDate.now() : request.dateVaccination();
        BigDecimal dose = request.doseMl() == null ? vaccin.getDoseMl() : request.doseMl();
        VaccinationIndividuelle value = new VaccinationIndividuelle();
        value.setAnimalId(request.animalId()); value.setAnimalCode(text(animal, "codeUnique")); value.setEspece(com.reseau_partage.core.entities.Espece.valueOf(especeText));
        value.setFermeId(longValue(animal, "fermeId")); value.setVaccin(vaccin); value.setNumeroLotVaccin(request.numeroLotVaccin()); value.setDateExpirationLot(request.dateExpirationLot());
        value.setTypeVaccination(request.typeVaccination() == null ? com.reseau_partage.core.entities.enumtypes.TypeVaccination.PREVENTIVE : request.typeVaccination()); value.setNumeroDoseDansProtocole(request.numeroDoseDansProtocole()); value.setDateVaccination(date);
        value.setAgeAnimalJoursAuMoment(age(dateNaissance(animal), date)); value.setVoieAdministration(request.voieAdministration() == null ? vaccin.getVoieAdministration() : request.voieAdministration()); value.setDoseMl(dose);
        value.setDateProchaineRappel(rappel(date, vaccin)); value.setDateFinDelaiAttente(delai(date, vaccin)); value.setReactionObservee(request.reactionObservee()); value.setVeterinaireNom(request.veterinaireNom()); value.setOperateurNom(request.operateurNom()); value.setNotes(request.notes());
        VaccinationIndividuelle saved = repository.save(value);
        if (saved.getDateProchaineRappel() != null) notificationClient.sendAlert("RAPPEL_VACCIN_INDIVIDUEL", "NORMALE", "Rappel de vaccination de l'animal " + saved.getAnimalCode(), saved.getAnimalId());
        return response(saved);
    }

    @Transactional(readOnly = true) public List<VaccinationIndividuelleResponse> historique(Long animalId) { return repository.findHistoriqueComplet(animalId).stream().map(this::response).toList(); }
    @Transactional(readOnly = true) public Map<String, Object> statut(Long animalId) { List<VaccinationIndividuelleResponse> historique = historique(animalId); return Map.of("animalId", animalId, "nombreVaccinations", historique.size(), "dernieresVaccinations", historique); }
    @Transactional(readOnly = true) public List<VaccinationIndividuelleResponse> rappels(Long fermeId, int horizon) { return repository.findRappelsIndividuelsAVenir(fermeId, LocalDate.now(), LocalDate.now().plusDays(horizon)).stream().map(this::response).toList(); }
    @Transactional(readOnly = true)
    public byte[] certificat(Long animalId) {
        List<VaccinationIndividuelleResponse> vaccinations = historique(animalId);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfDocument pdf = new PdfDocument(new PdfWriter(output));
            try (Document document = new Document(pdf)) {
                document.add(new Paragraph("Carnet vaccinal - animal " + animalId));
                for (VaccinationIndividuelleResponse vaccination : vaccinations) {
                    document.add(new Paragraph(vaccination.dateVaccination() + " - " + vaccination.vaccinNom()
                            + " - dose " + vaccination.doseMl() + " ml"));
                }
            }
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de générer le certificat vaccinal.", exception);
        }
    }
    private LocalDate rappel(LocalDate date, Vaccin vaccin) { return vaccin.getIntervalleRappelJours() == null ? null : date.plusDays(vaccin.getIntervalleRappelJours()); }
    private LocalDate delai(LocalDate date, Vaccin vaccin) { return vaccin.getDelaiAttenteAbattageJours() == null ? null : date.plusDays(vaccin.getDelaiAttenteAbattageJours()); }
    private LocalDate dateNaissance(Map<String, Object> data) { Object value = data.get("dateNaissance"); return value == null ? LocalDate.now() : LocalDate.parse(value.toString()); }
    private int age(LocalDate date, LocalDate today) { return (int) ChronoUnit.DAYS.between(date, today); }
    private String text(Map<String, Object> data, String key) { return data.get(key) == null ? null : data.get(key).toString(); }
    private Long longValue(Map<String, Object> data, String key) { return data.get(key) == null ? null : Long.valueOf(data.get(key).toString()); }
    private VaccinationIndividuelleResponse response(VaccinationIndividuelle v) { return new VaccinationIndividuelleResponse(v.getId(), v.getAnimalId(), v.getAnimalCode(), v.getEspece(), v.getFermeId(), v.getVaccin().getId(), v.getVaccin().getNom(), v.getNumeroLotVaccin(), v.getDateExpirationLot(), v.getTypeVaccination(), v.getNumeroDoseDansProtocole(), v.getDateVaccination(), v.getAgeAnimalJoursAuMoment(), v.getVoieAdministration(), v.getDoseMl(), v.getDateProchaineRappel(), v.getDateFinDelaiAttente(), v.getReactionObservee(), v.getVeterinaireNom(), v.getOperateurNom(), v.getNotes(), v.getDateCreation()); }
}
