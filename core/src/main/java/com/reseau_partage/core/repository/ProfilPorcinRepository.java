package com.reseau_partage.core.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reseau_partage.core.entities.ProfilPorcin;
import com.reseau_partage.core.entities.StatutReproductifPorcin;

public interface ProfilPorcinRepository extends JpaRepository<ProfilPorcin, Long> {

    Optional<ProfilPorcin> findByAnimalId(Long animalId);

    List<ProfilPorcin> findByStatutReproductif(StatutReproductifPorcin statut);

    /** Truies en gestation dont la mise-bas approche dans [debut, fin]. */
    @Query("SELECT p FROM ProfilPorcin p " +
           "WHERE p.dateMiseBasPrevue BETWEEN :debut AND :fin " +
           "AND p.statutReproductif = 'GESTATION'")
    List<ProfilPorcin> findMisesBasProches(@Param("debut") LocalDate debut,
                                           @Param("fin")   LocalDate fin);

    /** Truies disponibles pour la saillie (EN_CHALEUR ou EN_ATTENTE_SAILLIE) sur une ferme. */
    @Query("SELECT p FROM ProfilPorcin p " +
           "WHERE p.statutReproductif IN ('EN_CHALEUR','EN_ATTENTE_SAILLIE') " +
           "AND p.animal.structure.site.ferme.id = :fermeId")
    List<ProfilPorcin> findTruiesDisponiblesSaillie(@Param("fermeId") Long fermeId);

    /** Truies dont le retour en chaleur est attendu dans [debut, fin]. */
    @Query("SELECT p FROM ProfilPorcin p " +
           "WHERE p.dateRetourChaleurEstimee BETWEEN :debut AND :fin " +
           "AND p.statutReproductif = 'SEVRAGE'")
    List<ProfilPorcin> findRetourChaleurProche(@Param("debut") LocalDate debut,
                                               @Param("fin")   LocalDate fin);

    /** Moyenne du nombre de nés vivants par portée pour une ferme. */
    @Query("SELECT AVG(p.moyNesVivantsParPortee) FROM ProfilPorcin p " +
           "WHERE p.animal.structure.site.ferme.id = :fermeId " +
           "AND p.nbPorteesTotal > 0")
    Double avgNesVivantsParPorteeByFerme(@Param("fermeId") Long fermeId);
}
