package com.example.Tji_Teliman.services;

import com.example.Tji_Teliman.entites.Categorie;
import com.example.Tji_Teliman.entites.Administrateur;
import com.example.Tji_Teliman.repository.CategorieRepository;
import com.example.Tji_Teliman.repository.AdministrateurRepository;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.Tji_Teliman.dto.CategorieDTO;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final AdministrateurRepository administrateurRepository;
    private final FileStorageService storageService;

    public CategorieService(CategorieRepository categorieRepository, AdministrateurRepository administrateurRepository, FileStorageService storageService) {
        this.categorieRepository = categorieRepository;
        this.administrateurRepository = administrateurRepository;
        this.storageService = storageService;
    }

    @Transactional
    public Categorie create(Long adminId, String nom, MultipartFile photo) throws IOException {
        if (nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Nom requis");
        if (photo == null || photo.isEmpty()) throw new IllegalArgumentException("Photo requise");
        Administrateur admin = administrateurRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("Administrateur introuvable"));
        Categorie c = new Categorie();
        c.setNom(nom.trim());
        String path = storageService.store(photo, "categories");
        c.setUrlPhoto(path);
        c.setAdministrateur(admin);
        return categorieRepository.save(c);
    }

    @Transactional(readOnly = true)
    public List<Categorie> list() {
        return categorieRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CategorieDTO toDTO(Categorie c) {
        CategorieDTO dto = new CategorieDTO();
        dto.setId(c.getId());
        dto.setNom(c.getNom());
        dto.setUrlPhoto(c.getUrlPhoto());
        dto.setMissionsCount(c.getMissions() == null ? 0 : c.getMissions().size());
        return dto;
    }

    @Transactional
    public Categorie update(Long id, String nom, MultipartFile photo) throws IOException {
        Categorie c = categorieRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));
        if (nom != null && !nom.trim().isEmpty()) c.setNom(nom.trim());
        if (photo != null && !photo.isEmpty()) {
            String path = storageService.store(photo, "categories");
            c.setUrlPhoto(path);
        }
        return categorieRepository.save(c);
    }

    @Transactional
    public void delete(Long id) {
        if (!categorieRepository.existsById(id)) throw new IllegalArgumentException("Catégorie introuvable");
        categorieRepository.deleteById(id);
    }
}
