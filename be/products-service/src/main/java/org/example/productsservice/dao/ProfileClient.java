package org.example.productsservice.dao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "recommend-client", url = "${service.recommend}")
public interface RecommendClient {
    @PostMapping(value = "/internal/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> createRecommend(@RequestPart("product_name") String productName,
            @RequestPart("product_id") String productId,
            @RequestPart("product_price") Double productPrice,
            @RequestPart(value = "image", required = false) List<MultipartFile> files,
            @RequestPart("product_image") String productImage) throws Exception;
}
