# Cinema Tickets DWP Exercise

The following is my submission for the DWP coding exercise.

---
## Features

### Three ticket types:

* Adult (£25, requires a seat)
* Child (£15, requires a seat, must be purchased with an adult ticket)
* Infant (£0, no seat, must be purchased with an adult ticket)

### Validates purchase rules:

* At least one adult must be present for children or infants
* No more than 25 tickets per purchase
* No invalid or null requests
* No zero or negative ticket counts
* Calculates correct payment amount and calls external TicketPaymentService
* Descriptive exception messages for invalid requests

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.6+

### Running Tests

1. Clone the project, make sure you're in **cinema-tickets-java** and run:

   ```
    mvn clean test
   ```
