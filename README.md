# Antici AI — Student Assistant

> An AI-powered student assistant Android application with a secure Spring Boot backend, JWT authentication, and MySQL database integration.

## 📌 Overview

**Antici AI** is a student-focused Android application designed to provide a centralized platform for managing academic activities and accessing intelligent assistance.

The project follows a **full-stack mobile architecture**:

* 📱 **Android application** — student-facing mobile interface
* ⚙️ **Spring Boot backend** — REST API and business logic
* 🗄️ **MySQL database** — persistent student data
* 🔐 **JWT authentication** — secure login and protected API access

The current implementation includes user registration, login, JWT-based authentication, student dashboard data, and Android-to-backend integration.

---

## ✨ Features

### 🔐 Authentication

* Student registration
* Student login
* Password hashing using Spring Security
* JWT token generation
* JWT token storage on Android
* Automatic JWT authorization for protected API requests
* Protected student endpoints

### 📊 Student Dashboard

The dashboard provides personalized information for the authenticated student, including:

* Student name
* Student email
* Student ID
* Student role
* AI status
* Academic overview
* Active tasks
* Latest prediction
* Today's schedule

### 📱 Android Application

The Android application includes navigation for:

* 🏠 Dashboard
* 📅 Schedule
* ✅ Tasks
* 🤖 AI Chat
* 👤 Profile

The application uses Retrofit for communication with the Spring Boot REST API.

### 🗄️ Database

MySQL is used for storing student account information.

The current `users` table contains information such as:

* ID
* Name
* Email
* Password hash
* Role

---

## 🏗️ Architecture

```text
┌─────────────────────────────┐
│       Android App           │
│                             │
│  Dashboard                  │
│  Schedule                   │
│  Tasks                      │
│  AI Chat                    │
│  Profile                    │
└──────────────┬──────────────┘
               │
               │ REST API
               │ Retrofit
               ▼
┌─────────────────────────────┐
│      Spring Boot API        │
│                             │
│  Authentication             │
│  JWT Security                │
│  Student APIs               │
│  Business Logic             │
└──────────────┬──────────────┘
               │
               │ JPA / Hibernate
               ▼
┌─────────────────────────────┐
│          MySQL              │
│                             │
│          users              │
│       student data          │
└─────────────────────────────┘
```

---

## 🛠️ Technology Stack

### Android

| Technology           | Purpose                               |
| -------------------- | ------------------------------------- |
| Java                 | Android application development       |
| Android SDK          | Mobile application platform           |
| AndroidX             | Android UI and application components |
| Retrofit 3.0.0       | REST API communication                |
| Gson Converter 3.0.0 | JSON serialization/deserialization    |
| OkHttp 5.2.1         | HTTP client and networking            |

### Backend

| Technology        | Purpose                                 |
| ----------------- | --------------------------------------- |
| Java              | Backend development                     |
| Spring Boot 4.0.0 | REST API framework                      |
| Spring Security   | Authentication and security             |
| JWT               | Stateless authentication                |
| Spring Data JPA   | Database access                         |
| Hibernate         | ORM                                     |
| Maven             | Backend build and dependency management |

### Database

| Technology | Purpose             |
| ---------- | ------------------- |
| MySQL 8.0  | Relational database |

### Development Tools

* IntelliJ IDEA
* Android Studio
* Git
* GitHub
* Android Emulator

---

## 📁 Project Structure

