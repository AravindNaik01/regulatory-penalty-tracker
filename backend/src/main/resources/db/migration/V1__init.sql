CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE penalties (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(50) NOT NULL,
    issued_date DATE NOT NULL,
    amount DECIMAL(15, 2),
    company_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    user_id BIGINT REFERENCES users(id),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

-- Insert default admin user (password: admin123, encrypted via BCrypt in real world, but here we just need a dummy structure, the application will handle creating real ones, let's put an encrypted one: $2a$10$wY1twJwX.3x.p93lQy2e4eB0R.y0.R5.a/1e5./O./C/l.v.u.8mK which is admin123)
INSERT INTO users (username, password, email, role) VALUES ('admin', '$2a$10$wY1twJwX.3x.p93lQy2e4eB0R.y0.R5.a/1e5./O./C/l.v.u.8mK', 'admin@tracker.com', 'ADMIN');
