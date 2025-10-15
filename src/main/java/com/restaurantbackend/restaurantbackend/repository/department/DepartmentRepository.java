package com.restaurantbackend.restaurantbackend.repository.department;

import com.restaurantbackend.restaurantbackend.entity.department.Department;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // Department entity'sinde categories ilişkisi yok, basit sorgu kullan
    @Query("SELECT d FROM Department d ORDER BY d.sortOrder ASC")
    List<Department> findAllWithDetails();
    
    Optional<Department> findByName(String name);
    
    List<Department> findByIsActiveTrueOrderBySortOrderAsc();
    
    List<Department> findByIsActiveFalseOrderByDeletedAtDesc();
    
    List<Department> findAllByOrderBySortOrderAsc();
    
    @Query("SELECT d FROM Department d WHERE d.isActive = true ORDER BY d.sortOrder ASC")
    List<Department> findActiveDepartmentsOrdered();
    
    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.department.id = :departmentId")
    Long countMenuItemsByDepartment(@Param("departmentId") Long departmentId);
    
    // Simplified queries - will be implemented later when order structure is clear
    @Query("SELECT 0 FROM Department d WHERE d.id = :departmentId")
    Long countOrdersByDepartment(@Param("departmentId") Long departmentId);
    
    @Query("SELECT 0.0 FROM Department d WHERE d.id = :departmentId")
    Double getTotalRevenueByDepartment(@Param("departmentId") Long departmentId);
}