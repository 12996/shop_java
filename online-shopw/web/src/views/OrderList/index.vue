<template>
  <div class="order-list-page fade-in">
    <div class="page-card">
      <div class="page-header">
        <h2>我的订单</h2>
      </div>

      <div class="status-tabs">
        <button
          v-for="tab in statusTabs"
          :key="tab.value"
          :class="['status-tab', { active: currentStatus === tab.value }]"
          @click="changeStatus(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>

      <div v-if="loading" class="empty-state">正在加载订单...</div>
      <div v-else-if="orders.length === 0" class="empty-state">还没有相关订单</div>

      <div v-else class="order-list">
        <div v-for="(order, index) in orders" :key="order.orderNumber" class="order-card">
          <div class="order-head">
            <span class="order-number">订单编号：{{ order.orderNumber }}</span>
            <div class="order-head-right">
              <span :class="['order-status', statusClass(order.status)]">{{ statusText(order.status) }}</span>
              <button
                v-if="canDelete(order.status)"
                class="icon-btn"
                @click="deleteOrder(order.orderNumber)"
              >
                删除
              </button>
            </div>
          </div>

          <div
            v-if="(order.orderItemDtos || []).length === 1"
            class="single-item"
            @click="toOrderDetail(order)"
          >
            <img :src="order.orderItemDtos[0].pic" :alt="order.orderItemDtos[0].prodName" class="single-cover" />
            <div class="single-info">
              <div class="prod-name">{{ order.orderItemDtos[0].prodName }}</div>
              <div class="sku-name">{{ order.orderItemDtos[0].skuName || '默认规格' }}</div>
              <div class="prod-meta">
                <span class="price">￥{{ formatAmount(order.orderItemDtos[0].price) }}</span>
                <span class="count">x{{ order.orderItemDtos[0].prodCount }}</span>
              </div>
            </div>
          </div>

          <div
            v-else
            class="multi-items"
            @click="toOrderDetail(order)"
          >
            <div class="thumb-list">
              <img
                v-for="item in order.orderItemDtos || []"
                :key="`${order.orderNumber}-${item.skuId}`"
                :src="item.pic"
                :alt="item.prodName"
                class="thumb"
              />
            </div>
          </div>

          <div class="order-total">
            <span>共{{ totalCount(order) }}件商品</span>
            <span>合计：<strong>￥{{ formatAmount(order.actualTotal) }}</strong></span>
          </div>

          <div class="order-actions">
            <button
              v-if="Number(order.status) === 1"
              class="action-btn"
              @click="cancelOrder(order.orderNumber)"
            >
              取消订单
            </button>
            <button class="action-btn primary" @click="buyAgain(index)">
              再次购买
            </button>
            <button
              v-if="Number(order.status) === 1"
              class="action-btn primary"
              @click="payAgain(order.orderNumber)"
            >
              支付
            </button>
            <button
              v-if="[3, 5].includes(Number(order.status))"
              class="action-btn"
              @click="viewDelivery(order.orderNumber)"
            >
              查看物流
            </button>
            <button
              v-if="Number(order.status) === 3"
              class="action-btn primary"
              @click="confirmReceipt(order.orderNumber)"
            >
              确认收货
            </button>
          </div>
        </div>
      </div>

      <div v-if="!loading && currentPage < totalPages" class="load-more-wrap">
        <button class="load-more-btn" @click="loadMore">加载更多</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../../utils/http'

const route = useRoute()
const router = useRouter()

const statusTabs = [
  { label: '全部', value: 0 },
  { label: '待支付', value: 1 },
  { label: '待发货', value: 2 },
  { label: '待收货', value: 3 },
  { label: '已完成', value: 5 }
]

const orders = ref([])
const currentPage = ref(1)
const totalPages = ref(0)
const loading = ref(false)
const currentStatus = computed(() => Number(route.query.sts || 0))

