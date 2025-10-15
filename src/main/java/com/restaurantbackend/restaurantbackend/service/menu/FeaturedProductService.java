package com.restaurantbackend.restaurantbackend.service.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.CreateFeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.FeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.UpdateFeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.entity.menu.FeaturedProduct;
import com.restaurantbackend.restaurantbackend.mapper.menu.FeaturedProductMapper;
import com.restaurantbackend.restaurantbackend.repository.menu.FeaturedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeaturedProductService {
    
    @Autowired
    private FeaturedProductRepository featuredProductRepository;
    
    @Autowired
    private FeaturedProductMapper featuredProductMapper;
    
    public List<FeaturedProductDTO> getAllFeaturedProducts() {
        List<FeaturedProduct> products = featuredProductRepository.findAll();
        return products.stream()
                .map(featuredProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<FeaturedProductDTO> getActiveFeaturedProducts() {
        List<FeaturedProduct> products = featuredProductRepository.findActiveFeaturedProductsOrdered();
        return products.stream()
                .map(featuredProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<FeaturedProductDTO> getFeaturedProductById(Long id) {
        return featuredProductRepository.findById(id)
                .map(featuredProductMapper::toDTO);
    }
    
    public FeaturedProductDTO createFeaturedProduct(CreateFeaturedProductDTO dto) {
        // Eğer displayOrder belirtilmemişse, en son sıraya ekle
        if (dto.getDisplayOrder() == null) {
            Integer maxOrder = featuredProductRepository.findMaxDisplayOrder();
            dto.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);
        }
        
        FeaturedProduct entity = featuredProductMapper.toEntity(dto);
        FeaturedProduct saved = featuredProductRepository.save(entity);
        return featuredProductMapper.toDTO(saved);
    }
    
    public Optional<FeaturedProductDTO> updateFeaturedProduct(Long id, UpdateFeaturedProductDTO dto) {
        return featuredProductRepository.findById(id)
                .map(entity -> {
                    featuredProductMapper.updateEntity(entity, dto);
                    FeaturedProduct updated = featuredProductRepository.save(entity);
                    return featuredProductMapper.toDTO(updated);
                });
    }
    
    public boolean deleteFeaturedProduct(Long id) {
        if (featuredProductRepository.existsById(id)) {
            featuredProductRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public boolean toggleActiveStatus(Long id) {
        Optional<FeaturedProduct> product = featuredProductRepository.findById(id);
        if (product.isPresent()) {
            FeaturedProduct entity = product.get();
            entity.setIsActive(!entity.getIsActive());
            featuredProductRepository.save(entity);
            return true;
        }
        return false;
    }
    
    public boolean updateDisplayOrder(Long id, Integer newOrder) {
        Optional<FeaturedProduct> product = featuredProductRepository.findById(id);
        if (product.isPresent()) {
            FeaturedProduct entity = product.get();
            entity.setDisplayOrder(newOrder);
            featuredProductRepository.save(entity);
            return true;
        }
        return false;
    }
}



