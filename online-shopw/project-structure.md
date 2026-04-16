# mall4m 项目结构说明

## 1. 项目定位

这是一个微信小程序商城前端项目，采用原生小程序目录结构，面向 `online-shop` 后端接口。

- 小程序入口配置在 `app.json`
- 小程序启动逻辑在 `app.js`
- 全局样式在 `app.wxss`
- 接口基础地址在 `utils/config.js`
- 统一请求封装在 `utils/http.js`

当前接口基础地址：

- `http://127.0.0.1:18086`，定义于 `utils/config.js`

## 2. 根目录说明

### 配置与入口

- `app.js`
  - 小程序全局入口。
  - 负责初始化全局状态，例如请求队列、登录状态、购物车总数。
- `app.json`
  - 小程序全局配置。
  - 定义页面路由、窗口配置、底部 TabBar。
  - 当前 4 个 Tab 页是首页、分类、购物车、我的。
- `app.wxss`
  - 全局样式文件。
- `sitemap.json`
  - 微信小程序站点地图配置。
- `project.config.json`
  - 微信开发者工具的项目配置，包含 `appid`、基础库版本、编译设置等。
- `project.private.config.json`
  - 本地私有配置，通常覆盖开发者工具本机调试选项。
- `package.json`
  - npm 包信息。这里依赖较少，主要是 `crypto-js`。
- `.gitignore`
  - Git 忽略规则。
- `LICENSE`
  - 开源许可证。
- `debug.log`
  - 调试日志文件，不属于业务代码。

### 资源与第三方代码

- `images/`
  - 本地静态图片资源。
  - 主要用于 tabbar 图标和页面内图标。
- `screenshot/`
  - 项目截图资源，更像说明/展示材料，不参与核心业务逻辑。
- `miniprogram_npm/`
  - 小程序 npm 构建后的依赖目录。
  - 当前主要有 `crypto-js`，用于密码加密。
- `vant/`
  - vendored 的 Vant Weapp 组件源码。
  - 属于第三方 UI 组件库，页面通过各自 `*.json` 引入使用。
- `wxs/`
  - 小程序 WXS 脚本，通常用于模板层简单计算。

## 3. 公共目录说明

### `utils/`

公共工具层，核心价值是把“接口请求、配置、格式化、加密、数值计算”集中管理。

- `utils/config.js`
  - 定义接口域名。
  - 当前只导出了 `domain`。
- `utils/http.js`
  - 项目最核心的公共模块之一。
  - 统一封装 `wx.request`。
  - 负责拼接请求地址、附带 token、处理响应码、跳转登录页、维护购物车角标。
- `utils/util.js`
  - 通用工具函数。
  - 包含时间格式化、富文本内容处理、移除购物车角标。
- `utils/crypto.js`
  - 登录/注册时对密码做加密处理。
- `utils/big.min.js`
  - 大数运算库，避免金额运算精度问题。

### `components/`

项目自定义组件目录。

- `components/production/`
  - 商品卡片组件。
  - 主要职责是展示商品并跳转商品详情页。
- `components/coupon/`
  - 优惠券组件。
  - 支持领取优惠券、勾选优惠券、跳转到优惠券可用商品列表。

每个组件目录内通常包含：

- `*.js`：组件逻辑
- `*.wxml`：组件模板
- `*.wxss`：组件样式
- `*.json`：组件配置

## 4. 页面目录说明

### `pages/`

业务页面主目录。每个页面一般包含：

- `*.js`：页面逻辑
- `*.wxml`：页面结构
- `*.wxss`：页面样式
- `*.json`：页面级配置，例如引用组件

### 核心页面分组

#### 首页与浏览

- `pages/index/`
  - 首页。
  - 加载轮播图、公告、商品标签分组、首页商品列表。
  - 入口接口包括 `/indexImgs`、`/shop/notice/topNoticeList`、`/prod/tag/prodTagList`、`/prod/prodListByTagId`。
- `pages/category/`
  - 分类页。
  - 加载商品分类树，并根据分类获取商品列表。
  - 主要接口：`/category/categoryInfo`、`/prod/pageProd`。
- `pages/prod/`
  - 商品详情页。
  - 负责展示商品图片、详情富文本、SKU、收藏状态、加入购物车、立即购买。
  - 主要接口：`/prod/prodInfo`、`/p/user/collection/isCollection`、`/p/user/collection/addOrCancel`、`/p/shopCart/changeItem`。
- `pages/prod-classify/`
  - 商品活动/专题列表页。
  - 复用一个页面承接多种场景：标签商品、新品推荐、限时特惠、每日疯抢、优惠券商品、我的收藏。

#### 搜索

- `pages/search-page/`
  - 搜索入口页。
  - 展示热门搜索、最近搜索，并跳转到搜索结果页。
  - 主要接口：`/search/hotSearchByShopId`。
- `pages/search-prod-show/`
  - 搜索结果页。
  - 支持切换展示方式、按排序重新查询。
  - 主要接口：`/search/searchProdPage`。

#### 购物车与下单

