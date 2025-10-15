package com.restaurantbackend.restaurantbackend.controller.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.SubcategoryDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.SubcategoryRequestDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.UpdateSortOrderDTO;
import com.restaurantbackend.restaurantbackend.service.menu.SubcategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subcategories")
@RequiredArgsConstructor
public class SubcategoryController {

    private final SubcategoryService subcategoryService;

    @PostMapping
    public SubcategoryDTO createSubcategory(@RequestBody SubcategoryRequestDTO subcategoryRequestDTO) {
        return subcategoryService.createSubcategory(subcategoryRequestDTO);
    }

    @GetMapping("/{id}")
    public SubcategoryDTO getSubcategory(@PathVariable Long id) {
        return subcategoryService.getSubcategory(id);
    }

    @GetMapping
    public List<SubcategoryDTO> getAllSubcategories() {
        return subcategoryService.getAllSubcategories();
    }

    @GetMapping("/byCategory/{categoryId}")
    public List<SubcategoryDTO> getSubcategoriesByCategoryId(@PathVariable Long categoryId) {
        return subcategoryService.getSubcategoriesByCategoryId(categoryId);
    }

    @PutMapping("/{id}")
    public SubcategoryDTO updateSubcategory(@PathVariable Long id, @RequestBody SubcategoryRequestDTO subcategoryRequestDTO) {
        return subcategoryService.updateSubcategory(id, subcategoryRequestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteSubcategory(@PathVariable Long id) {
        subcategoryService.deleteSubcategory(id);
    }

    @PutMapping("/update-sort-order")
    public void updateSubcategoriesSortOrder(@RequestBody UpdateSortOrderDTO updateSortOrderDTO) {
        subcategoryService.updateSortOrder(updateSortOrderDTO);
    }
}