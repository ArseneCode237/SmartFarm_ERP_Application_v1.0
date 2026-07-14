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
                entity.getStructureId(),
                entity.getStructureNom(),
                entity.getTypeActivite(),
                entity.getTypeService(),
                entity.getLocalisation(),
                entity.getActif(),
                entity.getDateCreation(),
                entity.getDateModification()
        );
    }

    public static Utilisateur toEntity(UtilisateurPojo pojo) {
        if (pojo == null) return null;
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(pojo.getId());
        utilisateur.setNom(pojo.getNom());
        utilisateur.setPrenom(pojo.getPrenom());
        utilisateur.setEmail(pojo.getEmail());
        utilisateur.setTelephone(pojo.getTelephone());
        utilisateur.setProfil_id(pojo.getProfil_id());
        utilisateur.setStructureId(pojo.getStructureId());
        utilisateur.setStructureNom(pojo.getStructureNom());
        utilisateur.setTypeActivite(pojo.getTypeActivite());
        utilisateur.setTypeService(pojo.getTypeService());
        utilisateur.setLocalisation(pojo.getLocalisation());
        utilisateur.setActif(pojo.getActif());
        utilisateur.setDateCreation(pojo.getDateCreation());
        utilisateur.setDateModification(pojo.getDateModification());
        return utilisateur;
    }
}
