var http = require('../../utils/http.js');

Component({
  properties: {
    item: Object,
    type: Number,
    order: Boolean,
    canUse: Boolean,
    index: Number,
    showTimeType: Number
  },

  methods: {
    receiveCoupon: function() {
      var couponNo = this.data.item && this.data.item.couponNo;
      if (!couponNo) {
        wx.showToast({
          title: '优惠券编码缺失',
          icon: 'none'
        });
        return;
      }

      http.request({
        url: '/p/coupon/receive/' + couponNo,
        method: 'POST',
        data: {},
        callBack: () => {
          this.setData({
            item: Object.assign({}, this.data.item, {
              canReceive: false
            })
          });
          this.triggerEvent('receiveCoupon', {
            couponNo: couponNo
          });
          wx.showToast({
            title: '领取成功',
            icon: 'none'
          });
        },
        errCallBack: res => {
          wx.showToast({
            title: (res && res.msg) || '领取失败',
            icon: 'none'
          });
        }
      });
    },

    checkCoupon: function(e) {
      this.triggerEvent('checkCoupon', {
        couponId: e.currentTarget.dataset.couponid
      });
    },

    useCoupon: function() {
      wx.navigateTo({
        url: '/pages/submit-order/submit-order?orderEntry=0'
      });
    }
  }
});
