package com.reseau_partage.stocks.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.stocks.client.NotificationClient;
import com.reseau_partage.core.entities.Article;
import com.reseau_partage.core.entities.enumtypes.StatutStock;
import com.reseau_partage.core.repository.ArticleRepository;

@Service
public class AlerteStockService {

    private final ArticleRepository articleRepository;
    private final NotificationClient notificationClient;

    public AlerteStockService(ArticleRepository articleRepository, NotificationClient notificationClient) {
        this.articleRepository = articleRepository;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public void evaluerEtEnvoyerAlertes(Article article) {
        StatutStock statut = article.getStatut();
        String message;
        String priorite;

        switch (statut) {
            case RUPTURE:
                priorite = "CRITIQUE";
                message = "Rupture de stock pour l'article: " + article.getDesignation() + " (code: " + article.getCodeArticle() + ")";
                break;
            case CRITIQUE:
                priorite = "HAUTE";
                message = "Stock critique pour l'article: " + article.getDesignation() + " (code: " + article.getCodeArticle() + "), stock restant: " + article.getStockActuel();
                break;
            case FAIBLE:
                priorite = "NORMALE";
                message = "Stock faible pour l'article: " + article.getDesignation() + " (code: " + article.getCodeArticle() + "), stock restant: " + article.getStockActuel();
                break;
            case PERIME:
                priorite = "CRITIQUE";
                message = "Article perime pour: " + article.getDesignation() + " (code: " + article.getCodeArticle() + ")";
                break;
            default:
                return;
        }

        envoyerAlerte(priorite, message, article.getId());
    }

    @Scheduled(cron = "0 7 * * * *")
    @Transactional
    public void verifierPeremptionsProches() {
        LocalDate aujourdHui = LocalDate.now();
        LocalDate dateLimite = aujourdHui.plusDays(30);
        List<Article> articles = articleRepository.findAll().stream()
                .filter(a -> a.getActif() != null && a.getActif())
                .filter(a -> a.getDatePeremption() != null
                        && !a.getDatePeremption().isBefore(aujourdHui)
                        && !a.getDatePeremption().isAfter(dateLimite))
                .toList();

        for (Article article : articles) {
            long jours = java.time.temporal.ChronoUnit.DAYS.between(aujourdHui, article.getDatePeremption());
            String message = "Article en péremption dans " + jours + " jours: "
                    + article.getDesignation() + " (code: " + article.getCodeArticle() + ")";
            envoyerAlerte("NORMALE", message, article.getId());
        }
    }

    private void envoyerAlerte(String priorite, String message, Long articleId) {
        try {
            notificationClient.sendAlert(priorite, message, articleId);
        } catch (Exception e) {
            System.err.println("Impossible d'envoyer l'alerte vers M13 (Notifications): " + e.getMessage());
        }
    }
}