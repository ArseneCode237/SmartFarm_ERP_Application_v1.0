package com.reseau_partage.stocks.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.stocks.dto.article.ArticleRequest;
import com.reseau_partage.stocks.dto.article.ArticleResponse;
import com.reseau_partage.stocks.service.ArticleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stocks/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> create(@Valid @RequestBody ArticleRequest request, Authentication authentication) {
        ArticleResponse response = articleService.creerArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public org.springframework.data.domain.Page<ArticleResponse> list(
            @RequestParam(required = false) com.reseau_partage.core.entities.enumtypes.CategorieArticle categorie,
            @RequestParam(required = false) String statutStock,
            @RequestParam(required = false) String search,
            @RequestParam Long fermeId,
            org.springframework.data.domain.Pageable pageable) {
        return articleService.listerArticles(fermeId, categorie, statutStock, search, pageable);
    }

    @GetMapping("/{id}")
    public ArticleResponse get(@PathVariable Long id) {
        return articleService.getById(id);
    }

    @PutMapping("/{id}")
    public ArticleResponse update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        ArticleResponse response = articleService.mettreAJourArticle(id, request);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        articleService.supprimerArticle(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Map<String, Object>> desactiver(@PathVariable Long id) {
        ArticleResponse response = articleService.desactiverArticle(id);
        return ResponseEntity.ok(Map.of("data", response, "message", "Article id=" + id + " desactive."));
    }

    @GetMapping("/alertes")
    public ResponseEntity<Map<String, Object>> alertes(@RequestParam Long fermeId) {
        return ResponseEntity.ok(Map.of("content", articleService.getArticlesEnAlerte(fermeId)));
    }

    @GetMapping("/peremptions")
    public ResponseEntity<Map<String, Object>> peremptions(@RequestParam Long fermeId, @RequestParam(defaultValue = "30") int jours) {
        return ResponseEntity.ok(Map.of("content", articleService.getArticlesPeremptionProche(fermeId, jours)));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(@RequestParam Long fermeId) {
        return ResponseEntity.ok(Map.of("data", articleService.getDashboardStats(fermeId)));
    }

    @GetMapping("/statistiques")
    public Map<String, Object> statistiques(@RequestParam Long fermeId) {
        return articleService.statistiques(fermeId);
    }

    @GetMapping("/categories")
    public List<Map<String, Object>> categories(@RequestParam(required = false) Long fermeId) {
        return articleService.categories(fermeId);
    }

    @GetMapping("/{id}/evolution")
    public ResponseEntity<Map<String, Object>> evolution(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("data", articleService.getEvolutionStock(id)));
    }

    @GetMapping("/{id}/prevision")
    public ResponseEntity<Map<String, Object>> prevision(@PathVariable Long id) {
        Integer jours = articleService.getJoursStockRestants(id);
        return ResponseEntity.ok(Map.of("data", Map.of("joursStockRestants", jours)));
    }
}
