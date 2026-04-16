# mall4j Windows 开发机迁移说明

本文档用于把当前这套 `mall4j` 本地开发环境，从一台 Windows 电脑迁移到另一台 Windows 电脑，并继续本地开发。

适用场景：

- 从旧电脑迁移到新电脑
- 新电脑仍然是 Windows
- 目标是本地开发，不是部署到服务器

## 1. 迁移目标

迁移完成后，新电脑应达到下面的状态：

- 可以执行 `mvn -version`
- 可以编译项目
- MySQL 使用 `3307`
- Redis 使用 `6380`
- 可以运行 `start-local.bat`
- 可以同时启动：
  - `yami-shop-admin`
  - `yami-shop-api`
- 可以访问：
  - `http://localhost:18085/doc.html`
  - `http://localhost:18086/doc.html`

## 2. 旧电脑迁移前要做什么

建议先在旧电脑上整理好以下信息，再开始迁移。

### 2.1 备份代码

至少保留以下内容之一：

- Git 仓库远程代码
- 当前项目目录完整备份

如果你本地还有未提交改动，建议先自己确认是否需要保留。

### 2.2 记录当前开发环境版本

建议在旧电脑上记录：

- Java 版本：`17`
- Maven 版本
- IDEA 版本
- MySQL 版本
- Redis 版本

### 2.3 记录当前关键配置

当前项目本地开发约定如下：

- MySQL 端口：`3307`
- Redis 端口：`6380`
- 管理后台端口：`18085`
- 商城前台接口端口：`18086`
- Maven 本地仓库：`D:\Maven\maven-repo`

### 2.4 确认数据库是否需要迁移数据

你需要先决定迁移的是哪一种：

- 只迁项目结构和初始化环境
- 连同旧电脑上的本地测试数据一起迁走

如果只是继续开发，通常有两种做法：

1. 最简单：新电脑重新导入 `db/yami_shop.sql`
2. 保留旧数据：从旧电脑导出 `yami_shops` 数据库，再导入新电脑

### 2.5 备份本地数据库数据

如果你需要保留旧电脑上的本地测试数据，建议导出：

- 数据库 `yami_shops`

如果你不需要保留测试数据，这一步可以跳过，直接在新电脑导入初始化 SQL。

### 2.6 确认本地上传目录是否有需要保留的文件

当前项目本地上传目录为：

```text
F:/work/project/shop_java/mall4j/.local/upload/
```

如果你本地联调时上传过图片，且这些文件还需要保留，可以一起拷走。

## 3. 新电脑需要安装什么

新电脑建议按下面顺序安装。

### 3.1 安装 Git

用于拉取代码。

### 3.2 安装 Java 17

本项目本地开发使用 Java 17。

安装后确认：

```bat
java -version
```

### 3.3 安装 Maven

安装后确认：

```bat
mvn -version
```

### 3.4 配置 Maven 本地仓库

建议沿用当前机器的仓库路径习惯：

```text
D:\Maven\maven-repo
```

一般在下面文件中配置：

```text
C:\Users\你的用户名\.m2\settings.xml
```

示例：

```xml
<settings>
  <localRepository>D:\Maven\maven-repo</localRepository>
</settings>
```

### 3.5 安装 IDEA

用于本地调试和运行项目。

## 4. 新电脑如何准备项目

### 4.1 拉取或拷贝项目代码

把项目放到你习惯的目录，例如：

```text
F:\work\project\shop_java\mall4j
```

注意：

- 如果项目路径变了，不影响启动脚本使用
- 但本地上传目录会跟随当前项目目录重新生成

### 4.2 打开项目

用 IDEA 打开项目根目录：

```text
mall4j
```

### 4.3 确认 IDEA 使用的是 Java 17

重点检查：

- Project SDK
- Maven Runner JRE
- Run Configuration 的 JRE

## 5. 新电脑如何准备数据库和 Redis

### 5.1 准备 MySQL

本项目当前本地开发默认使用：

- 地址：`127.0.0.1`
- 端口：`3307`
- 数据库：`yami_shops`

你可以用两种方式准备：

