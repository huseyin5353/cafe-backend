package com.restaurantbackend.restaurantbackend.controller.table;

import com.restaurantbackend.restaurantbackend.dto.table.CreateTableDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableUpdateDTO;
import com.restaurantbackend.restaurantbackend.dto.table.SimpleTableRequest;
import com.restaurantbackend.restaurantbackend.dto.table.TableStatisticsDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableHistoryDTO;
import com.restaurantbackend.restaurantbackend.service.table.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping
    public ResponseEntity<TableDTO> createTable(@RequestBody CreateTableDTO dto) {
        try {
            TableDTO table = tableService.createTable(dto);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/simple")
    public ResponseEntity<TableDTO> createSimpleTable(@RequestBody SimpleTableRequest request) {
        try {
            TableDTO table = tableService.createSimpleTable(request.getLocation());
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TableDTO>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableDTO> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/next-password")
    public ResponseEntity<String> getNextPassword(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(tableService.getNextPassword(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableDTO> updateTable(@PathVariable Long id, @RequestBody TableUpdateDTO dto) {
        try {
            TableDTO updated = tableService.updateTable(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<TableDTO> deactivateTable(@PathVariable Long id) {
        try {
            TableDTO table = tableService.deactivateTable(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<TableDTO> activateTable(@PathVariable Long id) {
        try {
            TableDTO table = tableService.activateTable(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancel-reservation")
    public ResponseEntity<TableDTO> cancelReservation(@PathVariable Long id) {
        try {
            TableDTO table = tableService.cancelReservation(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/reset")
    public ResponseEntity<TableDTO> resetTableStatus(@PathVariable Long id) {
        try {
            TableDTO table = tableService.resetTableStatus(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Toplu işlemler
    @PostMapping("/bulk/delete")
    public ResponseEntity<Void> deleteTables(@RequestBody List<Long> tableIds) {
        try {
            tableService.deleteTables(tableIds);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bulk/status")
    public ResponseEntity<List<TableDTO>> updateTablesStatus(
            @RequestBody List<Long> tableIds, 
            @RequestParam String status) {
        try {
            List<TableDTO> updatedTables = tableService.updateTablesStatus(tableIds, status);
            return ResponseEntity.ok(updatedTables);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Masa istatistikleri
    @GetMapping("/statistics")
    public ResponseEntity<TableStatisticsDTO> getTableStatistics() {
        try {
            TableStatisticsDTO stats = tableService.getTableStatistics();
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // QR kod oluşturma
    @GetMapping("/{id}/qr")
    public ResponseEntity<String> generateQRCode(@PathVariable Long id) {
        try {
            String qrCode = tableService.generateQRCode(id);
            return ResponseEntity.ok(qrCode);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Masa rezervasyonu
    @PostMapping("/{id}/reserve")
    public ResponseEntity<TableDTO> reserveTable(
            @PathVariable Long id,
            @RequestParam String reservedBy,
            @RequestParam String reservedUntil) {
        try {
            TableDTO table = tableService.reserveTable(id, reservedBy, reservedUntil);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/cancel-reservation")
    public ResponseEntity<TableDTO> cancelReservation(@PathVariable Long id) {
        try {
            TableDTO table = tableService.cancelReservation(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Bakım modu
    @PostMapping("/{id}/maintenance")
    public ResponseEntity<TableDTO> setMaintenanceMode(@PathVariable Long id) {
        try {
            TableDTO table = tableService.setMaintenanceMode(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/exit-maintenance")
    public ResponseEntity<TableDTO> exitMaintenanceMode(@PathVariable Long id) {
        try {
            TableDTO table = tableService.exitMaintenanceMode(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Masa geçmişi
    @GetMapping("/{id}/history")
    public ResponseEntity<List<TableHistoryDTO>> getTableHistory(@PathVariable Long id) {
        try {
            List<TableHistoryDTO> history = tableService.getTableHistory(id);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Excel/CSV export
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTables(@RequestParam(defaultValue = "excel") String format) {
        try {
            byte[] data = tableService.exportTables(format);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=tables." + format)
                    .body(data);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Excel/CSV import
    @PostMapping("/import")
    public ResponseEntity<List<TableDTO>> importTables(@RequestBody byte[] fileData) {
        try {
            List<TableDTO> tables = tableService.importTables(fileData);
            return ResponseEntity.ok(tables);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}