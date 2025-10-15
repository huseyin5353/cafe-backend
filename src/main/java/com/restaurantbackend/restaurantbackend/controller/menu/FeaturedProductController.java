package com.restaurantbackend.restaurantbackend.controller.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.CreateFeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.FeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.UpdateFeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.service.menu.FeaturedProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/featured-products")
public class FeaturedProductController {
    
    @Autowired
    private FeaturedProductService featuredProductService;
    
    @GetMapping
    public ResponseEntity<List<FeaturedProductDTO>> getAllFeaturedProducts() {
        List<FeaturedProductDTO> products = featuredProductService.getAllFeaturedProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<FeaturedProductDTO>> getActiveFeaturedProducts() {
        List<FeaturedProductDTO> products = featuredProductService.getActiveFeaturedProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FeaturedProductDTO> getFeaturedProductById(@PathVariable Long id) {
        return featuredProductService.getFeaturedProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<FeaturedProductDTO> createFeaturedProduct(@RequestBody CreateFeaturedProductDTO dto) {
        try {
            FeaturedProductDTO created = featuredProductService.createFeaturedProduct(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FeaturedProductDTO> updateFeaturedProduct(
            @PathVariable Long id, 
            @RequestBody UpdateFeaturedProductDTO dto) {
        return featuredProductService.updateFeaturedProduct(id, dto)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeaturedProduct(@PathVariable Long id) {
        boolean deleted = featuredProductService.deleteFeaturedProduct(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<Void> toggleActiveStatus(@PathVariable Long id) {
        boolean toggled = featuredProductService.toggleActiveStatus(id);
        return toggled ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    @PatchMapping("/{id}/display-order")
    public ResponseEntity<Void> updateDisplayOrder(
            @PathVariable Long id, 
            @RequestParam Integer order) {
        boolean updated = featuredProductService.updateDisplayOrder(id, order);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
