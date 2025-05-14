# Easy Drive
# Project Description
**This project is a car-sharing service** built using **Java 17 and Spring Boot.** It allows users to rent vehicles, view available cars, and pay online via Stripe.
## Problem and Solution
The app addresses the growing demand for flexible, on-demand vehicle rental options. It helps users rent cars in an easy and efficient way, without the need for a long-term commitment. The system supports online payment, car management for administrators, and notifications about car rentals.
### Key Features for Users:
- **Browse all available cars:** Users can view a list of all available cars in real time. 
- **Rent a car:** Users can select a car and complete the rental process.
- **Online Payment:** Payments for rentals are securely processed via Stripe.
### Key Features for Administrators:
- **Car Management:** Admins can add and edit car details as needed.
- **Telegram Bot Notifications:** A Telegram bot is integrated for notifications regarding rentals and payments. The bot has simple commands like:
  - **/start** - to initiate the bot.
  - **/stop** - to stop the bot
  - **/manager** - to enter the admin section with a password for privileged access.
- **Admin Authentication:** Only authorized administrators can receive notifications about rental actions and payments.
- **Real-time Notifications:** Admins are notified immediately when a user rents a car or makes a payment, as well as when there are active or overdue rentals.
### Rental Calculation:
Rentals are charged per minute, and users are only billed for the actual time the car is rented, even if returned earlier than planned.
## Technologies Used
- **Java 17**
- **Spring Boot:** Used for building the backend of the application.
- **Spring Security:** Implements authentication and authorization using JWT (JSON Web Tokens) for secure user login and session management.
- **Spring Data JPA:** Provides easy integration with MySQL through JPA, simplifying database operations and reducing boilerplate code.
- **Stripe API:** Integrated for secure and efficient online payments. Stripe handles the payment processing for car rentals, providing a seamless experience for users.
- **Telegram Bot:** A custom Telegram bot for real-time notifications. The bot is used to send alerts to administrators about new rentals, payments, and overdue rentals, ensuring quick and efficient communication.
- **MapStruct:** Used for object mapping, simplifying the transformation of data between entities and DTOs.
- **MySQL:** Database.
- **Liquibase:** Used for database version control.
- **Docker:** Containerization for application deployment.
- **Swagger:** Used for API documentation and testing, providing an interactive interface to explore and test the RESTful endpoints.
- **Junit**, **Mockito**, **Testcontainers:** Testing frameworks used for unit testing, mocking dependencies, and testing database interactions in a containerized environment.
## Technologies version
- **Java 17, Spring Boot(v.3.4.1), Stripe API(v.22.13.0), Telegram API(v.6.7.0), MapStruct(v.1.5.5), JWT(v.0.11.5), MYSQL(v.8.4.2), TestContainers(v.1.19.0)**
## Design Patterns
- **Builder Pattern:** Used for dynamic message creation.
- **Strategy Pattern:** Employed to calculate rental prices. This is a scalable choice, allowing for future enhancements like promotions and discount logic.
- ## Database structure
![image](https://github.com/user-attachments/assets/6c3eadb9-f7a9-40d5-ac8b-e3e595977ae6)

# How to run the project
### Prerequisites:
- **Java 17 must be installed on your system.**
- **MySQL should be installed and running. Ensure you have the credentials (username and password) to access your MySQL database.**
### Clone the Repository:
```
git clone https://github.com/dlvsn/car-sharing-app
cd <project_directory>
```
### Set Up the Database:
- **Create a MySQL database.**
- **Create your own .env file and fill in the required data.**
   ```
   TELEGRAM_BOT_USERNAME=<your_telegram_bot_username>
   TELEGRAM_BOT_TOKEN=<your_telegram_bot_token>
   
   JWT_SECRET=<your_jwt_secret>
   
   STRIPE_SECRET_KEY=<your_stripe_secret_key>
   
   MYSQLDB_DATABASE=<your_database_name>
   MYSQLDB_USER=<your_username>
   MYSQLDB_PASSWORD=<your_password>
   MYSQLDB_ROOT_PASSWORD=<your_root_password>
   
   SPRING_DATASOURCE_PORT=<your_spring_datasource_port>
   MYSQLDB_LOCAL_PORT=<your_local_port>
   MYSQLDB_DOCKER_PORT=<your_docker_port>
   SPRING_LOCAL_PORT=<your_spring_local_port>
   SPRING_DOCKER_PORT=<your_spring_docker_port>
   ```
### Run the Application:
- **Open a terminal in the project directory and execute:**
  ``` 
  ./mvnw spring-boot:run
  ```
- **Alternatively, if you are using an IDE like IntelliJ IDEA, open the project, locate the Application class (usually in the src/main/java directory), and run it.**
- **Run with Docker. You can also run the project using Docker by executing the following command:**
  ```
  docker-compose up --build
  ```

### Access the API Documentation:
- **The project includes Swagger for API documentation. Once the application is running, open your browser and navigate to:**
  http://localhost:8080/swagger-ui/index.html
- **If you are running the project in Docker, the Swagger documentation may be available on a different port. Check your container settings.**

# Now you can explore and test the available endpoints!
