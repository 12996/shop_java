<template>
  <div class="address-page fade-in">
    <div class="page-header">
      <div>
        <h1>收货地址</h1>
        <p>{{ isSelectMode ? '选择一个地址用于当前订单' : '管理你的收货地址' }}</p>
      </div>
      <button class="primary-btn" @click="goCreateAddress">新增地址</button>
    </div>

    <div v-if="loading" class="loading-state">正在加载地址...</div>

    <div v-else-if="addressList.length" class="address-list">
      <div
        v-for="item in addressList"
        :key="item.addrId"
        class="address-card"
        :class="{ selectable: isSelectMode, active: selectedAddrId === item.addrId }"
        @click="handleSelectAddress(item)"
      >
        <div class="card-main">
          <div class="card-top">
            <div class="contact">
              <span class="name">{{ item.receiver }}</span>
              <span class="mobile">{{ item.mobile }}</span>
              <span v-if="item.commonAddr === 1" class="default-tag">默认</span>
            </div>
            <button class="text-btn" @click.stop="goEditAddress(item.addrId)">编辑</button>
          </div>

          <div class="address-text">
            {{ item.province }} {{ item.city }} {{ item.area }} {{ item.addr }}
          </div>
        </div>

        <div class="card-actions">
          <button
            class="outline-btn"
            :disabled="item.commonAddr === 1 || defaultLoadingId === item.addrId"
            @click.stop="setDefaultAddress(item)"
          >
            {{ item.commonAddr === 1 ? '默认地址' : (defaultLoadingId === item.addrId ? '设置中...' : '设为默认') }}
          </button>
          <button
            v-if="isSelectMode"
            class="select-btn"
            @click.stop="handleSelectAddress(item)"
          >
            选择地址
          </button>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <p>你还没有收货地址</p>
      <button class="primary-btn" @click="goCreateAddress">去新增</button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../../utils/http'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const addressList = ref([])
const defaultLoadingId = ref(0)
const selectedAddrId = ref(0)
const SELECTED_ADDRESS_KEY = 'selectedAddress'

const isSelectMode = computed(() => route.query.order === '0')

const loadAddressList = async () => {
  loading.value = true
  try {
    const res = await http.get('/p/address/list')
    addressList.value = Array.isArray(res) ? res : []

    const storedAddress = sessionStorage.getItem(SELECTED_ADDRESS_KEY)
    if (storedAddress) {
      try {
        const parsed = JSON.parse(storedAddress)
        selectedAddrId.value = parsed.addrId || 0
      } catch (error) {
        selectedAddrId.value = 0
      }
    } else {
      selectedAddrId.value = addressList.value.find((item) => item.commonAddr === 1)?.addrId || 0
    }
  } catch (error) {
    console.error('Failed to load address list', error)
    addressList.value = []
  } finally {
    loading.value = false
  }
}

const goCreateAddress = () => {
  router.push({
    path: '/edit-address',
    query: {
      ...route.query
    }
  })
}

const goEditAddress = (addrId) => {
  router.push({
    path: '/edit-address',
    query: {
      ...route.query,
      addrId: String(addrId)
    }
  })
}

const setDefaultAddress = async (item) => {
  if (!item?.addrId) {
    return
  }

  defaultLoadingId.value = item.addrId
  try {
    await http.put(`/p/address/defaultAddr/${item.addrId}`, { addrId: item.addrId })
    addressList.value = addressList.value.map((address) => ({
      ...address,
      commonAddr: address.addrId === item.addrId ? 1 : 0
    }))
  } catch (error) {
    alert(error?.message || '设置默认地址失败')
  } finally {
    defaultLoadingId.value = 0
  }
}

const handleSelectAddress = (item) => {
  if (!isSelectMode.value) {
    return
  }

  selectedAddrId.value = item.addrId
  sessionStorage.setItem(SELECTED_ADDRESS_KEY, JSON.stringify(item))
  router.push({
    path: '/submit-order',
    query: {
      orderEntry: route.query.orderEntry || '0'
    }
  })
}

onMounted(() => {
  loadAddressList()
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

.address-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  background: white;
  border-radius: 20px;
  padding: 28px 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.page-header h1 {
  margin: 0 0 8px;
  font-size: 28px;
  color: #1e293b;
}

.page-header p {
  margin: 0;
  color: #94a3b8;
  font-size: 14px;
}

.address-list {
  display: grid;
  gap: 18px;
}

.address-card {
  background: white;
  border-radius: 20px;
  padding: 24px 28px;
  display: flex;
  justify-content: space-between;
  gap: 20px;
  border: 1px solid rgba(226, 232, 240, 0.7);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

.address-card.selectable {
  cursor: pointer;
}

.address-card.active {
  border-color: #3b9dfd;
  box-shadow: 0 10px 24px rgba(59, 157, 253, 0.12);
}

.card-main {
  flex: 1;
  min-width: 0;
}

.card-top {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.contact {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.name,
.mobile {
  font-size: 18px;
  color: #1e293b;
  font-weight: 600;
}

.default-tag {
  display: inline-flex;
  align-items: center;
  height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  color: #3b9dfd;
  background: rgba(59, 157, 253, 0.1);
  font-size: 12px;
}

.address-text {
  color: #64748b;
  line-height: 1.7;
  font-size: 15px;
}

.card-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  min-width: 120px;
}

.primary-btn,
.select-btn {
  border: none;
  background: linear-gradient(135deg, #4aa3ff 0%, #3594fb 100%);
  color: white;
  border-radius: 12px;
  padding: 12px 18px;
  font-size: 14px;
  cursor: pointer;
}

.outline-btn {
  border: 1px solid #dbeafe;
  background: #f8fbff;
  color: #3b82f6;
  border-radius: 12px;
  padding: 11px 16px;
  font-size: 14px;
  cursor: pointer;
}

.outline-btn:disabled {
  cursor: not-allowed;
  color: #94a3b8;
  border-color: #e2e8f0;
  background: #f8fafc;
}

.text-btn {
  padding: 0;
  border: none;
  background: transparent;
  color: #3b82f6;
  font-size: 14px;
  cursor: pointer;
}

.loading-state,
.empty-state {
  background: white;
  border-radius: 20px;
  padding: 56px 24px;
  text-align: center;
  color: #94a3b8;
  border: 1px solid rgba(226, 232, 240, 0.7);
}

.empty-state p {
  margin: 0 0 16px;
}

@media (max-width: 900px) {
  .page-header,
  .address-card {
    padding: 20px;
    border-radius: 16px;
  }

  .page-header,
  .address-card {
    flex-direction: column;
    align-items: stretch;
  }

  .card-actions {
    min-width: 0;
    align-items: stretch;
  }
}
</style>
