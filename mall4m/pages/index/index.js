var http = require('../../utils/http.js');
var config = require('../../utils/config.js');
const app = getApp();

Page({
  data: {
    indicatorDots: true,
    indicatorColor: '#d1e5fb',
    indicatorActiveColor: '#1b7dec',
    autoplay: true,
    interval: 2000,
    duration: 1000,
    indexImgs: [],
    seq: 0,
    news: [],
    taglist: [],
    sts: 0,
    scrollTop: 0
  },

  bindViewTap: function() {
    wx.navigateTo({
      url: '../logs/logs'
    });
  },

  onLoad: function() {
    this.getAllData();
  },

  onPageScroll: function(e) {
    this.setData({
      scrollTop: e.scrollTop
    });
  },

  toProdPage: function(e) {
    var prodid = e.currentTarget.dataset.prodid;
    if (prodid) {
      wx.navigateTo({
        url: '/pages/prod/prod?prodid=' + prodid
      });
    }
  },

  toCouponCenter: function() {
    wx.navigateTo({
      url: '/pages/coupon-center/coupon-center'
    });
  },

  toSearchPage: function() {
    wx.navigateTo({
      url: '/pages/search-page/search-page'
    });
  },

  toClassifyPage: function(e) {
    var url = '/pages/prod-classify/prod-classify?sts=' + e.currentTarget.dataset.sts;
    var id = e.currentTarget.dataset.id;
    var title = e.currentTarget.dataset.title;
    if (id) {
      url += '&tagid=' + id + '&title=' + title;
    }
    wx.navigateTo({
      url: url
    });
  },

  toLimitedTimeOffer: function() {
    wx.showToast({
      icon: 'none',
      title: '该功能未开放'
    });
  },

  onNewsPage: function() {
    wx.navigateTo({
      url: '/pages/recent-news/recent-news'
    });
  },

  onShow: function() {},

  getAllData: function() {
    http.getCartCount();
    this.getIndexImgs();
    this.getNoticeList();
    this.getTag();
  },

  getIndexImgs: function() {
    http.request({
      url: '/indexImgs',
      method: 'GET',
      data: {},
      callBack: res => {
        this.setData({
          indexImgs: res,
          seq: res
        });
      }
    });
  },

  getNoticeList: function() {
    http.request({
      url: '/shop/notice/topNoticeList',
      method: 'GET',
      data: {},
      callBack: res => {
        this.setData({
          news: res
        });
      }
    });
  },

  addToCart: function(e) {
    const prodId = e.currentTarget.dataset.prodid;
    const ths = this;
    wx.showLoading();
    http.request({
      url: '/prod/prodInfo',
      method: 'GET',
      data: {
        prodId: prodId
      },
      callBack: res => {
        http.request({
          url: '/p/shopCart/changeItem',
          method: 'POST',
          data: {
            basketId: 0,
            count: 1,
            prodId: res.prodId,
            shopId: res.shopId,
            skuId: res.skuList[0].skuId
          },
          callBack: function() {
            ths.setData({
              totalCartNum: (ths.data.totalCartNum || 0) + (ths.data.prodNum || 1)
            });
            wx.hideLoading();
            http.getCartCount();
            wx.showToast({
              title: '加入购物车成功',
              icon: 'none'
            });
          },
          errCallBack: function() {
            wx.hideLoading();
          }
        });
      },
      errCallBack: function() {
        wx.hideLoading();
      }
    });
  },

  getTag: function() {
    http.request({
      url: '/prod/tag/prodTagList',
      method: 'GET',
      data: {},
      callBack: res => {
        this.setData({
          taglist: res
        });
        for (var i = 0; i < res.length; i++) {
          this.getTagProd(res[i].id, i);
        }
      }
    });
  },

  getTagProd: function(id, index) {
    http.request({
      url: '/prod/prodListByTagId',
      method: 'GET',
      data: {
        tagId: id,
        size: 6
      },
      callBack: res => {
        var taglist = this.data.taglist;
        taglist[index].prods = (res.records || []).filter(prod => !this.shouldHideHomeProd(prod));
        this.setData({
          taglist: taglist
        });
      }
    });
  },

  shouldHideHomeProd: function(prod) {
    var name = ((prod && prod.prodName) || '').replace(/\s+/g, '');
    return /\u65fa\u4ed4\u725b\u5976/.test(name);
  },

  onPullDownRefresh: function() {
    var ths = this;
    setTimeout(function() {
      ths.getAllData();
      wx.stopPullDownRefresh();
    }, 100);
  },

  showProdInfo: function(e) {
    var relation = e.currentTarget.dataset.relation;
    if (relation) {
      wx.navigateTo({
        url: 'pages/prod/prod'
      });
    }
  }
});
