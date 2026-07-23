package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.DeclarationAnimalHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeclarationAnimalHistoriqueRepository extends JpaRepository<DeclarationAnimalHistorique, Long> {

    List<DeclarationAnimalHistorique> findByDeclarationAnimalIdOrderByDateActionDesc(Long declarationAnimalId);
}
