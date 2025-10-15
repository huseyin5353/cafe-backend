package com.restaurantbackend.restaurantbackend.dto.table;

import java.time.LocalDateTime;
import java.util.List;

public class TableSessionDTO {
    
    private Long id;
    private String password;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean active;
    private Long tableId;
    private String tableNumber;
    
    // Session detayları için ek alanlar
    private List<ParticipantDTO> participants;
    private Integer totalParticipants;
    private Integer totalOrders;
    private Double totalRevenue;
    private String duration;

    public TableSessionDTO() {}

    public TableSessionDTO(Long id, String password, LocalDateTime startTime, LocalDateTime endTime, Boolean active, Long tableId, String tableNumber, List<ParticipantDTO> participants, Integer totalParticipants, Integer totalOrders, Double totalRevenue, String duration) {
        this.id = id;
        this.password = password;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.participants = participants;
        this.totalParticipants = totalParticipants;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public Integer getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Integer totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public static class ParticipantDTO {
        private Long id;
        private String customerName;
        private LocalDateTime joinedAt;
        private LocalDateTime leftAt;
        private Double totalSpent;
        private Integer orderCount;
        private List<OrderDTO> orders;

        public ParticipantDTO() {}

        public ParticipantDTO(Long id, String customerName, LocalDateTime joinedAt, LocalDateTime leftAt, Double totalSpent, Integer orderCount, List<OrderDTO> orders) {
            this.id = id;
            this.customerName = customerName;
            this.joinedAt = joinedAt;
            this.leftAt = leftAt;
            this.totalSpent = totalSpent;
            this.orderCount = orderCount;
            this.orders = orders;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public LocalDateTime getJoinedAt() {
            return joinedAt;
        }

        public void setJoinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
        }

        public LocalDateTime getLeftAt() {
            return leftAt;
        }

        public void setLeftAt(LocalDateTime leftAt) {
            this.leftAt = leftAt;
        }

        public Double getTotalSpent() {
            return totalSpent;
        }

        public void setTotalSpent(Double totalSpent) {
            this.totalSpent = totalSpent;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }

        public List<OrderDTO> getOrders() {
            return orders;
        }

        public void setOrders(List<OrderDTO> orders) {
            this.orders = orders;
        }
    }
    
    public static class OrderDTO {
        private Long id;
        private String menuItemName;
        private Integer quantity;
        private Double unitPrice;
        private Double totalPrice;
        private String status;
        private LocalDateTime orderedAt;

        public OrderDTO() {}

        public OrderDTO(Long id, String menuItemName, Integer quantity, Double unitPrice, Double totalPrice, String status, LocalDateTime orderedAt) {
            this.id = id;
            this.menuItemName = menuItemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.status = status;
            this.orderedAt = orderedAt;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMenuItemName() {
            return menuItemName;
        }

        public void setMenuItemName(String menuItemName) {
            this.menuItemName = menuItemName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getOrderedAt() {
            return orderedAt;
        }

        public void setOrderedAt(LocalDateTime orderedAt) {
            this.orderedAt = orderedAt;
        }
    }
}