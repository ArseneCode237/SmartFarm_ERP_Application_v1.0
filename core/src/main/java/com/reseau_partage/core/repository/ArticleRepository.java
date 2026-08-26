package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Article;
import com.reseau_partage.core.entities.enumtypes.CategorieArticle;
import com.reseau_partage.core.entities.enumtypes.StatutStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByCodeArticle(String codeArticle);

    boolean existsByCodeArticle(String codeArticle);

    boolean existsByCodeArticleAndIdNot(String codeArticle, Long id);

    List<Article> findByFermeIdAndActifTrue(Long fermeId);

    List<Article> findByFermeIdAndCategorieAndActifTrue(Long fermeId, CategorieArticle categorie);

    List<Article> findByEntrepotId(Long entrepotId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Article a WHERE a.id = :id")
    Optional<Article> findByIdPessimisticWrite(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT a FROM Article a WHERE a.id = :id")
    Optional<Article> findByIdPessimisticRead(@Param("id") Long id);

    @Query("""
        SELECT a FROM Article a
        WHERE a.fermeId = :fermeId
          AND a.actif = true
          AND a.statut IN ('FAIBLE','CRITIQUE','RUPTURE','PERIME')
        ORDER BY a.statut DESC
        """)
    List<Article> findArticlesEnAlerte(@Param("fermeId") Long fermeId);

    @Query("""
        SELECT a FROM Article a
        WHERE a.fermeId = :fermeId
          AND a.actif = true
          AND a.datePeremption IS NOT NULL
          AND a.datePeremption BETWEEN :aujourd_hui
              AND :dateLimite
        ORDER BY a.datePeremption ASC
        """)
    List<Article> findArticlesPeremptionProche(
            @Param("fermeId") Long fermeId,
            @Param("aujourd_hui") LocalDate aujourd_hui,
            @Param("dateLimite") LocalDate dateLimite);

    @Query("""
        SELECT COALESCE(SUM(a.valeurStock), 0)
        FROM Article a
        WHERE a.fermeId = :fermeId
          AND a.actif = true
        """)
    BigDecimal findValeurTotaleStockByFerme(@Param("fermeId") Long fermeId);

    @Query("""
        SELECT a.categorie, COALESCE(SUM(a.valeurStock), 0)
        FROM Article a
        WHERE a.fermeId = :fermeId
          AND a.actif = true
        GROUP BY a.categorie
        """)
    List<Object[]> findValeurParCategorie(@Param("fermeId") Long fermeId);
}
