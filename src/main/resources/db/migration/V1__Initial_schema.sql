-- Initial database schema for Stock Trading Simulator

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    cash_balance DECIMAL(19,2) NOT NULL DEFAULT 10000.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

-- Stocks table
CREATE TABLE stocks (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(10) UNIQUE NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    current_price DECIMAL(19,2) NOT NULL,
    previous_close DECIMAL(19,2),
    day_high DECIMAL(19,2),
    day_low DECIMAL(19,2),
    volume BIGINT,
    market_cap BIGINT,
    pe_ratio DECIMAL(10,2),
    dividend_yield DECIMAL(5,2),
    fifty_two_week_high DECIMAL(19,2),
    fifty_two_week_low DECIMAL(19,2),
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Portfolios table (user stock holdings)
CREATE TABLE portfolios (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    average_cost DECIMAL(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_portfolio_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_portfolio_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_stock UNIQUE (user_id, stock_id)
);

-- Trades table (transaction history)
CREATE TABLE trades (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    trade_type VARCHAR(4) NOT NULL CHECK (trade_type IN ('BUY', 'SELL')),
    quantity INTEGER NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    commission DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    trade_status VARCHAR(10) NOT NULL DEFAULT 'COMPLETED' CHECK (trade_status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'FAILED')),
    executed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    CONSTRAINT fk_trade_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_trade_stock FOREIGN KEY (stock_id) REFERENCES stocks(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);

CREATE INDEX idx_stocks_symbol ON stocks(symbol);
CREATE INDEX idx_stocks_active ON stocks(is_active);
CREATE INDEX idx_stocks_last_updated ON stocks(last_updated);

CREATE INDEX idx_portfolios_user ON portfolios(user_id);
CREATE INDEX idx_portfolios_stock ON portfolios(stock_id);
CREATE INDEX idx_portfolios_user_stock ON portfolios(user_id, stock_id);

CREATE INDEX idx_trades_user ON trades(user_id);
CREATE INDEX idx_trades_stock ON trades(stock_id);
CREATE INDEX idx_trades_executed_at ON trades(executed_at);
CREATE INDEX idx_trades_user_executed ON trades(user_id, executed_at);
CREATE INDEX idx_trades_trade_type ON trades(trade_type);
CREATE INDEX idx_trades_status ON trades(trade_status);

-- Insert some initial sample stocks
INSERT INTO stocks (symbol, company_name, current_price) VALUES
('AAPL', 'Apple Inc.', 150.00),
('GOOGL', 'Alphabet Inc.', 2500.00),
('MSFT', 'Microsoft Corporation', 300.00),
('AMZN', 'Amazon.com Inc.', 3000.00),
('TSLA', 'Tesla Inc.', 800.00),
('META', 'Meta Platforms Inc.', 250.00),
('NVDA', 'NVIDIA Corporation', 400.00),
('NFLX', 'Netflix Inc.', 400.00),
('DIS', 'The Walt Disney Company', 100.00),
('PYPL', 'PayPal Holdings Inc.', 200.00);

-- Create a sample admin user (password: admin123)
-- Note: In production, this should be done securely
INSERT INTO users (username, email, password, first_name, last_name, cash_balance, role) VALUES
('admin', 'admin@stocktrade.com', '$2a$10$oE39pJlaMF6sbc.7QQ8Ys.5.wVh6p5KQsyQf2QQc6pU6jN8YWK2GS', 'Admin', 'User', 50000.00, 'ADMIN');

-- Create a sample regular user (password: user123)
INSERT INTO users (username, email, password, first_name, last_name, cash_balance, role) VALUES
('demouser', 'demo@stocktrade.com', '$2a$10$M9.rqSSLKGnhCp1jNe.z5OOgGPvOuNhFW6GW.rOdNpzKbgOQl.PWG', 'Demo', 'User', 25000.00, 'USER');
