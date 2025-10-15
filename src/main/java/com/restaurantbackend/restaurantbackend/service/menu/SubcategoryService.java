package com.restaurantbackend.restaurantbackend.service.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.SubcategoryDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.SubcategoryRequestDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.UpdateSortOrderDTO;
import com.restaurantbackend.restaurantbackend.entity.menu.Category;
import com.restaurantbackend.restaurantbackend.entity.menu.Subcategory;
import com.restaurantbackend.restaurantbackend.mapper.menu.SubcategoryMapper;
import com.restaurantbackend.restaurantbackend.repository.menu.CategoryRepository;
import com.restaurantbackend.restaurantbackend.repository.menu.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;
    private final SubcategoryMapper subcategoryMapper;
    private final CategoryRepository categoryRepository;

    public SubcategoryDTO createSubcategory(SubcategoryRequestDTO subcategoryRequestDTO) {
        Category category = categoryRepository.findById(subcategoryRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + subcategoryRequestDTO.getCategoryId()));

        Subcategory subcategory = subcategoryMapper.toEntity(subcategoryRequestDTO);
        subcategory.setCategory(category);
        subcategory = subcategoryRepository.save(subcategory);
        return subcategoryMapper.toDTO(subcategory);
    }

    public SubcategoryDTO getSubcategory(Long id) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + id));
        return subcategoryMapper.toDTO(subcategory);
    }

    public List<SubcategoryDTO> getAllSubcategories() {
        return subcategoryRepository.findAll().stream()
                .map(subcategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SubcategoryDTO> getSubcategoriesByCategoryId(Long categoryId) {
        return subcategoryRepository.findByCategoryId(categoryId).stream()
                .map(subcategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SubcategoryDTO updateSubcategory(Long id, SubcategoryRequestDTO subcategoryRequestDTO) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + id));

        Category category = categoryRepository.findById(subcategoryRequestDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + subcategoryRequestDTO.getCategoryId()));

        subcategory.setName(subcategoryRequestDTO.getName());
        subcategory.setCategory(category);
        subcategory = subcategoryRepository.save(subcategory);
        return subcategoryMapper.toDTO(subcategory);
    }

    public void deleteSubcategory(Long id) {
        subcategoryRepository.deleteById(id);
    }

    @Transactional
    public void updateSortOrder(UpdateSortOrderDTO updateSortOrderDTO) {
        for (UpdateSortOrderDTO.SortOrderItem item : updateSortOrderDTO.getItems()) {
            Subcategory subcategory = subcategoryRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Subcategory not found with id: " + item.getId()));
            subcategory.setSortOrder(item.getSortOrder());
            subcategoryRepository.save(subcategory);
        }
    }
}