<template>
  <div class="my-coupon-page fade-in">
    <div class="tabs">
      <button class="tab" :class="{ active: currentStatus === 1 }" @click="changeStatus(1)">未使用</button>
      <button class="tab" :class="{ active: currentStatus === 2 }" @click="changeStatus(2)">已使用</button>
      <button class="tab" :class="{ active: currentStatus === 3 }" @click="changeStatus(3)">已过期</button>
    </div>

    <div v-if="loading" class="loading">正在加载优惠券...</div>

    <div v-else-if="couponList.length" class="coupon-list">
      <div
        v-for="item in couponList"
        :key="item.couponId || item.couponNo"
        class="coupon-card"
        :class="{ disabled: currentStatus !== 1 }"
      >
        <div class="coupon-left">
          <div class="amount">¥{{ toMoney(item.reduceAmount) }}</div>
          <div class="condition">满{{ toMoney(item.conditionAmount || item.cashCondition || 0) }}可用</div>
        </div>
        <div class="coupon-right">
          <div class="coupon-name">{{ item.couponName }}</div>
          <div class="coupon-desc">{{ item.useInfo || '平台指定商品可用' }}</div>
          <div class="coupon-time">
            {{ formatDate(item.startTime) }} - {{ formatDate(item.endTime) }}
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">暂无优惠券</div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../../utils/http'

const loading = ref(false)
const currentStatus = ref(1)
const couponList = ref([])

const toNumber = (value) => {
  const parsed = Number(value || 0)
  return Number.isFinite(parsed) ? parsed : 0
}

const toMoney = (value) => toNumber(value).toFixed(2)

const formatDate = (value) => {
  if (!value) {
    return '长期有效'
  }
  return String(value).split(' ')[0]
}

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await http.get('/p/myCoupons', {
      params: {
        pageNum: 1,
        pageSize: 20,
        status: currentStatus.value
      }
    })
    let records = res?.records || []
    if (Array.isArray(res)) {
      records = res
    }
    couponList.value = records
  } catch (error) {
    console.error('Failed to load my coupons', error)
    couponList.value = []
  } finally {
    loading.value = false
  }
}

const changeStatus = (status) => {
  if (currentStatus.value === status) {
    return
  }
  currentStatus.value = status
  couponList.value = []
  loadCoupons()
}

onMounted(() => {
  loadCoupons()
})
</script>

<style scoped>
.fade-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.my-coupon-page {
  max-width: 860px;
  margin: 0 auto;
}

.tabs {
  display: flex;
  background: white;
  border-radius: 12px;
  padding: 6px;
  margin-bottom: 20px;
  border: 1px solid rgba(226, 232, 240, 0.7);
}

.tab {
  flex: 1;
  height: 42px;
  border: none;
  background: transparent;
  color: #64748b;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.tab.active {
  background: #eff6ff;
  color: #2563eb;
  font-weight: 600;
}

.loading,
.empty-state {
  background: white;
  border-radius: 12px;
  padding: 48px 24px;
  text-align: center;
  color: #94a3b8;
  border: 1px solid rgba(226, 232, 240, 0.7);
}

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.coupon-card {
  display: flex;
  overflow: hidden;
  border-radius: 16px;
  background: white;
  border: 1px solid rgba(226, 232, 240, 0.7);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.coupon-card.disabled {
  opacity: 0.55;
}

.coupon-left {
  width: 160px;
  background: linear-gradient(135deg, #4aa3ff 0%, #3b82f6 100%);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.amount {
  font-size: 34px;
  font-weight: 700;
  line-height: 1;
}

.condition {
  margin-top: 10px;
  font-size: 13px;
}

.coupon-right {
  flex: 1;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-name {
  font-size: 18px;
  color: #1e293b;
  font-weight: 600;
}

.coupon-desc {
  margin-top: 8px;
  font-size: 14px;
  color: #64748b;
}

.coupon-time {
  margin-top: 10px;
  font-size: 12px;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .coupon-card {
    flex-direction: column;
  }

  .coupon-left {
    width: 100%;
  }
}
</style>
