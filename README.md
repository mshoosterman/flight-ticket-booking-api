# Flight Ticket Booking API

Minimal Spring Boot REST API for creating and cancelling flight ticket bookings. The API assumes clients already know the flight number; it does not provide flight search, booking retrieval, authentication, or database persistence.

## Tech Stack

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Jakarta Bean Validation
- Maven

## Seeded Flights

The service starts with these hard-coded flights:

| Flight number | Capacity |
| --- | ---: |
| `FL100` | 2 |
| `FL200` | 3 |
| `FL300` | 1 |

## Storage

All flights and bookings are stored in memory. Data is reset whenever the application restarts.

## Overbooking Prevention

Booking creation checks the requested `seatCount` against the remaining capacity for the requested flight. The capacity check and seat reservation happen inside a synchronized service method so a booking is only stored after seats are confirmed as available.

## Run the Service

```bash
./mvnw spring-boot:run
```

The service runs on `http://localhost:8080` by default.

## Run Tests

```bash
./mvnw test
```

## Create a Booking

```bash
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{
    "flightNumber": "FL100",
    "passengerName": "Alex Morgan",
    "passengerEmail": "alex@example.com",
    "seatCount": 1
  }'
```

Successful response:

```json
{
  "bookingId": "2e0a76bd-e128-4652-bc2c-74db1e4fe780",
  "flightNumber": "FL100",
  "passengerName": "Alex Morgan",
  "passengerEmail": "alex@example.com",
  "seatCount": 1
}
```

## Cancel a Booking

```bash
curl -i -X DELETE http://localhost:8080/api/bookings/2e0a76bd-e128-4652-bc2c-74db1e4fe780
```

Successful cancellation returns `204 No Content`.

## Example Overbooking Error

```bash
curl -i -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{
    "flightNumber": "FL300",
    "passengerName": "Alex Morgan",
    "passengerEmail": "alex@example.com",
    "seatCount": 2
  }'
```

Response:

```json
{
  "error": "Not enough seats available"
}
```

## What I Would Improve With More Time

- [ ] Add item here.
- [ ] Add item here.
- [ ] Add item here.
