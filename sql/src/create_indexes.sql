-- Drop existing indexes if they exist
DROP INDEX IF EXISTS idx_users_phoneNum;
DROP INDEX IF EXISTS idx_catalog_gameName;
DROP INDEX IF EXISTS idx_catalog_genre;
DROP INDEX IF EXISTS idx_rentalOrder_login;
DROP INDEX IF EXISTS idx_trackingInfo_rentalOrderID;
DROP INDEX IF EXISTS idx_gamesInOrder_rentalOrderID;
DROP INDEX IF EXISTS idx_gamesInOrder_gameID;

-- Indexes for Users table
CREATE INDEX idx_users_phoneNum ON Users(phoneNum);

-- Indexes for Catalog table
CREATE INDEX idx_catalog_gameName ON Catalog(gameName);
CREATE INDEX idx_catalog_genre ON Catalog(genre);

-- Indexes for RentalOrder table
CREATE INDEX idx_rentalOrder_login ON RentalOrder(login);

-- Indexes for TrackingInfo table
CREATE INDEX idx_trackingInfo_rentalOrderID ON TrackingInfo(rentalOrderID);

-- Indexes for GamesInOrder table
CREATE INDEX idx_gamesInOrder_rentalOrderID ON GamesInOrder(rentalOrderID);
CREATE INDEX idx_gamesInOrder_gameID ON GamesInOrder(gameID);
