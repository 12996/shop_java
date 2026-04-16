// pages/submit-order/submit-order.js
var http = require("../../utils/http.js");

function safeParseStorage(key) {
  const raw = wx.getStorageSync(key);
  if (!raw) return undefined;
  try {
    return JSON.parse(raw);
  } catch (e) {
    return undefined;
  }
}

Page({
  data: {
    popupShow: false,
    couponSts: 1,
    couponList: [],
    // 0: 购物车下单, 1: 立即购买
    orderEntry: "0",
    userAddr: null,
    orderItems: [],
    coupons: {
      totalLength: 0,
      canUseCoupons: [],
      unCanUseCoupons: []
    },
    actualTotal: 0,
    total: 0,
    totalCount: 0,
    transfee: 0,
    reduceAmount: 0,
    remark: "",
    couponIds: [],
    submitting: false
  },

  onLoad: function(options) {
    this.setData({
      orderEntry: options.orderEntry || "0"
    });
  },

  // 加载确认单
  loadOrderData: function(done) {
    var addrId = 0;
    if (this.data.userAddr != null) {
      addrId = this.data.userAddr.addrId;
    }

    wx.showLoading({ mask: true });
    var params = {
      url: "/p/order/confirm",
      method: "POST",
      data: {
        addrId: addrId,
        orderItem: this.data.orderEntry === "1" ? safeParseStorage("orderItem") : undefined,
        basketIds: this.data.orderEntry === "0" ? safeParseStorage("basketIds") : undefined,
        couponIds: this.data.couponIds,
        userChangeCoupon: 1
      },
      callBack: res => {
        wx.hideLoading();

        let orderItems = [];
        const shopCartOrder = (res.shopCartOrders && res.shopCartOrders[0]) || {};
        const discounts = shopCartOrder.shopCartItemDiscounts || [];
        discounts.forEach(itemDiscount => {
          orderItems = orderItems.concat(itemDiscount.shopCartItems || []);
        });

        const allCoupons = shopCartOrder.coupons || [];
        const canUseCoupons = [];
        const unCanUseCoupons = [];
        allCoupons.forEach(coupon => {
          if (coupon.canUse) {
            canUseCoupons.push(coupon);
          } else {
            unCanUseCoupons.push(coupon);
          }
        });

        this.setData({
          orderItems: orderItems,
          actualTotal: res.actualTotal,
          total: res.total,
          totalCount: res.totalCount,
          userAddr: res.userAddr,
          transfee: shopCartOrder.transfee,
          shopReduce: shopCartOrder.shopReduce,
          coupons: {
            totalLength: allCoupons.length,
            canUseCoupons: canUseCoupons,
            unCanUseCoupons: unCanUseCoupons
          }
        });

        if (done) done(true);
      },
      errCallBack: res => {
        wx.hideLoading();
        this.chooseCouponErrHandle(res);
        if (done) done(false);
      }
    };
    http.request(params);
  },

  chooseCouponErrHandle(res) {
    if (res && (res.statusCode == 601 || res.code === "A00001")) {
      wx.showToast({
        title: (res && (res.data || res.msg)) || "确认订单失败",
        icon: "none",
        duration: 3000,
        success: () => {
          this.setData({ couponIds: [] });
        }
      });
      setTimeout(() => {
        this.loadOrderData();
      }, 1200);
    }
  },

  onRemarksInput: function(e) {
    this.setData({
      remark: e.detail.value
    });
  },

  // 提交订单
  toPay: function() {
    if (this.data.submitting) {
      return;
    }
    if (!this.data.userAddr) {
      wx.showToast({
        title: "请选择地址",
        icon: "none"
      });
      return;
    }

    this.setData({ submitting: true });
    // 提交前刷新确认单缓存，避免“订单已过期”
    this.loadOrderData(ok => {
      if (!ok) {
        this.setData({ submitting: false });
        return;
      }
      this.submitOrder();
    });
  },

  submitOrder: function() {
    wx.showLoading({ mask: true });
    var params = {
      url: "/p/order/submit",
      method: "POST",
      data: {
        orderShopParam: [
          {
            remarks: this.data.remark,
            shopId: 1
          }
        ]
      },
      callBack: res => {
        wx.hideLoading();
        this.calWeixinPay(res.orderNumbers);
      },
      errCallBack: res => {
        wx.hideLoading();
        this.setData({ submitting: false });
        wx.showToast({
          title: (res && res.msg) || "提交失败",
          icon: "none"
        });
      }
    };
    http.request(params);
  },

  // 当前后端走的是模拟支付，直接调用 normalPay
  calWeixinPay: function(orderNumbers) {
    wx.showLoading({ mask: true });
    var params = {
      url: "/p/order/normalPay",
      method: "POST",
      data: {
        orderNumbers: orderNumbers
      },
      callBack: res => {
        wx.hideLoading();
        this.setData({ submitting: false });
        if (!res) {
          wx.showToast({
            title: "支付失败",
            icon: "none"
          });
          return;
        }
        wx.showToast({
          title: "模拟支付成功",
          icon: "none"
        });
        setTimeout(() => {
          wx.navigateTo({
            url:
              "/pages/pay-result/pay-result?sts=1&orderNumbers=" +
              orderNumbers +
              "&orderType=" +
              this.data.orderType
          });
        }, 1200);
      },
      errCallBack: res => {
        wx.hideLoading();
        this.setData({ submitting: false });
        wx.showToast({
          title: (res && res.msg) || "支付参数获取失败",
          icon: "none"
        });
      }
    };
    http.request(params);
  },

  onReady: function() {},

  onShow: function() {
    var pages = getCurrentPages();
    var currPage = pages[pages.length - 1];
    if (currPage.data.selAddress == "yes") {
      this.setData({
        userAddr: currPage.data.item
      });
    }
    this.loadOrderData();
  },

  onHide: function() {},
  onUnload: function() {},
  onPullDownRefresh: function() {},
  onReachBottom: function() {},
  onShareAppMessage: function() {},

  changeCouponSts: function(e) {
    this.setData({
      couponSts: e.currentTarget.dataset.sts
    });
  },

  showCouponPopup: function() {
    this.setData({
      popupShow: true
    });
  },

  closePopup: function() {
    this.setData({
      popupShow: false
    });
  },

  toAddrListPage: function() {
    wx.navigateTo({
      url: "/pages/delivery-address/delivery-address?order=0"
    });
  },

  choosedCoupon: function() {
    this.loadOrderData();
    this.setData({
      popupShow: false
    });
  },

  checkCoupon: function(e) {
    var ths = this;
    let index = ths.data.couponIds.indexOf(e.detail.couponId);
    if (index === -1) {
      ths.data.couponIds.push(e.detail.couponId);
    } else {
      ths.data.couponIds.splice(index, 1);
    }
  }
});
