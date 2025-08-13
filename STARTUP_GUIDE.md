# 🚀 Stock Trading Simulator - Complete Startup Guide

## Your Application is Ready!

I've created a complete **Stock Trading Simulator** with your Alpha Vantage API key: `FXQLVLO4WPQXSJQF`

## ✅ What's Been Set Up

- **Complete Spring Boot Application** with all trading features
- **Your API Key** already configured in the application
- **H2 Database** for easy development (no PostgreSQL setup needed)
- **Sample Data** with 15 popular stocks pre-loaded
- **Demo Users** ready for testing
- **Maven** downloaded and configured

## 🎯 Quick Start (3 Simple Steps)

### Step 1: Open PowerShell in Project Directory
```powershell
cd "C:\Projects\Stock trade"
```

### Step 2: Set Up Environment and Run
```powershell
# Set up Maven and environment
$env:PATH = "$pwd\apache-maven-3.9.4\bin;$env:PATH"
$env:M2_HOME = "$pwd\apache-maven-3.9.4" 
$env:SPRING_PROFILES_ACTIVE = "dev"
$env:ALPHA_VANTAGE_API_KEY = "FXQLVLO4WPQXSJQF"

# Start the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Step 3: Wait for Startup
Look for this message in the console:
```
Started StockTradingSimulatorApplication in X.XXX seconds
```

## 🧪 Test Your Application

Once running, test these endpoints:

### 1. Get All Stocks (No Auth Required)
```bash
curl http://localhost:8080/api/stocks
```

### 2. Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "trader1",
    "email": "trader1@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### 3. Login and Get JWT Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "trader1",
    "password": "password123"
  }'
```

### 4. Use Demo Account (Pre-configured)
- **Username**: `demo`
- **Password**: `demo123`
- **Starting Balance**: $50,000

## 🎮 Demo Scenarios

### Scenario 1: Login as Demo User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "demo", "password": "demo123"}'
```

Save the JWT token from the response.

### Scenario 2: Check Portfolio
```bash
curl -X GET http://localhost:8080/api/portfolio \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Scenario 3: Buy Apple Stock
```bash
curl -X POST http://localhost:8080/api/trades/buy \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "stockSymbol": "AAPL",
    "quantity": 5,
    "price": 189.50
  }'
```

### Scenario 4: Get Real-Time Stock Quote
```bash
curl http://localhost:8080/api/stocks/AAPL/quote
```

## 🔧 Alternative Startup Methods

### Method 1: Using Batch File
```cmd
start-app.bat
```

### Method 2: Using PowerShell Script
```powershell
.\setup-and-run.ps1
```

### Method 3: Manual Maven Commands
```bash
# Set environment
set SPRING_PROFILES_ACTIVE=dev
set ALPHA_VANTAGE_API_KEY=FXQLVLO4WPQXSJQF

# Run application
apache-maven-3.9.4\bin\mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🌐 Web Interface Access

Once running, you can access:
- **API Base URL**: `http://localhost:8080`
- **H2 Database Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:stocktrade`
  - Username: `sa`
  - Password: (leave empty)

## 📊 Pre-loaded Data

### Sample Stocks
- AAPL (Apple Inc.) - $189.50
- GOOGL (Alphabet Inc.) - $142.35  
- MSFT (Microsoft) - $378.85
- AMZN (Amazon) - $153.75
- TSLA (Tesla) - $248.50
- And 10+ more popular stocks

### Demo Users
1. **Admin User**
   - Username: `admin`
   - Password: `admin123`
   - Balance: $100,000

2. **Demo Trader**
   - Username: `demo`
   - Password: `demo123`  
   - Balance: $50,000
   - Has existing trades and portfolio

## 🚨 Troubleshooting

### If Port 8080 is Busy
```bash
# Check what's using port 8080
netstat -ano | findstr :8080

# Kill the process if needed
taskkill /PID <PID_NUMBER> /F
```

### If Maven Fails
1. Ensure Java 11+ is installed: `java -version`
2. Clear Maven cache: `rm -rf ~/.m2/repository`
3. Try: `mvn clean compile`

### If Database Errors
The app uses H2 in-memory database, so no external database setup needed.

## 🎯 What This Application Demonstrates

### FinTech Skills
- ✅ **Real-time market data** integration
- ✅ **Financial transaction** processing  
- ✅ **Portfolio management** and analytics
- ✅ **Risk validation** (balance checks, position limits)
- ✅ **Audit trails** for compliance

### Technical Skills  
- ✅ **Microservices architecture** (UserService, TradeService, etc.)
- ✅ **RESTful API design** with proper HTTP codes
- ✅ **Database design** with normalized schema
- ✅ **Security implementation** (JWT, authentication)
- ✅ **External API integration** (Alpha Vantage)
- ✅ **Transaction management** (@Transactional)

## 🎉 You're Ready to Trade!

Your **Stock Trading Simulator** is fully configured with:
- ✅ Real market data from Alpha Vantage
- ✅ Complete trading functionality
- ✅ Portfolio management
- ✅ User authentication
- ✅ Sample data for testing

**Start the application and begin trading!** 📈💰

---
*Built with Java Spring Boot • Powered by Alpha Vantage API • Ready for FinTech interviews*
