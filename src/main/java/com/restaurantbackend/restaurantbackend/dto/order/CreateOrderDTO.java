package com.restaurantbackend.restaurantbackend.dto.order;

import java.util.List;

public class CreateOrderDTO {
    private Long sessionId;
    private String customerName;
    private Long participantId;
    private String tableNumber;
    private String notes;
    private List<CreateOrderItemDTO> orderItems;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<CreateOrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<CreateOrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}






