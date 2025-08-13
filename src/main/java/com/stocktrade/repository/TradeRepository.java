package com.stocktrade.repository;

import com.stocktrade.entity.Trade;
import com.stocktrade.entity.User;
import com.stocktrade.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    
    List<Trade> findByUser(User user);
    
    List<Trade> findByUserId(Long userId);
    
    Page<Trade> findByUserId(Long userId, Pageable pageable);
    
    List<Trade> findByUserIdOrderByExecutedAtDesc(Long userId);
    
    Page<Trade> findByUserIdOrderByExecutedAtDesc(Long userId, Pageable pageable);
    
    List<Trade> findByStock(Stock stock);
    
    List<Trade> findByStockSymbol(String stockSymbol);
    
    List<Trade> findByTradeType(Trade.TradeType tradeType);
    
    List<Trade> findByTradeStatus(Trade.TradeStatus tradeStatus);
    
    List<Trade> findByUserIdAndTradeType(Long userId, Trade.TradeType tradeType);
    
    List<Trade> findByUserIdAndStockSymbol(Long userId, String stockSymbol);
    
    List<Trade> findByUserIdAndStockSymbolOrderByExecutedAtDesc(Long userId, String stockSymbol);
    
    @Query("SELECT t FROM Trade t WHERE t.user.id = :userId AND t.executedAt >= :fromDate")
    List<Trade> findUserTradesSince(@Param("userId") Long userId, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT t FROM Trade t WHERE t.user.id = :userId AND t.executedAt BETWEEN :fromDate AND :toDate")
    List<Trade> findUserTradesBetween(@Param("userId") Long userId, 
                                     @Param("fromDate") LocalDateTime fromDate, 
                                     @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT t FROM Trade t WHERE t.stock.symbol = :symbol AND t.executedAt >= :fromDate")
    List<Trade> findStockTradesSince(@Param("symbol") String symbol, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT SUM(t.totalAmount) FROM Trade t WHERE t.user.id = :userId AND t.tradeType = :tradeType AND t.tradeStatus = 'COMPLETED'")
    Double calculateTotalTradeAmount(@Param("userId") Long userId, @Param("tradeType") Trade.TradeType tradeType);
    
    @Query("SELECT SUM(t.quantity) FROM Trade t WHERE t.user.id = :userId AND t.stock.symbol = :symbol AND t.tradeType = :tradeType AND t.tradeStatus = 'COMPLETED'")
    Integer calculateTotalQuantityTraded(@Param("userId") Long userId, 
                                        @Param("symbol") String symbol, 
                                        @Param("tradeType") Trade.TradeType tradeType);
    
    @Query("SELECT COUNT(t) FROM Trade t WHERE t.user.id = :userId AND t.tradeStatus = 'COMPLETED'")
    long countCompletedTradesByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Trade t WHERE t.stock.symbol = :symbol AND t.tradeStatus = 'COMPLETED'")
    long countCompletedTradesForStock(@Param("symbol") String symbol);
    
    @Query("SELECT t FROM Trade t WHERE t.tradeStatus = 'PENDING' ORDER BY t.createdAt ASC")
    List<Trade> findPendingTrades();
    
    @Query("SELECT AVG(t.price) FROM Trade t WHERE t.stock.symbol = :symbol AND t.executedAt >= :fromDate AND t.tradeStatus = 'COMPLETED'")
    Double calculateAverageTradePrice(@Param("symbol") String symbol, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT SUM(t.quantity) FROM Trade t WHERE t.stock.symbol = :symbol AND t.executedAt >= :fromDate AND t.tradeStatus = 'COMPLETED'")
    Long calculateTotalVolume(@Param("symbol") String symbol, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT t FROM Trade t WHERE t.user.id = :userId AND t.tradeStatus = 'COMPLETED' ORDER BY t.executedAt DESC")
    List<Trade> findRecentCompletedTrades(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT DISTINCT t.stock.symbol FROM Trade t WHERE t.user.id = :userId AND t.tradeStatus = 'COMPLETED'")
    List<String> findTradedStockSymbolsByUser(@Param("userId") Long userId);
}
