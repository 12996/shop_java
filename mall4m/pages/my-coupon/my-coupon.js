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

function getCouponTypeTag(status) {
  if (status === 2) {
    return 1;
  }
  if (status === 3) {
    return 2;
  }
  return 0;
}

Page({
  data: {
    sts: 1,
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

  changeSts: function(e) {
    var sts = Number(e.currentTarget.dataset.sts);
    if (sts === this.data.sts) {
      return;
    }
    this.setData({
      sts: sts,
      list: [],
      current: 1,
      pages: 1
    });
    this.loadCouponList(1);
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
      url: '/p/myCoupons',
      method: 'GET',
      data: {
        pageNum: pageNum,
        pageSize: 20,
        status: this.data.sts
      },
      callBack: function(res) {
        var page = normalizePage(res);
        var records = (page.records || []).map(function(item) {
          return Object.assign({}, item, {
            canReceive: false,
            showUseButton: false,
            couponTypeTag: getCouponTypeTag(item.status)
          });
        });
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
  }
});
