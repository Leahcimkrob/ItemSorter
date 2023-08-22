CREATE TABLE IF NOT EXISTS ItemSorterDB(
    schema_version INT,
    PRIMARY KEY(schema_version)
);

CREATE TABLE IF NOT EXISTS Systems(
    systemID INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    ownerUUID VARCHAR(100),
    ownerName VARCHAR(50) NOT NULL,
    world VARCHAR(50) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL,
    signx INT NOT NULL,
    signy INT NOT NULL,
    signz INT NOT NULL,
    UNIQUE(name)
)

CREATE TABLE IF NOT EXISTS Trusted(
    systemID INTEGER,
    trustedID INTEGER PRIMARY KEY AUTOINCREMENT,
    trustedUUID VARCHAR(100),
    trustedName VARCHAR(50) NOT NULL,
    FOREIGN KEY(systemID) REFERENCES Systems(systemID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Filters(
    systemID INTEGER,
    filterID INTEGER PRIMARY KEY AUTOINCREMENT,
    trash LOGICAL NOT NULL,
    trashPriority INT NOT NULL,
    world VARCHAR(50) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL,
    signx INT NOT NULL,
    signy INT NOT NULL,
    signz INT NOT NULL,
    FOREIGN KEY(systemID) REFERENCES Systems(systemID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Materials(
    systemID INTEGER,
    filterID INTEGER,
    material VARCHAR(50) NOT NULL,
    priority INT NOT NULL,
    PRIMARY KEY(systemID, filterID, material),
    FOREIGN KEY(systemID, filterID) REFERENCES Filters(systemID, filterID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Deposits(
    systemID INTEGER,
    depositID INTEGER PRIMARY KEY AUTOINCREMENT,
    world VARCHAR(50) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL,
    signx INT NOT NULL,
    signy INT NOT NULL,
    signz INT NOT NULL,
    FOREIGN KEY(systemID) REFERENCES Systems(systemID) ON DELETE CASCADE
);

INSERT INTO ItemSorterDB (schema_version) values (5);

SELECT schema_version FROM ItemSorterDB ORDER BY schema_version DESC LIMIT 1;

INSERT INTO Trusted (systemID, trustedUUID, trustedName) VALUES (1);



INSERT INTO Systems (name, ownerUUID, ownerName, world, x, y, z, signx, signy, signz) VALUES ('test', null, 'clcon', 'www', 1, 2, 3, 4, 5, 6);
INSERT INTO Systems (name, ownerUUID, ownerName, world, x, y, z, signx, signy, signz) VALUES ('test2', null, 'clcon2', 'www2', 1, 2, 3, 4, 5, 6);

INSERT INTO Trusted (systemID, trustedUUID, trustedName) VALUES (1, null, "HAHAHAHAHA");

INSERT INTO Filters (systemID, trash, trashPriority, world, x, y, z, signx, signy, signz) VALUES (1, false, 3, 'www', 1, 2, 3, 4, 5, 6);
INSERT INTO Filters (systemID, trash, trashPriority, world, x, y, z, signx, signy, signz) VALUES (2, false, 3, 'www2', 1, 2, 3, 4, 5, 6);

INSERT INTO Materials (systemID, filterID, material, priority) VALUES (1, 1, 'CUIR', 1);
INSERT INTO Materials (systemID, filterID, material, priority) VALUES (2, 2, 'CUIR', 1);

