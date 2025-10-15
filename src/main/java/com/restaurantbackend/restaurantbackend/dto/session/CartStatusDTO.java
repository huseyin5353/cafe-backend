package com.restaurantbackend.restaurantbackend.dto.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartStatusDTO {
    private Long sessionId;
    private List<Map<String, Object>> cartItems;
    private List<CartConfirmationDTO> confirmations;
    private Long totalParticipants;
    private Long confirmedParticipants;
    private boolean allConfirmed;
    private String cartHash;
    private boolean canOrder;
    private String status; // "WAITING_CONFIRMATIONS", "READY_TO_ORDER", "ORDERED"
}

