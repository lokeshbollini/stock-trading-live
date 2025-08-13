package com.stocktrade.controller;

import com.stocktrade.entity.Stock;
import com.stocktrade.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.findAllActiveStocks();
        return ResponseEntity.ok(stocks);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestParam String query) {
        List<Stock> stocks = stockService.searchStocks(query);
        return ResponseEntity.ok(stocks);
    }
    
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStock(@PathVariable String symbol) {
        Optional<Stock> stock = stockService.findActiveBySymbol(symbol);
        return stock.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{symbol}/quote")
    public ResponseEntity<StockQuoteResponse> getStockQuote(@PathVariable String symbol) {
        try {
            Optional<Stock> stockOpt = stockService.findActiveBySymbol(symbol);
            if (stockOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Stock stock = stockOpt.get();
            
            // Refresh data if stale
            if (stockService.isMarketDataStale(symbol, 5)) {
                stock = stockService.refreshStockData(symbol);
            }
            
            StockQuoteResponse quote = new StockQuoteResponse(
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    stock.getCurrentPrice(),
                    stock.getPreviousClose(),
                    stock.getPriceChange(),
                    stock.getPriceChangePercentage(),
                    stock.getDayHigh(),
                    stock.getDayLow(),
                    stock.getVolume(),
                    stock.getLastUpdated()
            );
            
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{symbol}/refresh")
    public ResponseEntity<Stock> refreshStockData(@PathVariable String symbol) {
        try {
            Stock stock = stockService.refreshStockData(symbol);
            return ResponseEntity.ok(stock);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<Stock>> getPopularStocks() {
        List<Stock> stocks = stockService.findStocksByVolumeDesc();
        // Return top 20 most active stocks
        List<Stock> topStocks = stocks.size() > 20 ? stocks.subList(0, 20) : stocks;
        return ResponseEntity.ok(topStocks);
    }
    
    @GetMapping("/top-gainers")
    public ResponseEntity<List<Stock>> getTopGainers() {
        List<Stock> stocks = stockService.findAllActiveStocks();
        
        // Sort by price change percentage (descending)
        stocks.sort((s1, s2) -> {
            BigDecimal change1 = s1.getPriceChangePercentage();
            BigDecimal change2 = s2.getPriceChangePercentage();
            return change2.compareTo(change1);
        });
        
        List<Stock> topGainers = stocks.size() > 20 ? stocks.subList(0, 20) : stocks;
        return ResponseEntity.ok(topGainers);
    }
    
    @GetMapping("/top-losers")
    public ResponseEntity<List<Stock>> getTopLosers() {
        List<Stock> stocks = stockService.findAllActiveStocks();
        
        // Sort by price change percentage (ascending)
        stocks.sort((s1, s2) -> {
            BigDecimal change1 = s1.getPriceChangePercentage();
            BigDecimal change2 = s2.getPriceChangePercentage();
            return change1.compareTo(change2);
        });
        
        List<Stock> topLosers = stocks.size() > 20 ? stocks.subList(0, 20) : stocks;
        return ResponseEntity.ok(topLosers);
    }
    
    @GetMapping("/most-expensive")
    public ResponseEntity<List<Stock>> getMostExpensiveStocks() {
        List<Stock> stocks = stockService.findStocksByPriceDesc();
        List<Stock> topExpensive = stocks.size() > 20 ? stocks.subList(0, 20) : stocks;
        return ResponseEntity.ok(topExpensive);
    }
    
    @GetMapping("/cheapest")
    public ResponseEntity<List<Stock>> getCheapestStocks() {
        List<Stock> stocks = stockService.findStocksByPriceAsc();
        List<Stock> cheapest = stocks.size() > 20 ? stocks.subList(0, 20) : stocks;
        return ResponseEntity.ok(cheapest);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<StockStatsResponse> getStockStats() {
        long totalStocks = stockService.getActiveStockCount();
        List<Stock> recentlyTraded = stockService.getStocksWithRecentTrades(24);
        
        StockStatsResponse stats = new StockStatsResponse(totalStocks, recentlyTraded.size());
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/refresh-stale")
    public ResponseEntity<RefreshResponse> refreshStaleStocks(@RequestParam(defaultValue = "15") int minutesThreshold) {
        try {
            List<Stock> refreshedStocks = stockService.refreshStaleStocks(minutesThreshold);
            RefreshResponse response = new RefreshResponse(refreshedStocks.size(), 
                    "Refreshed " + refreshedStocks.size() + " stale stocks");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            RefreshResponse response = new RefreshResponse(0, "Failed to refresh stocks: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    public static class StockQuoteResponse {
        private String symbol;
        private String companyName;
        private BigDecimal currentPrice;
        private BigDecimal previousClose;
        private BigDecimal priceChange;
        private BigDecimal priceChangePercentage;
        private BigDecimal dayHigh;
        private BigDecimal dayLow;
        private Long volume;
        private java.time.LocalDateTime lastUpdated;
        
        public StockQuoteResponse(String symbol, String companyName, BigDecimal currentPrice,
                                 BigDecimal previousClose, BigDecimal priceChange, BigDecimal priceChangePercentage,
                                 BigDecimal dayHigh, BigDecimal dayLow, Long volume, 
                                 java.time.LocalDateTime lastUpdated) {
            this.symbol = symbol;
            this.companyName = companyName;
            this.currentPrice = currentPrice;
            this.previousClose = previousClose;
            this.priceChange = priceChange;
            this.priceChangePercentage = priceChangePercentage;
            this.dayHigh = dayHigh;
            this.dayLow = dayLow;
            this.volume = volume;
            this.lastUpdated = lastUpdated;
        }
        
        // Getters and setters
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        
        public BigDecimal getCurrentPrice() { return currentPrice; }
        public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
        
        public BigDecimal getPreviousClose() { return previousClose; }
        public void setPreviousClose(BigDecimal previousClose) { this.previousClose = previousClose; }
        
        public BigDecimal getPriceChange() { return priceChange; }
        public void setPriceChange(BigDecimal priceChange) { this.priceChange = priceChange; }
        
        public BigDecimal getPriceChangePercentage() { return priceChangePercentage; }
        public void setPriceChangePercentage(BigDecimal priceChangePercentage) { this.priceChangePercentage = priceChangePercentage; }
        
        public BigDecimal getDayHigh() { return dayHigh; }
        public void setDayHigh(BigDecimal dayHigh) { this.dayHigh = dayHigh; }
        
        public BigDecimal getDayLow() { return dayLow; }
        public void setDayLow(BigDecimal dayLow) { this.dayLow = dayLow; }
        
        public Long getVolume() { return volume; }
        public void setVolume(Long volume) { this.volume = volume; }
        
        public java.time.LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(java.time.LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }
    
    public static class StockStatsResponse {
        private long totalActiveStocks;
        private long recentlyTradedStocks;
        
        public StockStatsResponse(long totalActiveStocks, long recentlyTradedStocks) {
            this.totalActiveStocks = totalActiveStocks;
            this.recentlyTradedStocks = recentlyTradedStocks;
        }
        
        public long getTotalActiveStocks() { return totalActiveStocks; }
        public void setTotalActiveStocks(long totalActiveStocks) { this.totalActiveStocks = totalActiveStocks; }
        
        public long getRecentlyTradedStocks() { return recentlyTradedStocks; }
        public void setRecentlyTradedStocks(long recentlyTradedStocks) { this.recentlyTradedStocks = recentlyTradedStocks; }
    }
    
    public static class RefreshResponse {
        private int stocksRefreshed;
        private String message;
        
        public RefreshResponse(int stocksRefreshed, String message) {
            this.stocksRefreshed = stocksRefreshed;
            this.message = message;
        }
        
        public int getStocksRefreshed() { return stocksRefreshed; }
        public void setStocksRefreshed(int stocksRefreshed) { this.stocksRefreshed = stocksRefreshed; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
