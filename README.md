# 🏗️ Tender Management API

This is a Spring Boot-based RESTful API for managing government tender bidding. It includes role-based access control using JWT for two main user roles: **BIDDER** and **APPROVER**.

## 🚀 Features

- **User Roles**: 
  - `BIDDER`: Can add and delete their own biddings.
  - `APPROVER`: Can update status and delete any bidding.
- **JWT Authentication**
- **Role-Based Access Control**
- **H2 In-Memory Database** (for testing/demo)

## 🧰 Technologies Used

- Java 11+
- Spring Boot
- Spring Security
- JWT
- H2 Database
- Maven

## 📦 API Endpoints

| Endpoint                  | Method | Access Role(s)     | Description                       |
|--------------------------|--------|---------------------|-----------------------------------|
| `/login`                 | POST   | All                 | Authenticate and get JWT         |
| `/bidding/add`           | POST   | BIDDER              | Add a new bidding                |
| `/bidding/update/{id}`   | PUT    | APPROVER            | Update bidding status            |
| `/bidding/list`          | GET    | BIDDER, APPROVER    | Get biddings with filters        |
| `/bidding/delete/{id}`   | DELETE | BIDDER (own), APPROVER | Delete bidding by access control |

## 🔐 Authentication

1. Use `/login` with valid credentials:
   ```json
   {
     "email": "bidder@example.com",
     "password": "password"
   }
   ```
2. Response:
   ```json
   {
     "jwt": "eyJhbGciOiJIUzI1NiIsIn..."
   }
   ```
3. Add `Authorization` header in further requests:
   ```
   Authorization: Bearer <JWT_TOKEN>
   ```

## ⚙️ Role Control in Code

- `BIDDER` role only allows access to `/bidding/add` and `/bidding/delete/{id}` (if it's their own).
- `APPROVER` role can access `/bidding/update/{id}` and delete any bidding.
- `/bidding/list` is accessible by both roles.

## 🧪 Running Tests

Run all tests using:

```bash
mvn test
```

Ensure that all mock JWT tokens and role setups are configured in `test/resources`.

## 🛠️ Setup & Run Locally

1. Clone repo
2. Run:
   ```bash
   mvn spring-boot:run
   ```
3. Access H2 DB at:  
   `http://localhost:8080/h2-console`  
   (JDBC URL: `jdbc:h2:mem:testdb`)

## 👤 Default Users (for Testing)

| Email                  | Password | Role     |
|------------------------|----------|----------|
| bidder@example.com     | password | BIDDER   |
| approver@example.com   | password | APPROVER |

## 📂 Project Structure

- `model/` - Entity classes
- `controller/` - REST endpoints
- `service/` - Business logic
- `security/` - JWT filters & config
- `repository/` - Spring Data JPA

## 📌 Notes

- Use `NoOpPasswordEncoder` only for testing.
- Always replace it with `BCryptPasswordEncoder` in production.
- Customize `SecurityConfiguration` as needed for new endpoints.
