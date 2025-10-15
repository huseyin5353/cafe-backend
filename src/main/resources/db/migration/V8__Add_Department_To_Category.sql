-- Add department_id column to category table
ALTER TABLE category ADD COLUMN department_id BIGINT;

-- Add foreign key constraint
ALTER TABLE category ADD CONSTRAINT fk_category_department 
    FOREIGN KEY (department_id) REFERENCES departments(id);

-- Add index for better performance
CREATE INDEX idx_category_department_id ON category(department_id);

-- Optional: Update existing categories with default department
-- UPDATE category SET department_id = 1 WHERE department_id IS NULL;



