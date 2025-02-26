# Web Server for Betting Stakes

## Overview

This project implements an HTTP-based backend that allows customers to:

- Obtain a session key to place bets.
- Submit stakes for a betting offer.
- Retrieve the highest stakes per betting offer.

## Running the JAR

This project is compiled as a JAR (Java Archive) file. To start the server, run:

```
java -jar betting-stake-api.jar
```

The server starts on port 8001.

## API Usage

### 1. Create/Get a Session

```
curl -X GET http://localhost:8001/1234/session
```

Example Response:

```
4e05faf
```

- Sessions last 10 minutes.
- Multiple calls within this period will return the same session key.

### 2. Place a Stake

```
curl -X POST "http://localhost:8001/888/stake?sessionkey=4e05faf" -d "5000"
```

- Only requests with a valid session key are processed.
- Customers can place multiple stakes, but only the highest per customer is stored.
- Only the top 20 stakes per betting offer are maintained

### 3. Retrieve High Stakes

```
curl -X GET "http://localhost:8001/888/highstakes"
```

Example Response:

```
1234=5000
```

- Returns the top 20 highest stakes in descending order.
- Each customer appears only once.

## Design Choices

### 1. Session Management

- Sessions are stored in memory using a Concurrent Hash Map, where the key is customer ID and the value is the most recent session key.
- Each session is valid for 10 minutes, tracked via a timestamp.
- The expiry time of each session key is recorded in another map.
- A reverse map is used for looking up customer ID by session key.

### 2. Data Structure for Storing Stakes

- A nested Concurrent Hash Map is used to store stakes
	- Key: Bet offer ID.
	- Value: Priority Queue of stake entries (customer id -> stake)
- Only top 20 stakes per betting offer are stored, optimizing time and space complexity.

### 3. Usage of Java Record

Instead using a static inner class, I defined:

```
private static record StakeEntry(int customerId, int stake) {}
```
It keeps the data immutable and more memory-efficient, improves readability and simplifies the implementation.

## Performance

- Used Concurrent Hash Map for thread safety.
- Java's built-in HTTP server has a thread pool to handle concurrent requests efficiently.
- O(n) time complexity for adding stakes.
- O(20*log20) = O(1) time for retrieving highest stakes.

## Conclusion

This implementation meets the functional and non-functional requirements while being efficient, thread-safe, and scalable. Using Java's built-in HTTP server and optimized data structures, it ensures smooth handling of concurrent requests.

## Developed By

Yifan Meng