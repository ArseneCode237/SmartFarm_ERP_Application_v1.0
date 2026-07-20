package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Bande;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.StatutBande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BandeRepository extends JpaRepository<Bande, Long> {

    Optional<Bande> findByCodeBande(String codeBande);
    boolean existsByCodeBande(String codeBande);

    List<Bande> findByStructureId(Long structureId);
    List<Bande> findByStructureSiteId(Long siteId);
    List<Bande> findByStatut(StatutBande statut);
    List<Bande> findByEspeceAndStatut(Espece espece, StatutBande statut);

    @Query("SELECT b FROM Bande b WHERE b.structure.site.ferme.id = :fermeId")
    List<Bande> findByStructureSiteFermeId(@Param("fermeId") Long fermeId);

    @Query("SELECT b FROM Bande b WHERE (:fermeId IS NULL OR b.structure.site.ferme.id = :fermeId) " +
           "AND (:siteId IS NULL OR b.structure.site.id = :siteId) " +
           "AND (:structureId IS NULL OR b.structure.id = :structureId) " +
           "AND (:espece IS NULL OR b.espece = :espece) " +
           "AND (:statut IS NULL OR b.statut = :statut)")
    Page<Bande> findFiltre(
            @Param("fermeId") Long fermeId,
            @Param("siteId") Long siteId,
            @Param("structureId") Long structureId,
            @Param("espece") Espece espece,
            @Param("statut") StatutBande statut,
            Pageable pageable);

    @Query("SELECT b FROM Bande b WHERE b.statut = 'EN_COURS' AND b.dateSortiePrevue BETWEEN :debut AND :fin")
    List<Bande> findBandesSortieProchaineEntre(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT b.espece, AVG(b.fcrCumule) FROM Bande b WHERE b.statut = 'EN_COURS' AND b.structure.site.ferme.id = :fermeId GROUP BY b.espece")
    List<Object[]> avgFcrByEspeceForFerme(@Param("fermeId") Long fermeId);
}
