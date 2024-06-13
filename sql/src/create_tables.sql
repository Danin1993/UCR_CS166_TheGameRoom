DROP TABLE IF EXISTS Users CASCADE;
DROP TABLE IF EXISTS Catalog CASCADE;
DROP TABLE IF EXISTS RentalOrder CASCADE;
DROP TABLE IF EXISTS TrackingInfo CASCADE;
DROP TABLE IF EXISTS GamesInOrder CASCADE;

CREATE TABLE Users ( login varchar(50) NOT NULL,
                     password varchar(30) NOT NULL,
                     role char(20) NOT NULL,
                     favGames text,
                     phoneNum varchar(20) NOT NULL,
                     numOverDueGames integer DEFAULT 0,
                     PRIMARY KEY(login)
);

CREATE TABLE Catalog ( gameID varchar(50) NOT NULL,
                       gameName varchar(300) NOT NULL,
                       genre varchar(30) NOT NULL,
                       price decimal(10,2) NOT NULL,
                       description text,
                       imageURL varchar(20),
                       PRIMARY KEY(gameID)
);

CREATE TABLE RentalOrder ( rentalOrderID varchar(50) NOT NULL,
                           login varchar(50) NOT NULL,
                           noOfGames integer NOT NULL,
                           totalPrice decimal(10,2) NOT NULL,
                           orderTimestamp timestamp NOT NULL,
                           dueDate timestamp NOT NULL,
                           PRIMARY KEY(rentalOrderID),
                           FOREIGN KEY(login) REFERENCES Users(login)
                           ON DELETE CASCADE
);

CREATE TABLE TrackingInfo ( trackingID varchar(50) NOT NULL,
                           rentalOrderID varchar(50) NOT NULL,
                           status varchar(50) NOT NULL,
                           currentLocation varchar(60) NOT NULL,
                           courierName varchar(60) NOT NULL,
                           lastUpdateDate timestamp NOT NULL,
                           additionalComments text,
                           PRIMARY KEY(trackingID),
                           FOREIGN KEY(rentalOrderID) REFERENCES RentalOrder(rentalOrderID)
                           ON DELETE CASCADE
);

CREATE TABLE GamesInOrder ( rentalOrderID varchar(50) NOT NULL,
                           gameID varchar(50) NOT NULL,
                           unitsOrdered integer NOT NULL,
                           PRIMARY KEY(rentalOrderID, gameID),
                           FOREIGN KEY(rentalOrderID) REFERENCES RentalOrder(rentalOrderID) ON DELETE CASCADE,
                           FOREIGN KEY(gameID) REFERENCES Catalog(gameID)
                           ON DELETE CASCADE
);
