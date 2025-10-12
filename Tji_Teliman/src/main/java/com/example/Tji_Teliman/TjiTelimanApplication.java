package com.example.Tji_Teliman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.example.Tji_Teliman.entites.Administrateur;
import com.example.Tji_Teliman.entites.enums.Role;
import com.example.Tji_Teliman.entites.enums.TypeGenre;
import com.example.Tji_Teliman.repository.AdministrateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TjiTelimanApplication {

    public static void main(String[] args) {
        SpringApplication.run(TjiTelimanApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAdmin(AdministrateurRepository adminRepo, PasswordEncoder encoder) {
        return args -> {
            boolean exists = adminRepo.findAll().stream().anyMatch(a -> "70000000".equals(a.getTelephone()));
            if (!exists) {
                Administrateur a = new Administrateur();
                a.setNom("Bagayoko");
                a.setPrenom("Amadou");
                a.setEmail("abagayoko304@mail.com");
                a.setTelephone("70000000");
                a.setGenre(TypeGenre.MASCULIN);
                a.setMotDePasse(encoder.encode("Tji_Teliman123"));
                a.setRole(Role.ADMINISTRATEUR);
                adminRepo.save(a);
            }
        };
    }
}
