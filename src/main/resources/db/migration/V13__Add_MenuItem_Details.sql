-- MenuItem tablosuna detay alanları ekleme

-- Besin değerleri
ALTER TABLE menu_item ADD COLUMN calories INT;
ALTER TABLE menu_item ADD COLUMN protein DECIMAL(5,2);
ALTER TABLE menu_item ADD COLUMN carbs DECIMAL(5,2);
ALTER TABLE menu_item ADD COLUMN fat DECIMAL(5,2);

-- Ürün özellikleri
ALTER TABLE menu_item ADD COLUMN preparation_time INT;
ALTER TABLE menu_item ADD COLUMN spice_level INT;
ALTER TABLE menu_item ADD COLUMN is_vegetarian BOOLEAN;
ALTER TABLE menu_item ADD COLUMN is_vegan BOOLEAN;
ALTER TABLE menu_item ADD COLUMN is_gluten_free BOOLEAN;

-- İçerik bilgileri
ALTER TABLE menu_item ADD COLUMN ingredients TEXT;
ALTER TABLE menu_item ADD COLUMN allergens TEXT;

-- Varsayılan değerler
UPDATE menu_item SET 
    calories = 350,
    protein = 25.0,
    carbs = 30.0,
    fat = 15.0,
    preparation_time = 20,
    spice_level = 2,
    is_vegetarian = false,
    is_vegan = false,
    is_gluten_free = false,
    ingredients = 'Taze malzemelerle hazırlanır',
    allergens = 'Bilgi yok'
WHERE calories IS NULL;

