package com.reseau_partage.animaux.service;

import com.reseau_partage.animaux.dto.declaration.DeclarationRequest;
import com.reseau_partage.animaux.dto.declaration.DeclarationResponse;
import com.reseau_partage.animaux.dto.declaration.DeclarationStatsResponse;
import com.reseau_partage.animaux.exception.QuantiteInvalideException;
import com.reseau_partage.animaux.exception.ResourceNotFoundException;
import com.reseau_partage.core.entities.*;
import com.reseau_partage.core.repository.BandeRepository;
import com.reseau_partage.core.repository.DeclarationBandeRepository;
import com.reseau_partage.core.repository.DeclarationHistoriqueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeclarationService {

    private final DeclarationBandeRepository declarationRepo;
    private final DeclarationHistoriqueRepository historiqueRepo;
    private final BandeRepository bandeRepo;

    public DeclarationService(DeclarationBandeRepository declarationRepo, DeclarationHistoriqueRepository historiqueRepo, BandeRepository bandeRepo) {
        this.declarationRepo = declarationRepo;
        this.historiqueRepo = historiqueRepo;
        this.bandeRepo = bandeRepo;
    }

    @Transactional
    public DeclarationResponse creerDeclaration(DeclarationRequest request, Long utilisateurId, String utilisateurNom) {
        Bande bande = bandeRepo.findById(request.bandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Bande non trouve avec l'identifiant: " + request.bandeId()));

        validerBandeActive(bande);
        validerQuantite(request.quantite(), bande.getEffectifActuel());
        validerTypeMotif(request.type(), request.motif());

        if (request.type() == TypeDeclaration.VENTE) {
            validerChampsVente(request);
        }

        int effectifAvant = bande.getEffectifActuel();
        int effectifApres = effectifAvant - request.quantite();

        DeclarationBande declaration = new DeclarationBande();
        declaration.setBandeId(bande.getId());
        declaration.setBandeNom(bande.getNom());
        declaration.setEspece(bande.getEspece().name());
        declaration.setFermeId(bande.getStructure().getSite().getFerme().getId());
        declaration.setUtilisateurId(utilisateurId);
        declaration.setUtilisateurNom(utilisateurNom);
        declaration.setType(request.type());
        declaration.setMotif(request.motif());
        declaration.setDateDeclaration(request.dateDeclaration());
        declaration.setQuantite(request.quantite());
        declaration.setEffectifAvantDeclaration(effectifAvant);
        declaration.setEffectifApresDeclaration(effectifApres);
        declaration.setPoidsMoyenKg(request.poidsMoyenKg());
        declaration.setPrixParKg(request.prixParKg() != null ? request.prixParKg() : false);
        declaration.setPrixUnitaire(request.prixUnitaire());
        declaration.setNomAcheteur(request.nomAcheteur());
        declaration.setTelephoneAcheteur(request.telephoneAcheteur());
        declaration.setLocaliteAcheteur(request.localiteAcheteur());
        declaration.setObservations(request.observations());
        declaration.setSource(SourceDeclaration.MANUEL);
        declaration.setStatut(StatutDeclaration.ACTIF);

        declaration = declarationRepo.save(declaration);

        mettreAJourBande(bande, request, effectifApres);

        sauvegarderHistorique(declaration, ActionHistorique.CREATION, null, utilisateurId, utilisateurNom);

        return toResponse(declaration);
    }

    @Transactional
    public DeclarationResponse annulerDeclaration(Long declarationId, String motifAnnulation, Long utilisateurId, String utilisateurNom) {
        DeclarationBande declaration = declarationRepo.findById(declarationId)
                .orElseThrow(() -> new ResourceNotFoundException("Declaration non trouve avec l'identifiant: " + declarationId));

        if (declaration.getStatut() == StatutDeclaration.ANNULE) {
            throw new IllegalArgumentException("Cette declaration est deja annulee");
        }

        Map<String, Object> anciennesValeurs = toMap(declaration);

        declaration.setStatut(StatutDeclaration.ANNULE);
        declaration.setMotifAnnulation(motifAnnulation);
        declaration.setDateAnnulation(LocalDateTime.now());
        declaration.setUtilisateurAnnulationId(utilisateurId);
        declarationRepo.save(declaration);

        Bande bande = bandeRepo.findById(declaration.getBandeId()).orElseThrow();
        annulerEffetSurBande(bande, declaration);

        sauvegarderHistorique(declaration, ActionHistorique.ANNULATION, anciennesValeurs, utilisateurId, utilisateurNom);

        return toResponse(declaration);
    }

    public Page<DeclarationResponse> listerParBande(Long bandeId, Pageable pageable) {
        return declarationRepo.findByBandeIdAndStatut(bandeId, StatutDeclaration.ACTIF, pageable).map(this::toResponse);
    }

    public DeclarationStatsResponse getStatsBande(Long bandeId) {
        Bande bande = bandeRepo.findById(bandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bande non trouve avec l'identifiant: " + bandeId));

        List<Object[]> totaux = declarationRepo.findTotauxParTypePourBande(bandeId);
        int totalMorts = 0;
        int totalVendus = 0;
        int totalReformes = 0;

        for (Object[] row : totaux) {
            TypeDeclaration type = (TypeDeclaration) row[0];
            int quantite = ((Number) row[1]).intValue();
            switch (type) {
                case MORT -> totalMorts = quantite;
                case VENTE -> totalVendus = quantite;
                case REFORME -> totalReformes = quantite;
            }
        }

        BigDecimal revenuTotal = declarationRepo.findRevenusTotauxBande(bandeId);

        List<Object[]> rawMortalite = declarationRepo.findCourbeMortalite(bandeId);
        List<DeclarationStatsResponse.PointCourbe> courbeMortalite = buildCourbeMortalite(rawMortalite, bande.getEffectifInitial());

        List<Object[]> rawEffectif = declarationRepo.findEvolutionEffectif(bandeId);
        List<DeclarationStatsResponse.PointCourbe> courbeEffectif = buildCourbeEffectif(rawEffectif, bande.getEffectifInitial());

        List<Object[]> rawMotifs = declarationRepo.findMortaliteParMotif(
                bande.getStructure().getSite().getFerme().getId(),
                bande.getDateEntree(),
                LocalDate.now()
        );
        List<DeclarationStatsResponse.PointCamembert> mortaliteParMotif = buildCamembert(rawMotifs, totalMorts);

        double tauxMort = bande.getEffectifInitial() > 0 ? (totalMorts * 100.0) / bande.getEffectifInitial() : 0.0;
        double tauxVente = bande.getEffectifInitial() > 0 ? (totalVendus * 100.0) / bande.getEffectifInitial() : 0.0;

        return new DeclarationStatsResponse(
                bandeId,
                bande.getNom(),
                bande.getEffectifInitial(),
                bande.getEffectifActuel(),
                totalMorts,
                totalVendus,
                totalReformes,
                revenuTotal,
                BigDecimal.valueOf(tauxMort).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                BigDecimal.valueOf(tauxVente).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                courbeMortalite,
                courbeEffectif,
                mortaliteParMotif
        );
    }

    public List<DeclarationHistorique> getHistorique(Long declarationId) {
        return historiqueRepo.findByDeclarationIdOrderByDateActionDesc(declarationId);
    }

    private void validerBandeActive(Bande bande) {
        if (bande.getStatut() != StatutBande.EN_COURS) {
            throw new IllegalArgumentException("Impossible de declarer sur une bande " + bande.getStatut() + ": " + bande.getNom());
        }
    }

    private void validerQuantite(int quantite, int effectifActuel) {
        if (quantite > effectifActuel) {
            throw new QuantiteInvalideException(quantite, effectifActuel);
        }
    }

    private void validerTypeMotif(TypeDeclaration type, MotifDeclaration motif) {
        Map<TypeDeclaration, List<MotifDeclaration>> regles = Map.of(
                TypeDeclaration.MORT, List.of(
                        MotifDeclaration.MALADIE,
                        MotifDeclaration.ACCIDENT,
                        MotifDeclaration.PREDATEUR,
                        MotifDeclaration.CAUSE_INCONNUE,
                        MotifDeclaration.MORT_NEE,
                        MotifDeclaration.ASPHYXIE,
                        MotifDeclaration.CHALEUR_EXCESSIVE
                ),
                TypeDeclaration.VENTE, List.of(
                        MotifDeclaration.PARTICULIER,
                        MotifDeclaration.GROSSISTE,
                        MotifDeclaration.ABATTOIR,
                        MotifDeclaration.BOUCHERIE,
                        MotifDeclaration.ELEVEUR,
                        MotifDeclaration.RESTAURATION
                ),
                TypeDeclaration.REFORME, List.of(
                        MotifDeclaration.PRODUCTIVITE,
                        MotifDeclaration.AGE,
                        MotifDeclaration.BLESSURE,
                        MotifDeclaration.MALADIE_CHRONIQUE,
                        MotifDeclaration.DEFAUT_MORPHOLOGIQUE
                )
        );

        if (!regles.get(type).contains(motif)) {
            throw new IllegalArgumentException("Motif " + motif + " incompatible avec le type " + type);
        }
    }

    private void validerChampsVente(DeclarationRequest request) {
        if (request.prixUnitaire() == null) {
            throw new IllegalArgumentException("Le prix unitaire est obligatoire pour une vente");
        }
        if (request.nomAcheteur() == null || request.nomAcheteur().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'acheteur est obligatoire pour une vente");
        }
    }

    @Transactional
    private void mettreAJourBande(Bande bande, DeclarationRequest request, int effectifApres) {
        bande.setEffectifActuel(effectifApres);
        bande.setDateDerniereDeclaration(request.dateDeclaration());

        switch (request.type()) {
            case MORT -> bande.setTotalDeclaresMorts(
                    (bande.getTotalDeclaresMorts() != null ? bande.getTotalDeclaresMorts() : 0) + request.quantite()
            );
            case VENTE -> {
                bande.setTotalDeclaresVendus(
                        (bande.getTotalDeclaresVendus() != null ? bande.getTotalDeclaresVendus() : 0) + request.quantite()
                );
                BigDecimal montantTotal = request.prixUnitaire() != null && request.quantite() != null
                        ? request.prixUnitaire().multiply(BigDecimal.valueOf(request.quantite()))
                        : BigDecimal.ZERO;
                bande.setRevenuTotalVentes(
                        (bande.getRevenuTotalVentes() != null ? bande.getRevenuTotalVentes() : BigDecimal.ZERO).add(montantTotal)
                );
            }
            case REFORME -> bande.setTotalDeclaresReformes(
                    (bande.getTotalDeclaresReformes() != null ? bande.getTotalDeclaresReformes() : 0) + request.quantite()
            );
        }

        if (effectifApres == 0) {
            bande.setStatut(StatutBande.TERMINEE);
            bande.setDateSortieReelle(request.dateDeclaration());
        }

        bandeRepo.save(bande);
    }

    @Transactional
    private void annulerEffetSurBande(Bande bande, DeclarationBande decl) {
        bande.setEffectifActuel(bande.getEffectifActuel() + decl.getQuantite());

        switch (decl.getType()) {
            case MORT -> bande.setTotalDeclaresMorts(
                    (bande.getTotalDeclaresMorts() != null ? bande.getTotalDeclaresMorts() : 0) - decl.getQuantite()
            );
            case VENTE -> {
                bande.setTotalDeclaresVendus(
                        (bande.getTotalDeclaresVendus() != null ? bande.getTotalDeclaresVendus() : 0) - decl.getQuantite()
                );
                if (decl.getMontantTotal() != null) {
                    bande.setRevenuTotalVentes(
                            (bande.getRevenuTotalVentes() != null ? bande.getRevenuTotalVentes() : BigDecimal.ZERO).subtract(decl.getMontantTotal())
                    );
                }
            }
            case REFORME -> bande.setTotalDeclaresReformes(
                    (bande.getTotalDeclaresReformes() != null ? bande.getTotalDeclaresReformes() : 0) - decl.getQuantite()
            );
        }

        if (bande.getStatut() == StatutBande.TERMINEE && bande.getEffectifActuel() > 0) {
            bande.setStatut(StatutBande.EN_COURS);
            bande.setDateSortieReelle(null);
        }

        bandeRepo.save(bande);
    }

    private void sauvegarderHistorique(DeclarationBande declaration, ActionHistorique action, Map<String, Object> anciennesValeurs, Long utilisateurId, String utilisateurNom) {
        DeclarationHistorique historique = new DeclarationHistorique();
        historique.setDeclarationId(declaration.getId());
        historique.setAction(action);
        historique.setAnciennesValeurs(anciennesValeurs);
        historique.setNouvellesValeurs(toMap(declaration));
        historique.setUtilisateurId(utilisateurId);
        historique.setUtilisateurNom(utilisateurNom);
        historiqueRepo.save(historique);
    }

    private Map<String, Object> toMap(DeclarationBande d) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", d.getType());
        map.put("motif", d.getMotif());
        map.put("quantite", d.getQuantite());
        map.put("dateDeclaration", d.getDateDeclaration());
        map.put("effectifAvant", d.getEffectifAvantDeclaration());
        map.put("effectifApres", d.getEffectifApresDeclaration());
        map.put("prixUnitaire", d.getPrixUnitaire());
        map.put("montantTotal", d.getMontantTotal());
        map.put("statut", d.getStatut());
        return map;
    }

    private List<DeclarationStatsResponse.PointCourbe> buildCourbeMortalite(List<Object[]> raw, int effectifInitial) {
        List<DeclarationStatsResponse.PointCourbe> courbe = new ArrayList<>();
        int cumul = 0;
        for (Object[] row : raw) {
            int qte = ((Number) row[1]).intValue();
            cumul += qte;
            courbe.add(new DeclarationStatsResponse.PointCourbe(
                    row[0].toString(),
                    qte,
                    effectifInitial - cumul
            ));
        }
        return courbe;
    }

    private List<DeclarationStatsResponse.PointCourbe> buildCourbeEffectif(List<Object[]> raw, int effectifInitial) {
        List<DeclarationStatsResponse.PointCourbe> courbe = new ArrayList<>();
        int effectifCourant = effectifInitial;
        for (Object[] row : raw) {
            int qte = ((Number) row[2]).intValue();
            effectifCourant -= qte;
            courbe.add(new DeclarationStatsResponse.PointCourbe(
                    row[0].toString(),
                    qte,
                    effectifCourant
            ));
        }
        return courbe;
    }

    private List<DeclarationStatsResponse.PointCamembert> buildCamembert(List<Object[]> raw, int totalMorts) {
        List<DeclarationStatsResponse.PointCamembert> camembert = new ArrayList<>();
        for (Object[] row : raw) {
            int qte = ((Number) row[1]).intValue();
            double pct = totalMorts > 0 ? (qte * 100.0) / totalMorts : 0.0;
            camembert.add(new DeclarationStatsResponse.PointCamembert(
                    row[0].toString(),
                    qte,
                    BigDecimal.valueOf(pct).setScale(1, RoundingMode.HALF_UP).doubleValue()
            ));
        }
        return camembert;
    }

    private DeclarationResponse toResponse(DeclarationBande d) {
        return new DeclarationResponse(
                d.getId(),
                d.getBandeId(),
                d.getBandeNom(),
                d.getEspece(),
                d.getFermeId(),
                d.getType(),
                d.getMotif(),
                d.getDateDeclaration(),
                d.getQuantite(),
                d.getEffectifAvantDeclaration(),
                d.getEffectifApresDeclaration(),
                d.getPoidsMoyenKg(),
                d.getPoidsTotalKg(),
                d.getPrixParKg(),
                d.getPrixUnitaire(),
                d.getMontantTotal(),
                d.getNomAcheteur(),
                d.getTelephoneAcheteur(),
                d.getLocaliteAcheteur(),
                d.getObservations(),
                d.getSource(),
                d.getStatut(),
                d.getUtilisateurNom(),
                d.getDateCreation(),
                d.getDateModification()
        );
    }
}
