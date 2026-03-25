DROP DATABASE IF EXISTS librarydemo;
CREATE DATABASE librarydemo;

CREATE USER IF NOT EXISTS 'userLibrary'@'localhost' IDENTIFIED BY 'userLibraryPW';
GRANT SELECT, INSERT, UPDATE, DELETE ON librarydemo.* TO 'userLibrary'@'localhost';
FLUSH PRIVILEGES;