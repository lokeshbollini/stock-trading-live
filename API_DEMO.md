# 🚀 Stock Trading Simulator - Live API Demo

**Try these commands with your running application!** Make sure your app is running on http://localhost:8080

## 📊 **1. Public Endpoints (No Authentication Required)**

### Get Live Apple Stock Quote
```bash
curl http://localhost:8080/api/stocks/AAPL/quote
```
**Expected Response:** Real-time AAPL data with current price, volume, high/low

### Search for Stocks
```bash
curl "http://localhost:8080/api/stocks/search?symbol=TSLA"
```

### Get Popular Stocks List
```bash
curl http://localhost:8080/api/stocks/popular
```

---

## 👤 **2. User Registration & Authentication**

### Register a New Trader
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newtrader",
    "email": "trader@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Trader"
  }'
```

### Login and Get JWT Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newtrader",
    "password": "password123"
  }'
```
**Copy the token from the response - you'll need it for authenticated endpoints!**

---

## 💰 **3. Trading Operations (Requires Authentication)**

> **Important:** Replace `YOUR_JWT_TOKEN` with the actual token from login response

### Execute a Buy Order
```bash
curl -X POST http://localhost:8080/api/trades/buy \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "stockSymbol": "AAPL",
    "quantity": 5
  }'
```

### View Your Portfolio
```bash
curl -X GET http://localhost:8080/api/portfolio/summary \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Check Trading History
```bash
curl -X GET http://localhost:8080/api/trades/history \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Your Profile
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🎮 **4. Demo Account Quick Start**

**Skip registration** and use the pre-loaded demo account:

### Login as Demo User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "demo123"
  }'
```

The demo user already has:
- ✅ $50,000 virtual cash
- ✅ Existing portfolio with AAPL, MSFT, TSLA holdings
- ✅ Trading history with previous transactions

---

## 📈 **5. Advanced Portfolio Analysis**

### Get Current Portfolio Value
```bash
curl -X GET http://localhost:8080/api/portfolio/value \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### View Portfolio Performance
```bash
curl -X GET http://localhost:8080/api/portfolio \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🔍 **6. Database Exploration**

### Access H2 Database Console
1. Open: http://localhost:8080/h2-console
2. **JDBC URL**: `jdbc:h2:mem:stocktrade`
3. **Username**: `sa`
4. **Password**: (leave blank)

### Useful SQL Queries to Try:
```sql
-- View all stocks with current prices
SELECT * FROM STOCKS;

-- Check user portfolios
SELECT * FROM PORTFOLIOS;

-- See recent trades
SELECT * FROM TRADES ORDER BY executed_at DESC;

-- Portfolio summary with stock details
SELECT p.*, s.symbol, s.company_name, s.current_price 
FROM PORTFOLIOS p 
JOIN STOCKS s ON p.stock_id = s.id;
```

---

## 💡 **Pro Tips**

1. **Start with demo account** for immediate testing
2. **Check live data** with public endpoints first
3. **Use H2 console** to see what's happening in the database
4. **JWT tokens expire** - login again if you get 401 errors
5. **Real stock prices** update from Alpha Vantage API

---

## 🚨 **Troubleshooting**

### Common Issues:
- **Connection refused**: Make sure app is running (`mvn spring-boot:run`)
- **Unauthorized (401)**: Login again to get a fresh JWT token
- **Empty responses**: Check if Alpha Vantage API key is set correctly

### Check App Status:
```bash
# Test if app is running
curl http://localhost:8080/actuator/health

# Should return: {"status":"UP"}
```

---

**🎯 Happy Trading!** Your simulator uses **real market data** so you can practice with actual stock movements without any financial risk.
