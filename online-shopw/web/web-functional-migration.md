# 商城 Web 版：小程序功能迁移计划蓝皮书

> 此文档基于现有的微信小程序端 `mall4m/pages` 的业务逻辑推导而出。以帮助团队后续全面对接。

## 目标与背景
本项目已经在宽屏中部署了符合“Web 高级视觉感”的 Vue 3 组件框架体系。为了形成一个端对端商城全流程应用，现在需要将原生小程序的接口拉取与业务交互等价转移过来。

## 页面模块路由对应表

下述内容已梳理出了从“移动端 Page”向“Web 宽屏界面”转变所需的组件和网络钩子关系。

### 主流程：商品流展示与购买池
| 业务角色 | 小程序源路径 | Web Vue 目标对应 | 接口对接侧重点 |
| :------- | :----------- | :--------------- | :------------- |
| **聚合首页** | `pages/index` | `views/Home/index.vue` | `/indexImgs` (展示 Banner) <br/> `/prod/prodListByTagId` (推荐商品网格加载) |
| **类目查找** | `pages/category` | `views/Category/index.vue` | `/category/categoryInfo` <br/> 左宽泛父级+右多图平铺 |
| **商品展示** | `pages/prod` | `views/Product/index.vue` | 提供左侧超清大图粘性固定，右侧滚动显示富文本与 SKU 选择器的左右并排 Layout |
| **购物车** | `pages/basket` | `views/Cart/index.vue` | 结合 `utils/http` 获取 `/p/shopCart/info` 动态变动总额 |
| **下单台** | `pages/submit-order` | `views/Checkout/index.vue` | 地址关联与 `/p/order/confirm` 拉取合计单 |

### 辅助流：个人业务操作台 (挂载至 User 下级)
| 业务角色 | 小程序源路径 | Web Vue 目标对应 | 接口对接侧重点 |
| :------- | :----------- | :--------------- | :------------- |
| **个人主板** | `pages/user` | `views/User/index.vue` | 极简数据仪表面板，请求 `/p/myOrder/orderCount` 控制状态 |
| **我的订单** | `pages/orderList` | `views/User/Order.vue` | 各状态单页过滤显示，带有再次购买与取消控制组件 |
| **收货地址** | `pages/delivery-address` | `views/User/Address.vue` | 展示各地址，带默认标签判断与增改弹窗 (`/p/address/list`) |

## 后续开发要点
- 因在 Web 下我们使用了**左侧固定导航 (Sidebar)** 作为路由中枢，像“搜索”（`search-page`）可以直接内嵌一个全站顶栏或悬浮搜索框（Ctrl+K），无须像小程序必须专开新页面，来改善宽屏操作的人机交互效率。
