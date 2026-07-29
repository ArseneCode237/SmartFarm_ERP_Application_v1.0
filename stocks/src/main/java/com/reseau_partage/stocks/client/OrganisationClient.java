package com.reseau_partage.stocks.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class OrganisationClient {

    private final RestTemplate restTemplate;

    @Value("${services.organisation.url}")
    private String organisationUrl;

    public OrganisationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getEntrepot(Long entrepotId) {
        String url = organisationUrl + "/api/organisation/structures/" + entrepotId;
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.err.println("Impossible de contacter le service Organisation pour l'entrepot " + entrepotId + ": " + e.getMessage());
            return null;
        }
    }
}