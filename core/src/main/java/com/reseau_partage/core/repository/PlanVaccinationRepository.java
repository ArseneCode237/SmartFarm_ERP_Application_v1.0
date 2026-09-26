package com.reseau_partage.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.PlanVaccination;

public interface PlanVaccinationRepository extends JpaRepository<PlanVaccination, Long> {
    List<PlanVaccination> findByFermeIdAndEspece(Long fermeId, Espece espece);
    List<PlanVaccination> findByFermeIdAndActifTrue(Long fermeId);
}
