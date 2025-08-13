package com.stocktrade.repository;

import com.stocktrade.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    Optional<Stock> findBySymbol(String symbol);
    
    Optional<Stock> findBySymbolAndIsActiveTrue(String symbol);
    
    List<Stock> findByIsActiveTrue();
    
    List<Stock> findByIsActiveTrueOrderBySymbolAsc();
    
    @Query("SELECT s FROM Stock s WHERE s.isActive = true AND " +
           "(LOWER(s.symbol) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.companyName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Stock> searchActiveStocks(@Param("search") String search);
    
    @Query("SELECT s FROM Stock s WHERE s.lastUpdated < :cutoffTime AND s.isActive = true")
    List<Stock> findStaleStocks(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT s FROM Stock s WHERE s.isActive = true ORDER BY s.volume DESC")
    List<Stock> findActiveStocksByVolumeDesc();
    
    @Query("SELECT s FROM Stock s WHERE s.isActive = true ORDER BY s.currentPrice DESC")
    List<Stock> findActiveStocksByPriceDesc();
    
    @Query("SELECT s FROM Stock s WHERE s.isActive = true ORDER BY s.currentPrice ASC")
    List<Stock> findActiveStocksByPriceAsc();
    
    @Query("SELECT COUNT(s) FROM Stock s WHERE s.isActive = true")
    long countActiveStocks();
    
    @Query("SELECT s FROM Stock s JOIN s.portfolios p WHERE p.user.id = :userId AND s.isActive = true")
    List<Stock> findStocksInUserPortfolio(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT s FROM Stock s JOIN s.trades t WHERE t.executedAt >= :fromDate AND s.isActive = true")
    List<Stock> findStocksWithRecentTrades(@Param("fromDate") LocalDateTime fromDate);
    
    boolean existsBySymbol(String symbol);
}
