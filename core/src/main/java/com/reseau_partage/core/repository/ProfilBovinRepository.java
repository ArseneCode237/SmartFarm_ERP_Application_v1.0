package com.reseau_partage.core.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reseau_partage.core.entities.ProfilBovin;
import com.reseau_partage.core.entities.StatutReproductifBovin;

public interface ProfilBovinRepository extends JpaRepository<ProfilBovin, Long> {

    Optional<ProfilBovin> findByAnimalId(Long animalId);

    List<ProfilBovin> findByStatutReproductif(StatutReproductifBovin statut);

    @Query("""
            SELECT p FROM ProfilBovin p
            WHERE p.dateVelagePrevu BETWEEN :debut AND :fin
              AND p.statutReproductif IN :statuts
            """)
    List<ProfilBovin> findVelagesProches(@Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            @Param("statuts") List<StatutReproductifBovin> statuts);

    @Query("""
            SELECT p FROM ProfilBovin p
            WHERE p.dateTarissementPrevu BETWEEN :debut AND :fin
              AND p.statutReproductif = :statut
            """)
    List<ProfilBovin> findTarissementsProches(@Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            @Param("statut") StatutReproductifBovin statut);
}
