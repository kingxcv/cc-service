---
name: cc-service-code-style
description: cc-service 项目编码规范。当在 cc-service 项目中编写业务代码、创建新模块、定义分层类或询问代码结构时触发此skill。定义了 Controller/VO/DTO/Entity/DAO/Service 各层的职责与写法约定。
---

# cc-service 项目编码规范

## 1. 项目概述

- **基础框架**：Spring Boot 3.2.5 + JDK 17
- **微服务**：Spring Cloud 2023.0.3 + Nacos
- **代码包根路径**：`com.cccz`

### 本地环境

| 组件 | 路径 |
|------|------|
| JDK 17 | `C:\Program Files\Java\jdk-17` |
| Maven 3.6.3 | `D:\Program Files\Java\apache-maven-3.6.3` |
| MySQL 8.0.45 | `192.168.56.101:3306`（虚拟机） |
| Nacos | `http://192.168.56.101:8847/nacos` |

**编译/启动命令**（需先设置环境变量）：
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:Path="C:\Program Files\Java\jdk-17\bin;D:\Program Files\Java\apache-maven-3.6.3\bin;" + $env:Path
cd d:\work\jdk17\cc-service
mvn clean compile -DskipTests    # 编译
mvn spring-boot:run               # 启动
```

## 2. 模块包结构

项目按业务模块分包子目录，每个模块内按分层划分子包：

```
com.cccz
├── common/                    # 通用模块
│   ├── common/               # 常量类
│   ├── controller/           # 通用接口
│   ├── dto/                  # 数据传输对象（中转）
│   ├── entity/               # 数据库实体（对应表）
│   ├── enum/                 # 枚举类
│   ├── service/              # 业务接口
│   │   └── impl/             # 业务实现
│   └── vo/                   # 视图对象（入参/出参）
│
├── order/                     # 订单模块
│   ├── common/               # 订单模块常量
│   ├── controller/           # OrderController
│   ├── dao/                  # Mapper 接口（数据访问层）
│   ├── dto/                  # DTO 中转类
│   ├── entity/               # 实体类（对应 t_order 等表）
│   ├── enum/                 # 订单相关枚举
│   ├── service/              # 业务接口
│   │   └── impl/             # 业务实现
│   └── vo/                   # 视图对象
│
└── CcServiceApplication.java  # 启动类
```

## 3. 各层职责与写法规范

### 3.1 Controller（接口层）

**职责**：仅暴露 REST 接口、做简单参数校验，不写任何业务逻辑。

```java
@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public Result<OrderVO> create(@Valid @RequestBody OrderCreateReq req) {
        // 仅做简单参数校验，复杂校验在 Service
        OrderVO vo = orderService.createOrder(req);
        return Result.success(vo);
    }
}
```

**规范要点**：
- 必须加 `@RestController`
- 构造函数注入（用 Lombok `@RequiredArgsConstructor` + `final`）
- 只调用 Service，不直接调 DAO
- 入参/出参用 VO/Req 类，不暴露 Entity
- 简单校验用 `@Valid` + JSR303 注解
- 使用统一返回体 `Result<T>`

### 3.2 VO（视图对象层）

**职责**：入参（Req）和出参（Resp），与前端交互。

- **入参 VO**：命名以 `Req` 结尾，如 `OrderCreateReq`
- **出参 VO**：命名以 `VO` 或 `Resp` 结尾，如 `OrderVO`

```java
@Data
public class OrderCreateReq {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotBlank(message = "收货人不能为空")
    private String receiverName;
    
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal totalAmount;
}
```

### 3.3 DTO（数据传输层）

**职责**：服务间、方法间的数据中转/传输，不直接对前端暴露。

- 用于 Service 之间、Service 内部方法之间传递数据
- 可包含组合字段（跨 Entity 的数据）

```java
@Data
public class OrderCreateDTO {
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;  // 组合数据
}
```

### 3.4 Entity（实体层）

**职责**：与数据库表一一对应的持久化对象。

- 类名 = 表名转大驼峰（`t_order` → `Order`）
- 必须标注 `@TableName`（MyBatis-Plus）或对应 JPA 注解

```java
@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Integer orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
```

**规范要点**：
- 时间字段用 `LocalDateTime`，不用 `Date`
- 金额用 `BigDecimal`
- `is_deleted` 对应 `Integer` 类型（0/1）
- 必备字段：`id`, `createTime`, `updateTime`, `isDeleted`

### 3.5 DAO / Mapper（数据访问层）

**职责**：纯数据库 CRUD 操作，不掺杂任何业务逻辑。

```java
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据订单号查询
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 按用户分页查询订单
     */
    List<Order> selectByUserId(@Param("userId") Long userId, 
                                @Param("status") Integer status);
}
```

**规范要点**：
- 接口加 `@Mapper` 注解
- 建议继承 MyBatis-Plus 的 `BaseMapper<T>`
- 方法名体现操作类型：`select`/`insert`/`update`/`delete`
- SQL 用 XML 或注解，复杂查询用 XML
- **禁止在 Mapper 里写业务逻辑**

### 3.6 Service（业务层）

**职责**：核心业务逻辑 + 事务管理。

- 接口放 `service/` 目录
- 实现放 `service/impl/` 目录

```java
// 接口
public interface OrderService {
    OrderVO createOrder(OrderCreateReq req);
    OrderVO getOrderByNo(String orderNo);
    void cancelOrder(Long orderId, String reason);
}

// 实现
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final UserService userService;  // 跨模块调用其他 Service

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateReq req) {
        // 1. 业务校验
        // 2. 组装 Entity，调用 Mapper
        // 3. 返回 VO
    }
}
```

**规范要点**：
- `@Service` 加在 Impl 上
- 事务注解 `@Transactional(rollbackFor = Exception.class)` 加在写操作方法上
- 跨模块调用只能调其他 Service，不直接调 Mapper
- 大事务需拆分，避免长事务锁表

## 4. 调用链路

```
前端请求
  → Controller（接收 + 参数校验）
    → Service 接口 → ServiceImpl（业务逻辑 + 事务）
      → DAO/Mapper（数据库 CRUD）
    ← DTO/VO（数据组装返回）
  ← Controller（返回统一响应体）
```

## 5. 统一返回体

```java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> Result<T> success(T data) { ... }
    public static <T> Result<T> fail(Integer code, String message) { ... }
}
```

## 6. 禁止事项

- Controller 里直接调 DAO/Mapper
- Controller 里写业务逻辑
- VO 直接当成 Entity 传给 Mapper
- Entity 直接返回给前端
- Service 之间循环依赖
- Mapper 里写业务判断逻辑

## 7. DDL 规范

表 DDL 文件放在 `src/main/resources/db/` 目录下，命名按模块：

```
src/main/resources/db/
├── t_order.sql
├── t_user.sql
└── ...
```

表字段必备：`id`, `create_time`, `update_time`, `is_deleted`。
