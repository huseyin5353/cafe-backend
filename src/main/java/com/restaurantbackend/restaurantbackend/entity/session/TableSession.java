package com.restaurantbackend.restaurantbackend.entity.session;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.restaurantbackend.restaurantbackend.entity.order.RestaurantOrder;
import com.restaurantbackend.restaurantbackend.entity.table.Table;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@jakarta.persistence.Table(name = "table_session")
public class TableSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_seq")
    @SequenceGenerator(name = "session_seq", sequenceName = "table_session_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String password;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RestaurantOrder> orders = new ArrayList<>();
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SessionParticipant> participants = new ArrayList<>();

    public TableSession() {}

    public TableSession(Long id, String password, LocalDateTime startTime, LocalDateTime endTime, boolean active, Table table, List<RestaurantOrder> orders, List<SessionParticipant> participants) {
        this.id = id;
        this.password = password;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
        this.table = table;
        this.orders = orders;
        this.participants = participants;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public List<RestaurantOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<RestaurantOrder> orders) {
        this.orders = orders;
    }

    public List<SessionParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<SessionParticipant> participants) {
        this.participants = participants;
    }
}