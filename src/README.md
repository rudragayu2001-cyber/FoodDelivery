Flow
order place -> accepted -> preoaring -> out-for-delivery -> delivered



- Restaurant management Service = > manages inventory, restaurants, menu's,
- order management Service => manages order cycle
- Payment management Service =>
- Delivery management Service => delivery partner assignment
- Customer management Service =>



Controller layer -> dto layer -> service layer -> exception layer -> entity layer -> repository layer


Functional Requirements
1) customer browses based on location and menu adn restaurant
2) customer selects menu in a restaurant adds to cart and checks out 
3) deliver charges are charged based on the location of the restaurant
4) customer chooses payment option and pays for the ordered item
5) Concurrent orders for the same menu item should not oversell limited stock, 
6) Once payment is done Restaurant receive's the order and we start assigning a delivery partner 
7) Restaurant provides estimated time to cook the food to the delivery agent 
8) Delivery partner accepts to deliver the order and and partner assignment should
   handle multiple partners contending for the same order. 
9) customer sees delivery partner assigned on app 
10) Delivery partner picks up and delivers to customer 
11) Order Completed
12) Status updates should fan out asynchronously to customer, restaurant, and 
    delivery partner without blocking the calling flow. 
13) Ratings and reviews after delivery should be supported.


Entities Involved
- User -
- Customer 
- Delivery Partner -
- Location -
- Restaurant -
- MenuItem -
- CartItem 
- Order -
- OrderItem -
- Payment -
- DeliveryAssignment -  
- Review -
- Notification -


Enitity Observation

![Untitled-2026-06-19-2351.png](Untitled-2026-06-19-2351.png)
As createdAt and UpdatedAt fields are repetetive we can create a common baseClass for these fields instead of defining it many times

- **Restaurant management** — cities, restaurants, menus & inventory (`RestaurantService`, `MenuService`, `CityService`, `BrowseService`)
- **Customer management** — registration, cart (`UserService`, `CustomerService`, `CartService`)
- **Order management** — order lifecycle & state machine (`OrderService`)
- **Payment management** — simulated payment + atomic order placement (`PaymentService`)
- **Delivery management** — assignment contention & delivery status (`DeliveryService`)
- **Notifications** — async status fan-out (`event/*`, `NotificationService`)


Order and Delivery Flow : 
```
PENDING_PAYMENT --pay--> PLACED --accept--> ACCEPTED --prepare--> PREPARING --pickup--> OUT_FOR_DELIVERY --deliver--> DELIVERED
|                   |  \--reject--> REJECTED
\--cancel--> CANCELLED
```

## Architecture & design

The code is organized **package‑by‑feature** (`city`, `restaurant`, `menu`, `order`,
`payment`, `delivery`, `review`, `notification`, `user`, `auth`), with cross‑cutting
concerns under `common`, `config` and `security`. Each feature follows a layered shape:

```
Controller  ->  Service (transaction + business rules)  ->  Repository (JPA)
   DTOs            domain entities                            atomic SQL
```

SOLID in practice:

- **SRP** – controllers only translate HTTP↔DTO; services own business rules and
  transactions; repositories own persistence. The order *state machine* lives in the
  `OrderStatus` enum, separate from the service that applies it.
- **OCP** – adding an order state or a new notification channel doesn't require changing
  existing transition callers (transitions are data in the enum; notifications listen to
  events).
- **LSP/ISP** – small, focused repository interfaces; DTOs are immutable records.
- **DIP** – services depend on repository/abstraction interfaces and an
  `ApplicationEventPublisher`, not concrete infrastructure.

## Domain model

```
User (ADMIN | RESTAURANT_OWNER | CUSTOMER | DELIVERY_PARTNER)
City  1───* Restaurant  1───* MenuItem (price, stockQuantity, @Version)
Customer 1───* Order ───1 Restaurant
Order 1───* OrderItem ───* MenuItem (snapshots name & price at order time)
Order 1───1 Payment (amount, method, status)
Order 1───1 DeliveryAssignment ───* DeliveryPartner(User)
DeliveryPartnerProfile (availability, operating city)
Order 1───1 Review (foodRating, deliveryRating, comment)
Notification ───* User (recipient)
```

Key modelling decisions:

- **Order items snapshot** the item name and unit price, so later menu/price changes
  never alter historical orders.
- **Money** uses `BigDecimal` throughout.
- **Optimistic locking** (`@Version`) on `MenuItem` and `Order` guards against lost
  updates on edits/transitions.