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

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建优惠券参数
 *
 * @author mall4j
 */
@Data
public class CouponCreateParam {

    /**
     * 优惠券唯一标识
     */
    @NotBlank(message = "couponNo不能为空")
    private String couponNo;

    /**
     * 优惠券名称
     */
    @NotBlank(message = "couponName不能为空")
    private String couponName;

    /**
     * 优惠金额
     */
    @NotNull(message = "reduceAmount不能为空")
    @DecimalMin(value = "0.01", message = "reduceAmount必须大于0")
    private BigDecimal reduceAmount;

    /**
     * 使用门槛金额
     */
    @NotNull(message = "conditionAmount不能为空")
    @DecimalMin(value = "0.00", message = "conditionAmount不能小于0")
    private BigDecimal conditionAmount;

    /**
     * 领取方式 1后台指定用户发放 2前台公开领取
     */
    @NotNull(message = "receiveMode不能为空")
    private Integer receiveMode;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 生效时间
     */
    @NotNull(message = "startTime不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 失效时间
     */
    @NotNull(message = "endTime不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 备注
     */
    private String remark;
}

