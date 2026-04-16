/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * 
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.bean.app.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lanhai
 */
@Data
public class CouponOrderDto implements Serializable {

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券编号
     */
    private String couponNo;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠金额
     */
    private Double reduceAmount;

    /**
     * 使用门槛金额
     */
    private Double conditionAmount;

    /**
     * 当前状态
     */
    private Integer status;

    /**
     * 是否可用于当前店铺订单
     */
    private Boolean canUse;

    /**
     * 不可用原因
     */
    private String unusableReason;

    /**
     * 生效时间
     */
    private Date startTime;

    /**
     * 失效时间
     */
    private Date endTime;
}
