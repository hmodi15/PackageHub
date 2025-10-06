# PackageHub

A comprehensive package management system with Android frontend and Spring Boot backend, developed as part of COMS 309 coursework.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [API Documentation](#api-documentation)
- [Team Members](#team-members)
- [Experiments](#experiments)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

PackageHub is a full-stack application designed to manage package deliveries in residential buildings. The system provides role-based access for residents, managers, and administrators, with features including package tracking, building management, user authentication, and real-time notifications.

## ✨ Features

### Core Functionality
- **User Management**: Registration, authentication, and role-based access control
- **Package Tracking**: Add, update, and track package deliveries
- **Building Management**: Manage buildings, rooms, and residents
- **Real-time Notifications**: WebSocket-based chat and notifications
- **Image Processing**: OCR-based package recognition using Tesseract
- **Social Features**: Media posts and community interactions

### User Roles
- **Residents**: View and manage their packages
- **Managers**: Oversee building operations and resident management
- **Administrators**: Full system access and user management

### Mobile Features
- **QR Code Scanning**: Quick package identification
- **Push Notifications**: Real-time updates
- **Offline Support**: Basic functionality without internet
- **Dark Mode**: User preference support

## 🏗️ Project Structure

```
mk1_4/
├── Backend/                    # Spring Boot REST API
│   └── springboot_example/
│       ├── src/main/java/      # Java source code
│       ├── src/main/resources/ # Configuration files
│       └── pom.xml            # Maven dependencies
├── Frontend/                   # Android Application
│   └── AndroidExample/
│       ├── app/src/main/       # Android source code
│       ├── app/src/main/res/   # Resources (layouts, drawables)
│       └── build.gradle        # Gradle configuration
├── Experiments/                # Team member experiments
│   ├── BenjaminB/             # Benjamin's experiments
│   ├── BenjaminS/             # Benjamin's experiments
│   ├── Harsh/                 # Harsh's experiments
│   └── Lucas/                 # Lucas's experiments
└── Documents/                  # Project documentation
    ├── TestingReports/         # Test coverage reports
    ├── FrontendJavadocs/       # API documentation
    └── Design Documents/       # UI/UX designs
```

## 🛠️ Technologies Used

### Backend
- **Java 17**
- **Spring Boot 2.7.x**
- **Spring Data JPA**
- **Spring WebSocket**
- **Maven**
- **H2 Database** (Development)
- **MySQL** (Production ready)

### Frontend
- **Android SDK**
- **Java**
- **Volley** (HTTP requests)
- **WebSocket** (Real-time communication)
- **Tesseract OCR** (Text recognition)
- **Material Design**

### Development Tools
- **IntelliJ IDEA**
- **Android Studio**
- **Git**
- **JaCoCo** (Code coverage)
- **JUnit** (Testing)

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Android Studio (latest version)
- Maven 3.6+
- Git

### Backend Setup

1. **Navigate to backend directory**
   ```bash
   cd Backend/springboot_example
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The backend will be available at `http://localhost:8080`

### Frontend Setup

1. **Open Android Studio**

2. **Import the project**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to `Frontend/AndroidExample`

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for the sync to complete

4. **Run the application**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout

### Package Management
- `GET /api/packages` - Get all packages
- `POST /api/packages` - Create new package
- `PUT /api/packages/{id}` - Update package
- `DELETE /api/packages/{id}` - Delete package

### Building Management
- `GET /api/buildings` - Get all buildings
- `POST /api/buildings` - Create new building
- `GET /api/buildings/{id}/rooms` - Get rooms in building

### User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user

## 👥 Team Members

- **Harsh Modi** - Project Lead & Backend Development
- **Benjamin Bartels** - Frontend Development & Testing
- **Benjamin Steenhoek** - Backend Development & API Design
- **Lucas** - Frontend Development & UI/UX

## 🧪 Experiments

The `Experiments/` directory contains individual team member projects and learning exercises:

### BenjaminB Experiments
- **Exp1_1 to Exp1_3**: Basic Spring Boot setup and REST API development
- **Exp2_1 to Exp2_5**: Advanced Spring Boot features and database integration

### BenjaminS Experiments
- **Exp1 to Exp9**: Progressive Android development from basic UI to advanced features
- **MyExp1**: Custom experiment with unique functionality

### Harsh Experiments
- **Exp1 to Exp7**: Spring Boot learning progression and full-stack development

### Lucas Experiments
- **exp1_1 to exp1_3**: Android development fundamentals
- **exp2_1**: Advanced Android features and API integration

## 🧪 Testing

### Backend Testing
```bash
cd Backend/springboot_example
mvn test
```

### Frontend Testing
- Unit tests are located in `app/src/test/`
- Instrumented tests are in `app/src/androidTest/`
- Run tests through Android Studio or command line

### Code Coverage
- JaCoCo reports are generated in `target/site/jacoco/`
- View coverage reports in `Documents/` directory

## 📱 Screenshots

*Screenshots of the application will be added here*

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages

## 📄 License

This project is developed as part of COMS 309 coursework at Iowa State University. All rights reserved.

## 📞 Support

For support and questions:
- Create an issue in this repository
- Contact the development team
- Check the documentation in the `Documents/` folder

---

**Note**: This project is for educational purposes as part of COMS 309 coursework. It demonstrates full-stack development skills using modern technologies and best practices.