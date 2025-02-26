# Emergency Roadside Help - Backend

## Overview
This repository contains the backend implementation for the **Emergency Roadside Help** system. The system is designed to provide assistance to vehicles in emergency situations by connecting them with available service providers in real time.
The backend follows an **event-driven microservices architecture**, utilizing technologies such as **Spring boot, Java, Axon Server, CQRS, Redis, and Hibernate ORM** to ensure scalability and efficiency.

## Features
- **Service Request Management:** Users can request roadside assistance, and the system assigns the nearest available service provider.
- **Event-Driven Architecture:** Uses message queues (Axon Event Store) for seamless communication between microservices.
- **Microservices-based Design:** Independent services for handling user management, responder assignment, service tracking.
- **Database Integration:** PostgreSQL with Hibernate ORM for efficient data handling.
- **Real-time Communication:** Polling mechanisms to update users on request status.
- **Observability & Monitoring:** Integrated logging and monitoring tools for system health tracking.

## Tech Stack
- **Backend Framework:** Spring boot with Java
- **Database:** PostgreSQL with Hibernate ORM
- **Message Broker:** Axon Server with Axon Event Bus
- **Cache:** Redis
- **Containerization:** Docker
- **Testing:** Junit, Mockito
- **API Documentation:** Swagger, MD files.

## Microservices
The backend consists of multiple microservices that communicate asynchronously:
1. **Booking Service:** Manages user authentication, profile details, booking.
2. **Assignment Service:** Manages responders, their services, and responder assignment.
3. **Roadside help Service:** Tracks all data of help provided, assistance status, billing.

## Setup & Installation
1. **Clone the Repository**
   ```bash
   git clone https://github.com/sajib-4414/emergency-roadside-help-backend.git
   cd emergency-roadside-help-backend
   ```
2. **Open the project in Intellijidea**
  - Open one of the projects in intellij ide. then on the right side menu, click on the m. click add project, then add the other two projects. Now all three projects are available as module.
3. **Set Up Environment Variables**
   for each of the projects, go to main/resources. create a secret.yml with these contents
```
spring:
  datasource:
    url: jdbc:postgresql://postgres_db_host:port/db_name
    username: username
    password: password
```
6. **Start the Services**
go to each of the projects' main file, and click run. It will start the projects.

## API Endpoints
The API follows RESTful conventions. Detailed documentation is available via Swagger:
```
http://localhost:8080/api/docs
```

## Contribution
Contributions are welcome! Please follow these steps:
- Fork the repository
- Create a feature branch
- Commit changes with descriptive messages
- Submit a pull request

## License
This project is licensed under the MIT License.

---
Feel free to reach out for any inquiries or improvements!
