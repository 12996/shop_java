# mall4m 小程序接口与页面复用清单

## 1. 项目定位

- 项目类型：微信小程序商城前端
- 当前接口基地址：`http://127.0.0.1:18086`
- 基础请求封装：`utils/http.js`
- 页面注册入口：`app.json`
- 当前底部 Tab：`首页`、`分类`、`购物车`、`我的`

这份文档的目标不是讲目录结构，而是给后续网页端复用逻辑用。重点记录：

- 这个小程序实际用了哪些 API
- 每个页面负责什么功能
- 哪些逻辑可以直接搬到网页端
- 哪些接口/页面是遗留逻辑，迁移时不要照抄

## 2. 请求与登录约定

### 2.1 统一请求

- 所有业务请求统一走 `utils/http.js`
- 实际请求地址 = `config.domain + params.url`
- 默认请求方法是 `POST`
- 登录态通过请求头 `Authorization` 传 token
- 返回码约定：
  - `00000`：成功
  - `A00004`：未授权，跳登录页
  - `A00005`：服务异常
  - `A00001`：业务错误，直接 toast 提示

### 2.2 登录相关

- 小程序原本有 `wx.login -> /login?grant_type=mini_app` 的 token 流程，但 `app.js` 里当前是注释状态
- 现在项目里还保留了账号密码登录页：
  - `POST /login`
  - `POST /user/register`
- 用户信息补充接口：
  - `PUT /p/user/setUserInfo`

网页端如果要复用业务，建议把“登录态存储、统一请求拦截、未登录跳转”抽成公共模块，不要散落在页面里。

## 3. API 总表

## 3.1 首页与商品浏览

### 首页

- `GET /indexImgs`
  - 首页轮播图
- `GET /shop/notice/topNoticeList`
  - 首页公告滚动信息
- `GET /prod/tag/prodTagList`
  - 首页商品标签分组
- `GET /prod/prodListByTagId`
  - 某个标签下的商品列表
- `GET /prod/prodInfo`
  - 首页“加入购物车”前先查商品详情，拿默认 `skuId`
- `POST /p/shopCart/changeItem`
  - 首页直接加入购物车

### 分类与商品列表

- `GET /category/categoryInfo`
  - 分类树/分类列表
- `GET /prod/pageProd`
  - 按分类取商品列表
- `GET /prod/prodListByTagId`
  - 标签商品列表
- `GET /prod/lastedProdPage`
  - 新品推荐列表
- `GET /prod/discountProdList`
  - 限时特惠商品列表
- `GET /prod/moreBuyProdList`
  - 每日疯抢商品列表
- `GET /coupon/prodListByCouponId`
  - 某张优惠券可用商品列表
- `GET /p/user/collection/prods`
  - 我的收藏商品列表

### 商品详情

- `GET /prod/prodInfo`
  - 商品详情、图文详情、SKU 列表
- `GET /p/user/collection/isCollection`
  - 是否已收藏
- `POST /p/user/collection/addOrCancel`
  - 收藏/取消收藏
- `POST /p/shopCart/changeItem`
  - 加入购物车

### 搜索

- `GET /search/hotSearchByShopId`
  - 热门搜索词
- `GET /search/searchProdPage`
  - 搜索结果列表

## 3.2 购物车与下单

### 购物车

- `GET /p/shopCart/prodCount`
  - 购物车商品数量，主要用于 Tab 角标
- `POST /p/shopCart/info`
  - 购物车明细
- `POST /p/shopCart/totalPay`
  - 已勾选商品的金额试算
- `POST /p/shopCart/changeItem`
  - 修改购物车商品数量 / 添加购物车商品
- `DELETE /p/shopCart/deleteItem`
  - 删除购物车选中商品

### 提交订单

- `POST /p/order/confirm`
  - 下单确认页数据
  - 返回地址、商品、运费、满减、优惠券可用性等
- `POST /p/order/submit`
  - 提交订单
- `POST /p/order/normalPay`
  - 当前项目里实际走的是这个模拟支付/普通支付接口

说明：

- 当前前端已经按“一单只允许一张券”实现，`couponIds` 只传一个
- 当前前端提交订单前会重新请求一次 `/p/order/confirm`，避免订单确认过期

