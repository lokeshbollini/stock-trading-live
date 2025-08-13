# Stock Trading Simulator

A comprehensive stock trading simulation platform built with **Java Spring Boot** that allows users to trade stocks using real-time market data without risking real money. Perfect for learning about financial markets, practicing trading strategies, and demonstrating software engineering skills for FinTech roles.

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

## 🚀 Quick Start

### 1. Clone and Setup
```bash
git clone <repository-url>
cd stock-trading-simulator
```

### 2. Database Setup
```bash
# Create PostgreSQL database
createdb stocktrade

# Or use the provided Docker setup
docker run --name postgres-stocktrade \
  -e POSTGRES_DB=stocktrade \
  -e POSTGRES_USER=stocktrade_user \
  -e POSTGRES_PASSWORD=stocktrade_pass \
  -p 5432:5432 -d postgres:13
```

### 3. Environment Configuration
```bash
# Set environment variables
export ALPHA_VANTAGE_API_KEY=your_api_key_here
export JWT_SECRET=your-secret-key-min-32-chars-long

# Or create application-local.yml with your settings
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

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

## 🧪 Sample API Calls

### Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "trader123",
    "email": "trader@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Execute a Buy Order
```bash
curl -X POST http://localhost:8080/api/trades/buy \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "stockSymbol": "AAPL",
    "quantity": 10,
    "price": 150.00
  }'
```

### Get Portfolio Summary
```bash
curl -X GET http://localhost:8080/api/portfolio/summary \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

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
