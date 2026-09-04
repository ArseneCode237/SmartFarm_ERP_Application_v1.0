package com.reseau_partage.vaccination.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AnimauxClient {
    private final RestTemplate restTemplate;
    @Value("${services.animaux.url}") private String animauxUrl;

    public AnimauxClient(RestTemplate restTemplate) { this.restTemplate = restTemplate; }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getBande(Long bandeId) {
        return getData("/api/animaux/bandes/" + bandeId, "Bande", bandeId);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getAnimal(Long animalId) {
        return getData("/api/animaux/animaux/" + animalId, "Animal", animalId);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getData(String path, String resource, Long id) {
        try {
            Map<String, Object> response = restTemplate.getForObject(animauxUrl + path, Map.class);
            Object data = response == null ? null : response.get("data");
            if (data instanceof Map<?, ?> map) return (Map<String, Object>) map;
            throw new IllegalStateException(resource + " introuvable avec l'identifiant: " + id);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Impossible de récupérer " + resource + " " + id + " depuis le service animaux.", exception);
        }
    }
}
