package com.reseau_partage.stocks.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.core.entities.Article;
import com.reseau_partage.core.entities.enumtypes.StatutStock;
import com.reseau_partage.core.repository.ArticleRepository;
import com.reseau_partage.stocks.entities.AlerteStock;
import com.reseau_partage.stocks.repository.AlerteStockRepository;

@Service
public class AlerteStockService {

    private final AlerteStockRepository alerteRepository;
    private final ArticleRepository articleRepository;

    public AlerteStockService(AlerteStockRepository alerteRepository, ArticleRepository articleRepository) {
        this.alerteRepository = alerteRepository;
        this.articleRepository = articleRepository;
    }

    @Transactional
    public void synchroniserAlertesArticle(Long articleId) {
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null || Boolean.FALSE.equals(article.getActif())) {
            return;
        }

        alerteRepository.deleteByArticleIdAndTypeAlerte(articleId, "RUPTURE_STOCK");
        alerteRepository.deleteByArticleIdAndTypeAlerte(articleId, "RUPTURE_CRITIQUE");
        alerteRepository.deleteByArticleIdAndTypeAlerte(articleId, "SEUIL_ALERTE");
        alerteRepository.deleteByArticleIdAndTypeAlerte(articleId, "PEREMPTION_PROCHE");
        alerteRepository.deleteByArticleIdAndTypeAlerte(articleId, "PEREMPTION_DEPASSEE");
        alerteRepository.deleteByArticleIdAndTypeAlerte(articleId, "TEMPERATURE");

