package com.reseau_partage.core.mappers;

import com.reseau_partage.core.entities.Session;
import com.reseau_partage.core.pojo.SessionPojo;

public class SessionMapper {

    public static SessionPojo toPojo(Session entity) {
        if (entity == null) return null;
        return new SessionPojo(
                entity.getId(),
                entity.getUtilisateurId(),
                entity.getTokenHash(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getDateCreation(),
                entity.getDateExpiration()
        );
    }

    public static Session toEntity(SessionPojo pojo) {
        if (pojo == null) return null;
        return new Session(
                pojo.getId(),
                pojo.getUtilisateurId(),
                pojo.getTokenHash(),
                pojo.getIpAddress(),
                pojo.getUserAgent(),
                pojo.getDateCreation(),
                pojo.getDateExpiration()
        );
    }
}
