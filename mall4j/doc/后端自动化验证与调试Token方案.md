# 后端自动化验证与调试 Token 方案

本文档给测试和自动化脚本使用，目标是在本地开发环境下快速获取后台管理员 Token，避免被验证码和手工登录流程卡住。

## 0. 使用前确认（必须）

- 已启动 `yami-shop-admin`（不是 `yami-shop-api`）
- 管理后台服务地址是 `http://localhost:18085`
- 当前运行环境是 `dev`（本项目默认 `application.yml` 为 `spring.profiles.active=dev`）
- Redis 可用（Token 会写入 Redis）

## 1. 方案说明

- 这是一个仅用于本地开发联调的调试入口，不是固定写死的万能 Token
- 接口由 `yami-shop-admin` 提供
- 接口路径为 `POST /dev/auth/admin-token`
- 完整地址为 `http://localhost:18085/dev/auth/admin-token`
- 当前默认签发超级管理员 `user_id=1` 的 Token

## 2. 安全限制

这个接口当前有三层限制：

- 仅在 `dev` Profile 下加载
- 仅当 `app.dev-auth.enabled=true` 时可用
- 仅允许本机请求访问（`127.0.0.1` / `::1` / `0:0:0:0:0:0:0:1`）

也就是说：

- `prod` 环境不会暴露这个接口
- 远程机器、代理转发、非本机直连请求都拿不到 Token

## 3. 配置位置

配置文件：

- [application-dev.yml](/F:/work/project/shop_java/mall4j/yami-shop-admin/src/main/resources/application-dev.yml)

当前配置：

```yaml
app:
  dev-auth:
    enabled: true
```

如果暂时不想让自动化脚本使用这个能力，把它改成 `false` 即可。

## 4. 返回结构

成功返回示例：

```json
{
  "code": "00000",
  "msg": "success",
  "data": {
    "accessToken": "xxx",
    "refreshToken": "xxx",
    "expiresIn": 2592000,
    "tokenName": "Authorization"
  }
}
```

字段说明：

- `accessToken`：后续请求要带上的 Token
- `refreshToken`：当前实现会一起返回
- `expiresIn`：过期时间，单位为秒
- `tokenName`：请求头名称，当前默认是 `Authorization`

## 5. 测试调用方式

第一步，取 Token：

```bash
curl -X POST "http://localhost:18085/dev/auth/admin-token"
```

第二步，访问后台受保护接口：

```bash
curl -H "Authorization: <accessToken>" "http://localhost:18085/sys/menu/nav"
```

说明：这里是直接传 `Authorization: <accessToken>`，不是 `Bearer <accessToken>`。

如果脚本里要自动化调用，建议固定做成两步：

1. 每轮执行前先调用一次 `/dev/auth/admin-token`
2. 从返回值中提取 `accessToken`
3. 后续所有后台请求统一带上 `Authorization: <accessToken>`

## 6. Windows PowerShell 示例

```powershell
$tokenResp = Invoke-RestMethod -Method Post -Uri "http://localhost:18085/dev/auth/admin-token"
$token = $tokenResp.data.accessToken

Invoke-RestMethod -Method Get `
  -Uri "http://localhost:18085/sys/menu/nav" `
  -Headers @{ Authorization = $token }
```

也可以加一个最小断言（适合自动化冒烟）：

```powershell
if (-not $token) { throw "token is empty" }
$nav = Invoke-RestMethod -Method Get -Uri "http://localhost:18085/sys/menu/nav" -Headers @{ Authorization = $token }
if ($nav.code -ne "00000") { throw "nav check failed: $($nav.msg)" }
```

## 7. 失败排查

- 返回 `404`（调用 `/dev/auth/admin-token`）
- 通常是服务没启动在 `18085`，或当前不是 `dev` 环境，或请求打到了 `yami-shop-api`

- 返回 `dev auth is disabled`
- 检查 `application-dev.yml` 中的 `app.dev-auth.enabled` 是否为 `true`

- 返回 `dev auth only allows local requests`
- 说明当前请求不是本机直连发起的
- 请在运行服务的同一台机器上调用，不要经过远程代理

- 返回 `super admin user not found`
- 检查数据库 `tz_sys_user` 表里是否存在 `user_id=1`

- 返回 `super admin is disabled`
- 检查管理员状态是否为启用，也就是 `status=1`

- 返回 `401`
- 检查请求头是否带了 `Authorization: <accessToken>`（不要带 `Bearer ` 前缀）
- 检查 Redis 是否正常运行
- 检查 Token 是否已经过期

## 8. 给测试同学的使用建议

- 只在本地开发机使用这个接口，不要在测试环境和生产环境依赖它
- 每次执行自动化前重新取一次 Token，不要长期缓存
- 不要把获取到的 Token 写进代码仓库、共享文档或长期脚本常量
- 如果自动化脚本要做登录相关测试，仍然应该走正式登录流程；这个接口只用于跳过登录前置条件，验证后台业务接口本身

## 9. 相关代码位置

- [DevAuthController.java](/F:/work/project/shop_java/mall4j/yami-shop-admin/src/main/java/com/yami/shop/admin/controller/DevAuthController.java)
- [DevTokenInfoVO.java](/F:/work/project/shop_java/mall4j/yami-shop-admin/src/main/java/com/yami/shop/admin/vo/DevTokenInfoVO.java)
- [ResourceServerAdapter.java](/F:/work/project/shop_java/mall4j/yami-shop-security/yami-shop-security-admin/src/main/java/com/yami/shop/security/admin/adapter/ResourceServerAdapter.java)
- [TokenStore.java](/F:/work/project/shop_java/mall4j/yami-shop-security/yami-shop-security-common/src/main/java/com/yami/shop/security/common/manager/TokenStore.java)
