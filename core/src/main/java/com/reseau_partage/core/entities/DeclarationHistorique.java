package com.reseau_partage.core.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "declarations_historique")
public class DeclarationHistorique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "declaration_id", nullable = false)
    private Long declarationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActionHistorique action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "anciennes_valeurs", columnDefinition = "jsonb")
    private Map<String, Object> anciennesValeurs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "nouvelles_valeurs", columnDefinition = "jsonb")
    private Map<String, Object> nouvellesValeurs;

    @Column(name = "utilisateur_id")
    private Long utilisateurId;

    @Column(name = "utilisateur_nom", length = 150)
    private String utilisateurNom;

    @Column(name = "date_action", nullable = false, updatable = false)
    private LocalDateTime dateAction;

    @Column(name = "ip_adresse", length = 45)
    private String ipAdresse;

    @PrePersist
    protected void onCreate() {
        this.dateAction = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDeclarationId() { return declarationId; }
    public void setDeclarationId(Long declarationId) { this.declarationId = declarationId; }
    public ActionHistorique getAction() { return action; }
    public void setAction(ActionHistorique action) { this.action = action; }
    public Map<String, Object> getAnciennesValeurs() { return anciennesValeurs; }
    public void setAnciennesValeurs(Map<String, Object> anciennesValeurs) { this.anciennesValeurs = anciennesValeurs; }
    public Map<String, Object> getNouvellesValeurs() { return nouvellesValeurs; }
    public void setNouvellesValeurs(Map<String, Object> nouvellesValeurs) { this.nouvellesValeurs = nouvellesValeurs; }
    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getUtilisateurNom() { return utilisateurNom; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }
    public LocalDateTime getDateAction() { return dateAction; }
    public String getIpAdresse() { return ipAdresse; }
    public void setIpAdresse(String ipAdresse) { this.ipAdresse = ipAdresse; }
}
