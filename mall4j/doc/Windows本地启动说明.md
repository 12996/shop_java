# mall4j Windows 本地启动说明

本文档适用于当前这台 Windows 开发机，目标是用一个脚本同时启动 `yami-shop-admin` 和 `yami-shop-api`。

## 1. 前置条件

启动前请先确认以下条件：

- 已安装 Java 17
- 已安装 Maven，并且 `mvn` 命令可用
- MySQL 可访问地址为 `127.0.0.1:3307`
- Redis 可访问地址为 `127.0.0.1:6380`
- 数据库 `yami_shops` 已导入脚本 `db/yami_shop.sql`

当前项目本地开发默认端口如下：

- 管理后台服务：`18085`
- 商城前台接口服务：`18086`
- MySQL：`3307`
- Redis：`6380`

## 2. 数据库名称和连接方式

本项目本地开发使用的 MySQL 数据库名是 `yami_shops`，不是 `mall`。

数据库初始化脚本位置：

- `db/yami_shop.sql`

如果你在 IDEA 的 Database 工具窗口里新建连接，建议按下面填写：

- Host：`127.0.0.1`
- Port：`3307`
- Database：`yami_shops`
- User：`root`
- Password：`root`

如果你使用 JDBC URL，建议填：

```text
jdbc:mysql://127.0.0.1:3307/yami_shops?allowMultiQueries=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true
```

你也可以先在 MySQL 客户端里执行下面的语句，确认本机有哪些库：

```sql
SHOW DATABASES;
```

如果没有看到 `yami_shops`，说明还没有导入初始化数据，需要执行 `db/yami_shop.sql`。

如果你想直接检查当前电脑上的本地数据库是否就是项目要用的那个实例，可以在项目根目录执行：

```bat
check-local-db.bat
```

这个脚本会按项目默认配置检查：

- Host：`127.0.0.1`
- Port：`3307`
- Database：`yami_shops`
- User：`root`
- Password：`root`

## 3. 一键启动方式

项目根目录下已经提供脚本：

- `start-local.bat`

使用方法：

1. 打开 `cmd` 或 PowerShell
2. 进入项目根目录
3. 执行：

```bat
start-local.bat
```

脚本会自动完成下面几件事：

1. 检查 `3307` 和 `6380` 是否可连通
2. 执行一次 `mvn -DskipTests install`
3. 分别打开两个新窗口启动：
   - `yami-shop-admin`
   - `yami-shop-api`

## 4. 手动启动方式

如果你不想用脚本，也可以手动执行。

先在项目根目录执行：

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

## 5. 启动成功后如何验证

启动成功后可以先看接口文档页面：

- 管理后台服务：`http://localhost:18085/doc.html`
- 商城前台接口服务：`http://localhost:18086/doc.html`

也可以观察启动日志，看到类似内容说明服务已经起来：

- `Started WebApplication`
- `Started ApiApplication`

## 6. 两个服务分别是什么

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

## 7. 常见问题

### 7.1 脚本提示 MySQL 或 Redis 不可达

说明本地依赖服务还没有准备好。先确认：

- MySQL 是否运行在 `3307`
- Redis 是否运行在 `6380`

### 7.2 Maven install 失败

优先检查：

- 是否能联网下载依赖
- `mvn -version` 是否正常
- Java 版本是否为 17

### 7.3 18085 或 18086 端口被占用

说明已有程序占用了项目端口。先关闭占用进程，再重新执行脚本。

### 7.4 上传相关功能异常

当前项目已经切到本地上传模式，目录为：

```text
F:/work/project/shop_java/mall4j/.local/upload/
```

如果后续你改回云存储，需要同步调整 `shop.properties`。

### 7.5 IDEA 提示 `Unknown database 'yami_shops'`

如果你看到这个报错，不代表项目库名写错了。更常见的原因是：

- IDEA 连接到了错误的 MySQL 端口，比如 `3306`
- IDEA 连接到了另一台 MySQL 实例
- 你在 IDEA 里保存的是旧连接，数据库名还没刷新

本项目本地开发正确的连接应该是：

- Host：`127.0.0.1`
- Port：`3307`
- Database：`yami_shops`
- User：`root`
- Password：`root`

建议先在项目根目录执行：

```bat
check-local-db.bat
```

如果脚本能查到 `yami_shops` 和表列表，但 IDEA 还是报错，说明是 IDEA 当前连接配置不对，不是项目数据库不存在。
