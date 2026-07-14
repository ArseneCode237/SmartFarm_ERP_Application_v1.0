package com.reseau_partage.core.pojo;

public class ProfilPojo {

    private Long id;
    private String code;
    private String libelle;
    private String permissions;

    public ProfilPojo() {
    }

    public ProfilPojo(Long id, String code, String libelle, String permissions) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.permissions = permissions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
}
