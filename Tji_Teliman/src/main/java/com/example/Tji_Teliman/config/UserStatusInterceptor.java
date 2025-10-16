package com.example.Tji_Teliman.config;

import com.example.Tji_Teliman.entites.Utilisateur;
import com.example.Tji_Teliman.entites.enums.StatutUtilisateur;
import com.example.Tji_Teliman.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserStatusInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public UserStatusInterceptor(JwtService jwtService, UtilisateurRepository utilisateurRepository) {
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Expect header: Authorization: Bearer <token>
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return true; // endpoints are globally permitAll per current security; don't block anonymously
        }
        String token = auth.substring(7);
        Long userId = jwtService.parseUserId(token);
        if (userId == null) {
            return true; // invalid token, let downstream handle auth checks
        }
        Utilisateur user = utilisateurRepository.findById(userId).orElse(null);
        if (user == null) {
            return true;
        }
        if (user.getStatut() == StatutUtilisateur.DESACTIVER) {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Compte désactivé\"}");
            return false;
        }
        return true;
    }
}


