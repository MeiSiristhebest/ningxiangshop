# Ningxiang Go (宁享购) Enterprise Microservices E-Commerce System

<p align="center">
  [![License](https://img.shields.io/badge/License-AGPL_v3-blue.svg?style=for-the-badge)](LICENSE)
  [![Java 21](https://img.shields.io/badge/Java-21_LTS-orange.svg?style=for-the-badge)](https://openjdk.org/)
  [![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-green.svg?style=for-the-badge)](https://spring.io/projects/spring-boot)
  [![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-Alibaba_2023.0.1.0-red.svg?style=for-the-badge)](https://spring.io/projects/spring-cloud)
</p>

<p align="center">
  <a href="README.md">🇨🇳 中文</a> &nbsp;|&nbsp; <a href="README_EN.md">🇺🇸 English</a>
</p>

---

<p align="center">
    <strong>Enterprise Microservices E-Commerce · Java 21 + Spring Boot 3 + Spring Cloud Alibaba + Vue 3 · Production-Grade for 1M+ QPS</strong>
</p>

## 📑 Table of Contents

- [📖 Introduction](#-introduction)
- [🛠️ Microservice Core Architecture & Engineering Design](#️-microservice-core-architecture--engineering-design)
- [🛠️ Microservice Module Breakdown](#️-microservice-module-breakdown-comningxiangshop)
- [📊 Core Technology Stack Matrix](#-core-technology-stack-matrix)
- [📦 Compilation & Packaging Verification](#-compilation--packaging-verification)
- [🏃 Quick Start Guide](#-quick-start-guide)
- [🤝 Contributing](#-contributing)
- [🔒 Security](#-security)
- [📜 License](#-license)

---

## 📖 Introduction

**Ningxiang Go (宁享购)** is a production-grade distributed microservices e-commerce system built on **Java 21 (LTS)**, **Spring Boot 3.3**, **Spring Cloud Alibaba 2023**, and **Vue 3**.

Engineered specifically for high-concurrency environments handling 1,000,000+ QPS stress scenarios, the system encapsulates production-level backend best practices: API gateway security offloading, annotation-driven multi-level caching consistency, anti-overselling distributed locking with watchdog auto-renewal, custom SpEL-based AOP idempotency protection, and Sentinel fault-tolerance. Global compilation and packaging verification (`BUILD SUCCESS`) are fully passed, providing 100% containerization-ready deployment productivity.

---

## 🛠️ Microservice Core Architecture & Engineering Design

This section highlights the technical selection, architectural trade-offs, and engineering solutions implemented in Ningxiang Go. Click any source code link below to inspect exact implementation details:

### 1. Gateway Security Offloading & Zero-RPC Authentication 🛡️

*   **Architectural Evolution**: Refactored the traditional pattern where every microservice issues Feign RPC calls to the auth center for token validation. All authentication filters are unified at the API Gateway layer (Reactive Reactor filters). Upon token validation, the gateway extracts Sa-Session user data cached in Redis, serializes it into JSON, URL-encodes it, and passes it downstream via the `x-user-info` HTTP header. Business microservices simply decode this header, **reducing internal RPC authentication overhead to exactly zero**.
*   **Sequence Diagram**:
```mermaid
sequenceDiagram
    actor Client as Client App
    participant Gateway as "Ningxiang Gateway (ningxiang-gateway)"
    participant Auth as "Auth Center (ningxiang-auth)"
    participant Service as "Business Service (e.g., ningxiang-order)"

    Client->>Gateway: Issue HTTP request with JWT Token
    Note over Gateway: SaTokenConfig matches route rules
    Gateway-->>Gateway: SaReactorFilter intercepts & validates token
    Gateway-->>Gateway: Extract associated Sa-Session (User Data)
    Note over Gateway: GlobalAuthFilter serializes & URL-encodes user payload
    Gateway->>Service: Forward request (with x-user-info header)
    Note over Service: AuthFilter decodes header & populates ThreadLocal
    Service-->>Client: Fast response processing (0 internal Feign calls!)
```
*   **📂 Direct Source Code Links**:
    - [SaTokenConfig.java (Gateway Route Security Filter)](ningxiang-gateway/src/main/java/com/ningxiang/shop/gateway/config/SaTokenConfig.java)
    - [GlobalAuthFilter.java (Gateway User Info Serialization Filter)](ningxiang-gateway/src/main/java/com/ningxiang/shop/gateway/filter/GlobalAuthFilter.java)
    - [AuthFilter.java (Lightweight Service Header Decoding Filter)](ningxiang-common/ningxiang-common-security/src/main/java/com/ningxiang/shop/common/security/filter/AuthFilter.java)
    - [TokenStore.java (Auth Center Stateless JWT Token Manager)](ningxiang-auth/src/main/java/com/ningxiang/shop/auth/manager/TokenStore.java)
