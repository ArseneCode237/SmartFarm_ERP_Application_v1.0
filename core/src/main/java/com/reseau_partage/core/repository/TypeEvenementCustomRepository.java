package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.TypeEvenementCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeEvenementCustomRepository extends JpaRepository<TypeEvenementCustom, Long> {
    List<TypeEvenementCustom> findByEspeceAndActifTrue(Espece espece);
    List<TypeEvenementCustom> findByEspece(Espece espece);
}