```text
AnticiAI/
│
├── app/                              # Android application
│   └── src/main/
│       ├── java/com/anticai/
│       │   └── studentassistant/
│       │       ├── activities/
│       │       ├── fragments/
│       │       │   ├── ai/
│       │       │   ├── home/
│       │       │   ├── profile/
│       │       │   ├── schedule/
│       │       │   └── tasks/
│       │       └── network/
│       │
│       └── res/
│           ├── layout/
│           ├── drawable/
│           ├── values/
│           └── xml/
│
├── src/                              # Spring Boot backend
│   └── main/
│       ├── java/com/anticiai/backend/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── repository/
│       │   ├── security/
│       │   └── service/
│       │
│       └── resources/
│           └── application.properties
│
├── pom.xml                           # Maven configuration
├── build.gradle.kts                   # Android Gradle configuration
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

---

# 🔐 Authentication Flow

Antici AI uses JWT-based authentication.

### 1. Registration

The Android application sends student registration information to:

```http
POST /api/auth/register
```

Example request:

```json
{
  "name": "Student Name",
  "email": "student@example.com",
  "password": "password123"
}
```

The backend:

1. Checks whether the email already exists.
2. Hashes the password.
3. Creates the student account.
4. Generates a JWT.
5. Returns the authentication response.

---

### 2. Login

The Android application sends credentials to:

```http
POST /api/auth/login
```

Example:

```json
{
  "email": "student@example.com",
  "password": "password123"
}
```

The backend validates the credentials and returns a JWT.

---

### 3. Token Storage

The Android application stores the JWT locally using `SharedPreferences`.

The token is stored under:

```text
AnticiPrefs
└── jwt_token
```

---

### 4. Protected Requests

An Android `AuthInterceptor` automatically adds the JWT to protected requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

This allows the backend to identify the currently authenticated student.

---

# 🌐 REST API

## Authentication APIs

### Register

```http
POST /api/auth/register
```

### Login

```http
POST /api/auth/login
```

---

## Student APIs

### Get Current Student

```http
GET /api/student/me
```

Requires:

```http
Authorization: Bearer <JWT_TOKEN>
```

Example response:

```json
{
  "message": "JWT authentication successful",
  "email": "student@example.com"
}
```

---

### Get Student Dashboard

```http
GET /api/student/dashboard
```

Requires:

```http
Authorization: Bearer <JWT_TOKEN>
```

Example response:

```json
{
  "id": 1,
  "name": "Student Name",
  "email": "student@example.com",
  "role": "ROLE_STUDENT"
}
```

---

# 🗄️ Database Configuration

The backend uses MySQL.

Create the database:

```sql
CREATE DATABASE antici_ai;
```

The application connects to the database through the Spring Boot configuration.

Example configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/antici_ai
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> Replace the database username and password with your local MySQL credentials.

### User Table

The application uses a `users` table containing fields such as:

```text
id
name
email
password_hash
role
```

Passwords are stored as hashes rather than plain text.

---

# 🚀 Running the Backend

## Prerequisites

Make sure the following are installed:

* Java JDK
* Maven
* MySQL
* IntelliJ IDEA or another Java IDE

### Step 1 — Clone the repository

```bash
git clone https://github.com/AsthaSisodiya-123/antici-ai-student-assistant.git
```

### Step 2 — Open the project

Open the project in IntelliJ IDEA.

Navigate to:

```text
src/main/java/com/anticiai/backend
```

### Step 3 — Configure MySQL

Create the `antici_ai` database and update the database credentials in:

```text
src/main/resources/application.properties
```

### Step 4 — Start the backend

Run the Spring Boot application from IntelliJ.

The backend is configured to run on:

```text
http://localhost:8081
```

For Android emulator/network testing, the Android application can connect through the host machine's local network IP.

---

# 📱 Running the Android Application

## Prerequisites

Install:

* Android Studio
* Android SDK
* Android Emulator or physical Android device

### Step 1 — Open the Android project

Open the Antici AI project in Android Studio.

### Step 2 — Start the backend

Make sure the Spring Boot server is running before launching the Android application.

### Step 3 — Configure the API URL

The Android application uses Retrofit to communicate with the backend.

The current development configuration uses:

```text
http://172.16.1.136:8081/
```

Update the address in `RetrofitClient.java` if your computer's local IP address changes.

### Step 4 — Start the emulator

Launch an Android emulator and run the application.

### Step 5 — Test authentication

Create a student account or use an existing account.

After successful login:

```text
Login
  ↓
JWT received
  ↓
JWT stored locally
  ↓
Dashboard request
  ↓
JWT automatically attached
  ↓
