package com.example.Tji_Teliman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Autoriser toutes les origines pour le développement (Flutter Web, Mobile, etc.)
        // Utiliser setAllowedOriginPatterns pour permettre * avec allowCredentials
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));

        // Headers autorisés (important pour Flutter avec Authorization token)
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Headers exposés pour le client
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // Permettre les credentials (cookies, tokens)
        configuration.setAllowCredentials(true);

        // Cache la réponse preflight pendant 1 heure
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final com.example.Tji_Teliman.config.UserStatusInterceptor userStatusInterceptor;

    public SecurityConfig(com.example.Tji_Teliman.config.UserStatusInterceptor userStatusInterceptor) {
        this.userStatusInterceptor = userStatusInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les fichiers uploadés depuis le dossier uploads/
        Path uploadsPath = Paths.get("uploads").toAbsolutePath().normalize();
        // Format correct pour Windows et Unix/Linux
        String uploadsLocation = "file:" + uploadsPath.toString().replace("\\", "/") + "/";

        // Ajouter le ResourceHandler pour servir les fichiers statiques
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsLocation)
                .setCachePeriod(3600) // Cache pour 1 heure
                .resourceChain(true); // Activer la chaîne de ressources pour meilleure performance

        // Log pour debug (peut être retiré en production)
        System.out.println("Configuration ResourceHandler - Chemin uploads: " + uploadsLocation);
        System.out.println("Chemin absolu normalisé: " + uploadsPath.toString());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userStatusInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/uploads/**"); // Exclure les fichiers uploadés de l'intercepteur
    }
}