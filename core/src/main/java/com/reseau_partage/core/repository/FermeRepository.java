package com.reseau_partage.core.repository;
import com.reseau_partage.core.entities.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface FermeRepository extends JpaRepository<Ferme,Long> {
    List<Ferme> findByStatut(StatutFerme statut);
    List<Ferme> findByStatutNot(StatutFerme statut);
    boolean existsByNomAndPays(String nom,String pays);
}
