package com.stocktrade.service;

import com.stocktrade.entity.Stock;
import com.stocktrade.entity.Trade;
import com.stocktrade.entity.User;
import com.stocktrade.repository.StockRepository;
import com.stocktrade.repository.TradeRepository;
import com.stocktrade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TradeService {
    
    private final TradeRepository tradeRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserService userService;
    private final PortfolioService portfolioService;
    private final StockService stockService;
    
    @Autowired
    public TradeService(TradeRepository tradeRepository,
                       UserRepository userRepository,
                       StockRepository stockRepository,
                       UserService userService,
                       PortfolioService portfolioService,
                       StockService stockService) {
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.stockService = stockService;
    }
    
    @Transactional
    public Trade executeBuyOrder(Long userId, String stockSymbol, Integer quantity, BigDecimal price) {
        // Validate inputs
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Stock stock = stockRepository.findBySymbolAndIsActiveTrue(stockSymbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Active stock not found with symbol: " + stockSymbol));
        
        // Calculate total cost
        BigDecimal totalCost = price.multiply(BigDecimal.valueOf(quantity));
        
        // Check if user has sufficient cash
        if (!user.hasSufficientCash(totalCost)) {
            throw new IllegalArgumentException("Insufficient cash balance. Required: $" + totalCost + 
                                             ", Available: $" + user.getCashBalance());
        }
        
        // Refresh stock price if data is stale
        if (stockService.isMarketDataStale(stockSymbol, 5)) {
            try {
                stockService.refreshStockData(stockSymbol);
                stock = stockRepository.findBySymbolAndIsActiveTrue(stockSymbol.toUpperCase()).orElse(stock);
            } catch (Exception e) {
                // Log warning but continue with current price
                System.err.println("Warning: Could not refresh stock data for " + stockSymbol + ": " + e.getMessage());
            }
        }
        
        // Use current market price instead of provided price for execution
        BigDecimal marketPrice = stock.getCurrentPrice();
        BigDecimal actualTotalCost = marketPrice.multiply(BigDecimal.valueOf(quantity));
        
        // Re-check cash balance with market price
        if (!user.hasSufficientCash(actualTotalCost)) {
            throw new IllegalArgumentException("Insufficient cash balance at market price. Required: $" + actualTotalCost + 
                                             ", Available: $" + user.getCashBalance());
        }
        
        // Create trade record
        Trade trade = new Trade(user, stock, Trade.TradeType.BUY, quantity, marketPrice);
        trade.setTradeStatus(Trade.TradeStatus.COMPLETED);
        trade.setExecutedAt(LocalDateTime.now());
        
        // Update user cash balance
        userService.subtractCash(userId, actualTotalCost);
        
        // Update portfolio
        portfolioService.addToPortfolio(userId, stockSymbol, quantity, marketPrice);
        
        // Save trade
        return tradeRepository.save(trade);
    }
    
    @Transactional
    public Trade executeSellOrder(Long userId, String stockSymbol, Integer quantity, BigDecimal price) {
        // Validate inputs
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Stock stock = stockRepository.findBySymbolAndIsActiveTrue(stockSymbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Active stock not found with symbol: " + stockSymbol));
        
        // Check if user has sufficient shares
        Integer sharesOwned = portfolioService.getSharesOwned(userId, stockSymbol);
        if (sharesOwned < quantity) {
            throw new IllegalArgumentException("Insufficient shares to sell. Owned: " + sharesOwned + 
                                             ", Requested: " + quantity);
        }
        
        // Refresh stock price if data is stale
        if (stockService.isMarketDataStale(stockSymbol, 5)) {
            try {
                stockService.refreshStockData(stockSymbol);
                stock = stockRepository.findBySymbolAndIsActiveTrue(stockSymbol.toUpperCase()).orElse(stock);
            } catch (Exception e) {
                // Log warning but continue with current price
                System.err.println("Warning: Could not refresh stock data for " + stockSymbol + ": " + e.getMessage());
            }
        }
        
        // Use current market price
        BigDecimal marketPrice = stock.getCurrentPrice();
        BigDecimal totalRevenue = marketPrice.multiply(BigDecimal.valueOf(quantity));
        
        // Create trade record
        Trade trade = new Trade(user, stock, Trade.TradeType.SELL, quantity, marketPrice);
        trade.setTradeStatus(Trade.TradeStatus.COMPLETED);
        trade.setExecutedAt(LocalDateTime.now());
        
        // Update portfolio (remove shares)
        portfolioService.removeFromPortfolio(userId, stockSymbol, quantity);
        
        // Update user cash balance
        userService.addCash(userId, totalRevenue);
        
        // Save trade
        return tradeRepository.save(trade);
    }
    
    public List<Trade> getUserTrades(Long userId) {
        return tradeRepository.findByUserIdOrderByExecutedAtDesc(userId);
    }
    
    public Page<Trade> getUserTrades(Long userId, Pageable pageable) {
        return tradeRepository.findByUserIdOrderByExecutedAtDesc(userId, pageable);
    }
    
    public List<Trade> getUserTradesForStock(Long userId, String stockSymbol) {
        return tradeRepository.findByUserIdAndStockSymbolOrderByExecutedAtDesc(userId, stockSymbol.toUpperCase());
    }
    
    public List<Trade> getUserTradesSince(Long userId, LocalDateTime fromDate) {
        return tradeRepository.findUserTradesSince(userId, fromDate);
    }
    
    public List<Trade> getUserTradesBetween(Long userId, LocalDateTime fromDate, LocalDateTime toDate) {
        return tradeRepository.findUserTradesBetween(userId, fromDate, toDate);
    }
    
    public List<Trade> getStockTrades(String stockSymbol) {
        return tradeRepository.findByStockSymbol(stockSymbol.toUpperCase());
    }
    
    public List<Trade> getStockTradesSince(String stockSymbol, LocalDateTime fromDate) {
        return tradeRepository.findStockTradesSince(stockSymbol.toUpperCase(), fromDate);
    }
    
    public BigDecimal getTotalBuyAmount(Long userId) {
        Double total = tradeRepository.calculateTotalTradeAmount(userId, Trade.TradeType.BUY);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalSellAmount(Long userId) {
        Double total = tradeRepository.calculateTotalTradeAmount(userId, Trade.TradeType.SELL);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }
    
    public Integer getTotalSharesBought(Long userId, String stockSymbol) {
        Integer total = tradeRepository.calculateTotalQuantityTraded(userId, stockSymbol.toUpperCase(), Trade.TradeType.BUY);
        return total != null ? total : 0;
    }
    
    public Integer getTotalSharesSold(Long userId, String stockSymbol) {
        Integer total = tradeRepository.calculateTotalQuantityTraded(userId, stockSymbol.toUpperCase(), Trade.TradeType.SELL);
        return total != null ? total : 0;
    }
    
    public long getUserTradeCount(Long userId) {
        return tradeRepository.countCompletedTradesByUser(userId);
    }
    
    public long getStockTradeCount(String stockSymbol) {
        return tradeRepository.countCompletedTradesForStock(stockSymbol.toUpperCase());
    }
    
    public BigDecimal getAverageTradePrice(String stockSymbol, LocalDateTime fromDate) {
        Double average = tradeRepository.calculateAverageTradePrice(stockSymbol.toUpperCase(), fromDate);
        return average != null ? BigDecimal.valueOf(average) : BigDecimal.ZERO;
    }
    
    public Long getTotalVolume(String stockSymbol, LocalDateTime fromDate) {
        return tradeRepository.calculateTotalVolume(stockSymbol.toUpperCase(), fromDate);
    }
    
    public List<String> getUserTradedSymbols(Long userId) {
        return tradeRepository.findTradedStockSymbolsByUser(userId);
    }
    
    public TradeSummary getTradeSummary(Long userId) {
        BigDecimal totalBuyAmount = getTotalBuyAmount(userId);
        BigDecimal totalSellAmount = getTotalSellAmount(userId);
        long totalTradeCount = getUserTradeCount(userId);
        List<String> tradedSymbols = getUserTradedSymbols(userId);
        
        BigDecimal realizedGainLoss = totalSellAmount.subtract(totalBuyAmount);
        
        return new TradeSummary(
                totalBuyAmount,
                totalSellAmount,
                realizedGainLoss,
                totalTradeCount,
                tradedSymbols.size()
        );
    }
    
    public boolean canAffordTrade(Long userId, String stockSymbol, Integer quantity) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return false;
            
            BigDecimal currentPrice = stockService.getCurrentPrice(stockSymbol);
            BigDecimal totalCost = currentPrice.multiply(BigDecimal.valueOf(quantity));
            
            return user.hasSufficientCash(totalCost);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean canSellShares(Long userId, String stockSymbol, Integer quantity) {
        Integer sharesOwned = portfolioService.getSharesOwned(userId, stockSymbol);
        return sharesOwned >= quantity;
    }
    
    public static class TradeSummary {
        private final BigDecimal totalBuyAmount;
        private final BigDecimal totalSellAmount;
        private final BigDecimal realizedGainLoss;
        private final long totalTradeCount;
        private final long uniqueStocksTraded;
        
        public TradeSummary(BigDecimal totalBuyAmount, BigDecimal totalSellAmount,
                           BigDecimal realizedGainLoss, long totalTradeCount, long uniqueStocksTraded) {
            this.totalBuyAmount = totalBuyAmount;
            this.totalSellAmount = totalSellAmount;
            this.realizedGainLoss = realizedGainLoss;
            this.totalTradeCount = totalTradeCount;
            this.uniqueStocksTraded = uniqueStocksTraded;
        }
        
        // Getters
        public BigDecimal getTotalBuyAmount() { return totalBuyAmount; }
        public BigDecimal getTotalSellAmount() { return totalSellAmount; }
        public BigDecimal getRealizedGainLoss() { return realizedGainLoss; }
        public long getTotalTradeCount() { return totalTradeCount; }
        public long getUniqueStocksTraded() { return uniqueStocksTraded; }
        
        public BigDecimal getRealizedGainLossPercentage() {
            if (totalBuyAmount.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return realizedGainLoss.divide(totalBuyAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }
}
