<template>
  <div class="user-page fade-in">
    <div class="profile-header">
      <div class="title-bar">
        <span>个人中心</span>
      </div>
      <div class="avatar-wrap">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
          <circle cx="12" cy="7" r="4" />
        </svg>
      </div>
      <div class="user-info">
        <h2>网页用户</h2>
      </div>
    </div>

    <div class="dashboard-card">
      <div class="card-title">
        <h3>我的订单</h3>
        <button class="view-all-btn" @click="goOrderList(0)">查看全部 ></button>
      </div>
      <div class="order-status-grid">
        <div class="status-item" @click="goOrderList(1)">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2">
              <rect x="3" y="4" width="18" height="16" rx="2" ry="2" />
              <line x1="3" y1="10" x2="21" y2="10" />
            </svg>
            <span v-if="orderAmount?.unPay" class="badge">{{ orderAmount.unPay }}</span>
          </div>
          <span>待支付</span>
        </div>
        <div class="status-item" @click="goOrderList(2)">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2">
              <line x1="22" y1="2" x2="11" y2="13" />
              <polygon points="22 2 15 22 11 13 2 9 22 2" />
            </svg>
            <span v-if="orderAmount?.payed" class="badge">{{ orderAmount.payed }}</span>
          </div>
          <span>待发货</span>
        </div>
        <div class="status-item" @click="goOrderList(3)">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
              <polyline points="10 9 9 9 8 9" />
            </svg>
            <span v-if="orderAmount?.consignment" class="badge">{{ orderAmount.consignment }}</span>
          </div>
          <span>待收货</span>
        </div>
        <div class="status-item" @click="goOrderList(5)">
          <div class="icon-circle">
            <svg viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2">
              <rect x="2" y="7" width="20" height="14" rx="2" ry="2" />
              <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
            </svg>
            <span v-if="orderAmount?.success" class="badge">{{ orderAmount.success }}</span>
          </div>
          <span>已完成</span>
        </div>
      </div>

      <div class="stats-row">
        <div class="stat-item" @click="handleFavorites">
          <span class="stat-num">{{ collectionCount }}</span>
          <span class="stat-label">我的收藏</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">0</span>
          <span class="stat-label">我的消息</span>
        </div>
        <div class="stat-item">
          <span class="stat-num">0</span>
          <span class="stat-label">我的足迹</span>
        </div>
      </div>
    </div>

    <div class="menu-list">
      <div class="menu-item" @click="router.push('/coupon')">
        <div class="menu-left">
          <svg viewBox="0 0 24 24" fill="none" stroke="#334155" stroke-width="2">
            <rect x="3" y="8" width="18" height="12" rx="2" />
            <path d="M7 8V6a4 4 0 0 1 8 0v2" />
            <circle cx="12" cy="14" r="1" />
          </svg>
          <span>领券中心</span>
        </div>
        <svg class="chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6" />
        </svg>
      </div>

      <div class="menu-item" @click="router.push('/my-coupon')">
        <div class="menu-left">
          <svg viewBox="0 0 24 24" fill="none" stroke="#334155" stroke-width="2">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
          </svg>
          <span>我的优惠券</span>
        </div>
        <svg class="chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6" />
        </svg>
      </div>

      <div class="menu-item" @click="handleAddress">
        <div class="menu-left">
          <svg viewBox="0 0 24 24" fill="none" stroke="#334155" stroke-width="2">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
            <circle cx="12" cy="10" r="3" />
          </svg>
          <span>收货地址</span>
        </div>
        <svg class="chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6" />
        </svg>
      </div>
    </div>

    <div class="bottom-actions">
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../utils/http'

const orderAmount = ref({})
const collectionCount = ref(0)
const router = useRouter()

onMounted(() => {
  getOrderCount()
  getCollectionCount()
})

const getOrderCount = async () => {
  try {
    const res = await http.get('/p/myOrder/orderCount')
    orderAmount.value = res || {}
  } catch (error) {
    console.error('Failed to load order counts', error)
  }
}

const getCollectionCount = async () => {
  try {
    const res = await http.get('/p/user/collection/count')
    collectionCount.value = res || 0
  } catch (error) {
    console.error('Failed to load collection count', error)
  }
}

const goOrderList = (sts) => {
  router.push({
    path: '/order-list',
    query: { sts: String(sts) }
  })
}

const handleFavorites = () => {
  router.push({
    path: '/classify',
    query: {
      sts: '5',
      title: '我的收藏商品'
    }
  })
}

const handleAddress = () => {
  router.push('/address')
}

const logout = async () => {
  if (!window.confirm('确定退出当前账号吗？')) {
    return
  }
  try {
    await http.post('/logOut')
  } catch (error) {
    console.error('Logout request failed', error)
  }
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.user-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: white;
  padding: 40px 20px;
  border-radius: 8px;
}

.title-bar {
  width: 100%;
  text-align: center;
  font-size: 16px;
  margin-bottom: 20px;
  color: #333;
}

.avatar-wrap {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.avatar-wrap svg {
  width: 40px;
  height: 40px;
  color: #ccc;
}

.user-info h2 {
  margin: 0;
  font-size: 18px;
  font-weight: normal;
  color: #333;
}

.dashboard-card {
  background: white;
  border-radius: 8px;
  padding: 0;
}

.card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f9f9f9;
}

.card-title h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.view-all-btn {
  border: none;
  background: transparent;
  color: #999;
  font-size: 14px;
  cursor: pointer;
  padding: 0;
}

.order-status-grid {
  display: flex;
  justify-content: space-between;
  padding: 20px 40px;
}

.status-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.icon-circle {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-circle svg {
  width: 28px;
  height: 28px;
}

.badge {
  position: absolute;
  top: -5px;
  right: -10px;
  background: white;
  color: #e64340;
  font-size: 12px;
  padding: 0 4px;
  border: 1px solid #e64340;
  border-radius: 10px;
  min-width: 12px;
  text-align: center;
}

.status-item span:not(.badge) {
  font-size: 14px;
  color: #666;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 10px solid #f6f8fb;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.stat-num {
  font-size: 16px;
  font-weight: bold;
  color: #3b9dfd;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.menu-list {
  background: white;
  border-radius: 8px;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f9f9f9;
  cursor: pointer;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #333;
  font-size: 15px;
}

.menu-left svg {
  width: 20px;
  height: 20px;
  color: #666;
}

.chevron {
  width: 16px;
  height: 16px;
  color: #ccc;
}

.bottom-actions {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.logout-btn {
  width: 100%;
  max-width: 400px;
  background: #e64340;
  color: white;
  border: none;
  padding: 15px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

@media (max-width: 768px) {
  .order-status-grid {
    padding: 20px;
    gap: 12px;
  }

  .stats-row {
    padding: 16px 0;
  }
}
</style>
