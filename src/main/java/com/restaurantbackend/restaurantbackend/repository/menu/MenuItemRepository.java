package com.restaurantbackend.restaurantbackend.repository.menu;

import com.restaurantbackend.restaurantbackend.entity.menu.MenuItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    @EntityGraph(attributePaths = {"subcategory", "department"})
    @Query("SELECT m FROM MenuItem m ORDER BY m.sortOrder ASC, m.id ASC")
    List<MenuItem> findAllWithDetails();
}