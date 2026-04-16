// pages/pay-result/pay-result.js
var http = require("../../utils/http.js");

Page({
  data: {
    sts: 0,
    orderNumbers: ""
  },

  onLoad: function(options) {
    this.setData({
      sts: options.sts,
      orderNumbers: options.orderNumbers
    });
  },

  toOrderList: function() {
    wx.navigateTo({
      url: "/pages/orderList/orderList?sts=0"
    });
  },

  toIndex: function() {
    wx.switchTab({
      url: "/pages/index/index"
    });
  },

  payAgain: function() {
    wx.showLoading({
      mask: true
    });
    var params = {
      url: "/p/order/normalPay",
      method: "POST",
      data: {
        orderNumbers: this.data.orderNumbers
      },
      callBack: res => {
        wx.hideLoading();
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
          wx.redirectTo({
            url: "/pages/pay-result/pay-result?sts=1&orderNumbers=" + this.data.orderNumbers
          });
        }, 1200);
      },
      errCallBack: res => {
        wx.hideLoading();
        wx.showToast({
          title: (res && res.msg) || "支付失败",
          icon: "none"
        });
      }
    };
    http.request(params);
  },

  onReady: function() {},
  onShow: function() {},
  onHide: function() {},
  onUnload: function() {},
  onPullDownRefresh: function() {},
  onReachBottom: function() {},
  onShareAppMessage: function() {}
});
