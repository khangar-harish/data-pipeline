# CSV Data Pipeline

## Overview

The CSV Data Pipeline application is a Spring Boot service designed to process CSV files, detect outliers in the data, and save valid records to a PostgreSQL database. The application uses Docker for containerization and is configured to run with PostgreSQL as the database.

## Features

- **CSV Upload**: Upload CSV files through a REST API.
- **Outlier Detection**: Detect outliers in the 'value' column based on statistical calculations.
- **Database Integration**: Save valid records to a PostgreSQL database.
- **Dockerized**: Containerized with Docker for easy deployment and testing.

## Prerequisites

1. **Java**: Ensure you have Java (JDK 17 or later) installed if you plan to build the project locally. [Java Installation Guide](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
2. **Maven**: Maven is required to build the project. [Maven Installation Guide](https://maven.apache.org/install.html)
3. **Docker**: Make sure Docker and Docker Compose are installed on your machine. [Docker Installation Guide](https://docs.docker.com/get-docker/)
4. **PostgreSQL Client**: Install a PostgreSQL client like [pgAdmin](https://www.pgadmin.org/) to interact with your PostgreSQL database.

### 1. Clone the Repository

First, clone the repository to your local machine:

```sh
git clone https://github.com/khangar-harish/data-pipeline.git

cd datapipeline
```

### 2. Build the Project

```sh
mvn clean package -DskipTests 
```

1. **Clean up**: Remove any previous build artifacts.
2. **Build**: Compile the code and package it into a JAR file.
3. **Skip Tests**: Do not run the unit tests during this build process.

### 2. Build and Run Docker containers

```sh
docker compose up --build
```

This command builds the Docker images and starts the containers for both the application and PostgreSQL.

## How to Run This Code

### 1. Upload CSV File

Once the Docker containers are running, you can upload a CSV file to the application using a REST client like Postman.

- Open Postman and create a new POST request to `http://localhost:9595/api/files/upload`.
- In the body of the request, select `form-data` and add a key `file` with type `File`, then choose your CSV file to upload.
- Send the request.

The application will process the file, detect outliers, and save the data to the PostgreSQL database if no outliers are found.

### 2. Monitor Logs

You can view the application logs to ensure it is processing the data correctly:

```sh
docker compose logs -f
```

### 3. Access PostgreSQL database

Open pgAdmin and create a new server connection.
- Use the following connection details: 
  - **Host**: localhost
  - **Port**: 5433
  - **Database**: postgres
  - **User**: postgres
  - **Password**: postgres

