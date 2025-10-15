package com.restaurantbackend.restaurantbackend.repository.order;

import com.restaurantbackend.restaurantbackend.entity.order.RestaurantOrder;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    List<RestaurantOrder> findBySession(TableSession session);
}