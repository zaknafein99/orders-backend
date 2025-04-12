# Orders Management System

## Introduction

This project is a full-stack application for managing customer orders. It consists of a Kotlin/Spring Boot backend providing a REST API and a Vue.js frontend for user interaction. The system allows users to manage customers, items, trucks, and orders, including tracking order status and assigning orders to trucks.

## Architecture Overview

The system follows a client-server architecture with a separate frontend and backend application.

```mermaid
graph LR
    A[User Browser] -- HTTPS --> B(Vue.js Frontend);
    B -- REST API (JSON/HTTP) --> C(Kotlin/Spring Backend);
    C -- JPA --> D[(Database)];
    C -- JWT Auth --> C;

    subgraph Frontend
        B
        direction LR
        B1[Views]
        B2[Components]
        B3[Router]
        B4[Services]
        B5[Store]
        B1 --> B2
        B3 --> B1
        B1 --> B4
        B2 --> B4
        B4 --> B5
    end

    subgraph Backend
        C
        direction LR
        C1[Controllers]
        C2[Services]
        C3[Repositories]
        C4[Entities]
        C5[Mappers]
        C6[Config/Security]
        C1 --> C2
        C2 --> C3
        C3 --> C4
        C2 --> C5
        C1 --> C6
    end
```

*   **Frontend (Vue.js):** A single-page application (SPA) built with Vue 3 and Vite. It handles user interface rendering, user input, and communication with the backend API.
*   **Backend (Kotlin/Spring Boot):** A RESTful API service built with Kotlin and Spring Boot. It manages business logic, data persistence, and authentication.
*   **Interaction:** The frontend communicates with the backend via a REST API using JSON over HTTP(S).

## Backend Architecture (Kotlin/Spring Boot)

The backend follows a layered architecture pattern, primarily Controller-Service-Repository.

*   **`controller`:** Defines REST API endpoints using Spring Web annotations (`@RestController`, `@GetMapping`, etc.). Handles incoming HTTP requests, delegates processing to the service layer, and returns HTTP responses. Includes controllers for `Auth`, `Customer`, `Dashboard`, `Item`, `Order`, `Truck`, and `User`.
*   **`service`:** Contains the core business logic. Services orchestrate operations, interact with repositories, and perform data transformations.
*   **`repository`:** Interfaces extending Spring Data JPA's `JpaRepository`. Provides methods for database operations (CRUD, custom queries) on entities.
*   **`entity`:** Defines the data model using JPA annotations (`@Entity`). These classes map to database tables.
*   **`dto`:** Data Transfer Objects used for transferring data between layers, especially between the controller and the client. Helps decouple the API representation from the internal database model.
*   **`mapper`:** Uses MapStruct (`@Mapper`) to define mappings between Entity objects and DTOs, reducing boilerplate code.
*   **`config`:** Contains configuration classes, including security setup (`SecurityConfiguration` using Spring Security) and JWT handling (`JwtAuthenticationFilter`, `JwtProperties`).
*   **Authentication:** Implemented using JSON Web Tokens (JWT). The `AuthenticationService` handles token generation, and `JwtAuthenticationFilter` validates tokens on incoming requests.

## Frontend Architecture (Vue.js)

The frontend is built using modern web technologies.

*   **UI Framework:** Vue 3 (Composition API likely used within components).
*   **Build Tool:** Vite provides fast development server startup and optimized builds.
*   **Routing:** `vue-router` manages navigation between different views/pages within the SPA (e.g., `/login`, `/dashboard`, `/orders`). Includes navigation guards for authentication checks.
*   **State Management:** Uses Pinia or Vuex (a `stores/customer.js` file exists, suggesting Pinia or a simple store pattern). Manages shared application state.
*   **API Communication:** `axios` (configured in `services/api.js`) is used to make HTTP requests to the backend API. Specific services (`AuthService`, `OrderService`, etc.) encapsulate API calls for different resources.
*   **Styling:** Tailwind CSS is used for utility-first CSS styling.
*   **Key Directories:**
    *   `src/views`: Top-level page components corresponding to routes.
    *   `src/components`: Reusable UI elements used across different views.
    *   `src/services`: Modules for interacting with the backend API.
    *   `src/router`: Defines application routes and navigation guards.
    *   `src/stores`: State management modules.
    *   `src/assets`: Static assets like images and global styles.

## Database Schema

The application uses a relational database accessed via JPA/Hibernate ORM. Key entities include:

*   **`Customer`:** Represents customer information (name, phone, address, etc.).
*   **`Item`:** Represents products or items that can be ordered (name, price, etc.).
*   **`Order`:** Represents a customer order, linking to a `Customer`, potentially a `Truck`, and containing multiple `OrderItem`s. Includes status (`PENDING`, `DELIVERED`, etc.) and timestamps.
*   **`OrderItem`:** Represents a line item within an `Order`, linking to an `Item` and specifying quantity. Uses `@Embeddable` key for composite primary key.
*   **`Truck`:** Represents delivery trucks (number plate, capacity).
*   **`User`:** Represents application users for authentication (username, password, roles).

