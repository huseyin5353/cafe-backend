package com.restaurantbackend.restaurantbackend.controller.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.CategoryDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.CategoryRequestDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.UpdateSortOrderDTO;
import com.restaurantbackend.restaurantbackend.service.menu.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDTO createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        return categoryService.createCategory(categoryRequestDTO);
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PutMapping("/{id}")
    public CategoryDTO updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return categoryService.updateCategory(id, categoryRequestDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    @PostMapping("/{id}/assign-department/{departmentId}")
    public CategoryDTO assignDepartmentToCategory(@PathVariable Long id, @PathVariable Long departmentId) {
        return categoryService.assignDepartmentToCategory(id, departmentId);
    }

    @PostMapping("/{id}/remove-department")
    public CategoryDTO removeDepartmentFromCategory(@PathVariable Long id) {
        return categoryService.removeDepartmentFromCategory(id);
    }

    @PutMapping("/update-sort-order")
    public void updateCategoriesSortOrder(@RequestBody UpdateSortOrderDTO updateSortOrderDTO) {
        categoryService.updateSortOrder(updateSortOrderDTO);
    }
}