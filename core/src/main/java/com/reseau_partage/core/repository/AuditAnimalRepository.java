package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.AuditAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditAnimalRepository extends JpaRepository<AuditAnimal, Long> {
    List<AuditAnimal> findByAnimalIdOrderByDateModificationDesc(Long animalId);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM AuditAnimal a WHERE a.animal.id = :animalId ORDER BY a.dateModification DESC")
    List<AuditAnimal> historique(@Param("animalId") Long animalId);
}
