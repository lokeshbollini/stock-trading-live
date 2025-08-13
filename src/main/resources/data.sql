-- Insert sample stocks for development
INSERT INTO stocks (symbol, company_name, current_price, previous_close, day_high, day_low, volume, is_active, created_at, last_updated) VALUES
('AAPL', 'Apple Inc.', 189.50, 188.25, 190.15, 187.80, 45123000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('GOOGL', 'Alphabet Inc.', 142.35, 141.90, 143.20, 140.50, 28456000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MSFT', 'Microsoft Corporation', 378.85, 377.20, 380.10, 375.50, 32145000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AMZN', 'Amazon.com Inc.', 153.75, 152.40, 155.20, 151.80, 38967000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TSLA', 'Tesla Inc.', 248.50, 245.80, 251.30, 244.10, 95123000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('META', 'Meta Platforms Inc.', 331.20, 329.50, 333.80, 327.90, 22456000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('NVDA', 'NVIDIA Corporation', 481.25, 478.90, 485.60, 476.30, 41789000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('NFLX', 'Netflix Inc.', 486.30, 484.70, 488.90, 482.15, 18234000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DIS', 'The Walt Disney Company', 91.85, 90.95, 92.50, 90.20, 25678000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PYPL', 'PayPal Holdings Inc.', 62.40, 61.85, 63.20, 61.30, 19456000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CRM', 'Salesforce Inc.', 278.90, 276.50, 281.20, 275.80, 14567000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORCL', 'Oracle Corporation', 115.75, 114.30, 116.90, 113.80, 16789000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('INTC', 'Intel Corporation', 43.85, 43.20, 44.50, 42.90, 35678000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AMD', 'Advanced Micro Devices Inc.', 106.40, 105.70, 107.80, 104.90, 28945000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('UBER', 'Uber Technologies Inc.', 71.25, 70.45, 72.10, 69.80, 21345000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create admin user (password: admin123)
INSERT INTO users (username, email, password, first_name, last_name, cash_balance, role, is_active, created_at) VALUES
('admin', 'admin@stocktrade.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'User', 100000.00, 'ADMIN', true, CURRENT_TIMESTAMP);

-- Create demo user (password: demo123)
INSERT INTO users (username, email, password, first_name, last_name, cash_balance, role, is_active, created_at) VALUES
('demo', 'demo@stocktrade.com', '$2a$10$DowJonesVSP500FTSEDAXn.Ye4oKoEa3Ro9llC/.og/at2uheWG/demo.', 'Demo', 'Trader', 50000.00, 'USER', true, CURRENT_TIMESTAMP);

-- Create sample trades for demo user
INSERT INTO trades (user_id, stock_id, trade_type, quantity, price, total_amount, trade_status, executed_at, created_at) VALUES
(2, 1, 'BUY', 10, 185.50, 1855.00, 'EXECUTED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 3, 'BUY', 5, 375.00, 1875.00, 'EXECUTED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 5, 'BUY', 8, 245.00, 1960.00, 'EXECUTED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 'SELL', 3, 189.00, 567.00, 'EXECUTED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create sample portfolio holdings for demo user
INSERT INTO portfolios (user_id, stock_id, quantity, average_cost, created_at) VALUES
(2, 1, 7, 185.50, CURRENT_TIMESTAMP),  -- AAPL: 7 shares at avg cost $185.50
(2, 3, 5, 375.00, CURRENT_TIMESTAMP),  -- MSFT: 5 shares at avg cost $375.00  
(2, 5, 8, 245.00, CURRENT_TIMESTAMP);  -- TSLA: 8 shares at avg cost $245.00
