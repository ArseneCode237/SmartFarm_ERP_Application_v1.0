package com.reseau_partage.stocks.dto.stats;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class StockStatsResponse {

    private BigDecimal valeurTotale;
    private Long nbArticles;
    private Long nbEnAlerte;
    private Long nbMouvements30j;
    private Map<String, BigDecimal> valeurParCategorie;
    private List<Map<String, Object>> topConsommables;

    public BigDecimal getValeurTotale() {
        return valeurTotale;
    }

    public void setValeurTotale(BigDecimal valeurTotale) {
        this.valeurTotale = valeurTotale;
    }

    public Long getNbArticles() {
        return nbArticles;
    }

    public void setNbArticles(Long nbArticles) {
        this.nbArticles = nbArticles;
    }

    public Long getNbEnAlerte() {
        return nbEnAlerte;
    }

    public void setNbEnAlerte(Long nbEnAlerte) {
        this.nbEnAlerte = nbEnAlerte;
    }

    public Long getNbMouvements30j() {
        return nbMouvements30j;
    }

    public void setNbMouvements30j(Long nbMouvements30j) {
        this.nbMouvements30j = nbMouvements30j;
    }

    public Map<String, BigDecimal> getValeurParCategorie() {
        return valeurParCategorie;
    }

    public void setValeurParCategorie(Map<String, BigDecimal> valeurParCategorie) {
        this.valeurParCategorie = valeurParCategorie;
    }

    public List<Map<String, Object>> getTopConsommables() {
        return topConsommables;
    }

    public void setTopConsommables(List<Map<String, Object>> topConsommables) {
        this.topConsommables = topConsommables;
    }
}