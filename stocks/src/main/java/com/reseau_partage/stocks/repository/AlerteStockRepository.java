package com.reseau_partage.stocks.repository;

import com.reseau_partage.stocks.entities.AlerteStock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlerteStockRepository extends JpaRepository<AlerteStock, Long>, JpaSpecificationExecutor<AlerteStock> {

    boolean existsByArticleIdAndTypeAlerteAndResolueFalse(Long articleId, String typeAlerte);

    @Modifying
    @Query("DELETE FROM AlerteStock a WHERE a.articleId = :articleId AND a.typeAlerte = :typeAlerte")
    int deleteByArticleIdAndTypeAlerte(@Param("articleId") Long articleId, @Param("typeAlerte") String typeAlerte);

    long countByFermeIdAndResolueFalse(Long fermeId);

    @Query("SELECT a FROM AlerteStock a WHERE a.fermeId = :fermeId AND a.resolue = false ORDER BY " +
           "CASE a.priorite WHEN 'CRITIQUE' THEN 1 WHEN 'HAUTE' THEN 2 WHEN 'NORMALE' THEN 3 ELSE 4 END, " +
           "a.articleDesignation ASC")
    List<AlerteStock> findActiveByFermeId(@Param("fermeId") Long fermeId);

    @Query("SELECT a FROM AlerteStock a WHERE a.fermeId = :fermeId ORDER BY " +
           "CASE a.priorite WHEN 'CRITIQUE' THEN 1 WHEN 'HAUTE' THEN 2 WHEN 'NORMALE' THEN 3 ELSE 4 END, " +
           "a.articleDesignation ASC")
    List<AlerteStock> findAllByFermeId(@Param("fermeId") Long fermeId);
}
