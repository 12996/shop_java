/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 */

package com.yami.shop.api.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.yami.shop.bean.app.dto.ShopCartOrderDto;
import com.yami.shop.bean.event.SubmitOrderEvent;
import com.yami.shop.bean.order.SubmitOrderOrder;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.CouponService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Submit stage coupon verification and consume.
 */
@Component
@AllArgsConstructor
public class CouponSubmitOrderListener {

    private final CouponService couponService;

    @EventListener(SubmitOrderEvent.class)
    @Order(SubmitOrderOrder.COUPON)
    public void couponSubmitOrderEvent(SubmitOrderEvent event) {
        List<ShopCartOrderDto> shopCartOrders = event.getMergerOrder().getShopCartOrders();
        if (CollectionUtil.isEmpty(shopCartOrders)) {
            return;
        }

        // Task constraint: only one coupon can be selected in one submit.
        List<ShopCartOrderDto> selectedCouponOrders = shopCartOrders.stream()
                .filter(shopCartOrderDto -> shopCartOrderDto.getSelectedCouponId() != null)
                .collect(Collectors.toList());
        if (selectedCouponOrders.size() > 1) {
            throw new YamiShopBindException("一笔订单仅允许使用一张优惠券");
        }

        long couponReduceCount = shopCartOrders.stream()
                .filter(shopCartOrderDto -> shopCartOrderDto.getCouponReduce() != null && shopCartOrderDto.getCouponReduce() > 0D)
                .count();
        if (couponReduceCount > 1) {
            throw new YamiShopBindException("一笔订单仅允许使用一张优惠券");
        }
        if (couponReduceCount > 0 && selectedCouponOrders.isEmpty()) {
            throw new YamiShopBindException("优惠券信息异常，请重新确认订单");
        }
        if (CollectionUtil.isEmpty(selectedCouponOrders)) {
            return;
        }

        ShopCartOrderDto selectedCouponOrder = selectedCouponOrders.get(0);
        if (StrUtil.isBlank(selectedCouponOrder.getOrderNumber())) {
            throw new YamiShopBindException("订单号不能为空");
        }
        String userId = SecurityUtils.getUser().getUserId();
        BigDecimal amountForCondition = getAmountForCondition(selectedCouponOrder);
        couponService.verifyAndUseCouponForSubmit(
                selectedCouponOrder.getSelectedCouponId(),
                userId,
                selectedCouponOrder.getOrderNumber(),
                amountForCondition
        );
    }

    private BigDecimal getAmountForCondition(ShopCartOrderDto shopCartOrderDto) {
        double total = shopCartOrderDto.getTotal() == null ? 0D : shopCartOrderDto.getTotal();
        double discountReduce = shopCartOrderDto.getDiscountReduce() == null ? 0D : shopCartOrderDto.getDiscountReduce();
        double amountForCondition = Math.max(0D, Arith.sub(total, discountReduce));
        return BigDecimal.valueOf(amountForCondition);
    }
}
