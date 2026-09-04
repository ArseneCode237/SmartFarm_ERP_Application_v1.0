package com.reseau_partage.core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reseau_partage.core.entities.VaccinationBande;

public interface VaccinationBandeRepository extends JpaRepository<VaccinationBande, Long> {
    List<VaccinationBande> findByBandeIdOrderByDateVaccinationDesc(Long bandeId);
    List<VaccinationBande> findByStatutAndDateVaccinationBefore(com.reseau_partage.core.entities.enumtypes.StatutVaccination statut, LocalDate date);
    @Query("select v from VaccinationBande v where v.fermeId = :fermeId and v.dateProchaineRappel between :debut and :fin and v.statut = com.reseau_partage.core.entities.enumtypes.StatutVaccination.EFFECTUEE")
    List<VaccinationBande> findRappelsAVenir(@Param("fermeId") Long fermeId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
    @Query("select v from VaccinationBande v where v.bandeId = :bandeId and v.dateFinDelaiAttente >= :today")
    List<VaccinationBande> findDelaisAttenteActifs(@Param("bandeId") Long bandeId, @Param("today") LocalDate today);
    List<VaccinationBande> findByFermeIdAndStatut(Long fermeId, com.reseau_partage.core.entities.enumtypes.StatutVaccination statut);
}
