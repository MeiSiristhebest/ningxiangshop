# 🏗️ Ningxiang Go 分布式微服务架构设计文档 (Architecture Guide)

<p align="center">
  <b><a href="./ARCHITECTURE.md">English</a> | 简体中文</b>
</p>

本文档阐述 **Ningxiang Go (宁乡购)** 在亿级流量与高并发秒杀场景下的微服务架构推演与高可用工程实践。

```mermaid
graph TD
    Client[客户端 App / Vue 3 前端] -->|HTTPS / WSS| Gateway[Spring Cloud Gateway 网关集群]
    
    subgraph "网关层 (Edge Layer)"
        Gateway -->|无 RPC 鉴权 / JWT 快速验签| Auth[本地验签 + Redis Token 校验]
        Gateway -->|Sentinel 规则限流 / 熔断| Sentinel[分布式熔断限流中心]
    end
    
    subgraph "核心微服务集群 (Core Services - Java 21 LTS)"
        Gateway -->|负载均衡| UserSvc[用户服务 (User Service)]
        Gateway -->|虚拟线程并发处理| ProductSvc[商品服务 (Product Service)]
        Gateway -->|分布式事务调度| OrderSvc[订单服务 (Order Service)]
        Gateway -->|防超卖高可用引擎| SecKillSvc[秒杀服务 (Flash Sale Service)]
    end
    
    subgraph "多级缓存与异步流 (Caching & Streaming)"
        SecKillSvc -->|L1 进程内缓存| Caffeine[Caffeine Local Cache]
        SecKillSvc -->|L2 分布式缓存| Redis[(Redis Cluster + Redisson)]
        OrderSvc -->|异步削峰解耦| RocketMQ[RocketMQ 5.0 消息中间件]
    end
    
    subgraph "持久化与数据存储 (Data Persistence)"
        OrderSvc -->|分库分表 / 读写分离| MySQL[(MySQL 8.0 主从集群)]
        RocketMQ -->|最终一致性事务回落| MySQL
    end
```

---

## ⚡ 1. Java 21 全局虚拟线程 (Virtual Threads)
传统 Tomcat 线程池在面对上万并发 I/O 阻塞（如远程 RPC、数据库查询）时容易耗尽平台线程。本项目全面启用 Java 21 虚拟线程，将并发吞吐量提升 300% 以上，同时保持同步阻塞编码风格。

---

## 🛡️ 2. 防超卖多级防线
1. **L1 本地内存标记 (Caffeine)**：秒杀结束直接在本地内存置位拦截，零外部网络 I/O。
2. **L2 Redis + Lua 脚本原子扣减**：分布式库存原子扣减与令牌桶限流。
3. **L3 Redisson 分布式锁 (Watchdog)**：针对热点库存争抢的自动续期可重入锁，杜绝死锁与超卖。

---

## 🚀 3. 多级缓存一致性架构
- **读路径**：L1 Caffeine 缓存 ➡️ L2 Redis 缓存 ➡️ 数据库。
- **写路径**：更新数据库 ➡️ 删除 Redis 缓存 ➡️ RocketMQ 广播通知各微服务实例失效 L1 Caffeine 缓存。

---

<sub>© 2026 Ningxiang Go. Licensed under the MIT License.</sub>
