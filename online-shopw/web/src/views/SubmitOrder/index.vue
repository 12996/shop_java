<template>
  <div class="submit-order-page fade-in">
    <div class="header">
      <button class="back-btn" @click="goBack">&lt; 返回</button>
      <h1>确认订单</h1>
    </div>

    <div v-if="loading" class="loading">正在加载结算信息...</div>

    <div v-else class="content-wrapper">
      <div class="main-content">
        <div class="address-card section hover-scale" @click="addAddress">
          <svg class="location-icon" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
            <circle cx="12" cy="10" r="3" />
          </svg>

          <div v-if="userAddr" class="addr-content">
            <div class="contact">
              <span class="name">{{ userAddr.receiver }}</span>
              <span class="phone">{{ userAddr.mobile }}</span>
            </div>
            <div class="details">
              {{ userAddr.province }}{{ userAddr.city }}{{ userAddr.area }}{{ userAddr.addr }}
            </div>
          </div>
          <div v-else class="addr-content">
            <div class="empty-addr">新增收货地址</div>
          </div>

          <svg class="chevron-right" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="9 18 15 12 9 6" />
          </svg>
        </div>

        <div class="shop-list section">
          <div class="shop-card">
            <div class="order-items">
              <div v-for="(item, index) in orderItems" :key="item.skuId || item.basketId || index" class="item">
                <img :src="item.pic" alt="prod" class="item-pic" />
                <div class="item-info">
                  <p class="name">{{ item.prodName }}</p>
                  <p class="sku">{{ item.skuName || '默认规格' }}</p>
                  <div class="price-row">
                    <span class="price">￥{{ toMoney(item.price) }}</span>
                    <span class="count">x{{ item.prodCount }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="shop-subtotal">
              共{{ totalCount }}件商品，合计：￥{{ toMoney(total) }}
            </div>

            <div class="shop-options">
              <div class="option-row" @click="showCouponPopup">
                <span class="option-label">优惠券：</span>
                <span class="option-val interactable">
                  <span class="coupon-summary">
                    <template v-if="selectedCoupon">已选 {{ selectedCoupon.couponName }}</template>
                    <template v-else-if="!canUseCoupons.length">暂无可用</template>
                    <template v-else>去选择</template>
                  </span>
                  <span class="option-extra">
                    {{ coupons.totalLength || 0 }}张
                    <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="9 18 15 12 9 6" />
                    </svg>
                  </span>
                </span>
              </div>

              <div class="option-row">
                <span class="option-label">买家留言：</span>
                <span class="option-val">
                  <input v-model="remark" type="text" placeholder="给商家留言" />
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="summary-panel section">
        <div class="summary-details">
          <div class="val-row">
            <span class="label">订单总额：</span>
            <span class="value">￥{{ toMoney(total) }}</span>
          </div>
          <div class="val-row">
            <span class="label">运费：</span>
            <span class="value">￥{{ toMoney(transfee) }}</span>
          </div>
          <div class="val-row">
            <span class="label">优惠金额：</span>
            <span class="value">-￥{{ toMoney(shopReduce) }}</span>
          </div>
          <div class="val-row final-row">
            <span class="label">小计：</span>
            <span class="final-price">￥{{ toMoney(actualTotal) }}</span>
          </div>
        </div>

        <div class="panel-footer">
          <button class="submit-btn" :disabled="submitting || !userAddr" @click="toPay">
            {{ submitting ? '提交中...' : '提交订单' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="popupShow" class="coupon-mask" @click="closePopup">
      <div class="coupon-drawer" @click.stop>
        <div class="drawer-header">
          <h3>优惠券</h3>
          <button class="close-btn" @click="closePopup">关闭</button>
        </div>

        <div class="coupon-tabs">
          <button class="coupon-tab" :class="{ active: couponSts === 1 }" @click="couponSts = 1">
            可用优惠券 ({{ canUseCoupons.length }})
          </button>
          <button class="coupon-tab" :class="{ active: couponSts === 2 }" @click="couponSts = 2">
            不可用优惠券 ({{ unCanUseCoupons.length }})
          </button>
        </div>

        <div class="drawer-body">
          <div v-if="displayCoupons.length" class="coupon-list">
            <div
              v-for="item in displayCoupons"
              :key="item.couponId || item.couponNo"
              class="coupon-item"
              :class="{ active: isCouponSelected(item), disabled: couponSts === 2 }"
              @click="couponSts === 1 && checkCoupon(item)"
            >
              <div class="coupon-amount">￥{{ toMoney(item.reduceAmount) }}</div>
              <div class="coupon-info">
                <div class="coupon-title">{{ item.couponName }}</div>
                <div class="coupon-desc">满 ￥{{ toMoney(item.cashCondition || item.conditionAmount || 0) }} 可用</div>
                <div class="coupon-desc">{{ item.useInfo || '平台指定商品可用' }}</div>
              </div>
              <div class="coupon-action">
                <template v-if="couponSts === 1">{{ isCouponSelected(item) ? '已选择' : '选择' }}</template>
                <template v-else>不可用</template>
              </div>
            </div>
          </div>

          <div v-else class="drawer-empty">暂无优惠券</div>
        </div>

        <div v-if="couponSts === 1" class="drawer-footer">
          <button class="confirm-btn" @click="chooseCoupon">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onActivated, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../../utils/http'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const popupShow = ref(false)
const couponSts = ref(1)
const orderEntry = ref('0')
const userAddr = ref(null)
const orderItems = ref([])
const coupons = ref({
  totalLength: 0,
  canUseCoupons: [],
  unCanUseCoupons: []
})
const selectedCoupon = ref(null)
const actualTotal = ref(0)
const total = ref(0)
const totalCount = ref(0)
const transfee = ref(0)
const shopReduce = ref(0)
const remark = ref('')
const couponIds = ref([])
const shopCartOrders = ref([])
const SELECTED_ADDRESS_KEY = 'selectedAddress'

let basketIds = []
let orderItem = null

const safeParseStorage = (key, storage = localStorage) => {
  const raw = storage.getItem(key)
  if (!raw) {
    return undefined
  }
  try {
    return JSON.parse(raw)
  } catch (error) {
    return undefined
  }
}

const toNumber = (value) => {
  const parsed = Number(value || 0)
  return Number.isFinite(parsed) ? parsed : 0
}

const toMoney = (value) => toNumber(value).toFixed(2)

const canUseCoupons = computed(() => coupons.value.canUseCoupons || [])
const unCanUseCoupons = computed(() => coupons.value.unCanUseCoupons || [])
const displayCoupons = computed(() => (couponSts.value === 1 ? canUseCoupons.value : unCanUseCoupons.value))

const getConfirmPayload = () => ({
  addrId: userAddr.value?.addrId || 0,
  orderItem: orderEntry.value === '1' ? orderItem : undefined,
  basketIds: orderEntry.value === '0' ? basketIds : undefined,
  couponIds: couponIds.value,
  userChangeCoupon: 1
})

const initOrderContext = () => {
  orderEntry.value = route.query.orderEntry === '1' ? '1' : '0'
  basketIds = safeParseStorage('basketIds') || []
  orderItem = safeParseStorage('orderItem') || null

  const hasBasketFlow = orderEntry.value === '0' && basketIds.length > 0
  const hasBuyNowFlow = orderEntry.value === '1' && orderItem
  if (!hasBasketFlow && !hasBuyNowFlow) {
    router.replace('/basket')
    return false
  }
  return true
}

const restoreSelectedAddress = () => {
  const selectedAddress = safeParseStorage(SELECTED_ADDRESS_KEY, sessionStorage)
  if (!selectedAddress?.addrId) {
    return false
  }
  if (userAddr.value?.addrId === selectedAddress.addrId) {
    return false
  }
  userAddr.value = selectedAddress
  return true
}

const normalizeCouponsFromConfirm = (shopCartOrder) => {
  const allCoupons = shopCartOrder?.coupons || []
  const nextCanUseCoupons = []
  const nextUnCanUseCoupons = []

  allCoupons.forEach((coupon) => {
    const normalized = {
      ...coupon,
      choose: couponIds.value.includes(coupon.couponId)
    }
    if (coupon.canUse) {
      nextCanUseCoupons.push(normalized)
    } else {
      nextUnCanUseCoupons.push(normalized)
    }
  })

  coupons.value = {
    totalLength: allCoupons.length,
    canUseCoupons: nextCanUseCoupons,
    unCanUseCoupons: nextUnCanUseCoupons
  }
  selectedCoupon.value = nextCanUseCoupons.find((item) => item.choose) || null
}

const fillOrderDataFromConfirm = (res) => {
  const nextShopCartOrders = res.shopCartOrders || []
  const shopCartOrder = nextShopCartOrders[0] || {}
  const discounts = shopCartOrder.shopCartItemDiscounts || []
  const items = []

  discounts.forEach((discount) => {
    items.push(...(discount.shopCartItems || []))
  })

  shopCartOrders.value = nextShopCartOrders
  orderItems.value = items
  actualTotal.value = toNumber(res.actualTotal)
  total.value = toNumber(res.total)
  totalCount.value = toNumber(res.totalCount)
  userAddr.value = res.userAddr || userAddr.value
  transfee.value = toNumber(shopCartOrder.transfee)
  shopReduce.value = toNumber(shopCartOrder.shopReduce)
  normalizeCouponsFromConfirm(shopCartOrder)
}

const loadConfirmData = async ({ silent = false } = {}) => {
  if (!silent) {
    loading.value = true
  }

  try {
    const res = await http.post('/p/order/confirm', getConfirmPayload())
    fillOrderDataFromConfirm(res || {})
    restoreSelectedAddress()
    return res || {}
  } catch (error) {
    console.error('Order confirm failed:', error)
    shopCartOrders.value = []
    orderItems.value = []
    if (!silent) {
      window.alert(error.message || '确认订单失败')
    }
    throw error
  } finally {
    if (!silent) {
      loading.value = false
    }
  }
}

const getSubmitPayload = () => ({
  orderShopParam: (shopCartOrders.value || []).map((shopOrder) => ({
    shopId: shopOrder.shopId,
    remarks: remark.value || shopOrder.remarks || ''
  }))
})

const showCouponPopup = () => {
  popupShow.value = true
}

const closePopup = () => {
  popupShow.value = false
}

const isCouponSelected = (coupon) => couponIds.value[0] === coupon.couponId

const checkCoupon = (coupon) => {
  couponIds.value = couponIds.value[0] === coupon.couponId ? [] : [coupon.couponId]
  coupons.value = {
    ...coupons.value,
    canUseCoupons: canUseCoupons.value.map((item) => ({
      ...item,
      choose: couponIds.value[0] === item.couponId
    }))
  }
  selectedCoupon.value = coupons.value.canUseCoupons.find((item) => item.choose) || null
}

const chooseCoupon = async () => {
  try {
    await loadConfirmData()
    popupShow.value = false
  } catch (error) {
    console.error('Choose coupon failed:', error)
  }
}

const goBack = () => router.back()

const addAddress = () => {
  router.push({
    path: '/address',
    query: {
      order: '0',
      from: 'submit-order',
      orderEntry: orderEntry.value
    }
  })
}

onMounted(() => {
  if (!initOrderContext()) {
    return
  }
  loadConfirmData().catch(() => {})
})

onActivated(() => {
  if (restoreSelectedAddress()) {
    loadConfirmData().catch(() => {})
  }
})

const toPay = async () => {
  if (!userAddr.value) {
    window.alert('请选择地址')
    return
  }

  submitting.value = true
  try {
    await loadConfirmData({ silent: true })

    const submitRes = await http.post('/p/order/submit', getSubmitPayload())
    const orderNumbers = submitRes?.orderNumbers
    if (!orderNumbers) {
      throw new Error('提交订单失败')
    }

    const payRes = await http.post('/p/order/normalPay', {
      orderNumbers
    })
    if (!payRes) {
      throw new Error('支付失败')
    }

    localStorage.removeItem('basketIds')
    router.push({
      path: '/pay-result',
      query: {
        orderNumbers
      }
    })
  } catch (error) {
    console.error('Submit order failed:', error)
    window.alert(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.submit-order-page {
  width: 100%;
  padding: 20px 40px;
  color: #333;
  display: flex;
  flex-direction: column;
  background-color: #f6f8fb;
  min-height: 100vh;
  box-sizing: border-box;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.back-btn {
  background: white;
  border: 1px solid #e0e0e0;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
  color: #666;
  margin-right: 15px;
  font-size: 14px;
}

.header h1 {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.content-wrapper {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.summary-panel {
  width: 320px;
  background: white;
  border-radius: 8px;
  position: sticky;
  top: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.address-card {
  padding: 24px;
}

.hover-scale {
  cursor: pointer;
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.location-icon {
  margin-top: 2px;
  color: #ff4d4f;
}

.addr-content {
  flex: 1;
}

.contact {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 8px;
}

.name,
.phone {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.details {
  font-size: 14px;
  color: #999;
  line-height: 1.5;
}

.chevron-right {
  align-self: center;
  color: #ccc;
  width: 20px;
  height: 20px;
}

.empty-addr {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
  color: #ff4d4f;
  font-size: 16px;
  font-weight: bold;
}

.shop-card {
  display: flex;
  flex-direction: column;
}

.order-items {
  display: flex;
  flex-direction: column;
}

.item {
  display: flex;
  gap: 15px;
  padding: 24px;
  border-bottom: 1px solid #f9f9f9;
}

.item-pic {
  width: 90px;
  height: 90px;
  border-radius: 4px;
  object-fit: cover;
  background: #f5f5f5;
  border: 1px solid #eee;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-info .name {
  margin: 0 0 5px 0;
  font-size: 15px;
}

.item-info .sku {
  font-size: 13px;
  color: #999;
  margin: 0 0 auto 0;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.price {
  font-size: 16px;
}

.count {
  font-size: 14px;
  color: #999;
}

.shop-subtotal {
  text-align: right;
  padding: 15px 24px;
  font-size: 14px;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.shop-options {
  padding: 0 24px;
}

.option-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  font-size: 14px;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
}

.option-row:last-child {
  border-bottom: none;
}

.option-label {
  width: 100px;
}

.option-val {
  flex: 1;
  text-align: right;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 5px;
}

.option-val.interactable {
  cursor: pointer;
}

.coupon-summary {
  color: #666;
}

.option-extra {
  color: #ccc;
  margin-left: 10px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.option-val input {
  width: 100%;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333;
  text-align: left;
}

.option-val input::placeholder {
  color: #bbb;
}

.summary-details {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.val-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  font-size: 14px;
  color: #333;
}

.value {
  font-size: 14px;
  color: #e64340;
}

.final-row {
  margin-top: 10px;
}

.final-price {
  font-size: 20px;
  font-weight: bold;
  color: #e64340;
}

.panel-footer {
  display: flex;
  flex-direction: column;
  padding: 0;
}

.submit-btn {
  width: 100%;
  background: #e64340;
  color: white;
  border: none;
  padding: 16px;
  font-size: 16px;
  cursor: pointer;
  border-radius: 0 0 8px 8px;
  font-weight: bold;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.loading,
.drawer-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  color: #999;
  font-size: 14px;
}

.coupon-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.28);
  display: flex;
  justify-content: flex-end;
  z-index: 30;
}

.coupon-drawer {
  width: 440px;
  max-width: 100%;
  height: 100%;
  background: white;
  box-shadow: -10px 0 30px rgba(15, 23, 42, 0.12);
  display: flex;
  flex-direction: column;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eef2f7;
}

.drawer-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
}

.close-btn,
.confirm-btn,
.coupon-tab {
  border: 1px solid #e2e8f0;
  background: white;
  color: #64748b;
  border-radius: 999px;
  padding: 8px 14px;
  cursor: pointer;
}

.coupon-tabs {
  display: flex;
  gap: 12px;
  padding: 16px 24px 0;
}

.coupon-tab.active {
  color: #3b82f6;
  border-color: #bfdbfe;
  background: #eff6ff;
}

.drawer-body {
  padding: 20px 24px 24px;
  overflow: auto;
  flex: 1;
}

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.coupon-item {
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 16px;
  padding: 18px;
  cursor: pointer;
}

.coupon-item.active {
  border-color: #3b9dfd;
  box-shadow: 0 8px 20px rgba(59, 157, 253, 0.14);
}

.coupon-item.disabled {
  opacity: 0.55;
  cursor: default;
}

.coupon-amount {
  min-width: 82px;
  font-size: 28px;
  font-weight: 700;
  color: #ef4444;
}

.coupon-info {
  flex: 1;
  min-width: 0;
}

.coupon-title {
  font-size: 16px;
  color: #1e293b;
  margin-bottom: 8px;
}

.coupon-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
}

.coupon-action {
  color: #3b82f6;
  font-size: 14px;
  white-space: nowrap;
}

.drawer-footer {
  padding: 16px 24px 24px;
  border-top: 1px solid #eef2f7;
}

.confirm-btn {
  width: 100%;
  border: none;
  background: #e64340;
  color: #fff;
  border-radius: 12px;
  padding: 14px 16px;
}

@media (max-width: 1100px) {
  .content-wrapper {
    flex-direction: column;
  }

  .summary-panel {
    width: 100%;
    position: static;
  }
}

@media (max-width: 768px) {
  .submit-order-page {
    padding: 16px;
  }

  .coupon-drawer {
    width: 100%;
  }
}
</style>
