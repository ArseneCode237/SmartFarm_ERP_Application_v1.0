package com.reseau_partage.stocks.exception;

import java.math.BigDecimal;

public class StockInsuffisantException extends RuntimeException {
    public StockInsuffisantException(String designation, BigDecimal demandee, BigDecimal disponible) {
        super("Stock insuffisant pour l'article '" + designation + "'. " +
                "Demande : " + demandee + ", disponible : " + disponible);
    }
}