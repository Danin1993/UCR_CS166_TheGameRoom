/* Replace the location to where you saved the data files*/
COPY Users

FROM '/home/csmajs/dnami001/Project/UCR_CS166_OnlineStoreJava/data/users.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Catalog
FROM '/home/csmajs/dnami001/Project/UCR_CS166_OnlineStoreJava/data/catalog.csv'
WITH DELIMITER ',' CSV HEADER;

COPY RentalOrder
FROM '/home/csmajs/dnami001/Project/UCR_CS166_OnlineStoreJava/data/rentalorder.csv'
WITH DELIMITER ',' CSV HEADER;

COPY TrackingInfo
FROM '/home/csmajs/dnami001/Project/UCR_CS166_OnlineStoreJava/data/trackinginfo.csv'
WITH DELIMITER ',' CSV HEADER;

COPY GamesInOrder
FROM '/home/csmajs/dnami001/Project/UCR_CS166_OnlineStoreJava/data/gamesinorder.csv'
WITH DELIMITER ',' CSV HEADER;
