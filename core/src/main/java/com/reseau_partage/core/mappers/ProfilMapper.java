package com.reseau_partage.core.mappers;

import com.reseau_partage.core.entities.Profil;
import com.reseau_partage.core.pojo.ProfilPojo;

public class ProfilMapper {

    public static ProfilPojo toPoJo(Profil entity) {
        if (entity == null) return null;
        return new ProfilPojo(
                entity.getId(),
                entity.getCode(),
                entity.getLibelle(),
                entity.getPermissions()
        );
    }

    public static Profil toEntity(ProfilPojo pojo) {
        if (pojo == null) return null;
        return new Profil(
                pojo.getId(),
                pojo.getCode(),
                pojo.getLibelle(),
                pojo.getPermissions()
        );
    }
}
