package com.stocktrade.repository;

import com.stocktrade.entity.Portfolio;
import com.stocktrade.entity.Stock;
import com.stocktrade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    Optional<Portfolio> findByUserAndStock(User user, Stock stock);
    
    Optional<Portfolio> findByUserIdAndStockId(Long userId, Long stockId);
    
    Optional<Portfolio> findByUserIdAndStockSymbol(Long userId, String stockSymbol);
    
    List<Portfolio> findByUser(User user);
    
    List<Portfolio> findByUserId(Long userId);
    
    List<Portfolio> findByUserIdAndQuantityGreaterThan(Long userId, Integer quantity);
    
    List<Portfolio> findByStock(Stock stock);
    
    List<Portfolio> findByStockSymbol(String stockSymbol);
    
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND p.quantity > 0")
    List<Portfolio> findActivePortfoliosByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND p.quantity > 0 ORDER BY p.stock.symbol ASC")
    List<Portfolio> findActivePortfoliosByUserIdOrderBySymbol(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND p.quantity > 0 " +
           "ORDER BY (p.stock.currentPrice * p.quantity) DESC")
    List<Portfolio> findActivePortfoliosByUserIdOrderByValue(@Param("userId") Long userId);
    
    @Query("SELECT SUM(p.stock.currentPrice * p.quantity) FROM Portfolio p WHERE p.user.id = :userId AND p.quantity > 0")
    Double calculateTotalPortfolioValue(@Param("userId") Long userId);
    
    @Query("SELECT SUM(p.averageCost * p.quantity) FROM Portfolio p WHERE p.user.id = :userId AND p.quantity > 0")
    Double calculateTotalInvestedAmount(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM Portfolio p WHERE p.user.id = :userId AND p.quantity > 0")
    long countActiveHoldingsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Portfolio p WHERE p.stock.symbol = :symbol AND p.quantity > 0")
    List<Portfolio> findActiveHoldersOfStock(@Param("symbol") String symbol);
    
    @Query("SELECT COUNT(DISTINCT p.user.id) FROM Portfolio p WHERE p.stock.symbol = :symbol AND p.quantity > 0")
    long countActiveHoldersOfStock(@Param("symbol") String symbol);
    
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND p.quantity > 0 AND " +
           "((p.stock.currentPrice * p.quantity) - (p.averageCost * p.quantity)) > 0")
    List<Portfolio> findProfitablePositions(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Portfolio p WHERE p.user.id = :userId AND p.quantity > 0 AND " +
           "((p.stock.currentPrice * p.quantity) - (p.averageCost * p.quantity)) < 0")
    List<Portfolio> findLosingPositions(@Param("userId") Long userId);
    
    boolean existsByUserAndStock(User user, Stock stock);
    
    boolean existsByUserIdAndStockId(Long userId, Long stockId);
}
