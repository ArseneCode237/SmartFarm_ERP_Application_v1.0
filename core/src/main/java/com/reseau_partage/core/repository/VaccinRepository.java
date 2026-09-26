package com.reseau_partage.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reseau_partage.core.entities.Espece;
import com.reseau_partage.core.entities.Vaccin;

public interface VaccinRepository extends JpaRepository<Vaccin, Long> {
    List<Vaccin> findByEspecesCompatiblesContaining(Espece espece);
    List<Vaccin> findByActifTrue();
}
