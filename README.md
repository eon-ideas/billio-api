# Billio API

A Spring Boot-based REST API service for Billio payment processing and barcode generation.

## Project Purpose

Billio API is designed to provide backend services for the Billio payment platform. The API enables:

- Generation of 2D payment barcodes (PDF417) for invoices
- Secure authentication using JWT tokens from Supabase
- RESTful endpoints for payment processing

The primary focus is on providing a robust, secure, and scalable backend for payment processing applications.

## Framework and Libraries

This project is built using the following technologies:

### Core Framework
- **Spring Boot 3.4.4**: Modern Java framework for building web applications
- **Spring Security**: For authentication and authorization
- **Spring MVC**: For building RESTful endpoints

### Authentication
- **Auth0 Java JWT (4.3.0)**: For JWT token validation
- **Supabase Authentication**: For user management and token issuance

### Barcode Generation
- **ZXing (3.3.0)**: For generating PDF417 barcodes

### Documentation
- **SpringDoc OpenAPI (2.3.0)**: For API documentation with Swagger UI

### Validation
- **Jakarta Validation API (3.0.2)**: For request validation

## Development Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Supabase account (for authentication)

### Environment Variables
Both variables are required; the application will not start without them:

```
SUPABASE_JWT_SECRET=your-jwt-secret-from-supabase
SUPABASE_JWT_ISSUER=https://your-project-ref.supabase.co/auth/v1
```

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/eon-ideas/billio-api-public.git
   cd billio-api-public
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application in development mode:
   ```bash
   ./mvnw spring-boot:run
   ```

4. The API will be available at:
   - Base URL: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - OpenAPI Docs: http://localhost:8080/api-docs

### API Testing

You can test the API using the Swagger UI or with tools like curl or Postman:

```bash
# Example: Generate a barcode for invoice with ID 1
curl -X POST \
  http://localhost:8080/invoices/1/2d-barcode.png \
  -H 'Authorization: Bearer your-jwt-token' \
  -H 'Content-Type: application/json' \
  -d '{
    "amount": 100.00,
    "recipientName": "Company Name",
    "iban": "HR1210010051863000160",
    "model": "HR00",
    "callNumber": "123456-789",
    "description": "Invoice payment"
  }'
```

### Generating Barcodes from Deployed API

To generate a barcode from the deployed API and save it locally:

```bash
curl -X 'POST' \
  'https://<your-deployment-host>/invoices/6d9fd9ed-0ee8-4f2a-ba9a-23c36f78eeec/2d-barcode.png' \
  -H 'accept: image/png' \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -H 'Content-Type: application/json' \
  --output barcode.png \
  -d '{
  "amount": 2050.00,
  "recipientName": "Company Name",
  "recipientAddress": {
    "street": "Example Street",
    "houseNumber": "1",
    "city": {
      "name": "Zagreb",
      "postalCode": "10000"
    }
  },
  "iban": "HR1210010051863000160",
  "model": "99",
  "callNumber": "1-1-1",
  "description": "Invoice 1-1-1"
}'
```

## Production Deployment

### Building for Production

1. Build the production JAR:
   ```bash
   ./mvnw clean package
   ```

2. The production-ready JAR will be available in the `target` directory.

### Running in Production

1. Run the JAR with production configuration:
   ```bash
   java -jar target/billio-api-0.0.1-SNAPSHOT.jar
   ```

2. For production deployment, consider:
   - Using a process manager like systemd
   - Setting up HTTPS with a reverse proxy (Nginx, Apache)
   - Configuring proper logging
   - Setting up monitoring

## Security Considerations

- JWT tokens are validated for signature, issuer, and audience
- All API endpoints under `/invoices` require authentication
- Sensitive configuration should be provided via environment variables
- Consider implementing rate limiting for production use

## Further Documentation

For more detailed information about the API endpoints, refer to the Swagger UI documentation available at `/swagger-ui.html` when the application is running.

## Support

For support, contact Billio Support at support@eonideas.hr or visit our website at https://eon-billio.netlify.app.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
