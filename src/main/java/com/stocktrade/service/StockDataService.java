package com.stocktrade.service;

import com.stocktrade.entity.Stock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class StockDataService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${stock.api.alpha-vantage.api-key}")
    private String alphaVantageApiKey;
    
    @Value("${stock.api.alpha-vantage.base-url}")
    private String alphaVantageBaseUrl;
    
    public StockDataService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public Stock getStockData(String symbol) {
        try {
            String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", 
                    alphaVantageBaseUrl, symbol.toUpperCase(), alphaVantageApiKey);
            
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            return parseAlphaVantageResponse(response, symbol);
            
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to fetch stock data for " + symbol + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing stock data for " + symbol, e);
        }
    }
    
    private Stock parseAlphaVantageResponse(String response, String symbol) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode quoteNode = rootNode.get("Global Quote");
            
            if (quoteNode == null || quoteNode.isEmpty()) {
                // Handle rate limiting or API errors
                JsonNode errorNode = rootNode.get("Note");
                if (errorNode != null) {
                    throw new RuntimeException("API rate limit exceeded: " + errorNode.asText());
                }
                
                JsonNode infoNode = rootNode.get("Information");
                if (infoNode != null) {
                    throw new RuntimeException("API error: " + infoNode.asText());
                }
                
                throw new RuntimeException("No quote data found for symbol: " + symbol);
            }
            
            Stock stock = new Stock();
            stock.setSymbol(getTextValue(quoteNode, "01. symbol"));
            stock.setCurrentPrice(getBigDecimalValue(quoteNode, "05. price"));
            stock.setPreviousClose(getBigDecimalValue(quoteNode, "08. previous close"));
            stock.setDayHigh(getBigDecimalValue(quoteNode, "03. high"));
            stock.setDayLow(getBigDecimalValue(quoteNode, "04. low"));
            stock.setVolume(getLongValue(quoteNode, "06. volume"));
            
            // Try to get company name from another endpoint if needed
            if (stock.getCompanyName() == null || stock.getCompanyName().isEmpty()) {
                stock.setCompanyName(symbol.toUpperCase()); // Fallback to symbol
            }
            
            return stock;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse stock data response for " + symbol, e);
        }
    }
    
    public Stock getCompanyOverview(String symbol) {
        try {
            String url = String.format("%s?function=OVERVIEW&symbol=%s&apikey=%s", 
                    alphaVantageBaseUrl, symbol.toUpperCase(), alphaVantageApiKey);
            
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            return parseCompanyOverview(response, symbol);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch company overview for " + symbol, e);
        }
    }
    
    private Stock parseCompanyOverview(String response, String symbol) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            
            if (rootNode.isEmpty()) {
                throw new RuntimeException("No company data found for symbol: " + symbol);
            }
            
            Stock stock = new Stock();
            stock.setSymbol(getTextValue(rootNode, "Symbol"));
            stock.setCompanyName(getTextValue(rootNode, "Name"));
            stock.setMarketCap(getLongValue(rootNode, "MarketCapitalization"));
            stock.setPeRatio(getBigDecimalValue(rootNode, "PERatio"));
            stock.setDividendYield(getBigDecimalValue(rootNode, "DividendYield"));
            stock.setFiftyTwoWeekHigh(getBigDecimalValue(rootNode, "52WeekHigh"));
            stock.setFiftyTwoWeekLow(getBigDecimalValue(rootNode, "52WeekLow"));
            
            return stock;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse company overview for " + symbol, e);
        }
    }
    
    public Stock getCompleteStockData(String symbol) {
        try {
            // Get both quote and company overview
            Stock quoteData = getStockData(symbol);
            
            try {
                Stock overviewData = getCompanyOverview(symbol);
                
                // Merge the data
                if (overviewData.getCompanyName() != null) {
                    quoteData.setCompanyName(overviewData.getCompanyName());
                }
                if (overviewData.getMarketCap() != null) {
                    quoteData.setMarketCap(overviewData.getMarketCap());
                }
                if (overviewData.getPeRatio() != null) {
                    quoteData.setPeRatio(overviewData.getPeRatio());
                }
                if (overviewData.getDividendYield() != null) {
                    quoteData.setDividendYield(overviewData.getDividendYield());
                }
                if (overviewData.getFiftyTwoWeekHigh() != null) {
                    quoteData.setFiftyTwoWeekHigh(overviewData.getFiftyTwoWeekHigh());
                }
                if (overviewData.getFiftyTwoWeekLow() != null) {
                    quoteData.setFiftyTwoWeekLow(overviewData.getFiftyTwoWeekLow());
                }
                
            } catch (Exception e) {
                // If overview fails, just return quote data
                System.err.println("Failed to fetch company overview for " + symbol + ": " + e.getMessage());
            }
            
            return quoteData;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch complete stock data for " + symbol, e);
        }
    }
    
    public boolean isValidSymbol(String symbol) {
        try {
            Stock stock = getStockData(symbol);
            return stock != null && stock.getCurrentPrice() != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() && !field.asText().equals("None") ? field.asText() : null;
    }
    
    private BigDecimal getBigDecimalValue(JsonNode node, String fieldName) {
        String textValue = getTextValue(node, fieldName);
        if (textValue != null) {
            try {
                return new BigDecimal(textValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private Long getLongValue(JsonNode node, String fieldName) {
        String textValue = getTextValue(node, fieldName);
        if (textValue != null) {
            try {
                return Long.parseLong(textValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
