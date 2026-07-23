package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.DeclarationHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeclarationHistoriqueRepository extends JpaRepository<DeclarationHistorique, Long> {

    List<DeclarationHistorique> findByDeclarationIdOrderByDateActionDesc(Long declarationId);

    List<DeclarationHistorique> findByUtilisateurId(Long utilisateurId);
}
