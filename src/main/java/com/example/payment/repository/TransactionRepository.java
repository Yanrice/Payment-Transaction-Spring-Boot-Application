package com.example.payment.repository;

import com.example.payment.model.Transaction;
import com.example.payment.model.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    List<Transaction> findByMerchantId(String merchantId);
    
    List<Transaction> findByCustomerId(String customerId);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    Page<Transaction> findByMerchantId(String merchantId, Pageable pageable);
    
    Page<Transaction> findByCustomerId(String customerId, Pageable pageable);
    
    Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.merchantId = :merchantId AND t.status = :status")
    BigDecimal sumAmountByMerchantIdAndStatus(@Param("merchantId") String merchantId, 
                                            @Param("status") TransactionStatus status);
}
