package com.reseau_partage.animaux.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.reseau_partage.animaux.dto.animal.AnimalResponse;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ModeSuivi;
import com.reseau_partage.core.entities.MouvementAnimal;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.repository.AnimalRepository;

@Service
public class ExportService {

    private final AnimalRepository animalRepository;
    private final AnimalService animalService;
    private final AuditService auditService;

    public ExportService(AnimalRepository animalRepository, AnimalService animalService, AuditService auditService) {
        this.animalRepository = animalRepository;
        this.animalService = animalService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public byte[] exporterListeCSV(Espece espece, StatutAnimal statut, ModeSuivi modeSuivi,
                                    Long structureId, Long siteId, Long fermeId, Long bandeId,
                                    Sexe sexe, Integer ageMinJours, Integer ageMaxJours,
                                    java.math.BigDecimal poidsMinKg, java.math.BigDecimal poidsMaxKg,
                                    LocalDate dateEntreeDebut, LocalDate dateEntreeFin) {
        List<Animal> animaux = rechercher(espece, statut, modeSuivi, structureId, siteId, fermeId, bandeId,
                sexe, ageMinJours, ageMaxJours, poidsMinKg, poidsMaxKg, dateEntreeDebut, dateEntreeFin);
        try (StringWriter sw = new StringWriter();
             CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT
                     .withHeader("codeUnique", "espece", "race", "sexe", "statut", "structure", "dateEntree", "poidsActuelKg"))) {
            for (Animal a : animaux) {
                printer.printRecord(
                        a.getCodeUnique(),
                        a.getEspece(),
                        a.getRace(),
                        a.getSexe(),
                        a.getStatut(),
                        a.getStructure() != null ? a.getStructure().getId() : null,
                        a.getDateEntree(),
                        a.getPoidsActuelKg());
            }
            return sw.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Erreur generation CSV", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] exporterBilanViePDF(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal", animalId);
        }
            java.util.Map<String, Object> bilan = auditService.bilanVie(animalId);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            document.add(new Paragraph("Bilan de vie - Animal #" + animalId));
            Animal animal = (Animal) bilan.get("animal");
            if (animal != null) {
                document.add(new Paragraph("Code: " + animal.getCodeUnique() + " | Espece: " + animal.getEspece()
                        + " | Statut: " + animal.getStatut()));
            }
            document.add(new Paragraph("Mouvements"));
            List<MouvementAnimal> mouvements = (List<MouvementAnimal>) bilan.get("mouvements");
            Table table = new Table(3);
            table.addCell("Type");
            table.addCell("Date");
            table.addCell("Motif");
            for (MouvementAnimal m : mouvements) {
                table.addCell(String.valueOf(m.getTypeMouvement()));
                table.addCell(String.valueOf(m.getDateMouvement()));
                table.addCell(m.getMotif());
            }
            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur generation PDF", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AnimalResponse> rechercheAvancee(Espece espece, StatutAnimal statut, ModeSuivi modeSuivi,
                                                 Long structureId, Long siteId, Long fermeId, Long bandeId,
                                                 Sexe sexe, Integer ageMinJours, Integer ageMaxJours,
                                                 java.math.BigDecimal poidsMinKg, java.math.BigDecimal poidsMaxKg,
                                                 LocalDate dateEntreeDebut, LocalDate dateEntreeFin) {
        return rechercher(espece, statut, modeSuivi, structureId, siteId, fermeId, bandeId,
                sexe, ageMinJours, ageMaxJours, poidsMinKg, poidsMaxKg, dateEntreeDebut, dateEntreeFin)
                .stream().map(animalService::toResponsePublic).toList();
    }

    private List<Animal> rechercher(Espece espece, StatutAnimal statut, ModeSuivi modeSuivi,
                                    Long structureId, Long siteId, Long fermeId, Long bandeId,
                                    Sexe sexe, Integer ageMinJours, Integer ageMaxJours,
                                    java.math.BigDecimal poidsMinKg, java.math.BigDecimal poidsMaxKg,
                                    LocalDate dateEntreeDebut, LocalDate dateEntreeFin) {
        LocalDate aujourdHui = LocalDate.now();
        return animalRepository.findAll().stream().filter(a -> {
            if (espece != null && a.getEspece() != espece) return false;
            if (statut != null && a.getStatut() != statut) return false;
            if (modeSuivi != null && a.getModeSuivi() != modeSuivi) return false;
            if (sexe != null && a.getSexe() != sexe) return false;
            if (structureId != null && (a.getStructure() == null || !a.getStructure().getId().equals(structureId))) return false;
            if (bandeId != null && (a.getBande() == null || !a.getBande().getId().equals(bandeId))) return false;
            if (siteId != null && (a.getStructure() == null || a.getStructure().getSite() == null
                    || !a.getStructure().getSite().getId().equals(siteId))) return false;
            if (fermeId != null && (a.getStructure() == null || a.getStructure().getSite() == null
                    || a.getStructure().getSite().getFerme() == null
                    || !a.getStructure().getSite().getFerme().getId().equals(fermeId))) return false;
            if (a.getDateNaissance() != null && ageMinJours != null
                    && java.time.temporal.ChronoUnit.DAYS.between(a.getDateNaissance(), aujourdHui) < ageMinJours) return false;
            if (a.getDateNaissance() != null && ageMaxJours != null
                    && java.time.temporal.ChronoUnit.DAYS.between(a.getDateNaissance(), aujourdHui) > ageMaxJours) return false;
            if (poidsMinKg != null && (a.getPoidsActuelKg() == null || a.getPoidsActuelKg().compareTo(poidsMinKg) < 0)) return false;
            if (poidsMaxKg != null && (a.getPoidsActuelKg() == null || a.getPoidsActuelKg().compareTo(poidsMaxKg) > 0)) return false;
            if (dateEntreeDebut != null && (a.getDateEntree() == null || a.getDateEntree().isBefore(dateEntreeDebut))) return false;
            if (dateEntreeFin != null && (a.getDateEntree() == null || a.getDateEntree().isAfter(dateEntreeFin))) return false;
            return true;
        }).toList();
    }
}
