# E-Commerce Management System

A full-stack e-commerce administration system built with React, Spring Boot, Spring Security, JWT, and MySQL.

## Features

- Admin dashboard with sales overview and order analytics
- Product, category, order, customer, and inventory management
- JWT-based authentication with role-based access control
- MySQL-backed relational data model
- Responsive React frontend matching an admin dashboard design reference
- REST API with validation, error handling, and transactional order processing

## Tech Stack

- Frontend: React + Vite + JavaScript + Axios + Recharts
- Backend: Java 25 + Spring Boot 3.3 + Spring Security + JPA/Hibernate
- Database: MySQL 8+

## Project Structure

- backend/
- frontend/

## Database Setup

1. Create MySQL database:
   ```sql
   CREATE DATABASE ecommerce_management;
   ```
2. Update environment variables before running backend:
   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD=your_password
   export JWT_SECRET=your-long-secret-key
   export CORS_ALLOWED_ORIGINS=http://localhost:5173
   ```

## Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

## Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

## Default Login Credentials

- Admin: admin@shopkart.com / Admin@123
- HR: hr@shopkart.com / Hr@123

## API Documentation

Base URL: http://localhost:8080/api

## Common Errors and Solutions

- MySQL connection issues: verify username/password and ensure the database exists.
- JWT errors: ensure `JWT_SECRET` is set before startup.
- CORS errors: set `CORS_ALLOWED_ORIGINS` to include the frontend URL.
- Frontend not loading data: confirm backend is running and API base URL matches the frontend config.

## Testing Instructions

- Run backend build: `cd backend && mvn clean install`
- Run frontend build: `cd frontend && npm install && npm run build`
