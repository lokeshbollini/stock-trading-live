package com.stocktrade.service;

import com.stocktrade.entity.Portfolio;
import com.stocktrade.entity.Stock;
import com.stocktrade.entity.User;
import com.stocktrade.repository.PortfolioRepository;
import com.stocktrade.repository.StockRepository;
import com.stocktrade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PortfolioService {
    
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    
    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository,
                           UserRepository userRepository,
                           StockRepository stockRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
    }
    
    public List<Portfolio> getUserPortfolio(Long userId) {
        return portfolioRepository.findActivePortfoliosByUserIdOrderBySymbol(userId);
    }
    
    public List<Portfolio> getUserPortfolioByValue(Long userId) {
        return portfolioRepository.findActivePortfoliosByUserIdOrderByValue(userId);
    }
    
    public Optional<Portfolio> getUserStockHolding(Long userId, String stockSymbol) {
        return portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol.toUpperCase());
    }
    
    public Portfolio addToPortfolio(Long userId, String stockSymbol, Integer quantity, BigDecimal purchasePrice) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        Stock stock = stockRepository.findBySymbolAndIsActiveTrue(stockSymbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Active stock not found with symbol: " + stockSymbol));
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        if (purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase price must be positive");
        }
        
        Optional<Portfolio> existingPortfolio = portfolioRepository.findByUserAndStock(user, stock);
        
        if (existingPortfolio.isPresent()) {
            Portfolio portfolio = existingPortfolio.get();
            portfolio.addShares(quantity, purchasePrice);
            return portfolioRepository.save(portfolio);
        } else {
            Portfolio newPortfolio = new Portfolio(user, stock, quantity, purchasePrice);
            return portfolioRepository.save(newPortfolio);
        }
    }
    
    public Portfolio removeFromPortfolio(Long userId, String stockSymbol, Integer quantity) {
        Portfolio portfolio = portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Portfolio holding not found"));
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        if (!portfolio.hasSufficientShares(quantity)) {
            throw new IllegalArgumentException("Insufficient shares to sell. Available: " + portfolio.getQuantity());
        }
        
        portfolio.removeShares(quantity);
        
        if (portfolio.getQuantity() == 0) {
            portfolioRepository.delete(portfolio);
            return null;
        } else {
            return portfolioRepository.save(portfolio);
        }
    }
    
    public BigDecimal getTotalPortfolioValue(Long userId) {
        Double totalValue = portfolioRepository.calculateTotalPortfolioValue(userId);
        return totalValue != null ? BigDecimal.valueOf(totalValue) : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalInvestedAmount(Long userId) {
        Double totalInvested = portfolioRepository.calculateTotalInvestedAmount(userId);
        return totalInvested != null ? BigDecimal.valueOf(totalInvested) : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalUnrealizedGainLoss(Long userId) {
        return getTotalPortfolioValue(userId).subtract(getTotalInvestedAmount(userId));
    }
    
    public BigDecimal getTotalUnrealizedGainLossPercentage(Long userId) {
        BigDecimal totalInvested = getTotalInvestedAmount(userId);
        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal gainLoss = getTotalUnrealizedGainLoss(userId);
        return gainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    public long getHoldingsCount(Long userId) {
        return portfolioRepository.countActiveHoldingsByUserId(userId);
    }
    
    public List<Portfolio> getProfitablePositions(Long userId) {
        return portfolioRepository.findProfitablePositions(userId);
    }
    
    public List<Portfolio> getLosingPositions(Long userId) {
        return portfolioRepository.findLosingPositions(userId);
    }
    
    public boolean hasPosition(Long userId, String stockSymbol) {
        return portfolioRepository.existsByUserIdAndStockId(userId, 
                stockRepository.findBySymbol(stockSymbol.toUpperCase())
                        .map(Stock::getId)
                        .orElse(-1L));
    }
    
    public Integer getSharesOwned(Long userId, String stockSymbol) {
        return portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol.toUpperCase())
                .map(Portfolio::getQuantity)
                .orElse(0);
    }
    
    public BigDecimal getAverageCostBasis(Long userId, String stockSymbol) {
        return portfolioRepository.findByUserIdAndStockSymbol(userId, stockSymbol.toUpperCase())
                .map(Portfolio::getAverageCost)
                .orElse(BigDecimal.ZERO);
    }
    
    public List<Portfolio> getStockHolders(String stockSymbol) {
        return portfolioRepository.findActiveHoldersOfStock(stockSymbol.toUpperCase());
    }
    
    public long getStockHoldersCount(String stockSymbol) {
        return portfolioRepository.countActiveHoldersOfStock(stockSymbol.toUpperCase());
    }
    
    public PortfolioSummary getPortfolioSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        BigDecimal cashBalance = user.getCashBalance();
        BigDecimal portfolioValue = getTotalPortfolioValue(userId);
        BigDecimal totalInvested = getTotalInvestedAmount(userId);
        BigDecimal unrealizedGainLoss = getTotalUnrealizedGainLoss(userId);
        BigDecimal unrealizedGainLossPercentage = getTotalUnrealizedGainLossPercentage(userId);
        long holdingsCount = getHoldingsCount(userId);
        BigDecimal totalAccountValue = cashBalance.add(portfolioValue);
        
        return new PortfolioSummary(
                cashBalance,
                portfolioValue,
                totalInvested,
                unrealizedGainLoss,
                unrealizedGainLossPercentage,
                totalAccountValue,
                holdingsCount
        );
    }
    
    public List<Portfolio> getDiversificationAnalysis(Long userId) {
        return getUserPortfolioByValue(userId);
    }
    
    public static class PortfolioSummary {
        private final BigDecimal cashBalance;
        private final BigDecimal portfolioValue;
        private final BigDecimal totalInvested;
        private final BigDecimal unrealizedGainLoss;
        private final BigDecimal unrealizedGainLossPercentage;
        private final BigDecimal totalAccountValue;
        private final long holdingsCount;
        
        public PortfolioSummary(BigDecimal cashBalance, BigDecimal portfolioValue, 
                               BigDecimal totalInvested, BigDecimal unrealizedGainLoss,
                               BigDecimal unrealizedGainLossPercentage, BigDecimal totalAccountValue,
                               long holdingsCount) {
            this.cashBalance = cashBalance;
            this.portfolioValue = portfolioValue;
            this.totalInvested = totalInvested;
            this.unrealizedGainLoss = unrealizedGainLoss;
            this.unrealizedGainLossPercentage = unrealizedGainLossPercentage;
            this.totalAccountValue = totalAccountValue;
            this.holdingsCount = holdingsCount;
        }
        
        // Getters
        public BigDecimal getCashBalance() { return cashBalance; }
        public BigDecimal getPortfolioValue() { return portfolioValue; }
        public BigDecimal getTotalInvested() { return totalInvested; }
        public BigDecimal getUnrealizedGainLoss() { return unrealizedGainLoss; }
        public BigDecimal getUnrealizedGainLossPercentage() { return unrealizedGainLossPercentage; }
        public BigDecimal getTotalAccountValue() { return totalAccountValue; }
        public long getHoldingsCount() { return holdingsCount; }
        
        public BigDecimal getCashPercentage() {
            if (totalAccountValue.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return cashBalance.divide(totalAccountValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        
        public BigDecimal getInvestedPercentage() {
            if (totalAccountValue.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return portfolioValue.divide(totalAccountValue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }
}