### 支付结果

- `POST /p/order/normalPay`
  - 支付结果页里也支持发起支付

## 3.3 订单中心

- `GET /p/myOrder/orderCount`
  - “我的”页各订单状态数量
- `GET /p/myOrder/myOrder`
  - 订单列表
- `GET /p/myOrder/orderDetail`
  - 订单详情
- `PUT /p/myOrder/cancel/{orderNum}`
  - 取消订单
- `PUT /p/myOrder/receipt/{orderNum}`
  - 确认收货
- `DELETE /p/myOrder/{orderNum}`
  - 删除订单
- `POST /p/order/normalPay`
  - 再次支付
- `POST /p/shopCart/changeItem`
  - 再次购买时，把订单商品重新加入购物车

说明：

- “再次购买”当前不是直接拿订单列表里的字段加购，而是先查 `/p/myOrder/orderDetail`，再逐条调 `/p/shopCart/changeItem`
- 这是因为订单列表返回的商品项字段不全，不能直接用于加购

## 3.4 地址

- `GET /p/address/list`
  - 地址列表
- `PUT /p/address/defaultAddr/{addrId}`
  - 设为默认地址
- `GET /p/address/addrInfo/{addrId}`
  - 地址详情
- `GET /p/area/listByPid`
  - 省市区级联
- `POST /p/address/addAddr`
  - 新增地址
- `PUT /p/address/updateAddr`
  - 编辑地址
- `DELETE /p/address/deleteAddr/{addrId}`
  - 删除地址

## 3.5 优惠券

### 当前已经接通并在用

- `GET /p/coupons/public`
  - 领券中心列表
- `POST /p/coupon/receive/{couponNo}`
  - 领取优惠券
- `GET /p/myCoupons`
  - 我的优惠券列表
- `POST /p/order/confirm`
  - 提交订单页选券时，依赖这个接口返回可用券/不可用券

### 还残留在代码里，但不建议网页端直接照搬

- `GET /coupon/listByProdId`
  - 商品详情页旧领券弹层逻辑
- `GET /p/myCoupon/listCouponIds`
  - 商品详情页旧的“用户已领券 ID 列表”逻辑

这两条明显还是旧版优惠券接口痕迹。当前小程序主链路已经转到：

- 领券中心：`/p/coupons/public`
- 领取：`/p/coupon/receive/{couponNo}`
- 我的优惠券：`/p/myCoupons`
- 订单选券：`/p/order/confirm`

## 3.6 用户中心与账号

- `POST /login`
  - 账号密码登录
- `POST /user/register`
  - 用户注册
- `PUT /user/registerOrBindUser`
  - 绑定手机号
- `POST /p/sms/send`
  - 获取短信验证码
- `GET /p/user/collection/count`
  - 收藏数
- `POST /logOut`
  - 退出登录

## 3.7 公告与物流

- `GET /shop/notice/noticeList`
  - 公告列表
- `GET /shop/notice/info/{id}`
  - 公告详情
- `GET /delivery/check`
  - 物流轨迹

## 4. 页面功能总表

## 4.1 Tab 页面

### `pages/index/index`

作用：

- 首页聚合页
- 展示轮播图、公告、商品标签分组、商品卡片
- 进入搜索、商品详情、公告页、领券中心
- 首页商品支持直接加入购物车

网页端可复用：

- 首页聚合接口组合加载
- 标签分区商品流
- 商品卡片直接加购逻辑

注意：

- 当前首页有额外过滤逻辑，会隐藏商品名里包含“旺仔牛奶”的商品
- “限时特惠”入口和标签已经删掉了

### `pages/category/category`

作用：

- 左侧分类树 + 右侧商品列表
- 切分类后重新拉商品

网页端可复用：

- 标准分类页布局
- 分类切换 + 商品列表联动

### `pages/basket/basket`

作用：

- 展示购物车商品
- 勾选/全选
- 数量增减
- 删除
- 结算前金额试算
- 跳提交订单页

网页端可复用：

- 购物车选中态维护
- 金额试算逻辑
- 批量删除逻辑

### `pages/user/user`

作用：

- 用户中心首页
- 展示订单角标数量、收藏数量
- 进入订单列表、地址、绑定手机、领券中心、我的优惠券、收藏商品

