package org.example.userservice.dao;

import org.example.userservice.entity.VendorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorDao extends MongoRepository<VendorEntity, String> {
    List<VendorEntity> findByStatus(String status);
    Page<VendorEntity> findByStatus(String status, Pageable pageable);
}
