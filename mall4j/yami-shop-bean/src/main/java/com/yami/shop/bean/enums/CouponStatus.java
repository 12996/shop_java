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
 * 优惠券状态
 *
 * @author mall4j
 */
public enum CouponStatus {

    /**
     * 待发放
     */
    WAIT_SEND(0),

    /**
     * 未使用
     */
    UNUSED(1),

    /**
     * 已使用
     */
    USED(2),

    /**
     * 已过期
     */
    EXPIRED(3);

    private final Integer value;

    CouponStatus(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}

