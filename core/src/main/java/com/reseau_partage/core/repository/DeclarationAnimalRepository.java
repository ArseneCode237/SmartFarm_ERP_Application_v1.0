package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.DeclarationAnimal;
import com.reseau_partage.core.entities.StatutDeclaration;
import com.reseau_partage.core.entities.TypeDeclaration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeclarationAnimalRepository extends JpaRepository<DeclarationAnimal, Long> {

    Page<DeclarationAnimal> findByAnimalIdAndStatut(Long animalId, StatutDeclaration statut, Pageable pageable);

    @Query("SELECT d FROM DeclarationAnimal d WHERE " +
            "(:fermeId IS NULL OR d.fermeId = :fermeId) AND " +
            "(:type IS NULL OR d.type = :type) AND " +
            "(:statut IS NULL OR d.statut = :statut) AND " +
            "(:dateDebut IS NULL OR d.dateDeclaration >= :dateDebut) AND " +
            "(:dateFin IS NULL OR d.dateDeclaration <= :dateFin)")
    Page<DeclarationAnimal> findAllFiltered(
            @Param("fermeId") Long fermeId,
            @Param("type") TypeDeclaration type,
            @Param("statut") StatutDeclaration statut,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            Pageable pageable);
}
