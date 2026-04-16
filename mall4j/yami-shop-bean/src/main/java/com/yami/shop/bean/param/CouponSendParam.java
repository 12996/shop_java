/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * 
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.bean.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 按用户发券参数
 *
 * @author mall4j
 */
@Data
public class CouponSendParam {

    /**
     * 优惠券ID
     */
    @NotNull(message = "couponId不能为空")
    private Long couponId;

    /**
     * 用户ID
     */
    @NotBlank(message = "userId不能为空")
    private String userId;
}

