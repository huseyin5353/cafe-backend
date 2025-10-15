package com.restaurantbackend.restaurantbackend.dto.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableHistoryDTO {
    private Long id;
    private String action;
    private String description;
    private LocalDateTime timestamp;
    private String performedBy;
    private String previousStatus;
    private String newStatus;
    private Long sessionId;
    private String customerInfo;
}

