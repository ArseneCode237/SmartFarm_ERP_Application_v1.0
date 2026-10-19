package com.reseau_partage.core.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reseau_partage.core.entities.SaillieBovin;
import com.reseau_partage.core.entities.StatutSaillie;

public interface SaillieBovinRepository extends JpaRepository<SaillieBovin, Long> {

    List<SaillieBovin> findByVacheIdOrderByDateSaillieDesc(Long vacheId);

    List<SaillieBovin> findByStatutAndDateSaillieLessThanEqual(StatutSaillie statut, LocalDate date);
}
