/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 */

package com.yami.shop.bean.order;

/**
 * Cancel order event sequence.
 */
public interface CancelOrderOrder {

    /**
     * Default execution order.
     */
    int DEFAULT = 0;

    /**
     * Coupon rollback runs after default operations.
     */
    int COUPON = 100;
}
