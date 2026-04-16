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

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 优惠券分页查询参数
 *
 * @author mall4j
 */
@Data
public class CouponPageParam {

    /**
     * 优惠券唯一标识
     */
    private String couponNo;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 来源
     */
    private Integer sourceType;

    /**
     * 领取方式
     */
    private Integer receiveMode;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 生效开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeBegin;

    /**
     * 生效结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeEnd;
}

