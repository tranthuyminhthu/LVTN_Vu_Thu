package org.example.orderservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderGHNRequestDto {
    private Integer payment_type_id;
    private String note;
    private String required_note;
    private String from_name;
    private String from_phone;
    private String from_address;
    private String from_ward_name;
    private String from_district_name;
    private String from_province_name;
    private String return_phone;
    private String return_address;
    private Integer return_district_id;
    private String return_ward_code;
    private String client_order_code;
    private String to_name;
    private String to_phone;
    private String to_address;
    private String to_ward_code;
    private Integer to_district_id;
    private Integer cod_amount;
    private String content;
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    private Integer service_id;
    private Integer service_type_id;
    private String coupon;
    private List<Item> items;

    @Data
    public static class Item {
        private String name;
        private String code;
        private Integer quantity;
        private Integer price;
    }
} 
