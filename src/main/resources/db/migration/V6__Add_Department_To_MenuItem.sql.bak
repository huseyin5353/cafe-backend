-- MenuItem tablosuna department_id kolonu ekle
ALTER TABLE menu_item ADD COLUMN department_id BIGINT;

-- Foreign key constraint ekle
ALTER TABLE menu_item ADD CONSTRAINT fk_menu_item_department 
    FOREIGN KEY (department_id) REFERENCES departments(id);

-- Index ekle
CREATE INDEX idx_menu_item_department ON menu_item (department_id);

-- Mevcut ürünleri varsayılan departmana ata (Mutfak - ID: 1)
UPDATE menu_item SET department_id = 1 WHERE department_id IS NULL;
