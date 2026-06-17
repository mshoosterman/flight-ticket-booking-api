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

Required fields:

- `flightNumber`
- `passengerName`
- `passengerEmail` as a valid email address
- `seatCount` from `1` to `1000`

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

Response: `409 Conflict`

```json
{
  "error": "Not enough seats available"
}
```

## What I Would Improve With More Time
#### If this project had a greater scope, or was expected to take more time to complete these are the biggest changes I would make:

- [ ] Use a SQL database for persistent storage for flights and bookings so data survives application restarts.
- [ ] Add Swagger/OpenAPI support.
- [ ] Add booking retrieval endpoints, such as looking up a booking by ID.
- [ ] Add flight management or flight search.
- [ ] Add mapping logic to book shortest or cheepest series of flights between destinations.
- [ ] Add authentication.
- [ ] Add stronger concurrency testing around simultaneous booking attempts.
- [ ] Add observability basics, such as request logging, metrics, and health checks.

#### Another consideration for this project was to potentially use graphQL rather than REST API, althought this was decided against since REST API was specifically requested.
