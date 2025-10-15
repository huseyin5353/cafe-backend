package com.restaurantbackend.restaurantbackend.dto.table;

import com.restaurantbackend.restaurantbackend.entity.table.enums.TableStatus;

public class CreateTableDTO {
    private String tableNumber;
    private Integer capacity;
    private TableStatus status;
    private String location;

    public CreateTableDTO() {}

    public CreateTableDTO(String tableNumber, Integer capacity, TableStatus status, String location) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
        this.location = location;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}