<template>
  <div class="coupon-page fade-in">
    <div class="header">
      <h2>领券中心</h2>
      <p class="subtitle">超多优惠券等您来领取</p>
    </div>

    <div class="coupon-grid">
      <div class="coupon-card" v-for="item in couponList" :key="item.couponNo || item.couponId">
        <div class="coupon-left">
          <h3><span class="currency">¥</span>{{ item.reduceAmount }}</h3>
          <p>满 {{ item.conditionAmount || item.cashCondition || 0 }} 元可用</p>
        </div>
        <div class="coupon-divider"></div>
        <div class="coupon-right">
          <h4>{{ item.couponName }}</h4>
          <p>{{ item.useInfo || '平台指定商品可用' }}</p>
          <button
            class="claim-btn"
            :class="{ disabled: !item.canReceive }"
            :disabled="!item.canReceive"
            @click="claimCoupon(item)"
          >
            {{ getClaimButtonText(item) }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="couponList.length === 0 && !loading" class="empty-state">
      <p>目前暂无可领取的优惠券活动哦~</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '../../utils/http'

const couponList = ref([])
const loading = ref(true)

const normalizeCouponList = (records) => {
  return (records || []).map((item) => ({
    ...item,
    canReceive: item.canReceive !== false,
    showUseButton: false
  }))
}

const loadCoupons = async () => {
  try {
    loading.value = true
    const res = await http.get('/p/coupons/public', {
      params: { pageNum: 1, pageSize: 20 }
    })

    let records = res.records || []
    if (Array.isArray(res)) {
      records = res
    }

    couponList.value = normalizeCouponList(records)
  } catch (e) {
    console.error('Failed to load coupons', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCoupons()
})

const getClaimButtonText = (item) => {
  return item.canReceive ? '立即领取' : '已领取'
}

const claimCoupon = async (item) => {
  if (!item.couponNo) {
    alert('优惠券状态异常：由于编码缺失无法领取。')
    return
  }

  try {
    await http.post(`/p/coupon/receive/${item.couponNo}`)
    item.canReceive = false
    item.showUseButton = false
    alert('领取成功，优惠券已发放到您的账户中。')
  } catch (e) {
    alert(e?.message || '领取失败')
  }
}
</script>

<style scoped>
.fade-in { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.coupon-page {
  padding: 20px;
  max-width: 1440px;
  margin: 0 auto;
}

.header {
  background: linear-gradient(135deg, #facc15 0%, #eab308 100%);
  padding: 40px;
  border-radius: 20px;
  margin-bottom: 30px;
  color: white;
  text-align: center;
  box-shadow: 0 10px 30px rgba(234, 179, 8, 0.2);
}

.header h2 { margin: 0 0 10px 0; font-size: 32px; font-weight: 800; }
.header .subtitle { margin: 0; font-size: 16px; opacity: 0.9; }

.coupon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
}

.coupon-card {
  display: flex;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.03);
  border: 1px solid rgba(226, 232, 240, 0.6);
  position: relative;
}

.coupon-left {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: white;
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 130px;
}

.coupon-left h3 { margin: 0 0 8px 0; font-size: 36px; font-weight: 800; }
.currency { font-size: 16px; font-weight: normal; margin-right: 4px; }
.coupon-left p { margin: 0; font-size: 12px; opacity: 0.9; }

.coupon-divider {
  width: 1px;
  background: transparent;
  border-right: 2px dashed #f1f5f9;
  position: relative;
}
.coupon-divider::before, .coupon-divider::after {
  content: '';
  position: absolute;
  width: 16px;
  height: 16px;
  background: #f8fafc;
  border-radius: 50%;
  left: -8px;
}
.coupon-divider::before { top: -8px; }
.coupon-divider::after { bottom: -8px; }

.coupon-right {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.coupon-right h4 { margin: 0 0 8px 0; font-size: 18px; color: #1e293b; }
.coupon-right p { margin: 0 0 16px 0; font-size: 13px; color: #64748b; }

.claim-btn {
  background: #fef2f2;
  color: #ef4444;
  border: 1px solid #fca5a5;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  align-self: flex-end;
  transition: all 0.2s;
  white-space: nowrap;
}

.claim-btn:hover:not(:disabled) { background: #ef4444; color: white; }

.claim-btn.disabled {
  background: #f1f5f9;
  color: #94a3b8;
  border-color: #e2e8f0;
  cursor: not-allowed;
}

.empty-state { text-align: center; color: #94a3b8; margin-top: 60px; font-size: 16px; }
</style>
