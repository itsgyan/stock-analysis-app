# 📈 StockVision

StockVision is a high-density, premium stock market analysis and portfolio tracking application. It provides real-time tracking of Indian market indices, corporate earnings, news, and individual equity performance, bridging the gap between professional-grade trading terminals and accessible consumer finance applications.

---

## 🌟 Key Features

* **Real-time Market Data:** Automatically fetches live market prices for Indian equities and 12+ major Indian Indices (NIFTY 50, SENSEX, BANKNIFTY) directly from the Yahoo Finance API.
* **Interactive Portfolio:** Add real transactions to your portfolio and watch your "Total Invested" and "Current Value" dynamically calculate and update in real-time as market prices fluctuate.
* **Dynamic Watchlist:** Search for specific stock tickers (e.g., `TCS`, `RELIANCE`) and add them to your personalized watchlist for quick price monitoring.
* **Corporate Earnings:** A daily-updating corporate earnings calendar that mathematically projects current Q1/Q2 revenues and profit growths for top Indian corporations.
* **Secure Authentication:** JWT-based robust authentication system ensuring your watchlists and portfolios remain private and secure via protected route guards.
* **Financial Utilities:** Includes a functioning SIP Calculator to estimate mutual fund returns over time.

---

## 🏗️ Architecture & Tech Stack

This project is built using a modern decoupled architecture:

### Frontend (Client)
* **Framework:** React 18 powered by Vite for lightning-fast HMR.
* **Styling:** Tailwind CSS integrated with a premium dark/light "glassmorphism" aesthetic.
* **Icons:** Lucide React.
* **Routing:** React Router v6 with `React.lazy()` route-level code splitting and protected routes.

### Backend (Server)
* **Framework:** Java 21 with Spring Boot 3.x.
* **Database:** PostgreSQL (Relational Database) utilizing Spring Data JPA for object-relational mapping.
* **Security:** Spring Security with BCrypt password encoding and stateless JWT tokens.
* **External Integrations:** Custom scraping and REST polling of `query1.finance.yahoo.com` to seed database with live stock quotes without relying on expensive API tiers.

---

## 🚀 Getting Started

To run this application locally, you will need **Node.js**, **Java 21**, and a **PostgreSQL** database running on port 5432.

### 1. Database Setup
Ensure PostgreSQL is running and create a database named `stockanalysis`.
```sql
CREATE DATABASE stockanalysis;
```
*(The Spring Boot application is configured to automatically create the schema tables on startup via Hibernate's `update` strategy).*

### 2. Start the Backend
Navigate to the `backend` directory and run the Spring Boot application using Maven:
```bash
cd backend
./mvnw spring-boot:run
```
*The backend will start on `http://localhost:8080`. During the first boot, it will automatically run the `DataSeeder` to populate the top 30 Indian stocks into your database.*

### 3. Start the Frontend
Open a new terminal, navigate to the `frontend` directory, install dependencies, and start the development server:
```bash
cd frontend
npm install
npm run dev
```
*The frontend will start on `http://localhost:5173`.*

---

## 💡 Usage

1. **Register an Account:** Navigate to `http://localhost:5173/register` and create a new account.
2. **Explore the Dashboard:** Observe the live market indices at the top of the dashboard.
3. **Build your Portfolio:** Go to the Portfolio tab, click `+ Add Transaction`, and enter a symbol (e.g., `RELIANCE`, `ZOMATO`) and a quantity.
4. **Monitor Earnings:** Check the Earnings tab to see the live generated reports for the current day.

---

## 📝 License
This project is open source and available for educational and non-commercial use.
