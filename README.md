# Quotes Application

A Spring Boot application for managing and serving inspirational quotes and their authors.

## Overview

This application provides a RESTful API for managing a collection of quotes and their authors. It allows users to browse, search, create, update, and delete quotes and author information.

## Features

- CRUD operations for authors and quotes
- Pagination and sorting support
- Comprehensive API documentation with Swagger
- Clean architecture with separation of concerns (controllers, services, DTOs)

## Technology Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Web
- Swagger/OpenAPI for documentation
- Maven/Gradle for dependency management

## API Endpoints

### Authors API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/authors` | Get all authors (paginated) |
| GET | `/api/authors/{id}` | Get author by ID |
| POST | `/api/authors` | Create a new author |
| PUT | `/api/authors/{id}` | Update an existing author |
| DELETE | `/api/authors/{id}` | Delete an author |

### Quotes API

| Method | Endpoint                              | Description                 |
|--------|---------------------------------------|-----------------------------|
| GET | `/api/quotes`                         | Get all quotes (paginated)  |
| GET | `/api/quotes/{id}`                    | Get quote by ID             |
| GET | `/api/quotes/pairs/count/{maxLength}` | Count compabible quotepairs |
| POST | `/api/quotes`                         | Create a new quote          |
| PUT | `/api/quotes/{id}`                    | Update an existing quote    |
| DELETE | `/api/quotes/{id}`                    | Delete a quote              |

## Getting Started

### Prerequisites

- Java 17+ installed
- Gradle installed
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/lupnor5/quotes-application.git
   cd quotes-application
   ```

2. Build the application:
   ```bash
   ./gradlew clean build
   ```

3. Run the application:
   ```bash
   ./gradlew :bootRun
   ```

4. The application will be available at:
   ```
   http://localhost:8080
   ```

5. Access the Swagger documentation at:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Configuration

The application can be configured through the `application.properties` or `application.yml` file:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quotes_db
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080
```

## Database Schema

The application uses the following core entities:

- **Author**: Stores information about quote authors
- **Quote**: Stores the actual quotes with references to their authors

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact

For any inquiries, please contact:
- Email: lupnor5@gmal.com
- GitHub: [lupnor5](https://github.com/lupnor5)