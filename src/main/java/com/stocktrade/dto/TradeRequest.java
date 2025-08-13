package com.stocktrade.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.math.BigDecimal;

public class TradeRequest {
    
    @NotBlank(message = "Stock symbol is required")
    private String stockSymbol;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    public TradeRequest() {}
    
    public TradeRequest(String stockSymbol, Integer quantity, BigDecimal price) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
    }
    
    public String getStockSymbol() {
        return stockSymbol;
    }
    
    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
