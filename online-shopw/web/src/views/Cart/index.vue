<template>
  <div class="cart-page fade-in">
    <div class="page-title">
      <h1>我的购物车 <span>({{ totalCheckedCount }})</span></h1>
    </div>

    <div class="cart-layout">
      <!-- Item List -->
      <div class="cart-list">
        
        <template v-if="cartGroups.length > 0">
          <div v-for="(group, gIndex) in cartGroups" :key="gIndex" class="cart-group">
            <div class="cart-item" v-for="(item, iIndex) in group.shopCartItems" :key="item.basketId">
              <div class="checkbox">
                <input type="checkbox" v-model="item.checked" @change="onItemCheckChange" />
              </div>
              <img :src="item.pic" alt="" class="item-img" />
              <div class="item-info">
                <h3>{{ item.prodName }}</h3>
                <p class="specs">{{ item.skuName || '默认规格' }}</p>
                <p class="price">¥ {{ item.price }}</p>
              </div>
              <div class="item-actions">
                <div class="qty-control">
                  <button class="qty-btn" @click="updateCount(gIndex, iIndex, -1)" :disabled="item.prodCount <= 1">-</button>
                  <input type="text" :value="item.prodCount" readonly />
                  <button class="qty-btn" @click="updateCount(gIndex, iIndex, 1)">+</button>
                </div>
                <button class="del-btn" @click="deleteItem(item.basketId)">删除</button>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="empty-cart">
          <p>购物车是空的，快去挑选好物吧~</p>
        </div>
      </div>

      <!-- Checkout Summary Card -->
      <div class="checkout-card">
        <h2>结算摘要</h2>
        <div class="summary-row">
          <span>商品总价</span>
          <span>¥ {{ summary.totalMoney || '0.00' }}</span>
        </div>
        <div class="summary-row">
          <span>优惠折扣</span>
          <span class="discount">- ¥ {{ summary.subtractMoney || '0.00' }}</span>
        </div>
        <div class="summary-row total-row">
          <span>应付总额</span>
          <span class="total-price">¥ {{ summary.finalMoney || '0.00' }}</span>
        </div>
        <button class="checkout-btn" :disabled="!hasCheckedItems" @click="toFirmOrder">
          去结算 ({{ totalCheckedCount }})
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../utils/http'

const router = useRouter()
const cartGroups = ref([])
const summary = ref({ finalMoney: 0, totalMoney: 0, subtractMoney: 0 })

onMounted(() => {
  loadBasketData()
})

const loadBasketData = async () => {
  try {
    const res = await http.post('/p/shopCart/info', {})
    if (res && res.length > 0) {
      // API returns an array, first element contains shopCartItemDiscounts
      let discounts = res[0].shopCartItemDiscounts || []
      discounts.forEach(discountGroup => {
        discountGroup.shopCartItems.forEach(item => {
          item.checked = true // Web default UX: auto-select newly seen items or all items
        })
      })
      cartGroups.value = discounts
    } else {
      cartGroups.value = []
    }
    calTotalPrice()
  } catch (error) {
    console.error('Failed to load cart', error)
  }
}

const totalCheckedCount = computed(() => {
  let count = 0
  cartGroups.value.forEach(group => {
    group.shopCartItems.forEach(item => {
      if (item.checked) count += item.prodCount
    })
  })
  return count
})

const hasCheckedItems = computed(() => totalCheckedCount.value > 0)

const onItemCheckChange = () => {
  calTotalPrice()
}

const getCheckedBasketIds = () => {
  let ids = []
  cartGroups.value.forEach(group => {
    group.shopCartItems.forEach(item => {
      if (item.checked) ids.push(item.basketId)
    })
  })
  return ids
}

const calTotalPrice = async () => {
  const ids = getCheckedBasketIds()
  if (ids.length === 0) {
    summary.value = { finalMoney: 0, totalMoney: 0, subtractMoney: 0 }
    return
  }
  
  try {
    const res = await http.post('/p/shopCart/totalPay', ids)
    summary.value = {
      finalMoney: res.finalMoney,
      totalMoney: res.totalMoney,
      subtractMoney: res.subtractMoney
    }
  } catch (error) {
    console.error('Failed to calculate exact total', error)
  }
}

