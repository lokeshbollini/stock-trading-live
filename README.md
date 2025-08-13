# 📈 Stock Trading Simulator - Live Demo

> **🚀 LIVE DEMO**: A full-featured stock trading simulation platform with **REAL market data** integration

A comprehensive stock trading simulation platform built with **Java Spring Boot** that allows users to trade stocks using **live real-time market data** without risking real money. Perfect for learning about financial markets, practicing trading strategies, and demonstrating software engineering skills for FinTech roles.

## 🌟 **What Users Can Experience**

### **📊 Real-Time Stock Data** 
- **Live prices** from Alpha Vantage API (updates every minute)
- **Current market data** for 500+ stocks including FAANG, Tesla, and more
- **Real trading hours** with accurate market open/close times
- **Actual volume and price movement** from NYSE and NASDAQ

### **💰 Realistic Trading Experience**
- Start with **$50,000 virtual cash** (like a real trading account)
- **Buy and sell stocks** at current market prices
- **Portfolio tracking** with real-time profit/loss calculations
- **Transaction history** with timestamps and execution prices

## 🚀 Features

### Core Trading Features
- **Real-time Stock Data** - Integration with Alpha Vantage API for live market prices
- **Buy/Sell Orders** - Execute market orders with real-time price validation
- **Portfolio Management** - Track holdings, performance, and diversification
- **Transaction History** - Complete audit trail of all trades
- **Cash Management** - Virtual cash balance with deposit/withdrawal simulation

### Advanced Features
- **User Authentication** - JWT-based secure authentication system
- **Portfolio Analytics** - Unrealized gains/losses, performance metrics
- **Stock Discovery** - Search, browse popular stocks, top gainers/losers
- **Real-time Validation** - Check buying power and share availability
- **Data Persistence** - PostgreSQL database with optimized queries

### Technical Architecture
- **Microservices Design** - Modular service layer (UserService, TradeService, PortfolioService, StockService)
- **RESTful APIs** - Comprehensive REST endpoints with proper HTTP status codes
- **Database Design** - Normalized schema with proper relationships and indexes
- **Security** - Spring Security with JWT tokens and role-based access control
- **Transaction Management** - ACID compliance for financial operations

## 🛠 Technology Stack

