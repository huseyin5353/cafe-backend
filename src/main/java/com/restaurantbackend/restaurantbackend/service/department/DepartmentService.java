package com.restaurantbackend.restaurantbackend.service.department;

import com.restaurantbackend.restaurantbackend.dto.department.DepartmentDTO;
import com.restaurantbackend.restaurantbackend.entity.department.Department;
import com.restaurantbackend.restaurantbackend.mapper.department.DepartmentMapper;
import com.restaurantbackend.restaurantbackend.repository.department.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Department department = departmentMapper.toEntity(departmentDTO);
        department = departmentRepository.save(department);
        return departmentMapper.toDTO(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        // Basit sorgu kullan
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DepartmentDTO> getActiveDepartments() {
        return departmentRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(departmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DepartmentDTO> getArchivedDepartments() {
        return departmentRepository.findByIsActiveFalseOrderByDeletedAtDesc().stream()
                .map(departmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .map(departmentMapper::toDTO);
    }

    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO departmentDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        // Manuel güncelleme
        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());
        department.setIconName(departmentDTO.getIconName());
        department.setColorCode(departmentDTO.getColorCode());
        department.setActive(departmentDTO.isActive());
        department.setSortOrder(departmentDTO.getSortOrder());
        
        department = departmentRepository.save(department);
        return departmentMapper.toDTO(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        // Soft delete: Sadece isActive = false ve deletedAt set et
        department.setActive(false);
        department.setDeletedAt(LocalDateTime.now());
        departmentRepository.save(department);
        
        System.out.println("🗑️ Departman arşivlendi: " + department.getName() + " (ID: " + id + ")");
    }

    @Transactional
    public void permanentlyDeleteDepartment(Long id) {
        // Hard delete: Gerçekten veritabanından sil (sadece arşivdeki departmanlar için)
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        if (department.isActive()) {
            throw new IllegalStateException("Cannot permanently delete an active department. Archive it first.");
        }
        
        // Check if there are any menu items associated with this department
        Long menuItemCount = departmentRepository.countMenuItemsByDepartment(id);
        if (menuItemCount > 0) {
            throw new IllegalStateException("Cannot delete department with associated menu items. Please reassign or delete menu items first.");
        }
        
        departmentRepository.deleteById(id);
        System.out.println("💥 Departman kalıcı olarak silindi: " + department.getName() + " (ID: " + id + ")");
    }

    @Transactional
    public DepartmentDTO restoreDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        
        if (department.isActive()) {
            throw new IllegalStateException("Department is already active.");
        }
        
        // Restore: isActive = true ve deletedAt = null
        department.setActive(true);
        department.setDeletedAt(null);
        department = departmentRepository.save(department);
        
        System.out.println("♻️ Departman geri getirildi: " + department.getName() + " (ID: " + id + ")");
        return departmentMapper.toDTO(department);
    }

    @Transactional
    public DepartmentDTO toggleDepartmentStatus(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        department.setActive(!department.isActive());
        department = departmentRepository.save(department);
        return departmentMapper.toDTO(department);
    }

    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentStatistics(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        DepartmentDTO dto = departmentMapper.toDTO(department);

        // Calculate statistics
        Long menuItemCount = departmentRepository.countMenuItemsByDepartment(id);
        // TODO: Implement logic to calculate order count and total revenue for the department
        // This would require joining with Order and OrderItem tables based on MenuItem's department_id
        Long orderCount = 0L; // Placeholder
        Double totalRevenue = 0.0; // Placeholder

        dto.setMenuItemCount(menuItemCount);
        dto.setOrderCount(orderCount);
        dto.setTotalRevenue(totalRevenue);

        return dto;
    }
}