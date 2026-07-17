package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.ConfigEspece;
import com.reseau_partage.core.entities.Espece;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigEspeceRepository extends JpaRepository<ConfigEspece, Long> {
    Optional<ConfigEspece> findByEspece(Espece espece);
}
