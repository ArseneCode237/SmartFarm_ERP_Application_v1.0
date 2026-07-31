package com.reseau_partage.stocks.controller;

import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;
import com.reseau_partage.stocks.dto.mouvement.MouvementRequest;
import com.reseau_partage.stocks.dto.mouvement.MouvementResponse;
import com.reseau_partage.stocks.service.MouvementStockService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stocks/mouvements")
public class MouvementStockController {
    private final MouvementStockService service;
    public MouvementStockController(MouvementStockService service) { this.service = service; }

    @GetMapping
    public org.springframework.data.domain.Page<MouvementResponse> lister(@RequestParam Long fermeId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size, @RequestParam(defaultValue = "dateMouvement,desc") String sort,
            @RequestParam(required = false) Long articleId, @RequestParam(required = false) TypeMouvementStock type,
            @RequestParam(required = false) LocalDate dateDebut, @RequestParam(required = false) LocalDate dateFin, @RequestParam(required = false) String search) {
        return service.lister(fermeId, articleId, type, dateDebut, dateFin, search, pageable(page, size, sort));
    }

    @GetMapping("/article/{articleId}")
    public org.springframework.data.domain.Page<MouvementResponse> historique(@PathVariable Long articleId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.historiqueArticle(articleId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateMouvement")));
    }

    @PostMapping
    public ResponseEntity<MouvementResponse> enregistrer(@Valid @RequestBody MouvementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enregistrerMouvement(request));
    }

    @GetMapping("/statistiques")
    public MouvementStockService.MouvementStatistiques statistiques(@RequestParam Long fermeId, @RequestParam(required = false) LocalDate dateDebut, @RequestParam(required = false) LocalDate dateFin) {
        return service.statistiques(fermeId, dateDebut, dateFin);
    }

    private Pageable pageable(int page, int size, String sort) {
        String[] fields = sort.split(",", 2); Sort.Direction direction = fields.length == 2 && "asc".equalsIgnoreCase(fields[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, fields[0]));
    }
}
