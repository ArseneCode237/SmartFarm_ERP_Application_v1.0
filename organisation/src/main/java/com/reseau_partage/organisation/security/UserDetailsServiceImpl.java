package com.reseau_partage.organisation.security;

import com.reseau_partage.core.entities.Profil;
import com.reseau_partage.core.entities.Utilisateur;
import com.reseau_partage.core.repository.ProfilRepository;
import com.reseau_partage.core.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final ProfilRepository profilRepository;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository,
            ProfilRepository profilRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.profilRepository = profilRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'email : " + email));

        if (!Boolean.TRUE.equals(utilisateur.getActif())) {
            throw new UsernameNotFoundException("Compte désactivé");
        }

        List<SimpleGrantedAuthority> authorities = utilisateur.getProfil_id() == null
                ? List.of()
                : profilRepository.findById(utilisateur.getProfil_id())
                        .map(this::authoritiesFor)
                        .orElseGet(List::of);

        return new User(utilisateur.getEmail(), utilisateur.getMotDePasse(), authorities);
    }

    private List<SimpleGrantedAuthority> authoritiesFor(Profil profil) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + profil.getCode()));
        if (profil.getPermissions() != null) {
            Arrays.stream(profil.getPermissions().split(","))
                    .map(String::trim)
                    .filter(p -> !p.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
        return authorities;
    }
}