1. 自己本机安装 MySQL，并改为使用 `3307`
2. 用当前仓库的 Docker 方式启动 MySQL

### 5.2 准备 Redis

本项目当前本地开发默认使用：

- 地址：`127.0.0.1`
- 端口：`6380`

你可以用两种方式准备：

1. 自己本机安装 Redis，并改为使用 `6380`
2. 用当前仓库的 Docker 方式启动 Redis

### 5.3 初始化数据库

如果不迁移旧数据，直接导入：

```text
db/yami_shop.sql
```

导入目标库：

```text
yami_shops
```

如果你迁移的是旧电脑已有数据，则导入旧电脑导出的数据库备份即可。

## 6. 项目里已经固定的本地开发配置

当前仓库已经做过以下本地开发适配，新电脑不需要再重复改这些文件。

### 6.1 MySQL 和 Redis 端口已改

以下两个文件已经改为本地开发默认使用：

- `3307`
- `6380`

对应文件：

- `yami-shop-admin/src/main/resources/application-dev.yml`
- `yami-shop-api/src/main/resources/application-dev.yml`

### 6.2 上传方式已切到本地上传

文件：

- `yami-shop-common/src/main/resources/shop.properties`

当前配置：

- `shop.imgUpload.uploadType=1`

这意味着新电脑本地开发时，不依赖七牛云占位配置。

## 7. 迁移到新电脑后如何启动

### 7.1 推荐方式：使用脚本启动

项目根目录已有脚本：

- `start-local.bat`

执行：

```bat
start-local.bat
```

脚本会自动：

1. 检查 MySQL `3307`
2. 检查 Redis `6380`
3. 执行 `mvn -DskipTests install`
4. 分别启动：
   - `yami-shop-admin`
   - `yami-shop-api`

### 7.2 手动启动方式

先执行：

```bat
mvn -DskipTests install
```

再分别启动：

```bat
mvn -f yami-shop-admin/pom.xml spring-boot:run -DskipTests
```

```bat
mvn -f yami-shop-api/pom.xml spring-boot:run -DskipTests
```

## 8. 迁移完成后如何验证

### 8.1 检查命令行环境

```bat
java -version
mvn -version
```

### 8.2 检查依赖端口

确认：

- `3307` 可连
- `6380` 可连

### 8.3 检查项目能否构建

```bat
mvn -DskipTests install
```

### 8.4 检查两个服务能否启动

启动成功后确认下面两个地址可访问：

- `http://localhost:18085/doc.html`
- `http://localhost:18086/doc.html`

### 8.5 检查日志关键字

启动日志中应能看到：

- `Started WebApplication`
- `Started ApiApplication`

## 9. 常见问题

### 9.1 新电脑上 `mvn` 可以用，但项目启动失败

优先检查：

- 是否先执行过 `mvn -DskipTests install`
- IDEA 和 Maven 是否都在用 Java 17

### 9.2 MySQL 或 Redis 端口不对

当前项目默认是：

- MySQL `3307`
- Redis `6380`

如果你新电脑还是用了 `3306/6379`，就会和当前项目默认配置不一致。

### 9.3 启动脚本执行失败

优先检查：

- `java -version`
- `mvn -version`
- MySQL 和 Redis 是否已启动
- 数据库 `yami_shops` 是否已导入 SQL

### 9.4 上传图片后访问异常

先确认本地上传目录是否存在，以及当前项目是否有权限写入。

### 9.5 想保留旧电脑本地数据

建议迁移：

- `yami_shops` 数据库备份
- 本地上传目录中的文件

## 10. 推荐的迁移顺序

建议按下面顺序做，最稳：

1. 在旧电脑备份代码和数据库
2. 在新电脑安装 Java 17、Maven、Git、IDEA
3. 配置 Maven 本地仓库
4. 拉取或拷贝项目代码
5. 准备 MySQL 和 Redis，并使用 `3307/6380`
6. 导入 `db/yami_shop.sql` 或旧数据库备份
7. 执行 `start-local.bat`
8. 访问 `18085/18086` 的接口文档页面验证
