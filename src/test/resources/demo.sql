--This script is used for unit test cases, DO NOT CHANGE!

DROP TABLE IF EXISTS User;

CREATE TABLE User (UserId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
 UserName VARCHAR(30) NOT NULL,
 EmailAddress VARCHAR(30) NOT NULL);

CREATE UNIQUE INDEX idx_ue on User(UserName,EmailAddress);

INSERT INTO User (UserName, EmailAddress) VALUES ('Harry Potter','hpotter@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('Ronald Weasley','rweasley@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('Hermione Granger','hgranger@gmail.com');

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
UserName VARCHAR(30) NOT NULL,
Balance DECIMAL(19,4),
CurrencyCode VARCHAR(30)
);

CREATE UNIQUE INDEX idx_acc on Account(UserName,CurrencyCode);

INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Harry Potter',100.00,'USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Ronald Weasley',200.00,'USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Hermione Granger',300.00,'USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Harry Potter',200.00,'EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Ronald Weasley',300.00,'EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Hermione Granger ',100.00,'EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Harry Potter',300.00,'GBP');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Ronald Weasley',200.00,'GBP');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('Hermione Granger',100.00,'GBP');


DROP TABLE IF EXISTS UserTransaction;

CREATE TABLE UserTransaction (TransactionId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
FromAccountId LONG,
ToAccountId LONG,
Amount DECIMAL(19,4),
CurrencyCode VARCHAR(30),
TransactionDate TIMESTAMP);