# Spring Marketplace API

REST API for a marketplace built with [Spring](https://spring.io/) using Java 21, PostgreSQL, Keycloak and Docker.

## Table of Contents
- [Marketplace API demo](#api-demo)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Project](#running-the-project)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [API Endpoints](#api-endpoints)
- [Authentication & Authorization](#authentication--authorization)
- [License](#license)

---

## API Demo

The following demo shows:
- application startup
- authentication flow
- protected endpoints
- basic CRUD operations

Since this project acts as a Resource Server, authentication is delegated to Keycloak. The flow consists of two steps: obtaining an access token from Keycloak and then accessing protected resources.

1. Authentication (Get Token via Keycloak)
Since the API has no login endpoints, you must request a token directly from the Identity Provider.

POST http://localhost:8180/realms/marketplace-realm/protocol/openid-connect/token (Content-Type: application/x-www-form-urlencoded)

Request Body:
client_id=marketplace-client
username=user
password=user
grant_type=password

Response:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

2. Protected Endpoint (Checkout Order)
Once you have the access_token, include it in the Authorization header to access business logic.

POST http://localhost:8080/orders/checkout

Headers:
Authorization: Bearer <YOUR_ACCESS_TOKEN>

Response (201 Created):
```json
{
  "id": "15",
  "status": "CREATED",
  "totalPrice": 2500.00,
  "createdAt": "2026-02-10T14:30:00",
  "items": [
    {
      "productId": 101,
      "productTitle": "Mechanical Keyboard",
      "quantity": 1,
      "priceAtPurchase": 2500.00
    }
  ]
}
```

3. Public Endpoint (Get Products)
Some endpoints might be public (depending on Security Config).

GET http://localhost:8080/products

Response:
```json
[
  {
    "id": 1,
    "title": "Smartphone",
    "price": 999.99,
    "quantity": 10
  },
  {
    "id": 2,
    "title": "Laptop",
    "price": 1499.99,
    "quantity": 5
  }
]
```

---

## Prerequisites

This project requires a running PostgreSQL database.
The application will not start without a database connection.

### Option 1: Run PostgreSQL with Docker (recommended)

```bash
docker-compose up -d
```

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/WhiteP8wder8/spring_marketplace_api.git
cd spring_marketplace_api
```
2. Install dependencies:
```bash
./mvnw clean install
```

3. Environment Setup:
Ensure your src/main/resources/application.properties matches the ports defined in docker-compose.yml (DB on 5433, Keycloak on 8180).

---

## Running the Project

1. Configure Keycloak
Import the `realm-export.json` file (located in `/keycloak` folder) via Keycloak Admin Console (`http://localhost:8180`) to automatically configure the Realm, Client, and Roles.

2. Start the Application
Run the application using Maven:
```bash
./mvnw spring-boot:run
```
The server will start on: http://localhost:8080

---

## Testing

### Unit tests

The project includes Unit tests for critical business logic (e.g., OrderService transaction handling, stock validation) using JUnit 6 and Mockito.
Run tests with:
```bash
./mvnw test
```

---
## Project Structure

```text
src/main/java/com/api/spring_marketplace_api/
├── config/          # Security & OpenAPI configuration
├── controller/      # REST Controllers (API Layer)
├── enums/           # Enums
├── exception/       # Global Exception Handler
├── model/           # JPA Entities & DTOs
├── repository/      # Spring Data JPA Repositories
├── service/         # Business Logic Layer
└── util/            # Untils For Admin And JWT
```

---

## Technologies Used

Language: Java 21

Framework: Spring Boot 4.0.2

Database: PostgreSQL 16 (Hibernate/JPA)

Security: Spring Security + Keycloak (OAuth2 / OIDC)

DevOps: Docker & Docker Compose

Testing: JUnit 6, Mockito

Documentation: SpringDoc OpenAPI (Swagger UI)

---

## API Endpoints

For detailed API documentation and testing, visit Swagger UI at: 👉 http://localhost:8080/swagger-ui/index.html#/

Main Resources

Authentication
| Method | Route                          | Description                  |
| ------ | ------------------------------ | ---------------------------- |
| POST   | /register                      | Create a new account         |
| POST   | /register/seller               | Become a seller              |

Products
| Method | Route                    | Description                     |
| ------ | ------------------------ | ------------------------------- |
| GET    | /products                | Get all products                |
| GET    | /products/{productId}    | Get product by ID               |
| POST   | /products                | Create new product              |
| PATCH  | /products/{productId}    | Update a product by ID          |
| DELETE | /products/{productId}    | Remove product                  |

Orders
| Method | Route                          | Description                  |
| ------ | ------------------------------ | ---------------------------- |
| GET    | /orders                        | Get your orders              |
| POST   | /orders/checkout               | Checkout cart                |

Cart
| Method | Route                    | Description                             |
| ------ | ------------------------ | --------------------------------------- |
| GET    | /cart                    | Get all products in cart                |
| POST   | /cart/items              | Add product to cart with quantity       |
| DELETE | /cart/items/{cartItemId} | Delete product from cart                |

---

## Authentication & Authorization

The API acts as a Resource Server.

Flow: Clients must obtain a JWT (Access Token) from Keycloak.

Protection: The token is validated by Spring Security.

Usage: Pass the token in the header: Authorization: Bearer <token>.

---

## License

MIT License






