package com.reseau_partage.animaux.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reseau_partage.animaux.dto.bovin.ControleLaitierRequest;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.ControleLaitier;
import com.reseau_partage.core.entities.Ferme;
import com.reseau_partage.core.entities.ProfilBovin;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.Site;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutReproductifBovin;
import com.reseau_partage.core.entities.Structure;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.ControleLaitierRepository;
import com.reseau_partage.core.repository.ProfilBovinRepository;

@ExtendWith(MockitoExtension.class)
class ControleLaitierServiceTest {

    @Mock private AnimalRepository animalRepository;
    @Mock private ProfilBovinRepository profilRepository;
    @Mock private ControleLaitierRepository controleRepository;
    @Mock private Structure structure;
    @Mock private Site site;
    @Mock private Ferme farm;

    private ControleLaitierService service;

    @BeforeEach
    void setUp() {
        service = new ControleLaitierService(animalRepository, profilRepository, controleRepository);
        Animal cow = new Animal();
        cow.setId(5L);
        cow.setEspece(com.reseau_partage.core.entities.Espece.BOVIN);
        cow.setSexe(Sexe.FEMELLE);
        cow.setStatut(StatutAnimal.ACTIF);
        cow.setStructure(structure);
        when(structure.getSite()).thenReturn(site);
        when(site.getFerme()).thenReturn(farm);
        when(farm.getId()).thenReturn(3L);
        when(animalRepository.findById(5L)).thenReturn(Optional.of(cow));

        ProfilBovin profile = new ProfilBovin();
        profile.setAnimal(cow);
        profile.setCategorieBovine(CategorieBovine.VACHE_LAITIERE);
        profile.setStatutReproductif(StatutReproductifBovin.LACTATION);
        profile.setNumeroLactationActuelle(2);
        profile.setDateDebutLactationActuelle(LocalDate.of(2026, 1, 1));
        profile.setProductionLaitCibleJKg(new BigDecimal("20"));
        when(profilRepository.findByAnimalId(5L)).thenReturn(Optional.of(profile));
        when(controleRepository.existsByAnimalIdAndNumeroLactationAndDateControle(
                5L, 2, LocalDate.of(2026, 1, 3))).thenReturn(false);
        when(controleRepository.sumLactationMilk(5L, 2)).thenReturn(BigDecimal.ZERO);
        lenient().when(controleRepository.findByAnimalIdAndNumeroLactationOrderByDateControleDesc(5L, 2))
                .thenReturn(java.util.List.of());
        when(controleRepository.save(any(ControleLaitier.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createSumsMilkingAmountsAndCalculatesDailyVariance() {
        var response = service.create(new ControleLaitierRequest(5L, LocalDate.of(2026, 1, 3),
                new BigDecimal("8.25"), null, new BigDecimal("9.75"), null,
                null, null, null, "agent", null));

        assertEquals(new BigDecimal("18.00"), response.laitTotalJourKg());
        assertEquals(new BigDecimal("-10.00"), response.ecartVsCiblePct());
        assertEquals(2, response.joursEnLait());
        assertEquals(new BigDecimal("18.00"), response.laitCumulLactationKg());
    }

    @Test
    void highSomaticCellCountGeneratesMastitisAlert() {
        var response = service.create(new ControleLaitierRequest(5L, LocalDate.of(2026, 1, 3),
                new BigDecimal("10"), null, null, null,
                null, null, 201, "agent", null));

        assertEquals("MAMMITE_SUSPECTEE", response.typeAlerte());
    }

    @Test
    void threeConsecutiveLowProductionDaysGenerateProductionDropAlert() {
        ControleLaitier yesterday = priorControl(LocalDate.of(2026, 1, 2));
        ControleLaitier dayBefore = priorControl(LocalDate.of(2026, 1, 1));
        when(controleRepository.findByAnimalIdAndNumeroLactationOrderByDateControleDesc(5L, 2))
                .thenReturn(java.util.List.of(yesterday, dayBefore));

        var response = service.create(new ControleLaitierRequest(5L, LocalDate.of(2026, 1, 3),
                new BigDecimal("15"), null, null, new BigDecimal("20"),
                null, null, null, "agent", null));

        assertEquals("CHUTE_PRODUCTION", response.typeAlerte());
    }

    private ControleLaitier priorControl(LocalDate date) {
        ControleLaitier control = new ControleLaitier();
        control.setDateControle(date);
        control.setLaitTotalJourKg(new BigDecimal("15"));
        control.setLaitCibleJourKg(new BigDecimal("20"));
        return control;
    }
}
