-- Performance Indexes for 3000 concurrent users
-- Created carefully to avoid breaking existing functionality

-- ========== ORDERS TABLE INDEXES ==========
-- Most critical: session_id (used in 80% of queries)
CREATE INDEX IF NOT EXISTS idx_orders_session_id ON restaurant_orders(session_id);

-- Status filtering (kitchen, waiter queries)
CREATE INDEX IF NOT EXISTS idx_orders_status ON restaurant_orders(status);

-- Order time sorting (recent orders, analytics)
CREATE INDEX IF NOT EXISTS idx_orders_order_time ON restaurant_orders(order_time);

-- Table number queries (waiter dashboard)
CREATE INDEX IF NOT EXISTS idx_orders_table_number ON restaurant_orders(table_number);

-- Composite index for common query pattern: session + status
CREATE INDEX IF NOT EXISTS idx_orders_session_status ON restaurant_orders(session_id, status);

-- Composite index for time-based queries: status + order_time
CREATE INDEX IF NOT EXISTS idx_orders_status_time ON restaurant_orders(status, order_time);

-- Customer name queries (customer history)
CREATE INDEX IF NOT EXISTS idx_orders_customer_name ON restaurant_orders(customer_name);

-- ========== ORDER_ITEMS TABLE INDEXES ==========
-- Most critical: order_id (foreign key, used in joins)
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON restaurant_order_items(order_id);

-- Menu item filtering (department queries)
CREATE INDEX IF NOT EXISTS idx_order_items_menu_item_id ON restaurant_order_items(menu_item_id);

-- Status filtering (kitchen operations)
CREATE INDEX IF NOT EXISTS idx_order_items_status ON restaurant_order_items(status);

-- Composite index for department queries: menu_item_id + status
CREATE INDEX IF NOT EXISTS idx_order_items_menu_status ON restaurant_order_items(menu_item_id, status);

-- ========== CART_ITEMS TABLE INDEXES ==========
-- Session-based cart queries
CREATE INDEX IF NOT EXISTS idx_cart_items_session_id ON cart_items(session_id);

-- Participant-based queries
CREATE INDEX IF NOT EXISTS idx_cart_items_participant_id ON cart_items(participant_id);

-- Menu item filtering
CREATE INDEX IF NOT EXISTS idx_cart_items_menu_item_id ON cart_items(menu_item_id);

-- Composite index for unique constraint optimization
CREATE INDEX IF NOT EXISTS idx_cart_items_session_participant_menu ON cart_items(session_id, participant_id, menu_item_id);

-- ========== PRODUCT_EVENTS TABLE INDEXES ==========
-- Analytics queries by product
CREATE INDEX IF NOT EXISTS idx_product_events_product_id ON product_events(product_id);

-- Date-based analytics queries
CREATE INDEX IF NOT EXISTS idx_product_events_event_date ON product_events(event_date);

-- Event type filtering
CREATE INDEX IF NOT EXISTS idx_product_events_event_type ON product_events(event_type);

-- Composite index for analytics: product + date
CREATE INDEX IF NOT EXISTS idx_product_events_product_date ON product_events(product_id, event_date);

-- Composite index for analytics: product + event_type + date
CREATE INDEX IF NOT EXISTS idx_product_events_product_type_date ON product_events(product_id, event_type, event_date);

-- ========== PRODUCT_STATS TABLE INDEXES ==========
-- Analytics queries by product
CREATE INDEX IF NOT EXISTS idx_product_stats_product_id ON product_stats(product_id);

-- Date-based analytics queries
CREATE INDEX IF NOT EXISTS idx_product_stats_date ON product_stats(date);

-- Composite index for analytics: product + date (unique constraint)
CREATE INDEX IF NOT EXISTS idx_product_stats_product_date ON product_stats(product_id, date);

-- ========== SESSION TABLES INDEXES ==========
-- Table session queries
CREATE INDEX IF NOT EXISTS idx_table_session_table_id ON table_session(table_id);

-- Session participant queries
CREATE INDEX IF NOT EXISTS idx_session_participants_session_id ON session_participants(session_id);

-- ========== MENU TABLES INDEXES ==========
-- Department filtering
CREATE INDEX IF NOT EXISTS idx_menu_items_department_id ON menu_item(department_id);

-- Active menu items
CREATE INDEX IF NOT EXISTS idx_menu_items_available ON menu_item(available);

-- Category filtering
CREATE INDEX IF NOT EXISTS idx_menu_items_category_id ON menu_item(category_id);

-- Composite index for active items by department
CREATE INDEX IF NOT EXISTS idx_menu_items_dept_available ON menu_item(department_id, available);

-- ========== NOTIFICATIONS TABLE INDEXES ==========
-- Status filtering
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);

-- Target role filtering
CREATE INDEX IF NOT EXISTS idx_notifications_target_role ON notifications(target_role);

-- Created time sorting
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

-- Composite index for unread notifications by role
CREATE INDEX IF NOT EXISTS idx_notifications_status_role ON notifications(status, target_role);
