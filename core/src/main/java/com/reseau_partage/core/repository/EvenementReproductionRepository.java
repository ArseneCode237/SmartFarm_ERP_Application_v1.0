package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.EvenementReproduction;
import com.reseau_partage.core.entities.StatutGestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EvenementReproductionRepository extends JpaRepository<EvenementReproduction, Long> {

    List<EvenementReproduction> findByFemelleId(Long femelleId);

    @Query("SELECT e FROM EvenementReproduction e " +
           "WHERE e.statut = 'CONFIRMEE' " +
           "AND e.dateMiseBasPrevue BETWEEN :debut AND :fin")
    List<EvenementReproduction> findGestationsProchesTerme(
            @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(e) FROM EvenementReproduction e " +
           "WHERE e.femelle.id = :femelleId AND e.statut = 'MISE_BAS'")
    long countMisesBasReussies(@Param("femelleId") Long femelleId);

    List<EvenementReproduction> findByFemelleIdAndStatut(Long femelleId, StatutGestation statut);
}
