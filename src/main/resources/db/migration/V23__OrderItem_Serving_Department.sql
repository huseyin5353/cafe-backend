-- Add serving department fields to restaurant_order_items
ALTER TABLE restaurant_order_items
    ADD COLUMN IF NOT EXISTS serving_department_id BIGINT,
    ADD COLUMN IF NOT EXISTS serving_department_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS source_department_id BIGINT,
    ADD COLUMN IF NOT EXISTS department_overridden BOOLEAN,
    ADD COLUMN IF NOT EXISTS department_changed_by VARCHAR(100),
    ADD COLUMN IF NOT EXISTS department_changed_at TIMESTAMP;

-- Indexes for reporting
CREATE INDEX IF NOT EXISTS idx_order_items_serving_department_id ON restaurant_order_items (serving_department_id);
CREATE INDEX IF NOT EXISTS idx_order_items_source_department_id ON restaurant_order_items (source_department_id);





