package com.restaurantbackend.restaurantbackend.repository.analytics;

import com.restaurantbackend.restaurantbackend.entity.analytics.ProductStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStatsRepository extends JpaRepository<ProductStats, Long> {

    Optional<ProductStats> findByProductIdAndDate(Long productId, LocalDate date);

    @Query("SELECT ps FROM ProductStats ps WHERE ps.date >= :startDate ORDER BY ps.date DESC")
    List<ProductStats> findByDateAfter(@Param("startDate") LocalDate startDate);

    @Query("SELECT ps FROM ProductStats ps WHERE ps.productId = :productId AND ps.date >= :startDate ORDER BY ps.date DESC")
    List<ProductStats> findByProductIdAndDateAfter(@Param("productId") Long productId, @Param("startDate") LocalDate startDate);

    @Query("SELECT ps.productId, SUM(ps.views) as totalViews, SUM(ps.cartAdds) as totalCartAdds, SUM(ps.orders) as totalOrders " +
           "FROM ProductStats ps WHERE ps.date >= :startDate " +
           "GROUP BY ps.productId " +
           "ORDER BY SUM(ps.views) DESC")
    List<Object[]> getProductStatsSummary(@Param("startDate") LocalDate startDate);

    @Query("SELECT ps.productId, SUM(ps.views) as totalViews, SUM(ps.cartAdds) as totalCartAdds, SUM(ps.orders) as totalOrders " +
           "FROM ProductStats ps WHERE ps.date >= :startDate " +
           "GROUP BY ps.productId " +
           "ORDER BY SUM(ps.orders) DESC")
    List<Object[]> getTopSellingProducts(@Param("startDate") LocalDate startDate);

    @Query("SELECT SUM(ps.views) FROM ProductStats ps WHERE ps.date >= :startDate")
    Long getTotalViews(@Param("startDate") LocalDate startDate);

    @Query("SELECT SUM(ps.cartAdds) FROM ProductStats ps WHERE ps.date >= :startDate")
    Long getTotalCartAdds(@Param("startDate") LocalDate startDate);

    @Query("SELECT SUM(ps.orders) FROM ProductStats ps WHERE ps.date >= :startDate")
    Long getTotalOrders(@Param("startDate") LocalDate startDate);
}
