package com.stocktrade.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
public class Trade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    @NotNull(message = "Stock is required")
    private Stock stock;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false)
    @NotNull(message = "Trade type is required")
    private TradeType tradeType;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    private Integer quantity;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    
    @NotNull(message = "Total amount is required")
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "commission", precision = 19, scale = 2)
    private BigDecimal commission = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trade_status", nullable = false)
    private TradeStatus tradeStatus = TradeStatus.COMPLETED;
    
    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "notes")
    private String notes;
    
    public enum TradeType {
        BUY, SELL
    }
    
    public enum TradeStatus {
        PENDING, COMPLETED, CANCELLED, FAILED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (executedAt == null) {
            executedAt = LocalDateTime.now();
        }
        if (totalAmount == null) {
            calculateTotalAmount();
        }
    }
    
    // Constructors
    public Trade() {}
    
    public Trade(User user, Stock stock, TradeType tradeType, Integer quantity, BigDecimal price) {
        this.user = user;
        this.stock = stock;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
        calculateTotalAmount();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
    
    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        calculateTotalAmount();
    }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { 
        this.price = price;
        calculateTotalAmount();
    }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = commission; }
    
    public TradeStatus getTradeStatus() { return tradeStatus; }
    public void setTradeStatus(TradeStatus tradeStatus) { this.tradeStatus = tradeStatus; }
    
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Business logic methods
    private void calculateTotalAmount() {
        if (quantity != null && price != null) {
            BigDecimal baseAmount = price.multiply(BigDecimal.valueOf(quantity));
            this.totalAmount = baseAmount.add(commission != null ? commission : BigDecimal.ZERO);
        }
    }
    
    public BigDecimal getNetAmount() {
        // For buy orders: total amount including commission
        // For sell orders: total amount minus commission
        if (tradeType == TradeType.BUY) {
            return totalAmount;
        } else {
            return totalAmount.subtract(commission != null ? commission : BigDecimal.ZERO);
        }
    }
    
    public boolean isBuyOrder() {
        return tradeType == TradeType.BUY;
    }
    
    public boolean isSellOrder() {
        return tradeType == TradeType.SELL;
    }
    
    public boolean isCompleted() {
        return tradeStatus == TradeStatus.COMPLETED;
    }
    
    public boolean isPending() {
        return tradeStatus == TradeStatus.PENDING;
    }
    
    public String getTradeDescription() {
        return String.format("%s %d shares of %s at $%.2f", 
                tradeType.name(), quantity, 
                stock != null ? stock.getSymbol() : "N/A", 
                price);
    }
}
