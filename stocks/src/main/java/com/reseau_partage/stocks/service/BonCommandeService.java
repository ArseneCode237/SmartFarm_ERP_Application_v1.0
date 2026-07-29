package com.reseau_partage.stocks.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.stocks.dto.boncommande.BonCommandeRequest;
import com.reseau_partage.stocks.dto.boncommande.BonCommandeResponse;
import com.reseau_partage.stocks.dto.boncommande.LigneBonCommandeRequest;
import com.reseau_partage.stocks.exception.ResourceNotFoundException;
import com.reseau_partage.stocks.mapper.BonCommandeMapper;
import com.reseau_partage.core.entities.BonCommande;
import com.reseau_partage.core.entities.LigneBonCommande;
import com.reseau_partage.core.entities.Article;
import com.reseau_partage.core.entities.enumtypes.StatutBonCommande;
import com.reseau_partage.core.repository.ArticleRepository;
import com.reseau_partage.core.repository.BonCommandeRepository;
import com.reseau_partage.core.repository.FournisseurRepository;

@Service
public class BonCommandeService {

    private final BonCommandeRepository bonCommandeRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ArticleRepository articleRepository;
    private final BonCommandeMapper bonCommandeMapper;

    public BonCommandeService(BonCommandeRepository bonCommandeRepository, FournisseurRepository fournisseurRepository, ArticleRepository articleRepository, BonCommandeMapper bonCommandeMapper) {
        this.bonCommandeRepository = bonCommandeRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.articleRepository = articleRepository;
        this.bonCommandeMapper = bonCommandeMapper;
    }

    @Transactional
    public BonCommandeResponse creerBonCommande(BonCommandeRequest request) {
        String numeroBc = genererNumeroBonCommande();
        BonCommande bc = new BonCommande();
        bc.setNumeroBc(numeroBc);
        bc.setFermeId(request.getFermeId());
        if (request.getFournisseurId() != null) {
            com.reseau_partage.core.entities.Fournisseur fournisseur = fournisseurRepository.findById(request.getFournisseurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", request.getFournisseurId()));
            bc.setFournisseur(fournisseur);
        }
        bc.setDateCommande(request.getDateCommande() != null ? request.getDateCommande() : LocalDate.now());
        bc.setDateLivraisonPrevue(request.getDateLivraisonPrevue());
        bc.setStatut(StatutBonCommande.BROUILLON);
        bc.setNotes(request.getNotes());

        BigDecimal montantTotal = BigDecimal.ZERO;
        if (request.getLignes() != null) {
            for (LigneBonCommandeRequest ligneReq : request.getLignes()) {
                Article article = articleRepository.findById(ligneReq.getArticleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Article", ligneReq.getArticleId()));

                LigneBonCommande ligne = new LigneBonCommande();
                ligne.setBonCommande(bc);
                ligne.setArticle(article);
                ligne.setQuantiteCommandee(ligneReq.getQuantiteCommandee());
                ligne.setPrixUnitaire(ligneReq.getPrixUnitaire());
                if (ligneReq.getPrixUnitaire() != null && ligneReq.getQuantiteCommandee() != null) {
                    ligne.setMontantLigne(ligneReq.getQuantiteCommandee().multiply(ligneReq.getPrixUnitaire()));
                }
                if (ligne.getMontantLigne() != null) {
                    montantTotal = montantTotal.add(ligne.getMontantLigne());
                }
                bc.getLignes().add(ligne);
            }
        }
        bc.setMontantTotalHt(montantTotal);
        bc = bonCommandeRepository.save(bc);
        return enrichirResponse(bc);
    }

    @Transactional(readOnly = true)
    public Page<BonCommandeResponse> listerBons(Long fermeId, StatutBonCommande statut, Pageable pageable) {
        Page<BonCommande> page;
        if (fermeId != null && statut != null) {
            page = bonCommandeRepository.findByFermeIdAndStatut(fermeId, statut).stream()
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));
        } else if (fermeId != null) {
            page = bonCommandeRepository.findByFermeIdOrderByDateCommandeDesc(fermeId, pageable);
        } else {
            page = bonCommandeRepository.findAll(pageable);
        }
        return page.map(this::enrichirResponse);
    }

