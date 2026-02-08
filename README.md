# Warehouse Management System

A simple RESTful API for a shop warehouse management system built with Spring Boot and MySQL.

## Requirements

-   **Java 17** (Ensure `JAVA_HOME` is set correctly)
-   **Maven** (3.6+)
-   **MySQL** (Running on port 3306)

## Setup & Installation

### 1. Database Setup
The application requires a MySQL database named `warehouse`. A single script is provided to set up the database, tables, and sample data.

1.  Open your terminal.
2.  Log in to MySQL (or use your preferred client like DBeaver):
    ```bash
    mysql -u root -p
    ```
3.  Run the setup script:
    ```bash
    source full_setup.sql;
    ```
    *Alternatively, run directly from the command line:*
    ```bash
    mysql -u root -p < full_setup.sql
    ```

**Note:** The default configuration assumes:
-   Host: `localhost`
-   Port: `3306`
-   User: `root`
-   Password: `root`

If your configuration differs, update `src/main/resources/application.properties`.

### 2. Run the Application
1.  Navigate to the project directory:
    ```bash
    cd Warehouse-Managemet-Java
    ```
2.  Run with Maven:
    ```bash
    mvn spring-boot:run
    ```
    *If you encounter a Java version error, explicitly set JAVA_HOME:*
    ```bash
    export JAVA_HOME="/path/to/java17"
    mvn spring-boot:run
    ```

The API will start at `http://localhost:8080`.

## API Endpoints

### Items
-   **GET /api/items** - List all items
-   **POST /api/items** - Create new item
    ```json
    { "name": "Laptop", "description": "Gaming Laptop" }
    ```
-   **GET /api/items/{id}** - Get item details

### Variants
-   **POST /api/items/{itemId}/variants** - Add variant
    ```json
    { "name": "16GB RAM", "price": 1200.00, "stockQuantity": 10 }
    ```
-   **PATCH /api/variants/{variantId}/stock** - Update stock
    ```json
    50
    ```

### Orders
-   **POST /api/orders** - Sell item (Decreases stock)
    ```json
    { "variantId": 1, "quantity": 1 }
    ```

## Project Structure
-   `src/main/java`: Source code
-   `src/main/resources`: Configuration (`application.properties`)
-   `full_setup.sql`: Database initialization script
