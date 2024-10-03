# Database Service API

**Database Service API** is a RESTful API designed to work with different types of databases (a file-based JSON database and an SQL database). The project provides an interface `DatabaseService` that allows easy switching between database implementations, making the application flexible and extendable.

## Table of Contents
1. [Description](#description)
2. [Components](#components)
3. [Requirements](#requirements)
4. [Installation](#installation)
5. [Configuration](#configuration)
6. [Running the Project](#running-the-project)
7. [Usage](#usage)
8. [Testing](#testing)
9. [Contact](#contact)

## Description
This project contains a RESTful API for performing CRUD operations on different types of databases. The system includes two main database implementations:
- **File-based JSON Database (`JsonDatabaseService`)**: Stores data in JSON files.
- **SQL Database (`SqlDatabaseService`)**: Interacts with a traditional SQL database.

A REST controller (`DatabaseServiceRestController`) is used to expose the API endpoints, allowing the client to interact with the chosen database service.

## Components
- **`DatabaseService` Interface**: Defines a common interface for all database operations.
- **`JsonDatabaseService`**: Implementation of `DatabaseService` that manages data stored in JSON files.
- **`SqlDatabaseService`**: Implementation of `DatabaseService` for managing data in SQL databases.
- **`DatabaseServiceRestController`**: REST controller that provides endpoints for interacting with the database services.
- **`Settings`**: Class responsible for loading configuration properties from a `.properties` file, which is used to determine the type of database service and other settings.

## Requirements
- **Java 11** or higher.
- **Maven** for dependency management.
- **SQL Database** (e.g., MySQL, PostgreSQL) if using the `SqlDatabaseService` implementation.

## Installation
1. **Clone the repository**:
   ```bash
   git clone https://github.com/shashinadya/code-practice.git
   
2. **Navigate to the project directory**:
    ```bash
   cd database
   
3. **Build the project using Maven**:
    ```bash
   mvn clean install
   
4. **Check the dependencies: Make sure all required dependencies are listed in the pom.xml (for Maven) file and are installed.**

## Configuration
The application uses an `application.properties` file for configuration. Ensure that you have a file named `application.properties` in the `resources` directory with the following structure:

```properties
# Application properties
app.name=DatabaseService
app.version=1.0.0

# File storage configuration
database.storage.path=./data

# Limit parameter value
limit=100

# Application port
port=8080

# Database username
database.username=db_user

# Database password
database.password=Qwerty!1

# Database base URL
database.base.url=jdbc:mysql://localhost:3306/

# Database name
database.name=entities
```
Configure the database.storage.path in application.properties before running the application.

## Running the Project
1. **Navigate to the target directory: If you built the project using Maven, the jar file will be located in the target directory.**


2. **Run the application: Use the following command to run the project. Replace your-application.jar with the actual name of your jar file:**
```bash
java -jar target/database.jar
```

3. **Check the application status: Make sure your application starts correctly without any errors. Check the console output for confirmation.**


4. **Access the application: After the application has started, you can access it at http://localhost:8080 (or the port you specified in application.properties).**

## Usage
Once the server is running, you can interact with the RESTful API using tools like curl, Postman, or your own client application.

### Example Endpoints
**Create table**:
``POST localhost:8080/api/v1/database/Student/table``

**Add new record to table**:
``POST localhost:8080/api/v1/database/Student``
Request Body:
```json
{
    "fullName": "Nadya",
    "averageScore": 4.0
}
```

**Update an existing record**:
``PUT localhost:8080/api/v1/database/Student/1``
Request Body:
```json
{
  "id": 1,
  "fullName": "Iva",
  "averageScore": 3.5
}
```

**Delete record**:
``DELETE localhost:8080/api/v1/database/Student/1``

## Testing
To run the unit and integration tests, use the following command:
```bash
mvn test
```
Make sure you have the appropriate environment (e.g., a running SQL database if you are using the SqlDatabaseService) before running the tests.

## Contact
If you have any questions or need support, feel free to contact:

GitHub: https://github.com/shashinadya