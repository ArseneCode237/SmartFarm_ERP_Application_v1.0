package com.reseau_partage.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reseau_partage.core.entities.Loge;

public interface LogeRepository extends JpaRepository<Loge, Long> {
    Optional<Loge> findByCode(String code);
    boolean existsByCode(String code);
    List<Loge> findByBatimentId(Long batimentId);
    List<Loge> findByBandeId(Long bandeId);
    long countByBatimentId(Long batimentId);
}