const fetchOrders = async (page = 1, append = false) => {
  loading.value = true
  try {
    const res = await http.get('/p/myOrder/myOrder', {
      params: {
        current: page,
        size: 10,
        status: currentStatus.value
      }
    })
    const records = (res && res.records) || []
    orders.value = append ? orders.value.concat(records) : records
    currentPage.value = res?.current || page
    totalPages.value = res?.pages || 0
  } catch (error) {
    console.error('Failed to load order list', error)
    if (!append) {
      orders.value = []
    }
    window.alert(error.message || '订单列表加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})

watch(
  () => route.query.sts,
  () => {
    fetchOrders()
  }
)

const changeStatus = (status) => {
  router.push({
    path: '/order-list',
    query: { sts: String(status) }
  })
}

const loadMore = () => {
  if (currentPage.value >= totalPages.value || loading.value) {
    return
  }
  fetchOrders(currentPage.value + 1, true)
}

const statusText = (status) => {
  switch (Number(status)) {
    case 1:
      return '待支付'
    case 2:
      return '待发货'
    case 3:
      return '待收货'
    case 5:
      return '已完成'
    case 6:
      return '已取消'
    default:
      return '全部'
  }
}

const statusClass = (status) => {
  return [1].includes(Number(status)) ? 'is-warn' : [5, 6].includes(Number(status)) ? 'is-muted' : ''
}

const totalCount = (order) => {
  if (order.totalCount) {
    return order.totalCount
  }
  return (order.orderItemDtos || []).reduce((sum, item) => sum + Number(item.prodCount || 0), 0)
}

const formatAmount = (amount) => {
  const value = Number(amount || 0)
  return value.toFixed(2)
}

const canDelete = (status) => [5, 6].includes(Number(status))

const cancelOrder = async (orderNumber) => {
  if (!window.confirm('要取消此订单吗？')) {
    return
  }
  try {
    await http.put(`/p/myOrder/cancel/${orderNumber}`)
    await fetchOrders()
  } catch (error) {
    console.error('Cancel order failed', error)
    window.alert(error.message || '取消订单失败')
  }
}

const payAgain = async (orderNumber) => {
  try {
    const res = await http.post('/p/order/normalPay', {
      orderNumbers: orderNumber
    })
    if (!res) {
      window.alert('支付失败')
      return
    }
    window.alert('模拟支付成功')
    router.push({
      path: '/pay-result',
      query: {
        sts: '1',
        orderNumbers: orderNumber
      }
    })
  } catch (error) {
    console.error('Pay again failed', error)
    window.alert(error.message || '支付失败')
  }
}

const buyAgain = async (index) => {
  const order = orders.value[index]
  if (!order || !order.orderNumber) {
    window.alert('订单商品不存在')
    return
  }
  try {
    const detail = await http.get('/p/myOrder/orderDetail', {
      params: { orderNumber: order.orderNumber }
    })
    const items = detail?.orderItemDtos || []
    const shopId = detail?.shopId
    if (!items.length || !shopId) {
      window.alert('订单详情不完整')
      return
    }

    for (const item of items) {
      if (!item.prodId || !item.skuId || !item.prodCount) {
        throw new Error('订单商品数据异常')
      }
      await http.post('/p/shopCart/changeItem', {
        basketId: 0,
        count: item.prodCount,
        prodId: item.prodId,
        shopId,
        skuId: item.skuId
      })
    }

    window.alert('已加入购物车')
    router.push('/basket')
  } catch (error) {
    console.error('Buy again failed', error)
    window.alert(error.message || '加入购物车失败')
  }
}

const confirmReceipt = async (orderNumber) => {
  if (!window.confirm('我已收到货？')) {
    return
  }
  try {
    await http.put(`/p/myOrder/receipt/${orderNumber}`)
    await fetchOrders()
  } catch (error) {
    console.error('Confirm receipt failed', error)
    window.alert(error.message || '确认收货失败')
  }
}

const deleteOrder = async (orderNumber) => {
  if (!window.confirm('确定删除此订单吗？')) {
    return
  }
  try {
    await http.delete(`/p/myOrder/${orderNumber}`)
    await fetchOrders()
  } catch (error) {
    console.error('Delete order failed', error)
    window.alert(error.message || '删除订单失败')
  }
}

const viewDelivery = (orderNumber) => {
  window.alert(`物流查询待接入，订单号：${orderNumber}`)
}

const toOrderDetail = (order) => {
  if (!order?.orderNumber) {
    return
  }
  window.alert(`订单详情页待接入，订单号：${order.orderNumber}`)
}
</script>

<style scoped>
.order-list-page {
  width: 100%;
}

.page-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.status-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  padding-bottom: 20px;
  border-bottom: 1px solid #f1f5f9;
}

.status-tab {
  border: 1px solid #dbe2ea;
  background: #fff;
  color: #666;
  padding: 8px 18px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.status-tab.active {
  color: #3b9dfd;
  border-color: #3b9dfd;
  background: #eef6ff;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}

.order-card {
  border: 1px solid #eef2f7;
  border-radius: 8px;
  overflow: hidden;
}

.order-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 18px;
  background: #fafcff;
  border-bottom: 1px solid #eef2f7;
}

.order-number {
  color: #333;
  font-size: 14px;
}

.order-head-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-status {
  color: #3b82f6;
  font-size: 14px;
}

.order-status.is-warn {
  color: #ef4444;
}

.order-status.is-muted {
  color: #94a3b8;
}

.icon-btn {
  border: none;
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  padding: 0;
}

.single-item,
.multi-items {
  padding: 18px;
  cursor: pointer;
}

.single-item {
  display: flex;
  gap: 16px;
}

.single-cover {
  width: 96px;
  height: 96px;
  border-radius: 8px;
  background: #f8fafc;
  object-fit: contain;
  flex-shrink: 0;
}

.single-info {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.prod-name {
  font-size: 15px;
  color: #334155;
  line-height: 1.5;
}

.sku-name {
  font-size: 13px;
  color: #94a3b8;
  margin: 10px 0;
}

.prod-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ef4444;
  font-size: 18px;
  font-weight: 700;
}

.count {
  color: #64748b;
  font-size: 14px;
}

.thumb-list {
  display: flex;
  gap: 12px;
  overflow-x: auto;
}

.thumb {
  width: 84px;
  height: 84px;
  border-radius: 8px;
  background: #f8fafc;
  object-fit: contain;
  flex-shrink: 0;
}

.order-total {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  padding: 0 18px 16px;
  color: #475569;
  font-size: 14px;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 0 18px 18px;
  flex-wrap: wrap;
}

.action-btn,
.load-more-btn {
  border: 1px solid #dbe2ea;
  background: white;
  color: #475569;
  border-radius: 18px;
  padding: 8px 16px;
  cursor: pointer;
}

.action-btn.primary {
  color: #e64340;
  border-color: #f3b0ad;
  background: #fff7f7;
}

.load-more-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.empty-state {
  text-align: center;
  color: #94a3b8;
  padding: 48px 0;
}

@media (max-width: 768px) {
  .page-card {
    padding: 16px;
  }

  .order-head,
  .order-total,
  .order-actions {
    padding-left: 14px;
    padding-right: 14px;
  }

  .order-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .order-head-right {
    width: 100%;
    justify-content: space-between;
  }

  .single-item {
    align-items: center;
  }

  .single-cover {
    width: 84px;
    height: 84px;
  }

  .order-total,
  .order-actions {
    justify-content: flex-start;
  }
}
</style>
