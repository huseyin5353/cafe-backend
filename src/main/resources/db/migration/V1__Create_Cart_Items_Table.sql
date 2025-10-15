-- Cart Items tablosu oluştur
DROP TABLE IF EXISTS cart_items CASCADE;

CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    menu_item_id BIGINT NOT NULL,
    menu_item_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Unique constraint: bir session'da bir participant'ın aynı menu item'ı sadece bir kez ekleyebilir
    CONSTRAINT uk_cart_items_session_participant_menu UNIQUE (session_id, participant_id, menu_item_id)
);

-- Index'ler
CREATE INDEX idx_cart_items_session_id ON cart_items(session_id);
CREATE INDEX idx_cart_items_participant_id ON cart_items(participant_id);
CREATE INDEX idx_cart_items_customer_name ON cart_items(customer_name);
CREATE INDEX idx_cart_items_menu_item_id ON cart_items(menu_item_id);
