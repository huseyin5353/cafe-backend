package com.restaurantbackend.restaurantbackend.dto.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableStatisticsDTO {
    private int totalTables;
    private int availableTables;
    private int occupiedTables;
    private int reservedTables;
    private int maintenanceTables;
    private int inactiveTables;
    private double occupancyRate;
    private int totalCapacity;
    private int averageCapacity;
    private String mostUsedLocation;
    private String leastUsedLocation;
    private double averageSessionDuration;
    private int totalSessionsToday;
}
