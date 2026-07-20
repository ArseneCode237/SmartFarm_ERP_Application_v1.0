package com.reseau_partage.core.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reseau_partage.core.entities.MiseBas;

public interface MiseBaRepository extends JpaRepository<MiseBas, Long> {

    List<MiseBas> findByTruieIdOrderByDateMiseBasReelleDesc(Long truieId);

    Optional<MiseBas> findBySaillieId(Long saillieId);

    Optional<MiseBas> findTopByTruieIdOrderByDateMiseBasReelleDesc(Long truieId);

    long countByTruieId(Long truieId);

    /** Sevrages à effectuer dans [debut, fin] non encore enregistrés. */
    @Query("SELECT m FROM MiseBas m " +
           "WHERE m.dateSevragePrevu BETWEEN :debut AND :fin " +
           "AND m.dateSevrageReel IS NULL")
    List<MiseBas> findSevragesAEffectuer(@Param("debut") LocalDate debut,
                                          @Param("fin")   LocalDate fin);

    /** Moyenne des nés vivants par portée pour une ferme. */
    @Query("SELECT AVG(m.nbNesVivants) FROM MiseBas m " +
           "WHERE m.truie.structure.site.ferme.id = :fermeId")
    Double avgNesVivantsByFerme(@Param("fermeId") Long fermeId);

    /** Moyenne des sevrés par portée pour une ferme. */
    @Query("SELECT AVG(m.nbSevres) FROM MiseBas m " +
           "WHERE m.truie.structure.site.ferme.id = :fermeId " +
           "AND m.nbSevres IS NOT NULL")
    Double avgSeVresByFerme(@Param("fermeId") Long fermeId);
}
