# Transaction Management System

A robust transaction management system built with Spring Boot and modern web technologies. This system provides a complete solution for managing financial transactions with features like creation, updating, deletion, and listing of transactions.

## Features

- **Transaction Management**
  - Create new transactions with description, amount, currency, and type
  - View all transactions with pagination support
  - Update existing transactions
  - Delete transactions
  - Automatic ID generation using Snowflake algorithm

- **Data Validation**
  - Amount validation
    - Must be positive numbers
    - Must have 2 decimal places
    - Must not exceed 1,000,000
  - Description validation
    - Required field
    - Maximum length: 1024 characters
  - Currency validation
    - Required field
    - Length between 1 and 32 characters
    - Must be a valid currency code
  - Transaction type validation
    - Must be one of: PAYMENT, TRANSFER, DEPOSIT

- **User Interface**
  - Clean and responsive Bootstrap-based UI
  - Real-time form validation
  - Modal-based transaction editing
  - Confirmation dialogs for destructive actions
  - Error handling and user feedback

- **Technical Features**
  - RESTful API architecture
  - Layered architecture with clear separation of concerns
  - Caching support for better performance
  - Comprehensive error handling
  - Docker containerization support

### Backend
- Java 21
- Spring Boot
- Spring Cache
- Jakarta Validation
- Lombok
- JUnit 5 for testing

### Frontend
- HTML5
- CSS3
- JavaScript (ES6+)
- Bootstrap 5
- Fetch API for HTTP requests

### DevOps & Deployment
- Docker
- Docker Compose
- Maven

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 21 or later
- Maven 3.6 or later
- Docker 20.10.x or later (for containerized deployment)
- Docker Compose v2.x or later (for containerized deployment)

### Running Locally

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd transaction-management
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Access the application:
   - Open your browser and navigate to `http://localhost:8080`
   - The API endpoints are available at `http://localhost:8080/api/transactions`

### Running with Docker

#### Using Docker Directly

1. Build the Docker image:
   ```bash
   docker build -t transaction-management .
   ```

2. Run the container:
   ```bash
   docker run -d -p 8080:8080 --name transaction-app transaction-management
   ```

3. View logs (optional):
   ```bash
   docker logs -f transaction-app
   ```

4. Stop and remove the container:
   ```bash
   docker stop transaction-app
   docker rm transaction-app
   ```

5. Remove the image:
   ```bash
   docker rmi transaction-management
   ```

6. Remove all stopped containers and unused images (optional):
   ```bash
   # Remove all stopped containers
   docker container prune
   
   # Remove all unused images
   docker image prune -a
   
   # Remove all unused containers, networks, images (both dangling and unreferenced)
   docker system prune -a
   ```

#### Using Docker Compose

1. Start the application:
   ```bash
   docker-compose up -d
   ```

2. View logs:
   ```bash
   docker-compose logs -f
   ```

3. Stop and remove containers and networks:
   ```bash
   # Stop and remove containers
   docker-compose down
   
   # Stop and remove containers along with images
   docker-compose down --rmi all
   
   # Stop and remove containers, images, and volumes
   docker-compose down --rmi all -v
   ```

#### Docker Configuration

The application includes the following Docker-related files:

- `Dockerfile`: Defines the container image build process
- `docker-compose.yml`: Defines the multi-container application setup
- `.dockerignore`: Specifies which files should be excluded from the Docker build context

Example `Dockerfile`:
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

Example `docker-compose.yml`:
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    restart: unless-stopped
```

## API Endpoints

### Transaction Management

- **Create Transaction**
  - POST `/api/transactions`
  - Request body example:
    ```json
    {
      "id": "123",
      "description": "Payment for services",
      "amount": "100.00",
      "currency": "USD",
      "type": "PAYMENT"
    }
    ```
    The field "id" is optional.

- **Get Transaction**
  - GET `/api/transactions/{id}`
  - Returns transaction details by ID

- **Update Transaction**
  - PUT `/api/transactions/{id}`
  - Request body similar to create transaction

- **Delete Transaction**
  - DELETE `/api/transactions/{id}`
  - Removes the transaction

- **List Transactions**
  - GET `/api/transactions?page=0&size=10`
  - Supports pagination
  - Returns paginated list of transactions

## Data Models

### Transaction
```json
{
  "id": "string",
  "description": "string",
  "amount": "string",
  "currency": "string",
  "type": "string",
  "status": "string", 
  "timestamp": "string"
}
```
The field "status" is set to "COMPLETED" by default.

### Supported Transaction Types
- PAYMENT
- TRANSFER
- DEPOSIT

## Error Handling

The system provides detailed error messages for various scenarios:
- Invalid transaction ID format
- Transaction not found
- Validation errors (invalid amount, missing required fields, etc.)
- Duplicate transaction errors
- General system errors

## Testing

Run the tests using:
```bash
./mvnw test
```

The project includes:
- Unit tests
- Integration tests
- Controller tests
- Service layer tests

## Security Considerations

- Input validation on both client and server side
- XSS prevention in the frontend
- CORS configuration for API security
- Error message sanitization
- Secure Docker configuration

## Performance Features

- Snowflake ID generation for better performance
- Caching support for frequently accessed data
- Pagination for large datasets
- Efficient database queries
- Optimized Docker image size

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 