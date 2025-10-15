-- Öne çıkan ürünler tablosu
CREATE TABLE featured_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Index'ler
CREATE INDEX idx_featured_products_active ON featured_products(is_active);
CREATE INDEX idx_featured_products_display_order ON featured_products(display_order);
CREATE INDEX idx_featured_products_active_order ON featured_products(is_active, display_order);

-- Örnek veriler
INSERT INTO featured_products (name, description, price, image_url, display_order, is_active) VALUES
('Adana Kebap', 'Adanalı Yavuz''un favorisi', 400.00, '/images/adana-kebap.jpg', 1, TRUE),
('Lahmacun', 'İnce hamur üzerine kıyma ve baharat', 150.00, '/images/lahmacun.jpg', 2, TRUE),
('Çiğ Köfte', 'Acılı ve lezzetli çiğ köfte', 200.00, '/images/cig-kofte.jpg', 3, TRUE),
('Ayran', 'Serinletici ayran', 50.00, '/images/ayran.jpg', 4, TRUE);



