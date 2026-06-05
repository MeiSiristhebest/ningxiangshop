# GEMINI.md - The Ningxiang Go Chronicle

Project-level Persistent Memory for Google Antigravity.

---

## [2026-06-04] Rebranding & Framework Upgrades
- **Decision**: Renamed package `com.mall4j.cloud` to `com.ningxiang.shop`, renamed all microservice modules, and upgraded build system to JDK 21 + Spring Boot 3.3.0 + Spring Cloud 2023.0.1.
- **Reason**: Standardize the codebase for brand-new personalized portfolio showcase.
- **TODO**: Verify compatibility of other third-party dependencies in newer Spring Boot 3.3 release.

## [2026-06-04] Sa-Token & Gateway Decentralized Auth
- **Decision**: Integrated `sa-token-spring-boot3-starter` and shifted token verification to网关 (ningxiang-gateway), passing user context down in `x-user-info` HTTP header.
- **Reason**: Reduced internal auth Feign RPC traffic to 0, preventing bottleneck on auth microservice.

## [2026-06-04] Multilevel Cache Synchronization
- **Decision**: Built a custom dynamic `CacheManager` with Caffeine as L1 cache and Redis as L2 cache, synchronized across microservice instances via RocketMQ broadcasting (`MessageModel.BROADCASTING`).
- **Reason**: Eliminate network I/O overhead on Redis for hot products, while keeping cached data consistent.

## [2026-06-05] High Concurrency Distributed Locks & Deadlock Prevention
- **Decision**: Sort SKU IDs in ascending order prior to lock acquisition, and lock them sequentially using Redisson (`RLock`) without explicit lease times to trigger Watchdog auto-renewal.
- **Reason**: Prevent distributed deadlocks (Distributed Deadlocks) when concurrent users buy identical products in different orders. Keep lock valid as long as business transaction is running.

## [2026-06-05] General AOP Idempotency framework
- **Decision**: Implemented `@Idempotent` custom annotation and SpEL-parsing aspect using Redis `SETNX` (token based processing/success status machine).
- **Reason**: Insulate critical handlers (e.g., RocketMQ payment notification consumers) against network-induced duplicate deliveries.

## [2026-06-05] Sentinel Dynamic Flow Control & Fallback
- **Decision**: Added Sentinel rate limiting to core stock lock APIs with fallback JSON responses and configured Nacos as Sentinel dynamic rules datasource.
- **Reason**: Protect microservices against traffic spikes and persist rule configurations across restarts.
