package com.reseau_partage.stocks.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.stocks.dto.mouvement.MouvementRequest;
import com.reseau_partage.stocks.dto.mouvement.MouvementResponse;
import com.reseau_partage.stocks.exception.ResourceNotFoundException;
import com.reseau_partage.stocks.exception.StockInsuffisantException;
import com.reseau_partage.stocks.mapper.MouvementStockMapper;
import com.reseau_partage.core.entities.Article;
import com.reseau_partage.core.entities.MouvementStock;
import com.reseau_partage.core.entities.enumtypes.StatutStock;
import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;
import com.reseau_partage.core.repository.ArticleRepository;
import com.reseau_partage.core.repository.MouvementStockRepository;

@Service
public class MouvementStockService {

    private final ArticleRepository articleRepository;
    private final MouvementStockRepository mouvementRepository;
    private final MouvementStockMapper mapper;

    public MouvementStockService(ArticleRepository articleRepository, MouvementStockRepository mouvementRepository, MouvementStockMapper mapper) {
        this.articleRepository = articleRepository;
        this.mouvementRepository = mouvementRepository;
        this.mapper = mapper;
    }

    @Transactional
    public MouvementResponse enregistrerMouvement(MouvementRequest request) {
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", request.getArticleId()));

        if (!article.getActif()) {
            throw new IllegalArgumentException("L'article est inactif et ne peut pas etre modifie.");
        }

        TypeMouvementStock type = request.getTypeMouvement();
        if (estSortie(type) && request.getQuantite().compareTo(article.getStockActuel()) > 0) {
            throw new StockInsuffisantException(
                    article.getDesignation(),
                    request.getQuantite(),
                    article.getStockActuel());
        }

        BigDecimal stockAvant = article.getStockActuel();
        BigDecimal stockApres = calculerStockApres(stockAvant, request);

        if (article.getStockMax() != null && stockApres.compareTo(article.getStockMax()) > 0
                && type == TypeMouvementStock.ENTREE) {
            throw new IllegalArgumentException(
                    "Le stock apres mouvement (/" + stockApres + ") depasserait le maximum autorise (/" + article.getStockMax() + ") pour l'article '" + article.getDesignation() + "'.");
        }

        MouvementStock mvt = new MouvementStock();
        mvt.setArticle(article);
        mvt.setFermeId(article.getFermeId());
        mvt.setTypeMouvement(type);
        mvt.setMotif(request.getMotif());
        mvt.setQuantite(request.getQuantite());
        mvt.setStockAvant(stockAvant);
        mvt.setStockApres(stockApres);
        mvt.setPrixUnitaire(request.getPrixUnitaire());
        if (request.getPrixUnitaire() != null) {
            mvt.setMontantTotal(request.getQuantite().multiply(request.getPrixUnitaire()));
        }
        mvt.setDateMouvement(request.getDateMouvement());
        mvt.setOperateurNom(request.getOperateurNom());
        mvt.setNotes(request.getNotes());

        if (request.getFournisseurId() != null) {
            com.reseau_partage.core.entities.Fournisseur fournisseur = new com.reseau_partage.core.entities.Fournisseur();
            fournisseur.setId(request.getFournisseurId());
            mvt.setFournisseur(fournisseur);
        }
        mvt.setNumeroFacture(request.getNumeroFacture());
        mvt.setNumeroLot(request.getNumeroLot());
        mvt.setDatePeremptionLot(request.getDatePeremptionLot());
        mvt.setBandeId(request.getBandeId());
        mvt.setBandeNom(request.getBandeNom());
        mvt.setAnimalId(request.getAnimalId());
        mvt.setAnimalCode(request.getAnimalCode());
        mvt.setVaccinationId(request.getVaccinationId());
        mvt.setEntrepotSourceId(request.getEntrepotSourceId());
        mvt.setEntrepotDestinationId(request.getEntrepotDestinationId());
        mvt.setEntrepotDestinationNom(request.getEntrepotDestinationNom());

        mouvementRepository.save(mvt);

        article.setStockActuel(stockApres);
        if (type == TypeMouvementStock.ENTREE && request.getPrixUnitaire() != null) {
            article.setPrixUnitaireRef(request.getPrixUnitaire());
        }
        article.setValeurStock(article.getStockActuel().multiply(
                article.getPrixUnitaireRef() != null ? article.getPrixUnitaireRef() : BigDecimal.ZERO));

        if (type == TypeMouvementStock.SORTIE || type == TypeMouvementStock.AJUSTEMENT_NEGATIF || type == TypeMouvementStock.TRANSFERT_SORTIE) {
            article.setDateDerniereSortie(LocalDate.now());
        } else if (type == TypeMouvementStock.ENTREE || type == TypeMouvementStock.AJUSTEMENT_POSITIF || type == TypeMouvementStock.TRANSFERT_ENTREE) {
            article.setDateDerniereEntree(LocalDate.now());
        }

        article = articleRepository.save(article);

        recalculerEtMettreAJourStatut(article);

        return enrichirResponse(mvt);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MouvementResponse> historiqueArticle(Long articleId, org.springframework.data.domain.Pageable pageable) {
        return mouvementRepository.findByArticleIdOrderByDateMouvementDesc(articleId, pageable)
                .map(this::enrichirResponse);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MouvementResponse> mouvementsParFerme(Long fermeId, LocalDate debut, LocalDate fin, org.springframework.data.domain.Pageable pageable) {
        List<MouvementStock> mouvements;
        if (debut != null && fin != null) {
            mouvements = mouvementRepository.findByFermeIdAndDateMouvementBetween(fermeId, debut, fin);
        } else {
            mouvements = mouvementRepository.findAll();
        }
        List<MouvementStock> filtres = new ArrayList<>();
        for (MouvementStock m : mouvements) {
            if (m.getFermeId().equals(fermeId)) {
                filtres.add(m);
            }
        }
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtres.size());
        List<MouvementStock> pageContent = filtres.subList(start, end);
        List<MouvementResponse> respContent = pageContent.stream().map(this::enrichirResponse).toList();
        return new org.springframework.data.domain.PageImpl<>(respContent, pageable, filtres.size());
    }

    @Transactional(readOnly = true)
    public List<MouvementResponse> mouvementsParBande(Long bandeId) {
        return mouvementRepository.findByBandeIdOrderByDateMouvementDesc(bandeId).stream()
                .map(this::enrichirResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MouvementResponse> mouvementsParVaccination(Long vaccinationId) {
        return mouvementRepository.findByVaccinationId(vaccinationId).stream()
                .map(this::enrichirResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal depensesAchatsSurPeriode(Long fermeId, LocalDate debut, LocalDate fin) {
        return mouvementRepository.findDepensesAchatSurPeriode(fermeId, debut, fin);
    }

    private MouvementResponse enrichirResponse(MouvementStock mvt) {
        MouvementResponse resp = mapper.toResponse(mvt);
        if (mvt.getArticle() != null) {
            resp.setArticleId(mvt.getArticle().getId());
            resp.setArticleDesignation(mvt.getArticle().getDesignation());
            resp.setArticleCode(mvt.getArticle().getCodeArticle());
        }
        if (mvt.getFournisseur() != null) {
            resp.setFournisseurNom(mvt.getFournisseur().getNom());
        }
        return resp;
    }

    private BigDecimal calculerStockApres(BigDecimal stockAvant, MouvementRequest request) {
        switch (request.getTypeMouvement()) {
            case ENTREE:
            case AJUSTEMENT_POSITIF:
            case TRANSFERT_ENTREE:
                return stockAvant.add(request.getQuantite());
            case SORTIE:
            case AJUSTEMENT_NEGATIF:
            case TRANSFERT_SORTIE:
                return stockAvant.subtract(request.getQuantite());
            default:
                return stockAvant;
        }
    }

    private boolean estSortie(TypeMouvementStock type) {
        return type == TypeMouvementStock.SORTIE
                || type == TypeMouvementStock.AJUSTEMENT_NEGATIF
                || type == TypeMouvementStock.TRANSFERT_SORTIE;
    }

    private void recalculerEtMettreAJourStatut(Article article) {
        com.reseau_partage.core.entities.enumtypes.StatutStock nouveauStatut = calculerStatut(article);
        article.setStatut(nouveauStatut);
        articleRepository.save(article);
    }

    private com.reseau_partage.core.entities.enumtypes.StatutStock calculerStatut(Article article) {
        if (!article.getActif()) {
            return StatutStock.INACTIF;
        }
        if (article.getDatePeremption() != null && !LocalDate.now().isBefore(article.getDatePeremption())) {
            return StatutStock.PERIME;
        }
        if (article.getStockActuel().compareTo(BigDecimal.ZERO) == 0) {
            return StatutStock.RUPTURE;
        }
        if (article.getSeuilAlerteCritique() != null
                && article.getStockActuel().compareTo(article.getSeuilAlerteCritique()) <= 0) {
            return StatutStock.CRITIQUE;
        }
        if (article.getStockActuel().compareTo(article.getSeuilAlerteMin()) <= 0) {
            return StatutStock.FAIBLE;
        }
        return StatutStock.NORMAL;
    }
}