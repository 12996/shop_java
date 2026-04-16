<template>
  <div class="category-page fade-in">
    <div class="cat-sidebar">
      <ul class="cat-list">
        <li
          v-for="cat in categoryList"
          :key="cat.categoryId"
          :class="['cat-item', { active: activeCat === cat.categoryId }]"
          @click="selectCategory(cat)"
        >
          {{ cat.categoryName }}
        </li>
      </ul>
    </div>

    <div class="cat-content">
      <div class="cat-header">
        <h2>{{ currentCategoryName }}</h2>
        <p class="subtitle">共 {{ productList.length }} 件好物</p>
      </div>

      <div class="grid-layout">
        <div
          v-for="prod in productList"
          :key="prod.prodId"
          class="prod-item"
          @click="goToProdPage(prod.prodId)"
        >
          <div class="img-box">
            <img v-if="prod.pic" :src="prod.pic" :alt="prod.prodName" class="prod-cover" />
            <div
              v-else
              class="placeholder-img"
              :style="{ filter: `hue-rotate(${prod.prodId * 15}deg)` }"
            ></div>
          </div>
          <div class="info-box">
            <p class="title">{{ prod.prodName }}</p>
            <div class="price-row">
              <p class="price">¥ {{ prod.price }}</p>
              <button class="add-cart-btn" @click.prevent.stop="addToCart(prod)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="productList.length === 0" class="empty-state">
        <p>该分类下暂无商品</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../utils/http'

const activeCat = ref(null)
const categoryList = ref([])
const productList = ref([])
const router = useRouter()

const currentCategoryName = computed(() => {
  return categoryList.value.find((c) => c.categoryId === activeCat.value)?.categoryName || '加载中...'
})

onMounted(() => {
  getCategoryInfo()
})

const getCategoryInfo = async () => {
  try {
    const res = await http.get('/category/categoryInfo', {
      params: { parentId: '' }
    })

    if (res && res.length > 0) {
      categoryList.value = res
      activeCat.value = res[0].categoryId
      await getProdList(res[0].categoryId)
    }
  } catch (err) {
    console.error('Failed to load category info', err)
  }
}

const selectCategory = async (cat) => {
  activeCat.value = cat.categoryId
  await getProdList(cat.categoryId)
}

const getProdList = async (categoryId) => {
  try {
    const res = await http.get('/prod/pageProd', {
      params: { categoryId }
    })
    productList.value = res.records || []
  } catch (err) {
    console.error('Failed to load products for category', err)
    productList.value = []
  }
}

const addToCart = async (prod) => {
  try {
    const prodInfo = await http.get('/prod/prodInfo', {
      params: { prodId: prod.prodId }
    })

    if (prodInfo.skuList && prodInfo.skuList.length > 0) {
      await http.post('/p/shopCart/changeItem', {
        basketId: 0,
        count: 1,
        prodId: prodInfo.prodId,
        shopId: prodInfo.shopId,
        skuId: prodInfo.skuList[0].skuId
      })
      alert('加入购物车成功')
    }
  } catch (err) {
    console.error('Failed to add to cart', err)
    alert(err.message || '加入购物车失败')
  }
}

const goToProdPage = (prodId) => {
  router.push({ path: '/product', query: { prodid: prodId } })
}
</script>

<style scoped>
.fade-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.category-page {
  display: flex;
  height: calc(100vh - 80px);
  gap: 30px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.02);
  padding: 10px;
  border: 1px solid rgba(226, 232, 240, 0.6);
  overflow: hidden;
}

.cat-sidebar {
  width: 180px;
  background: #f8fafc;
  border-radius: 12px;
  padding: 10px 0;
  overflow-y: auto;
}

.cat-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.cat-item {
  padding: 16px 20px;
  font-size: 15px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.cat-item:hover {
  background: #f1f5f9;
  color: #334155;
}

.cat-item.active {
  color: #3b9dfd;
  font-weight: 600;
  background: white;
}

.cat-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 10%;
  height: 80%;
  width: 4px;
  background: #3b9dfd;
  border-radius: 0 4px 4px 0;
}

.cat-content {
  flex: 1;
  padding: 20px 30px;
  overflow-y: auto;
}

.cat-header {
  margin-bottom: 30px;
}

.cat-header h2 {
  margin: 0 0 5px 0;
  font-size: 22px;
  color: #1e293b;
}

.cat-header .subtitle {
  margin: 0;
  font-size: 14px;
  color: #94a3b8;
}

.grid-layout {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
}

.prod-item {
  cursor: pointer;
  transition: transform 0.2s;
}

.prod-item:hover {
  transform: translateY(-4px);
}

.img-box {
  width: 100%;
  padding-top: 100%;
  background: #f1f5f9;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.prod-cover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #a5b4fc 0%, #818cf8 100%);
  opacity: 0.2;
}

.info-box {
  padding: 12px 5px;
}

.title {
  margin: 0 0 6px 0;
  font-size: 14px;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.price {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #3b9dfd;
}

.add-cart-btn {
  background: #ecfdf5;
  border: 1px solid #a7f3d0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #10b981;
  cursor: pointer;
  transition: all 0.2s;
}

.add-cart-btn:hover {
  background: #10b981;
  color: white;
}

.add-cart-btn svg {
  width: 16px;
  height: 16px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60%;
  color: #94a3b8;
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
