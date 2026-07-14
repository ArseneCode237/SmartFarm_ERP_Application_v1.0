package com.reseau_partage.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reseau_partage.core.entities.Depot;

@Repository
public interface DepotRepository extends JpaRepository<Depot, Long> {
    Optional<Depot> findByStructureIdAndMoisAndAnnee(String structureId, Integer mois, Integer annee);
    List<Depot> findAllByStructureId(String structureId);
    List<Depot> findByMoisAndAnnee(Integer mois, Integer annee);
}
