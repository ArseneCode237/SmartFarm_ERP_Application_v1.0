package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.BonCommande;
import com.reseau_partage.core.entities.enumtypes.StatutBonCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BonCommandeRepository extends JpaRepository<BonCommande, Long> {

    Optional<BonCommande> findByNumeroBc(String numeroBc);

    Page<BonCommande> findByFermeIdOrderByDateCommandeDesc(Long fermeId, Pageable pageable);

    List<BonCommande> findByFermeIdAndStatut(Long fermeId, StatutBonCommande statut);

    @Query("""
        SELECT b FROM BonCommande b
        WHERE b.fermeId = :fermeId
          AND b.statut IN ('CONFIRME','PARTIELLEMENT_RECU')
          AND b.dateLivraisonPrevue <= :dateLimite
        """)
    List<BonCommande> findBcEnRetardDeLivraison(
            @Param("fermeId") Long fermeId,
            @Param("dateLimite") LocalDate dateLimite);
}