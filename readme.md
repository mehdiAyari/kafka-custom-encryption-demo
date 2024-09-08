# Kafka Custom Encryption Demo

This project demonstrates custom encryption for Kafka messages using Spring Boot and Avro serialization.

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

## Getting Started

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/kafka-custom-encryption-demo.git
   cd kafka-custom-encryption-demo
   ```

2. Start the Kafka ecosystem using Docker Compose:
   ```
   docker-compose up -d
   ```

3. Build the project:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

## Usage

To send a message, use the following curl command:

```bash
curl -X POST http://localhost:8080/produce \
     -H "Content-Type: application/json" \
     -d '{
           "idPerson": 1,
           "action": "CREATE",
           "name": "John",
           "lastname": "Doe"
         }'
```

The application will encrypt the message, send it to Kafka, and then decrypt it when consuming.

## Monitoring

You can monitor your Kafka topics using AKHQ, which is included in the Docker Compose setup. Access it at:

```
http://localhost:8880
```

## Running Tests

To run the unit tests with the embedded Kafka broker:

```
mvn test
```

## Project Structure

- `src/main/java/com/kafka/custom/encryption/demo/`: Contains the main application code
  - `config/`: Kafka configuration
  - `consumer/`: Kafka consumer
  - `controller/`: REST controller for producing messages
  - `deserializer/`: Custom Avro deserializer with decryption
  - `producer/`: Kafka producer
  - `serializer/`: Custom Avro serializer with encryption
  - `service/`: Encryption service
- `src/main/resources/`: Contains application properties and Avro schema
- `src/test/`: Contains unit tests
