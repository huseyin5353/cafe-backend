package com.restaurantbackend.restaurantbackend.service.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.MenuItemDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.MenuItemRequestDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.UpdateSortOrderDTO;
import com.restaurantbackend.restaurantbackend.entity.menu.MenuItem;
import com.restaurantbackend.restaurantbackend.entity.menu.Subcategory;
import com.restaurantbackend.restaurantbackend.entity.menu.Category;
import com.restaurantbackend.restaurantbackend.entity.department.Department;
import com.restaurantbackend.restaurantbackend.repository.department.DepartmentRepository;
import com.restaurantbackend.restaurantbackend.mapper.menu.MenuItemMapper;
import com.restaurantbackend.restaurantbackend.repository.menu.MenuItemRepository;
import com.restaurantbackend.restaurantbackend.repository.menu.SubcategoryRepository;
import com.restaurantbackend.restaurantbackend.repository.menu.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;
    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;

    @CacheEvict(value = {"menuItems", "categories", "departments"}, allEntries = true)
    @Transactional
    public MenuItemDTO createMenuItem(MenuItemRequestDTO menuItemRequestDTO) {
        Subcategory subcategory = subcategoryRepository.findById(menuItemRequestDTO.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + menuItemRequestDTO.getSubcategoryId()));

        // Category'yi açıkça yükle
        Category category = categoryRepository.findById(subcategory.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + subcategory.getCategory().getId()));

        MenuItem menuItem = menuItemMapper.toEntity(menuItemRequestDTO);
        menuItem.setSubcategory(subcategory);

        // Departman atama: önce istekte departmentId varsa onu kullan, yoksa otomatik ata
        if (menuItemRequestDTO.getDepartmentId() != null) {
            Department chosenDept = departmentRepository.findById(menuItemRequestDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + menuItemRequestDTO.getDepartmentId()));
            menuItem.setDepartment(chosenDept);
            System.out.println("🟨 İstek ile departman atandı: " + chosenDept.getName() + " -> " + menuItem.getName());
        } else {
            if (category.getDepartment() != null) {
                menuItem.setDepartment(category.getDepartment());
                System.out.println("✅ Otomatik departman atandı: " + category.getDepartment().getName() + 
                                 " -> " + menuItem.getName());
            } else {
                System.out.println("⚠️ Kategori için departman bulunamadı: " + category.getName());
            }
        }
        
        menuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toDTO(menuItem);
    }

    @Cacheable(value = "menuItems", key = "#id")
    public MenuItemDTO getMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + id));
        return menuItemMapper.toDTO(menuItem);
    }

    @Cacheable("menuItems")
    public List<MenuItemDTO> getAllMenuItems() {
        // EntityGraph ile optimize edilmiş sorgu kullan
        return menuItemRepository.findAllWithDetails().stream()
                .map(menuItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignDepartmentsToExistingMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        System.out.println("=== DEPARTMAN ATAMA İŞLEMİ BAŞLIYOR ===");
        
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getDepartment() == null && menuItem.getSubcategory() != null) {
                Subcategory subcategory = subcategoryRepository.findById(menuItem.getSubcategory().getId())
                        .orElse(null);
                
                if (subcategory != null && subcategory.getCategory() != null) {
                    Category category = categoryRepository.findById(subcategory.getCategory().getId())
                            .orElse(null);
                    
                    if (category != null && category.getDepartment() != null) {
                        menuItem.setDepartment(category.getDepartment());
                        menuItemRepository.save(menuItem);
                        System.out.println("✅ " + menuItem.getName() + " -> " + category.getDepartment().getName());
                    } else {
                        System.out.println("⚠️ " + menuItem.getName() + " için departman bulunamadı");
                    }
                } else {
                    System.out.println("⚠️ " + menuItem.getName() + " için kategori bulunamadı");
                }
            } else if (menuItem.getDepartment() != null) {
                System.out.println("ℹ️ " + menuItem.getName() + " zaten " + menuItem.getDepartment().getName() + " departmanında");
            }
        }
        
        System.out.println("=== DEPARTMAN ATAMA İŞLEMİ TAMAMLANDI ===");
    }

    @Transactional
    public void createTestData() {
        System.out.println("=== TEST VERİSİ OLUŞTURULUYOR ===");
        
        // Departmanları oluştur
        Department mutfak = new Department();
        mutfak.setName("Mutfak");
        mutfak.setDescription("Ana yemekler ve sıcak yemekler");
        mutfak.setColorCode("#ff4d4f");
        mutfak.setIconName("🍽️");
        mutfak.setActive(true);
        mutfak.setSortOrder(1);
        mutfak.setCreatedAt(java.time.LocalDateTime.now());
        mutfak = departmentRepository.save(mutfak);
        
        Department bar = new Department();
        bar.setName("Bar");
        bar.setDescription("İçecekler ve soğuk içecekler");
        bar.setColorCode("#1890ff");
        bar.setIconName("🥤");
        bar.setActive(true);
        bar.setSortOrder(2);
        bar.setCreatedAt(java.time.LocalDateTime.now());
        bar = departmentRepository.save(bar);
        
        // Kategorileri oluştur
        Category anaYemekler = new Category();
        anaYemekler.setName("Ana Yemekler");
        anaYemekler.setDepartment(mutfak);
        anaYemekler = categoryRepository.save(anaYemekler);
        
        Category icecekler = new Category();
        icecekler.setName("İçecekler");
        icecekler.setDepartment(bar);
        icecekler = categoryRepository.save(icecekler);
        
        // Alt kategorileri oluştur
        Subcategory kofte = new Subcategory();
        kofte.setName("Köfte");
        kofte.setCategory(anaYemekler);
        kofte = subcategoryRepository.save(kofte);
        
        Subcategory cay = new Subcategory();
        cay.setName("Çay");
        cay.setCategory(icecekler);
        cay = subcategoryRepository.save(cay);
        
        // Menü öğelerini oluştur
        MenuItem suluKofte = new MenuItem();
        suluKofte.setName("Sulu Köfte");
        suluKofte.setPrice(new java.math.BigDecimal("45.00"));
        suluKofte.setDescription("Geleneksel sulu köfte");
        suluKofte.setAvailable(true);
        suluKofte.setSubcategory(kofte);
        suluKofte.setDepartment(mutfak);
        menuItemRepository.save(suluKofte);
        
        MenuItem cayItem = new MenuItem();
        cayItem.setName("Çay");
        cayItem.setPrice(new java.math.BigDecimal("5.00"));
        cayItem.setDescription("Demli çay");
        cayItem.setAvailable(true);
        cayItem.setSubcategory(cay);
        cayItem.setDepartment(bar);
        menuItemRepository.save(cayItem);
        
        System.out.println("✅ Test verisi oluşturuldu:");
        System.out.println("   - Sulu Köfte -> Mutfak");
        System.out.println("   - Çay -> Bar");
        System.out.println("=== TEST VERİSİ TAMAMLANDI ===");
    }

    @CacheEvict(value = {"menuItems", "categories", "departments"}, allEntries = true)
    @Transactional
    public MenuItemDTO updateMenuItem(Long id, MenuItemRequestDTO menuItemRequestDTO) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + id));

        Subcategory subcategory = subcategoryRepository.findById(menuItemRequestDTO.getSubcategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + menuItemRequestDTO.getSubcategoryId()));

        // Category'yi açıkça yükle
        Category category = categoryRepository.findById(subcategory.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + subcategory.getCategory().getId()));

        menuItem.setName(menuItemRequestDTO.getName());
        menuItem.setPrice(menuItemRequestDTO.getPrice());
        menuItem.setDescription(menuItemRequestDTO.getDescription());
        menuItem.setAvailable(menuItemRequestDTO.isAvailable());
        menuItem.setImageUrl(menuItemRequestDTO.getImageUrl());
        menuItem.setSubcategory(subcategory);

        // Besin değerleri
        menuItem.setCalories(menuItemRequestDTO.getCalories());
        menuItem.setProtein(menuItemRequestDTO.getProtein());
        menuItem.setCarbs(menuItemRequestDTO.getCarbs());
        menuItem.setFat(menuItemRequestDTO.getFat());

        // Ürün özellikleri
        menuItem.setPreparationTime(menuItemRequestDTO.getPreparationTime());
        menuItem.setSpiceLevel(menuItemRequestDTO.getSpiceLevel());
        menuItem.setIsVegetarian(menuItemRequestDTO.getIsVegetarian());
        menuItem.setIsVegan(menuItemRequestDTO.getIsVegan());
        menuItem.setIsGlutenFree(menuItemRequestDTO.getIsGlutenFree());

        // İçerik bilgileri
        menuItem.setIngredients(menuItemRequestDTO.getIngredients());
        menuItem.setAllergens(menuItemRequestDTO.getAllergens());
        
        // Departman atama (güncelleme): önce istekte departmentId varsa onu kullan, yoksa otomatik ata
        if (menuItemRequestDTO.getDepartmentId() != null) {
            Department chosenDept = departmentRepository.findById(menuItemRequestDTO.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + menuItemRequestDTO.getDepartmentId()));
            menuItem.setDepartment(chosenDept);
            System.out.println("🟨 Güncellemede istek ile departman atandı: " + chosenDept.getName() + " -> " + menuItem.getName());
        } else {
            if (category.getDepartment() != null) {
                menuItem.setDepartment(category.getDepartment());
                System.out.println("✅ Güncelleme sırasında otomatik departman atandı: " + 
                                 category.getDepartment().getName() + " -> " + menuItem.getName());
            } else {
                System.out.println("⚠️ Güncelleme sırasında kategori için departman bulunamadı: " + 
                                 category.getName());
            }
        }

        menuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toDTO(menuItem);
    }

    @CacheEvict(value = {"menuItems", "categories", "departments"}, allEntries = true)
    @Transactional
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    @CacheEvict(value = {"menuItems", "categories", "departments"}, allEntries = true)
    @Transactional
    public void updateSortOrder(UpdateSortOrderDTO updateSortOrderDTO) {
        for (UpdateSortOrderDTO.SortOrderItem item : updateSortOrderDTO.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + item.getId()));
            menuItem.setSortOrder(item.getSortOrder());
            menuItemRepository.save(menuItem);
        }
    }
}