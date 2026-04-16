/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * 
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.bean.enums;

/**
 * 优惠券领取方式
 *
 * @author mall4j
 */
public enum CouponReceiveMode {

    /**
     * 后台指定用户发放
     */
    ADMIN_ASSIGN(1),

    /**
     * 前台公开领取
     */
    PUBLIC_RECEIVE(2);

    private final Integer value;

    CouponReceiveMode(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}

