# crypto-trading-sim

This is a web application that simulates a cryptocurrency trading platform. It allows users to view real-time prices of the top 20 cryptocurrencies, perform virtual buy/sell transactions, manage their account balance, track their trading history and cryptocurrency holdings and reset their account — all without using real money.

## Features

- **Live Crypto Prices**: Real-time updates from the Kraken WebSocket V2 API.
- **Virtual Account**: Users start with an initial virtual balance of $10,000.
- **Buy/Sell Cryptocurrency**: Perform trades using live market prices.
- **Transaction History**: View all transactions with profit/loss tracking.
- **Reset Account**: Reset the account balance and holdings, clear transactions.
- **Robust Validation**: Prevent invalid actions like overspending or negative amounts.
- **Unit Testing**: JUnit and Mockito used for testing backend logic.

## Tech Stack

### Backend
- Java 17
- Spring Boot
- PostgreSQL
- Kraken WebSocket V2 API
- JUnit & Mockito for unit testing

### Frontend
- React (app.jsx)
- HTML, CSS

## Project Structure

cryptotradingsim/
├── backend/
│   ├── src/
│   │   └── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── com.cryptotradingsim.backend/
│   │   │   │           ├
│   │   │   │           ├── controllers/
│   │   │   │           │   ├── ApplicationController.java
│   │   │   │           │   ├── CryptoPriceController.java
│   │   │   │           │   ├── TransactionController.java
│   │   │   │           │   └── UserController.java
│   │   │   │           ├── models/
│   │   │   │           │   ├── CryptoPrice.java
│   │   │   │           │   ├── Transaction.java
│   │   │   │           │   └── User.java
│   │   │   │           ├── repositories/
│   │   │   │           │   ├── TransactionRepository.java
│   │   │   │           │   └── UserRepository.java    
│   │   │   │           ├── services/
│   │   │   │           │   ├── KrakenWebSocketClient.java
│   │   │   │           │   ├── TransactionService.java
│   │   │   │           │   └── UserService.java
│   │   │   │           ├── utils/
│   │   │   │           │   ├── KrakenPairs.java
│   │   │   │           └── CryptoTradingSimApplication.java
│   │   │   │           └── WebConfig.java
│   │   │   └──resources/
│   │   │       ├── application.properties
│   │   │       └── ...
│   │   └── test/
│   │       └── java/
│   │            └── com.cryptotradingsim.backend/
│   │               └── CryptoTradingSimApplicationTests.java
│   │               └── TransactionServiceTest.java
│   │               └── UserServiceTest.java
│   └── pom.xml
│   └── schema.sql
│
├── frontend/
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── index.js
│   │   ├── index.css
│   ├── package.json
│   └── README.md
│
├── README.md
└── Crypto_Trading_Simulator_API_Documentation.docx

## Setup Instructions

### Backend (Spring Boot + PostgreSQL)

**Create PostgreSQL Database**
Run the queries from schema.sql in pgadmin to manually create the tables

Configure Application - Edit application.properties with DB credentials:

spring.datasource.url=jdbc:postgresql://localhost:5432/cryptotradingsim
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

Run the backend from the IDE by running CryptoTradingSimApplication.java.

### Available Endpoints

GET /api/test
Returns plain text message “Backend is working!”

GET /api/prices/top20
Returns a list of CryptoPrice objects representing real-time prices of the top 20 cryptocurrencies by market cap.

GET /api/transactions/{userId}
Returns all transactions for a specific/given user.

GET /api/transactions/{userId}/holdings
Calculates and returns the current holdings of the user, mapping symbols (e.g., BTC, ETH) to quantities.

POST /api/users/create?username=PlamenPavlov
Creates a new user with the given username and initializes their balance - $10 000.

GET /api/users/{id}
Fetches user data (including balance) by user ID.

POST /api/users/{id}/buy
Buys cryptocurrency for a given user using current market price. 

POST /api/users/{id}/sell
Sells cryptocurrency for a given user using current market price. 

POST /api/users/{id}/reset
Resets a user account: Sets balance to the default initial value - $10 000, clears all transactions and holdings for the specific user.

### Frontend (React)
Navigate to the frontend directory from Visual Studio Code in the terminal and run this command:

npm install

To run the frontend React app, execute:

npm start

The app will launch on http://localhost:3000 and communicate with the backend on port 8080.

### Running Unit Tests
Tests can be run by right-clicking on a test in Intellij IDE and selecting Run...

### Notes
No real cryptocurrency is involved.

No ORM is used — raw SQL queries only (scripts included).

No user authentication is implemented (not required).