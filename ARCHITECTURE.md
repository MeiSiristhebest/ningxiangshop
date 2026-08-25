# 🏗️ Ningxiang Go Microservices Architecture Guide

<p align="center">
  <b>English | <a href="./ARCHITECTURE_zh.md">简体中文</a></b>
</p>

This document details the high-concurrency microservices architecture and high-availability engineering practices powering **Ningxiang Go**.

```mermaid
graph TD
    Client[Client App / Vue 3 Frontend] -->|HTTPS / WSS| Gateway[Spring Cloud Gateway Cluster]
    
    subgraph "Edge Gateway Layer"
        Gateway -->|Zero-RPC Auth / Fast JWT Verify| Auth[Local JWT Verification + Redis Token Cache]
        Gateway -->|Sentinel Flow Control / Circuit Breaking| Sentinel[Sentinel Distributed Limiter]
    end
    
    subgraph "Core Microservices (Java 21 LTS)"
        Gateway -->|Load Balancing| UserSvc[User Service]
        Gateway -->|Virtual Threads| ProductSvc[Product Service]
        Gateway -->|Distributed Tx| OrderSvc[Order Service]
        Gateway -->|Anti-Overselling| SecKillSvc[Flash Sale Service]
    end
    
    subgraph "Multi-Level Caching & Streaming"
        SecKillSvc -->|L1 In-Memory Cache| Caffeine[Caffeine Local Cache]
        SecKillSvc -->|L2 Distributed Cache| Redis[(Redis Cluster + Redisson)]
        OrderSvc -->|Async Peak Shaving| RocketMQ[RocketMQ 5.0 Message Queue]
    end
    
    subgraph "Data Persistence"
        OrderSvc -->|Sharding / Read-Write Split| MySQL[(MySQL 8.0 Master-Slave Cluster)]
        RocketMQ -->|Eventual Consistency Tx| MySQL
    end
```

---

## ⚡ 1. Java 21 Global Virtual Threads
Replaces traditional platform thread pools with Java 21 virtual threads, boosting I/O concurrency throughput by over 300% without reactive callback complexity.

---

## 🛡️ 2. Multi-Tier Anti-Overselling Safeguards
1. **L1 In-Memory Fast Path (Caffeine)**: Immediate in-memory bitset intercept once stock reaches 0, zero external network roundtrips.
2. **L2 Redis + Lua Scripts**: Atomic stock deduction and token-bucket rate limiting.
3. **L3 Redisson Distributed Locks (Watchdog)**: Automatic lock renewal and fair locks under heavy contention.

---

## 🚀 3. Multi-Level Cache Consistency Architecture
- **Read Path**: L1 Caffeine ➡️ L2 Redis ➡️ MySQL.
- **Write Path**: Update MySQL ➡️ Invalidate Redis ➡️ RocketMQ broadcast to invalidate local Caffeine caches on all service nodes.

---

<sub>© 2026 Ningxiang Go. Licensed under the MIT License.</sub>
