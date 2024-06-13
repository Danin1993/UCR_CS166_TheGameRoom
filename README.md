
# Game Rental Management System

## To run the program

```sh
source sql/scripts/create_db.sh
source java/scripts/compile.sh
```

Enjoy!

## Project Overview

The Game Rental Management System is designed to handle various operations for a game rental store, including user authentication, profile management, game catalog browsing, favorite game management, rental order processing, and tracking information updates. The project includes a PostgreSQL database schema and a Java application to interact with the database, providing a user-friendly interface.

## Database Schema

### Users Table
```sql
CREATE TABLE Users (
  login VARCHAR(50) NOT NULL,
  password VARCHAR(30) NOT NULL,
  role CHAR(20) NOT NULL,
  favGames TEXT,
  phoneNum VARCHAR(20) NOT NULL,
  numOverDueGames INTEGER DEFAULT 0,
  PRIMARY KEY(login)
);
```
*Purpose*: Stores user information including login credentials and contact details.

### Catalog Table
```sql
CREATE TABLE Catalog (
  gameID VARCHAR(50) NOT NULL,
  gameName VARCHAR(300) NOT NULL,
  genre VARCHAR(30) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  description TEXT,
  imageURL VARCHAR(20),
  PRIMARY KEY(gameID)
);
```
*Purpose*: Stores information about the games available for rent.

### RentalOrder Table
```sql
CREATE TABLE RentalOrder (
  rentalOrderID VARCHAR(50) NOT NULL,
  login VARCHAR(50) NOT NULL,
  noOfGames INTEGER NOT NULL,
  totalPrice DECIMAL(10,2) NOT NULL,
  orderTimestamp TIMESTAMP NOT NULL,
  dueDate TIMESTAMP NOT NULL,
  PRIMARY KEY(rentalOrderID),
  FOREIGN KEY(login) REFERENCES Users(login) ON DELETE CASCADE
);
```
*Purpose*: Stores details of rental orders placed by users.

### TrackingInfo Table
```sql
CREATE TABLE TrackingInfo (
  trackingID VARCHAR(50) NOT NULL,
  rentalOrderID VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL,
  currentLocation VARCHAR(60) NOT NULL,
  courierName VARCHAR(60) NOT NULL,
  lastUpdateDate TIMESTAMP NOT NULL,
  additionalComments TEXT,
  PRIMARY KEY(trackingID),
  FOREIGN KEY(rentalOrderID) REFERENCES RentalOrder(rentalOrderID) ON DELETE CASCADE
);
```
*Purpose*: Stores tracking information for rental orders.

### GamesInOrder Table
```sql
CREATE TABLE GamesInOrder (
  rentalOrderID VARCHAR(50) NOT NULL,
  gameID VARCHAR(50) NOT NULL,
  unitsOrdered INTEGER NOT NULL,
  PRIMARY KEY(rentalOrderID, gameID),
  FOREIGN KEY(rentalOrderID) REFERENCES RentalOrder(rentalOrderID) ON DELETE CASCADE,
  FOREIGN KEY(gameID) REFERENCES Catalog(gameID) ON DELETE CASCADE
);
```
*Purpose*: Stores the relationship between rental orders and the games included in them.

## Features and Implementations

1. **User Authentication**
   - Sign Up
   - Sign In

2. **Profile Management**
   - View Profile
   - Edit Profile

3. **Game Management**
   - View All Games
   - View Favorite Games
   - Add Favorite Game
   - Remove Favorite Game

4. **Order Management**
   - Create New Order
   - View All Orders
   - View Recent Orders
   - View Order Details

5. **Admin Functions**
   - Update User Information
   - Update Catalog Information
   - Update Tracking Information

## Contributions

Danin Namiranian implemented the entire project, including:

- Designing the database schema.
- Implementing user authentication (sign up and sign in).
- Developing profile management features (view and edit profile).
- Creating game management functionalities (view all games, view favorite games, add favorite game, remove favorite game).
- Implementing order management processes (create new order, view all orders, view recent orders, view order details).
- Developing admin functionalities (update user information, update catalog information, update tracking information).
- Ensuring data consistency and integrity across multiple tables.
- Handling user inputs securely to prevent SQL injection and other vulnerabilities.
- Managing transactions to ensure atomicity of operations.
- Optimizing SQL queries for better performance.
- Developing the Java application to interact with the database and provide a user-friendly interface for various operations.

## Problems/Findings

During the implementation, several challenges arose, particularly in maintaining data consistency and integrity across multiple tables. Cascading deletes required careful management to ensure no orphaned records were left. Additionally, handling user inputs securely to prevent SQL injection and other vulnerabilities was a priority. Managing transactions to ensure the atomicity of operations like creating orders and updating tracking information was also a critical consideration.
