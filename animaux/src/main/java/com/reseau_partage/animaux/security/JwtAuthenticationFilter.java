package com.reseau_partage.animaux.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.reseau_partage.core.repository.UtilisateurRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UtilisateurRepository utilisateurRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UtilisateurRepository utilisateurRepository) {
        this.jwtUtils = jwtUtils;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/auth/register") || requestURI.contains("/api/auth/login") || requestURI.contains("/api/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = authHeader.substring(7);
        String userEmail = jwtUtils.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            utilisateurRepository.findByEmail(userEmail).ifPresent(utilisateur -> {
                if (Boolean.TRUE.equals(utilisateur.getActif()) && jwtUtils.validateToken(jwt, new org.springframework.security.core.userdetails.User(utilisateur.getEmail(), utilisateur.getMotDePasse(), List.of()))) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            utilisateur.getEmail(), null, List.of());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            });
        }
        filterChain.doFilter(request, response);
    }
}
