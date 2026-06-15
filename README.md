# MarketLens | Premium Market Analysis

MarketLens (formerly StockVision) is a comprehensive, full-stack web application for tracking Indian stock market data, managing portfolios, and monitoring live market indices.

## Features

- **Live Market Data**: Tracks live prices of major Indian stocks and indices (Sensex, Nifty 50, etc.).
- **User Authentication**: Secure user registration and login with JWT token-based authentication.
- **Portfolio Management**: Add, track, and manage your personal stock portfolio.
- **Watchlist**: Save your favorite stocks to a personal watchlist for quick access.
- **Market News**: Stay updated with the latest business and financial news from top sources.
- **Interactive UI**: A responsive, high-density React frontend with real-time UI updates and data visualization.

## Architecture

- **Frontend**: Built with React, Vite, TailwindCSS, and Axios.
- **Backend**: Built with Java 17, Spring Boot 3, Spring Security, and Spring Data JPA.
- **Database**: PostgreSQL for persistent, relational data storage.

## Getting Started

### Prerequisites
- Node.js (v16+)
- Java 17
- PostgreSQL
- Maven

### Backend Setup
1. Navigate to the `backend` directory.
2. Update `src/main/resources/application.properties` with your PostgreSQL credentials, or copy `.env.example` to `.env` and set your variables.
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *Note: The backend runs on `http://localhost:8080`. The database will automatically seed with 30 initial stocks on first boot.*

### Frontend Setup
1. Navigate to the `frontend` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *Note: The frontend runs on `http://localhost:5173`.*

## License
All rights reserved. MarketLens 2026.
