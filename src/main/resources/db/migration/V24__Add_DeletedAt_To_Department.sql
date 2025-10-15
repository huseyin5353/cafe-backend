-- Add deleted_at column to departments table for soft delete functionality

ALTER TABLE departments 
ADD COLUMN deleted_at TIMESTAMP;

-- Add index on deleted_at for better query performance
CREATE INDEX idx_departments_deleted_at ON departments(deleted_at);

-- Add comment
COMMENT ON COLUMN departments.deleted_at IS 'Timestamp when department was soft deleted (archived)';




