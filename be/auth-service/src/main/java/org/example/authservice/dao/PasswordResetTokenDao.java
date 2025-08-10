package org.example.authservice.dao;

import org.example.authservice.entity.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenDao extends MongoRepository<PasswordResetToken, String> {
    
    Optional<PasswordResetToken> findByTokenAndUsedFalseAndExpiryDateAfter(String token, LocalDateTime now);
    
    Optional<PasswordResetToken> findByEmailAndUsedFalseAndExpiryDateAfter(String email, LocalDateTime now);
    
    List<PasswordResetToken> findByEmailAndUsedTrue(String email);
    
    void deleteByExpiryDateBefore(LocalDateTime expiryDate);
} 