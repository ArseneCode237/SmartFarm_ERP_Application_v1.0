package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.MouvementAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MouvementAnimalRepository extends JpaRepository<MouvementAnimal, Long> {

    List<MouvementAnimal> findByAnimalId(Long animalId);
    List<MouvementAnimal> findByBandeId(Long bandeId);
    List<MouvementAnimal> findByDateMouvementBetween(LocalDate debut, LocalDate fin);

    @Query("SELECT m FROM MouvementAnimal m WHERE m.typeMouvement = 'SORTIE_MORT' AND m.dateMouvement BETWEEN :debut AND :fin AND m.bande.structure.site.ferme.id = :fermeId")
    List<MouvementAnimal> findMortsByFermeAndPeriode(@Param("fermeId") Long fermeId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
