# Vue3 商城前端项目结构与计划文档 (Web版)

本项目是基于原有小程序（`online-shopw`）的业务接口与页面划分架构，所孵化出的现代化 Vue 3 Web 前端项目。

## 1. 核心计划与完成情况

- **[已完成]** 采用 Vue 3 + Vite 初始化项目底座。
- **[已完成]** 基于原生 CSS 完全还原设计图指定的 Login 页面极简视觉交互。
- **[已完成]** 将小程序架构中的 `wx.request`、接口配置文件、加密处理平滑过渡为 Web 端的 `axios` 方案。
- **[待进行]** 将现有预留好的（Home、Category、Cart、User）四大前台页面对接并填充 UI。

## 2. 目录体系映射

针对小程序的对应功能，目前该 `web` 目录承接了如下架构逻辑：

### 工具与接口层 (`src/utils/`) 
相当于小程序原本的 `utils` 文件夹核心，现已使用适用于 Web 的标准化方案重构。
- `config.js` -> `const domain = 'http://127.0.0.1:18086'` (接口统一寻址)
- `http.js` -> 引入 Axios 包装，在请求拦截器中携带 Token，并在响应报错 401 触发踢回。
- `crypto.js` -> 定义了原 `crypto-js` 版的密码加密平移逻辑 (MD5/AES)。

### 页面层 (`src/views/` 结合 `src/router/index.js`) 
对齐了原小程序的 `pages/` 功能节点。

1. **`Login/index.vue` (登录/注册)**
   - 包含高度还原 UI 的表单与密码开关动态掩码交互。
   - 使用了本地 Token 和模拟跳转逻辑。
2. **首页底栏四核心页 (配合 App.vue)**
   - `Home/index.vue`: 对标小程序 `pages/index/` 
   - `Category/index.vue`: 对标小程序 `pages/category/` 
   - `Cart/index.vue`: 对标小程序 `pages/basket/`
   - `User/index.vue`: 对标小程序 `pages/user/`

### 根级组件
- `src/App.vue`: Web应用骨架，已在底部植入了代替小程序 `app.json中tabBar` 的通用悬浮栏（非登录页展示）。
- `src/main.js`: 将 `vue-router` 绑定入全局实例。

## 3. 如何启动该Web项目？

目前该前端项目独立存在于现有的工作区子目录 `web/` 当中。您如果想独立预览它，您需要**先将终端目录切换进 `web` 包中**。

**正确的执行顺序：**
1. 终端进入 web 目录： `cd web` (或者 `cd f:/work/project/shop_java/online-shopw/web`)
2. `npm install` (如果您尚未安装依赖)
3. `npm run dev` （注意 Vite 框架下默认的启动命令叫做 run dev 而非 run start）

随后，浏览器访问 `http://localhost:5173/login` 即可。

*【已知可能遇到的问题】因 Vite 6 中的 rolldown 和您本地 Nodev22 的兼容异常，如果 run dev 报错中断，请在 `web/package.json` 中将 `"vite": "^6.x"` 的版本数字改为 `"^5.0.0"` ，再度 `npm install` 并运行。*
