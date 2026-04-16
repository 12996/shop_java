# 公开可领取优惠券列表接口补充说明

## 1. 背景

当前优惠券用户侧后端接口只有：

- `POST /p/coupon/receive/{couponNo}`
- `GET /p/myCoupons`

这两条接口分别解决：

- 按已知 `couponNo` 领取一张公开券
- 查询当前用户已经拥有的优惠券

但“小程序领券中心”与“商品详情页领券弹层”在展示前，必须先获取“当前有哪些公开券可以领取”。当前后端尚未提供这一能力，因此前端目前没有正确接口可调。

## 2. 当前结论

现在不存在可用于“领券中心列表展示”的现成接口。

因此：

- `GET /p/myCoupons` 不能用于领券中心
- `POST /p/coupon/receive/{couponNo}` 也不能用于先拉列表
- 领券中心功能要继续推进，必须新增“公开可领取优惠券列表接口”

## 3. 建议接口

- `GET /p/coupons/public`

## 4. 建议筛选条件

接口应只返回“当前可公开领取”的优惠券：

- `status = 0`
- `receive_mode = 2`
- `user_id IS NULL`
- `start_time <= now <= end_time`

不应返回：

- 已被领取的券
- 已过期的券
- 尚未生效的券
- 后台定向发放券

## 5. 建议返回字段

- `couponId`
- `couponNo`
- `couponName`
- `reduceAmount`
- `conditionAmount`
- `startTime`
- `endTime`

如前端需要，也可增加：

- `remark`
- `canReceive`
- `unavailableReason`

但最小实现可以先不加扩展字段。

## 6. 使用场景

### 6.1 领券中心

- 前端先调用 `GET /p/coupons/public`
- 展示全部公开可领取券
- 用户点击某张券后，再调用 `POST /p/coupon/receive/{couponNo}`

### 6.2 商品详情页领券弹层

- 前端先调用 `GET /p/coupons/public`
- 展示当前可领取券
- 用户点击领取后，再调用 `POST /p/coupon/receive/{couponNo}`

说明：

- 当前最小方案未做“按商品筛券”或“按店铺筛券”
- 因此商品详情页阶段先展示公开可领券即可，不做商品专属匹配

## 7. 与现有实现的关系

本接口是对当前优惠券最小方案的补齐，不改变现有以下链路：

- 用户领券
- 我的优惠券
- 确认订单试算
- 提交订单核销
- 取消订单退券

本次只先补文档定义，不在本文件中包含代码实现。
