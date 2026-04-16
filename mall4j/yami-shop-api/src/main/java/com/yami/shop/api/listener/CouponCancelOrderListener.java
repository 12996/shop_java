/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 */

package com.yami.shop.api.listener;

import cn.hutool.core.util.StrUtil;
import com.yami.shop.bean.event.CancelOrderEvent;
import com.yami.shop.bean.order.CancelOrderOrder;
import com.yami.shop.service.CouponService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Roll back coupon when order is canceled.
 */
@Component
@AllArgsConstructor
public class CouponCancelOrderListener {

    private final CouponService couponService;

    @EventListener(CancelOrderEvent.class)
    @Order(CancelOrderOrder.COUPON)
    public void rollbackCouponOnCancel(CancelOrderEvent event) {
        if (event == null || event.getOrder() == null || StrUtil.isBlank(event.getOrder().getOrderNumber())) {
            return;
        }
        couponService.rollbackCouponByOrderNumber(event.getOrder().getOrderNumber());
    }
}
