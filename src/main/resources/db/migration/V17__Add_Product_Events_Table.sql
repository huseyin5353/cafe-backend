-- Basit product events tablosu - sadece görüntülenme ve sepete ekleme
CREATE TABLE product_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  event_type VARCHAR(20) NOT NULL,
  event_date DATE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for performance
CREATE INDEX idx_product_date ON product_events(product_id, event_date);
