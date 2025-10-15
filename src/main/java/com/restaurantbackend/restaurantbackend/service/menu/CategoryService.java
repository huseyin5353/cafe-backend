package com.restaurantbackend.restaurantbackend.service.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.CategoryDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.CategoryRequestDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.UpdateSortOrderDTO;
import com.restaurantbackend.restaurantbackend.entity.menu.Category;
import com.restaurantbackend.restaurantbackend.entity.department.Department;
import com.restaurantbackend.restaurantbackend.mapper.menu.CategoryMapper;
import com.restaurantbackend.restaurantbackend.repository.menu.CategoryRepository;
import com.restaurantbackend.restaurantbackend.repository.department.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final DepartmentRepository departmentRepository;

    public CategoryDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryMapper.toEntity(categoryRequestDTO);
        
        // Departman ataması
        if (categoryRequestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(categoryRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + categoryRequestDTO.getDepartmentId()));
            category.setDepartment(department);
        }
        
        category = categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    public CategoryDTO getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toDTO(category);
    }

    public List<CategoryDTO> getAllCategories() {
        // EntityGraph ile tek sorguda tüm ilişkili verileri çek
        return categoryRepository.findAllWithDetails().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setName(categoryRequestDTO.getName());
        
        // Departman güncellemesi
        if (categoryRequestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(categoryRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found with id: " + categoryRequestDTO.getDepartmentId()));
            category.setDepartment(department);
        } else {
            category.setDepartment(null);
        }
        
        category = categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Kategoriye departman atar
     */
    @Transactional
    public CategoryDTO assignDepartmentToCategory(Long categoryId, Long departmentId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));
        
        category.setDepartment(department);
        category = categoryRepository.save(category);
        
        return categoryMapper.toDTO(category);
    }

    /**
     * Kategoriden departmanı kaldırır
     */
    @Transactional
    public CategoryDTO removeDepartmentFromCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        
        category.setDepartment(null);
        category = categoryRepository.save(category);
        
        return categoryMapper.toDTO(category);
    }

    @Transactional
    public void updateSortOrder(UpdateSortOrderDTO updateSortOrderDTO) {
        for (UpdateSortOrderDTO.SortOrderItem item : updateSortOrderDTO.getItems()) {
            Category category = categoryRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + item.getId()));
            category.setSortOrder(item.getSortOrder());
            categoryRepository.save(category);
        }
    }
}