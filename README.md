# 宁享购 (Ningxiang Go) 企业级微服务电商系统

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-svg)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-blue.svg?style=flat-svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-green.svg?style=flat-svg)](https://spring.io/projects/spring-cloud)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg?style=flat-svg)](#)

宁享购（Ningxiang Go）是一套基于 **Java 21**、**Spring Boot 3.3** 以及 **Vue 3** 体系构建的现代化、高性能、云原生企业级微服务 B2B2C 电商平台。项目深度践行敏捷开发与微服务最佳实践，实现了核心鉴权下沉、多级缓存一致性保障及极致的并发处理能力，是新一代数字化电商系统的理想底座。

---

## 🚀 项目核心高光与技术加分项

### 1. 基于 Sa-Token + Gateway 的统一网关前置鉴权 🛡️
*   **架构演进**：摒弃传统的“微服务每次请求都要通过 Feign 远程调用认证中心校验 Token”的落后设计，将鉴权拦截统一前置于 [ningxiang-gateway](ningxiang-gateway) 网关层（使用 `sa-token-reactor-spring-boot3-starter` 响应式版本）。
*   **零开销透传**：网关鉴权通过后，将已登录的账户 Session 数据（UserInfo）进行 JSON 序列化并进行 URL 编码，以 `x-user-info` 请求头传递给下游。业务微服务过滤器只需从请求头解码即可直接装配 `ThreadLocal` 线程上下文，**内网鉴权 RPC 交互开销降为 0**。

### 2. 注解级分布式二级缓存架构（Caffeine + Redis + MQ 广播同步）⚡
*   **极致吞吐**：在商品模块中设计了自定义的 Spring Cache 抽象实现。一级缓存为本地 JVM 内存缓存 **Caffeine**（极致响应速度），二级缓存为分布式缓存 **Redis**（保障数据持久与共享）。
*   **注解级无感集成**：自定义 `MultilevelCacheManager`，无需对业务层代码进行任何侵入式修改，原生的 `@Cacheable`、`@CacheEvict` 即可自动享受多级缓存。
*   **基于 MQ 广播的缓存强一致性**：当后台修改商品或执行缓存失效时，除了清理 Redis，还会向 RocketMQ 广播一条清除通知；集群中所有部署的微服务实例监听到广播消息后，自动擦除本地 Caffeine 缓存，**彻底解决了分布式环境下本地内存缓存不一致的痛点**。

### 3. Java 21 虚拟线程 (Virtual Threads) 全局激活
*   在网关、授权及 10 个业务微服务中全面开启了虚拟线程支持。在高 I/O 的电商交易并发场景下，Tomcat 容器会自动以极低开销的虚拟线程替换重量级物理线程池，使系统在网络并发处理能力上呈现出数量级级别的飞跃，并极大地降低了 JVM 内存开销。

---

## 🛠️ 后端微服务模块构成 (com.ningxiang.shop)

```
ningxiang
├─ningxiang-api -- 微服务间内网 RPC 声明接口
│  ├─ningxiang-api-auth       -- 授权服务 API
│  ├─ningxiang-api-biz        -- 业务支撑 API
│  ├─ningxiang-api-leaf       -- 美团分布式 ID 生成 API
│  ├─ningxiang-api-multishop  -- 商家服务 API
│  ├─ningxiang-api-order      -- 订单服务 API
│  ├─ningxiang-api-platform   -- 运营平台 API
│  ├─ningxiang-api-product    -- 商品服务 API
│  ├─ningxiang-api-rbac       -- 权限控制 API
│  ├─ningxiang-api-search     -- 搜索服务 API
│  └─ningxiang-api-user       -- 用户服务 API
├─ningxiang-auth -- 统一授权登录校验服务
├─ningxiang-biz -- 通用业务支撑服务（图片存储、短信网关等）
├─ningxiang-gateway -- 微服务统一网关（Sa-Token 响应式网关鉴权）
├─ningxiang-leaf -- 基于美团 Leaf 算法的分布式主键生成器
├─ningxiang-multishop -- 商家端业务微服务
├─ningxiang-platform -- 运营管理端业务微服务
├─ningxiang-product -- 商品与多级缓存服务
├─ningxiang-order -- 订单与交易流程服务
├─ningxiang-payment -- 聚合支付服务
├─ningxiang-rbac -- 角色及菜单权限控制服务
├─ningxiang-search -- 基于 ElasticSearch + Canal 的搜索引擎服务
├─ningxiang-user -- 用户资产与会员服务
└─ningxiang-common -- 核心公共依赖与架构抽象组件
```

---

## 📊 核心技术选型

| 技术 | 选型版本 | 作用 |
| :--- | :--- | :--- |
| **Java SDK** | JDK 21 (LTS) | 核心编译与虚拟线程运行目标 |
| **核心框架** | Spring Boot 3.3.0 | 基础应用及自动装配基座 |
| **微服务治理** | Spring Cloud 2023.0.1 | 声明式 RPC 与负载均衡组件 |
| **注册/配置中心** | Nacos 2.3.2 | 服务注册发现与动态配置中心 |
| **安全与鉴权** | Sa-Token 1.38.0 | 响应式路由鉴权、分布式多会话管理 |
| **分布式事务** | Seata 2.0.0 | 微服务高并发分布式事务控制 |
| **消息队列** | RocketMQ 5.x | 分布式事件驱动、多级缓存广播同步 |
| **本地缓存** | Caffeine 3.x | 本地一级高性能内存缓存 |
| **分布式缓存** | Redis 7.x / Jackson | 分布式二级缓存与 Token 共享介质 |
| **数据库** | MySQL 8.0 | 关系型主数据存储 |
| **前端架构** | Vue 3 + Vite 5 + TS | 平台管理端与商家端前沿前端底座 |

---

## 🏃 开发环境快速启动指南

### 1. 启动中间件
推荐使用本地 Docker 容器快速拉起开发所需的各项中间件：
*   **MySQL 8.0**
*   **Redis 7.x**
*   **RocketMQ 5.x**
*   **Nacos 2.x**
*   **Seata 2.0.0**

### 2. 数据库与配置导入
1.  创建 MySQL 数据库，将 [db/](db/) 目录下各模块对应的 SQL 脚本（如 `ningxiang_platform.sql`、`ningxiang_user.sql` 等）导入其中。
2.  登录 Nacos 控制台（`http://localhost:8848/nacos`），将 [db/ningxiang_nacos.sql](db/ningxiang_nacos.sql) 中的配置表导入配置中心。
3.  在 Nacos 的 `application-dev.yml` 配置中修改 MySQL 数据库连接、Redis 与 RocketMQ 地址。

### 3. 微服务启动顺序
顺序在 IDE 中执行以下各模块的主启动类（`Application`）：
1.  `ningxiang-leaf` (ID 生成服务)
2.  `ningxiang-auth` (认证中心)
3.  `ningxiang-gateway` (统一网关，服务端口：`8000`)
4.  其他业务微服务（`ningxiang-product`、`ningxiang-user`、`ningxiang-order` 等）