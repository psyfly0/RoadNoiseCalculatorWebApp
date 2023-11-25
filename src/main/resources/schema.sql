DROP TABLE IF EXISTS DRINK;
DROP TABLE IF EXISTS HUMAN;

DROP TABLE IF EXISTS ROLES_PRIVILEGES;
DROP TABLE IF EXISTS USERS_ROLES;
DROP TABLE IF EXISTS PRIVILEGE;
DROP TABLE IF EXISTS ROLE;
DROP TABLE IF EXISTS USERS;

DROP TABLE IF EXISTS DBF_DATA;





CREATE TABLE HUMAN(id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), kilogram INT, alcohol_gram DOUBLE);
CREATE TABLE DRINK(id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), ml INT, percent DOUBLE, human_id INT);
CREATE TABLE DBF_DATA(
	id INT PRIMARY KEY AUTO_INCREMENT, 
	identifier INT,
    speed1 INT,
    speed2 INT,
    speed3 INT,
    acousticCatDay1 INT,
    acousticCatDay2 INT,
    acousticCatDay3 INT,
    acousticCatNight1 INT,
    acousticCatNight2 INT,
    acousticCatNight3 INT,
	speed1R INT,
    speed2R INT,
    speed3R INT,   
    acousticCatDay1R INT,
    acousticCatDay2R INT,
    acousticCatDay3R INT,
    acousticCatNight1R INT,
    acousticCatNight2R INT,
    acousticCatNight3R INT
);

CREATE TABLE Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE Role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Privilege (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE users_roles (
    user_id INT,
    role_id INT,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (role_id) REFERENCES Role(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE roles_privileges (
    role_id INT,
    privilege_id INT,
    FOREIGN KEY (role_id) REFERENCES Role(id),
    FOREIGN KEY (privilege_id) REFERENCES Privilege(id),
    PRIMARY KEY (role_id, privilege_id)
);
