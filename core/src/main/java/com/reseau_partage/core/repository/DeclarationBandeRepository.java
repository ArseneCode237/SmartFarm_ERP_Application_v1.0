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

    // ============================================
    // REQUÊTES EXISTANTES
    // ============================================

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

    // ============================================
    // 🔥 NOUVELLES REQUÊTES POUR LES POISSONS
    // ============================================

    /**
     * Trouve les déclarations par race de poisson
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.racePoisson = :racePoisson AND d.statut = 'ACTIF'")
    List<DeclarationBande> findByRacePoisson(@Param("racePoisson") String racePoisson);

    /**
     * Trouve les déclarations par race de poisson avec pagination
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.racePoisson = :racePoisson AND d.statut = 'ACTIF'")
    Page<DeclarationBande> findByRacePoisson(@Param("racePoisson") String racePoisson, Pageable pageable);

    /**
     * Trouve les déclarations par système d'élevage
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.systemeElevage = :systemeElevage AND d.statut = 'ACTIF'")
    List<DeclarationBande> findBySystemeElevage(@Param("systemeElevage") String systemeElevage);

    /**
     * Trouve les déclarations par type d'alimentation
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.alimentation = :alimentation AND d.statut = 'ACTIF'")
    List<DeclarationBande> findByAlimentation(@Param("alimentation") String alimentation);

    /**
     * Trouve les déclarations par plage de température d'eau
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.temperatureEau BETWEEN :tempMin AND :tempMax AND d.statut = 'ACTIF'")
    List<DeclarationBande> findByTemperatureEauBetween(@Param("tempMin") BigDecimal tempMin, @Param("tempMax") BigDecimal tempMax);

    /**
     * Trouve les déclarations par plage de pH
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.phEau BETWEEN :phMin AND :phMax AND d.statut = 'ACTIF'")
    List<DeclarationBande> findByPhEauBetween(@Param("phMin") BigDecimal phMin, @Param("phMax") BigDecimal phMax);

    /**
     * Trouve les déclarations par plage d'oxygène dissous
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.oxygeneDissous BETWEEN :o2Min AND :o2Max AND d.statut = 'ACTIF'")
    List<DeclarationBande> findByOxygeneDissousBetween(@Param("o2Min") BigDecimal o2Min, @Param("o2Max") BigDecimal o2Max);

    /**
     * Trouve les déclarations par plage de densité
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.densitePoissons BETWEEN :densiteMin AND :densiteMax AND d.statut = 'ACTIF'")
    List<DeclarationBande> findByDensitePoissonsBetween(@Param("densiteMin") BigDecimal densiteMin, @Param("densiteMax") BigDecimal densiteMax);

    /**
     * Statistiques des paramètres aquacoles par bande
     */
    @Query("SELECT d.bandeId, d.bandeNom, AVG(d.temperatureEau), AVG(d.phEau), AVG(d.oxygeneDissous), AVG(d.densitePoissons) " +
           "FROM DeclarationBande d WHERE d.bandeId = :bandeId AND d.statut = 'ACTIF' " +
           "GROUP BY d.bandeId, d.bandeNom")
    List<Object[]> findAquacultureStatsByBande(@Param("bandeId") Long bandeId);

    /**
     * Statistiques des paramètres aquacoles par ferme
     */
    @Query("SELECT d.fermeId, AVG(d.temperatureEau), AVG(d.phEau), AVG(d.oxygeneDissous), AVG(d.densitePoissons) " +
           "FROM DeclarationBande d WHERE d.fermeId = :fermeId AND d.espece = 'POISSON' AND d.statut = 'ACTIF' " +
           "GROUP BY d.fermeId")
    List<Object[]> findAquacultureStatsByFerme(@Param("fermeId") Long fermeId);

    /**
     * Distribution des races de poissons par ferme
     */
    @Query("SELECT d.racePoisson, COUNT(d) FROM DeclarationBande d " +
           "WHERE d.fermeId = :fermeId AND d.espece = 'POISSON' AND d.statut = 'ACTIF' " +
           "GROUP BY d.racePoisson ORDER BY COUNT(d) DESC")
    List<Object[]> findRaceDistributionByFerme(@Param("fermeId") Long fermeId);

    /**
     * Distribution des systèmes d'élevage par ferme
     */
    @Query("SELECT d.systemeElevage, COUNT(d) FROM DeclarationBande d " +
           "WHERE d.fermeId = :fermeId AND d.espece = 'POISSON' AND d.statut = 'ACTIF' " +
           "GROUP BY d.systemeElevage ORDER BY COUNT(d) DESC")
    List<Object[]> findSystemeElevageDistributionByFerme(@Param("fermeId") Long fermeId);

    /**
     * Recherche filtrée avec paramètres aquacoles
     */
    @Query("SELECT d FROM DeclarationBande d WHERE " +
           "(:fermeId IS NULL OR d.fermeId = :fermeId) AND " +
           "(:type IS NULL OR d.type = :type) AND " +
           "(:statut IS NULL OR d.statut = :statut) AND " +
           "(:dateDebut IS NULL OR d.dateDeclaration >= :dateDebut) AND " +
           "(:dateFin IS NULL OR d.dateDeclaration <= :dateFin) AND " +
           "(:racePoisson IS NULL OR d.racePoisson = :racePoisson) AND " +
           "(:systemeElevage IS NULL OR d.systemeElevage = :systemeElevage) AND " +
           "(:alimentation IS NULL OR d.alimentation = :alimentation) AND " +
           "(:temperatureMin IS NULL OR d.temperatureEau >= :temperatureMin) AND " +
           "(:temperatureMax IS NULL OR d.temperatureEau <= :temperatureMax) AND " +
           "(:phMin IS NULL OR d.phEau >= :phMin) AND " +
           "(:phMax IS NULL OR d.phEau <= :phMax)")
    Page<DeclarationBande> findAllWithAquacultureFilters(
            @Param("fermeId") Long fermeId,
            @Param("type") TypeDeclaration type,
            @Param("statut") StatutDeclaration statut,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("racePoisson") String racePoisson,
            @Param("systemeElevage") String systemeElevage,
            @Param("alimentation") String alimentation,
            @Param("temperatureMin") BigDecimal temperatureMin,
            @Param("temperatureMax") BigDecimal temperatureMax,
            @Param("phMin") BigDecimal phMin,
            @Param("phMax") BigDecimal phMax,
            Pageable pageable);

    /**
     * Moyenne des paramètres aquacoles par race de poisson
     */
    @Query("SELECT d.racePoisson, AVG(d.temperatureEau), AVG(d.phEau), AVG(d.oxygeneDissous), AVG(d.densitePoissons), AVG(d.tailleMoyenne) " +
           "FROM DeclarationBande d WHERE d.fermeId = :fermeId AND d.espece = 'POISSON' AND d.statut = 'ACTIF' " +
           "GROUP BY d.racePoisson")
    List<Object[]> findAverageAquacultureParamsByRace(@Param("fermeId") Long fermeId);

    /**
     * Dernières déclarations pour une bande aquacole
     */
    @Query("SELECT d FROM DeclarationBande d WHERE d.bandeId = :bandeId AND d.espece = 'POISSON' AND d.statut = 'ACTIF' ORDER BY d.dateDeclaration DESC")
    List<DeclarationBande> findLatestAquacultureDeclarationsByBande(@Param("bandeId") Long bandeId, Pageable pageable);

    /**
     * Vérifier si des paramètres aquacoles existent pour une bande
     */
    @Query("SELECT COUNT(d) > 0 FROM DeclarationBande d WHERE d.bandeId = :bandeId AND d.espece = 'POISSON' AND d.densitePoissons IS NOT NULL AND d.statut = 'ACTIF'")
    boolean hasAquacultureData(@Param("bandeId") Long bandeId);
}