Personalized student dashboard
```

---

# 🔒 Security

The project implements several security practices:

* Password hashing
* JWT-based authentication
* Protected student endpoints
* Authorization header interception
* Server-side authentication
* Database-level unique email constraint

Passwords should **never** be stored as plain text.

For production deployment, additional security measures should be added, including:

* HTTPS
* Secure secret management
* Environment variables
* Strong JWT signing keys
* Production database credentials
* Proper CORS configuration
* Refresh-token strategy where appropriate

---

# 🖥️ Current Application Flow

```text
Launch Antici AI
       │
       ▼
    Login
       │
       ├──────────────► Register
       │
       ▼
 Authenticate
       │
       ▼
 Receive JWT
       │
       ▼
 Store JWT
       │
       ▼
 Open Dashboard
       │
       ▼
 GET /api/student/dashboard
       │
       ▼
 Spring Security validates JWT
       │
       ▼
 Identify student
       │
       ▼
 Retrieve student from MySQL
       │
       ▼
 Return dashboard data
       │
       ▼
 Display student's name
```

---

# 📊 Current Status

| Component                     | Status            |
| ----------------------------- | ----------------- |
| Android application           | ✅ Implemented     |
| Spring Boot backend           | ✅ Implemented     |
| MySQL integration             | ✅ Implemented     |
| Student registration          | ✅ Implemented     |
| Student login                 | ✅ Implemented     |
| Password hashing              | ✅ Implemented     |
| JWT authentication            | ✅ Implemented     |
| Protected `/api/student/me`   | ✅ Implemented     |
| Student dashboard API         | ✅ Implemented     |
| Android dashboard integration | ✅ Implemented     |
| Personalized student greeting | ✅ Implemented     |
| Schedule module               | 🚧 In development |
| Tasks module                  | 🚧 In development |
| AI Chat module                | 🚧 In development |
| Profile module                | 🚧 In development |
| AI prediction engine          | 🚧 Planned        |

---

# 🔮 Future Enhancements

The planned evolution of Antici AI includes:

### 🤖 AI Assistant

* Intelligent student chat
* Academic question answering
* Personalized recommendations
* AI-powered study assistance

### 📈 Academic Prediction

* Performance prediction
* Academic progress analysis
* Risk detection
* Personalized improvement suggestions

### 📅 Smart Scheduling

* Automated study planning
* Exam preparation schedules
* Deadline reminders
* Priority-based task management

### ✅ Task Management

* Create and manage tasks
* Task priorities
* Due dates
* Completion tracking

### 👤 Student Profile

* Profile management
* Academic information
* Preferences
* Personalized settings

### 🔔 Notifications

* Assignment reminders
* Exam reminders
* Study reminders
* AI-generated recommendations

---

# 🧪 Development & Testing

The project can be tested using:

* Android Emulator
* Physical Android device
* IntelliJ IDEA
* Android Studio
* MySQL
* REST API testing tools such as Postman

Recommended API testing sequence:

```text
1. Register student
       ↓
2. Login
       ↓
3. Copy JWT
       ↓
4. Call /api/student/me
       ↓
5. Call /api/student/dashboard
```

---

# 📌 Important Development Notes

### Android Emulator Networking

When connecting an Android emulator to a backend running on the development computer, the API base URL must point to an address reachable from the emulator.

If the computer's local IP changes, update:

```text
RetrofitClient.java
```

For example:

```java
private static final String BASE_URL =
        "http://YOUR_LOCAL_IP:8081/";
```

### Backend Port

The Spring Boot backend currently runs on:

```text
8081
```

---

# 👩‍💻 Project

**Antici AI — Student Assistant**

A full-stack student assistant application combining:

```text
Android
   +
Spring Boot
   +
Spring Security
   +
JWT
   +
MySQL
   +
AI-powered student assistance
```

---

# 📄 License

This project is currently intended for educational and development purposes.

Add an appropriate open-source license here if the project is later released publicly under a specific license.

---

## ⭐ Project Goal

The long-term goal of **Antici AI** is to evolve from a basic student management application into an intelligent academic companion that can understand student activity, anticipate academic needs, and provide personalized assistance.
