package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Pesee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PeseeRepository extends JpaRepository<Pesee, Long> {

    List<Pesee> findByAnimalIdOrderByDatePeseeDesc(Long animalId);

    List<Pesee> findByBandeIdOrderByDatePeseeDesc(Long bandeId);
    List<Pesee> findByBandeIdOrderByDatePeseeAsc(Long bandeId);

    Optional<Pesee> findTopByAnimalIdOrderByDatePeseeDesc(Long animalId);

    List<Pesee> findByAnimalIdOrderByDatePeseeAsc(Long animalId);

    @Query("SELECT p FROM Pesee p WHERE p.sousPerformeur = true " +
           "AND p.animal.statut = 'ACTIF' " +
           "AND p.animal.structure.site.ferme.id = :fermeId " +
           "AND p.datePesee = (" +
           "  SELECT MAX(p2.datePesee) FROM Pesee p2 " +
           "  WHERE p2.animal.id = p.animal.id)")
    List<Pesee> findSousPerformeursByFerme(@Param("fermeId") Long fermeId);
}
