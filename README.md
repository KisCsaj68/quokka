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

- Postman Collection for API testing purpose

Under the hood, Quokka Trading Platfo®m has a high performance order management engine.
It's mission critical state is handled fully in-memory, while every change of that state will eventually get persisted.

## Grafana dashboards:
## OMS
### Handle Market order(rate, duration)
![market_order](./prometheus/images/oms_market_order.png)
### Limit order(rate, duration)
![market_order](./prometheus/images/oms_limit_order.png)

## Assetcache
### Read_Write in-memory storage
![market_order](./prometheus/images/assetcache_r_w.png)

### Handle price tracker(case of limit orders)
![market_order](./prometheus/images/assetcache_price_tracker.png)

## Feature progress:
### Architecture
- [x] microservice components
- [x] microservice communication (RESTApi, RabbitMQ)


### Infra/Misc
- [x] Makefile for automating more complex housekeeping tasks
- [x] fast multistage dockerbuilds with caching enabled
- [x] Postman collection (in Git)
- [ ] helm-chart to run on k8s
- [ ] Logging
- [ ] Log shipping (ELK stack)
- [x] Visibility
    - [x] prometheus metrics
    - [x] grafana dashboard



### User management
- [x] User registration
- [ ] Change password
- [x] Login / Logout
- [x] JWT based authentication / authorization
- [x] Protected routes

### Finance management
- [x] Market order
- [x] Limit order
- [x] Open Position
- [x] Sell position
- [ ] Cash deposit - CSD
- [ ] Cash withdrawal - CSW
- [ ] Accounting/balance tracking

### Asset management
- [x] API request based connection to Alpaca
- [x] Stream based connection to Alpaca
- [x] Price trackers for limit order settlement

### Persistance
- [x] write-ahead log persistent in RabbitMQ
- [x] persister component saves data into relational DB
- [x] reload pending orders from DB into in-memory on app start
- [x] keep and serve pending orders from in-memory

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
  - Bootstrap

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


### To run the application
After you cloned the github repository, you need to do some initial setup. First you need to create an account to be able to receive data from Alpaca streaming.<br>Please register here: Create your Trading API account After registration you will receive the following important data: APCA_API_KEY_ID, APCA_API_SECRET_KEY.
<br>You need to create a ".env" file in the assetcache/src folder with the following data:<br>
export APCA_API_KEY_ID={ID you received} <br>
export APCA_API_SECRET_KEY={secret key you received} <br>
export APCA_API_BASE_URL="https://paper-api.alpaca.markets/" <br>
export APCA_API_VERSION={actual version} <br>
export APCA_API_STREAM_URL="https://stream.data.alpaca.markets" <br>
export RABBITMQ_URL="amqp://guest:guest@rabbitmq:5672" <br>
export EXCHANGE="oms_exchange" <br>
export ROUTING_KEY="filled_order_routing_key" <br>
export QUEUE="filled_order_queue" <br>
export PROMETHEUS_MULTIPROC_DIR="/tmp/metrics" <br>

Those setup will allows you to connect to Alpaca streaming, and use the predefined RabbitMQ queues in the application.

Adjust `spring.jpa.hibernate.ddl-auto = create` in one of the component's application.properties to auto create the DB schema. When schema is created, change it back to `none` or `validate`.
