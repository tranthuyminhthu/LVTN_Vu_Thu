package org.example.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dao.*;
import org.example.orderservice.dto.*;
import org.example.orderservice.entity.OrderEntity;
import org.example.orderservice.entity.OrderItemEntity;
import org.example.orderservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.example.orderservice.entity.OrderStatusHistory;
import org.example.orderservice.dto.VendorRevenueStatsDto;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderDao orderDao;
    private final OrderStatusHistoryDao orderStatusHistoryDao;
    private final UserContext userContext;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final GHNClient ghnClient;
    private final ProfileClient profileClient;
    private final VnPayService vnPayService;

    @Value("${variable.ghn-token}")
    private String ghnToken;

    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto request) {
        String userId = userContext.getUserId();
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverEmail(request.getReceiverEmail());
        order.setNote(request.getNote());
        order.setFee(request.getFee() != null ? request.getFee() : 0.0);
        order.setReceiverAddress(request.getShippingAddress());
        order.setReceiverWardCode(request.getReceiverWardCode());
        order.setReceiverDistrictId(request.getReceiverDistrictId());
        order.setVendorId(request.getVendorId());

        List<OrderItemEntity> items = request.getItems().stream().map(itemDto -> {
            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setProductSku(itemDto.getProductSku());
            item.setProductName(itemDto.getProductName());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(itemDto.getPrice());
            item.setImage(itemDto.getImage());
            item.setTotalPrice(itemDto.getPrice() * itemDto.getQuantity());
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);
        order.setTotalAmount(items.stream().mapToDouble(OrderItemEntity::getTotalPrice).sum());
        OrderResponseDto response = new OrderResponseDto();
        OrderEntity savedOrder = orderDao.save(order);


        response.setOrderId(savedOrder.getId());
        BeanUtils.copyProperties(savedOrder, response);
        response.setItems(savedOrder.getItems().stream().map(item -> {
            OrderResponseDto.OrderItemDto dto = new OrderResponseDto.OrderItemDto();
            BeanUtils.copyProperties(item, dto);
            return dto;
        }).collect(Collectors.toList()));
        response.setReceiverName(savedOrder.getReceiverName());
        response.setReceiverPhone(savedOrder.getReceiverPhone());
        response.setReceiverEmail(savedOrder.getReceiverEmail());
        response.setNote(savedOrder.getNote());

        // Lưu lịch sử trạng thái đầu tiên
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrderId(savedOrder.getId());
        history.setStatus(savedOrder.getStatus());
        history.setNote("Đơn hàng vừa được tạo");
        history.setChangedAt(savedOrder.getCreatedAt());
        orderStatusHistoryDao.save(history);

        // delete cart items after order creation
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<String> cartIds = request.getItems().stream()
                .map(OrderCreateRequestDto.OrderItemDto::getCartItemId)
                .collect(Collectors.toList());
            cartClient.deleteCart(cartIds);
        }
        // if payment method is VNPay, create payment URL
        if (request.getPaymentMethod().equals("VNPAY")) {
            try {
                PaymentDTO paymentDTO = vnPayService.createVnPayPayment(order.getId(), order.getTotalAmount().longValue());
                response.setPaymentUrl(paymentDTO.paymentUrl);
                order.setPaymentStatus("PENDING");
            } catch (Exception e) {
                throw new RuntimeException("Error creating VNPay payment: " + e.getMessage());
            }
        }

        return response;
    }

    public OrderListResponseDto getOrders(Integer page, Integer size, String status) {
        Page<OrderEntity> orderPage = createPageable(page, size, status);
        OrderListResponseDto response = new OrderListResponseDto();
        response.setOrders(orderPage.getContent().stream().map(order -> {
            OrderResponseDto dto = new OrderResponseDto();
            dto.setOrderId(order.getId());
            BeanUtils.copyProperties(order, dto);
            return dto;
        }).collect(java.util.stream.Collectors.toList()));
        response.setPage(orderPage.getNumber());
        response.setSize(orderPage.getSize());
        response.setTotalElements(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());
        return response;
    }

    public OrderListResponseDto getOrdersByVendorId(Integer page, Integer size, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderPage;
        String vendorId = userContext.getUserId();
        orderPage = orderDao.findAllByVendorIdAndStatusOrderByCreatedAtDesc(vendorId, status, pageable);
        OrderListResponseDto response = new OrderListResponseDto();
        response.setOrders(orderPage.getContent().stream().map(order -> {
            OrderResponseDto dto = new OrderResponseDto();
            dto.setOrderId(order.getId());
            BeanUtils.copyProperties(order, dto);
            return dto;
        }).collect(java.util.stream.Collectors.toList()));
        response.setPage(orderPage.getNumber());
        response.setSize(orderPage.getSize());
        response.setTotalElements(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());
        return response;
    }

    public OrderResponseDto getOrderById(String orderId) {
        OrderEntity order = orderDao.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        VendorResponseDto vendor = profileClient.getVendorProfile(order.getVendorId()).getBody();
        OrderResponseDto dto = new OrderResponseDto();
        dto.setSenderDistrictId(vendor.getDistrictId());
        dto.setSenderWardCode(vendor.getWardId());
        dto.setShopId(vendor.getShopId());
        dto.setOrderId(order.getId());
        BeanUtils.copyProperties(order, dto);
        dto.setItems(order.getItems().stream().map(item -> {
            OrderResponseDto.OrderItemDto itemDto = new OrderResponseDto.OrderItemDto();
            BeanUtils.copyProperties(item, itemDto);
            return itemDto;
        }).collect(Collectors.toList()));
        dto.setTimeline(orderStatusHistoryDao.findByOrderIdOrderByChangedAtDesc(orderId)
            .stream().map(history -> {
                OrderStatusHistoryDto hDto = new OrderStatusHistoryDto();
                hDto.setStatus(history.getStatus());
                hDto.setNote(history.getNote());
                hDto.setChangedAt(history.getChangedAt());
                return hDto;
            }).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    public OrderEntity updateOrderStatus(String orderId, String status, String note) {
        OrderEntity order = orderDao.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        order.setUpdatedAt(java.time.LocalDateTime.now());
        orderDao.save(order);
        // Lưu lịch sử
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrderId(orderId);
        history.setStatus(status);
        history.setNote(note);
        history.setChangedAt(java.time.LocalDateTime.now());
        orderStatusHistoryDao.save(history);
        return order;
    }

    public OrderListResponseDto getMyOrder(Integer page, Integer size) {
        String userId = userContext.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orders = orderDao.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        OrderListResponseDto response = new OrderListResponseDto();
        response.setOrders(orders.stream().map(order -> {
            OrderResponseDto dto = new OrderResponseDto();
            dto.setOrderId(order.getId());
            BeanUtils.copyProperties(order, dto);
            dto.setImage(order.getItems().isEmpty() ? null : order.getItems().getFirst().getImage());
            return dto;
        }).collect(Collectors.toList()));
        response.setPage(0);
        response.setSize(orders.getSize());
        response.setTotalElements(orders.getTotalElements());
        response.setTotalPages(orders.getTotalPages());
        return response;
    }


    @Transactional
    public void changeStatusToAccept(String orderId) {
        updateOrderStatus(orderId, "PROCESSING", "Đơn hàng của bạn đang được đóng gói");
    }

    @Transactional
    public void changeStatusToReceive(String orderId) {
        updateOrderStatus(orderId, "RECEIVED", "Đơn hàng đã được nhận thành công.");
    }

    @Transactional
    public void changeStatusToCancel(String orderId, String note) {
        updateOrderStatus(orderId, "CANCELLED", "Bạn đã hủy đơn hàng thành công.");
        OrderEntity order = orderDao.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setNote(note != null ? note : "Đơn hàng đã bị hủy bởi người dùng");
        orderDao.save(order);
    }

    @Transactional
    public Map<Object, Object> changeStatusToDelivering(OrderAcceptRequestDto order) {
        OrderEntity orderEntity = updateOrderStatus(order.getOrderId(), "DELIVERING", "Đơn hàng đã được chấp nhận" );
        // Cập nhật số lượng sản phẩm trong kho
        List<ProductItemQuantityDto> productItemQuantityDto = orderDao.findById(order.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"))
                .getItems().stream()
                .map(item -> {
                    ProductItemQuantityDto dto = new ProductItemQuantityDto();
                    dto.setSku(item.getProductSku());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                }).collect(Collectors.toList());
        productClient.updateProductVariant(productItemQuantityDto);
        VendorResponseDto vendor = profileClient.getVendorProfile(orderEntity.getVendorId()).getBody();
        OrderGHNRequestDto ghnRequest = new OrderGHNRequestDto();
        BeanUtils.copyProperties(order, ghnRequest);
        // set info receiver
        ghnRequest.setTo_name(orderEntity.getReceiverName());
        ghnRequest.setTo_phone(orderEntity.getReceiverPhone());
        ghnRequest.setTo_address(orderEntity.getReceiverAddress());
        ghnRequest.setTo_ward_code(orderEntity.getReceiverWardCode());
        ghnRequest.setTo_district_id(orderEntity.getReceiverDistrictId());
        // set info sender
        ghnRequest.setFrom_name(vendor.getName());
        ghnRequest.setFrom_phone(vendor.getPhone());
        ghnRequest.setFrom_address(vendor.getAddress());
        ghnRequest.setFrom_ward_name(vendor.getWardName());
        ghnRequest.setFrom_district_name(vendor.getDistrictName());
        ghnRequest.setFrom_province_name(vendor.getProvinceName());
        ghnRequest.setReturn_phone(vendor.getPhone());
        ghnRequest.setReturn_address(vendor.getAddress());
        ghnRequest.setReturn_district_id(vendor.getDistrictId());
        ghnRequest.setReturn_ward_code(vendor.getWardId());
        // set info order
        ghnRequest.setClient_order_code(order.getOrderId());
        ghnRequest.setWeight(200);
        ghnRequest.setLength(20);
        ghnRequest.setWidth(20);
        ghnRequest.setHeight(50);
        ghnRequest.setService_type_id(2); // ecommerce delivery
        ghnRequest.setPayment_type_id(2);
        ghnRequest.setCod_amount(orderEntity.getTotalAmount().intValue());
        ghnRequest.setService_id(order.getServiceId());
        ghnRequest.setRequired_note(order.getRequiredNote());
        ghnRequest.setContent(order.getContent());
        ghnRequest.setCoupon(order.getCoupon());
        ghnRequest.setNote(order.getNote());
        // set items
        ghnRequest.setItems(orderDao.findById(order.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"))
                .getItems().stream()
                .map(item -> {
                    OrderGHNRequestDto.Item itemDto = new OrderGHNRequestDto.Item();
                    itemDto.setName(item.getProductName());
                    itemDto.setCode(item.getProductSku());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPrice(item.getPrice().intValue());
                    return itemDto;
                }).collect(Collectors.toList()));
        return ghnClient.createOrder(ghnRequest, ghnToken, vendor.getShopId()).getBody();
    }

    private Page<OrderEntity> createPageable(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderPage;
        if (status != null && !status.isEmpty()) {
            orderPage = orderDao.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            orderPage = orderDao.findAll(pageable);
        }

        return orderPage;
    }

    /**
     * Lấy thống kê doanh thu cho vendor theo khoảng thời gian
     */
    public VendorRevenueStatsDto getVendorRevenueStats(String vendorId, LocalDateTime startDate, LocalDateTime endDate) {
        VendorRevenueStatsDto stats = new VendorRevenueStatsDto();
        stats.setVendorId(vendorId);
        stats.setStartDate(startDate);
        stats.setEndDate(endDate);

        // Lấy tổng doanh thu
        Double totalRevenue = orderDao.getRevenueByVendorAndDateRange(vendorId, startDate, endDate);
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);

        // Lấy số lượng đơn hàng
        Long totalOrders = orderDao.getOrderCountByVendorAndDateRange(vendorId, startDate, endDate);
        stats.setTotalOrders(totalOrders != null ? totalOrders : 0L);

        // Tính giá trị đơn hàng trung bình
        if (totalOrders != null && totalOrders > 0 && totalRevenue != null) {
            stats.setAverageOrderValue(totalRevenue / totalOrders);
        } else {
            stats.setAverageOrderValue(0.0);
        }

        // Lấy thống kê theo ngày
        List<Object[]> dailyStats = orderDao.getDailyRevenueByVendor(vendorId, startDate, endDate);
        List<VendorRevenueStatsDto.DailyRevenueDto> dailyRevenue = dailyStats.stream()
            .map(row -> {
                VendorRevenueStatsDto.DailyRevenueDto dto = new VendorRevenueStatsDto.DailyRevenueDto();
                // Chuyển đổi java.sql.Date sang LocalDateTime
                if (row[0] instanceof Date) {
                    Date sqlDate = (Date) row[0];
                    dto.setDate(sqlDate.toLocalDate().atStartOfDay());
                } else if (row[0] instanceof java.time.LocalDate) {
                    java.time.LocalDate localDate = (java.time.LocalDate) row[0];
                    dto.setDate(localDate.atStartOfDay());
                } else {
                    dto.setDate((LocalDateTime) row[0]);
                }
                dto.setRevenue((Double) row[1]);
                dto.setOrderCount((Long) row[2]);
                return dto;
            })
            .collect(Collectors.toList());
        stats.setDailyRevenue(dailyRevenue);

        // Lấy top sản phẩm bán chạy
        List<Object[]> topProducts = orderDao.getTopProductsByVendor(vendorId, startDate, endDate);
        List<VendorRevenueStatsDto.TopProductDto> topProductDtos = topProducts.stream()
            .limit(10) // Chỉ lấy top 10
            .map(row -> {
                VendorRevenueStatsDto.TopProductDto dto = new VendorRevenueStatsDto.TopProductDto();
                dto.setProductSku((String) row[0]);
                dto.setProductName((String) row[1]);
                dto.setQuantitySold((Long) row[2]);
                dto.setTotalRevenue((Double) row[3]);
                return dto;
            })
            .collect(Collectors.toList());
        stats.setTopProducts(topProductDtos);

        return stats;
    }

    /**
     * Lấy thống kê doanh thu theo tháng cho vendor
     */
    public VendorRevenueStatsDto getVendorMonthlyRevenueStats(String vendorId, Integer year) {
        VendorRevenueStatsDto stats = new VendorRevenueStatsDto();
        stats.setVendorId(vendorId);

        // Lấy thống kê theo tháng
        List<Object[]> monthlyStats = orderDao.getMonthlyRevenueByVendor(vendorId, year);
        List<VendorRevenueStatsDto.MonthlyRevenueDto> monthlyRevenue = monthlyStats.stream()
            .map(row -> {
                VendorRevenueStatsDto.MonthlyRevenueDto dto = new VendorRevenueStatsDto.MonthlyRevenueDto();
                dto.setYear((Integer) row[0]);
                dto.setMonth((Integer) row[1]);
                dto.setRevenue((Double) row[2]);
                dto.setOrderCount((Long) row[3]);
                return dto;
            })
            .collect(Collectors.toList());
        stats.setMonthlyRevenue(monthlyRevenue);

        // Tính tổng doanh thu và đơn hàng
        Double totalRevenue = monthlyRevenue.stream()
            .mapToDouble(VendorRevenueStatsDto.MonthlyRevenueDto::getRevenue)
            .sum();
        Long totalOrders = monthlyRevenue.stream()
            .mapToLong(VendorRevenueStatsDto.MonthlyRevenueDto::getOrderCount)
            .sum();

        stats.setTotalRevenue(totalRevenue);
        stats.setTotalOrders(totalOrders);
        stats.setAverageOrderValue(totalOrders > 0 ? totalRevenue / totalOrders : 0.0);

        return stats;
    }

    /**
     * Lấy thống kê doanh thu toàn bộ cho vendor (không filter theo ngày)
     */
    public VendorRevenueStatsDto getVendorTotalRevenueStats(String vendorId) {
        VendorRevenueStatsDto stats = new VendorRevenueStatsDto();
        stats.setVendorId(vendorId);

        // Lấy tổng doanh thu toàn bộ
        Double totalRevenue = orderDao.getTotalRevenueByVendor(vendorId);
        stats.setTotalRevenue(totalRevenue != null ? totalRevenue : 0.0);

        // Lấy số lượng đơn hàng toàn bộ
        Long totalOrders = orderDao.getTotalOrderCountByVendor(vendorId);
        stats.setTotalOrders(totalOrders != null ? totalOrders : 0L);

        // Tính giá trị đơn hàng trung bình
        if (totalOrders != null && totalOrders > 0 && totalRevenue != null) {
            stats.setAverageOrderValue(totalRevenue / totalOrders);
        } else {
            stats.setAverageOrderValue(0.0);
        }

        // Lấy thống kê theo tháng toàn bộ
        List<Object[]> monthlyStats = orderDao.getTotalMonthlyRevenueByVendor(vendorId);
        List<VendorRevenueStatsDto.MonthlyRevenueDto> monthlyRevenue = monthlyStats.stream()
            .map(row -> {
                VendorRevenueStatsDto.MonthlyRevenueDto dto = new VendorRevenueStatsDto.MonthlyRevenueDto();
                dto.setYear((Integer) row[0]);
                dto.setMonth((Integer) row[1]);
                dto.setRevenue((Double) row[2]);
                dto.setOrderCount((Long) row[3]);
                return dto;
            })
            .collect(Collectors.toList());
        stats.setMonthlyRevenue(monthlyRevenue);

        // Lấy top sản phẩm bán chạy toàn bộ
        List<Object[]> topProducts = orderDao.getTotalTopProductsByVendor(vendorId);
        List<VendorRevenueStatsDto.TopProductDto> topProductDtos = topProducts.stream()
            .limit(10) // Chỉ lấy top 10
            .map(row -> {
                VendorRevenueStatsDto.TopProductDto dto = new VendorRevenueStatsDto.TopProductDto();
                dto.setProductSku((String) row[0]);
                dto.setProductName((String) row[1]);
                dto.setQuantitySold((Long) row[2]);
                dto.setTotalRevenue((Double) row[3]);
                return dto;
            })
            .collect(Collectors.toList());
        stats.setTopProducts(topProductDtos);

        // Lấy thống kê theo trạng thái đơn hàng
        List<Object[]> statusStats = orderDao.getOrderStatusStatsByVendor(vendorId);
        List<VendorRevenueStatsDto.OrderStatusStatsDto> orderStatusStats = statusStats.stream()
            .map(row -> {
                VendorRevenueStatsDto.OrderStatusStatsDto dto = new VendorRevenueStatsDto.OrderStatusStatsDto();
                dto.setStatus((String) row[0]);
                dto.setCount((Long) row[1]);
                dto.setRevenue((Double) row[2]);
                return dto;
            })
            .collect(Collectors.toList());
        stats.setOrderStatusStats(orderStatusStats);

        return stats;
    }

    public boolean verifyPayment(VnpayReturnParams request) {
        Map<String, String> params = new java.util.HashMap<>();
        params.put("vnp_Amount", request.getVnp_Amount());
        params.put("vnp_BankCode", request.getVnp_BankCode());
        params.put("vnp_BankTranNo", request.getVnp_BankTranNo());
        params.put("vnp_CardType", request.getVnp_CardType());
        params.put("vnp_OrderInfo", request.getVnp_OrderInfo());
        params.put("vnp_PayDate", request.getVnp_PayDate());
        params.put("vnp_ResponseCode", request.getVnp_ResponseCode());
        params.put("vnp_SecureHash", request.getVnp_SecureHash());
        params.put("vnp_TmnCode", request.getVnp_TmnCode());
        params.put("vnp_TransactionNo", request.getVnp_TransactionNo());
        params.put("vnp_TransactionStatus", request.getVnp_TransactionStatus());
        params.put("vnp_TxnRef", request.getVnp_TxnRef());
        boolean isPaid = vnPayService.verifyVnPaySignature(params);
        OrderEntity order = orderDao.findById(request.getVnp_TxnRef())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (isPaid && request.getVnp_ResponseCode().equals("00")) {
            order.setPaymentStatus("PAID");
            order.setPaymentTransactionId(request.getVnp_TransactionNo());
            order.setUpdatedAt(LocalDateTime.now());
            orderDao.save(order);
            return true;
        } else {
            return false;
        }
    }
} 
