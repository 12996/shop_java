# Windows Dev Migration Documentation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 `mall4j` 增加一份从当前 Windows 开发机迁移到另一台 Windows 开发机继续本地开发的操作文档。

**Architecture:** 文档聚焦本地开发迁移，不写服务器部署。内容按迁移前准备、迁移后安装配置、数据库与缓存准备、项目启动与验证、常见问题组织，尽量复用当前仓库已确认的端口和脚本约定。

**Tech Stack:** Markdown、Windows、Java 17、Maven、MySQL、Redis、Spring Boot

---

### Task 1: 编写迁移文档

**Files:**
- Create: `doc/Windows开发机迁移说明.md`

**Step 1: 写迁移前准备清单**
- 记录旧电脑上需要确认和备份的内容。

**Step 2: 写新电脑安装与配置步骤**
- 包括 Java、Maven、Maven 仓库路径、Git、IDEA。

**Step 3: 写数据库与 Redis 准备步骤**
- 包括端口约定和 SQL 初始化。

**Step 4: 写项目启动与验证步骤**
- 包括 `start-local.bat` 和手动启动方式。

**Step 5: 写迁移后验证与常见问题**
- 包括端口、接口文档、构建失败、依赖未启动等问题。

### Task 2: 自检文档一致性

**Files:**
- Verify: `doc/Windows开发机迁移说明.md`
- Reference: `start-local.bat`
- Reference: `doc/Windows本地启动说明.md`

**Step 1: 检查文档中的端口、路径、命令**

Run: 手工阅读文档并与现有脚本、配置比对。

Expected: 文档中的命令、端口和路径与当前仓库一致。
