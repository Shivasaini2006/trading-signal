# Trading Signal Tracking Application

## Setup Instructions

### Prerequisites
- Java 17+
- Maven
- (Optional) PostgreSQL

### How to Run
This application is currently configured to run using an in-memory **H2 Database** for ease of execution (no setup required). It operates in PostgreSQL compatibility mode. 

To run the application, execute:
```bash
./mvnw spring-boot:run
```

To run tests, execute:
```bash
./mvnw test
```

### Database Setup
If you want to use PostgreSQL instead of H2, update `src/main/resources/application.properties` as follows:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trading_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### API Documentation
Swagger UI is available at: 
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Architecture Overview
- **Project Structure**: Follows strict layered architecture (`controller`, `service`, `repository`, `entity`, `dto`, `mapper`, `validation`, `exception`).
- **Business Logic Flow**: The `TradingSignalController` intercepts HTTP requests, passing DTOs to the `TradingSignalService`. The service performs domain logic (evaluating target limits, expirations) alongside the `SignalValidator`, utilizing the `TradingSignalRepository` for persistence.
- **External API Integration**: A dedicated `BinanceService` utilizes Spring Boot 3 `RestClient` to fetch real-time ticker prices from the public Binance API.