const updateCount = async (gIndex, iIndex, changeAmt) => {
  const item = cartGroups.value[gIndex].shopCartItems[iIndex]
  if (item.prodCount === 1 && changeAmt === -1) return
  
  try {
    await http.post('/p/shopCart/changeItem', {
      count: changeAmt,
      prodId: item.prodId,
      skuId: item.skuId,
      shopId: 1
    })
    // Local optimistic update
    item.prodCount += changeAmt
    if(item.checked) calTotalPrice()
  } catch (e) {
    console.error('Update count failed', e)
  }
}

const deleteItem = async (basketId) => {
  if (!confirm('确认要删除选中的商品吗？')) return
  try {
    await http.delete('/p/shopCart/deleteItem', { data: [basketId] }) // Note: depending on backend it might be payload body
    loadBasketData()
  } catch(e) {
    console.error('Delete failed', e)
  }
}

const toFirmOrder = () => {
  const ids = getCheckedBasketIds()
  if (!ids.length) {
    alert('请选择商品')
    return
  }
  // 购物车结算明确走 basketIds，避免立即购买残留缓存干扰
  localStorage.removeItem('orderItem')
  localStorage.setItem('basketIds', JSON.stringify(ids))
  router.push({ path: '/submit-order', query: { orderEntry: '0' } })
}
</script>

<style scoped>
.fade-in { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.page-title { margin-bottom: 30px; }
.page-title h1 {
  font-size: 28px;
  color: #1e293b;
  margin: 0;
}
.page-title span {
  color: #94a3b8;
  font-size: 20px;
  font-weight: 400;
}

.cart-layout {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

/* Left list */
.cart-list {
  flex: 1;
  background: white;
  border-radius: 20px;
  padding: 10px 30px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
  border: 1px solid rgba(226, 232, 240, 0.6);
  min-height: 200px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 30px 0;
  border-bottom: 1px solid #f1f5f9;
}
.cart-group:last-child .cart-item:last-child {
  border-bottom: none;
}

.checkbox input {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: #3b9dfd;
}

.item-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 12px;
  margin: 0 24px;
  background: #f8fafc;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 90%;
}

.specs {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #94a3b8;
}

.price {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #3b9dfd;
}

.item-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 20px;
}

.qty-control {
  display: flex;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.qty-btn {
  background: #f8fafc;
  border: none;
  width: 32px;
  height: 32px;
  cursor: pointer;
  color: #64748b;
  font-size: 16px;
  transition: all 0.2s;
}

.qty-btn:hover:not(:disabled) {
  background: #e2e8f0;
}
.qty-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.qty-control input {
  width: 40px;
  border: none;
  text-align: center;
  border-left: 1px solid #e2e8f0;
  border-right: 1px solid #e2e8f0;
  font-weight: 500;
  color: #334155;
}

.del-btn {
  background: none;
  border: none;
  color: #cbd5e1;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.2s;
}
.del-btn:hover {
  color: #ef4444;
}

/* Right summary */
.empty-cart {
  padding: 60px 0;
  text-align: center;
  color: #94a3b8;
  font-size: 16px;
}

.checkout-card {
  width: 340px;
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.04);
  border: 1px solid rgba(226, 232, 240, 0.6);
  position: sticky;
  top: 40px;
}

.checkout-card h2 {
  margin: 0 0 30px 0;
  font-size: 20px;
  color: #1e293b;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  color: #64748b;
  font-size: 15px;
}

.summary-row .discount {
  color: #ef4444;
}

.total-row {
  border-top: 1px dashed #e2e8f0;
  padding-top: 20px;
  margin-top: 10px;
  align-items: center;
}

.total-row span:first-child {
  font-size: 16px;
  color: #334155;
  font-weight: 600;
}

.total-price {
  font-size: 28px;
  font-weight: 800;
  color: #3b9dfd;
}

.checkout-btn {
  width: 100%;
  margin-top: 30px;
  background: linear-gradient(135deg, #4aa3ff 0%, #3594fb 100%);
  color: white;
  border: none;
  padding: 16px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(59, 157, 253, 0.3);
  transition: all 0.3s;
}

.checkout-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(59, 157, 253, 0.4);
}
.checkout-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #cbd5e1;
  box-shadow: none;
  transform: none;
}
</style>
