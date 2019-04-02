# Transfer Cash Rest API

A Java RESTful API for money transfers between users accounts


### How to run
mvn exec:java


Application starts a jetty server on localhost port 8080 An H2 in memory database initialized with some sample user and account data To view

### Available Services and their JSON payload

| HTTP METHOD | PATH | USAGE | PAYLOAD |
| -----------| ------ | ------ |------|
| GET | /users/ | get all users | |
| GET | /users/{userId} | get user by id | |
| GET | /users/username/{userName} | get user by name | |
| POST | /users/ | create a new user | {"userName": "Harry Potter","emailAddress": "rweasley@gmail.com"} |
| PUT | /users/{userId} | update user | {"userName": "Harry Potter1","emailAddress": "hpotter@gmail.com"} |
| DELETE | /users/{userId} | remove user | |
| GET | /accounts/ | get all accounts | |
| GET | /accounts/{accountId} | get account by id | |
| GET | /accounts/username/{userName} | get account by username | |
| GET | /accounts/{accountId}/balance | get account balance by accountId | |
| POST | /accounts/ | create a new account | {"userName": "Harry Potter1","balance": 1000,"currencyCode": "USD"} |
| DELETE | /accounts/{accountId} | remove account by accountId | |
| PUT | /accounts/{accountId}/withdraw/{amount} | withdraw money from account | |
| PUT | /accounts/{accountId}/deposit/{amount} | deposit money to account | |
| POST | /transactions | perform transaction between 2 user accounts | {"currencyCode":"USD","amount":10,"fromAccountId":1,"toAccountId":2} |




    


### Http Status
- 200 OK: The request was successful
- 400 Bad Request: The server could not understand the request
- 404 Not Found: The requested resource could not be found
- 500 Internal Server Error: An unexpected condition was encountered by the server


