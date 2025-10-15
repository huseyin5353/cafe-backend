package com.restaurantbackend.restaurantbackend.repository.table;

import com.restaurantbackend.restaurantbackend.entity.table.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {
    Optional<Table> findByTableNumber(String tableNumber);
}