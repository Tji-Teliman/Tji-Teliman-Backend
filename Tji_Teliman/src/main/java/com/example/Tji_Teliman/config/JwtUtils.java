package com.example.Tji_Teliman.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final JwtService jwtService;

    public JwtUtils(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Extrait l'ID utilisateur du token JWT présent dans l'en-tête Authorization
     * @param request La requête HTTP
     * @return L'ID utilisateur ou null si le token est invalide/absent
     */
    public Long getUserIdFromToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        String token = auth.substring(7);
        return jwtService.parseUserId(token);
    }

    /**
     * Extrait le rôle utilisateur du token JWT présent dans l'en-tête Authorization
     * @param request La requête HTTP
     * @return Le rôle utilisateur ou null si le token est invalide/absent
     */
    public String getUserRoleFromToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        String token = auth.substring(7);
        return jwtService.parseUserRole(token);
    }

    /**
     * Vérifie si l'utilisateur est authentifié (token valide présent)
     * @param request La requête HTTP
     * @return true si l'utilisateur est authentifié, false sinon
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        return getUserIdFromToken(request) != null;
    }

    /**
     * Vérifie si l'utilisateur est un administrateur
     * @param request La requête HTTP
     * @return true si l'utilisateur est un administrateur, false sinon
     */
    public boolean isAdmin(HttpServletRequest request) {
        String role = getUserRoleFromToken(request);
        return "ADMINISTRATEUR".equals(role);
    }
}
