package com.reseau_partage.stocks.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Comparator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.stocks.client.OrganisationClient;
import com.reseau_partage.stocks.dto.article.ArticleRequest;
import com.reseau_partage.stocks.dto.article.ArticleResponse;
import com.reseau_partage.stocks.dto.mouvement.MouvementRequest;
import com.reseau_partage.stocks.dto.mouvement.MouvementResponse;
import com.reseau_partage.stocks.dto.stats.StockStatsResponse;
import com.reseau_partage.stocks.exception.ResourceNotFoundException;
import com.reseau_partage.stocks.exception.StockInsuffisantException;
import com.reseau_partage.core.entities.Article;
import com.reseau_partage.core.entities.MouvementStock;
import com.reseau_partage.core.entities.enumtypes.CategorieArticle;
import com.reseau_partage.core.entities.enumtypes.StatutStock;
import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;
import com.reseau_partage.core.repository.ArticleRepository;
import com.reseau_partage.core.repository.MouvementStockRepository;
import com.reseau_partage.core.repository.FournisseurRepository;
import com.reseau_partage.core.entities.Fournisseur;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MouvementStockRepository mouvementRepository;
    private final AlerteStockService alerteStockService;
    private final OrganisationClient organisationClient;
    private final FournisseurRepository fournisseurRepository;

    public ArticleService(ArticleRepository articleRepository, MouvementStockRepository mouvementRepository, AlerteStockService alerteStockService, OrganisationClient organisationClient, FournisseurRepository fournisseurRepository) {
        this.articleRepository = articleRepository;
        this.mouvementRepository = mouvementRepository;
        this.alerteStockService = alerteStockService;
        this.organisationClient = organisationClient;
        this.fournisseurRepository = fournisseurRepository;
    }

    @Transactional
    public ArticleResponse creerArticle(ArticleRequest request) {
        Map<String, Object> entrepot = request.getEntrepotId() == null ? null : organisationClient.getEntrepot(request.getEntrepotId());
        if (request.getEntrepotId() != null && entrepot == null) throw new ResourceNotFoundException("Entrepot", request.getEntrepotId());
        if (request.getFermeId() == null) throw new IllegalArgumentException("fermeId est obligatoire.");
        verifierEntrepot(request.getEntrepotId(), request.getFermeId(), entrepot);
        String entrepotNom = entrepot != null ? (String) entrepot.get("nom") : request.getEntrepot();
        Long fermeId = request.getFermeId() != null ? request.getFermeId() : extractFermeIdFromEntrepot(entrepot);
        String codeArticle = request.getCodeArticle() == null || request.getCodeArticle().isBlank()
                ? genererCodeArticle(request.getCategorie()) : request.getCodeArticle();
        if (articleRepository.existsByCodeArticle(codeArticle)) throw new IllegalArgumentException("La reference existe deja : " + codeArticle);

        Article article = new Article();
        article.setCodeArticle(codeArticle);
        article.setDesignation(request.getDesignation());
        article.setDescription(request.getDescription());
        article.setCategorie(request.getCategorie());
        article.setUniteMesure(request.getUniteMesure());
        article.setFermeId(fermeId);
        article.setEntrepotId(request.getEntrepotId());
        article.setEntrepotNom(entrepotNom);
        article.setSiteId(entrepot == null ? null : extractSiteId(entrepot));
        article.setSiteNom(entrepot == null ? null : extractSiteNom(entrepot));
        article.setSeuilAlerteMin(request.getSeuilAlerteMin());
        article.setSeuilAlerteCritique(request.getSeuilAlerteCritique());
        article.setStockMax(request.getStockMax());
        article.setPrixUnitaireRef(request.getPrixUnitaireRef());
        article.setDatePeremption(request.getDatePeremption());
        article.setAlertePeremptionJours(request.getAlertePeremptionJours());
        article.setEspecesLiees(request.getEspecesLiees());
        article.setActif(true);
        article.setEmplacementStockage(request.getEmplacementStockage());
        article.setNumeroLot(request.getNumeroLot());
        article.setNotesInternes(request.getNotesInternes());
        article.setEstPerissable(Boolean.TRUE.equals(request.getEstPerissable()));
        article.setEstSuiviLot(Boolean.TRUE.equals(request.getEstSuiviLot()));
        article.setTemperatureConservation(request.getTemperatureConservation());
        article.setDateEntree(request.getDateEntree());
        if (request.getFournisseurId() != null) article.setFournisseurHabituel(fournisseurRepository.findById(request.getFournisseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", request.getFournisseurId())));

        BigDecimal stockInitial = request.getStockInitial() != null ? request.getStockInitial() : request.getStockActuel();
        article.setStockInitial(stockInitial == null ? BigDecimal.ZERO : stockInitial);
        article.setStockActuel(stockInitial == null ? BigDecimal.ZERO : stockInitial);
        article.setValeurStock(article.getStockActuel().multiply(request.getPrixUnitaireRef() == null ? BigDecimal.ZERO : request.getPrixUnitaireRef()));
        article = articleRepository.save(article);

        if (stockInitial != null && stockInitial.compareTo(BigDecimal.ZERO) > 0) {

            MouvementStock mvt = new MouvementStock();
            mvt.setArticle(article);
            mvt.setFermeId(fermeId);
            mvt.setTypeMouvement(TypeMouvementStock.ENTREE);
            mvt.setMotif(com.reseau_partage.core.entities.enumtypes.MotifMouvement.PRODUCTION);
            mvt.setQuantite(stockInitial);
            mvt.setStockAvant(BigDecimal.ZERO);
            mvt.setStockApres(stockInitial);
            mvt.setPrixUnitaire(request.getPrixUnitaireRef());
            mvt.setDateMouvement(LocalDate.now());
            mvt.setNotes("Creation initiale de stock");
            mouvementRepository.save(mvt);
        }
        mettreAJourStatut(article);
        alerteStockService.synchroniserAlertesArticle(article.getId());
        return toResponse(article);
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponse> listerArticles(Long fermeId, CategorieArticle categorie, String statutStock, String search, Pageable pageable) {
        if (fermeId == null) throw new IllegalArgumentException("fermeId est obligatoire.");
        Page<Article> articles;
        {
            List<Article> list = articleRepository.findByFermeIdAndActifTrue(fermeId).stream()
                    .filter(a -> categorie == null || a.getCategorie() == categorie)
                    .filter(a -> correspondStatut(a, statutStock))
                    .filter(a -> correspondRecherche(a, search))
                    .sorted(comparateur(pageable.getSort()))
                    .toList();
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), list.size());
            java.util.List<Article> pageContent = start > list.size() ? java.util.List.of() : list.subList(start, end);
            articles = new PageImpl<>(pageContent, pageable, list.size());
        }
        return articles.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ArticleResponse getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        return toResponse(article);
    }

    @Transactional
    public ArticleResponse mettreAJourArticle(Long id, ArticleRequest request) {
        Article article = articleRepository.findByIdPessimisticWrite(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        article.setDesignation(request.getDesignation());
        article.setDescription(request.getDescription());
        article.setCategorie(request.getCategorie());
        article.setUniteMesure(request.getUniteMesure());
        article.setSeuilAlerteMin(request.getSeuilAlerteMin());
        article.setSeuilAlerteCritique(request.getSeuilAlerteCritique());
        article.setStockMax(request.getStockMax());
        article.setPrixUnitaireRef(request.getPrixUnitaireRef());
        article.setDatePeremption(request.getDatePeremption());
        article.setAlertePeremptionJours(request.getAlertePeremptionJours());
        if (request.getCodeArticle() != null && !request.getCodeArticle().isBlank() && !request.getCodeArticle().equals(article.getCodeArticle())) {
            if (articleRepository.existsByCodeArticleAndIdNot(request.getCodeArticle(), id)) throw new IllegalArgumentException("La reference existe deja : " + request.getCodeArticle());
            article.setCodeArticle(request.getCodeArticle());
        }
        article.setEntrepotNom(request.getEntrepot() == null ? article.getEntrepotNom() : request.getEntrepot());
        article.setEmplacementStockage(request.getEmplacementStockage());
        article.setNumeroLot(request.getNumeroLot());
        article.setNotesInternes(request.getNotesInternes());
        article.setEstPerissable(Boolean.TRUE.equals(request.getEstPerissable()));
        article.setEstSuiviLot(Boolean.TRUE.equals(request.getEstSuiviLot()));
        article.setTemperatureConservation(request.getTemperatureConservation());
        article.setDateEntree(request.getDateEntree());
        if (request.getFournisseurId() != null) article.setFournisseurHabituel(fournisseurRepository.findById(request.getFournisseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", request.getFournisseurId())));
        if (request.getStockActuel() != null) article.setStockActuel(request.getStockActuel());
        article.setValeurStock(article.getStockActuel().multiply(article.getPrixUnitaireRef() == null ? BigDecimal.ZERO : article.getPrixUnitaireRef()));
        article = articleRepository.save(article);
        mettreAJourStatut(article);
        alerteStockService.synchroniserAlertesArticle(article.getId());
        return toResponse(article);
    }

    @Transactional
    public ArticleResponse desactiverArticle(Long id) {
        Article article = articleRepository.findByIdPessimisticWrite(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        article.setActif(false);
        article.setStatut(StatutStock.INACTIF);
        articleRepository.save(article);
        return toResponse(article);
    }

    @Transactional
    public void supprimerArticle(Long id) {
        Article article = articleRepository.findByIdPessimisticWrite(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        long mouvements = mouvementRepository.countByArticleId(id);
        if (mouvements > 0) throw new IllegalStateException("Impossible a supprimer : " + mouvements + " mouvements associes");
        articleRepository.delete(article);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> statistiques(Long fermeId) {
        List<Article> articles = fermeId == null ? articleRepository.findAll().stream().filter(a -> Boolean.TRUE.equals(a.getActif())).toList() : articleRepository.findByFermeIdAndActifTrue(fermeId);
        long rupture = articles.stream().filter(a -> a.getStockActuel().compareTo(BigDecimal.ZERO) == 0).count();
        long critiques = articles.stream().filter(a -> a.getStatut() == StatutStock.CRITIQUE || a.getStatut() == StatutStock.FAIBLE).count();
        BigDecimal valeur = articles.stream().map(a -> a.getValeurStock() == null ? BigDecimal.ZERO : a.getValeurStock()).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("totalReferences", (long) articles.size()); resultat.put("articlesEnStock", articles.size() - rupture);
        resultat.put("articlesEnRupture", rupture); resultat.put("articlesCritiques", critiques);
        resultat.put("valeurTotaleStockFCFA", valeur); resultat.put("valeurTotaleStockFormatee", formatMontant(valeur));
        return resultat;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> categories(Long fermeId) {
        List<Article> articles = fermeId == null ? articleRepository.findAll().stream().filter(a -> Boolean.TRUE.equals(a.getActif())).toList() : articleRepository.findByFermeIdAndActifTrue(fermeId);
        Map<CategorieArticle, String> couleurs = Map.of(CategorieArticle.ALIMENT, "#4A7C23", CategorieArticle.MEDICAMENT, "#1E88E5", CategorieArticle.VACCIN, "#8E24AA", CategorieArticle.MATERIEL, "#546E7A", CategorieArticle.PESTICIDE, "#F4511E", CategorieArticle.SEMENCE, "#7CB342", CategorieArticle.AUTRE, "#757575");
        return java.util.Arrays.stream(CategorieArticle.values()).map(categorie -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("code", categorie.name()); item.put("label", labelCategorie(categorie)); item.put("couleur", couleurs.get(categorie));
            item.put("nombreArticles", articles.stream().filter(a -> a.getCategorie() == categorie).count());
            return item;
        }).toList();
    }

    @Transactional(readOnly = true)
    public java.util.List<ArticleResponse> getArticlesEnAlerte(Long fermeId) {
        return articleRepository.findArticlesEnAlerte(fermeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public java.util.List<ArticleResponse> getArticlesPeremptionProche(Long fermeId, int jours) {
        LocalDate aujourdHui = LocalDate.now();
        LocalDate dateLimite = aujourdHui.plusDays(jours);
        return articleRepository.findArticlesPeremptionProche(fermeId, aujourdHui, dateLimite).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StockStatsResponse getDashboardStats(Long fermeId) {
        StockStatsResponse stats = new StockStatsResponse();
        stats.setValeurTotale(articleRepository.findValeurTotaleStockByFerme(fermeId));
        stats.setNbArticles((long) articleRepository.findByFermeIdAndActifTrue(fermeId).size());
        stats.setNbEnAlerte((long) articleRepository.findArticlesEnAlerte(fermeId).size());
        LocalDate debut30j = LocalDate.now().minusDays(30);
        stats.setNbMouvements30j((long) mouvementRepository.findByFermeIdAndDateMouvementBetween(fermeId, debut30j, LocalDate.now()).size());

        java.util.List<Object[]> valeurParCat = articleRepository.findValeurParCategorie(fermeId);
        java.util.Map<String, BigDecimal> map = new java.util.HashMap<>();
        for (Object[] row : valeurParCat) {
            map.put(row[0].toString(), (BigDecimal) row[1]);
        }
        stats.setValeurParCategorie(map);
        return stats;
    }

    @Transactional(readOnly = true)
    public java.util.List<Object[]> getEvolutionStock(Long articleId) {
        return mouvementRepository.findEvolutionStock(articleId);
    }

    @Transactional(readOnly = true)
    public Integer getJoursStockRestants(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", articleId));
        if (article.getStockActuel().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        LocalDate fin = LocalDate.now();
        LocalDate debut = fin.minusDays(7);
        BigDecimal consommation = mouvementRepository.findConsommationSurPeriode(articleId, debut, fin);
        BigDecimal consommationJournaliere = consommation.divide(BigDecimal.valueOf(7), MathContext.DECIMAL128);
        if (consommationJournaliere.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        BigDecimal jours = article.getStockActuel().divide(consommationJournaliere, MathContext.DECIMAL128);
        return jours.intValue();
    }

    @Transactional
    public void mettreAJourStatut(Article article) {
        StatutStock nouveauStatut = calculerStatut(article);
        article.setStatut(nouveauStatut);
        articleRepository.save(article);
    }

    private StatutStock calculerStatut(Article article) {
        if (Boolean.FALSE.equals(article.getActif())) {
            return StatutStock.INACTIF;
        }
        if (article.getDatePeremption() != null && !LocalDate.now().isBefore(article.getDatePeremption())) {
            return StatutStock.PERIME;
        }
        if (article.getStockActuel().compareTo(BigDecimal.ZERO) == 0) {
            return StatutStock.RUPTURE;
        }
        if (article.getSeuilAlerteCritique() != null
                && article.getStockActuel().compareTo(article.getSeuilAlerteCritique()) <= 0) {
            return StatutStock.CRITIQUE;
        }
        if (article.getStockActuel().compareTo(article.getSeuilAlerteMin()) <= 0) {
            return StatutStock.FAIBLE;
        }
        return StatutStock.NORMAL;
    }

    private String genererCodeArticle(CategorieArticle categorie) {
        String prefixe = switch (categorie) {
            case ALIMENT -> "AL";
            case MEDICAMENT -> "MD";
            case VACCIN -> "VC";
            case MATERIEL -> "MT";
            case PESTICIDE -> "PC";
            case SEMENCE -> "SM";
            case AUTRE -> "AT";
        };
        long count = articleRepository.count();
        String sequence = String.format("%05d", count + 1);
        return "ART-" + prefixe + "-" + sequence;
    }

    private boolean correspondRecherche(Article article, String search) {
        if (search == null || search.isBlank()) return true;
        String recherche = search.toLowerCase(Locale.ROOT);
        return article.getDesignation().toLowerCase(Locale.ROOT).contains(recherche)
                || article.getCodeArticle().toLowerCase(Locale.ROOT).contains(recherche);
    }

    private Comparator<Article> comparateur(org.springframework.data.domain.Sort sort) {
        org.springframework.data.domain.Sort.Order ordre = sort.stream().findFirst().orElse(null);
        if (ordre == null) return Comparator.comparing(Article::getDesignation, String.CASE_INSENSITIVE_ORDER);
        Comparator<Article> comparateur = switch (ordre.getProperty()) {
            case "nom", "designation" -> Comparator.comparing(Article::getDesignation, String.CASE_INSENSITIVE_ORDER);
            case "reference", "codeArticle" -> Comparator.comparing(Article::getCodeArticle, String.CASE_INSENSITIVE_ORDER);
            case "stockActuel" -> Comparator.comparing(Article::getStockActuel);
            case "coutUnitaire", "prixUnitaireRef" -> Comparator.comparing(a -> a.getPrixUnitaireRef() == null ? BigDecimal.ZERO : a.getPrixUnitaireRef());
            default -> Comparator.comparing(Article::getDesignation, String.CASE_INSENSITIVE_ORDER);
        };
        return ordre.isAscending() ? comparateur : comparateur.reversed();
    }

    private boolean correspondStatut(Article article, String statutStock) {
        if (statutStock == null || statutStock.isBlank()) return true;
        return switch (statutStock.toLowerCase(Locale.ROOT)) {
            case "normal" -> article.getStatut() == StatutStock.NORMAL;
            case "alerte" -> article.getStatut() != StatutStock.NORMAL;
            default -> throw new IllegalArgumentException("statutStock doit etre 'normal' ou 'alerte'.");
        };
    }

    private String formatMontant(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.valueOf(1_000_000)) >= 0) return montant.divide(BigDecimal.valueOf(1_000_000), 1, java.math.RoundingMode.HALF_UP).toPlainString().replace('.', ',') + " M";
        if (montant.compareTo(BigDecimal.valueOf(1_000)) >= 0) return montant.divide(BigDecimal.valueOf(1_000), 1, java.math.RoundingMode.HALF_UP).toPlainString().replace('.', ',') + " k";
        return montant.toPlainString();
    }

    private String labelCategorie(CategorieArticle categorie) {
        return switch (categorie) {
            case ALIMENT -> "Aliment"; case MEDICAMENT -> "Médicament"; case VACCIN -> "Vaccin";
            case MATERIEL -> "Matériel"; case PESTICIDE -> "Pesticide"; case SEMENCE -> "Semence"; case AUTRE -> "Autre";
        };
    }

    private ArticleResponse toResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setCodeArticle(article.getCodeArticle());
        response.setDesignation(article.getDesignation());
        response.setDescription(article.getDescription());
        response.setCategorie(article.getCategorie());
        response.setUniteMesure(article.getUniteMesure());
        response.setFermeId(article.getFermeId());
        response.setEntrepotId(article.getEntrepotId());
        response.setEntrepotNom(article.getEntrepotNom());
        response.setStockActuel(article.getStockActuel());
        response.setStockInitial(article.getStockInitial());
        response.setSeuilAlerteMin(article.getSeuilAlerteMin());
        response.setSeuilAlerteCritique(article.getSeuilAlerteCritique());
        response.setStockMax(article.getStockMax());
        response.setStatut(article.getStatut());
        response.setPrixUnitaireRef(article.getPrixUnitaireRef());
        response.setValeurStock(article.getValeurStock());

        if (article.getStockActuel().compareTo(BigDecimal.ZERO) > 0
                && article.getStockMax() != null && article.getStockMax().compareTo(BigDecimal.ZERO) > 0) {
            response.setTauxRemplissagePct(article.getStockActuel()
                    .divide(article.getStockMax(), 4, java.math.RoundingMode.HALF_UP)
                    .doubleValue() * 100.0);
        }

        if (article.getDatePeremption() != null) {
            long jours = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), article.getDatePeremption());
            response.setJoursAvantPeremption((int) jours);
        }

        response.setDatePeremption(article.getDatePeremption());
        response.setJoursStockRestants(getJoursStockRestants(article.getId()));
        response.setDateDerniereEntree(article.getDateDerniereEntree());
        response.setDateDerniereSortie(article.getDateDerniereSortie());
        response.setDateCreation(article.getDateCreation());
        response.setDateDerniereMiseAJour(article.getDateModification());
        response.setNombreMouvements(mouvementRepository.countByArticleId(article.getId()));
        mouvementRepository.findTopByArticleIdOrderByDateMouvementDesc(article.getId()).ifPresent(m -> response.setDateDernierMouvement(m.getDateMouvement()));
        response.setEmplacementStockage(article.getEmplacementStockage());
        response.setNumeroLot(article.getNumeroLot());
        response.setNotesInternes(article.getNotesInternes());
        response.setEstPerissable(article.getEstPerissable());
        response.setEstSuiviLot(article.getEstSuiviLot());
        response.setTemperatureConservation(article.getTemperatureConservation());
        response.setDateEntree(article.getDateEntree());

        if (article.getFournisseurHabituel() != null) {
            response.setFournisseurNom(article.getFournisseurHabituel().getNom());
            Fournisseur fournisseur = article.getFournisseurHabituel();
            response.setFournisseurPrincipal(Map.of("id", fournisseur.getId(), "nom", fournisseur.getNom(), "contact", fournisseur.getTelephone() == null ? "" : fournisseur.getTelephone()));
        }

        return response;
    }

    private Long extractFermeIdFromEntrepot(Map<String, Object> entrepot) {
        Object fermeId = entrepot.get("fermeId");
        if (fermeId instanceof Number number) return number.longValue();
        Object siteObj = entrepot.get("site");
        if (siteObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> site = (Map<String, Object>) siteObj;
            Object fermeObj = site.get("ferme");
            if (fermeObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> ferme = (Map<String, Object>) fermeObj;
                Object id = ferme.get("id");
                if (id instanceof Number) {
                    return ((Number) id).longValue();
                }
            }
        }
        return null;
    }

    private Long extractSiteId(Map<String, Object> entrepot) {
        Object siteId = entrepot.get("siteId");
        if (siteId instanceof Number number) return number.longValue();
        Object siteObj = entrepot.get("site");
        if (siteObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> site = (Map<String, Object>) siteObj;
            Object id = site.get("id");
            if (id instanceof Number) {
                return ((Number) id).longValue();
            }
        }
        return null;
    }

    private String extractSiteNom(Map<String, Object> entrepot) {
        Object siteNom = entrepot.get("siteNom");
        if (siteNom instanceof String value) return value;
        Object siteObj = entrepot.get("site");
        if (siteObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> site = (Map<String, Object>) siteObj;
            return (String) site.get("nom");
        }
        return null;
    }

    private void verifierEntrepot(Long entrepotId, Long fermeId, Map<String, Object> entrepot) {
        if (entrepotId == null) return;
        if (!"ENTREPOT".equals(entrepot.get("typeStructure"))) {
            throw new IllegalArgumentException("La structure id=" + entrepotId + " n'est pas un entrepot.");
        }
        if (!"ACTIF".equals(entrepot.get("statut"))) {
            throw new IllegalArgumentException("L'entrepot id=" + entrepotId + " n'est pas actif.");
        }
        Long fermeEntrepot = extractFermeIdFromEntrepot(entrepot);
        if (fermeEntrepot == null || !fermeId.equals(fermeEntrepot)) {
            throw new IllegalArgumentException("L'entrepot id=" + entrepotId + " n'appartient pas a la ferme indiquee.");
        }
    }
}
