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
    ResponseEntity<?> createRecommend(@RequestPart("product_id") String productId,
            @RequestPart(value = "image", required = false) List<MultipartFile> files);
    
    @PostMapping(value = "/internal/product/url", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createRecommendByUrl(@RequestBody CreateRecommendRequest request);

    @PostMapping(value = "/recommend_by_image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> getListRecommend(@RequestPart("image") MultipartFile image);
    
    class CreateRecommendRequest {
        private String product_id;
        private List<String> image_urls;
        
        public CreateRecommendRequest() {}
        
        public CreateRecommendRequest(String productId, List<String> imageUrls) {
            this.product_id = productId;
            this.image_urls = imageUrls;
        }
        
        public String getProduct_id() {
            return product_id;
        }
        
        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }
        
        public List<String> getImage_urls() {
            return image_urls;
        }
        
        public void setImage_urls(List<String> image_urls) {
            this.image_urls = image_urls;
        }
    }
}
