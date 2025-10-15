package com.restaurantbackend.restaurantbackend.controller.menu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
public class ImageUploadController {
    
    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;
    
    @PostMapping("/featured-product")
    public ResponseEntity<Map<String, String>> uploadFeaturedProductImage(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Dosya kontrolü
            if (file.isEmpty()) {
                response.put("error", "Dosya boş olamaz");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Dosya tipi kontrolü
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("error", "Sadece resim dosyaları yüklenebilir");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Dosya boyutu kontrolü (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("error", "Dosya boyutu 5MB'dan küçük olmalıdır");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Upload dizinini oluştur
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Benzersiz dosya adı oluştur
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String filename = "featured_" + UUID.randomUUID().toString() + extension;
            
            // Dosyayı kaydet
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // URL döndür
            String imageUrl = "/uploads/images/" + filename;
            response.put("imageUrl", imageUrl);
            response.put("filename", filename);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("error", "Dosya yüklenirken hata oluştu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
}
