package com.reseau_partage.organisation.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.reseau_partage.core.entities.Ferme;
import com.reseau_partage.core.entities.Site;
import com.reseau_partage.core.entities.StatutFerme;
import com.reseau_partage.core.entities.StatutSite;
import com.reseau_partage.core.repository.FermeRepository;
import com.reseau_partage.core.repository.SiteRepository;
import com.reseau_partage.core.repository.UtilisateurRepository;
import com.reseau_partage.organisation.dto.ImportErrorDetail;
import com.reseau_partage.organisation.dto.ImportResultResponse;
import com.reseau_partage.organisation.dto.SiteRequest;
import com.reseau_partage.organisation.exception.ConflictException;
import com.reseau_partage.organisation.exception.ResourceNotFoundException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class SiteImportExportService {

    private static final String[] EXPORT_HEADERS = {
            "id", "nom", "adresse", "ville", "region", "superficie", "statut",
            "responsableNom", "responsableTelephone", "latitude", "longitude",
            "fermeNom", "dateCreation"
    };

    private static final String[] TEMPLATE_HEADERS = {
            "nom*", "adresse", "ville", "region", "superficie",
            "responsableNom", "responsableTelephone", "latitude", "longitude"
    };

    private static final int TEMPLATE_COL_NOM = 0;

    private final SiteRepository siteRepository;
    private final FermeRepository fermeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public SiteImportExportService(SiteRepository siteRepository,
            FermeRepository fermeRepository,
            UtilisateurRepository utilisateurRepository) {
        this.siteRepository = siteRepository;
        this.fermeRepository = fermeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // ────────────────────────────────────────────────────────────────────────
    // EXPORT
    // ────────────────────────────────────────────────────────────────────────

    public record ExportResult(byte[] data, String filename, String contentType) {
    }

    @Transactional(readOnly = true)
    public ExportResult exportSites(Long fermeId, String format, String userEmail) {
        Long userId = utilisateurRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", 0L))
                .getId();

        List<Site> sites = loadSitesForUser(fermeId, userId);
        String fmt = (format == null || format.isBlank()) ? "excel" : format.trim().toLowerCase(Locale.ROOT);
        String dateStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        return switch (fmt) {
            case "csv" -> exportCsv(sites, dateStr);
            case "excel", "xlsx" -> exportExcel(sites, dateStr);
            default -> throw new IllegalArgumentException(
                    "Format invalide : " + fmt + ". Valeurs acceptées : csv, excel.");
        };
    }

    private List<Site> loadSitesForUser(Long fermeId, Long userId) {
        List<Ferme> mesFermes = fermeRepository.findByProprietaireIdAndStatutNot(userId, StatutFerme.ARCHIVEE);
        if (mesFermes.isEmpty()) {
            return List.of();
        }
        Set<Long> fermeIdsAutorisees = new HashSet<>();
        for (Ferme f : mesFermes) {
            fermeIdsAutorisees.add(f.getId());
        }

        if (fermeId != null) {
            if (!fermeIdsAutorisees.contains(fermeId)) {
                throw new ResourceNotFoundException("Ferme", fermeId);
            }
            return siteRepository.findByFermeId(fermeId);
        }
        List<Site> all = new ArrayList<>();
        for (Long fid : fermeIdsAutorisees) {
            all.addAll(siteRepository.findByFermeId(fid));
        }
        return all;
    }

    private ExportResult exportCsv(List<Site> sites, String dateStr) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter csv = new CSVWriter(writer, ',', '"', '"', "\n")) {
            writer.write('\ufeff');
            csv.writeNext(EXPORT_HEADERS);
            for (Site s : sites) {
                csv.writeNext(siteToRow(s));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erreur génération CSV", e);
        }
        return new ExportResult(baos.toByteArray(),
                "sites_export_" + dateStr + ".csv",
                "text/csv;charset=UTF-8");
    }

    private ExportResult exportExcel(List<Site> sites, String dateStr) {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sites");
            CellStyle headerStyle = headerStyle(wb);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < EXPORT_HEADERS.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(EXPORT_HEADERS[i]);
                c.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            for (Site s : sites) {
                Row r = sheet.createRow(rowNum++);
                String[] values = siteToRow(s);
                for (int i = 0; i < values.length; i++) {
                    r.createCell(i).setCellValue(values[i] == null ? "" : values[i]);
                }
            }
            for (int i = 0; i < EXPORT_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return new ExportResult(baos.toByteArray(),
                    "sites_export_" + dateStr + ".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } catch (IOException e) {
            throw new UncheckedIOException("Erreur génération Excel", e);
        }
    }

    private String[] siteToRow(Site s) {
        return new String[] {
                toString(s.getId()),
                s.getNom(),
                s.getAdresse(),
                s.getVille(),
                s.getRegion(),
                toString(s.getSuperficie()),
                s.getStatut() == null ? "" : s.getStatut().name(),
                s.getResponsableNom(),
                s.getResponsableTelephone(),
                toString(s.getLatitude()),
                toString(s.getLongitude()),
                s.getFerme() == null ? "" : s.getFerme().getNom(),
                formatDateTime(s.getDateCreation())
        };
    }

    // ────────────────────────────────────────────────────────────────────────
    // TEMPLATE
    // ────────────────────────────────────────────────────────────────────────

    public ExportResult buildTemplate() {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Sites");
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            CellStyle headerStyle = headerStyle(wb);
            CellStyle commentStyle = commentStyle(wb);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < TEMPLATE_HEADERS.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(TEMPLATE_HEADERS[i]);
                c.setCellStyle(headerStyle);
            }

            Row commentRow = sheet.createRow(1);
            Cell c1 = commentRow.createCell(TEMPLATE_COL_NOM);
            c1.setCellValue("Champ obligatoire");
            c1.setCellStyle(commentStyle);
            for (int i = 1; i < TEMPLATE_HEADERS.length; i++) {
                commentRow.createCell(i).setCellStyle(commentStyle);
            }

            DataValidationConstraint nonVide = dvHelper.createCustomConstraint(
                    "LEN(TRIM($A$2:$A$1048576))>0");
            CellRangeAddressList range = new CellRangeAddressList(
                    2, 1048575, TEMPLATE_COL_NOM, TEMPLATE_COL_NOM);
            DataValidation validation = dvHelper.createValidation(nonVide, range);
            validation.setEmptyCellAllowed(false);
            validation.setShowErrorBox(true);
            validation.createErrorBox("Erreur", "Le nom est obligatoire.");
            validation.setSuppressDropDownArrow(true);
            sheet.addValidationData(validation);

            for (int i = 0; i < TEMPLATE_HEADERS.length; i++) {
                sheet.setColumnWidth(i, 6000);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return new ExportResult(baos.toByteArray(),
                    "sites_import_template.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        } catch (IOException e) {
            throw new UncheckedIOException("Erreur génération template", e);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // IMPORT
    // ────────────────────────────────────────────────────────────────────────

    public ImportResultResponse importerSites(MultipartFile file, Long fermeId, String userEmail) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni.");
        }
        if (fermeId == null) {
            throw new IllegalArgumentException("fermeId est obligatoire.");
        }

        Long userId = utilisateurRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", 0L))
                .getId();

        Ferme ferme = fermeRepository.findById(fermeId)
                .orElseThrow(() -> new ResourceNotFoundException("Ferme", fermeId));
        if (!Objects.equals(ferme.getProprietaireId(), userId)) {
            throw new ResourceNotFoundException("Ferme", fermeId);
        }
        if (ferme.getStatut() == StatutFerme.ARCHIVEE) {
            throw new IllegalArgumentException("Impossible d'importer sur une ferme archivée.");
        }

        String filename = file.getOriginalFilename() == null ? ""
                : file.getOriginalFilename().toLowerCase(Locale.ROOT);

        try (InputStream is = file.getInputStream()) {
            if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                return importerExcel(is, ferme);
            } else if (filename.endsWith(".csv")) {
                return importerCsv(is, ferme);
            } else {
                throw new IllegalArgumentException(
                        "Format non supporté : " + filename + ". Formats acceptés : .csv, .xlsx, .xls.");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erreur lecture fichier", e);
        }
    }

    private ImportResultResponse importerCsv(InputStream is, Ferme ferme) {
        List<ImportErrorDetail> errors = new ArrayList<>();
        int importes = 0;
        int total = 0;
        int lineNum = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String[] ligne;
            while ((ligne = reader.readNext()) != null) {
                lineNum++;
                if (lineNum == 1) continue;
                if (ligne == null || isEmptyRow(ligne)) continue;
                total++;
                SiteRequest parsed = parseRow(ligne, lineNum, errors);
                if (parsed == null) continue;
                if (createIfValid(ferme, parsed, lineNum, errors)) {
                    importes++;
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Erreur parsing CSV : " + e.getMessage(), e);
        }

        return new ImportResultResponse(errors.isEmpty(), total, importes, errors.size(), errors);
    }

    private ImportResultResponse importerExcel(InputStream is, Ferme ferme) {
        List<ImportErrorDetail> errors = new ArrayList<>();
        int importes = 0;
        int total = 0;

        try (Workbook wb = WorkbookFactory.create(is)) {
            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIt = sheet.iterator();
            int rowNum = 0;
            while (rowIt.hasNext()) {
                rowNum++;
                Row row = rowIt.next();
                if (rowNum == 1 || rowNum == 2) continue;
                String[] ligne = rowToStringArray(row);
                if (isEmptyRow(ligne)) continue;
                total++;
                SiteRequest parsed = parseRow(ligne, rowNum, errors);
                if (parsed == null) continue;
                if (createIfValid(ferme, parsed, rowNum, errors)) {
                    importes++;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erreur lecture Excel", e);
        }

        return new ImportResultResponse(errors.isEmpty(), total, importes, errors.size(), errors);
    }

    private SiteRequest parseRow(String[] ligne, int lineNum, List<ImportErrorDetail> errors) {
        String nom = safe(ligne, 0);
        String adresse = safe(ligne, 1);
        String ville = safe(ligne, 2);
        String region = safe(ligne, 3);
        String superficie = safe(ligne, 4);
        String respNom = safe(ligne, 5);
        String respTel = safe(ligne, 6);
        String latitude = safe(ligne, 7);
        String longitude = safe(ligne, 8);

        List<String> lineErrors = new ArrayList<>();
        if (nom == null || nom.isBlank()) {
            lineErrors.add("Nom obligatoire manquant");
        }

        BigDecimal supNum = null;
        if (superficie != null && !superficie.isBlank()) {
            try {
                supNum = new BigDecimal(superficie.replace(',', '.').trim());
                if (supNum.signum() < 0) {
                    lineErrors.add("Superficie invalide (negatif) : '" + superficie + "'");
                    supNum = null;
                }
            } catch (NumberFormatException e) {
                lineErrors.add("Superficie invalide : '" + superficie + "'");
            }
        }

        BigDecimal lat = parseDecimal(latitude, "Latitude", lineErrors);
        BigDecimal lng = parseDecimal(longitude, "Longitude", lineErrors);

        if (!lineErrors.isEmpty()) {
            errors.add(new ImportErrorDetail(lineNum, String.join(" ; ", lineErrors)));
            return null;
        }

        return new SiteRequest(null, nom, adresse, ville, region, lat, lng, supNum, respNom, respTel);
    }

    private boolean createIfValid(Ferme ferme, SiteRequest parsed, int lineNum,
            List<ImportErrorDetail> errors) {
        try {
            if (siteRepository.existsByNomAndFermeId(parsed.nom(), ferme.getId())) {
                errors.add(new ImportErrorDetail(lineNum,
                        "Un site portant ce nom existe deja dans cette ferme : '" + parsed.nom() + "'"));
                return false;
            }
            Site s = new Site();
            s.setFerme(ferme);
            s.setNom(parsed.nom());
            s.setAdresse(parsed.adresse());
            s.setVille(parsed.ville());
            s.setRegion(parsed.region());
            s.setSuperficie(parsed.superficie());
            s.setLatitude(parsed.latitude());
            s.setLongitude(parsed.longitude());
            s.setResponsableNom(parsed.responsableNom());
            s.setResponsableTelephone(parsed.responsableTelephone());
            s.setStatut(StatutSite.ACTIF);
            siteRepository.save(s);
            return true;
        } catch (ConflictException ce) {
            errors.add(new ImportErrorDetail(lineNum, ce.getMessage()));
            return false;
        } catch (Exception e) {
            errors.add(new ImportErrorDetail(lineNum, "Erreur inattendue : " + e.getMessage()));
            return false;
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Utilitaires
    // ────────────────────────────────────────────────────────────────────────

    private BigDecimal parseDecimal(String value, String label, List<String> errors) {
        if (value == null || value.isBlank()) return null;
        try {
            return new BigDecimal(value.replace(',', '.').trim());
        } catch (NumberFormatException e) {
            errors.add(label + " invalide : '" + value + "'");
            return null;
        }
    }

    private String safe(String[] row, int idx) {
        if (row == null || idx >= row.length) return null;
        String v = row[idx];
        return v == null ? null : v.trim();
    }

    private String[] rowToStringArray(Row row) {
        int n = TEMPLATE_HEADERS.length;
        String[] arr = new String[n];
        DataFormatter fmt = new DataFormatter(Locale.ROOT);
        for (int i = 0; i < n; i++) {
            Cell c = row.getCell(i);
            arr[i] = c == null ? null : fmt.formatCellValue(c).trim();
        }
        return arr;
    }

    private boolean isEmptyRow(String[] row) {
        if (row == null) return true;
        for (String v : row) {
            if (v != null && !v.isBlank()) return false;
        }
        return true;
    }

    private CellStyle headerStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        s.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
        return s;
    }

    private CellStyle commentStyle(Workbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        f.setItalic(true);
        s.setFont(f);
        return s;
    }

    private String toString(Object o) {
        return o == null ? "" : o.toString();
    }

    private String formatDateTime(LocalDateTime dt) {
        if (dt == null) return "";
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
