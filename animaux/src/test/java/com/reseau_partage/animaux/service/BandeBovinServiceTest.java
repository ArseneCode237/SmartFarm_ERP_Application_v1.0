package com.reseau_partage.animaux.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reseau_partage.animaux.dto.bande.BandeRequest;
import com.reseau_partage.animaux.dto.bande.BandeResponse;
import com.reseau_partage.animaux.dto.bovin.BandeBovinRequest;
import com.reseau_partage.animaux.dto.bovin.ProfilBandeBovinRequest;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.ProfilBandeBovin;
import com.reseau_partage.core.entities.TypeProductionBovin;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.ProfilBandeBovinRepository;

@ExtendWith(MockitoExtension.class)
class BandeBovinServiceTest {

    @Mock private BandeService bandeService;
    @Mock private BandeRepository bandeRepository;
    @Mock private ProfilBandeBovinRepository profilRepository;

    private BandeBovinService service;

    @BeforeEach
    void setUp() {
        service = new BandeBovinService(bandeService, bandeRepository, profilRepository);
    }

    @Test
    void creationBandeBovinePersisteLeProfil() {
        BandeResponse bande = bandeResponse(7L);
        when(bandeService.create(any(BandeRequest.class))).thenReturn(bande);
        when(bandeService.get(7L)).thenReturn(bande);

        Bande entity = new Bande();
        entity.setId(7L);
        entity.setEspece(Espece.BOVIN);
        when(bandeRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(profilRepository.save(any(ProfilBandeBovin.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.creerBandeBovine(new BandeBovinRequest(
                bandeRequest(Espece.BOVIN),
                new ProfilBandeBovinRequest(TypeProductionBovin.ENGRAISSEMENT, "Brahman",
                        new BigDecimal("450.0"), 24, 800, new BigDecimal("7.50"), 180, null, null)));

        assertNotNull(response.bande());
        assertEquals(7L, response.profil().bandeId());
        assertEquals(TypeProductionBovin.ENGRAISSEMENT, response.profil().typeProductionBovin());
        assertEquals("Brahman", response.profil().race());
        assertEquals(0, new BigDecimal("450.0").compareTo(response.profil().poidsCibleSortieKg()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.profil().alimentConsommeTotalKg()));
    }

    @Test
    void creationSansProfilRestePossible() {
        BandeResponse bande = bandeResponse(7L);
        when(bandeService.create(any(BandeRequest.class))).thenReturn(bande);
        when(bandeService.get(7L)).thenReturn(bande);

        Bande entity = new Bande();
        entity.setId(7L);
        entity.setEspece(Espece.BOVIN);
        when(bandeRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(profilRepository.save(any(ProfilBandeBovin.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.creerBandeBovine(
                new BandeBovinRequest(bandeRequest(Espece.BOVIN), null));

        assertEquals(7L, response.profil().bandeId());
        assertNull(response.profil().typeProductionBovin());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.profil().alimentConsommeTotalKg()));
    }

    @Test
    void creationRejetteUneEspeceNonBovine() {
        BandeBovinRequest request = new BandeBovinRequest(
                bandeRequest(Espece.PORC), new ProfilBandeBovinRequest(null, null, null, null, null,
                        null, null, null, null));

        assertThrows(IllegalArgumentException.class, () -> service.creerBandeBovine(request));
    }

    @Test
    void verifierBandeBovineRejetteUneBandeNonBovine() {
        Bande bande = new Bande();
        bande.setId(3L);
        bande.setEspece(Espece.PORC);
        when(bandeRepository.findById(3L)).thenReturn(Optional.of(bande));

        assertThrows(IllegalArgumentException.class, () -> service.verifierBandeBovine(3L));
    }

    @Test
    void verifierBandeBovineAccepteUneBandeBovine() {
        Bande bande = new Bande();
        bande.setId(4L);
        bande.setEspece(Espece.BOVIN);
        when(bandeRepository.findById(4L)).thenReturn(Optional.of(bande));

        service.verifierBandeBovine(4L);
    }

    @Test
    void getProfilLeveUneExceptionSiAbsent() {
        when(profilRepository.findByBandeId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getProfil(99L));
    }

    @Test
    void parFermeRetourneLesBandesBovines() {
        Bande bande = new Bande();
        bande.setId(7L);
        bande.setEspece(Espece.BOVIN);
        ProfilBandeBovin profil = new ProfilBandeBovin();
        profil.setBande(bande);
        when(profilRepository.findByFermeId(1L)).thenReturn(List.of(profil));
        when(bandeService.get(7L)).thenReturn(mock(BandeResponse.class));

        var bandes = service.parFerme(1L);

        assertEquals(1, bandes.size());
        assertEquals(7L, bandes.get(0).profil().bandeId());
    }

    @Test
    void existeProfilBandeRefleteLaPresenceEnBase() {
        when(profilRepository.findByBandeId(5L)).thenReturn(Optional.of(new ProfilBandeBovin()));
        assertTrue(service.existeProfilBande(5L));

        when(profilRepository.findByBandeId(6L)).thenReturn(Optional.empty());
        assertFalse(service.existeProfilBande(6L));
    }

    private BandeResponse bandeResponse(Long id) {
        BandeResponse response = mock(BandeResponse.class);
        when(response.id()).thenReturn(id);
        return response;
    }

    private BandeRequest bandeRequest(Espece espece) {
        return new BandeRequest("Troupeau Test", espece, null, null, null, null,
                null, 5L, null, null, null, 10, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null);
    }
}
