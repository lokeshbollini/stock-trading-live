package com.stocktrade.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "stock_id"})
})
public class Portfolio {
    
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
    
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be positive or zero")
    @Column(nullable = false)
    private Integer quantity;
    
    @NotNull(message = "Average cost is required")
    @PositiveOrZero(message = "Average cost must be positive or zero")
    @Column(name = "average_cost", nullable = false, precision = 19, scale = 2)
    private BigDecimal averageCost;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Portfolio() {}
    
    public Portfolio(User user, Stock stock, Integer quantity, BigDecimal averageCost) {
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
        this.averageCost = averageCost;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getAverageCost() { return averageCost; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business logic methods
    public BigDecimal getTotalCost() {
        return averageCost.multiply(BigDecimal.valueOf(quantity));
    }
    
    public BigDecimal getCurrentValue() {
        if (stock != null && stock.getCurrentPrice() != null) {
            return stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getUnrealizedGainLoss() {
        return getCurrentValue().subtract(getTotalCost());
    }
    
    public BigDecimal getUnrealizedGainLossPercentage() {
        BigDecimal totalCost = getTotalCost();
        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
            return getUnrealizedGainLoss()
                    .divide(totalCost, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
    
    public void addShares(Integer additionalQuantity, BigDecimal purchasePrice) {
        if (additionalQuantity <= 0) {
            throw new IllegalArgumentException("Additional quantity must be positive");
        }
        
        BigDecimal currentTotalCost = getTotalCost();
        BigDecimal additionalCost = purchasePrice.multiply(BigDecimal.valueOf(additionalQuantity));
        Integer newQuantity = this.quantity + additionalQuantity;
        
        // Calculate new average cost
        this.averageCost = currentTotalCost.add(additionalCost)
                .divide(BigDecimal.valueOf(newQuantity), 2, java.math.RoundingMode.HALF_UP);
        this.quantity = newQuantity;
    }
    
    public void removeShares(Integer sharesToRemove) {
        if (sharesToRemove <= 0) {
            throw new IllegalArgumentException("Shares to remove must be positive");
        }
        if (sharesToRemove > this.quantity) {
            throw new IllegalArgumentException("Cannot remove more shares than owned");
        }
        
        this.quantity -= sharesToRemove;
        // Average cost remains the same when selling shares
    }
    
    public boolean hasShares() {
        return quantity != null && quantity > 0;
    }
    
    public boolean hasSufficientShares(Integer requiredShares) {
        return quantity != null && quantity >= requiredShares;
    }
}