网页端可复用：

- 用户中心入口聚合页

## 4.2 商品与搜索相关页面

### `pages/prod/prod`

作用：

- 商品详情页
- 展示图文详情、SKU 选择、收藏、加购、立即购买

网页端可复用：

- SKU 组合逻辑
- 默认 SKU 推断逻辑
- 商品详情 HTML 适配逻辑
- 收藏切换逻辑

注意：

- 商品详情里仍残留旧优惠券弹层逻辑，不是当前主链路的一部分

### `pages/prod-classify/prod-classify`

作用：

- 通用商品列表页
- 根据 `sts` 复用成多个场景：
  - 标签商品列表
  - 新品推荐
  - 限时特惠
  - 每日疯抢
  - 优惠券商品
  - 我的收藏

网页端可复用：

- 一个列表页组件承接多个来源
- 通过 query 参数决定取数接口

### `pages/search-page/search-page`

作用：

- 搜索入口页
- 展示热门搜索、最近搜索
- 维护本地搜索历史

网页端可复用：

- 搜索建议入口页
- 本地搜索历史

### `pages/search-prod-show/search-prod-show`

作用：

- 搜索结果页
- 支持结果展示模式切换
- 支持不同排序状态重新搜索

网页端可复用：

- 搜索结果页
- 排序切换
- 列表/双列切换

## 4.3 下单与订单相关页面

### `pages/submit-order/submit-order`

作用：

- 下单确认页
- 支持购物车下单和立即购买两种入口
- 选择地址
- 展示优惠券并单选
- 提交订单
- 调普通支付接口

网页端可复用：

- 下单确认模型
- 地址选择
- 单券选择
- 下单前重新 confirm 校验

### `pages/pay-result/pay-result`

作用：

- 支付结果展示
- 支持回订单列表或首页
- 支持再次发起普通支付

网页端可复用：

- 支付完成/失败结果页

### `pages/orderList/orderList`

作用：

- 订单列表
- 状态筛选
- 分页加载
- 取消订单
- 再次支付
- 再次购买
- 查看物流
- 确认收货
- 删除订单

网页端可复用：

- 订单列表页
- 订单操作集合
- “再次购买”重建购物车链路

### `pages/order-detail/order-detail`

作用：

- 订单详情
- 展示商品、地址、金额、备注、时间
- 支持订单商品重新加入购物车

网页端可复用：

- 订单详情展示页

### `pages/express-delivery/express-delivery`

作用：

- 查看物流公司、物流单号、物流轨迹

网页端可复用：

- 物流轨迹页

## 4.4 地址与账号相关页面

### `pages/delivery-address/delivery-address`

作用：

- 地址列表
- 设默认地址
- 进入编辑地址
- 在下单流程里回传选中的地址

网页端可复用：

- 地址管理列表
- 下单地址选择弹窗/页面

### `pages/editAddress/editAddress`

作用：

- 新增/编辑/删除地址
- 省市区三级联动
- 收件人、手机号、详细地址校验

网页端可复用：

- 地址表单
- 行政区联动选择

### `pages/login/login`

作用：

- 账号密码登录/注册

网页端可复用：

- 登录注册一体页

### `pages/binding-phone/binding-phone`

作用：

- 发送短信验证码
- 绑定手机号

网页端可复用：

- 手机号绑定页

注意：

- 这个页面成功后还残留了 `uni.navigateTo`，是旧代码痕迹，网页端重写时不要照抄

## 4.5 优惠券页面

### `pages/coupon-center/coupon-center`

作用：

- 领券中心
- 拉公共优惠券列表
- 领取优惠券

网页端可复用：

- 领券中心页

### `pages/my-coupon/my-coupon`

作用：

- 我的优惠券
- 按状态切换：可用 / 已使用 / 已失效

网页端可复用：

- 我的优惠券页

## 4.6 资讯页面

### `pages/recent-news/recent-news`

作用：

- 公告列表

### `pages/news-detail/news-detail`

作用：

- 公告详情
- 对富文本里的图片宽高做适配处理

网页端可复用：

- 资讯列表页
- 富文本详情页

## 5. 组件复用点

### `components/production`

作用：

- 统一商品卡片
- 点击进入商品详情

