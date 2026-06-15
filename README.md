# Stock Analysis - Spring Boot Application

A **REST API** application built with **Spring Boot 3.3.x** and **Maven**.

---

## 🛠️ Tech Stack

| Technology          | Version       | Purpose                    |
|---------------------|---------------|----------------------------|
| Java                | 17            | Programming Language       |
| Spring Boot         | 3.3.5         | Application Framework      |
| Spring Web          | (via Boot)    | REST API                   |
| Spring Data JPA     | (via Boot)    | ORM / Database Access      |
| H2 Database         | (via Boot)    | In-memory DB (Dev)         |
| Lombok              | (via Boot)    | Boilerplate reduction      |
| Maven               | 3.x           | Build Tool                 |

---

## 📁 Project Structure

```
stock-analysis/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/project/stockanalysis/
    │   │   └── StockAnalysisApplication.java   ← Main class
    │   └── resources/
    │       └── application.properties          ← App config
    └── test/
        └── java/com/project/stockanalysis/
            └── StockAnalysisApplicationTests.java
```

---

## 🚀 How to Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at: **http://localhost:8080/api**

H2 Console: **http://localhost:8080/api/h2-console**

---

## ⚙️ Configuration

Edit `src/main/resources/application.properties` to:
- Change the server port (`server.port`)
- Switch to a real database (MySQL, PostgreSQL, etc.)
- Adjust JPA/Hibernate settings
