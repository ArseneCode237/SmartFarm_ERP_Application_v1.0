package com.reseau_partage.core.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "fichiers_depot")
public class FichierDepot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depot_id", nullable = false)
    private Depot depot;

    @Column(nullable = false)
    private String nomOriginal;

    @Column(nullable = false)
    private String nomStockage;

    @Column(nullable = false)
    private String hashSha256;

    @Column(nullable = false)
    private String categorie;

    @Column(nullable = false)
    private Long taille;

    @Column(nullable = false)
    private String extension;

    public FichierDepot() {}

    public FichierDepot(Long id, Depot depot, String nomOriginal, String nomStockage, 
                        String hashSha256, String categorie, Long taille, String extension) {
        this.id = id;
        this.depot = depot;
        this.nomOriginal = nomOriginal;
        this.nomStockage = nomStockage;
        this.hashSha256 = hashSha256;
        this.categorie = categorie;
        this.taille = taille;
        this.extension = extension;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Depot getDepot() { return depot; }
    public void setDepot(Depot depot) { this.depot = depot; }

    public String getNomOriginal() { return nomOriginal; }
    public void setNomOriginal(String nomOriginal) { this.nomOriginal = nomOriginal; }

    public String getNomStockage() { return nomStockage; }
    public void setNomStockage(String nomStockage) { this.nomStockage = nomStockage; }

    public String getHashSha256() { return hashSha256; }
    public void setHashSha256(String hashSha256) { this.hashSha256 = hashSha256; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    // Manual Builder
    public static FichierDepotBuilder builder() { return new FichierDepotBuilder(); }

    public static class FichierDepotBuilder {
        private Long id;
        private Depot depot;
        private String nomOriginal;
        private String nomStockage;
        private String hashSha256;
        private String categorie;
        private Long taille;
        private String extension;

        public FichierDepotBuilder id(Long id) { this.id = id; return this; }
        public FichierDepotBuilder depot(Depot depot) { this.depot = depot; return this; }
        public FichierDepotBuilder nomOriginal(String nomOriginal) { this.nomOriginal = nomOriginal; return this; }
        public FichierDepotBuilder nomStockage(String nomStockage) { this.nomStockage = nomStockage; return this; }
        public FichierDepotBuilder hashSha256(String hashSha256) { this.hashSha256 = hashSha256; return this; }
        public FichierDepotBuilder categorie(String categorie) { this.categorie = categorie; return this; }
        public FichierDepotBuilder taille(Long taille) { this.taille = taille; return this; }
        public FichierDepotBuilder extension(String extension) { this.extension = extension; return this; }

        public FichierDepot build() {
            return new FichierDepot(id, depot, nomOriginal, nomStockage, hashSha256, categorie, taille, extension);
        }
    }
}
