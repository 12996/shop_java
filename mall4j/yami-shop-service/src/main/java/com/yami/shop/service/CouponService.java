/*
 * Copyright (c) 2018-2999 骞垮窞甯傝摑娴峰垱鏂扮鎶€鏈夐檺鍏徃 All rights reserved.
 *
 *
 *
 * 鏈粡鍏佽锛屼笉鍙仛鍟嗕笟鐢ㄩ€旓紒
 *
 * 鐗堟潈鎵€鏈夛紝渚垫潈蹇呯┒锛? */

package com.yami.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.Coupon;
import com.yami.shop.bean.param.CouponCreateParam;
import com.yami.shop.bean.param.CouponSendParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 浼樻儬鍒?
 *
 * @author mall4j
 */
public interface CouponService extends IService<Coupon> {

    Long createCoupon(CouponCreateParam couponCreateParam);

    void sendCouponToUser(CouponSendParam couponSendParam);

    void invalidateCoupon(Long couponId);

    void receivePublicCoupon(String couponNo, String userId);

    List<Coupon> listPublicAvailableCoupons();

    IPage<Coupon> pageMyCoupons(Page<Coupon> page, String userId, Integer status);

    List<Coupon> listUserUnusedCoupons(String userId);

    void verifyAndUseCouponForSubmit(Long couponId, String userId, String orderNumber, BigDecimal amountForCondition);

    void rollbackCouponByOrderNumber(String orderNumber);
}
