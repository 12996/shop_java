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
 * 优惠券来源
 *
 * @author mall4j
 */
public enum CouponSourceType {

    /**
     * 后台发放
     */
    ADMIN_SEND(1),

    /**
     * 用户领取
     */
    USER_RECEIVE(2);

    private final Integer value;

    CouponSourceType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}

