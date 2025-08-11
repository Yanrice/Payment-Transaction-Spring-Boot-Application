package com.example.payment.repository;

import com.example.payment.model.Transaction;
import com.example.payment.model.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    
    List<Transaction> findByMerchantId(String merchantId);
    
    List<Transaction> findByCustomerId(String customerId);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    Page<Transaction> findByMerchantId(String merchantId, Pageable pageable);
    
    Page<Transaction> findByCustomerId(String customerId, Pageable pageable);
    
    Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);
    
    @Query("{'createdAt': { $gte: ?0, $lte: ?1 }}")
    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'merchantId': ?0, 'status': ?1}")
    List<Transaction> findByMerchantIdAndStatus(String merchantId, TransactionStatus status);
}
