package com.reseau_partage.stocks.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reseau_partage.stocks.client.OrganisationClient;

/**
 * Expose au front du module Stocks les entrepots geres par le module Organisation.
 * Les entrepots ne sont pas copies dans Stocks : la liste est toujours lue depuis
 * sa source de verite, ce qui rend une creation visible immediatement.
 */
@RestController
@RequestMapping("/api/stocks/entrepots")
public class EntrepotController {

    private final OrganisationClient organisationClient;

    public EntrepotController(OrganisationClient organisationClient) {
        this.organisationClient = organisationClient;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listByFerme(@RequestParam Long fermeId) {
        return ResponseEntity.ok(organisationClient.getEntrepotsByFerme(fermeId));
    }
}
