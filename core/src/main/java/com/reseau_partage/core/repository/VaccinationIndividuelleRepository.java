package com.reseau_partage.core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reseau_partage.core.entities.VaccinationIndividuelle;

public interface VaccinationIndividuelleRepository extends JpaRepository<VaccinationIndividuelle, Long> {
    List<VaccinationIndividuelle> findByAnimalIdOrderByDateVaccinationDesc(Long animalId);
    @Query("select v from VaccinationIndividuelle v where v.fermeId = :fermeId and v.dateProchaineRappel between :debut and :fin")
    List<VaccinationIndividuelle> findRappelsIndividuelsAVenir(@Param("fermeId") Long fermeId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
    @Query("select v from VaccinationIndividuelle v where v.animalId = :animalId order by v.dateVaccination asc")
    List<VaccinationIndividuelle> findHistoriqueComplet(@Param("animalId") Long animalId);
}
