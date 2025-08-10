package org.example.productsservice.dto;

import lombok.Data;

@Data
public class CommentRequestBodyDto {
    private Long productId;
    private String content;
    private Integer rating;
}
