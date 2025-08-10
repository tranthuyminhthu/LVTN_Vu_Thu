package org.example.orderservice.dao;

import org.example.orderservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDao extends JpaRepository<OrderEntity, String> {
    Page<OrderEntity> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    Page<OrderEntity> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Page<OrderEntity> findAllByVendorIdAndStatusOrderByCreatedAtDesc(String vendorId, String status, Pageable pageable);

    // Thống kê doanh thu theo vendor và khoảng thời gian
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Double getRevenueByVendorAndDateRange(@Param("vendorId") String vendorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Đếm số đơn hàng theo vendor và khoảng thời gian
    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Long getOrderCountByVendorAndDateRange(@Param("vendorId") String vendorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Thống kê doanh thu theo tháng
    @Query("SELECT YEAR(o.createdAt) as year, MONTH(o.createdAt) as month, SUM(o.totalAmount) as revenue, COUNT(o) as orderCount FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED' AND YEAR(o.createdAt) = :year GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyRevenueByVendor(@Param("vendorId") String vendorId, @Param("year") Integer year);

    // Thống kê doanh thu theo ngày
    @Query("SELECT CAST(o.createdAt AS date) as date, SUM(o.totalAmount) as revenue, COUNT(o) as orderCount FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED' AND o.createdAt BETWEEN :startDate AND :endDate GROUP BY CAST(o.createdAt AS date) ORDER BY date DESC")
    List<Object[]> getDailyRevenueByVendor(@Param("vendorId") String vendorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Top sản phẩm bán chạy
    @Query("SELECT oi.productSku, oi.productName, SUM(oi.quantity) as quantitySold, SUM(oi.totalPrice) as totalRevenue FROM OrderEntity o JOIN o.items oi WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED' AND o.createdAt BETWEEN :startDate AND :endDate GROUP BY oi.productSku, oi.productName ORDER BY quantitySold DESC")
    List<Object[]> getTopProductsByVendor(@Param("vendorId") String vendorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ========== THỐNG KÊ TOÀN BỘ (KHÔNG FILTER NGÀY) ==========

    // Thống kê doanh thu toàn bộ theo vendor
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED'")
    Double getTotalRevenueByVendor(@Param("vendorId") String vendorId);

    // Đếm số đơn hàng toàn bộ theo vendor
    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED'")
    Long getTotalOrderCountByVendor(@Param("vendorId") String vendorId);

    // Thống kê doanh thu theo tháng toàn bộ
    @Query("SELECT YEAR(o.createdAt) as year, MONTH(o.createdAt) as month, SUM(o.totalAmount) as revenue, COUNT(o) as orderCount FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED' GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) ORDER BY year DESC, month DESC")
    List<Object[]> getTotalMonthlyRevenueByVendor(@Param("vendorId") String vendorId);

    // Top sản phẩm bán chạy toàn bộ
    @Query("SELECT oi.productSku, oi.productName, SUM(oi.quantity) as quantitySold, SUM(oi.totalPrice) as totalRevenue FROM OrderEntity o JOIN o.items oi WHERE o.vendorId = :vendorId AND o.status = 'RECEIVED' GROUP BY oi.productSku, oi.productName ORDER BY quantitySold DESC")
    List<Object[]> getTotalTopProductsByVendor(@Param("vendorId") String vendorId);

    // Thống kê theo trạng thái đơn hàng
    @Query("SELECT o.status, COUNT(o) as count, SUM(o.totalAmount) as revenue FROM OrderEntity o WHERE o.vendorId = :vendorId GROUP BY o.status")
    List<Object[]> getOrderStatusStatsByVendor(@Param("vendorId") String vendorId);
} 
