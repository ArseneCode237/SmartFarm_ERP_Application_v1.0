package com.reseau_partage.animaux.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.reseau_partage.core.repository.ControleLaitierRepository;
import com.reseau_partage.core.repository.ProfilBovinRepository;
import com.reseau_partage.core.repository.SaillieBovinRepository;
import com.reseau_partage.core.repository.VelageBovinRepository;

@DataJpaTest
class BovinRepositoryTest {

    @Autowired private ProfilBovinRepository profilRepository;
    @Autowired private SaillieBovinRepository saillieRepository;
    @Autowired private VelageBovinRepository velageRepository;
    @Autowired private ControleLaitierRepository controleRepository;

    @Test
    void bovineRepositoriesAndQueriesInitialize() {
        assertNotNull(profilRepository);
        assertNotNull(saillieRepository);
        assertNotNull(velageRepository);
        assertNotNull(controleRepository);
    }
}
