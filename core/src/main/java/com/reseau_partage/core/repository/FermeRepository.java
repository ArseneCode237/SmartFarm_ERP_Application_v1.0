package com.reseau_partage.core.repository;
import java.util.List;

 import org.springframework.data.jpa.repository.JpaRepository;

 import com.reseau_partage.core.entities.Ferme;
import com.reseau_partage.core.entities.StatutFerme;
public interface FermeRepository extends JpaRepository<Ferme,Long> {
    List<Ferme> findByStatut(StatutFerme statut);
    List<Ferme> findByStatutNot(StatutFerme statut);
    List<Ferme> findByProprietaireIdAndStatutNot(Long proprietaireId, StatutFerme statut);
    boolean existsByNomAndPays(String nom, String pays);
}
