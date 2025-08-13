package com.stocktrade.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stocks")
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Stock symbol is required")
    @Column(unique = true, nullable = false, length = 10)
    private String symbol;
    
    @NotBlank(message = "Company name is required")
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @NotNull(message = "Current price is required")
    @Positive(message = "Current price must be positive")
    @Column(name = "current_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(name = "previous_close", precision = 19, scale = 2)
    private BigDecimal previousClose;
    
    @Column(name = "day_high", precision = 19, scale = 2)
    private BigDecimal dayHigh;
    
    @Column(name = "day_low", precision = 19, scale = 2)
    private BigDecimal dayLow;
    
    @Column(name = "volume")
    private Long volume;
    
    @Column(name = "market_cap")
    private Long marketCap;
    
    @Column(name = "pe_ratio", precision = 10, scale = 2)
    private BigDecimal peRatio;
    
    @Column(name = "dividend_yield", precision = 5, scale = 2)
    private BigDecimal dividendYield;
    
    @Column(name = "fifty_two_week_high", precision = 19, scale = 2)
    private BigDecimal fiftyTwoWeekHigh;
    
    @Column(name = "fifty_two_week_low", precision = 19, scale = 2)
    private BigDecimal fiftyTwoWeekLow;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Portfolio> portfolios = new HashSet<>();
    
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Trade> trades = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    // Constructors
    public Stock() {}
    
    public Stock(String symbol, String companyName, BigDecimal currentPrice) {
        this.symbol = symbol.toUpperCase();
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol != null ? symbol.toUpperCase() : null; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    public BigDecimal getPreviousClose() { return previousClose; }
    public void setPreviousClose(BigDecimal previousClose) { this.previousClose = previousClose; }
    
    public BigDecimal getDayHigh() { return dayHigh; }
    public void setDayHigh(BigDecimal dayHigh) { this.dayHigh = dayHigh; }
    
    public BigDecimal getDayLow() { return dayLow; }
    public void setDayLow(BigDecimal dayLow) { this.dayLow = dayLow; }
    
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
    
    public Long getMarketCap() { return marketCap; }
    public void setMarketCap(Long marketCap) { this.marketCap = marketCap; }
    
    public BigDecimal getPeRatio() { return peRatio; }
    public void setPeRatio(BigDecimal peRatio) { this.peRatio = peRatio; }
    
    public BigDecimal getDividendYield() { return dividendYield; }
    public void setDividendYield(BigDecimal dividendYield) { this.dividendYield = dividendYield; }
    
    public BigDecimal getFiftyTwoWeekHigh() { return fiftyTwoWeekHigh; }
    public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh) { this.fiftyTwoWeekHigh = fiftyTwoWeekHigh; }
    
    public BigDecimal getFiftyTwoWeekLow() { return fiftyTwoWeekLow; }
    public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow) { this.fiftyTwoWeekLow = fiftyTwoWeekLow; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Set<Portfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(Set<Portfolio> portfolios) { this.portfolios = portfolios; }
    
    public Set<Trade> getTrades() { return trades; }
    public void setTrades(Set<Trade> trades) { this.trades = trades; }
    
    // Helper methods
    public BigDecimal getPriceChange() {
        if (previousClose != null) {
            return currentPrice.subtract(previousClose);
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getPriceChangePercentage() {
        if (previousClose != null && previousClose.compareTo(BigDecimal.ZERO) > 0) {
            return getPriceChange()
                    .divide(previousClose, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
    
    public boolean isDataStale(int minutesThreshold) {
        return lastUpdated.isBefore(LocalDateTime.now().minusMinutes(minutesThreshold));
    }
}
