package com.restaurantbackend.restaurantbackend.service.table;// Güncellenmiş TableService (Mapper injection, mapToDTO çağrıları değiştirildi)


import com.restaurantbackend.restaurantbackend.dto.table.CreateTableDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableUpdateDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableStatisticsDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableHistoryDTO;
import com.restaurantbackend.restaurantbackend.entity.table.Table;
import com.restaurantbackend.restaurantbackend.entity.table.enums.TableStatus;
import com.restaurantbackend.restaurantbackend.mapper.table.TableMapper;
import com.restaurantbackend.restaurantbackend.repository.table.TableRepository;
import com.restaurantbackend.restaurantbackend.util.PasswordGenerator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TableService {

    private final TableRepository tableRepository;

    private final TableMapper tableMapper;
    PasswordGenerator passwordGenerator = new PasswordGenerator();

    public TableService(TableRepository tableRepository, TableMapper tableMapper) {
        this.tableRepository = tableRepository;
        this.tableMapper = tableMapper;
    }

    @CacheEvict(value = "tables", allEntries = true)
    @Transactional
    public TableDTO createTable(CreateTableDTO dto) {
        try {
            Table table = tableMapper.toEntity(dto);
            table.setNextPassword(passwordGenerator.generateNumericPassword());
            table.setStatus(dto.getStatus() != null ? dto.getStatus() : TableStatus.AVAILABLE);
            table = tableRepository.save(table);
            return tableMapper.toDTO(table);
        } catch (Exception e) {
            throw new RuntimeException("Masa eklenemedi: " + e.getMessage(), e);
        }
    }

    @CacheEvict(value = "tables", allEntries = true)
    @Transactional
    public TableDTO createSimpleTable(String location) {
        try {
            // Son masa numarasını bul
            List<Table> allTables = tableRepository.findAll();
            int nextTableNumber = 1;
            if (!allTables.isEmpty()) {
                nextTableNumber = allTables.stream()
                    .mapToInt(table -> {
                        try {
                            return Integer.parseInt(table.getTableNumber());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0) + 1;
            }

            Table table = new Table();
            table.setTableNumber(String.valueOf(nextTableNumber));
            table.setCapacity(4); // Varsayılan kapasite
            table.setStatus(TableStatus.AVAILABLE); // Her zaman müsait olarak başla
            table.setLocation(location);
            table.setNextPassword(passwordGenerator.generateNumericPassword());
            
            table = tableRepository.save(table);
            return tableMapper.toDTO(table);
        } catch (Exception e) {
            throw new RuntimeException("Masa eklenemedi: " + e.getMessage(), e);
        }
    }

    // @Cacheable("tables") // Geçici olarak devre dışı
    public List<TableDTO> getAllTables() {
        // Sadece aktif masaları getir (INACTIVE hariç)
        List<Table> tables = tableRepository.findAll().stream()
                .filter(table -> table.getStatus() != TableStatus.INACTIVE)
                .collect(Collectors.toList());
        
        // Masa numarasına göre sırala
        tables.sort((t1, t2) -> {
            try {
                int num1 = Integer.parseInt(t1.getTableNumber());
                int num2 = Integer.parseInt(t2.getTableNumber());
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                // Sayısal olmayan masa numaraları için alfabetik sıralama
                return t1.getTableNumber().compareTo(t2.getTableNumber());
            }
        });
        return tableMapper.toDTOList(tables);
    }

    @Cacheable(value = "tables", key = "#id")
    public Optional<TableDTO> getTableById(Long id) {
        return tableRepository.findById(id).map(tableMapper::toDTO);
    }

    public String getNextPassword(Long tableId) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        return table.getNextPassword();
    }

    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public TableDTO updateTable(Long id, TableUpdateDTO dto) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));

        tableMapper.updateEntityFromDTO(table, dto);
        table = tableRepository.save(table);
        System.out.println("✅ Masa güncellendi - ID: " + id + ", Masa No: " + table.getTableNumber() + ", Konum: " + table.getLocation());
        return tableMapper.toDTO(table);
    }

    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public TableDTO deactivateTable(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        // Masa pasif hale getir
        table.setStatus(TableStatus.INACTIVE);
        table = tableRepository.save(table);
        
        System.out.println("✅ Masa pasif hale getirildi - ID: " + id + ", Masa No: " + table.getTableNumber());
        return tableMapper.toDTO(table);
    }

    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public TableDTO activateTable(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        // Masa aktif hale getir
        table.setStatus(TableStatus.AVAILABLE);
        table.setNextPassword(passwordGenerator.generateNumericPassword());
        table = tableRepository.save(table);
        
        System.out.println("✅ Masa aktif hale getirildi - ID: " + id + ", Masa No: " + table.getTableNumber());
        return tableMapper.toDTO(table);
    }

    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public TableDTO cancelReservation(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        // Sadece rezerve edilmiş masaların rezervasyonunu iptal et
        if (table.getStatus() != TableStatus.RESERVED) {
            throw new RuntimeException("Bu masa rezerve edilmemiş!");
        }
        
        // Rezervasyonu iptal et ve müsait hale getir
        table.setStatus(TableStatus.AVAILABLE);
        table.setNextPassword(passwordGenerator.generateNumericPassword());
        table = tableRepository.save(table);
        
        System.out.println("✅ Rezervasyon iptal edildi - ID: " + id + ", Masa No: " + table.getTableNumber());
        return tableMapper.toDTO(table);
    }
    
    @Transactional
    public TableDTO resetTableStatus(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        // Masayı AVAILABLE yap ve yeni şifre ata
        table.setStatus(TableStatus.AVAILABLE);
        table.setNextPassword(passwordGenerator.generateNumericPassword());
        table = tableRepository.save(table);
        
        System.out.println("✅ Masa durumu sıfırlandı - ID: " + id + ", Status: " + table.getStatus());
        
        return tableMapper.toDTO(table);
    }

    // Toplu işlemler
    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public void deleteTables(List<Long> tableIds) {
        for (Long id : tableIds) {
            tableRepository.deleteById(id);
        }
        System.out.println("✅ Toplu masa silme işlemi tamamlandı: " + tableIds.size() + " masa silindi");
    }

    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public List<TableDTO> updateTablesStatus(List<Long> tableIds, String status) {
        TableStatus newStatus = TableStatus.valueOf(status);
        List<Table> tables = tableRepository.findAllById(tableIds);
        
        for (Table table : tables) {
            table.setStatus(newStatus);
            if (newStatus == TableStatus.AVAILABLE) {
                table.setNextPassword(passwordGenerator.generateNumericPassword());
            }
        }
        
        List<Table> savedTables = tableRepository.saveAll(tables);
        System.out.println("✅ Toplu durum güncelleme tamamlandı: " + savedTables.size() + " masa güncellendi");
        
        return savedTables.stream()
                .map(tableMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Masa istatistikleri
    public TableStatisticsDTO getTableStatistics() {
        List<Table> allTables = tableRepository.findAll();
        
        int totalTables = allTables.size();
        int availableTables = (int) allTables.stream().filter(t -> t.getStatus() == TableStatus.AVAILABLE).count();
        int occupiedTables = (int) allTables.stream().filter(t -> t.getStatus() == TableStatus.OCCUPIED).count();
        int reservedTables = (int) allTables.stream().filter(t -> t.getStatus() == TableStatus.RESERVED).count();
        int maintenanceTables = (int) allTables.stream().filter(t -> t.getStatus() == TableStatus.MAINTENANCE).count();
        int inactiveTables = (int) allTables.stream().filter(t -> t.getStatus() == TableStatus.INACTIVE).count();
        
        double occupancyRate = totalTables > 0 ? (double) occupiedTables / totalTables * 100 : 0;
        int totalCapacity = allTables.stream().mapToInt(Table::getCapacity).sum();
        int averageCapacity = totalTables > 0 ? totalCapacity / totalTables : 0;
        
        // En çok ve en az kullanılan konumları bul
        String mostUsedLocation = allTables.stream()
                .collect(Collectors.groupingBy(Table::getLocation, Collectors.counting()))
                .entrySet().stream()
                .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .map(e -> e.getKey())
                .orElse("Bilinmiyor");
        
        String leastUsedLocation = allTables.stream()
                .collect(Collectors.groupingBy(Table::getLocation, Collectors.counting()))
                .entrySet().stream()
                .min((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .map(e -> e.getKey())
                .orElse("Bilinmiyor");
        
        return new TableStatisticsDTO(
                totalTables, availableTables, occupiedTables, reservedTables, maintenanceTables, inactiveTables,
                occupancyRate, totalCapacity, averageCapacity, mostUsedLocation, leastUsedLocation,
                0.0, 0 // Bu değerler session servisinden gelecek
        );
    }

    // QR kod oluşturma
    public String generateQRCode(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        // Basit QR kod içeriği - masa numarası ve şifre
        String qrContent = String.format("Masa: %s, Şifre: %s", 
                table.getTableNumber(), table.getNextPassword());
        
        System.out.println("✅ QR kod oluşturuldu - Masa: " + table.getTableNumber());
        return qrContent;
    }

    // Masa rezervasyonu
    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public TableDTO reserveTable(Long id, String reservedBy, String reservedUntil) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new RuntimeException("Masa rezerve edilemez - mevcut durum: " + table.getStatus());
        }
        
        table.setStatus(TableStatus.RESERVED);
        table = tableRepository.save(table);
        
        System.out.println("✅ Masa rezerve edildi - ID: " + id + ", Rezerve eden: " + reservedBy);
        
        return tableMapper.toDTO(table);
    }

    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public TableDTO cancelReservation(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        if (table.getStatus() != TableStatus.RESERVED) {
            throw new RuntimeException("Masa rezerve değil - mevcut durum: " + table.getStatus());
        }
        
        table.setStatus(TableStatus.AVAILABLE);
        table.setNextPassword(passwordGenerator.generateNumericPassword());
        table = tableRepository.save(table);
        
        System.out.println("✅ Rezervasyon iptal edildi - ID: " + id);
        
        return tableMapper.toDTO(table);
    }

    // Bakım modu
    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public TableDTO setMaintenanceMode(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        table.setStatus(TableStatus.MAINTENANCE);
        table = tableRepository.save(table);
        
        System.out.println("✅ Masa bakım moduna alındı - ID: " + id);
        
        return tableMapper.toDTO(table);
    }

    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public TableDTO exitMaintenanceMode(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));
        
        if (table.getStatus() != TableStatus.MAINTENANCE) {
            throw new RuntimeException("Masa bakım modunda değil - mevcut durum: " + table.getStatus());
        }
        
        table.setStatus(TableStatus.AVAILABLE);
        table.setNextPassword(passwordGenerator.generateNumericPassword());
        table = tableRepository.save(table);
        
        System.out.println("✅ Masa bakım modundan çıkarıldı - ID: " + id);
        
        return tableMapper.toDTO(table);
    }

    // Masa geçmişi (şimdilik boş liste döndürüyor)
    public List<TableHistoryDTO> getTableHistory(Long id) {
        // Bu metod session servisi ile entegre edilecek
        return List.of();
    }

    // Excel/CSV export (şimdilik boş byte array döndürüyor)
    public byte[] exportTables(String format) {
        // Bu metod Excel/CSV kütüphanesi ile implement edilecek
        return new byte[0];
    }

    // Excel/CSV import (şimdilik boş liste döndürüyor)
    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public List<TableDTO> importTables(byte[] fileData) {
        // Bu metod Excel/CSV kütüphanesi ile implement edilecek
        return List.of();
    }
}