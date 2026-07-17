package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.CourbeCroissanceReference;
import com.reseau_partage.core.entities.Espece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourbeCroissanceReferenceRepository extends JpaRepository<CourbeCroissanceReference, Long> {

    List<CourbeCroissanceReference> findByConfigEspeceEspeceOrderByAgeJoursAsc(Espece espece);

    List<CourbeCroissanceReference> findByConfigEspeceEspeceAndRaceOrderByAgeJoursAsc(Espece espece, String race);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM CourbeCroissanceReference c " +
            "WHERE c.configEspece.espece = :espece AND c.ageJours = :ageJours " +
            "AND (c.race = :race OR c.race IS NULL) ORDER BY c.race DESC NULLS LAST")
    Optional<CourbeCroissanceReference> findReference(Espece espece, String race, @Param("ageJours") Integer ageJours);
}
