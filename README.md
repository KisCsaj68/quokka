# Quokka Trading Platform


## High level overview


Quokka Trading Platfo®m is a minimal viable product (MVP) for trading. 
It is capable of:

- User management
    - registration
    - authentication
- Order management
  - creation of
    - limit order, filling upon reaching the provided price
    - market orders, filling at the current price
  - deletion of pending limit orders
- Asset management
  - registering an asset for price tracking
  - registering an order for price tracking
  - connects to an external data provider for tracking the price for the requested assets

Under the hood, Quokka Trading Platfo®m has a high performance order management engine.
It's mission critical state is handled fully in-memory, while every change of that state will eventually get persisted.


## Feature progress:
### Architecture
- [x] microservice components
- [x] microservice communication (RESTApi, RabbitMQ)


### Infra/Misc
- [x] Makefile for automating more complex housekeeping tasks
- [x] fast multistage dockerbuilds with caching enabled
- [ ] helm-chart to run on k8s
- [ ] Logging
- [ ] Log shipping (ELK stack)
- [ ] Visibility
    - [ ] prometheus metrics
    - [ ] grafana dashboard



### User management
- [x] User registration
- [ ] Change password
- [x] Login / Logout
- [x] JWT based authentication / authorization
- [x] Protected routes

### Finance management
- [x] Market order
- [ ] Limit order
- [x] Open Position
- [ ] Sell position
- [ ] Cash deposit - CSD
- [ ] Cash withdrawal - CSW

### Asset management
- [x] API request based connection to Alpaca
- [ ] Stream based connection to Alpaca
- [ ] Price trackers for limit order settlement

### Persistance
- [x] write-ahead log persistent in RabbitMQ
- [x] persister component saves data into relational DB
- [ ] reload pending orders from DB into in-memory on app start
- [ ] keep and serve pending orders from in-memory

### Fronend
- [x] Minimal frontend
- [x] Login
- [ ] Logout
- [ ] Responsive web design Tailwind


## Architectural overview

Quokka Trading Platfo®m has three tiers:

- Frontend
- Backend
- Persistence

All components are dockerized, and docker-compose is used to make it easy to start the _myriad_ of components.

### Frontend
Frontend is implemented using React.

### Backend
Quokka Trading Platfo®m's backend follows the microservice architecture.
There are 4 backend components separated by their main distinct responsibilities:
- API is responsible for exposing account-facing functionalities through RESTful API
- Order Management System (OMS) is responsible for all tracking all trade related actions
- Asset cache is responsible for:
  - providing RESTful APIs for asset related functionalities
  - subscribing to an external streaming data provider
  - for notifying OMS if a limit order's price has been met
- Persister is responsible for persisting any message it consumes through its queue


All of our backend components are written in Java, except asset cache, which is written in python, since the external data provider has no Java SDK.

### Persistence
Persistence is implemented using durable RabbitMQ queues and Postgres relational database.

## Tech stack

Frontend:
  - React
  - Bootstrap (???)
Backend:
  - Springboot & spring security
  - JPA, Postgres
  - python
  - Docker and Docker-compose (maybe Helm when run on k8s if time permits)
  - RabbitMQ
  - gRPC


### Spring profiles
There are two spring profiles available. the default, and a dev profile.
If you want to run the application in your IDE locally, please activate the `dev` profile (like this: https://www.baeldung.com/spring-profiles#4-jvm-system-parameter or any other preferred ways)
