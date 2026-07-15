package com.reseau_partage.core.mappers;

import com.reseau_partage.core.entities.Utilisateur;
import com.reseau_partage.core.pojo.UtilisateurPojo;

public class UtilisateurMapper {

    public static UtilisateurPojo toPojo(Utilisateur entity) {
        if (entity == null) return null;
        return new UtilisateurPojo(
                entity.getId(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getEmail(),
                entity.getTelephone(),
                entity.getProfil_id(),
                entity.getAdresse(),
                entity.getSexe(),
                entity.getActif(),
                entity.getDateCreation(),
                entity.getDateModification()
        );
    }

    public static Utilisateur toEntity(UtilisateurPojo pojo) {
        if (pojo == null) return null;
        Utilisateur u = new Utilisateur();
        u.setId(pojo.getId());
        u.setNom(pojo.getNom());
        u.setPrenom(pojo.getPrenom());
        u.setEmail(pojo.getEmail());
        u.setTelephone(pojo.getTelephone());
        u.setProfil_id(pojo.getProfil_id());
        u.setAdresse(pojo.getAdresse());
        u.setSexe(pojo.getSexe());
        u.setActif(pojo.getActif());
        u.setDateCreation(pojo.getDateCreation());
        u.setDateModification(pojo.getDateModification());
        return u;
    }
}
