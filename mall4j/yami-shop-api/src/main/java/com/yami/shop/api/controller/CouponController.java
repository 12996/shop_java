/*
 * Copyright (c) 2018-2999 骞垮窞甯傝摑娴峰垱鏂扮鎶€鏈夐檺鍏徃 All rights reserved.
 *
 *
 *
 * 鏈粡鍏佽锛屼笉鍙仛鍟嗕笟鐢ㄩ€旓紒
 *
 * 鐗堟潈鎵€鏈夛紝渚垫潈蹇呯┒锛? */

package com.yami.shop.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.Coupon;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author mall4j
 */
@RestController
@RequestMapping("/p")
@Tag(name = "鐢ㄦ埛浼樻儬鍒告帴鍙?")
@AllArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/coupons/public")
    @Operation(summary = "鍏紑鍙鍙栦紭鎯犲埜鍒楄〃", description = "鑾峰彇褰撳墠鍙互鍏紑棰嗗彇鐨勪紭鎯犲埜")
    public ServerResponseEntity<List<Coupon>> publicCoupons() {
        return ServerResponseEntity.success(couponService.listPublicAvailableCoupons());
    }

    @PostMapping("/coupon/receive/{couponNo}")
    @Operation(summary = "棰嗗彇鍏紑浼樻儬鍒?", description = "鏍规嵁浼樻儬鍒哥紪鐮侀鍙栧叕寮€鍒?")
    @Parameter(name = "couponNo", description = "浼樻儬鍒哥紪鐮?", required = true)
    public ServerResponseEntity<Void> receiveCoupon(@PathVariable("couponNo") String couponNo) {
        String userId = SecurityUtils.getUser().getUserId();
        couponService.receivePublicCoupon(couponNo, userId);
        return ServerResponseEntity.success();
    }

    @GetMapping("/myCoupons")
    @Operation(summary = "鎴戠殑浼樻儬鍒稿垪琛?", description = "鍒嗛〉鑾峰彇褰撳墠鐢ㄦ埛鐨勪紭鎯犲埜")
    public ServerResponseEntity<IPage<Coupon>> myCoupons(PageParam<Coupon> page,
                                                         @RequestParam(value = "status", required = false) Integer status) {
        String userId = SecurityUtils.getUser().getUserId();
        return ServerResponseEntity.success(couponService.pageMyCoupons(page, userId, status));
    }
}
