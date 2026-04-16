<template>
  <div class="classify-page fade-in">
    <div class="header">
      <h2>{{ title || '精选推荐' }}</h2>
      <p class="subtitle">探索更多优质商品</p>
    </div>

    <div class="product-grid">
      <div class="product-card" v-for="product in products" :key="product.prodId" @click="toProdPage(product.prodId)">
        <div class="image-wrapper">
          <img :src="product.pic" :alt="product.prodName" />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.prodName }}</h3>
          <p class="product-brief">{{ product.brief || '品质首选' }}</p>
          <div class="price-row">
            <p class="product-price"><span class="currency">¥</span>{{ product.price }}</p>
            <button class="add-cart-btn" @click.stop="addToCart(product)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="empty-state" v-if="products.length === 0">
      <p>正在拉取专属数据或该分类下暂无商品...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../../utils/http'

const route = useRoute()
const router = useRouter()
const title = ref('')
const sts = ref('')
const products = ref([])

const loadData = async () => {
  title.value = route.query.title || '分类详情'
  sts.value = route.query.sts || ''
  
  let url = '/prod/pageProd'
  let queryParams = { current: 1, size: 20 }

  if (sts.value == '0' && route.query.tagid) {
    url = '/prod/prodListByTagId'
    queryParams.tagId = route.query.tagid
  } else if (sts.value == '1') {
    url = '/prod/lastedProdPage'
  } else if (sts.value == '2') {
    url = '/prod/discountProdList'
  } else if (sts.value == '3') {
    url = '/prod/moreBuyProdList'
  } else if (sts.value == '4' && route.query.tagid) {
    url = '/coupon/prodListByCouponId'
    queryParams.couponId = route.query.tagid
  } else if (sts.value == '5') {
    url = '/p/user/collection/prods'
  }

  try {
    const res = await http.get(url, { params: queryParams })
    products.value = res.records || []
  } catch(e) {
    console.error('Failed to load list', e)
  }
}

onMounted(() => {
  loadData()
})

watch(() => route.query, () => {
  loadData()
})

const toProdPage = (id) => {
  router.push({ path: '/product', query: { prodid: id } })
}

const addToCart = async (product) => {
  try {
    const prodInfo = await http.get('/prod/prodInfo', { params: { prodId: product.prodId } })
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
  } catch (error) {
    console.error('Failed to add to cart', error)
    alert(error.message || '加入购物车失败')
  }
}

</script>

<style scoped>
.fade-in { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.classify-page {
  padding: 20px;
  max-width: 1440px;
  margin: 0 auto;
}

.header {
  background: white;
  padding: 30px;
  border-radius: 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
  border: 1px solid rgba(226, 232, 240, 0.6);
  text-align: center;
}

.header h2 { margin: 0 0 10px 0; font-size: 28px; color: #1e293b; }
.header .subtitle { margin: 0; color: #94a3b8; }

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
}

.product-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0,0,0,0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(226, 232, 240, 0.6);
  cursor: pointer;
}

.product-card:hover { transform: translateY(-6px); box-shadow: 0 12px 30px rgba(0,0,0,0.06); }
.image-wrapper { position: relative; width: 100%; padding-top: 100%; background-color: #f8fafc; }
.image-wrapper img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; }

.product-info { padding: 20px; }
.product-name { margin: 0 0 8px 0; font-size: 16px; font-weight: 600; color: #334155; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.product-brief { margin: 0 0 16px 0; font-size: 13px; color: #94a3b8; }
.price-row { display: flex; justify-content: space-between; align-items: center; }
.product-price { margin: 0; font-size: 20px; font-weight: 700; color: #ef4444; }
.currency { font-size: 14px; margin-right: 2px; }

.add-cart-btn {
  background: #ecfdf5; border: 1px solid #a7f3d0; width: 36px; height: 36px;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  color: #10b981; cursor: pointer; transition: all 0.2s;
}
.add-cart-btn:hover { background: #10b981; color: white; }
.add-cart-btn svg { width: 18px; height: 18px; }

.empty-state { text-align: center; color: #94a3b8; margin-top: 60px; font-size: 16px; }
</style>
