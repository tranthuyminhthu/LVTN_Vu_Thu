package org.example.productsservice.dao;

import org.example.productsservice.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDao  extends JpaRepository<ImageEntity, String> {
}
