package com.example.payment.service;

import com.example.payment.dto.CreateTransactionRequest;
import com.example.payment.model.Transaction;
import com.example.payment.model.TransactionStatus;
import com.example.payment.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();
    
    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    public Transaction createTransaction(CreateTransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setMerchantId(request.getMerchantId());
        transaction.setCustomerId(request.getCustomerId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setDescription(request.getDescription());
        
        transaction.setStatus(mockPaymentProcessing());
        
        return transactionRepository.save(transaction);
    }
    
    public Optional<Transaction> getTransactionById(String id) {
        return transactionRepository.findById(id);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }
    
    public List<Transaction> getTransactionsByMerchant(String merchantId) {
        return transactionRepository.findByMerchantId(merchantId);
    }
    
    public List<Transaction> getTransactionsByCustomer(String customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }
    
    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }
    
    public Optional<Transaction> updateTransactionStatus(String id, TransactionStatus status) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            transaction.setStatus(status);
            return Optional.of(transactionRepository.save(transaction));
        }
        return Optional.empty();
    }
    
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    public BigDecimal getTotalAmountByMerchantAndStatus(String merchantId, TransactionStatus status) {
        BigDecimal total = transactionRepository.sumAmountByMerchantIdAndStatus(merchantId, status);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public boolean deleteTransaction(String id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private TransactionStatus mockPaymentProcessing() {
        double successRate = 0.8;
        if (random.nextDouble() <= successRate) {
            return TransactionStatus.COMPLETED;
        } else {
            return TransactionStatus.FAILED;
        }
    }
}
