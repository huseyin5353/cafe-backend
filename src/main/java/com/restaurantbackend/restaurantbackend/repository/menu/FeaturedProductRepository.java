package com.restaurantbackend.restaurantbackend.repository.menu;

import com.restaurantbackend.restaurantbackend.entity.menu.FeaturedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeaturedProductRepository extends JpaRepository<FeaturedProduct, Long> {
    
    List<FeaturedProduct> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    @Query("SELECT fp FROM FeaturedProduct fp WHERE fp.isActive = true ORDER BY fp.displayOrder ASC")
    List<FeaturedProduct> findActiveFeaturedProductsOrdered();
    
    boolean existsByDisplayOrderAndIsActiveTrue(Integer displayOrder);
    
    @Query("SELECT MAX(fp.displayOrder) FROM FeaturedProduct fp WHERE fp.isActive = true")
    Integer findMaxDisplayOrder();
}



