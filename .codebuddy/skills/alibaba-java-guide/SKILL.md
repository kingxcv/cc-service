---
name: alibaba-java-guide
description: 阿里巴巴Java开发手册精简版。当用户编写Java代码、进行代码审查或询问Java编码规范时触发此skill。涵盖命名、异常处理、日志、数据库、集合、安全、性能等核心规范。
---

# 阿里巴巴 Java 开发手册（精简版）

基于阿里巴巴Java开发手册，提取最核心、最高频的规约要点。

## 1. 命名规范

- **包名**：全小写，点分隔，如 `com.cccz.order.dto`
- **类名**：大驼峰（UpperCamelCase），如 `OrderService`
- **方法/变量**：小驼峰（lowerCamelCase），如 `getOrderById`
- **常量**：全大写 + 下划线，如 `MAX_RETRY_COUNT`
- **禁止**：拼音命名、拼音+英文混搭、魔法值直接硬编码

## 2. 判空防护（防止 NPE）

- 对象使用前必须判空
- 字符串判空用 `StringUtils.isBlank()` / `StringUtils.isEmpty()`
- 集合判空用 `CollectionUtils.isEmpty()`
- 常量放前面防御：`"success".equals(status)` 而非 `status.equals("success")`
- 包装类比较用 `equals()`，禁止用 `==`

## 3. 异常与日志

- **禁止吞异常**：catch 块不可为空
- **禁止** `System.out` / `e.printStackTrace()`，必须用日志框架
- **日志级别**：
  - `error`：系统错误、异常
  - `warn`：可恢复的异常、降级
  - `info`：关键业务流程节点
  - `debug`：调试信息
- 日志需包含关键参数，便于排查
- 使用 SLF4J + Logback（Spring Boot 默认）
- 用 Lombok `@Slf4j` 或 `LoggerFactory.getLogger()`

## 4. 数据库规范

- **每张表必备字段**：`id`, `create_time`, `update_time`, `is_deleted`
- 主键用 `BIGINT AUTO_INCREMENT`
- 时间字段用 `DATETIME`
- **禁止 `SELECT *`**，明确列出字段
- 慎用左模糊 `LIKE '%xx'`（不走索引）
- 索引遵循最左匹配原则
- 统一逻辑删除（`is_deleted` 标记，不物理删除）
- 金额用 `DECIMAL`，禁止 `float`/`double`
- 字符集统一 `utf8mb4`

## 5. 集合使用

- 初始化时预估容量：`new ArrayList<>(16)`
- **禁止**在 `foreach` 循环中增删元素，用 `Iterator`
- `Arrays.asList()` 返回的集合不可 `add`/`remove`
- 用 `Map` 的 `computeIfAbsent` 等现代方法

## 6. 控制语句

- 超过 3 层 `if` 嵌套需重构（卫语句、策略模式、提前 return）
- `switch` 必须带 `default` 分支
- 循环内不频繁调用耗时方法，能提取到循环外的就提取

## 7. 字符串处理

- 循环内拼接用 `StringBuilder`
- 大量字符串格式化用 `String.format()` 或 `MessageFormat`

## 8. 分层架构

| 层 | 职责 | 对应包 |
|---|------|--------|
| Controller | 接收请求、参数校验、返回响应，不写业务逻辑 | `*.controller` |
| Service | 业务逻辑 + 事务管理 | `*.service` + `*.service.impl` |
| DAO/Mapper | 纯数据库 CRUD，不掺杂业务逻辑 | `*.dao` / `*.mapper` |

## 9. 安全规约

- **防 SQL 注入**：MyBatis 用 `#{}` 而非 `${}`
- 敏感数据脱敏（手机号、身份证等）
- 密码加盐哈希存储
- 接口参数必须做非空/格式校验

## 10. 性能规约

- 循环内不创建对象，对象尽量复用
- 热点数据用缓存（Redis）
- 非核心流程异步处理（`@Async` / 消息队列）
- **禁止手动 `new Thread()`**，用线程池

## 11. 代码质量自查清单

- [ ] 入参是否做了校验
- [ ] 对象使用前是否判空
- [ ] 是否有魔法值
- [ ] 字符串拼接是否用了 StringBuilder
- [ ] 异常是否正确捕获和记录日志
- [ ] SQL 是否 `SELECT *`
- [ ] 资源是否关闭（try-with-resources）
- [ ] 金额字段是否用了 Decimal

## 12. 当前项目环境

- JDK 17
- Spring Boot 3.2.5
- Spring Cloud 2023.0.3 + Nacos
- Lombok 1.18.32
- MyBatis-Plus（建议引入）
