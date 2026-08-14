package com.reseau_partage.stocks.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;

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

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getEntrepotsByFerme(Long fermeId) {
        String url = organisationUrl + "/api/organisation/structures/ferme/" + fermeId + "/entrepots";
        try {
            List<?> result = restTemplate.getForObject(url, List.class);
            return result == null ? List.of() : (List<Map<String, Object>>) (List<?>) result;
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de recuperer les entrepots de la ferme " + fermeId + ".", e);
        }
    }
}
