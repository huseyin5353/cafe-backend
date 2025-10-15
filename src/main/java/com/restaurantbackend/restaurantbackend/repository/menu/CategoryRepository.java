package com.restaurantbackend.restaurantbackend.repository.menu;

import com.restaurantbackend.restaurantbackend.entity.menu.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    @EntityGraph(attributePaths = {"department", "subCategories"})
    @Query("SELECT c FROM Category c ORDER BY c.sortOrder ASC, c.id ASC")
    List<Category> findAllWithDetails();
    
    @EntityGraph(attributePaths = {"department", "subCategories", "subCategories.menuItems"})
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findByIdWithDetails(@Param("id") Long id);
    
    @EntityGraph(attributePaths = {"department"})
    @Query("SELECT c FROM Category c WHERE c.department.id = :departmentId")
    List<Category> findByDepartmentIdWithDetails(@Param("departmentId") Long departmentId);
}
