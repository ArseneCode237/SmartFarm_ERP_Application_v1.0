package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.MouvementStock;
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
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {

    Page<MouvementStock> findByArticleIdOrderByDateMouvementDesc(Long articleId, Pageable pageable);

    long countByArticleId(Long articleId);

    java.util.Optional<MouvementStock> findTopByArticleIdOrderByDateMouvementDesc(Long articleId);

    List<MouvementStock> findByFermeIdAndDateMouvementBetween(Long fermeId, LocalDate debut, LocalDate fin);

    @Query("""
        SELECT COALESCE(SUM(m.quantite), 0)
        FROM MouvementStock m
        WHERE m.article.id = :articleId
          AND m.typeMouvement IN ('SORTIE',
              'AJUSTEMENT_NEGATIF','TRANSFERT_SORTIE')
          AND m.dateMouvement BETWEEN :debut AND :fin
        """)
    BigDecimal findConsommationSurPeriode(
            @Param("articleId") Long articleId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    @Query("""
        SELECT m.dateMouvement,
               m.typeMouvement,
               SUM(m.quantite),
               MIN(m.stockApres)
        FROM MouvementStock m
        WHERE m.article.id = :articleId
        GROUP BY m.dateMouvement, m.typeMouvement
        ORDER BY m.dateMouvement ASC
        """)
    List<Object[]> findEvolutionStock(@Param("articleId") Long articleId);

    @Query("""
        SELECT COALESCE(SUM(m.montantTotal), 0)
        FROM MouvementStock m
        WHERE m.fermeId = :fermeId
          AND m.typeMouvement = 'ENTREE'
          AND m.motif = 'ACHAT'
          AND m.dateMouvement BETWEEN :debut AND :fin
        """)
    BigDecimal findDepensesAchatSurPeriode(
            @Param("fermeId") Long fermeId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    List<MouvementStock> findByBandeIdOrderByDateMouvementDesc(Long bandeId);

    List<MouvementStock> findByVaccinationId(Long vaccinationId);
}
