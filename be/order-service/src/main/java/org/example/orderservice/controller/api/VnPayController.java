package org.example.orderservice.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.CartRequestPayDto;
import org.example.orderservice.dto.PaymentDTO;
import org.example.orderservice.service.VnPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vnpay")
public class VnPayController {

    private final VnPayService vnPayService;
//    private final CartService cartService;
//    @PostMapping
//    public ResponseEntity<PaymentDTO> pay(HttpServletRequest request, @RequestBody CartRequestPayDto cartRequestPayDto)
//            throws JsonProcessingException {
//        return ResponseEntity.ok(vnPayService.createVnPayPayment(request, cartRequestPayDto));
//    }
//    @GetMapping("/vn-pay-callback")
//    public ResponseEntity<CartRequestPayDto> payCallbackHandler(HttpServletRequest request) throws JsonProcessingException {
//        try {
//            String status = request.getParameter("vnp_ResponseCode");
//            String orderInfo = request.getParameter("vnp_OrderInfo");
//            ObjectMapper objectMapper = new ObjectMapper();
//            CartRequestPayDto a =  objectMapper.readValue(orderInfo, CartRequestPayDto.class);
//            if (status.equals("00")) {
//                cartService.payCart(a);
//                return ResponseEntity.ok(a);
//            } else {
//                return ResponseEntity.badRequest().body(a);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//    }
}
