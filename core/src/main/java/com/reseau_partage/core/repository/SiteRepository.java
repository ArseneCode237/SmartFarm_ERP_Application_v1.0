package com.reseau_partage.core.repository;
import com.reseau_partage.core.entities.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SiteRepository extends JpaRepository<Site,Long> { List<Site> findByFermeId(Long fermeId); List<Site> findByFermeIdAndStatut(Long fermeId,StatutSite statut); boolean existsByNomAndFermeId(String nom,Long fermeId); }