网页端建议：

- 抽成通用商品卡片组件

### `components/coupon`

作用：

- 统一优惠券卡片
- 支持三种核心行为：
  - 领取
  - 下单页勾选
  - 跳去使用

网页端建议：

- 抽成通用优惠券卡片
- 通过 props 控制模式，不要拆成多个近似组件

## 6. 网页端迁移建议

后面做网页端时，建议按下面方式拆：

### 6.1 公共层

- `request`：统一 baseURL、token、错误码处理
- `auth`：登录态、退出登录、未登录拦截
- `cart`：购物车数量、购物车刷新事件
- `order`：确认订单、提交订单、支付
- `coupon`：领券中心、我的优惠券、选券

### 6.2 页面优先级

建议按业务主链路先做：

1. 首页
2. 分类页
3. 商品详情页
4. 购物车页
5. 提交订单页
6. 订单列表/订单详情页
7. 我的页
8. 领券中心/我的优惠券

### 6.3 明确不要直接照搬的部分

- 商品详情旧优惠券接口：`/coupon/listByProdId`、`/p/myCoupon/listCouponIds`
- 小程序专有跳转方式：`wx.navigateTo`、`wx.switchTab`
- 小程序本地缓存命名和事件写法
- `binding-phone` 里的 `uni.navigateTo` 旧残留代码
- 首页里历史遗留的 `../logs/logs` 跳转痕迹

## 7. 当前项目里的接口全量去重清单

下面这份更适合做网页端接口模块初始化时直接对照：

- `POST /login`
- `POST /login?grant_type=mini_app`
- `POST /user/register`
- `PUT /user/registerOrBindUser`
- `POST /logOut`
- `PUT /p/user/setUserInfo`
- `GET /indexImgs`
- `GET /shop/notice/topNoticeList`
- `GET /shop/notice/noticeList`
- `GET /shop/notice/info/{id}`
- `GET /category/categoryInfo`
- `GET /prod/tag/prodTagList`
- `GET /prod/prodListByTagId`
- `GET /prod/pageProd`
- `GET /prod/lastedProdPage`
- `GET /prod/discountProdList`
- `GET /prod/moreBuyProdList`
- `GET /prod/prodInfo`
- `GET /search/hotSearchByShopId`
- `GET /search/searchProdPage`
- `GET /p/shopCart/prodCount`
- `POST /p/shopCart/info`
- `POST /p/shopCart/totalPay`
- `POST /p/shopCart/changeItem`
- `DELETE /p/shopCart/deleteItem`
- `POST /p/order/confirm`
- `POST /p/order/submit`
- `POST /p/order/normalPay`
- `GET /p/myOrder/orderCount`
- `GET /p/myOrder/myOrder`
- `GET /p/myOrder/orderDetail`
- `PUT /p/myOrder/cancel/{orderNum}`
- `PUT /p/myOrder/receipt/{orderNum}`
- `DELETE /p/myOrder/{orderNum}`
- `GET /p/address/list`
- `PUT /p/address/defaultAddr/{addrId}`
- `GET /p/address/addrInfo/{addrId}`
- `POST /p/address/addAddr`
- `PUT /p/address/updateAddr`
- `DELETE /p/address/deleteAddr/{addrId}`
- `GET /p/area/listByPid`
- `GET /p/user/collection/count`
- `GET /p/user/collection/prods`
- `GET /p/user/collection/isCollection`
- `POST /p/user/collection/addOrCancel`
- `GET /p/coupons/public`
- `POST /p/coupon/receive/{couponNo}`
- `GET /p/myCoupons`
- `GET /coupon/prodListByCouponId`
- `GET /coupon/listByProdId`
- `GET /p/myCoupon/listCouponIds`
- `POST /p/sms/send`
- `GET /delivery/check`

## 8. 结论

这个小程序的主链路已经比较清楚，网页端完全可以沿用同一套业务模型：

- 商品浏览
- 搜索
- 购物车
- 提交订单
- 普通支付
- 订单中心
- 地址管理
- 优惠券中心

后面如果你让我开始写网页端，我会优先按这份文档把接口层和页面路由骨架先搭起来，然后逐页复用现有业务逻辑，不会再重新摸索一次。
