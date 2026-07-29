package com.reseau_partage.stocks.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.stocks.dto.fournisseur.FournisseurRequest;
import com.reseau_partage.stocks.dto.fournisseur.FournisseurResponse;
import com.reseau_partage.stocks.exception.ResourceNotFoundException;
import com.reseau_partage.stocks.mapper.FournisseurMapper;
import com.reseau_partage.core.entities.Fournisseur;
import com.reseau_partage.core.repository.FournisseurRepository;

@Service
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;

    public FournisseurService(FournisseurRepository fournisseurRepository, FournisseurMapper fournisseurMapper) {
        this.fournisseurRepository = fournisseurRepository;
        this.fournisseurMapper = fournisseurMapper;
    }

    @Transactional
    public FournisseurResponse creerFournisseur(FournisseurRequest request, Long fermeId) {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setFermeId(fermeId);
        fournisseur.setNom(request.getNom());
        fournisseur.setAdresse(request.getAdresse());
        fournisseur.setVille(request.getVille());
        fournisseur.setTelephone(request.getTelephone());
        fournisseur.setEmail(request.getEmail());
        fournisseur.setPersonneContact(request.getPersonneContact());
        fournisseur.setCategoriesFournies(request.getCategoriesFournies());
        fournisseur.setNoteQualite(request.getNoteQualite());
        fournisseur.setDelaiLivraisonJours(request.getDelaiLivraisonJours());
        fournisseur.setConditionsPaiement(request.getConditionsPaiement());
        fournisseur.setActif(true);
        fournisseur = fournisseurRepository.save(fournisseur);
        return fournisseurMapper.toResponse(fournisseur);
    }

    @Transactional(readOnly = true)
    public List<FournisseurResponse> listerFournisseurs(Long fermeId) {
        return fournisseurRepository.findByFermeIdAndActifTrue(fermeId).stream()
                .map(fournisseurMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FournisseurResponse getById(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
        return fournisseurMapper.toResponse(fournisseur);
    }

    @Transactional
    public FournisseurResponse mettreAJourFournisseur(Long id, FournisseurRequest request) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
        fournisseur.setNom(request.getNom());
        fournisseur.setAdresse(request.getAdresse());
        fournisseur.setVille(request.getVille());
        fournisseur.setTelephone(request.getTelephone());
        fournisseur.setEmail(request.getEmail());
        fournisseur.setPersonneContact(request.getPersonneContact());
        fournisseur.setCategoriesFournies(request.getCategoriesFournies());
        fournisseur.setNoteQualite(request.getNoteQualite());
        fournisseur.setDelaiLivraisonJours(request.getDelaiLivraisonJours());
        fournisseur.setConditionsPaiement(request.getConditionsPaiement());
        fournisseur = fournisseurRepository.save(fournisseur);
        return fournisseurMapper.toResponse(fournisseur);
    }

    @Transactional(readOnly = true)
    public List<FournisseurResponse> getArticlesFournisseur(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
        List<FournisseurResponse> responses = new java.util.ArrayList<>();
        FournisseurResponse fr = fournisseurMapper.toResponse(fournisseur);
        responses.add(fr);
        return responses;
    }
}