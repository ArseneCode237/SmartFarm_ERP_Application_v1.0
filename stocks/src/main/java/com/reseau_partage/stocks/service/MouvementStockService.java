package com.reseau_partage.stocks.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reseau_partage.core.entities.Article;
import com.reseau_partage.core.entities.MouvementStock;
import com.reseau_partage.core.entities.enumtypes.MotifMouvement;
import com.reseau_partage.core.entities.enumtypes.StatutStock;
import com.reseau_partage.core.entities.enumtypes.TypeMouvementStock;
import com.reseau_partage.core.repository.ArticleRepository;
import com.reseau_partage.core.repository.MouvementStockRepository;
import com.reseau_partage.stocks.dto.mouvement.AjustementStockRequest;
import com.reseau_partage.stocks.dto.mouvement.MouvementRequest;
import com.reseau_partage.stocks.dto.mouvement.MouvementResponse;
import com.reseau_partage.stocks.exception.ResourceNotFoundException;
import com.reseau_partage.stocks.exception.StockInsuffisantException;

@Service
public class MouvementStockService {
    private final ArticleRepository articleRepository;
    private final MouvementStockRepository mouvementRepository;
    private final AlerteStockService alerteStockService;

    public MouvementStockService(ArticleRepository articleRepository, MouvementStockRepository mouvementRepository, AlerteStockService alerteStockService) {
        this.articleRepository = articleRepository;
        this.mouvementRepository = mouvementRepository;
        this.alerteStockService = alerteStockService;
    }

