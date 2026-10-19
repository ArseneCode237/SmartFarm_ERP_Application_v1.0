package com.reseau_partage.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reseau_partage.core.entities.VelageBovin;

public interface VelageBovinRepository extends JpaRepository<VelageBovin, Long> {

    List<VelageBovin> findByVacheIdOrderByDateVelageReelDesc(Long vacheId);

    Optional<VelageBovin> findBySaillieId(Long saillieId);
}
