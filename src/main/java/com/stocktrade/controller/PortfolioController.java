package com.stocktrade.controller;

import com.stocktrade.entity.Portfolio;
import com.stocktrade.entity.User;
import com.stocktrade.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @GetMapping
    public ResponseEntity<List<Portfolio>> getPortfolio(@AuthenticationPrincipal User user) {
        List<Portfolio> portfolio = portfolioService.getUserPortfolio(user.getId());
        return ResponseEntity.ok(portfolio);
    }
    
    @GetMapping("/by-value")
    public ResponseEntity<List<Portfolio>> getPortfolioByValue(@AuthenticationPrincipal User user) {
        List<Portfolio> portfolio = portfolioService.getUserPortfolioByValue(user.getId());
        return ResponseEntity.ok(portfolio);
    }
    
    @GetMapping("/holdings/{symbol}")
    public ResponseEntity<Portfolio> getStockHolding(@AuthenticationPrincipal User user,
                                                    @PathVariable String symbol) {
        Optional<Portfolio> holding = portfolioService.getUserStockHolding(user.getId(), symbol);
        return holding.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/summary")
    public ResponseEntity<PortfolioService.PortfolioSummary> getPortfolioSummary(@AuthenticationPrincipal User user) {
        PortfolioService.PortfolioSummary summary = portfolioService.getPortfolioSummary(user.getId());
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/value")
    public ResponseEntity<PortfolioValueResponse> getPortfolioValue(@AuthenticationPrincipal User user) {
        BigDecimal totalValue = portfolioService.getTotalPortfolioValue(user.getId());
        BigDecimal totalInvested = portfolioService.getTotalInvestedAmount(user.getId());
        BigDecimal unrealizedGainLoss = portfolioService.getTotalUnrealizedGainLoss(user.getId());
        BigDecimal unrealizedGainLossPercentage = portfolioService.getTotalUnrealizedGainLossPercentage(user.getId());
        
        PortfolioValueResponse response = new PortfolioValueResponse(
                totalValue, totalInvested, unrealizedGainLoss, unrealizedGainLossPercentage);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profitable")
    public ResponseEntity<List<Portfolio>> getProfitablePositions(@AuthenticationPrincipal User user) {
        List<Portfolio> positions = portfolioService.getProfitablePositions(user.getId());
        return ResponseEntity.ok(positions);
    }
    
    @GetMapping("/losing")
    public ResponseEntity<List<Portfolio>> getLosingPositions(@AuthenticationPrincipal User user) {
        List<Portfolio> positions = portfolioService.getLosingPositions(user.getId());
        return ResponseEntity.ok(positions);
    }
    
    @GetMapping("/diversification")
    public ResponseEntity<List<Portfolio>> getDiversificationAnalysis(@AuthenticationPrincipal User user) {
        List<Portfolio> analysis = portfolioService.getDiversificationAnalysis(user.getId());
        return ResponseEntity.ok(analysis);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<PortfolioStatsResponse> getPortfolioStats(@AuthenticationPrincipal User user) {
        long holdingsCount = portfolioService.getHoldingsCount(user.getId());
        List<Portfolio> profitablePositions = portfolioService.getProfitablePositions(user.getId());
        List<Portfolio> losingPositions = portfolioService.getLosingPositions(user.getId());
        
        PortfolioStatsResponse stats = new PortfolioStatsResponse(
                holdingsCount, profitablePositions.size(), losingPositions.size());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/position/{symbol}")
    public ResponseEntity<PositionDetailsResponse> getPositionDetails(@AuthenticationPrincipal User user,
                                                                     @PathVariable String symbol) {
        boolean hasPosition = portfolioService.hasPosition(user.getId(), symbol);
        if (!hasPosition) {
            return ResponseEntity.notFound().build();
        }
        
        Integer sharesOwned = portfolioService.getSharesOwned(user.getId(), symbol);
        BigDecimal averageCost = portfolioService.getAverageCostBasis(user.getId(), symbol);
        
        PositionDetailsResponse response = new PositionDetailsResponse(symbol, sharesOwned, averageCost);
        return ResponseEntity.ok(response);
    }
    
    public static class PortfolioValueResponse {
        private BigDecimal totalValue;
        private BigDecimal totalInvested;
        private BigDecimal unrealizedGainLoss;
        private BigDecimal unrealizedGainLossPercentage;
        
        public PortfolioValueResponse(BigDecimal totalValue, BigDecimal totalInvested,
                                     BigDecimal unrealizedGainLoss, BigDecimal unrealizedGainLossPercentage) {
            this.totalValue = totalValue;
            this.totalInvested = totalInvested;
            this.unrealizedGainLoss = unrealizedGainLoss;
            this.unrealizedGainLossPercentage = unrealizedGainLossPercentage;
        }
        
        public BigDecimal getTotalValue() {
            return totalValue;
        }
        
        public void setTotalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
        }
        
        public BigDecimal getTotalInvested() {
            return totalInvested;
        }
        
        public void setTotalInvested(BigDecimal totalInvested) {
            this.totalInvested = totalInvested;
        }
        
        public BigDecimal getUnrealizedGainLoss() {
            return unrealizedGainLoss;
        }
        
        public void setUnrealizedGainLoss(BigDecimal unrealizedGainLoss) {
            this.unrealizedGainLoss = unrealizedGainLoss;
        }
        
        public BigDecimal getUnrealizedGainLossPercentage() {
            return unrealizedGainLossPercentage;
        }
        
        public void setUnrealizedGainLossPercentage(BigDecimal unrealizedGainLossPercentage) {
            this.unrealizedGainLossPercentage = unrealizedGainLossPercentage;
        }
    }
    
    public static class PortfolioStatsResponse {
        private long totalHoldings;
        private long profitablePositions;
        private long losingPositions;
        
        public PortfolioStatsResponse(long totalHoldings, long profitablePositions, long losingPositions) {
            this.totalHoldings = totalHoldings;
            this.profitablePositions = profitablePositions;
            this.losingPositions = losingPositions;
        }
        
        public long getTotalHoldings() {
            return totalHoldings;
        }
        
        public void setTotalHoldings(long totalHoldings) {
            this.totalHoldings = totalHoldings;
        }
        
        public long getProfitablePositions() {
            return profitablePositions;
        }
        
        public void setProfitablePositions(long profitablePositions) {
            this.profitablePositions = profitablePositions;
        }
        
        public long getLosingPositions() {
            return losingPositions;
        }
        
        public void setLosingPositions(long losingPositions) {
            this.losingPositions = losingPositions;
        }
    }
    
    public static class PositionDetailsResponse {
        private String symbol;
        private Integer sharesOwned;
        private BigDecimal averageCostBasis;
        
        public PositionDetailsResponse(String symbol, Integer sharesOwned, BigDecimal averageCostBasis) {
            this.symbol = symbol;
            this.sharesOwned = sharesOwned;
            this.averageCostBasis = averageCostBasis;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
        
        public Integer getSharesOwned() {
            return sharesOwned;
        }
        
        public void setSharesOwned(Integer sharesOwned) {
            this.sharesOwned = sharesOwned;
        }
        
        public BigDecimal getAverageCostBasis() {
            return averageCostBasis;
        }
        
        public void setAverageCostBasis(BigDecimal averageCostBasis) {
            this.averageCostBasis = averageCostBasis;
        }
    }
}