    @Transactional
    public MouvementResponse enregistrerMouvement(MouvementRequest request) {
        Article article = articleRepository.findByIdPessimisticWrite(request.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", request.getArticleId()));
        if (!Boolean.TRUE.equals(article.getActif())) throw new IllegalArgumentException("L'article est inactif.");
        if (!request.getFermeId().equals(article.getFermeId())) throw new IllegalArgumentException("L'article n'appartient pas a la ferme indiquee.");

        BigDecimal stockAvant = article.getStockActuel();
        BigDecimal stockApres = calculerStockApres(stockAvant, request);
        if (stockApres.compareTo(BigDecimal.ZERO) < 0) {
            throw new StockInsuffisantException(article.getDesignation(), request.getQuantite(), stockAvant);
        }
        if (article.getStockMax() != null && stockApres.compareTo(article.getStockMax()) > 0 && request.getTypeMouvement() == TypeMouvementStock.ENTREE) {
            throw new IllegalArgumentException("Le stock apres mouvement depasserait le maximum autorise.");
        }

        MouvementStock mouvement = new MouvementStock();
        mouvement.setArticle(article);
        mouvement.setFermeId(request.getFermeId());
        mouvement.setTypeMouvement(request.getTypeMouvement());
        mouvement.setMotif(request.getMotif());
        mouvement.setQuantite(request.getQuantite());
        mouvement.setStockAvant(stockAvant);
        mouvement.setStockApres(stockApres);
        mouvement.setPrixUnitaire(request.getCoutUnitaire());
        mouvement.setMontantTotal(request.getCoutUnitaire() == null ? null : request.getQuantite().multiply(request.getCoutUnitaire()));
        mouvement.setDateMouvement(request.getDateMouvement());
        mouvement.setFournisseurNom(request.getFournisseurNom());
        mouvement.setNumeroFacture(request.getNumeroBon());
        mouvement.setNumeroLot(request.getNumeroLot());
        mouvement.setBandeId(request.getBandeId());
        mouvement.setOperateurNom(request.getOperateur());
        mouvement.setNotes(request.getNotes());
        mouvement.setEntrepotDestinationNom(request.getDestinationOrigine());
        mouvementRepository.save(mouvement);

        article.setStockActuel(stockApres);
        if (request.getTypeMouvement() == TypeMouvementStock.ENTREE && request.getCoutUnitaire() != null) article.setPrixUnitaireRef(request.getCoutUnitaire());
        article.setValeurStock(stockApres.multiply(article.getPrixUnitaireRef() == null ? BigDecimal.ZERO : article.getPrixUnitaireRef()));
        if (request.getTypeMouvement() == TypeMouvementStock.ENTREE) article.setDateDerniereEntree(request.getDateMouvement());
        if (request.getTypeMouvement() == TypeMouvementStock.SORTIE || request.getTypeMouvement() == TypeMouvementStock.PERTE) article.setDateDerniereSortie(request.getDateMouvement());
        article.setStatut(calculerStatut(article));
        articleRepository.save(article);
        alerteStockService.synchroniserAlertesArticle(article.getId());
        return toResponse(mouvement);
    }

    @Transactional
    public MouvementResponse ajusterStockInventaire(AjustementStockRequest request) {
        Article article = articleRepository.findByIdPessimisticWrite(request.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", request.getArticleId()));
        if (!Boolean.TRUE.equals(article.getActif())) throw new IllegalArgumentException("L'article est inactif.");
        if (!request.getFermeId().equals(article.getFermeId())) throw new IllegalArgumentException("L'article n'appartient pas a la ferme indiquee.");

        BigDecimal stockAvant = article.getStockActuel();
        BigDecimal stockReel = request.getStockReelConstate();
        BigDecimal delta = stockReel.subtract(stockAvant);

        if (delta.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Aucun ecart constate : stock reel = stock theorique (" + stockAvant + ")");
        }
        if (stockReel.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le stock reel constate ne peut pas etre negatif");
        }

        TypeMouvementStock typeMouvement = delta.compareTo(BigDecimal.ZERO) > 0
                ? TypeMouvementStock.AJUSTEMENT_POSITIF
                : TypeMouvementStock.AJUSTEMENT_NEGATIF;
        BigDecimal quantiteMouvement = delta.abs();

        MouvementStock mouvement = new MouvementStock();
        mouvement.setArticle(article);
        mouvement.setFermeId(request.getFermeId());
        mouvement.setTypeMouvement(typeMouvement);
        mouvement.setMotif(MotifMouvement.INVENTAIRE);
        mouvement.setQuantite(quantiteMouvement);
        mouvement.setStockAvant(stockAvant);
        mouvement.setStockApres(stockReel);
        mouvement.setPrixUnitaire(article.getPrixUnitaireRef());
        if (article.getPrixUnitaireRef() != null) {
            mouvement.setMontantTotal(article.getPrixUnitaireRef().multiply(quantiteMouvement));
        }
        LocalDate dateMvt = request.getDateInventaire() == null ? LocalDate.now() : request.getDateInventaire();
        mouvement.setDateMouvement(dateMvt);
        mouvement.setOperateurNom(request.getOperateurNom());

        StringBuilder sb = new StringBuilder();
        sb.append("Inventaire : ecart de ").append(delta);
        if (article.getUniteMesure() != null) sb.append(" ").append(article.getUniteMesure());
        sb.append(" (stock avant: ").append(stockAvant);
        sb.append(", stock reel: ").append(stockReel).append(")");
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            sb.append(" - ").append(request.getNotes());
        }
        mouvement.setNotes(sb.toString());

        mouvement = mouvementRepository.save(mouvement);

        article.setStockActuel(stockReel);
        article.setValeurStock(stockReel.multiply(article.getPrixUnitaireRef() == null ? BigDecimal.ZERO : article.getPrixUnitaireRef()));
        article.setDateDernierInventaire(dateMvt);
        article.setStatut(calculerStatut(article));
        articleRepository.save(article);

        alerteStockService.synchroniserAlertesArticle(article.getId());
        return toResponse(mouvement);
    }

    @Transactional(readOnly = true)
    public Page<MouvementResponse> lister(Long fermeId, Long articleId, TypeMouvementStock type, LocalDate dateDebut, LocalDate dateFin, String search, Pageable pageable) {
        List<MouvementStock> resultats = mouvementRepository.findByFermeId(fermeId).stream()
                .filter(m -> articleId == null || m.getArticle().getId().equals(articleId))
                .filter(m -> type == null || m.getTypeMouvement() == type)
                .filter(m -> dateDebut == null || !m.getDateMouvement().isBefore(dateDebut))
                .filter(m -> dateFin == null || !m.getDateMouvement().isAfter(dateFin))
                .filter(m -> correspondRecherche(m, search))
                .sorted(comparateur(pageable))
                .toList();
        int start = Math.min((int) pageable.getOffset(), resultats.size());
        int end = Math.min(start + pageable.getPageSize(), resultats.size());
        return new PageImpl<>(resultats.subList(start, end).stream().map(this::toResponse).toList(), pageable, resultats.size());
    }

    @Transactional(readOnly = true)
    public Page<MouvementResponse> historiqueArticle(Long articleId, Pageable pageable) {
        return mouvementRepository.findByArticleId(articleId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MouvementStatistiques statistiques(Long fermeId, LocalDate dateDebut, LocalDate dateFin) {
        List<MouvementStock> mouvements = mouvementRepository.findByFermeId(fermeId).stream()
                .filter(m -> dateDebut == null || !m.getDateMouvement().isBefore(dateDebut))
                .filter(m -> dateFin == null || !m.getDateMouvement().isAfter(dateFin)).toList();
        return new MouvementStatistiques(mouvements);
    }

    private BigDecimal calculerStockApres(BigDecimal stockAvant, MouvementRequest request) {
        return switch (request.getTypeMouvement()) {
            case ENTREE, AJUSTEMENT_POSITIF, TRANSFERT_ENTREE -> stockAvant.add(request.getQuantite());
            case SORTIE, PERTE, AJUSTEMENT_NEGATIF, TRANSFERT_SORTIE -> stockAvant.subtract(request.getQuantite());
            case AJUSTEMENT -> request.getQuantite();
            case TRANSFERT -> stockAvant;
        };
    }

    private boolean correspondRecherche(MouvementStock m, String search) {
        if (search == null || search.isBlank()) return true;
        String s = search.toLowerCase();
        return m.getArticle().getDesignation().toLowerCase().contains(s)
                || m.getArticle().getCodeArticle().toLowerCase().contains(s)
                || (m.getNumeroFacture() != null && m.getNumeroFacture().toLowerCase().contains(s))
                || (m.getFournisseurNom() != null && m.getFournisseurNom().toLowerCase().contains(s));
    }

    private Comparator<MouvementStock> comparateur(Pageable pageable) {
        var order = pageable.getSort().stream().findFirst().orElse(null);
        Comparator<MouvementStock> comparator = Comparator.comparing(MouvementStock::getDateMouvement).thenComparing(MouvementStock::getId);
        return order != null && order.isAscending() ? comparator : comparator.reversed();
    }

    private StatutStock calculerStatut(Article article) {
        if (article.getStockActuel().compareTo(BigDecimal.ZERO) == 0) return StatutStock.RUPTURE;
        if (article.getSeuilAlerteCritique() != null && article.getStockActuel().compareTo(article.getSeuilAlerteCritique()) <= 0) return StatutStock.CRITIQUE;
        if (article.getSeuilAlerteMin() != null && article.getStockActuel().compareTo(article.getSeuilAlerteMin()) <= 0) return StatutStock.FAIBLE;
        return StatutStock.NORMAL;
    }

    private MouvementResponse toResponse(MouvementStock m) {
        MouvementResponse r = new MouvementResponse();
        r.setId(m.getId()); r.setTypeMouvement(m.getTypeMouvement()); r.setArticleId(m.getArticle().getId());
        r.setArticle(new MouvementResponse.ArticleResume(m.getArticle().getId(), m.getArticle().getDesignation(), m.getArticle().getCodeArticle()));
        r.setQuantite(m.getQuantite()); r.setUniteMesure(uniteFront(m.getArticle().getUniteMesure())); r.setDateMouvement(m.getDateMouvement()); r.setMotif(m.getMotif());
        r.setDestinationOrigine(m.getEntrepotDestinationNom()); r.setBandeId(m.getBandeId()); r.setFournisseurNom(m.getFournisseurNom());
        if (r.getFournisseurNom() == null && m.getFournisseur() != null) r.setFournisseurNom(m.getFournisseur().getNom());
        r.setNumeroBon(m.getNumeroFacture()); r.setCoutUnitaire(m.getPrixUnitaire()); r.setCoutTotal(m.getMontantTotal()); r.setNumeroLot(m.getNumeroLot());
        r.setOperateur(m.getOperateurNom()); r.setNotes(m.getNotes()); r.setFermeId(m.getFermeId()); r.setDateCreation(m.getDateCreation());
        return r;
    }

    private String uniteFront(com.reseau_partage.core.entities.enumtypes.UniteMesure unite) {
        return switch (unite) {
            case KG -> "kg";
            case LITRE -> "L";
            case UNITE -> "unite";
            case DOSE -> "dose";
            case COMPRIME -> "comprime";
            case SACHET -> "sachet";
            case BOITE -> "carton";
            case SAC -> "palette";
            case TONNE -> "kg";
        };
    }

    public static class MouvementStatistiques {
        public final long total, totalEntrees, totalSorties, totalAjustements, totalPertes;
        public final BigDecimal valeurEntrees, valeurSorties;
        MouvementStatistiques(List<MouvementStock> m) {
            total = m.size();
            totalEntrees = countMultiple(m, TypeMouvementStock.ENTREE, TypeMouvementStock.TRANSFERT_ENTREE, TypeMouvementStock.AJUSTEMENT_POSITIF);
            totalSorties = countMultiple(m, TypeMouvementStock.SORTIE, TypeMouvementStock.TRANSFERT_SORTIE, TypeMouvementStock.AJUSTEMENT_NEGATIF);
            totalAjustements = countMultiple(m, TypeMouvementStock.AJUSTEMENT, TypeMouvementStock.AJUSTEMENT_POSITIF, TypeMouvementStock.AJUSTEMENT_NEGATIF);
            totalPertes = count(m, TypeMouvementStock.PERTE);
            valeurEntrees = valueMultiple(m, TypeMouvementStock.ENTREE, TypeMouvementStock.TRANSFERT_ENTREE, TypeMouvementStock.AJUSTEMENT_POSITIF);
            valeurSorties = valueMultiple(m, TypeMouvementStock.SORTIE, TypeMouvementStock.TRANSFERT_SORTIE, TypeMouvementStock.AJUSTEMENT_NEGATIF);
        }
        private static long count(List<MouvementStock> m, TypeMouvementStock t) { return m.stream().filter(x -> x.getTypeMouvement() == t).count(); }
        private static long countMultiple(List<MouvementStock> m, TypeMouvementStock... types) {
            long c = 0;
            for (TypeMouvementStock t : types) c += count(m, t);
            return c;
        }
        private static BigDecimal value(List<MouvementStock> m, TypeMouvementStock t) { return m.stream().filter(x -> x.getTypeMouvement() == t).map(x -> x.getMontantTotal() == null ? BigDecimal.ZERO : x.getMontantTotal()).reduce(BigDecimal.ZERO, BigDecimal::add); }
        private static BigDecimal valueMultiple(List<MouvementStock> m, TypeMouvementStock... types) {
            BigDecimal s = BigDecimal.ZERO;
            for (TypeMouvementStock t : types) s = s.add(value(m, t));
            return s;
        }
        public long getTotal() { return total; } public long getTotalEntrees() { return totalEntrees; } public long getTotalSorties() { return totalSorties; } public long getTotalAjustements() { return totalAjustements; } public long getTotalPertes() { return totalPertes; } public BigDecimal getValeurEntrees() { return valeurEntrees; } public BigDecimal getValeurSorties() { return valeurSorties; }
    }
}
