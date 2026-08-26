package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Pesee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PeseeRepository extends JpaRepository<Pesee, Long> {

    List<Pesee> findByAnimalIdOrderByDatePeseeDesc(Long animalId);

    List<Pesee> findByBandeIdOrderByDatePeseeDesc(Long bandeId);
    List<Pesee> findByBandeIdOrderByDatePeseeAsc(Long bandeId);

    Optional<Pesee> findTopByAnimalIdOrderByDatePeseeDesc(Long animalId);

    List<Pesee> findByAnimalIdOrderByDatePeseeAsc(Long animalId);

    boolean existsByAnimalIdAndDatePesee(Long animalId, LocalDate datePesee);

    boolean existsByBandeIdAndDatePesee(Long bandeId, LocalDate datePesee);

    @Query("SELECT p FROM Pesee p WHERE p.sousPerformeur = true " +
           "AND p.animal.statut = 'ACTIF' " +
           "AND p.animal.structure.site.ferme.id = :fermeId " +
           "AND p.datePesee = (" +
           "  SELECT MAX(p2.datePesee) FROM Pesee p2 " +
           "  WHERE p2.animal.id = p.animal.id)")
    List<Pesee> findSousPerformeursByFerme(@Param("fermeId") Long fermeId);

    /**
     * Toutes les pesées (triées par date croissante) filtrées optionnellement
     * par ferme (via la bande ou l'animal) et/ou par bande.
     * Jointures explicites en LEFT JOIN pour ne pas exclure les pesées sans bande.
     */
    @Query("SELECT p FROM Pesee p " +
           "LEFT JOIN p.bande b " +
           "LEFT JOIN p.animal a " +
           "LEFT JOIN b.site bs " +
           "LEFT JOIN bs.ferme bf " +
           "LEFT JOIN a.structure ast " +
           "LEFT JOIN ast.site asts " +
           "LEFT JOIN asts.ferme aferme " +
           "WHERE (:bandeId IS NULL OR b.id = :bandeId) " +
           "AND (:fermeId IS NULL OR bf.id = :fermeId OR aferme.id = :fermeId) " +
           "ORDER BY p.datePesee ASC")
    List<Pesee> findForBilan(@Param("fermeId") Long fermeId, @Param("bandeId") Long bandeId);
}
