# Smart Finance Tracker

A comprehensive mobile application for personal finance management built with React Native and Spring Boot.

## Features

- User Authentication (Email, Phone, Social Login, Biometric)
- Multi-account Management
- Income & Expense Tracking
- Budgeting System
- Savings Goals
- Analytics & Dashboard
- Multi-currency Support
- Data Export & Backup
- Secure Data Management

## Tech Stack

### Frontend
- React Native
- React Navigation
- Redux/Context API
- Axios
- Chart.js/React Native Charts

### Backend
- Spring Boot
- Spring Security
- MySQL
- JPA/Hibernate
- JWT Authentication

## Project Structure

```
smart-finance-tracker/
├── frontend/           # React Native application
└── backend/           # Spring Boot application
```

## Getting Started

### Prerequisites
- Node.js (v14 or higher)
- Java JDK 11 or higher
- MySQL
- Android Studio / Xcode
- React Native CLI

### Frontend Setup
1. Navigate to the frontend directory
2. Install dependencies: `npm install`
3. Start the development server: `npm start`

### Backend Setup
1. Navigate to the backend directory
2. Configure MySQL database in `application.properties`
3. Build the project: `./mvnw clean install`
4. Run the application: `./mvnw spring-boot:run`

## Security
- JWT-based authentication
- Encrypted data storage
- Secure password handling
- Biometric authentication support

## License
MIT License 