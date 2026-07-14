package com.reseau_partage.auth.service;

import com.reseau_partage.core.entities.Profil;
import com.reseau_partage.core.repository.ProfilRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProfilRepository profilRepository;

    public DataInitializer(ProfilRepository profilRepository) {
        this.profilRepository = profilRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!profilRepository.existsByCode("ADMIN")) {
            Profil adminProfil = new Profil();
            adminProfil.setCode("ADMIN");
            adminProfil.setLibelle("Administrateur");
            adminProfil.setPermissions("READ,WRITE,DELETE");
            profilRepository.save(adminProfil);
        }

        if (!profilRepository.existsByCode("USER")) {
            Profil userProfil = new Profil();
            userProfil.setCode("USER");
            userProfil.setLibelle("Utilisateur Standard");
            userProfil.setPermissions("READ,WRITE");
            profilRepository.save(userProfil);
        }
    }
}
