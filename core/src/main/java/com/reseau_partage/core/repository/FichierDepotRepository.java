package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.FichierDepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichierDepotRepository extends JpaRepository<FichierDepot, Long> {
}
