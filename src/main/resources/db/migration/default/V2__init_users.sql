
CREATE TABLE IF NOT EXISTS USERS (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    USERNAME VARCHAR(255),
    PASSWORD VARCHAR(255),
    EMAIL VARCHAR(255),
    ENABLED BOOLEAN
);

