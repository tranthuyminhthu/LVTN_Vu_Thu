package org.example.productsservice.dao;

import org.example.productsservice.entity.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    
    Page<ProductDocument> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Page<ProductDocument> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    
    Page<ProductDocument> findByStatus(String status, Pageable pageable);
    
    Page<ProductDocument> findByCreatedBy(String createdBy, Pageable pageable);
    
    Page<ProductDocument> findByCreatedByAndStatus(String createdBy, String status, Pageable pageable);
    
    List<ProductDocument> findByPriceBetween(Double minPrice, Double maxPrice);
    
    Page<ProductDocument> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);
} 