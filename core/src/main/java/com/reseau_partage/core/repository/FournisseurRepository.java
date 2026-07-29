package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Fournisseur;
import com.reseau_partage.core.entities.enumtypes.CategorieArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    List<Fournisseur> findByFermeIdAndActifTrue(Long fermeId);

    List<Fournisseur> findByFermeIdAndCategoriesFourniesContaining(Long fermeId, CategorieArticle categorie);
}