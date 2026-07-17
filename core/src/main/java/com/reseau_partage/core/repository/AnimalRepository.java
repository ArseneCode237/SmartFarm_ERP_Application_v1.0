package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Animal;
import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.StatutAnimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Optional<Animal> findByCodeUnique(String codeUnique);
    Optional<Animal> findByCodeRfid(String codeRfid);
    boolean existsByCodeUnique(String codeUnique);

    List<Animal> findByStructureId(Long structureId);
    List<Animal> findByBandeId(Long bandeId);
    List<Animal> findByEspeceAndStatut(Espece espece, StatutAnimal statut);
    List<Animal> findByStatut(StatutAnimal statut);

    @Query("SELECT COUNT(a) FROM Animal a WHERE a.structure.site.id = :siteId AND a.statut = 'ACTIF'")
    long countActifsBySiteId(@Param("siteId") Long siteId);

    @Query("SELECT a FROM Animal a WHERE a.statut = 'ACTIF' AND a.modeSuivi = 'INDIVIDUEL' AND (a.dateDernierePesee IS NULL OR a.dateDernierePesee < :dateSeuilPesee)")
    List<Animal> findAnimauxSansPeseeRecente(@Param("dateSeuilPesee") LocalDate dateSeuilPesee);

    @Query("SELECT a FROM Animal a WHERE a.mere.id = :parentId OR a.pere.id = :parentId")
    List<Animal> findDescendants(@Param("parentId") Long parentId);

    @Query("SELECT a.espece, COUNT(a), a.statut FROM Animal a WHERE a.structure.site.ferme.id = :fermeId GROUP BY a.espece, a.statut")
    List<Object[]> countByEspeceAndStatutForFerme(@Param("fermeId") Long fermeId);

    Page<Animal> findByEspeceAndStatutAndModeSuiviAndStructureIdAndBandeId(
            Espece espece, StatutAnimal statut, com.reseau_partage.core.entities.ModeSuivi modeSuivi,
            Long structureId, Long bandeId, Pageable pageable);
}
