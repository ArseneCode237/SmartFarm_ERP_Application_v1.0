package com.reseau_partage.core.repository;

import com.reseau_partage.core.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByTokenHash(String tokenHash);

    List<Session> findAllByUtilisateurId(Long utilisateurId);

    Optional<Session> findByIdAndActiveTrue(Long id);

    void deleteByTokenHash(String tokenHash);
}
