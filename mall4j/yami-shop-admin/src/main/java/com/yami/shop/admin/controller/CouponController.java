/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * 
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.Coupon;
import com.yami.shop.bean.param.CouponCreateParam;
import com.yami.shop.bean.param.CouponPageParam;
import com.yami.shop.bean.param.CouponSendParam;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券管理
 *
 * @author mall4j
 */
@RestController
@RequestMapping("/admin/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 分页查询优惠券
     *
     * @param couponPageParam 查询参数
     * @param page 分页参数
     * @return 分页结果
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('admin:coupon:page')")
    public ServerResponseEntity<IPage<Coupon>> page(CouponPageParam couponPageParam, PageParam<Coupon> page) {
        IPage<Coupon> couponPage = couponService.page(page, new LambdaQueryWrapper<Coupon>()
                .eq(couponPageParam.getStatus() != null, Coupon::getStatus, couponPageParam.getStatus())
                .eq(couponPageParam.getSourceType() != null, Coupon::getSourceType, couponPageParam.getSourceType())
                .eq(couponPageParam.getReceiveMode() != null, Coupon::getReceiveMode, couponPageParam.getReceiveMode())
                .eq(StrUtil.isNotBlank(couponPageParam.getUserId()), Coupon::getUserId, couponPageParam.getUserId())
                .like(StrUtil.isNotBlank(couponPageParam.getCouponNo()), Coupon::getCouponNo, couponPageParam.getCouponNo())
                .like(StrUtil.isNotBlank(couponPageParam.getCouponName()), Coupon::getCouponName, couponPageParam.getCouponName())
                .ge(couponPageParam.getStartTimeBegin() != null, Coupon::getStartTime, couponPageParam.getStartTimeBegin())
                .le(couponPageParam.getStartTimeEnd() != null, Coupon::getStartTime, couponPageParam.getStartTimeEnd())
                .orderByDesc(Coupon::getCreateTime));
        return ServerResponseEntity.success(couponPage);
    }

    /**
     * 查询优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    @GetMapping("/{couponId}")
    @PreAuthorize("@pms.hasPermission('admin:coupon:info')")
    public ServerResponseEntity<Coupon> info(@PathVariable("couponId") Long couponId) {
        return ServerResponseEntity.success(couponService.getById(couponId));
    }

    /**
     * 创建优惠券
     *
     * @param couponCreateParam 创建参数
     * @return 优惠券ID
     */
    @SysLog("创建优惠券")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('admin:coupon:save')")
    public ServerResponseEntity<Long> save(@RequestBody @Valid CouponCreateParam couponCreateParam) {
        return ServerResponseEntity.success(couponService.createCoupon(couponCreateParam));
    }

    /**
     * 按用户发券
     *
     * @param couponSendParam 发券参数
     * @return void
     */
    @SysLog("按用户发券")
    @PostMapping("/send")
    @PreAuthorize("@pms.hasPermission('admin:coupon:send')")
    public ServerResponseEntity<Void> send(@RequestBody @Valid CouponSendParam couponSendParam) {
        couponService.sendCouponToUser(couponSendParam);
        return ServerResponseEntity.success();
    }

    /**
     * 作废优惠券
     *
     * @param couponId 优惠券ID
     * @return void
     */
    @SysLog("作废优惠券")
    @PostMapping("/{couponId}/invalidate")
    @PreAuthorize("@pms.hasPermission('admin:coupon:invalidate')")
    public ServerResponseEntity<Void> invalidate(@PathVariable("couponId") Long couponId) {
        couponService.invalidateCoupon(couponId);
        return ServerResponseEntity.success();
    }
}

