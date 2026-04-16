/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 */

package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Single shop order in confirm/submit flow.
 *
 * @author lanhai
 */
@Data
public class ShopCartOrderDto implements Serializable {

    @Schema(description = "Shop id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "Shop name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "Actual total amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double actualTotal;

    @Schema(description = "Product total amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double total;

    @Schema(description = "Total product count", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCount;

    @Schema(description = "Freight amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double transfee;

    @Schema(description = "Promotion discount amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double discountReduce;

    @Schema(description = "Coupon discount amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double couponReduce;

    @Schema(description = "Shop reduce amount", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double shopReduce = 0.0;

    @Schema(description = "Order remarks", requiredMode = Schema.RequiredMode.REQUIRED)
    private String remarks;

    @Schema(description = "Cart items by discount group", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ShopCartItemDiscountDto> shopCartItemDiscounts;

    @Schema(description = "Coupon list for this shop order", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CouponOrderDto> coupons;

    @Schema(description = "Selected coupon id for submit stage")
    private Long selectedCouponId;

    @Schema(description = "Order number", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNumber;
}
