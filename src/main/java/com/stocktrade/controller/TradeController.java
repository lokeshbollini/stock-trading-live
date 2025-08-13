package com.stocktrade.controller;

import com.stocktrade.dto.TradeRequest;
import com.stocktrade.entity.Trade;
import com.stocktrade.entity.User;
import com.stocktrade.service.TradeService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    
    @Autowired
    private TradeService tradeService;
    
    @PostMapping("/buy")
    public ResponseEntity<?> executeBuyOrder(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody TradeRequest tradeRequest) {
        try {
            Trade trade = tradeService.executeBuyOrder(
                    user.getId(),
                    tradeRequest.getStockSymbol(),
                    tradeRequest.getQuantity(),
                    tradeRequest.getPrice()
            );
            return ResponseEntity.ok(trade);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Buy order failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/sell")
    public ResponseEntity<?> executeSellOrder(@AuthenticationPrincipal User user,
                                            @Valid @RequestBody TradeRequest tradeRequest) {
        try {
            Trade trade = tradeService.executeSellOrder(
                    user.getId(),
                    tradeRequest.getStockSymbol(),
                    tradeRequest.getQuantity(),
                    tradeRequest.getPrice()
            );
            return ResponseEntity.ok(trade);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Sell order failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<Page<Trade>> getTradeHistory(@AuthenticationPrincipal User user,
                                                      Pageable pageable) {
        Page<Trade> trades = tradeService.getUserTrades(user.getId(), pageable);
        return ResponseEntity.ok(trades);
    }
    
    @GetMapping("/history/all")
    public ResponseEntity<List<Trade>> getAllTradeHistory(@AuthenticationPrincipal User user) {
        List<Trade> trades = tradeService.getUserTrades(user.getId());
        return ResponseEntity.ok(trades);
    }
    
    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<Trade>> getTradeHistoryForStock(@AuthenticationPrincipal User user,
                                                              @PathVariable String symbol) {
        List<Trade> trades = tradeService.getUserTradesForStock(user.getId(), symbol);
        return ResponseEntity.ok(trades);
    }
    
    @GetMapping("/history/since")
    public ResponseEntity<List<Trade>> getTradeHistorySince(@AuthenticationPrincipal User user,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) {
        List<Trade> trades = tradeService.getUserTradesSince(user.getId(), fromDate);
        return ResponseEntity.ok(trades);
    }
    
    @GetMapping("/history/between")
    public ResponseEntity<List<Trade>> getTradeHistoryBetween(@AuthenticationPrincipal User user,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        List<Trade> trades = tradeService.getUserTradesBetween(user.getId(), fromDate, toDate);
        return ResponseEntity.ok(trades);
    }
    
    @GetMapping("/summary")
    public ResponseEntity<TradeService.TradeSummary> getTradeSummary(@AuthenticationPrincipal User user) {
        TradeService.TradeSummary summary = tradeService.getTradeSummary(user.getId());
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/validate/buy")
    public ResponseEntity<TradeValidationResponse> validateBuyOrder(@AuthenticationPrincipal User user,
                                                                   @RequestParam String symbol,
                                                                   @RequestParam Integer quantity) {
        boolean canAfford = tradeService.canAffordTrade(user.getId(), symbol, quantity);
        return ResponseEntity.ok(new TradeValidationResponse(canAfford, 
                canAfford ? "Order can be executed" : "Insufficient cash balance"));
    }
    
    @GetMapping("/validate/sell")
    public ResponseEntity<TradeValidationResponse> validateSellOrder(@AuthenticationPrincipal User user,
                                                                    @RequestParam String symbol,
                                                                    @RequestParam Integer quantity) {
        boolean canSell = tradeService.canSellShares(user.getId(), symbol, quantity);
        return ResponseEntity.ok(new TradeValidationResponse(canSell, 
                canSell ? "Order can be executed" : "Insufficient shares"));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<TradeStatsResponse> getTradeStats(@AuthenticationPrincipal User user) {
        long totalTrades = tradeService.getUserTradeCount(user.getId());
        List<String> tradedSymbols = tradeService.getUserTradedSymbols(user.getId());
        
        TradeStatsResponse stats = new TradeStatsResponse(totalTrades, tradedSymbols.size(), tradedSymbols);
        return ResponseEntity.ok(stats);
    }
    
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class TradeValidationResponse {
        private boolean valid;
        private String message;
        
        public TradeValidationResponse(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class TradeStatsResponse {
        private long totalTrades;
        private int uniqueStocksTraded;
        private List<String> tradedSymbols;
        
        public TradeStatsResponse(long totalTrades, int uniqueStocksTraded, List<String> tradedSymbols) {
            this.totalTrades = totalTrades;
            this.uniqueStocksTraded = uniqueStocksTraded;
            this.tradedSymbols = tradedSymbols;
        }
        
        public long getTotalTrades() {
            return totalTrades;
        }
        
        public void setTotalTrades(long totalTrades) {
            this.totalTrades = totalTrades;
        }
        
        public int getUniqueStocksTraded() {
            return uniqueStocksTraded;
        }
        
        public void setUniqueStocksTraded(int uniqueStocksTraded) {
            this.uniqueStocksTraded = uniqueStocksTraded;
        }
        
        public List<String> getTradedSymbols() {
            return tradedSymbols;
        }
        
        public void setTradedSymbols(List<String> tradedSymbols) {
            this.tradedSymbols = tradedSymbols;
        }
    }
}
