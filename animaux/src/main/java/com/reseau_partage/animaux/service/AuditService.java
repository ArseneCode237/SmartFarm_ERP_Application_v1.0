package com.reseau_partage.animaux.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.AuditAnimal;
import com.reseau_partage.core.entities.MouvementAnimal;
import com.reseau_partage.core.entities.Pesee;
import com.reseau_partage.core.repository.AuditAnimalRepository;
import com.reseau_partage.core.repository.AnimalRepository;
import com.reseau_partage.core.repository.MouvementAnimalRepository;
import com.reseau_partage.core.repository.PeseeRepository;

@Service
public class AuditService {

    private final AuditAnimalRepository auditRepository;
    private final AnimalRepository animalRepository;
    private final MouvementAnimalRepository mouvementRepository;
    private final PeseeRepository peseeRepository;

    public AuditService(AuditAnimalRepository auditRepository, AnimalRepository animalRepository,
                        MouvementAnimalRepository mouvementRepository, PeseeRepository peseeRepository) {
        this.auditRepository = auditRepository;
        this.animalRepository = animalRepository;
        this.mouvementRepository = mouvementRepository;
        this.peseeRepository = peseeRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditAnimal> journal(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal", animalId);
        }
        return auditRepository.findByAnimalIdOrderByDateModificationDesc(animalId);
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> bilanVie(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal", animalId);
        }
        java.util.Map<String, Object> bilan = new java.util.LinkedHashMap<>();
        bilan.put("animal", animalRepository.findById(animalId).orElse(null));
        bilan.put("mouvements", mouvementRepository.findByAnimalId(animalId));
        bilan.put("pesees", peseeRepository.findByAnimalIdOrderByDatePeseeAsc(animalId));
        bilan.put("audit", auditRepository.findByAnimalIdOrderByDateModificationDesc(animalId));
        return bilan;
    }
}
