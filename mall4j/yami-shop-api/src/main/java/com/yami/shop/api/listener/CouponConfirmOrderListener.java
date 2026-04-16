/*
 * Copyright (c) 2018-2999 骞垮窞甯傝摑娴峰垱鏂扮鎶€鏈夐檺鍏徃 All rights reserved.
 *
 *
 *
 * 鏈粡鍏佽锛屼笉鍙仛鍟嗕笟鐢ㄩ€旓紒
 *
 * 鐗堟潈鎵€鏈夛紝渚垫潈蹇呯┒锛? */

package com.yami.shop.api.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.shop.bean.app.dto.CouponOrderDto;
import com.yami.shop.bean.app.dto.ShopCartOrderDto;
import com.yami.shop.bean.event.ConfirmOrderEvent;
import com.yami.shop.bean.model.Coupon;
import com.yami.shop.bean.order.ConfirmOrderOrder;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.CouponService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 确认订单时优惠券展示和试算
 *
 * @author mall4j
 */
@Component
@AllArgsConstructor
public class CouponConfirmOrderListener {

    private final CouponService couponService;

    @EventListener(ConfirmOrderEvent.class)
    @Order(ConfirmOrderOrder.COUPON)
    public void couponConfirmOrderEvent(ConfirmOrderEvent event) {
        ShopCartOrderDto shopCartOrderDto = event.getShopCartOrderDto();

        normalizeAmount(shopCartOrderDto);
        shopCartOrderDto.setSelectedCouponId(null);

        // 统一使用当前登录用户，避免参数注入影响试算
        String userId = SecurityUtils.getUser().getUserId();

        List<Coupon> coupons = couponService.listUserUnusedCoupons(userId);
        double amountForCondition = getAmountForCondition(shopCartOrderDto);
        Date now = new Date();

        List<CouponOrderDto> couponDtos = new ArrayList<>(coupons.size());
        for (Coupon coupon : coupons) {
            couponDtos.add(toCouponOrderDto(coupon, amountForCondition, now));
        }

        shopCartOrderDto.setCoupons(couponDtos);
        applySelectedCouponIfPresent(event, shopCartOrderDto, couponDtos);
    }

    private void applySelectedCouponIfPresent(ConfirmOrderEvent event, ShopCartOrderDto shopCartOrderDto, List<CouponOrderDto> couponDtos) {
        List<Long> couponIds = event.getOrderParam().getCouponIds();
        if (CollectionUtil.isEmpty(couponIds)) {
            return;
        }
        if (couponIds.size() > 1) {
            throw new YamiShopBindException("一笔订单仅允许使用一张优惠券");
        }
        Long selectedCouponId = couponIds.get(0);
        if (selectedCouponId == null) {
            return;
        }
        if (event.getAppliedCouponIds() != null && event.getAppliedCouponIds().contains(selectedCouponId)) {
            return;
        }

        Map<Long, CouponOrderDto> couponMap = couponDtos.stream()
                .collect(Collectors.toMap(CouponOrderDto::getCouponId, Function.identity(), (left, right) -> left));
        CouponOrderDto selectedCoupon = couponMap.get(selectedCouponId);
        if (selectedCoupon == null) {
            throw new YamiShopBindException("所选优惠券不可用");
        }
        if (!Boolean.TRUE.equals(selectedCoupon.getCanUse())) {
            throw new YamiShopBindException(selectedCoupon.getUnusableReason());
        }

        double reduceAmount = selectedCoupon.getReduceAmount() == null ? 0D : selectedCoupon.getReduceAmount();
        double payableAmount = shopCartOrderDto.getActualTotal() == null ? 0D : shopCartOrderDto.getActualTotal();
        double couponReduce = Math.min(reduceAmount, payableAmount);

        shopCartOrderDto.setCouponReduce(couponReduce);
        shopCartOrderDto.setShopReduce(Arith.add(shopCartOrderDto.getShopReduce(), couponReduce));
        shopCartOrderDto.setActualTotal(Arith.sub(payableAmount, couponReduce));
        shopCartOrderDto.setSelectedCouponId(selectedCouponId);
        if (event.getAppliedCouponIds() != null) {
            event.getAppliedCouponIds().add(selectedCouponId);
        }
    }

    private CouponOrderDto toCouponOrderDto(Coupon coupon, double amountForCondition, Date now) {
        CouponOrderDto dto = new CouponOrderDto();
        dto.setCouponId(coupon.getCouponId());
        dto.setCouponNo(coupon.getCouponNo());
        dto.setCouponName(coupon.getCouponName());
        dto.setStatus(coupon.getStatus());
        dto.setStartTime(coupon.getStartTime());
        dto.setEndTime(coupon.getEndTime());
        dto.setReduceAmount(toDouble(coupon.getReduceAmount()));
        dto.setConditionAmount(toDouble(coupon.getConditionAmount()));

        boolean canUse = isInValidTime(coupon, now) && isEnoughAmount(coupon, amountForCondition);
        dto.setCanUse(canUse);
        if (canUse) {
            dto.setUnusableReason(null);
            return dto;
        }

        if (!isInValidTime(coupon, now)) {
            dto.setUnusableReason("优惠券不在有效期内");
        } else if (!isEnoughAmount(coupon, amountForCondition)) {
            dto.setUnusableReason("未满足优惠券使用门槛");
        } else {
            dto.setUnusableReason("优惠券不可用");
        }
        return dto;
    }

    private double getAmountForCondition(ShopCartOrderDto shopCartOrderDto) {
        double total = shopCartOrderDto.getTotal() == null ? 0D : shopCartOrderDto.getTotal();
        double discountReduce = shopCartOrderDto.getDiscountReduce() == null ? 0D : shopCartOrderDto.getDiscountReduce();
        return Math.max(0D, Arith.sub(total, discountReduce));
    }

    private boolean isInValidTime(Coupon coupon, Date now) {
        Date startTime = coupon.getStartTime();
        Date endTime = coupon.getEndTime();
        if (startTime != null && now.before(startTime)) {
            return false;
        }
        return endTime == null || !now.after(endTime);
    }

    private boolean isEnoughAmount(Coupon coupon, double amountForCondition) {
        BigDecimal conditionAmount = coupon.getConditionAmount() == null ? BigDecimal.ZERO : coupon.getConditionAmount();
        return BigDecimal.valueOf(amountForCondition).compareTo(conditionAmount) >= 0;
    }

    private double toDouble(BigDecimal amount) {
        return amount == null ? 0D : amount.doubleValue();
    }

    private void normalizeAmount(ShopCartOrderDto shopCartOrderDto) {
        if (shopCartOrderDto.getShopReduce() == null) {
            shopCartOrderDto.setShopReduce(0D);
        }
        if (shopCartOrderDto.getDiscountReduce() == null) {
            shopCartOrderDto.setDiscountReduce(0D);
        }
        if (shopCartOrderDto.getCouponReduce() == null) {
            shopCartOrderDto.setCouponReduce(0D);
        }
    }
}
