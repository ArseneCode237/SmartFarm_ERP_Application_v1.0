package com.reseau_partage.animaux.mapper;

import com.reseau_partage.animaux.dto.animal.AnimalResponse;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Enclos;
import com.reseau_partage.core.entities.Site;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnimalMapperTest {

    private final AnimalMapper mapper = new AnimalMapperImpl();

    @Test
    void shouldMapStructureAndBandeDetailsToResponse() {
        Animal animal = new Animal();
        animal.setId(10L);
        animal.setCodeUnique("SF-PO-00010");

        Enclos structure = new Enclos();
        structure.setId(77L);
        structure.setNom("Enclos A");
        Site site = new Site();
        site.setNom("Site Principal");
        structure.setSite(site);

        Bande bande = new Bande();
        bande.setId(99L);
        bande.setNom("Bande 01");

        animal.setStructure(structure);
        animal.setBande(bande);

        AnimalResponse response = mapper.toResponse(animal);

        assertEquals(77L, response.structureId());
        assertEquals("Enclos A", response.structureNom());
        assertEquals("Site Principal", response.siteNom());
        assertEquals(99L, response.bandeId());
        assertEquals("Bande 01", response.bandeNom());
    }
}
