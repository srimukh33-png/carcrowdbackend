-- CarCrowd MySQL Database Schema DDL
-- Database: carcrowd_db

CREATE DATABASE IF NOT EXISTS carcrowd_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE carcrowd_db;

-- 1. Brands Table
CREATE TABLE IF NOT EXISTS car_brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Cars Table
CREATE TABLE IF NOT EXISTS cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    body_type VARCHAR(50),
    description TEXT,
    fuel_type VARCHAR(100),
    transmission VARCHAR(100),
    seating VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cars_brand FOREIGN KEY (brand_id) REFERENCES car_brands(id) ON DELETE CASCADE,
    INDEX idx_cars_brand_id (brand_id),
    INDEX idx_cars_model_name (model_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Interest Registrations Table
CREATE TABLE IF NOT EXISTS interest_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL,
    age INT NOT NULL,
    city VARCHAR(100) NOT NULL,
    purchase_timeline VARCHAR(50),
    consent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interests_car FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE,
    CONSTRAINT uk_car_phone UNIQUE (car_id, phone_number),
    CONSTRAINT uk_car_email UNIQUE (car_id, email),
    INDEX idx_reg_car_id (car_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
