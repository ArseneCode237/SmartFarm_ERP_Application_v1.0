package com.reseau_partage.vaccination.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganisationClient {
    private final RestTemplate restTemplate;
    @Value("${services.organisation.url}") private String organisationUrl;

    public OrganisationClient(RestTemplate restTemplate) { this.restTemplate = restTemplate; }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getFerme(Long fermeId) {
        try {
            return restTemplate.getForObject(organisationUrl + "/api/organisation/fermes/" + fermeId, Map.class);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Impossible de récupérer la ferme " + fermeId + " depuis le service organisation.", exception);
        }
    }
}
