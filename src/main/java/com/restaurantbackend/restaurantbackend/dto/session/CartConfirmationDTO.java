package com.restaurantbackend.restaurantbackend.dto.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartConfirmationDTO {
    private Long id;
    private Long sessionId;
    private Long participantId;
    private String customerName;
    private boolean isConfirmed;
    private LocalDateTime confirmedAt;
    private String cartHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