- **Backend**: Java 17, Spring Boot 3.2
- **Database**: PostgreSQL (with H2 for testing)
- **Security**: Spring Security, JWT
- **Data Access**: Spring Data JPA, Hibernate
- **External APIs**: Alpha Vantage Stock API
- **Build Tool**: Maven
- **Validation**: Bean Validation (JSR-303)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (or use H2 for development)
- Alpha Vantage API key (free at [alphavantage.co](https://www.alphavantage.co/support/#api-key))

## 🚀 **5-Minute Quick Start Guide**

### **Step 1: Clone & Start (2 minutes)**
```bash
# Clone the repository
git clone https://github.com/lokeshbollini/stock-trading-live.git
cd stock-trading-live

# Start with development database (H2 - no setup needed!)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### **Step 2: Get Your Free API Key (1 minute)**
1. Visit [Alpha Vantage](https://www.alphavantage.co/support/#api-key) 
2. Get your free API key (instant, no credit card needed)
3. Copy your API key

### **Step 3: Set Your API Key (30 seconds)**
```bash
# Windows PowerShell
$env:ALPHA_VANTAGE_API_KEY = "YOUR_API_KEY_HERE"

# Linux/Mac
export ALPHA_VANTAGE_API_KEY=your_api_key_here
```

### **Step 4: Test Live Data (30 seconds)**
```bash
# Check if app is running (should see "Started StockTradingSimulatorApplication")
# Then test live stock data:
curl http://localhost:8080/api/stocks/AAPL/quote
```

### **Step 5: Start Trading! (1 minute)**
1. Open **H2 Console**: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:stocktrade`
   - Username: `sa`, Password: (blank)
2. **Pre-loaded Demo Data**: 
   - 15 popular stocks (AAPL, GOOGL, TSLA, etc.)
   - Demo user: `demo` / password: `demo123`
   - Admin user: `admin` / password: `admin123`

**✅ You're Ready!** Your trading simulator is running with:
- 🔴 **Live market data**
- 👥 **Demo accounts** with virtual cash
- 📊 **Full trading functionality**
- 🗄️ **Database browser** to explore data

---

## 📊 **What You'll See Immediately**

### **Live Stock Quotes** (Public - No Login Required)
```bash
# Real Apple stock data
curl http://localhost:8080/api/stocks/AAPL/quote
# Returns current price: $229.65, volume: 55M+, today's high/low
```

### **Demo User Dashboard**
- **Virtual Cash**: $50,000 to start trading
- **Pre-loaded Portfolio**: Sample holdings in AAPL, MSFT, TSLA
- **Trading History**: Previous buy/sell transactions
- **Live P&L**: Real-time profit/loss calculations

### **Database Tables to Explore**
- **STOCKS**: 15 popular stocks with current prices
- **USERS**: Demo accounts ready to use
- **PORTFOLIOS**: Sample holdings with live valuations  
- **TRADES**: Trading history with timestamps

## 🚀 Advanced Setup (Optional)

### For Production Database (PostgreSQL)
```bash
# Create PostgreSQL database
createdb stocktrade

# Or use Docker
docker run --name postgres-stocktrade \
  -e POSTGRES_DB=stocktrade \
  -e POSTGRES_USER=stocktrade_user \
  -e POSTGRES_PASSWORD=stocktrade_pass \
  -p 5432:5432 -d postgres:13
```

### Full Environment Configuration
```bash
# Required
export ALPHA_VANTAGE_API_KEY=your_api_key_here

# Optional (with defaults)
export JWT_SECRET=your-secret-key-min-32-chars-long
export POSTGRES_URL=jdbc:postgresql://localhost:5432/stocktrade
```

## 📚 API Documentation

### Authentication Endpoints
```
POST /api/auth/register    - Register new user
POST /api/auth/login       - User login
POST /api/auth/validate    - Validate JWT token
```

### Trading Endpoints
```
POST /api/trades/buy       - Execute buy order
POST /api/trades/sell      - Execute sell order
GET  /api/trades/history   - Get trade history
GET  /api/trades/summary   - Get trading summary
```

### Portfolio Endpoints
```
GET  /api/portfolio        - Get user portfolio
GET  /api/portfolio/summary - Portfolio summary & metrics
GET  /api/portfolio/value  - Current portfolio value
```

### Stock Data Endpoints
```
GET  /api/stocks           - List all stocks
GET  /api/stocks/{symbol}  - Get stock details
GET  /api/stocks/{symbol}/quote - Real-time quote
GET  /api/stocks/popular   - Popular stocks
```

### User Management
```
GET  /api/users/me         - Get user profile
PUT  /api/users/me         - Update profile
POST /api/users/me/cash/add - Add virtual cash
```

## 💾 Database Schema

### Core Tables
- **users** - User accounts and authentication
- **stocks** - Stock information and market data
- **portfolios** - User stock holdings
- **trades** - Transaction history

### Key Relationships
- Users have many Portfolios and Trades
- Stocks have many Portfolios and Trades
- Portfolio tracks user's position in each stock
- Trades record all buy/sell transactions

## 🔒 Security Features

- **JWT Authentication** - Stateless token-based auth
- **Password Encryption** - BCrypt hashing
- **Role-based Access** - USER and ADMIN roles
- **CORS Configuration** - Secure cross-origin requests
- **Input Validation** - Bean validation on all endpoints

## 🎮 **Live Demo Scenarios - Try These!**

### **Scenario 1: Quick Stock Check with REAL Data**
```bash
# Get live Apple stock price (no authentication needed!)
curl http://localhost:8080/api/stocks/AAPL/quote

# Sample REAL response you'll see:
{
  "symbol": "AAPL",
  "companyName": "Apple Inc.",
  "currentPrice": 229.65,      // ← LIVE price from market!
  "previousClose": 227.18,
  "priceChange": 2.47,
  "priceChangePercentage": 1.09,
  "dayHigh": 230.80,
  "dayLow": 227.07,
  "volume": 55672301           // ← Real trading volume!
}
```

### **Scenario 2: Start Trading Journey**
```bash
# 1. Create your trader account
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "trader123",
    "email": "trader@example.com", 
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# 2. Login and get your trading token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "trader123",
    "password": "password123"
  }'
# Returns: {"token": "eyJhbGciOiJ...", "username": "trader123"}
```

### **Scenario 3: Execute Your First Trade**
```bash
# Buy 10 shares of Apple at current market price
curl -X POST http://localhost:8080/api/trades/buy \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "stockSymbol": "AAPL",
    "quantity": 10
  }'

# Sample response:
{
  "tradeId": 1,
  "stockSymbol": "AAPL",
  "quantity": 10,
  "executionPrice": 229.65,
  "totalAmount": 2296.50,
  "tradeType": "BUY",
  "executedAt": "2024-01-15T14:30:15Z",
  "status": "EXECUTED"
}
```

### **Scenario 4: Check Your Portfolio Performance**
```bash
# See your portfolio with live valuations
curl -X GET http://localhost:8080/api/portfolio/summary \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Sample response showing REAL gains/losses:
{
  "totalValue": 48734.50,
  "cashBalance": 47703.50,
  "totalGainLoss": -1265.50,
  "totalGainLossPercentage": -2.53,
  "holdings": [
    {
      "symbol": "AAPL",
      "quantity": 10,
      "averageCost": 229.65,
      "currentPrice": 230.12,     // ← Updated live!
      "marketValue": 2301.20,
      "unrealizedGainLoss": 4.70,
      "unrealizedGainLossPercentage": 0.20
    }
  ]
}
```

## 🧪 More API Examples

## 🚦 Getting Started for Development

1. **Start with Authentication** - Register a user and get a JWT token
2. **Add Virtual Cash** - Use the cash management endpoint to add funds
3. **Browse Stocks** - Explore available stocks and get real-time quotes
4. **Execute Trades** - Buy stocks and watch your portfolio grow
5. **Monitor Performance** - Track gains/losses and trading history

## 💡 Business Logic Highlights

### Transaction Processing
- **Atomic Operations** - All trades use database transactions
- **Balance Validation** - Prevents overdrafts and overselling
- **Real-time Pricing** - Uses current market prices for execution
- **Cost Basis Tracking** - Maintains average cost for tax calculations

### Portfolio Management
- **Position Tracking** - Quantity and average cost per stock
- **Performance Metrics** - Unrealized gains/losses with percentages
- **Diversification Analysis** - Holdings breakdown by value

## 🎯 **Who Benefits & How to Use This Project**

### **📚 For Students & Learners**
- **Learn stock market basics** without financial risk
- **Practice trading strategies** with real market data
- **Understand financial concepts**: P&L, portfolio diversification, market volatility
- **Try before you invest**: Test different investment approaches safely

**Try This**: Start with $50K virtual cash, buy popular stocks like AAPL, TSLA, MSFT and watch how your portfolio changes with real market movements!

### **💼 For Job Seekers (FinTech/Banking)**
- **Demonstrate technical skills** in financial software development
- **Show understanding** of trading systems architecture
- **Portfolio project** that stands out in interviews
- **Real-world application** that hiring managers can relate to

**Key Features to Highlight**: 
- Real-time data integration
- Transaction integrity with ACID compliance
- JWT security implementation
- Microservices architecture
- Financial data modeling

### **👨‍💻 For Developers**
- **Learn Spring Boot** with a practical, engaging project
- **Study financial APIs** and data integration patterns
- **Practice database design** for financial applications
- **Explore security patterns** for sensitive financial data
- **Understand transaction management** in distributed systems

**Technical Learning Points**:
- External API integration (Alpha Vantage)
- JWT authentication and authorization
- Database transactions for financial operations
- RESTful API design best practices
- Spring Security configuration

### **🏦 For Finance Professionals** 
- **Understand the technology** behind trading platforms
- **Test trading strategies** safely before real implementation
- **Learn about market data feeds** and how they work
- **Explore different order types** and execution strategies

**Use Cases**:
- Algorithm testing with real data
- Risk management strategy validation
- Portfolio optimization experiments
- Market analysis and backtesting

## 💡 **Real-World Applications**

This simulator demonstrates technology patterns used in:
- **Robinhood** - Commission-free trading platforms
- **E*TRADE** - Online brokerage systems  
- **Bloomberg Terminal** - Financial data systems
- **Interactive Brokers** - Professional trading platforms
- **Fidelity/Schwab** - Traditional brokerage backends

## 🎯 Project Highlights for FinTech Interviews

This project demonstrates key concepts relevant to financial technology:

1. **Financial Data Modeling** - Proper handling of money, prices, and quantities
2. **Transaction Integrity** - ACID compliance for financial operations
3. **Risk Management** - Validation to prevent invalid trades
4. **Performance Analytics** - Real-time portfolio valuation
5. **Audit Trail** - Complete transaction history
6. **API Design** - RESTful services for financial operations
7. **Security** - JWT authentication and authorization
8. **Scalability** - Service-oriented architecture
9. **Data Integration** - External API consumption
10. **Testing** - Unit and integration test patterns

## 🔧 Configuration

### Environment Variables
```bash
# Required
ALPHA_VANTAGE_API_KEY=your_api_key

# Optional (with defaults)
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
POSTGRES_URL=jdbc:postgresql://localhost:5432/stocktrade
POSTGRES_USER=stocktrade_user
POSTGRES_PASSWORD=stocktrade_pass
```

### Default Users
- **Admin**: username: `admin`, password: `admin123`
- **Demo User**: username: `demouser`, password: `user123`

## 📈 Future Enhancements

- WebSocket integration for real-time price updates
- Advanced order types (limit orders, stop-loss)
- Options trading simulation
- Portfolio optimization recommendations
- Social trading features
- Mobile app development
- Cryptocurrency support

## 🤝 Contributing

This is a demonstration project, but contributions are welcome! Please feel free to:
- Report bugs
- Suggest features
- Submit pull requests
- Improve documentation

## 📄 License

This project is created for educational and demonstration purposes.

---

**Ready to start trading?** 📊 Get your API key from Alpha Vantage and launch your trading simulation today!
