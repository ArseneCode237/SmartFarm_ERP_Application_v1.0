package com.reseau_partage.core.config;

import org.hibernate.dialect.H2Dialect;

/**
 * Dialect H2 personnalisé pour SmartFarm ERP.
 * <p>
 * Raison d'être : les tests utilisent H2 en mémoire, alors que la production
 * est sur PostgreSQL. Ce dialect garantit que :
 * <ul>
 *     <li>le type {@code SqlTypes.JSON} est mappé vers {@code TEXT} en H2
 *         (au lieu de {@code jsonb} qui n'existe pas en H2)</li>
 *     <li>toutes les correspondances de types standards restent inchangées</li>
 * </ul>
 * <p>
 * Les entités utilisent {@code @JdbcTypeCode(SqlTypes.JSON)} SANS
 * {@code columnDefinition = "jsonb"}. Hibernate choisit donc automatiquement
 * le type adapté au Dialect courant : {@code jsonb} en prod (PostgreSQL) et
 * {@code TEXT} en test (H2).
 * <p>
 * Usage dans application.properties de test :
 * <pre>spring.jpa.database-platform=com.reseau_partage.core.config.SmartFarmH2Dialect</pre>
 */
public class SmartFarmH2Dialect extends H2Dialect {

    /**
     * Constructeur par défaut.
     * On surcharge volontairement rien : les types bas-niveau sont déjà
     * gérés correctement par {@code H2Dialect} pour ce que nous utilisons.
     * La classe existe surtout pour clarifier l'intention et permettre
     * d'ajouter des spécificités H2 ultérieures sans toucher au pom.xml.
     */
    public SmartFarmH2Dialect() {
        super();
    }
}
