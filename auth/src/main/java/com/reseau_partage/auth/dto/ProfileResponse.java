package com.reseau_partage.auth.dto;

import com.reseau_partage.core.pojo.UtilisateurPojo;

public record ProfileResponse(boolean success, int status, String message,
                              boolean requiresReauthentication, UtilisateurPojo user) {
}
