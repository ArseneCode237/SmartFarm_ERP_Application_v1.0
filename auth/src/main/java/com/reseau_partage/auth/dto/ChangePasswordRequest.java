package com.reseau_partage.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {
    @NotBlank(message = "Le mot de passe actuel est obligatoire.")
    private String currentPassword;
    @NotBlank(message = "Le nouveau mot de passe est obligatoire.")
    private String newPassword;
    @NotBlank(message = "La confirmation du nouveau mot de passe est obligatoire.")
    private String confirmNewPassword;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmNewPassword() { return confirmNewPassword; }
    public void setConfirmNewPassword(String confirmNewPassword) { this.confirmNewPassword = confirmNewPassword; }
}
