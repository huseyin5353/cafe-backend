package com.restaurantbackend.restaurantbackend.controller.department;

import com.restaurantbackend.restaurantbackend.dto.department.DepartmentDTO;
import com.restaurantbackend.restaurantbackend.service.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO department = departmentService.createDepartment(departmentDTO);
        return new ResponseEntity<>(department, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DepartmentDTO>> getActiveDepartments() {
        List<DepartmentDTO> activeDepartments = departmentService.getActiveDepartments();
        return ResponseEntity.ok(activeDepartments);
    }

    @GetMapping("/archived")
    public ResponseEntity<List<DepartmentDTO>> getArchivedDepartments() {
        List<DepartmentDTO> archivedDepartments = departmentService.getArchivedDepartments();
        return ResponseEntity.ok(archivedDepartments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        DepartmentDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(updatedDepartment);
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<DepartmentDTO> toggleDepartmentStatus(@PathVariable Long id) {
        DepartmentDTO updatedDepartment = departmentService.toggleDepartmentStatus(id);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
        }
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<DepartmentDTO> restoreDepartment(@PathVariable Long id) {
        try {
            DepartmentDTO restored = departmentService.restoreDepartment(id);
            return ResponseEntity.ok(restored);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
        }
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentlyDeleteDepartment(@PathVariable Long id) {
        try {
            departmentService.permanentlyDeleteDepartment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
        }
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<DepartmentDTO> getDepartmentStatistics(@PathVariable Long id) {
        DepartmentDTO statistics = departmentService.getDepartmentStatistics(id);
        return ResponseEntity.ok(statistics);
    }
}