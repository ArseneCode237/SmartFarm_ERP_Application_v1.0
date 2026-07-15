package com.reseau_partage.core.repository;
import com.reseau_partage.core.entities.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface StructureRepository extends JpaRepository<Structure,Long> { List<Structure> findBySiteId(Long siteId); List<Structure> findBySiteIdAndStatut(Long siteId,StatutStructure statut); List<Structure> findBySiteFermeId(Long fermeId); long countBySiteId(Long siteId); }
