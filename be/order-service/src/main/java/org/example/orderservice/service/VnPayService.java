package org.example.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.config.VnPayConfig;
import org.example.orderservice.dto.CartRequestPayDto;
import org.example.orderservice.dto.PaymentDTO;
import org.example.orderservice.dto.VnpayReturnParams;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VnPayService {

    private final VnPayConfig vnPayConfig;
    public PaymentDTO createVnPayPayment(String orderId, Long amount) throws JsonProcessingException {
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount*100L));
        vnpParamsMap.put("vnp_IpAddr", "127.0.0.1");
        vnpParamsMap.put("vnp_OrderInfo", orderId);
        vnpParamsMap.put("vnp_TxnRef", orderId);
        //build query url
        String queryUrl = VnPayConfig.getPaymentURL(vnpParamsMap, true);
        String hashData = VnPayConfig.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    public boolean verifyVnPaySignature(Map<String, String> params) {
        if (params == null || !params.containsKey("vnp_SecureHash")) {
            return false;
        }
        // Lấy và loại bỏ vnp_SecureHash, vnp_SecureHashType khỏi params để build chuỗi kiểm tra
        String vnpSecureHash = params.get("vnp_SecureHash");
        Map<String, String> paramsForHash = new java.util.HashMap<>(params);
        paramsForHash.remove("vnp_SecureHash");
        paramsForHash.remove("vnp_SecureHashType");

        // Build chuỗi dữ liệu theo thứ tự alphabet
        String hashData = VnPayConfig.getPaymentURL(paramsForHash, false);

        // Tạo hash từ secretKey
        String calculatedHash = VnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        return vnpSecureHash.equalsIgnoreCase(calculatedHash);
    }
}
