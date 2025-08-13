package com.stocktrade.service;

import com.stocktrade.entity.Stock;
import com.stocktrade.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockService {
    
    private final StockRepository stockRepository;
    private final StockDataService stockDataService;
    
    @Autowired
    public StockService(StockRepository stockRepository, StockDataService stockDataService) {
        this.stockRepository = stockRepository;
        this.stockDataService = stockDataService;
    }
    
    public Stock createStock(String symbol, String companyName, BigDecimal currentPrice) {
        String upperSymbol = symbol.toUpperCase();
        
        if (stockRepository.existsBySymbol(upperSymbol)) {
            throw new IllegalArgumentException("Stock already exists with symbol: " + upperSymbol);
        }
        
        Stock stock = new Stock(upperSymbol, companyName, currentPrice);
        return stockRepository.save(stock);
    }
    
    public Stock createOrUpdateStock(Stock stock) {
        Optional<Stock> existingStock = stockRepository.findBySymbol(stock.getSymbol());
        
        if (existingStock.isPresent()) {
            Stock existing = existingStock.get();
            existing.setCompanyName(stock.getCompanyName());
            existing.setCurrentPrice(stock.getCurrentPrice());
            existing.setPreviousClose(stock.getPreviousClose());
            existing.setDayHigh(stock.getDayHigh());
            existing.setDayLow(stock.getDayLow());
            existing.setVolume(stock.getVolume());
            existing.setMarketCap(stock.getMarketCap());
            existing.setPeRatio(stock.getPeRatio());
            existing.setDividendYield(stock.getDividendYield());
            existing.setFiftyTwoWeekHigh(stock.getFiftyTwoWeekHigh());
            existing.setFiftyTwoWeekLow(stock.getFiftyTwoWeekLow());
            existing.setIsActive(stock.getIsActive());
            return stockRepository.save(existing);
        } else {
            return stockRepository.save(stock);
        }
    }
    
    public Optional<Stock> findById(Long id) {
        return stockRepository.findById(id);
    }
    
    public Optional<Stock> findBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase());
    }
    
    public Optional<Stock> findActiveBySymbol(String symbol) {
        return stockRepository.findBySymbolAndIsActiveTrue(symbol.toUpperCase());
    }
    
    public List<Stock> findAllActiveStocks() {
        return stockRepository.findByIsActiveTrueOrderBySymbolAsc();
    }
    
    public List<Stock> searchStocks(String searchTerm) {
        return stockRepository.searchActiveStocks(searchTerm);
    }
    
    public List<Stock> findStocksByVolumeDesc() {
        return stockRepository.findActiveStocksByVolumeDesc();
    }
    
    public List<Stock> findStocksByPriceDesc() {
        return stockRepository.findActiveStocksByPriceDesc();
    }
    
    public List<Stock> findStocksByPriceAsc() {
        return stockRepository.findActiveStocksByPriceAsc();
    }
    
    public Stock updateStockPrice(String symbol, BigDecimal newPrice) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found with symbol: " + symbol));
        
        // Store current price as previous close if it's a new day
        if (stock.getCurrentPrice() != null && isNewTradingDay(stock.getLastUpdated())) {
            stock.setPreviousClose(stock.getCurrentPrice());
        }
        
        stock.setCurrentPrice(newPrice);
        
        // Update day high/low
        if (stock.getDayHigh() == null || newPrice.compareTo(stock.getDayHigh()) > 0) {
            stock.setDayHigh(newPrice);
        }
        if (stock.getDayLow() == null || newPrice.compareTo(stock.getDayLow()) < 0) {
            stock.setDayLow(newPrice);
        }
        
        return stockRepository.save(stock);
    }
    
    public Stock updateStockData(String symbol, BigDecimal price, BigDecimal previousClose, 
                                BigDecimal dayHigh, BigDecimal dayLow, Long volume) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found with symbol: " + symbol));
        
        stock.setCurrentPrice(price);
        stock.setPreviousClose(previousClose);
        stock.setDayHigh(dayHigh);
        stock.setDayLow(dayLow);
        stock.setVolume(volume);
        
        return stockRepository.save(stock);
    }
    
    public Stock refreshStockData(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found with symbol: " + symbol));
        
        try {
            Stock updatedData = stockDataService.getStockData(symbol);
            
            stock.setCurrentPrice(updatedData.getCurrentPrice());
            if (updatedData.getPreviousClose() != null) {
                stock.setPreviousClose(updatedData.getPreviousClose());
            }
            if (updatedData.getDayHigh() != null) {
                stock.setDayHigh(updatedData.getDayHigh());
            }
            if (updatedData.getDayLow() != null) {
                stock.setDayLow(updatedData.getDayLow());
            }
            if (updatedData.getVolume() != null) {
                stock.setVolume(updatedData.getVolume());
            }
            
            return stockRepository.save(stock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh stock data for " + symbol, e);
        }
    }
    
    public List<Stock> refreshStaleStocks(int minutesThreshold) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(minutesThreshold);
        List<Stock> staleStocks = stockRepository.findStaleStocks(cutoffTime);
        
        for (Stock stock : staleStocks) {
            try {
                refreshStockData(stock.getSymbol());
            } catch (Exception e) {
                // Log error but continue with other stocks
                System.err.println("Failed to refresh stock data for " + stock.getSymbol() + ": " + e.getMessage());
            }
        }
        
        return staleStocks;
    }
    
    public Stock deactivateStock(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found with symbol: " + symbol));
        
        stock.setIsActive(false);
        return stockRepository.save(stock);
    }
    
    public Stock reactivateStock(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Stock not found with symbol: " + symbol));
        
        stock.setIsActive(true);
        return stockRepository.save(stock);
    }
    
    public boolean existsBySymbol(String symbol) {
        return stockRepository.existsBySymbol(symbol.toUpperCase());
    }
    
    public long getActiveStockCount() {
        return stockRepository.countActiveStocks();
    }
    
    public List<Stock> getStocksInUserPortfolio(Long userId) {
        return stockRepository.findStocksInUserPortfolio(userId);
    }
    
    public List<Stock> getStocksWithRecentTrades(int hoursBack) {
        LocalDateTime fromDate = LocalDateTime.now().minusHours(hoursBack);
        return stockRepository.findStocksWithRecentTrades(fromDate);
    }
    
    public BigDecimal getCurrentPrice(String symbol) {
        return stockRepository.findBySymbolAndIsActiveTrue(symbol.toUpperCase())
                .map(Stock::getCurrentPrice)
                .orElseThrow(() -> new IllegalArgumentException("Active stock not found with symbol: " + symbol));
    }
    
    public boolean isMarketDataStale(String symbol, int minutesThreshold) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .map(stock -> stock.isDataStale(minutesThreshold))
                .orElse(true);
    }
    
    private boolean isNewTradingDay(LocalDateTime lastUpdated) {
        LocalDateTime now = LocalDateTime.now();
        return lastUpdated.toLocalDate().isBefore(now.toLocalDate());
    }
}
