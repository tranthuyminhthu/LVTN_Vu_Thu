package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dao.VendorDao;
import org.example.userservice.dao.OrderClient;
import org.example.userservice.dto.VendorRequestDto;
import org.example.userservice.dto.VendorListResponseDto;
import org.example.userservice.dto.VendorRevenueStatsDto;
import org.example.userservice.entity.VendorEntity;
import org.example.userservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class VendorService {
    private final VendorDao vendorDao;
    private final OrderClient orderClient;

    public List<VendorEntity> getAllVendors() {
        return vendorDao.findAll();

    }

    public VendorEntity createVendor(VendorRequestDto vendor) {
        VendorEntity vendorEntity = new VendorEntity();
        BeanUtils.copyProperties(vendor, vendorEntity);
        vendorEntity.setCreatedAt(LocalDateTime.now());
        vendorEntity.setUpdatedAt(LocalDateTime.now());
        vendorEntity.setStatus("PENDING");
        return vendorDao.save(vendorEntity);
    }

    public VendorEntity getVendorById(String vendorId) {
        return vendorDao.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found for ID: " + vendorId));
    }

    public List<VendorEntity> getByStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return vendorDao.findByStatus(status);
    }

    public VendorListResponseDto getVendors(Integer page, Integer size, String status) {
        Page<VendorEntity> vendorPage = createPageable(page, size, status);
        VendorListResponseDto response = new VendorListResponseDto();
        response.setVendors(vendorPage.getContent());
        response.setPage(vendorPage.getNumber());
        response.setSize(vendorPage.getSize());
        response.setTotalElements(vendorPage.getTotalElements());
        response.setTotalPages(vendorPage.getTotalPages());
        return response;
    }

    private Page<VendorEntity> createPageable(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VendorEntity> vendorPage;
        if (status != null && !status.isEmpty()) {
            vendorPage = vendorDao.findByStatus(status, pageable);
        } else {
            vendorPage = vendorDao.findAll(pageable);
        }
        return vendorPage;
    }

    /**
     * Lấy thống kê doanh thu cho vendor theo khoảng thời gian
     */
    public VendorRevenueStatsDto getVendorRevenueStats(String vendorId, LocalDateTime startDate, LocalDateTime endDate) {
        // Kiểm tra vendor tồn tại
        VendorEntity vendor = getVendorById(vendorId);
        
        // Gọi API từ order-service để lấy thống kê
        ResponseEntity<VendorRevenueStatsDto> response = orderClient.getVendorRevenueStats(
            vendorId, 
            startDate.toString(), 
            endDate.toString()
        );
        
        if (response.getBody() != null) {
            VendorRevenueStatsDto stats = response.getBody();
            stats.setVendorId(vendorId);
            stats.setVendorName(vendor.getName());
            stats.setStartDate(startDate);
            stats.setEndDate(endDate);
            return stats;
        }
        
        throw new RuntimeException("Failed to get revenue statistics from order service");
    }

    /**
     * Lấy thống kê doanh thu theo tháng cho vendor
     */
    public VendorRevenueStatsDto getVendorMonthlyRevenueStats(String vendorId, Integer year) {
        // Kiểm tra vendor tồn tại
        VendorEntity vendor = getVendorById(vendorId);
        
        // Gọi API từ order-service để lấy thống kê theo tháng
        ResponseEntity<VendorRevenueStatsDto> response = orderClient.getVendorMonthlyRevenueStats(vendorId, year);
        
        if (response.getBody() != null) {
            VendorRevenueStatsDto stats = response.getBody();
            stats.setVendorId(vendorId);
            stats.setVendorName(vendor.getName());
            return stats;
        }
        
        throw new RuntimeException("Failed to get monthly revenue statistics from order service");
    }

    public VendorRevenueStatsDto getCurrentVendorRevenueStats(LocalDateTime startDate, LocalDateTime endDate) {
        String currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return getVendorRevenueStats(currentUserId, startDate, endDate);
    }

    public VendorRevenueStatsDto getCurrentVendorMonthlyRevenueStats(Integer year) {
        String currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return getVendorMonthlyRevenueStats(currentUserId, year);
    }

    /**
     * Lấy thống kê doanh thu toàn bộ cho vendor (không filter theo ngày)
     */
    public VendorRevenueStatsDto getVendorTotalRevenueStats(String vendorId) {
        // Kiểm tra vendor tồn tại
        VendorEntity vendor = getVendorById(vendorId);
        
        // Gọi API từ order-service để lấy thống kê toàn bộ
        ResponseEntity<VendorRevenueStatsDto> response = orderClient.getVendorTotalRevenueStats(vendorId);
        
        if (response.getBody() != null) {
            VendorRevenueStatsDto stats = response.getBody();
            stats.setVendorId(vendorId);
            stats.setVendorName(vendor.getName());
            return stats;
        }
        
        throw new RuntimeException("Failed to get total revenue statistics from order service");
    }

    /**
     * Lấy thống kê doanh thu toàn bộ cho vendor hiện tại
     */
    public VendorRevenueStatsDto getCurrentVendorTotalRevenueStats() {
        String currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return getVendorTotalRevenueStats(currentUserId);
    }

    /**
     * Accept vendor - chuyển status từ PENDING sang ACCEPTED
     */
    public void acceptVendor(List<String> vendorIds) {
        if (vendorIds == null || vendorIds.isEmpty()) {
            throw new IllegalArgumentException("Vendor IDs cannot be null or empty");
        }
        
        List<VendorEntity> vendors = vendorDao.findAllById(vendorIds);
        if (vendors.isEmpty()) {
            throw new IllegalArgumentException("No vendors found for the given IDs");
        }
        
        if (vendors.size() != vendorIds.size()) {
            throw new IllegalArgumentException("One or more vendors not found");
        }
        
        for (VendorEntity vendor : vendors) {
            if (!"PENDING".equals(vendor.getStatus())) {
                throw new IllegalStateException("Vendor " + vendor.getId() + " is not in PENDING status");
            }
            vendor.setStatus("ACCEPTED");
            vendor.setUpdatedAt(LocalDateTime.now());
        }
        
        vendorDao.saveAll(vendors);
    }
}