    @Transactional(readOnly = true)
    public BonCommandeResponse getById(Long id) {
        BonCommande bc = bonCommandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BonCommande", id));
        return enrichirResponse(bc);
    }

    @Transactional
    public BonCommandeResponse envoyerBonCommande(Long id) {
        BonCommande bc = bonCommandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BonCommande", id));
        if (bc.getStatut() != StatutBonCommande.BROUILLON) {
            throw new IllegalArgumentException("Seul un bon de commande en statut BROUILLON peut etre envoyer.");
        }
        bc.setStatut(StatutBonCommande.ENVOYE);
        bc.setDateCommande(LocalDate.now());
        bc = bonCommandeRepository.save(bc);
        return enrichirResponse(bc);
    }

    @Transactional
    public BonCommandeResponse confirmerBonCommande(Long id) {
        BonCommande bc = bonCommandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BonCommande", id));
        if (bc.getStatut() != StatutBonCommande.ENVOYE && bc.getStatut() != StatutBonCommande.PARTIELLEMENT_RECU) {
            throw new IllegalArgumentException("Seul un bon ENVOYE ou PARTIELLEMENT_RECU peut etre confirme.");
        }
        bc.setStatut(StatutBonCommande.CONFIRME);
        bc = bonCommandeRepository.save(bc);
        return enrichirResponse(bc);
    }

    @Transactional
    public BonCommandeResponse receptionnerBonCommande(Long id, List<LigneBonCommandeRequest> lignesRecues) {
        BonCommande bc = bonCommandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BonCommande", id));
        if (bc.getStatut() == StatutBonCommande.RECU || bc.getStatut() == StatutBonCommande.ANNULE) {
            throw new IllegalArgumentException("Bon de commande deja " + bc.getStatut().name());
        }

        for (LigneBonCommandeRequest ligneReq : lignesRecues) {
            LigneBonCommande ligneBC = bc.getLignes().stream()
                    .filter(l -> l.getArticle().getId().equals(ligneReq.getArticleId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("LigneBonCommande", ligneReq.getArticleId()));
            ligneBC.setQuantiteRecue(ligneReq.getQuantiteRecue());
        }

        boolean toutesRecues = true;
        for (LigneBonCommande ligne : bc.getLignes()) {
            if (ligne.getQuantiteRecue() == null || ligne.getQuantiteRecue().compareTo(ligne.getQuantiteCommandee()) < 0) {
                toutesRecues = false;
                break;
            }
        }
        bc.setStatut(toutesRecues ? StatutBonCommande.RECU : StatutBonCommande.PARTIELLEMENT_RECU);
        bc.setDateLivraisonReelle(LocalDate.now());
        bc = bonCommandeRepository.save(bc);
        return enrichirResponse(bc);
    }

    @Transactional
    public BonCommandeResponse annulerBonCommande(Long id) {
        BonCommande bc = bonCommandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BonCommande", id));
        if (bc.getStatut() == StatutBonCommande.RECU) {
            throw new IllegalArgumentException("Un bon de commande recu ne peut pas etre annule.");
        }
        bc.setStatut(StatutBonCommande.ANNULE);
        bc = bonCommandeRepository.save(bc);
        return enrichirResponse(bc);
    }

    private BonCommandeResponse enrichirResponse(BonCommande bc) {
        BonCommandeResponse resp = bonCommandeMapper.toResponse(bc);
        if (bc.getFournisseur() != null) {
            resp.setFournisseurId(bc.getFournisseur().getId());
            resp.setFournisseurNom(bc.getFournisseur().getNom());
        }
        return resp;
    }

    private String genererNumeroBonCommande() {
        long count = bonCommandeRepository.count();
        String annee = String.valueOf(LocalDate.now().getYear());
        String sequence = String.format("%04d", count + 1);
        return "BC-" + annee + "-" + sequence;
    }
}