package com.example.payment.controller;

import com.example.payment.dto.ApiResponse;
import com.example.payment.dto.CreateTransactionRequest;
import com.example.payment.dto.UpdateTransactionStatusRequest;
import com.example.payment.model.Transaction;
import com.example.payment.model.TransactionStatus;
import com.example.payment.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        try {
            Transaction transaction = transactionService.createTransaction(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Transaction created successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create transaction: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(@PathVariable String id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        if (transaction.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(transaction.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Transaction not found"));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Transaction>>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> transactions = transactionService.getAllTransactions(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
    
    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionsByMerchant(
            @PathVariable String merchantId) {
        List<Transaction> transactions = transactionService.getTransactionsByMerchant(merchantId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionsByCustomer(
            @PathVariable String customerId) {
        List<Transaction> transactions = transactionService.getTransactionsByCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionsByStatus(
            @PathVariable TransactionStatus status) {
        List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Transaction>> updateTransactionStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateTransactionStatusRequest request) {
        
        Optional<Transaction> updatedTransaction = transactionService
                .updateTransactionStatus(id, request.getStatus());
        
        if (updatedTransaction.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success("Status updated successfully", 
                    updatedTransaction.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Transaction not found"));
        }
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
    
    @GetMapping("/merchant/{merchantId}/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalAmountByMerchant(
            @PathVariable String merchantId,
            @RequestParam(defaultValue = "COMPLETED") TransactionStatus status) {
        
        BigDecimal total = transactionService.getTotalAmountByMerchantAndStatus(merchantId, status);
        return ResponseEntity.ok(ApiResponse.success("Total amount calculated", total));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable String id) {
        boolean deleted = transactionService.deleteTransaction(id);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Transaction not found"));
        }
    }
}
