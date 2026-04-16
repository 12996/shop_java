# mall4j Windows 本地启动说明

本文档适用于当前 Windows 开发机，目标是同时启动 `yami-shop-admin` 和 `yami-shop-api`。

## 1. 前置条件

启动前请先确认：

- 已安装 Java 17
- 已安装 Maven，并且 `mvn` 命令可用
- MySQL 可访问地址为 `127.0.0.1:3307`
- Redis 可访问地址为 `127.0.0.1:6380`
- 数据库 `yami_shops` 已导入脚本 `db/yami_shop.sql`

当前本地开发端口如下：

- 管理后台服务：`18085`
- 商城前台接口服务：`18086`
- MySQL：`3307`
- Redis：`6380`

## 2. 数据库名称和连接方式

项目本地开发使用的 MySQL 数据库名称是 `yami_shops`，不是 `mall`。

初始化脚本位置：

- `db/yami_shop.sql`

如果你在 IDEA 的 Database 工具窗口里新建连接，建议这样填：

- Host：`127.0.0.1`
- Port：`3307`
- Database：`yami_shops`
- User：`root`
- Password：`root`

JDBC URL 可使用：

```text
jdbc:mysql://127.0.0.1:3307/yami_shops?allowMultiQueries=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true
```

如果想先检查本机到底有哪些库，可以执行：

```sql
SHOW DATABASES;
```

如果没有看到 `yami_shops`，说明还没有导入初始化数据，需要执行 `db/yami_shop.sql`。

如果想直接检查当前电脑上的本地数据库是不是项目要用的实例，可以在项目根目录执行：

```bat
check-local-db.bat
```

## 3. 一键启动方式

项目根目录已经提供脚本：

- `start-local.bat`

使用方式：

1. 打开 `cmd` 或 PowerShell
2. 进入项目根目录
3. 执行

```bat
start-local.bat
```

脚本会自动完成：

1. 检查 `3307` 和 `6380` 是否可连通
2. 执行一次 `mvn -DskipTests install`
3. 分别打开两个新窗口启动：
- `yami-shop-admin`
- `yami-shop-api`

## 4. 手动启动方式

如果不想用脚本，也可以手动执行。

先在项目根目录运行：

```bat
mvn -DskipTests install
```

然后分别启动两个服务：

```bat
mvn -f yami-shop-admin/pom.xml spring-boot:run -DskipTests
```

```bat
mvn -f yami-shop-api/pom.xml spring-boot:run -DskipTests
```

## 5. 停止服务

项目根目录已经提供停止脚本：

- `stop-local.bat`

执行后会按端口关闭：

- `18085` 对应 `yami-shop-admin`
- `18086` 对应 `yami-shop-api`

## 6. 启动后如何验证

启动成功后可以先打开接口文档：

- 管理后台服务：`http://localhost:18085/doc.html`
- 商城前台接口服务：`http://localhost:18086/doc.html`

也可以从启动日志确认：

- `Started WebApplication`
- `Started ApiApplication`

## 7. 两个后端分别是什么

`yami-shop-admin`

- 管理后台服务
- 给管理员、运营人员使用
- 默认端口 `18085`
- 启动类：`com.yami.shop.admin.WebApplication`

`yami-shop-api`

- 商城前台接口服务
- 给小程序、H5、App、前台页面调用
- 默认端口 `18086`
- 启动类：`com.yami.shop.api.ApiApplication`

## 8. 自动化验证 Token

为了避免后台接口验证时被验证码和登录流程卡住，项目已经提供本地调试 Token 接口：

- 地址：`POST http://localhost:18085/dev/auth/admin-token`
- 该接口仅对 `yami-shop-admin` 生效，且仅允许本机请求

详细说明见：

- [后端自动化验证与调试Token方案.md](/F:/work/project/shop_java/mall4j/doc/后端自动化验证与调试Token方案.md)

测试同学最小使用流程（Windows PowerShell）：

```powershell
$tokenResp = Invoke-RestMethod -Method Post -Uri "http://localhost:18085/dev/auth/admin-token"
$token = $tokenResp.data.accessToken
Invoke-RestMethod -Method Get -Uri "http://localhost:18085/sys/menu/nav" -Headers @{ Authorization = $token }
```

如果第二步返回 `code=00000`，说明“取 Token + 访问受保护后台接口”链路正常。

优惠券后端联调与最小闭环验证请看：

- [优惠券后端联调与最小闭环测试.md](/F:/work/project/shop_java/mall4j/doc/优惠券后端联调与最小闭环测试.md)

## 9. 常见问题

### 9.1 MySQL 或 Redis 不可达

先确认：

- MySQL 是否运行在 `3307`
- Redis 是否运行在 `6380`

### 9.2 Maven install 失败

优先检查：

- 是否能联网下载依赖
- `mvn -version` 是否正常
- Java 版本是否为 17

### 9.3 18085 或 18086 端口被占用

说明已有其他程序占用了项目端口，先关闭占用进程，再重新执行脚本。

如果是 `18085` 被占用，`/dev/auth/admin-token` 会直接不可用或打到错误服务。

### 9.4 上传功能异常

当前项目已经切到本地上传模式，目录为：

```text
F:/work/project/shop_java/mall4j/.local/upload/
```

如果后续改回云存储，需要同步调整 `shop.properties`。

### 9.5 IDEA 提示 `Unknown database 'yami_shops'`

这通常不是库名写错，而是 IDEA 连接到了错误的 MySQL 实例。常见原因：

- 连到了错误端口，比如 `3306`
- 连到了另一台 MySQL
- 使用的是旧连接，没有刷新

本项目本地开发的正确连接应为：

- Host：`127.0.0.1`
- Port：`3307`
- Database：`yami_shops`
- User：`root`
- Password：`root`

建议先在项目根目录执行：

```bat
check-local-db.bat
```

如果脚本能查到 `yami_shops` 和表列表，但 IDEA 仍然报错，说明是 IDEA 当前连接配置不对，不是项目数据库不存在。
