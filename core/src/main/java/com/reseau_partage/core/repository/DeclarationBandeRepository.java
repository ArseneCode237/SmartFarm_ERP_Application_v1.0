package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.DeclarationBande;
import com.reseau_partage.core.entities.StatutDeclaration;
import com.reseau_partage.core.entities.TypeDeclaration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeclarationBandeRepository extends JpaRepository<DeclarationBande, Long> {

    Page<DeclarationBande> findByBandeIdAndStatut(Long bandeId, StatutDeclaration statut, Pageable pageable);

    List<DeclarationBande> findByBandeIdAndTypeAndStatut(Long bandeId, TypeDeclaration type, StatutDeclaration statut);

    List<DeclarationBande> findByFermeIdAndStatut(Long fermeId, StatutDeclaration statut);

    @Query("SELECT d FROM DeclarationBande d WHERE " +
           "(:fermeId IS NULL OR d.fermeId = :fermeId) AND " +
           "(:type IS NULL OR d.type = :type) AND " +
           "(:statut IS NULL OR d.statut = :statut) AND " +
           "(:dateDebut IS NULL OR d.dateDeclaration >= :dateDebut) AND " +
           "(:dateFin IS NULL OR d.dateDeclaration <= :dateFin)")
    Page<DeclarationBande> findAllFiltered(
            @Param("fermeId") Long fermeId,
            @Param("type") TypeDeclaration type,
            @Param("statut") StatutDeclaration statut,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            Pageable pageable);

    @Query("SELECT d.dateDeclaration, SUM(d.quantite) FROM DeclarationBande d WHERE d.bandeId = :bandeId AND d.type = 'MORT' AND d.statut = 'ACTIF' GROUP BY d.dateDeclaration ORDER BY d.dateDeclaration ASC")
    List<Object[]> findCourbeMortalite(@Param("bandeId") Long bandeId);

    @Query("SELECT d.dateDeclaration, d.type, SUM(d.quantite), MIN(d.effectifApresDeclaration) FROM DeclarationBande d WHERE d.bandeId = :bandeId AND d.statut = 'ACTIF' GROUP BY d.dateDeclaration, d.type ORDER BY d.dateDeclaration ASC")
    List<Object[]> findEvolutionEffectif(@Param("bandeId") Long bandeId);

    @Query("SELECT d.motif, SUM(d.quantite) FROM DeclarationBande d WHERE d.fermeId = :fermeId AND d.type = 'MORT' AND d.statut = 'ACTIF' AND d.dateDeclaration BETWEEN :debut AND :fin GROUP BY d.motif ORDER BY SUM(d.quantite) DESC")
    List<Object[]> findMortaliteParMotif(@Param("fermeId") Long fermeId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(d.montantTotal), 0) FROM DeclarationBande d WHERE d.bandeId = :bandeId AND d.type = 'VENTE' AND d.statut = 'ACTIF'")
    BigDecimal findRevenusTotauxBande(@Param("bandeId") Long bandeId);

    @Query("SELECT d.type, COALESCE(SUM(d.quantite), 0) FROM DeclarationBande d WHERE d.bandeId = :bandeId AND d.statut = 'ACTIF' GROUP BY d.type")
    List<Object[]> findTotauxParTypePourBande(@Param("bandeId") Long bandeId);

    @Query("SELECT COALESCE(SUM(d.quantite), 0) FROM DeclarationBande d WHERE d.bandeId = :bandeId AND d.type = 'MORT' AND d.statut = 'ACTIF'")
    Integer findTotalMortsPourBande(@Param("bandeId") Long bandeId);

    @Query("SELECT d.dateDeclaration, SUM(d.quantite), SUM(d.montantTotal) FROM DeclarationBande d WHERE d.fermeId = :fermeId AND d.type = 'VENTE' AND d.statut = 'ACTIF' AND d.dateDeclaration BETWEEN :debut AND :fin GROUP BY d.dateDeclaration ORDER BY d.dateDeclaration ASC")
    List<Object[]> findVentesParJour(@Param("fermeId") Long fermeId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
