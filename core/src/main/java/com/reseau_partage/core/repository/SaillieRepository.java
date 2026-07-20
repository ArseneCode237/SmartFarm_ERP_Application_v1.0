package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Saillie;
import com.reseau_partage.core.entities.StatutSaillie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SaillieRepository extends JpaRepository<Saillie, Long> {

    List<Saillie> findByTruieIdOrderByDateSaillieDesc(Long truieId);

    List<Saillie> findByVerratIdOrderByDateSaillieDesc(Long verratId);

    List<Saillie> findByStatut(StatutSaillie statut);

    long countByTruieId(Long truieId);

    /** Saillies en attente d'écho depuis au moins 28 jours. */
    @Query("SELECT s FROM Saillie s " +
           "WHERE s.statut = 'EN_ATTENTE' " +
           "AND s.dateSaillie <= :dateSeuilEcho")
    List<Saillie> findSailliesEnAttenteEcho(@Param("dateSeuilEcho") LocalDate dateSeuilEcho);

    /** Nombre de saillies réussies pour un verrat (taux fertilité). */
    @Query("SELECT COUNT(s) FROM Saillie s " +
           "WHERE s.verrat.id = :verratId AND s.statut = 'CONFIRMEE'")
    long countSailliesReussiesByVerrat(@Param("verratId") Long verratId);

    /** Nombre total de saillies terminées pour un verrat (pour calcul du taux). */
    @Query("SELECT COUNT(s) FROM Saillie s " +
           "WHERE s.verrat.id = :verratId " +
           "AND s.statut IN ('CONFIRMEE','ECHEC','AVORTEMENT')")
    long countSailliesTerminesByVerrat(@Param("verratId") Long verratId);
}
