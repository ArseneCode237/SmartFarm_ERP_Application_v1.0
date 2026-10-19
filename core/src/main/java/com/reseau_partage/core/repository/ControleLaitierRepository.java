package com.reseau_partage.core.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reseau_partage.core.entities.ControleLaitier;

public interface ControleLaitierRepository extends JpaRepository<ControleLaitier, Long> {

    List<ControleLaitier> findByAnimalIdOrderByDateControleAsc(Long animalId);

    List<ControleLaitier> findByAnimalIdAndNumeroLactationOrderByDateControleDesc(
            Long animalId, Integer numeroLactation);

    boolean existsByAnimalIdAndNumeroLactationAndDateControle(
            Long animalId, Integer numeroLactation, LocalDate dateControle);

    Optional<ControleLaitier> findTopByAnimalIdAndNumeroLactationOrderByDateControleDesc(
            Long animalId, Integer numeroLactation);

    @Query("""
            SELECT COALESCE(SUM(c.laitTotalJourKg), 0)
            FROM ControleLaitier c
            WHERE c.animalId = :animalId
              AND c.numeroLactation = :numeroLactation
            """)
    BigDecimal sumLactationMilk(@Param("animalId") Long animalId,
            @Param("numeroLactation") Integer numeroLactation);

    @Query("""
            SELECT COALESCE(SUM(c.laitTotalJourKg), 0)
            FROM ControleLaitier c
            WHERE c.fermeId = :fermeId
              AND c.dateControle BETWEEN :debut AND :fin
            """)
    BigDecimal sumMilkByFarmAndPeriod(@Param("fermeId") Long fermeId,
            @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
