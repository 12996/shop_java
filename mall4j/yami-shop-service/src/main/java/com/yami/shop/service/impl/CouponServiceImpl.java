/*
 * Copyright (c) 2018-2999 骞垮窞甯傝摑娴峰垱鏂扮鎶€鏈夐檺鍏徃 All rights reserved.
 */

package com.yami.shop.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.enums.CouponReceiveMode;
import com.yami.shop.bean.enums.CouponSourceType;
import com.yami.shop.bean.enums.CouponStatus;
import com.yami.shop.bean.model.Coupon;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.param.CouponCreateParam;
import com.yami.shop.bean.param.CouponSendParam;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.dao.CouponMapper;
import com.yami.shop.service.CouponService;
import com.yami.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Coupon service.
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Autowired
    private UserService userService;

    @Override
    public Long createCoupon(CouponCreateParam couponCreateParam) {
        validateBaseFields(couponCreateParam);
        if (!Objects.equals(couponCreateParam.getReceiveMode(), CouponReceiveMode.ADMIN_ASSIGN.value())
                && !Objects.equals(couponCreateParam.getReceiveMode(), CouponReceiveMode.PUBLIC_RECEIVE.value())) {
            throw new YamiShopBindException("receiveMode涓嶅悎娉?");
        }
        Coupon dbCoupon = this.getOne(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getCouponNo, couponCreateParam.getCouponNo()));
        if (dbCoupon != null) {
            throw new YamiShopBindException("couponNo宸插瓨鍦?");
        }

        Date now = new Date();
        Coupon coupon = new Coupon();
        coupon.setCouponNo(couponCreateParam.getCouponNo());
        coupon.setCouponName(couponCreateParam.getCouponName());
        coupon.setReduceAmount(couponCreateParam.getReduceAmount());
        coupon.setConditionAmount(couponCreateParam.getConditionAmount());
        coupon.setStartTime(couponCreateParam.getStartTime());
        coupon.setEndTime(couponCreateParam.getEndTime());
        coupon.setRemark(couponCreateParam.getRemark());
        coupon.setCreateTime(now);
        coupon.setUpdateTime(now);

        if (Objects.equals(couponCreateParam.getReceiveMode(), CouponReceiveMode.ADMIN_ASSIGN.value())) {
            validateUser(couponCreateParam.getUserId());
            coupon.setUserId(couponCreateParam.getUserId());
            coupon.setStatus(CouponStatus.UNUSED.value());
            coupon.setSourceType(CouponSourceType.ADMIN_SEND.value());
            coupon.setReceiveMode(CouponReceiveMode.ADMIN_ASSIGN.value());
            coupon.setReceiveTime(now);
        } else {
            coupon.setStatus(CouponStatus.WAIT_SEND.value());
            coupon.setSourceType(CouponSourceType.USER_RECEIVE.value());
            coupon.setReceiveMode(CouponReceiveMode.PUBLIC_RECEIVE.value());
        }

        this.save(coupon);
        return coupon.getCouponId();
    }

    @Override
    public void sendCouponToUser(CouponSendParam couponSendParam) {
        validateUser(couponSendParam.getUserId());
        Coupon dbCoupon = this.getById(couponSendParam.getCouponId());
        if (dbCoupon == null) {
            throw new YamiShopBindException("浼樻儬鍒镐笉瀛樺湪");
        }
        if (!Objects.equals(dbCoupon.getStatus(), CouponStatus.WAIT_SEND.value())) {
            throw new YamiShopBindException("褰撳墠浼樻儬鍒哥姸鎬佷笉鍏佽鍙戞斁");
        }
        Date now = new Date();
        boolean updated = this.update(new LambdaUpdateWrapper<Coupon>()
                .set(Coupon::getStatus, CouponStatus.UNUSED.value())
                .set(Coupon::getSourceType, CouponSourceType.ADMIN_SEND.value())
                .set(Coupon::getReceiveMode, CouponReceiveMode.ADMIN_ASSIGN.value())
                .set(Coupon::getUserId, couponSendParam.getUserId())
                .set(Coupon::getReceiveTime, now)
                .set(Coupon::getUpdateTime, now)
                .eq(Coupon::getCouponId, couponSendParam.getCouponId())
                .eq(Coupon::getStatus, CouponStatus.WAIT_SEND.value()));
        if (!updated) {
            throw new YamiShopBindException("鍙戝埜澶辫触锛岃閲嶈瘯");
        }
    }

    @Override
    public void invalidateCoupon(Long couponId) {
        Coupon dbCoupon = this.getById(couponId);
        if (dbCoupon == null) {
            throw new YamiShopBindException("浼樻儬鍒镐笉瀛樺湪");
        }
        if (Objects.equals(dbCoupon.getStatus(), CouponStatus.USED.value())) {
            throw new YamiShopBindException("宸蹭娇鐢ㄤ紭鎯犲埜涓嶈兘浣滃簾");
        }
        if (Objects.equals(dbCoupon.getStatus(), CouponStatus.EXPIRED.value())) {
            return;
        }
        boolean updated = this.update(new LambdaUpdateWrapper<Coupon>()
                .set(Coupon::getStatus, CouponStatus.EXPIRED.value())
                .set(Coupon::getUpdateTime, new Date())
                .eq(Coupon::getCouponId, couponId)
                .in(Coupon::getStatus, CouponStatus.WAIT_SEND.value(), CouponStatus.UNUSED.value()));
        if (!updated) {
            throw new YamiShopBindException("浣滃簾澶辫触锛岃鍒锋柊鍚庨噸璇?");
        }
    }

    @Override
    public void receivePublicCoupon(String couponNo, String userId) {
        if (StrUtil.isBlank(couponNo)) {
            throw new YamiShopBindException("couponNo涓嶈兘涓虹┖");
        }
        if (StrUtil.isBlank(userId)) {
            throw new YamiShopBindException("鐢ㄦ埛鏈櫥褰?");
        }
        Date now = new Date();
        int affect = baseMapper.update(null, new LambdaUpdateWrapper<Coupon>()
                .set(Coupon::getStatus, CouponStatus.UNUSED.value())
                .set(Coupon::getSourceType, CouponSourceType.USER_RECEIVE.value())
                .set(Coupon::getUserId, userId)
                .set(Coupon::getReceiveTime, now)
                .set(Coupon::getUpdateTime, now)
                .eq(Coupon::getCouponNo, couponNo)
                .eq(Coupon::getReceiveMode, CouponReceiveMode.PUBLIC_RECEIVE.value())
                .eq(Coupon::getStatus, CouponStatus.WAIT_SEND.value())
                .isNull(Coupon::getUserId)
                .le(Coupon::getStartTime, now)
                .ge(Coupon::getEndTime, now));
        if (affect < 1) {
            throw new YamiShopBindException("浼樻儬鍒镐笉瀛樺湪鎴栦笉鍙鍙?");
        }
    }

    @Override
    public List<Coupon> listPublicAvailableCoupons() {
        Date now = new Date();
        return this.list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, CouponStatus.WAIT_SEND.value())
                .eq(Coupon::getReceiveMode, CouponReceiveMode.PUBLIC_RECEIVE.value())
                .isNull(Coupon::getUserId)
                .le(Coupon::getStartTime, now)
                .ge(Coupon::getEndTime, now)
                .orderByAsc(Coupon::getEndTime)
                .orderByDesc(Coupon::getCouponId));
    }

    @Override
    public IPage<Coupon> pageMyCoupons(Page<Coupon> page, String userId, Integer status) {
        if (StrUtil.isBlank(userId)) {
            throw new YamiShopBindException("鐢ㄦ埛鏈櫥褰?");
        }
        LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getUserId, userId)
                .orderByDesc(Coupon::getReceiveTime)
                .orderByDesc(Coupon::getCouponId);
        if (status != null) {
            queryWrapper.eq(Coupon::getStatus, status);
        }
        return this.page(page, queryWrapper);
    }

    @Override
    public List<Coupon> listUserUnusedCoupons(String userId) {
        if (StrUtil.isBlank(userId)) {
            throw new YamiShopBindException("鐢ㄦ埛鏈櫥褰?");
        }
        return this.list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getUserId, userId)
                .eq(Coupon::getStatus, CouponStatus.UNUSED.value())
                .orderByAsc(Coupon::getEndTime)
                .orderByDesc(Coupon::getCouponId));
    }

    @Override
    public void verifyAndUseCouponForSubmit(Long couponId, String userId, String orderNumber, BigDecimal amountForCondition) {
        if (couponId == null) {
            throw new YamiShopBindException("couponId涓嶈兘涓虹┖");
        }
        if (StrUtil.isBlank(userId)) {
            throw new YamiShopBindException("鐢ㄦ埛鏈櫥褰?");
        }
        if (StrUtil.isBlank(orderNumber)) {
            throw new YamiShopBindException("璁㈠崟鍙蜂笉鑳戒负绌?");
        }
        BigDecimal submitAmount = amountForCondition == null ? BigDecimal.ZERO : amountForCondition;
        Date now = new Date();

        Coupon coupon = this.getOne(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getCouponId, couponId)
                .eq(Coupon::getUserId, userId)
                .eq(Coupon::getStatus, CouponStatus.UNUSED.value())
                .last("limit 1"));
        if (coupon == null) {
            throw new YamiShopBindException("浼樻儬鍒镐笉瀛樺湪鎴栦笉鍙娇鐢?");
        }
        if (coupon.getStartTime() != null && now.before(coupon.getStartTime())) {
            throw new YamiShopBindException("浼樻儬鍒镐笉鍦ㄦ湁鏁堟湡鍐?");
        }
        if (coupon.getEndTime() != null && now.after(coupon.getEndTime())) {
            throw new YamiShopBindException("浼樻儬鍒镐笉鍦ㄦ湁鏁堟湡鍐?");
        }
        BigDecimal conditionAmount = coupon.getConditionAmount() == null ? BigDecimal.ZERO : coupon.getConditionAmount();
        if (submitAmount.compareTo(conditionAmount) < 0) {
            throw new YamiShopBindException("鏈弧瓒充紭鎯犲埜浣跨敤闂ㄦ");
        }

        int affect = baseMapper.update(null, new LambdaUpdateWrapper<Coupon>()
                .set(Coupon::getStatus, CouponStatus.USED.value())
                .set(Coupon::getOrderNumber, orderNumber)
                .set(Coupon::getUseTime, now)
                .set(Coupon::getUpdateTime, now)
                .eq(Coupon::getCouponId, couponId)
                .eq(Coupon::getUserId, userId)
                .eq(Coupon::getStatus, CouponStatus.UNUSED.value()));
        if (affect < 1) {
            throw new YamiShopBindException("浼樻儬鍒告牳閿€澶辫触锛岃閲嶈瘯");
        }
    }

    @Override
    public void rollbackCouponByOrderNumber(String orderNumber) {
        if (StrUtil.isBlank(orderNumber)) {
            return;
        }
        Date now = new Date();
        baseMapper.update(null, new LambdaUpdateWrapper<Coupon>()
                .set(Coupon::getStatus, CouponStatus.UNUSED.value())
                .set(Coupon::getOrderNumber, null)
                .set(Coupon::getUseTime, null)
                .set(Coupon::getUpdateTime, now)
                .eq(Coupon::getOrderNumber, orderNumber)
                .eq(Coupon::getStatus, CouponStatus.USED.value()));
    }

    private void validateBaseFields(CouponCreateParam couponCreateParam) {
        if (couponCreateParam.getStartTime().after(couponCreateParam.getEndTime())) {
            throw new YamiShopBindException("startTime涓嶈兘鏅氫簬endTime");
        }
        if (couponCreateParam.getReduceAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new YamiShopBindException("reduceAmount蹇呴』澶т簬0");
        }
        if (couponCreateParam.getConditionAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new YamiShopBindException("conditionAmount涓嶈兘灏忎簬0");
        }
    }

    private void validateUser(String userId) {
        if (StrUtil.isBlank(userId)) {
            throw new YamiShopBindException("userId涓嶈兘涓虹┖");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new YamiShopBindException("鐢ㄦ埛涓嶅瓨鍦?");
        }
    }
}
