# CSV Data Pipeline

## Overview

The CSV Data Pipeline application is a Spring Boot service designed to process CSV files, detect outliers in the data, and save valid records to a PostgreSQL database. The application uses Docker for containerization and is configured to run with PostgreSQL as the database.

## Features

- **CSV Upload**: Upload CSV files through a REST API.
- **Outlier Detection**: Detect outliers in the 'value' column based on statistical calculations.
- **Database Integration**: Save valid records to a PostgreSQL database.
- **Dockerized**: Containerized with Docker for easy deployment and testing.

## Prerequisites

- Docker
- Docker Compose
- PostgreSQL Client (pgAdmin)







