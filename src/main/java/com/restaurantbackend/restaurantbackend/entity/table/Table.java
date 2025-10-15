package com.restaurantbackend.restaurantbackend.entity.table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import com.restaurantbackend.restaurantbackend.entity.table.enums.TableStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@jakarta.persistence.Table(name = "restaurant_table")
public class Table {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "table_seq")
    @SequenceGenerator(name = "table_seq", sequenceName = "restaurant_table_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "table_number", unique = true)
    private String tableNumber;

    @Column(name = "capacity")
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TableStatus status = TableStatus.AVAILABLE;

    @Column(name = "location")
    private String location;

    @Column(name = "next_password", nullable = false)
    private String nextPassword;

    @OneToMany(mappedBy = "table")
    @JsonIgnore
    private List<TableSession> sessions = new ArrayList<>();

    public Table() {}

    public Table(Long id, String tableNumber, Integer capacity, TableStatus status, String location, String nextPassword, List<TableSession> sessions) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
        this.location = location;
        this.nextPassword = nextPassword;
        this.sessions = sessions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNextPassword() {
        return nextPassword;
    }

    public void setNextPassword(String nextPassword) {
        this.nextPassword = nextPassword;
    }

    public List<TableSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<TableSession> sessions) {
        this.sessions = sessions;
    }
}