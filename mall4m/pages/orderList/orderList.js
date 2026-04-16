var http = require('../../utils/http.js');

Page({
  data: {
    list: [],
    current: 1,
    pages: 0,
    sts: 0
  },

  onLoad: function(options) {
    if (options.sts) {
      this.setData({
        sts: options.sts
      });
      this.loadOrderData(options.sts, 1);
      return;
    }
    this.loadOrderData(0, 1);
  },

  loadOrderData: function(sts, current) {
    var ths = this;
    wx.showLoading();
    http.request({
      url: '/p/myOrder/myOrder',
      method: 'GET',
      data: {
        current: current,
        size: 10,
        status: sts
      },
      callBack: function(res) {
        var list = [];
        if (res.current === 1) {
          list = res.records || [];
        } else {
          list = ths.data.list.slice();
          Array.prototype.push.apply(list, res.records || []);
        }
        ths.setData({
          list: list,
          pages: res.pages,
          current: res.current
        });
        wx.hideLoading();
      },
      errCallBack: function() {
        wx.hideLoading();
      }
    });
  },

  onStsTap: function(e) {
    var sts = e.currentTarget.dataset.sts;
    this.setData({
      sts: sts
    });
    this.loadOrderData(sts, 1);
  },

  onReady: function() {},
  onShow: function() {},
  onHide: function() {},
  onUnload: function() {},
  onPullDownRefresh: function() {},

  onReachBottom: function() {
    if (this.data.current < this.data.pages) {
      this.loadOrderData(this.data.sts, this.data.current + 1);
    }
  },

  onShareAppMessage: function() {},

  toDeliveryPage: function(e) {
    wx.navigateTo({
      url: '/pages/express-delivery/express-delivery?orderNum=' + e.currentTarget.dataset.ordernum
    });
  },

  onCancelOrder: function(e) {
    var ordernum = e.currentTarget.dataset.ordernum;
    var ths = this;
    wx.showModal({
      title: '',
      content: '要取消此订单？',
      confirmColor: '#3e62ad',
      cancelColor: '#3e62ad',
      cancelText: '否',
      confirmText: '是',
      success: function(res) {
        if (!res.confirm) {
          return;
        }
        wx.showLoading({
          mask: true
        });
        http.request({
          url: '/p/myOrder/cancel/' + ordernum,
          method: 'PUT',
          data: {},
          callBack: function() {
            ths.loadOrderData(ths.data.sts, 1);
            wx.hideLoading();
          },
          errCallBack: function() {
            wx.hideLoading();
          }
        });
      }
    });
  },

  onPayAgain: function(e) {
    wx.showLoading({
      mask: true
    });
    http.request({
      url: '/p/order/normalPay',
      method: 'POST',
      data: {
        orderNumbers: e.currentTarget.dataset.ordernum
      },
      callBack: res => {
        wx.hideLoading();
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
        setTimeout(function() {
          wx.navigateTo({
            url: '/pages/pay-result/pay-result?sts=1&orderNumbers=' + e.currentTarget.dataset.ordernum
          });
        }, 1200);
      },
      errCallBack: res => {
        wx.hideLoading();
        wx.showToast({
          title: (res && res.msg) || '支付失败',
          icon: 'none'
        });
      }
    });
  },

  onBuyAgain: function(e) {
    var orderIndex = e.currentTarget.dataset.index;
    var order = this.data.list[orderIndex];
    if (!order || !order.orderNumber) {
      wx.showToast({
        title: '订单商品不存在',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      mask: true
    });

    http.request({
      url: '/p/myOrder/orderDetail',
      method: 'GET',
      data: {
        orderNumber: order.orderNumber
      },
      callBack: detail => {
        var items = (detail && detail.orderItemDtos) || [];
        var shopId = detail && detail.shopId;

        if (!items.length || !shopId) {
          wx.hideLoading();
          wx.showToast({
            title: '订单详情不完整',
            icon: 'none'
          });
          return;
        }

        this.addOrderItemsToCart(items, shopId, 0);
      },
      errCallBack: res => {
        wx.hideLoading();
        wx.showToast({
          title: (res && res.msg) || '订单详情加载失败',
          icon: 'none'
        });
      }
    });
  },

  addOrderItemsToCart: function(items, shopId, index) {
    var ths = this;

    if (index >= items.length) {
      wx.hideLoading();
      wx.showToast({
        title: '已加入购物车',
        icon: 'none'
      });
      setTimeout(function() {
        wx.switchTab({
          url: '/pages/basket/basket'
        });
      }, 800);
      return;
    }

    var item = items[index];
    if (!item || !item.prodId || !item.skuId || !item.prodCount) {
      wx.hideLoading();
      wx.showToast({
        title: '订单商品数据异常',
        icon: 'none'
      });
      return;
    }

    http.request({
      url: '/p/shopCart/changeItem',
      method: 'POST',
      data: {
        basketId: 0,
        count: item.prodCount,
        prodId: item.prodId,
        shopId: shopId,
        skuId: item.skuId
      },
      callBack: function() {
        ths.addOrderItemsToCart(items, shopId, index + 1);
      },
      errCallBack: function(res) {
        wx.hideLoading();
        wx.showToast({
          title: (res && res.msg) || '加入购物车失败',
          icon: 'none'
        });
      }
    });
  },

  toOrderDetailPage: function(e) {
    wx.navigateTo({
      url: '/pages/order-detail/order-detail?orderNum=' + e.currentTarget.dataset.ordernum
    });
  },

  onConfirmReceive: function(e) {
    var ths = this;
    wx.showModal({
      title: '',
      content: '我已收到货？',
      confirmColor: '#eb2444',
      success: function(res) {
        if (!res.confirm) {
          return;
        }
        wx.showLoading({
          mask: true
        });
        http.request({
          url: '/p/myOrder/receipt/' + e.currentTarget.dataset.ordernum,
          method: 'PUT',
          data: {},
          callBack: function() {
            ths.loadOrderData(ths.data.sts, 1);
            wx.hideLoading();
          },
          errCallBack: function() {
            wx.hideLoading();
          }
        });
      }
    });
  },

  delOrderList: function(e) {
    var ths = this;
    wx.showModal({
      title: '',
      content: '确定要删除此订单吗？',
      confirmColor: '#eb2444',
      success: function(res) {
        if (!res.confirm) {
          return;
        }
        var ordernum = e.currentTarget.dataset.ordernum;
        wx.showLoading();
        http.request({
          url: '/p/myOrder/' + ordernum,
          method: 'DELETE',
          data: {},
          callBack: function() {
            ths.loadOrderData(ths.data.sts, 1);
            wx.hideLoading();
          },
          errCallBack: function() {
            wx.hideLoading();
          }
        });
      }
    });
  }
});
