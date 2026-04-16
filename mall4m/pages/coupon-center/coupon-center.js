var http = require('../../utils/http.js');

function normalizePage(res) {
  if (Array.isArray(res)) {
    return {
      records: res,
      current: 1,
      pages: 1
    };
  }
  return {
    records: (res && res.records) || [],
    current: (res && res.current) || 1,
    pages: (res && res.pages) || 1
  };
}

function formatCouponList(list) {
  return (list || []).map(function(item) {
    return Object.assign({}, item, {
      canReceive: item.canReceive !== false,
      showUseButton: false
    });
  });
}

Page({
  data: {
    list: [],
    current: 1,
    pages: 1,
    loading: false
  },

  onLoad: function() {
    this.loadCouponList(1);
  },

  onPullDownRefresh: function() {
    this.loadCouponList(1, function() {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom: function() {
    if (this.data.loading || this.data.current >= this.data.pages) {
      return;
    }
    this.loadCouponList(this.data.current + 1);
  },

  loadCouponList: function(pageNum, done) {
    var ths = this;
    this.setData({
      loading: true
    });
    if (pageNum === 1) {
      wx.showLoading({
        mask: true
      });
    }
    http.request({
      url: '/p/coupons/public',
      method: 'GET',
      data: {
        pageNum: pageNum,
        pageSize: 20
      },
      callBack: function(res) {
        var page = normalizePage(res);
        var records = formatCouponList(page.records);
        ths.setData({
          list: pageNum === 1 ? records : ths.data.list.concat(records),
          current: page.current,
          pages: page.pages,
          loading: false
        });
        wx.hideLoading();
        if (done) {
          done();
        }
      },
      errCallBack: function() {
        ths.setData({
          loading: false
        });
        wx.hideLoading();
        if (done) {
          done();
        }
      }
    });
  },

  onReceiveCoupon: function(e) {
    var couponNo = e.detail.couponNo;
    var list = this.data.list.map(function(item) {
      if (item.couponNo === couponNo) {
        item.canReceive = false;
      }
      return item;
    });
    this.setData({
      list: list
    });
  }
});
