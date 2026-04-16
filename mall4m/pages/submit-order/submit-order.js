var http = require('../../utils/http.js');

function safeParseStorage(key) {
  var raw = wx.getStorageSync(key);
  if (!raw) {
    return undefined;
  }
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
    orderEntry: '0',
    userAddr: null,
    orderItems: [],
    coupons: {
      totalLength: 0,
      canUseCoupons: [],
      unCanUseCoupons: []
    },
    selectedCoupon: null,
    actualTotal: 0,
    total: 0,
    totalCount: 0,
    transfee: 0,
    reduceAmount: 0,
    shopReduce: 0,
    remark: '',
    couponIds: [],
    submitting: false
  },

  onLoad: function(options) {
    this.setData({
      orderEntry: options.orderEntry || '0'
    });
  },

  loadOrderData: function(done) {
    var addrId = 0;
    if (this.data.userAddr) {
      addrId = this.data.userAddr.addrId;
    }

    wx.showLoading({
      mask: true
    });

    http.request({
      url: '/p/order/confirm',
      method: 'POST',
      data: {
        addrId: addrId,
        orderItem: this.data.orderEntry === '1' ? safeParseStorage('orderItem') : undefined,
        basketIds: this.data.orderEntry === '0' ? safeParseStorage('basketIds') : undefined,
        couponIds: this.data.couponIds,
        userChangeCoupon: 1
      },
      callBack: res => {
        wx.hideLoading();

        var orderItems = [];
        var shopCartOrder = (res.shopCartOrders && res.shopCartOrders[0]) || {};
        var discounts = shopCartOrder.shopCartItemDiscounts || [];
        discounts.forEach(function(itemDiscount) {
          orderItems = orderItems.concat(itemDiscount.shopCartItems || []);
        });

        var allCoupons = shopCartOrder.coupons || [];
        var canUseCoupons = [];
        var unCanUseCoupons = [];
        allCoupons.forEach(coupon => {
          coupon.choose = this.data.couponIds.indexOf(coupon.couponId) !== -1;
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
          transfee: shopCartOrder.transfee || 0,
          shopReduce: shopCartOrder.shopReduce || 0,
          selectedCoupon: canUseCoupons.find(function(item) {
            return item.choose;
          }) || null,
          coupons: {
            totalLength: allCoupons.length,
            canUseCoupons: canUseCoupons,
            unCanUseCoupons: unCanUseCoupons
          }
        });

        if (done) {
          done(true);
        }
      },
      errCallBack: res => {
        wx.hideLoading();
        this.chooseCouponErrHandle(res);
        if (done) {
          done(false);
        }
      }
    });
  },

  chooseCouponErrHandle: function(res) {
    if (res && (res.statusCode === 601 || res.code === 'A00001')) {
      wx.showToast({
        title: (res && (res.data || res.msg)) || '确认订单失败',
        icon: 'none',
        duration: 3000,
        success: () => {
          this.setData({
            couponIds: [],
            selectedCoupon: null
          });
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

  toPay: function() {
    if (this.data.submitting) {
      return;
    }
    if (!this.data.userAddr) {
      wx.showToast({
        title: '请选择地址',
        icon: 'none'
      });
      return;
    }

    this.setData({
      submitting: true
    });

    this.loadOrderData(ok => {
      if (!ok) {
        this.setData({
          submitting: false
        });
        return;
      }
      this.submitOrder();
    });
  },

  submitOrder: function() {
    wx.showLoading({
      mask: true
    });
    http.request({
      url: '/p/order/submit',
      method: 'POST',
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
        this.setData({
          submitting: false
        });
        wx.showToast({
          title: (res && res.msg) || '提交失败',
          icon: 'none'
        });
      }
    });
  },

  calWeixinPay: function(orderNumbers) {
    wx.showLoading({
      mask: true
    });
    http.request({
      url: '/p/order/normalPay',
      method: 'POST',
      data: {
        orderNumbers: orderNumbers
      },
      callBack: res => {
        wx.hideLoading();
        this.setData({
          submitting: false
        });
        if (!res) {
          wx.showToast({
            title: '支付失败',
            icon: 'none'
          });
          return;
        }
        wx.showToast({
          title: '模拟支付成功',
          icon: 'none'
        });
        setTimeout(() => {
          wx.navigateTo({
            url: '/pages/pay-result/pay-result?sts=1&orderNumbers=' + orderNumbers
          });
        }, 1200);
      },
      errCallBack: res => {
        wx.hideLoading();
        this.setData({
          submitting: false
        });
        wx.showToast({
          title: (res && res.msg) || '支付参数获取失败',
          icon: 'none'
        });
      }
    });
  },

  onReady: function() {},

  onShow: function() {
    var pages = getCurrentPages();
    var currPage = pages[pages.length - 1];
    if (currPage.data.selAddress === 'yes') {
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
      url: '/pages/delivery-address/delivery-address?order=0'
    });
  },

  choosedCoupon: function() {
    this.loadOrderData();
    this.setData({
      popupShow: false
    });
  },

  checkCoupon: function(e) {
    var couponId = e.detail.couponId;
    var nextCouponIds = [];
    if (this.data.couponIds[0] !== couponId) {
      nextCouponIds = [couponId];
    }

    var canUseCoupons = (this.data.coupons.canUseCoupons || []).map(function(item) {
      item.choose = nextCouponIds[0] === item.couponId;
      return item;
    });

    this.setData({
      couponIds: nextCouponIds,
      selectedCoupon: canUseCoupons.find(function(item) {
        return item.choose;
      }) || null,
      'coupons.canUseCoupons': canUseCoupons
    });
  }
});
