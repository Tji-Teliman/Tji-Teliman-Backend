package com.example.Tji_Teliman.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecurityUtils {

    @Autowired
    private JwtService jwtService;

    public Long getCurrentUserId() {
        String token = getTokenFromRequest();
        if (token != null) {
            Long userId = jwtService.parseUserId(token);
            if (userId != null) {
                return userId;
            }
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    public String getCurrentUserRole() {
        // Pour l'instant, retournez un rôle par défaut
        // Vous pourrez l'améliorer plus tard en lisant le rôle du token
        return "ROLE_JEUNE_PRESTATAIRE";
    }

    private String getTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }
}