- `pages/basket/`
  - 购物车页。
  - 负责勾选商品、修改数量、删除商品、计算总价、进入结算。
  - 主要接口：`/p/shopCart/info`、`/p/shopCart/totalPay`、`/p/shopCart/changeItem`、`/p/shopCart/deleteItem`。
- `pages/submit-order/`
  - 提交订单页。
  - 负责确认收货地址、优惠券、金额、提交订单、拉起微信支付。
  - 主要接口：`/p/order/confirm`、`/p/order/submit`、`/p/order/pay`。
- `pages/pay-result/`
  - 支付结果页。
  - 展示支付结果，并支持回订单列表或首页。

#### 用户中心

- `pages/user/`
  - 我的页面。
  - 展示订单数量、收藏数量，并提供地址管理、手机号绑定、订单入口、退出登录。
  - 主要接口：`/p/myOrder/orderCount`、`/p/user/collection/count`、`/logOut`。
- `pages/login/`
  - 登录/注册页。
  - 支持普通账号登录和注册。
  - 使用 `utils/crypto.js` 对密码做加密。
- `pages/binding-phone/`
  - 绑定手机号页面。
  - 先发送短信验证码，再调用绑定接口。
  - 主要接口：`/p/sms/send`、`/user/registerOrBindUser`。

#### 地址与订单

- `pages/delivery-address/`
  - 地址列表页。
  - 支持新增、编辑、设为默认、从下单页选择地址返回。
  - 主要接口：`/p/address/list`、`/p/address/defaultAddr/:addrId`。
- `pages/editAddress/`
  - 地址新增/编辑页。
  - 负责省市区联动、地址保存、地址删除。
  - 主要接口：`/p/address/addrInfo/:addrId`、`/p/area/listByPid`、`/p/address/addAddr`、`/p/address/updateAddr`、`/p/address/deleteAddr/:addrId`。
- `pages/orderList/`
  - 订单列表页。
  - 支持按状态筛选、分页加载、取消订单、再次支付、确认收货、删除订单。
  - 主要接口：`/p/myOrder/myOrder`、`/p/myOrder/cancel/:orderNum`、`/p/order/pay`、`/p/myOrder/receipt/:orderNum`、`/p/myOrder/:orderNum`。
- `pages/order-detail/`
  - 订单详情页。
  - 展示订单商品、收货地址、金额、创建时间，并支持再次加入购物车。
  - 主要接口：`/p/myOrder/orderDetail`、`/p/shopCart/changeItem`。
- `pages/express-delivery/`
  - 物流详情页。
  - 根据订单号查询物流公司、运单号和物流轨迹。
  - 主要接口：`/delivery/check`。

#### 公告资讯

- `pages/recent-news/`
  - 公告列表页。
  - 主要接口：`/shop/notice/noticeList`。
- `pages/news-detail/`
  - 公告详情页。
  - 负责加载公告富文本详情并做图片宽高适配。
  - 主要接口：`/shop/notice/info/:id`。

## 5. 页面之间的大致链路

### 主流程

- 首页 `index` 可以进入商品详情、搜索页、公告页、专题商品页。
- 分类页 `category` 可以进入商品详情和搜索页。
- 商品详情 `prod` 可以加入购物车或立即购买。
- 购物车 `basket` 可以进入提交订单 `submit-order`。
- 提交订单 `submit-order` 会拉起支付，支付后进入 `pay-result`。
- 用户中心 `user` 可以进入地址管理、手机号绑定、订单列表、收藏商品列表。
- 订单列表 `orderList` 可以进入订单详情、物流详情和再次支付。

### 底部 TabBar

- `pages/index/index`
- `pages/category/category`
- `pages/basket/basket`
- `pages/user/user`

## 6. 第三方与样式层

- `vant/`
  - 第三方 UI 组件库源码。
  - 不建议随意直接改，除非你明确要定制组件行为。
- `images/`
  - 页面图标、按钮背景、tabbar 图标等。
- `wxs/number.wxs`
  - 模板层数字处理脚本，通常配合 WXML 使用。

## 7. 当前项目里值得注意的遗留问题

这些不是本次文档目标，但你后续启动和联调时要注意：

- `pages/index/index.js` 里还保留了 `../logs/logs` 的跳转逻辑，但对应页面已经不存在。
- `pages/prod/prod.js` 使用了 `config.picDomain`，而 `utils/config.js` 并没有定义这个字段。
- `pages/pay-result/pay-result.js` 调用了 `http.request`，但文件里没有引入 `http`。
- `pages/binding-phone/binding-phone.js` 使用了 `uni.navigateTo`，而这个项目是原生微信小程序，不是 uni-app。

## 8. 如果你后续想继续梳理

建议按这个顺序继续往下看：

1. `utils/http.js`
2. `pages/index/index.js`
3. `pages/prod/prod.js`
4. `pages/basket/basket.js`
5. `pages/submit-order/submit-order.js`
6. `pages/orderList/orderList.js`

这几个文件基本能把这个商城小程序的主流程串起来。
