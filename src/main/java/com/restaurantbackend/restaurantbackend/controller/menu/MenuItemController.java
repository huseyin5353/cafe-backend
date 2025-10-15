package com.restaurantbackend.restaurantbackend.controller.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.MenuItemDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.MenuItemRequestDTO;
import com.restaurantbackend.restaurantbackend.service.menu.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public MenuItemDTO createMenuItem(@RequestBody MenuItemRequestDTO menuItemRequestDTO) {
        return menuItemService.createMenuItem(menuItemRequestDTO);
    }

    @GetMapping("/{id}")
    public MenuItemDTO getMenuItem(@PathVariable Long id) {
        return menuItemService.getMenuItem(id);
    }

    @GetMapping
    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("/debug")
    public String debugMenuItems() {
        List<MenuItemDTO> items = menuItemService.getAllMenuItems();
        StringBuilder debug = new StringBuilder();
        debug.append("=== MENU ITEMS DEBUG ===\n");
        for (MenuItemDTO item : items) {
            debug.append("ID: ").append(item.getId())
                 .append(", Name: ").append(item.getName())
                 .append(", Department: ").append(item.getDepartment() != null ? item.getDepartment().getName() : "NULL")
                 .append("\n");
        }
        return debug.toString();
    }

    @PostMapping("/assign-departments")
    public String assignDepartments() {
        menuItemService.assignDepartmentsToExistingMenuItems();
        return "Departman atama işlemi tamamlandı. Backend loglarını kontrol edin.";
    }

    @PostMapping("/create-test-data")
    public String createTestData() {
        menuItemService.createTestData();
        return "Test verisi oluşturuldu.";
    }

    @PutMapping("/{id}")
    public MenuItemDTO updateMenuItem(@PathVariable Long id, @RequestBody MenuItemRequestDTO menuItemRequestDTO) {
        return menuItemService.updateMenuItem(id, menuItemRequestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
    }

    @PostMapping("/upload-image")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Resim klasörünü oluştur
            String uploadDir = "uploads/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Benzersiz dosya adı oluştur
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // Dosyayı kaydet
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // URL'i döndür
            return "/uploads/images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Resim yüklenirken hata oluştu: " + e.getMessage());
        }
    }
}