**Key Relationships:**

*   `Order` to `Customer`: Many-to-One
*   `Order` to `Truck`: Many-to-One (nullable)
*   `Order` to `OrderItem`: One-to-Many (Order contains multiple OrderItems)
*   `OrderItem` to `Item`: Many-to-One (OrderItem refers to one Item)

## API Endpoint Documentation

This section outlines some of the main API endpoints provided by the backend. Authentication (JWT Bearer Token) is required for most endpoints unless specified otherwise.

**Authentication (`/api/auth`)**

*   `POST /api/auth/authenticate`: Authenticate user and receive JWT tokens.
    *   Auth Required: No
    *   Request Body: `AuthenticationRequest` (`email`, `password`)
    *   Response: `AuthenticationResponse` (`accessToken`, `refreshToken`)
*   `POST /api/auth/refresh`: Refresh an expired access token using a refresh token.
    *   Auth Required: No (Requires valid Refresh Token in body)
    *   Request Body: `RefreshTokenRequest` (`token`)
    *   Response: `TokenResponse` (`accessToken`)

**Customers (`/api/customers`)**

*   `POST /api/customers`: Create a new customer.
    *   Auth Required: Yes
    *   Request Body: `CustomerDTO`
    *   Response: `CustomerDTO`
*   `GET /api/customers`: Get a paginated list of customers. Supports searching by phone number.
    *   Auth Required: Yes
    *   Query Params: `page`, `size`, `phoneNumber` (optional)
    *   Response: `Page<CustomerDTO>`
*   `PUT /api/customers/{customerId}`: Update an existing customer.
    *   Auth Required: Yes
    *   Request Body: `CustomerDTO`
    *   Response: `CustomerDTO`
*   `DELETE /api/customers/{customerId}`: Delete a customer.
    *   Auth Required: Yes
    *   Response: `204 No Content`

**Orders (`/api/orders`)**

*   `POST /api/orders`: Create a new order.
    *   Auth Required: Yes
    *   Request Body: `OrderDTO` (includes customer details, items, quantities)
    *   Response: `OrderDTO`
*   `GET /api/orders`: Get a list of all orders (potentially paginated).
    *   Auth Required: Yes
    *   Response: `List<OrderDTO>` or `Page<OrderDTO>`
*   `GET /api/orders/pending`: Get a list of pending orders.
    *   Auth Required: Yes
    *   Response: `List<OrderDTO>`
*   `POST /api/orders/{orderId}/deliver`: Mark an order as delivered.
    *   Auth Required: Yes
    *   Response: `OrderDTO`
*   `PUT /api/orders/{orderId}/status`: Update the status of an order.
    *   Auth Required: Yes
    *   Request Body: `Map<String, String>` (e.g., `{"status": "DELIVERED"}`)
    *   Response: `OrderDTO`
*   `POST /api/orders/{orderId}/truck`: Assign a truck to an order.
    *   Auth Required: Yes
    *   Request Body: `Map<String, Int>` (e.g., `{"truckId": 1}`)
    *   Response: `OrderDTO`
*   `DELETE /api/orders/{orderId}`: Delete an order.
    *   Auth Required: Yes
    *   Response: `204 No Content`

**Items (`/api/items`)**

*   `POST /api/items`: Create a new item.
    *   Auth Required: Yes
    *   Request Body: `ItemDTO`
    *   Response: `ItemDTO`
*   `GET /api/items/list`: Get a list of all items (potentially paginated).
    *   Auth Required: Yes
    *   Response: `List<ItemDTO>` or `Page<ItemDTO>`

**Trucks (`/api/trucks`)**

*   `POST /api/trucks`: Create a new truck.
    *   Auth Required: Yes
    *   Request Body: `TruckDTO`
    *   Response: `TruckDTO`
*   `GET /api/trucks`: Get a list of all trucks.
    *   Auth Required: Yes
    *   Response: `List<TruckDTO>`

**Dashboard (`/api/dashboard`)**

*   `GET /api/dashboard/truck-sales/daily`: Get daily truck sales data.
    *   Auth Required: Yes
    *   Query Params: `date`
    *   Response: `Map<String, Any>`
*   `GET /api/dashboard/statistics`: Get general dashboard statistics.
    *   Auth Required: Yes
    *   Response: `Map<String, Any>`

## Technologies Used

**Backend:**

*   Kotlin
*   Spring Boot
*   Spring Web
*   Spring Data JPA
*   Spring Security
*   Hibernate
*   PostgreSQL (or other relational DB configured)
*   MapStruct
*   JJwt (for JWT handling)
*   Gradle

**Frontend:**

*   Vue.js 3
*   Vite
*   JavaScript
*   HTML5 / CSS3
*   Tailwind CSS
*   Vue Router
*   Axios
*   Pinia (likely, based on store structure)
*   Node.js / npm (for development environment)
