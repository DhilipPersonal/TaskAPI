-- Create task_attachments table for file attachments
CREATE TABLE task_attachments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(500),
    
    CONSTRAINT fk_task_attachments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_attachments_user FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_task_attachments_task_id ON task_attachments(task_id);
CREATE INDEX idx_task_attachments_uploaded_by ON task_attachments(uploaded_by);
CREATE INDEX idx_task_attachments_uploaded_at ON task_attachments(uploaded_at);

-- Add comments
COMMENT ON TABLE task_attachments IS 'Stores file attachments for tasks';
COMMENT ON COLUMN task_attachments.file_name IS 'Generated unique file name';
COMMENT ON COLUMN task_attachments.original_file_name IS 'Original file name as uploaded';
COMMENT ON COLUMN task_attachments.file_path IS 'Relative path to the stored file';
COMMENT ON COLUMN task_attachments.content_type IS 'MIME type of the file';
COMMENT ON COLUMN task_attachments.file_size IS 'File size in bytes';
