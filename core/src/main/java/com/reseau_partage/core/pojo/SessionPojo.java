package com.reseau_partage.core.pojo;

import java.util.Date;

public class SessionPojo {

    private Long id;
    private Long utilisateurId;
    private String tokenHash;
    private String ipAddress;
    private String userAgent;
    private Date dateCreation;
    private Date dateExpiration;

    public SessionPojo() {
    }

    public SessionPojo(Long id, Long utilisateurId, String tokenHash, String ipAddress,
                       String userAgent, Date dateCreation, Date dateExpiration) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.tokenHash = tokenHash;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(Date dateExpiration) { this.dateExpiration = dateExpiration; }
}
