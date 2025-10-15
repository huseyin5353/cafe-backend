package com.restaurantbackend.restaurantbackend.dto.session;

public class PlaceOrderRequestDTO {
    
    private Long participantId;
    private Long menuItemId;
    private Integer quantity;

    public PlaceOrderRequestDTO() {}

    public PlaceOrderRequestDTO(Long participantId, Long menuItemId, Integer quantity) {
        this.participantId = participantId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}











