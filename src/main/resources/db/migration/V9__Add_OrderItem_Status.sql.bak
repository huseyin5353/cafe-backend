-- OrderItem tablosuna status, prepared_time ve delivered_time alanlarını ekle
ALTER TABLE restaurant_order_items ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE restaurant_order_items ADD COLUMN prepared_time TIMESTAMP;
ALTER TABLE restaurant_order_items ADD COLUMN delivered_time TIMESTAMP;

-- Mevcut OrderItem'ların status'unu PENDING olarak ayarla
UPDATE restaurant_order_items SET status = 'PENDING' WHERE status IS NULL;
