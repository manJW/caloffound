CREATE TABLE IF NOT EXISTS foundation_calc_record (
    record_id VARCHAR(64) NOT NULL PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL,
    node_id VARCHAR(64) NOT NULL,
    foundation_type VARCHAR(64) NOT NULL,
    operation VARCHAR(64) NOT NULL,
    iteration_mode VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    input_values_json JSON NULL,
    result_items_json JSON NOT NULL,
    derived_items_json JSON NOT NULL,
    graphics_json JSON NOT NULL,
    export_json JSON NOT NULL,
    INDEX idx_foundation_calc_project_node (project_id, node_id, created_at),
    INDEX idx_foundation_calc_node (node_id, created_at)
);
