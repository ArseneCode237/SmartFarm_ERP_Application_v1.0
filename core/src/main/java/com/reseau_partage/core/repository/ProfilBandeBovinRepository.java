package com.reseau_partage.core.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reseau_partage.core.entities.ProfilBandeBovin;
import com.reseau_partage.core.entities.TypeProductionBovin;

public interface ProfilBandeBovinRepository extends JpaRepository<ProfilBandeBovin, Long> {

    Optional<ProfilBandeBovin> findByBandeId(Long bandeId);

    List<ProfilBandeBovin> findByTypeProductionBovin(TypeProductionBovin typeProductionBovin);

    /** Profils de bandes bovines d'une ferme. */
    @Query("SELECT p FROM ProfilBandeBovin p WHERE p.bande.structure.site.ferme.id = :fermeId")
    List<ProfilBandeBovin> findByFermeId(@Param("fermeId") Long fermeId);

    /** Bandes bovines dont l'abattage est prévu dans [debut, fin]. */
    @Query("""
            SELECT p FROM ProfilBandeBovin p
            WHERE p.dateAbattagePrevue BETWEEN :debut AND :fin
              AND p.dateAbattageReelle IS NULL
            """)
    List<ProfilBandeBovin> findAbattagesPrevus(@Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
}
