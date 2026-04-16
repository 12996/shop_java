# Windows Startup Script Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 `mall4j` 增加 Windows 本地一键启动脚本，并补充对应的启动说明文档。

**Architecture:** 在项目根目录提供一个 `.bat` 脚本，负责检查依赖端口、执行一次 Maven 安装、再分别打开两个窗口启动 `admin` 和 `api`。文档放在 `doc/` 目录，说明前置条件、启动方式、验证方式和常见问题。

**Tech Stack:** Windows Batch、Maven、Spring Boot

---

### Task 1: 添加 Windows 启动脚本

**Files:**
- Create: `start-local.bat`

**Step 1: 编写脚本**
- 检查 `3307`、`6380` 端口。
- 在项目根目录执行 `mvn -DskipTests install`。
- 使用 `start` 命令分别打开两个窗口启动：
  - `yami-shop-admin`
  - `yami-shop-api`

**Step 2: 自检脚本内容**

Run: 手工阅读脚本，确认路径、端口、命令与当前项目一致。

Expected: 脚本可在 Windows 终端直接执行。

### Task 2: 添加 Windows 启动文档

**Files:**
- Create: `doc/Windows本地启动说明.md`

**Step 1: 编写文档**
- 说明前置条件。
- 说明执行脚本的方法。
- 说明手动启动命令。
- 说明启动成功后的验证地址。
- 说明常见失败点。

**Step 2: 自检文档**

Run: 手工阅读文档，确认与当前仓库配置一致。

Expected: 文档可直接指导 Windows 用户完成本地启动。
