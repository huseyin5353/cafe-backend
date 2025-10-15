package com.restaurantbackend.restaurantbackend.repository.menu;

import com.restaurantbackend.restaurantbackend.entity.menu.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    List<Subcategory> findByCategoryIdOrderBySortOrderAscIdAsc(Long categoryId);
    
    @Deprecated
    default List<Subcategory> findByCategoryId(Long categoryId) {
        return findByCategoryIdOrderBySortOrderAscIdAsc(categoryId);
    }
}