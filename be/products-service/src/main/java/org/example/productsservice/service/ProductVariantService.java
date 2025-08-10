package org.example.productsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.productsservice.dao.ProductDao;
import org.example.productsservice.dao.ProductVariantDao;
import org.example.productsservice.dto.ProductItemQuantityDto;
import org.example.productsservice.dto.ProductVariantRequestDto;
import org.example.productsservice.entity.ProductEntity;
import org.example.productsservice.entity.ProductVariantEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantService {
    private final ProductVariantDao productVariantDao;
    private final ProductDao productDao;

    public List<ProductVariantEntity> insertProductVariant(List<ProductVariantRequestDto> productVariantDtos, Long productId) {
        if (productVariantDtos == null || productVariantDtos.isEmpty()) {
            throw new IllegalArgumentException("Product variants cannot be null or empty");
        }
        
        // Kiểm tra product tồn tại
        ProductEntity product = productDao.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        List<ProductVariantEntity> productVariants = productVariantDtos.stream()
                .map(dto -> convertToEntity(dto, productId))
                .toList();
        
        return productVariantDao.saveAll(productVariants);
    }
    
    private ProductVariantEntity convertToEntity(ProductVariantRequestDto dto, Long productId) {
        ProductVariantEntity entity = new ProductVariantEntity();
        
        entity.setProductId(productId);
        entity.setSize(dto.getSize());
        entity.setColorName(dto.getColorName());
        entity.setColorHex(dto.getColorHex());
        entity.setPrice(dto.getPrice());
        entity.setStockQuantity(dto.getStockQuantity());
        
        // Tạo SKU
        String sku = generateSku(productId, dto);
        entity.setSku(sku);
        
        return entity;
    }

    public void updateProductQuantity(List<ProductItemQuantityDto> productItemQuantityDto) {
        for (ProductItemQuantityDto item : productItemQuantityDto) {
            ProductVariantEntity variant = productVariantDao.findBySku(item.getSku())
                    .orElseThrow(() -> new EmptyResultDataAccessException("Product variant not found for SKU: " + item.getSku(), 1));
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for SKU: " + item.getSku() + ". Available: " + variant.getStockQuantity() + ", Requested: " + item.getQuantity());
            }
            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
            productVariantDao.save(variant);
        }
    }

    public List<ProductVariantEntity> updateProductVariants(Long productId, List<ProductVariantRequestDto> newVariants) {
        // Kiểm tra product tồn tại
        productDao.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        // Lấy danh sách variants hiện tại
        List<ProductVariantEntity> existingVariants = productVariantDao.findByProductId(productId);
        
        // Tạo map để dễ dàng tìm kiếm variant theo SKU
        Map<String, ProductVariantEntity> existingVariantsMap = existingVariants.stream()
                .collect(Collectors.toMap(ProductVariantEntity::getSku, variant -> variant));
        
        // Tạo map cho variants mới
        Map<String, ProductVariantRequestDto> newVariantsMap = newVariants.stream()
                .collect(Collectors.toMap(dto -> generateSku(productId, dto), dto -> dto));
        
        List<ProductVariantEntity> variantsToDelete = existingVariants.stream()
                .filter(variant -> !newVariantsMap.containsKey(variant.getSku()))
                .toList();
        
        if (!variantsToDelete.isEmpty()) {
            productVariantDao.deleteAll(variantsToDelete);
        }
        
        List<ProductVariantEntity> variantsToSave = new ArrayList<>();
        
        for (ProductVariantRequestDto newVariantDto : newVariants) {
            String sku = generateSku(productId, newVariantDto);
            ProductVariantEntity existingVariant = existingVariantsMap.get(sku);
            
            if (existingVariant != null) {
                existingVariant.setSize(newVariantDto.getSize());
                existingVariant.setColorName(newVariantDto.getColorName());
                existingVariant.setColorHex(newVariantDto.getColorHex());
                existingVariant.setPrice(newVariantDto.getPrice());
                existingVariant.setStockQuantity(newVariantDto.getStockQuantity());
                variantsToSave.add(existingVariant);
            } else {
                ProductVariantEntity newVariant = convertToEntity(newVariantDto, productId);
                variantsToSave.add(newVariant);
            }
        }
        
        return productVariantDao.saveAll(variantsToSave);
    }
    
    private String generateSku(Long productId, ProductVariantRequestDto dto) {
        return productId + "-" + dto.getColorHex() + "-" + dto.getSize();
    }
}
