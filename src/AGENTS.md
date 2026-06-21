# AGENTS.md

Guidance for AI coding agents (and humans) working in this repository. This file was used to steer the agentic development of the Food Delivery Order Management System.

## What this project is

A Spring Boot 3.5 / Java 21 / Gradle backend for a food-delivery order-management platform. Read `README.md` first for the full picture. The center of gravity is the **order state machine** and two **concurrency hotspots**: limited-stock ordering and delivery-partner assignment contention.

## Build, run, test

```bash
./gradlew bootRun     # run locally (seeds demo data unless profile=test)
./gradlew test        # unit + integration tests
./gradlew bootJar     # build runnable jar
```

- JDK **21** is required. The Gradle wrapper is pinned to **8.14.3** (Spring Boot 3.5 supports Gradle 8.x on Java 21; Gradle 9 would require Boot 4.x — do not bump the wrapper without bumping Boot).
- Tests run under the `test` Spring profile, which **disables the `DataSeeder`** so each context starts from a clean schema.

## Architecture rules (do not break these)

1. **Package-by-feature.** Each feature owns its `Controller`, DTOs, `Service`, entities, `Repository`. Cross-cutting code lives in `common`, `config`, `security`.
2. **Strict layering:** `Controller` (HTTP↔DTO only) → `Service` (`@Transactional`, business rules) → `Repository` (JPA). Controllers must not contain business logic; repositories must not contain rules.
3. **DTOs are immutable `record`s** at the web boundary. Entities never leave as request bodies; map to/from DTOs.
4. **Entities extend `BaseEntity`** for `id` + `createdAt`/`updatedAt` (JPA auditing). Don't re-declare those fields.
5. **Money is `BigDecimal`.** Never `double`/`float` for money.
6. **State transitions go through `OrderStatus`** (the legal graph) and `OrderService.applyTransition`. Add new states/edges as data in the enum — don't scatter `if` checks.
7. **Cross-module reactions use domain events**, not direct calls, to keep modules decoupled:
    - order accepted → `OrderAcceptedEvent` → delivery creates an offer;
    - order cancelled/rejected → `OrderTerminatedEvent` → payment refunds;
    - any status change → `OrderEvent` → async notification fan-out.
      The `order` module must not depend on `delivery`/`payment` at compile time.
8. **After-commit listeners that write to the DB use `@Transactional(propagation = REQUIRES_NEW)`** (synchronous listeners run while the original transaction's synchronization is still active).

## Concurrency invariants (must stay true)

- **No oversell:** stock is reserved only via the conditional atomic update `MenuItemRepository.decrementStock` (`... WHERE stock_quantity >= :qty`). A `0`-row result is a `409`. Never read-modify-write stock in Java.
- **No double assignment:** a partner claims an order only via `DeliveryAssignmentRepository.claimOffer` (`... WHERE status = OFFERED`). A `0`-row result is a `409`.
- `@Version` optimistic locking stays on `MenuItem`, `Order`, `DeliveryPartner`, `DeliveryAssignment`.

If you touch either flow, keep/extend the corresponding test (`StockConcurrencyIntegrationTest`, `DeliveryContentionIntegrationTest`).

## Error handling

- Throw the typed exceptions in `common.exception` (`ResourceNotFoundException`, `BusinessRuleException`, `ConflictException`, `InvalidStateTransitionException`). Each carries its HTTP status; `GlobalExceptionHandler` renders a uniform `ApiError`. Don't add per-controller try/catch.

## Security

- HTTP Basic, stateless. Coarse rules in `SecurityConfig`; per-operation checks via `@PreAuthorize`. Cross-entity ownership ("your own order/restaurant") is enforced inside services. No OAuth/SSO/MFA (out of scope).

## Conventions

- Constructor injection via Lombok `@RequiredArgsConstructor` (no field injection).
- Keep comments about *why*, not *what*. No narration comments.
- Validate request DTOs with Bean Validation annotations; let `MethodArgumentNotValidException` flow to the global handler.

## Testing expectations

- Pure logic (state machine) → fast unit tests, no Spring context.
- Concurrency and full flows → `@SpringBootTest` integration tests; use `TestDataFactory` (under `src/test`) to build fixtures via the real services.
- Run `./gradlew test` before considering a change done.