        creerAlertesPourArticle(article);
    }

    @Transactional
    public void synchroniserToutesAlertes(Long fermeId) {
        List<Article> articles = articleRepository.findByFermeIdAndActifTrue(fermeId);
        for (Article article : articles) {
            synchroniserAlertesArticle(article.getId());
        }
    }

    private void creerAlertesPourArticle(Article article) {
        List<AlerteStock> alertes = new ArrayList<>();
        LocalDate aujourdHui = LocalDate.now();

        if (article.getStatut() == StatutStock.RUPTURE || article.getStockActuel().compareTo(BigDecimal.ZERO) == 0) {
            alertes.add(buildAlerte(article, "RUPTURE_STOCK", "CRITIQUE",
                    "Rupture de stock",
                    "L'article " + article.getDesignation() + " (code " + article.getCodeArticle() + ") est en rupture."));
        } else if (article.getStatut() == StatutStock.CRITIQUE ||
                (article.getSeuilAlerteCritique() != null && article.getStockActuel().compareTo(article.getSeuilAlerteCritique()) <= 0)) {
            alertes.add(buildAlerte(article, "RUPTURE_CRITIQUE", "CRITIQUE",
                    "Stock critique",
                    "Stock critique pour " + article.getDesignation() + " (code " + article.getCodeArticle() + "), restant : " + article.getStockActuel()));
        } else if (article.getStatut() == StatutStock.FAIBLE ||
                article.getStockActuel().compareTo(article.getSeuilAlerteMin()) <= 0) {
            alertes.add(buildAlerte(article, "SEUIL_ALERTE", "HAUTE",
                    "Stock sous seuil d'alerte",
                    "Stock faible pour " + article.getDesignation() + " (code " + article.getCodeArticle() + "), restant : " + article.getStockActuel()));
        }

        if (article.getDatePeremption() != null) {
            long jours = java.time.temporal.ChronoUnit.DAYS.between(aujourdHui, article.getDatePeremption());
            if (jours < 0) {
                alertes.add(buildAlerte(article, "PEREMPTION_DEPASSEE", "CRITIQUE",
                        "Date de péremption dépassée",
                        "L'article " + article.getDesignation() + " est périmé depuis " + Math.abs(jours) + " jours."));
            } else if (jours <= 30) {
                alertes.add(buildAlerte(article, "PEREMPTION_PROCHE", "NORMALE",
                        "Péremption dans " + jours + " jours",
                        "L'article " + article.getDesignation() + " (code " + article.getCodeArticle() + ") arrive à péremption dans " + jours + " jours."));
            }
        }

        if (!alertes.isEmpty()) {
            alerteRepository.saveAll(alertes);
        }
    }

    @Transactional(readOnly = true)
    public List<AlerteStock> listerAlertes(Long fermeId, Boolean resolue, String typeAlerte, String search) {
        List<AlerteStock> alertes = alerteRepository.findAllByFermeId(fermeId);
        boolean filtreResolue = resolue == null ? false : resolue;

        List<AlerteStock> filtered = new java.util.ArrayList<>();
        for (AlerteStock alerte : alertes) {
            if (alerte.getResolue() != filtreResolue) continue;
            if (typeAlerte != null && !typeAlerte.isBlank() && !typeAlerte.equals(alerte.getTypeAlerte())) continue;
            if (search != null && !search.isBlank() && !correspond(alerte, search)) continue;
            filtered.add(alerte);
        }
        return filtered;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> statistiques(Long fermeId) {
        List<AlerteStock> alertes = alerteRepository.findAllByFermeId(fermeId);

        long total = 0;
        long totalResolues = 0;
        long critiques = 0;
        long ruptures = 0;
        long peremptions = 0;
        long seuils = 0;

        for (AlerteStock alerte : alertes) {
            if (alerte.getResolue()) {
                totalResolues++;
            } else {
                total++;
                if ("CRITIQUE".equals(alerte.getPriorite())) critiques++;
                if ("RUPTURE_STOCK".equals(alerte.getTypeAlerte())) ruptures++;
                if ("PEREMPTION_PROCHE".equals(alerte.getTypeAlerte()) || "PEREMPTION_DEPASSEE".equals(alerte.getTypeAlerte())) peremptions++;
                if ("SEUIL_ALERTE".equals(alerte.getTypeAlerte())) seuils++;
            }
        }

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("fermeId", fermeId);
        stats.put("totalActives", total);
        stats.put("totalResolues", totalResolues);
        stats.put("totalCritiques", critiques);
        stats.put("totalRuptures", ruptures);
        stats.put("totalPeremptions", peremptions);
        stats.put("totalSeuils", seuils);
        return stats;
    }

    @Transactional
    public AlerteStock resoudre(Long alerteId, String commentaire, Long utilisateurId) {
        AlerteStock alerte = alerteRepository.findById(alerteId)
                .orElseThrow(() -> new com.reseau_partage.stocks.exception.ResourceNotFoundException("AlerteStock", alerteId));
        alerte.setResolue(Boolean.TRUE);
        alerte.setDateResolution(LocalDateTime.now());
        alerte.setCommentaireResolution(commentaire);
        alerte.setResoluPar(utilisateurId);
        return alerteRepository.save(alerte);
    }

    @Transactional
    public void resoudreTout(Long fermeId, Long utilisateurId) {
        List<AlerteStock> alertes = alerteRepository.findActiveByFermeId(fermeId);
        LocalDateTime now = LocalDateTime.now();
        List<AlerteStock> aResoudre = new ArrayList<>();
        for (AlerteStock alerte : alertes) {
            if (!alerte.getResolue()) {
                alerte.setResolue(Boolean.TRUE);
                alerte.setDateResolution(now);
                alerte.setResoluPar(utilisateurId);
                aResoudre.add(alerte);
            }
        }
        alerteRepository.saveAll(aResoudre);
    }

    private AlerteStock buildAlerte(Article article, String typeAlerte, String priorite, String titre, String description) {
        AlerteStock alerte = new AlerteStock();
        alerte.setFermeId(article.getFermeId());
        alerte.setArticleId(article.getId());
        alerte.setArticleCode(article.getCodeArticle());
        alerte.setArticleDesignation(article.getDesignation());
        alerte.setArticleCategorie(article.getCategorie());
        alerte.setArticleUniteMesure(article.getUniteMesure());
        alerte.setArticleEmplacement(article.getEmplacementStockage());
        alerte.setArticleNumeroLot(article.getNumeroLot());
        alerte.setArticleTemperatureConservation(article.getTemperatureConservation());
        alerte.setTypeAlerte(typeAlerte);
        alerte.setPriorite(priorite);
        alerte.setTitre(titre);
        alerte.setDescription(description);
        alerte.setStockActuel(article.getStockActuel());
        alerte.setSeuilAlerteMin(article.getSeuilAlerteMin());
        alerte.setSeuilAlerteCritique(article.getSeuilAlerteCritique());
        alerte.setDatePeremption(article.getDatePeremption());
        if (article.getDatePeremption() != null) {
            alerte.setJoursAvantPeremption((int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), article.getDatePeremption()));
        }
        alerte.setResolue(Boolean.FALSE);
        return alerte;
    }

    private boolean correspond(AlerteStock alerte, String search) {
        if (search == null || search.isBlank()) return true;
        String s = search.toLowerCase();
        return alerte.getArticleDesignation().toLowerCase().contains(s)
                || alerte.getArticleCode().toLowerCase().contains(s)
                || alerte.getTitre().toLowerCase().contains(s);
    }
}
