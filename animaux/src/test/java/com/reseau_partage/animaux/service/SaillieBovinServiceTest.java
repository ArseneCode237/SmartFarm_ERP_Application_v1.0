package com.reseau_partage.animaux.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reseau_partage.animaux.dto.bovin.SaillieBovinConfirmationRequest;
import com.reseau_partage.animaux.dto.bovin.SaillieBovinRequest;
import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.CategorieBovine;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ProfilBovin;
import com.reseau_partage.core.entities.SaillieBovin;
import com.reseau_partage.core.entities.Sexe;
import com.reseau_partage.core.entities.StatutAnimal;
import com.reseau_partage.core.entities.StatutReproductifBovin;
import com.reseau_partage.core.entities.StatutSaillie;
import com.reseau_partage.core.entities.TypeSaillie;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.ProfilBovinRepository;
import com.reseau_partage.core.repository.SaillieBovinRepository;

@ExtendWith(MockitoExtension.class)
class SaillieBovinServiceTest {

    @Mock private AnimalRepository animalRepository;
    @Mock private ProfilBovinRepository profilRepository;
    @Mock private SaillieBovinRepository saillieRepository;

    private SaillieBovinService service;
    private Animal cow;
    private ProfilBovin profile;

    @BeforeEach
    void setUp() {
        service = new SaillieBovinService(animalRepository, profilRepository, saillieRepository);
        cow = new Animal();
        cow.setId(12L);
        cow.setEspece(Espece.BOVIN);
        cow.setSexe(Sexe.FEMELLE);
        cow.setStatut(StatutAnimal.ACTIF);
        profile = new ProfilBovin();
        profile.setAnimal(cow);
        profile.setCategorieBovine(CategorieBovine.VACHE_LAITIERE);
        profile.setStatutReproductif(StatutReproductifBovin.VIDE);
        lenient().when(animalRepository.findById(12L)).thenReturn(Optional.of(cow));
        lenient().when(profilRepository.findByAnimalId(12L)).thenReturn(Optional.of(profile));
    }

    @Test
    void createCalculatesBovinePregnancyAndDryOffDates() {
        when(saillieRepository.save(any(SaillieBovin.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(new SaillieBovinRequest(12L, null,
                TypeSaillie.INSEMINATION_ARTIFICIELLE, LocalDate.of(2026, 1, 1),
                null, "IA-1", "agent", null));

        assertEquals(LocalDate.of(2026, 10, 8), response.dateVelagePrevu());
        assertEquals(LocalDate.of(2026, 9, 17), response.datePreVelagePrevue());
        assertEquals(LocalDate.of(2026, 8, 9), response.dateTarissementPrevue());
        assertEquals(StatutReproductifBovin.SAILLIE, profile.getStatutReproductif());
    }

    @Test
    void confirmationWithinEchoWindowStartsGestation() {
        SaillieBovin saillie = new SaillieBovin();
        saillie.setVache(cow);
        saillie.setDateSaillie(LocalDate.of(2026, 1, 1));
        saillie.setDateVelagePrevu(LocalDate.of(2026, 10, 8));
        saillie.setDateTarissementPrevue(LocalDate.of(2026, 8, 9));
        saillie.setDatePreVelagePrevue(LocalDate.of(2026, 9, 17));
        when(saillieRepository.findById(8L)).thenReturn(Optional.of(saillie));

        var response = service.confirmer(8L,
                new SaillieBovinConfirmationRequest(true, LocalDate.of(2026, 2, 5), null));

        assertEquals(StatutSaillie.CONFIRMEE, response.statut());
        assertEquals(StatutReproductifBovin.GESTATION, profile.getStatutReproductif());
        assertEquals(LocalDate.of(2026, 10, 8), profile.getDateVelagePrevu());
    }

    @Test
    void failedMatingDuringLactationRestoresLactationState() {
        profile.setStatutReproductif(StatutReproductifBovin.LACTATION);
        when(saillieRepository.save(any(SaillieBovin.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        SaillieBovin saillie = new SaillieBovin();
        saillie.setVache(cow);
        saillie.setDateSaillie(LocalDate.of(2026, 1, 1));
        saillie.setStatutReproductifAvantSaillie(StatutReproductifBovin.LACTATION);
        when(saillieRepository.findById(8L)).thenReturn(Optional.of(saillie));

        service.create(new SaillieBovinRequest(12L, null, TypeSaillie.INSEMINATION_ARTIFICIELLE,
                LocalDate.of(2026, 1, 1), null, null, null, null));
        service.confirmer(8L, new SaillieBovinConfirmationRequest(
                false, LocalDate.of(2026, 2, 5), "Échec de gestation"));

        assertEquals(StatutReproductifBovin.LACTATION, profile.getStatutReproductif());
    }

    @Test
    void confirmationBeforeDayThirtyFiveIsRejected() {
        SaillieBovin saillie = new SaillieBovin();
        saillie.setVache(cow);
        saillie.setDateSaillie(LocalDate.of(2026, 1, 1));
        when(saillieRepository.findById(8L)).thenReturn(Optional.of(saillie));

        assertThrows(IllegalArgumentException.class, () -> service.confirmer(8L,
                new SaillieBovinConfirmationRequest(true, LocalDate.of(2026, 2, 4), null)));

        assertEquals(StatutSaillie.EN_ATTENTE, saillie.getStatut());
    }
}
