/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * 
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券
 *
 * @author mall4j
 */
@Data
@TableName("tz_coupon")
public class Coupon implements Serializable {

    /**
     * 优惠券ID
     */
    @TableId
    private Long couponId;

    /**
     * 优惠券唯一标识
     */
    private String couponNo;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠金额
     */
    private BigDecimal reduceAmount;

    /**
     * 使用门槛金额
     */
    private BigDecimal conditionAmount;

    /**
     * 状态 0待发放 1未使用 2已使用 3已过期
     */
    private Integer status;

    /**
     * 来源 1后台发放 2用户领取
     */
    private Integer sourceType;

    /**
     * 领取方式 1后台指定用户发放 2前台公开领取
     */
    private Integer receiveMode;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 使用该券的订单号
     */
    private String orderNumber;

    /**
     * 生效时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 失效时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 发放/领取时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;

    /**
     * 使用时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}

