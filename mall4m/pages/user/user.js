var http = require('../../utils/http.js');
var util = require('../../utils/util.js');

Page({
  data: {
    orderAmount: '',
    sts: '',
    collectionCount: 0
  },

  onLoad: function() {},

  onReady: function() {},

  onShow: function() {
    var ths = this;
    wx.showLoading();
    http.request({
      url: '/p/myOrder/orderCount',
      method: 'GET',
      data: {},
      callBack: function(res) {
        wx.hideLoading();
        ths.setData({
          orderAmount: res
        });
      },
      errCallBack: function() {
        wx.hideLoading();
      }
    });
    this.showCollectionCount();
  },

  onHide: function() {},

  onUnload: function() {},

  onPullDownRefresh: function() {},

  onReachBottom: function() {},

  onShareAppMessage: function() {},

  toDistCenter: function() {
    wx.showToast({
      icon: 'none',
      title: '该功能未开放'
    });
  },

  toCouponCenter: function() {
    wx.navigateTo({
      url: '/pages/coupon-center/coupon-center'
    });
  },

  toMyCouponPage: function() {
    wx.navigateTo({
      url: '/pages/my-coupon/my-coupon'
    });
  },

  toAddressList: function() {
    wx.navigateTo({
      url: '/pages/delivery-address/delivery-address'
    });
  },

  toBindingPhone: function() {
    wx.navigateTo({
      url: '/pages/binding-phone/binding-phone'
    });
  },

  logout: function() {
    http.request({
      url: '/logOut',
      method: 'post',
      callBack: () => {
        util.removeTabBadge();
        wx.removeStorageSync('loginResult');
        wx.removeStorageSync('token');
        wx.showToast({
          title: '退出成功',
          icon: 'none'
        });
        this.setData({
          orderAmount: ''
        });
        setTimeout(function() {
          wx.switchTab({
            url: '/pages/index/index'
          });
        }, 1000);
      }
    });
  },

  toOrderListPage: function(e) {
    var sts = e.currentTarget.dataset.sts;
    wx.navigateTo({
      url: '/pages/orderList/orderList?sts=' + sts
    });
  },

  showCollectionCount: function() {
    var ths = this;
    wx.showLoading();
    http.request({
      url: '/p/user/collection/count',
      method: 'GET',
      data: {},
      callBack: function(res) {
        wx.hideLoading();
        ths.setData({
          collectionCount: res
        });
      },
      errCallBack: function() {
        wx.hideLoading();
      }
    });
  },

  myCollectionHandle: function() {
    var url = '/pages/prod-classify/prod-classify?sts=5';
    var id = 0;
    var title = '我的收藏商品';
    if (id) {
      url += '&tagid=' + id + '&title=' + title;
    }
    wx.navigateTo({
      url: url
    });
  }
});
