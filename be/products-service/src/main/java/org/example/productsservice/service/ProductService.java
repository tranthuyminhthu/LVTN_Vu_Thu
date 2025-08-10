package org.example.productsservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.productsservice.constant.Status;
import org.example.productsservice.dao.*;
import org.example.productsservice.dto.*;
import org.example.productsservice.entity.ProductDocument;
import org.example.productsservice.entity.ProductEntity;
import org.example.productsservice.entity.ProductVariantEntity;
import org.example.productsservice.entity.ImageEntity;
import org.example.productsservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductDao productDao;
    private final ProductVariantDao productVariantDao;
    private final UserContext userContext;
    private final S3Service s3Service;
    private final ImageDao imageDao;
    private final RecommendClient recommendClient;
    private final ProfileClient profileClient;
    private final ProductSearchRepository productSearchRepository;
    private final FavoriteDao favoriteDao;
    private final ProductVariantService productVariantService;
    private final NotificationEventService notificationEventService;

    public List<ProductDto> getAllProducts() {
        List<ProductEntity> productEntities = productDao.findAll();
        return productEntities.stream().map(productEntity -> {
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(productEntity, productDto);
            List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(productEntity.getId());
            if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
                productDto.setVariants(productVariantEntities);
            }
            // Map images
            if (productEntity.getImages() != null) {
                List<String> imageDtos = productEntity.getImages().stream().map(ImageEntity::getUrl).toList();
                productDto.setImages(imageDtos);
            }
            return productDto;
        }).toList();
    }

    public ProductListResponseDto getProducts(Integer page, Integer size, String status, String gender) {
        Page<ProductEntity> productPage = createPageable(page, size, status, gender);
        ProductListResponseDto response = new ProductListResponseDto();
        response.setProducts(productPage.getContent().stream().map(productEntity -> {
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(productEntity, productDto);
            List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(productEntity.getId());
            if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
                productDto.setVariants(productVariantEntities);
            }
            // Map images
            if (productEntity.getImages() != null) {
                List<String> imageDtos = productEntity.getImages().stream().map(ImageEntity::getUrl).toList();
                productDto.setImages(imageDtos);
            }
            return productDto;
        }).toList());
        response.setPage(productPage.getNumber());
        response.setSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        return response;
    }

    private Page<ProductEntity> createPageable(int page, int size, String status, String gender) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> productPage;
        
        // Nếu có cả status và gender
        if (status != null && !status.isEmpty() && gender != null && !gender.isEmpty()) {
            productPage = productDao.findByStatusAndGender(status, gender, pageable);
        }
        // Nếu chỉ có status
        else if (status != null && !status.isEmpty()) {
            productPage = productDao.findByStatus(status, pageable);
        }
        // Nếu chỉ có gender
        else if (gender != null && !gender.isEmpty()) {
            productPage = productDao.findByGender(gender, pageable);
        }
        // Nếu không có filter nào
        else {
            // Không hiển thị sản phẩm đã bị xóa mềm (DELETED)
            productPage = productDao.findByStatusNot(Status.DELETED.getStatus(), pageable);
        }
        return productPage;
    }

    public ProductListResponseDto getProductByCreatedBy(int page, int size, String status, String gender) {
        String createdBy = userContext.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductEntity> productPage;
        
        // Nếu có cả status và gender
        if (status != null && !status.isEmpty() && gender != null && !gender.isEmpty()) {
            productPage = productDao.findByCreatedByAndStatusAndGender(createdBy, status, gender, pageable);
        }
        // Nếu chỉ có status
        else if (status != null && !status.isEmpty()) {
            productPage = productDao.findByCreatedByAndStatus(createdBy, status, pageable);
        }
        // Nếu chỉ có gender
        else if (gender != null && !gender.isEmpty()) {
            productPage = productDao.findByCreatedByAndGender(createdBy, gender, pageable);
        }
        // Nếu không có filter nào
        else {
            productPage = productDao.findByCreatedByAndStatusNot(createdBy, Status.DELETED.getStatus(), pageable);
        }
        
        ProductListResponseDto response = new ProductListResponseDto();
        response.setProducts(productPage.getContent().stream().map(productEntity -> {
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(productEntity, productDto);
            List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(productEntity.getId());
            if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
                productDto.setVariants(productVariantEntities);
            }
            // Map images
            if (productEntity.getImages() != null) {
                List<String> imageDtos = productEntity.getImages().stream().map(ImageEntity::getUrl).toList();
                productDto.setImages(imageDtos);
            }
            return productDto;
        }).toList());
        response.setPage(productPage.getNumber());
        response.setSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        return response;
    }

    public ProductEntity insertProduct(ProductEntity product) {
        ProductEntity newProduct = new ProductEntity();
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setRating(0.0);
        newProduct.setStatus("IN_PROGRESS");
        return productDao.save(newProduct);
    }

    @Transactional
    public ProductEntity insertProductWithVariants(ProductWithVariantsDto productWithVariantsDto,
                                                   List<MultipartFile> files) throws Exception {
        // Tạo product mới
        ProductEntity newProduct = new ProductEntity();
        newProduct.setName(productWithVariantsDto.getName());
        newProduct.setDescription(productWithVariantsDto.getDescription());
        newProduct.setPrice(productWithVariantsDto.getPrice());
        newProduct.setRating(0.0);
        newProduct.setStatus("IN_PROGRESS");
        String userId = userContext.getUserId();
        newProduct.setCreatedBy(userId);

        // Lưu product trước để có ID
        ProductEntity savedProduct = productDao.save(newProduct);

        List<String> imageUrls = new ArrayList<>();
        // Upload ảnh lên S3 và tạo ImageEntity
        if (files != null && !files.isEmpty()) {
            imageUrls = s3Service.uploadFiles(files);
            List<ImageEntity> images = new java.util.ArrayList<>();
            for (int i = 0; i < imageUrls.size(); i++) {
                ImageEntity img = new ImageEntity();
                img.setUrl(imageUrls.get(i));
                img.setProduct(savedProduct);
                img.setOrder(i); // thứ tự theo vị trí file upload
                images.add(img);
            }
            imageDao.saveAll(images);
            savedProduct.setImages(images);
        }
        // Tạo và lưu variants
        if (productWithVariantsDto.getVariants() != null && !productWithVariantsDto.getVariants().isEmpty()) {
            List<ProductVariantEntity> variants = productWithVariantsDto.getVariants().stream()
                    .map(variantDto -> convertToVariantEntity(variantDto, savedProduct.getId()))
                    .toList();
            productVariantDao.saveAll(variants);
        }

        // Đồng bộ với Elasticsearch
        indexProduct(savedProduct);

        return savedProduct;
    }

    private ProductVariantEntity convertToVariantEntity(ProductVariantRequestDto variantDto, Long productId) {
        ProductVariantEntity entity = new ProductVariantEntity();
        entity.setProductId(productId);
        entity.setSize(variantDto.getSize());
        entity.setColorName(variantDto.getColorName());
        entity.setColorHex(variantDto.getColorHex());
        entity.setPrice(variantDto.getPrice());
        entity.setStockQuantity(variantDto.getStockQuantity());

        // Tạo SKU
        String sku = productId + "-" + variantDto.getColorHex() + "-" + variantDto.getSize();
        entity.setSku(sku);

        return entity;
    }

    public void acceptProduct(List<String> productIds) {
        List<ProductEntity> products = productDao.findAllById(productIds.stream().map(Long::valueOf).toList());
        if (products.isEmpty()) {
            throw new EmptyResultDataAccessException("No products found for the given IDs", 1);
        }
        for (ProductEntity product : products) {
            product.setStatus(Status.ACCEPTED.name());
            ProductEntity savedProduct = productDao.save(product);
            // Đồng bộ với Elasticsearch
            updateProductInIndex(savedProduct);
            try {
                List<String> imageUrls = savedProduct.getImages().stream()
                        .map(ImageEntity::getUrl).toList();
                RecommendClient.CreateRecommendRequest request = 
                    new RecommendClient.CreateRecommendRequest(savedProduct.getId().toString(), imageUrls);
                recommendClient.createRecommendByUrl(request);
            } catch (Exception e) {
                product.setStatus(Status.IN_PROGRESS.name()); // Trả về trạng thái IN_PROGRESS nếu lỗi
                productDao.save(product);
                updateProductInIndex(product);
            }
        }
        
        // Gửi thông báo cho user có sản phẩm trong wishlist
        try {
            notificationEventService.sendProductApprovalNotification(productIds);
        } catch (Exception e) {

        }
    }

    public void rejectProduct(List<String> productIds) {
        List<ProductEntity> products = productDao.findAllById(productIds.stream().map(Long::valueOf).toList());
        if (products.isEmpty()) {
            throw new EmptyResultDataAccessException("No products found for the given IDs", 1);
        }
        for (ProductEntity product : products) {
            product.setStatus(Status.REJECTED.name());
            ProductEntity savedProduct = productDao.save(product);
            // Đồng bộ với Elasticsearch
            updateProductInIndex(savedProduct);
        }
    }

    public ProductDto getProductById(Long id) {
        ProductEntity productEntity = productDao.findById(id).orElseThrow(() -> new EmptyResultDataAccessException("Product not found", 1));
        productEntity.setViewCount(productEntity.getViewCount() != null ? productEntity.getViewCount() + 1 : 1);
        productDao.save(productEntity);
        updateProductInIndex(productEntity);
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(productEntity, productDto);
        List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(id);
        if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
            productDto.setVariants(productVariantEntities);
        }
        // Map images
        if (productEntity.getImages() != null) {
            List<String> imageDtos = productEntity.getImages().stream().map(ImageEntity::getUrl).toList();
            productDto.setImages(imageDtos);
        }
        productDto.setVendorInfo(profileClient.getVendorById(productEntity.getCreatedBy()).getBody());
        boolean isFavorite = false;
        if (userContext.getUserId() != null) {
            isFavorite = favoriteDao.findByUserIdAndProductId(userContext.getUserId(), String.valueOf(id)).isPresent();
        }
        productDto.setIsFavorite(isFavorite);
        return productDto;
    }

    public ProductListResponseDto searchProducts(String query, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductDocument> productPage = productSearchRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable);
            
            return buildProductListResponseFromDocuments(productPage);
        } catch (Exception e) {
            System.err.println("Error in searchProducts: " + e.getMessage());
            e.printStackTrace();
            // Trả về response rỗng nếu có lỗi
            ProductListResponseDto response = new ProductListResponseDto();
            response.setProducts(new ArrayList<>());
            response.setPage(page);
            response.setSize(size);
            response.setTotalElements(0L);
            response.setTotalPages(0);
            return response;
        }
    }
    
    public ProductListResponseDto searchByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDocument> productPage = productSearchRepository.findByStatus(category, pageable);
        
        return buildProductListResponseFromDocuments(productPage);
    }
    
    public ProductListResponseDto searchByPriceRange(Double minPrice, Double maxPrice) {
        List<ProductDocument> products = productSearchRepository.findByPriceBetween(minPrice, maxPrice);
        
        ProductListResponseDto response = new ProductListResponseDto();
        response.setProducts(products.stream()
                .map(this::convertDocumentToProductDto)
                .toList());
        response.setTotalElements((long) products.size());
        response.setPage(0);
        response.setSize(products.size());
        response.setTotalPages(1);
        
        return response;
    }

    // ========== ELASTICSEARCH SYNC METHODS ==========

    private void indexProduct(ProductEntity productEntity) {
        try {
            ProductDocument document = convertToProductDocument(productEntity);
            productSearchRepository.save(document);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to index product to Elasticsearch: " + e.getMessage());
        }
    }
    
    private void updateProductInIndex(ProductEntity productEntity) {
        try {
            ProductDocument document = convertToProductDocument(productEntity);
            productSearchRepository.save(document);
        } catch (Exception e) {
            System.err.println("Failed to update product in Elasticsearch: " + e.getMessage());
        }
    }
    
    private void deleteProductFromIndex(String productId) {
        try {
            productSearchRepository.deleteById(productId);
        } catch (Exception e) {
            System.err.println("Failed to delete product from Elasticsearch: " + e.getMessage());
        }
    }

    private ProductListResponseDto buildProductListResponseFromDocuments(Page<ProductDocument> productPage) {
        ProductListResponseDto response = new ProductListResponseDto();
        response.setProducts(productPage.getContent().stream()
                .map(this::convertDocumentToProductDto)
                .toList());
        response.setPage(productPage.getNumber());
        response.setSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        
        return response;
    }
    
    private ProductDto convertDocumentToProductDto(ProductDocument document) {
        ProductDto productDto = new ProductDto();
        try {
            productDto.setId(Long.valueOf(document.getId()));
        } catch (NumberFormatException e) {
            // Log lỗi để debug
            System.err.println("Failed to convert document ID: " + document.getId() + " to Long. Error: " + e.getMessage());
            // Nếu không convert được, set null hoặc giá trị mặc định
            productDto.setId(null);
        }
        productDto.setName(document.getName());
        productDto.setDescription(document.getDescription());
        productDto.setPrice(document.getPrice());
        productDto.setRating(document.getRating());
        productDto.setStatus(document.getStatus());
        productDto.setViewCount(document.getViewCount());
        productDto.setImages(document.getImageUrls());
        productDto.setVendorInfo(profileClient.getVendorById(document.getCreatedBy()).getBody());
        List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(Long.valueOf(document.getId()));
        if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
            productDto.setVariants(productVariantEntities);
        }
        return productDto;
    }
    
    private ProductDocument convertToProductDocument(ProductEntity productEntity) {
        return ProductDocument.builder()
                .id(productEntity.getId().toString())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .rating(productEntity.getRating())
                .status(productEntity.getStatus())
                .viewCount(productEntity.getViewCount())
                .createdBy(productEntity.getCreatedBy())
                .createdAt(productEntity.getCreatedAt())
                .updatedAt(productEntity.getUpdatedAt())
                .imageUrls(productEntity.getImages() != null ? 
                        productEntity.getImages().stream()
                                .map(ImageEntity::getUrl)
                                .toList() : new ArrayList<>())
                .build();
    }

    public List<ProductVariantEntity> updateProductVariants(Long productId, List<ProductVariantRequestDto> variants) {
        return productVariantService.updateProductVariants(productId, variants);
    }

    public void softDeleteProduct(Long productId) {
        ProductEntity product = productDao.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        // Kiểm tra quyền - chỉ người tạo sản phẩm hoặc admin mới được xóa
        String currentUserId = userContext.getUserId();
        if (!product.getCreatedBy().equals(currentUserId)) {
            throw new IllegalArgumentException("You can only delete your own products");
        }
        
        // Xóa mềm bằng cách đổi status thành DELETED
        product.setStatus(Status.DELETED.getStatus());
        productDao.save(product);
        
        // Xóa khỏi Elasticsearch index
        deleteProductFromIndex(productId.toString());
    }

    public void softDeleteProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs cannot be null or empty");
        }
        
        String currentUserId = userContext.getUserId();
        List<ProductEntity> products = productDao.findAllById(productIds);
        
        // Kiểm tra tất cả sản phẩm có tồn tại không
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("One or more products not found");
        }
        
        // Kiểm tra quyền xóa
        for (ProductEntity product : products) {
            if (!product.getCreatedBy().equals(currentUserId)) {
                throw new IllegalArgumentException("You can only delete your own products. Product ID: " + product.getId());
            }
        }
        
        // Xóa mềm tất cả sản phẩm
        products.forEach(product -> {
            product.setStatus(Status.DELETED.getStatus());
            deleteProductFromIndex(product.getId().toString());
        });
        
        productDao.saveAll(products);
    }

    public void updateProduct(Long id, String name, String desc) {
        ProductEntity product = productDao.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException("Product not found with id: " + id, 1));
        if (name != null && !name.isEmpty()) {
            product.setName(name);
        }
        if (desc != null && !desc.isEmpty()) {
            product.setDescription(desc);
        }
        productDao.save(product);
        // Đồng bộ với Elasticsearch
        updateProductInIndex(product);
    }

    public List<ProductDto> getRecommendProduct(MultipartFile image) {
        ResponseEntity<?> response = recommendClient.getListRecommend(image);
        if (response.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse response để lấy product_ids
            Map<String, Object> responseMap = objectMapper.convertValue(response.getBody(), Map.class);
            List<String> productIds = (List<String>) responseMap.get("product_ids");
            List<ProductEntity> products = productDao.findAllById(productIds.stream().map(Long::valueOf).toList());
            return products.stream().map(productEntity -> {
                ProductDto productDto = new ProductDto();
                BeanUtils.copyProperties(productEntity, productDto);
                List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(productEntity.getId());
                if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
                    productDto.setVariants(productVariantEntities);
                }
                // Map images
                if (productEntity.getImages() != null) {
                    List<String> imageDtos = productEntity.getImages().stream().map(ImageEntity::getUrl).toList();
                    productDto.setImages(imageDtos);
                }
                return productDto;
            }).toList();
        } else {
            throw new RuntimeException("Failed to get recommended products");
        }
    }

    /**
     * Lấy 4 sản phẩm mới nhất
     */
    public List<ProductDto> getLatestProducts() {
        List<ProductEntity> latestProducts = productDao.findTop4ByStatusOrderByCreatedAtDesc(Status.ACCEPTED.name());
        return latestProducts.stream().map(productEntity -> {
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(productEntity, productDto);
            List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(productEntity.getId());
            if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
                productDto.setVariants(productVariantEntities);
            }
            // Map images
            if (productEntity.getImages() != null) {
                List<String> imageDtos = productEntity.getImages().stream().map(ImageEntity::getUrl).toList();
                productDto.setImages(imageDtos);
            }
            return productDto;
        }).toList();
    }

    /**
     * Lấy 4 sản phẩm có lượt xem cao nhất
     */
    public List<ProductDto> getMostViewedProducts() {
        List<ProductEntity> mostViewedProducts = productDao.findTop4ByStatusOrderByViewCountDesc(Status.ACCEPTED.name());
        return mostViewedProducts.stream().map(productEntity -> {
            ProductDto productDto = new ProductDto();
            BeanUtils.copyProperties(productEntity, productDto);
            List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(productEntity.getId());
            if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
                productDto.setVariants(productVariantEntities);
            }
            // Map images
            if (productEntity.getImages() != null) {
                List<String> imageDtos = productEntity.getImages().stream().map(ImageEntity::getUrl).toList();
                productDto.setImages(imageDtos);
            }
            return productDto;
        }).toList();
    }
}
