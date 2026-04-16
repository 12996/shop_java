<template>
  <div class="home-page fade-in">
    
    <!-- Top Search Bar (Simulated) -->
    <div class="search-wrap">
      <div class="search-bar" @click="toSearchPage">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
        <span>搜索商品</span>
      </div>
    </div>

    <!-- Carousel Banner -->
    <div class="carousel-container" v-if="indexImgs.length > 0">
      <div class="slider" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
        <div class="slide" v-for="img in indexImgs" :key="img.imgUrl">
          <img :src="img.imgUrl" alt="banner" />
        </div>
      </div>
      <div class="dots">
        <span v-for="(img, idx) in indexImgs" :key="idx" 
              :class="['dot', { active: currentIndex === idx }]"
              @click="currentIndex = idx"></span>
      </div>
    </div>
    <div class="carousel-container placeholder-banner" v-else>
      <p>正在努力获取大图...</p>
    </div>

    <!-- Quick Action Icons -->
    <div class="quick-actions">
      <div class="action-item" @click="toClassifyPage(1, '新品推荐')">
        <div class="icon light-bulb">
           <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v2"></path><path d="M12 20v2"></path><path d="M5 5l1.5 1.5"></path><path d="M17.5 17.5L19 19"></path><path d="M2 12h2"></path><path d="M20 12h2"></path><path d="M5 19l1.5-1.5"></path><path d="M17.5 5.5L19 4"></path><circle cx="12" cy="12" r="5"></circle></svg>
        </div>
        <span>新品推荐</span>
      </div>
      <div class="action-item" @click="toClassifyPage(3, '每日疯抢')">
        <div class="icon flame">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8.5 14.5A2.5 2.5 0 0011 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 11-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 002.5 2.5z"></path></svg>
        </div>
        <span>每日疯抢</span>
      </div>
      <div class="action-item" @click="toCouponCenter">
        <div class="icon ticket">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="8" width="18" height="8" rx="2" ry="2"></rect><path d="M10 8v8"></path><path d="M14 8v8"></path></svg>
        </div>
        <span>领优惠券</span>
      </div>
    </div>

    <!-- Notice Bar -->
    <div class="notice-bar" v-if="news.length > 0" @click="onNewsPage">
      <span class="notice-tag">通知</span>
      <div class="notice-text">
        <span>{{ news[0].title }}</span>
      </div>
      <svg viewBox="0 0 24 24" class="arrow" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"></polyline></svg>
    </div>

    <!-- Product Grids organized by Tags -->
    <template v-for="tag in taglist" :key="tag.id">
      <section class="section-container" v-if="tag.prods && tag.prods.length > 0">
        <div class="section-header">
           <div class="header-left">
             <div class="blue-block"></div>
             <h2>{{ tag.title }}</h2>
           </div>
           <a href="#" class="view-more">查看更多</a>
        </div>
        
        <div class="product-grid">
          <div class="product-card" v-for="product in tag.prods" :key="product.prodId" @click.stop="toProdPage(product.prodId)">
            <div class="image-wrapper">
              <img :src="product.pic" :alt="product.prodName" />
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.prodName }}</h3>
              <p class="product-brief">{{ product.brief || '精选好物' }}</p>
              <div class="price-row">
                 <p class="product-price"><span class="currency">¥</span>{{ product.price }}</p>
                 <button class="add-cart-btn" @click.prevent.stop="addToCart(product)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                 </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../utils/http'

const router = useRouter()
const indexImgs = ref([])
const news = ref([])
const taglist = ref([])

const currentIndex = ref(0)
let timer = null

onMounted(() => {
  getAllData()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const startCarousel = () => {
  if (timer) clearInterval(timer)
  timer = setInterval(() => {
    if (indexImgs.value.length > 0) {
      currentIndex.value = (currentIndex.value + 1) % indexImgs.value.length
    }
  }, 4000)
}

const getAllData = () => {
  getIndexImgs()
  getNoticeList()
  getTag()
}

const getIndexImgs = async () => {
  try {
    const res = await http.get('/indexImgs')
    indexImgs.value = res || []
    if (indexImgs.value.length > 1) {
      startCarousel()
    }
  } catch (error) {
    console.error('Failed to load banner images', error)
  }
}

const getNoticeList = async () => {
  try {
    const res = await http.get('/shop/notice/topNoticeList')
    news.value = res || []
  } catch (error) {
    console.error('Failed to load news', error)
  }
}

const getTag = async () => {
  try {
    const res = await http.get('/prod/tag/prodTagList')
    taglist.value = res || []
    
    // Fetch products for each tag
    for (let i = 0; i < taglist.value.length; i++) {
      getTagProd(taglist.value[i].id, i)
    }
  } catch (error) {
    console.error('Failed to load tags', error)
  }
}

const getTagProd = async (id, index) => {
  try {
    const res = await http.get('/prod/prodListByTagId', {
      params: { tagId: id, size: 6 }
    })
    
    const validProds = (res.records || []).filter(p => !shouldHideHomeProd(p))
    taglist.value[index].prods = validProds
  } catch (error) {
    console.error('Failed to load prods for tag group', error)
  }
}

const shouldHideHomeProd = (prod) => {
  const name = ((prod && prod.prodName) || '').replace(/\s+/g, '')
  return /\u65fa\u4ed4\u725b\u5976/.test(name)
}

const addToCart = async (prod) => {
  try {
    const prodInfo = await http.get('/prod/prodInfo', { params: { prodId: prod.prodId } })
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
  }
}

// Router Hooks
const toSearchPage = () => router.push('/search')
const toClassifyPage = (sts, title) => router.push({ path: '/classify', query: { sts, title } })
const toLimitedTimeOffer = () => alert('当前活动时间未开始或已结束，敬请期待！')
const toCouponCenter = () => router.push('/coupon')
const onNewsPage = () => console.log('Routing to news detail')
const toProdPage = (id) => router.push({ path: '/product', query: { prodid: id } })

</script>

<style scoped>
.fade-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.home-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 1440px;
  margin: 0 auto;
}

/* Simulated Mobile-like elements mapped to Desktop friendly blocks */

/* Search */
.search-wrap {
  width: 100%;
}
.search-bar {
  background: white;
  border-radius: 30px;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #94a3b8;
  cursor: text;
  box-shadow: 0 4px 15px rgba(0,0,0,0.02);
  border: 1px solid rgba(226, 232, 240, 0.8);
}
.search-bar svg { width: 18px; height: 18px; }
.search-bar span { font-size: 15px; }

/* Carousel */
.carousel-container {
  position: relative;
  width: 100%;
  height: 340px;
  border-radius: 20px;
  overflow: hidden;
  background: #f1f5f9;
  box-shadow: 0 10px 30px rgba(0,0,0,0.03);
}

.placeholder-banner {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #94a3b8;
}

.slider {
  display: flex;
  width: 100%;
  height: 100%;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide {
  min-width: 100%;
  height: 100%;
}

.slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  background: rgba(255,255,255,0.5);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.dot.active {
  background: white;
  width: 24px;
}

/* Quick Actions */
.quick-actions {
  display: flex;
  justify-content: space-around;
  padding: 24px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.02);
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.action-item:hover {
  transform: translateY(-4px);
}

.action-item .icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.action-item .icon svg { width: 28px; height: 28px; }

.icon.light-bulb { background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%); }
.icon.timer { background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%); }
.icon.flame { background: linear-gradient(135deg, #f87171 0%, #ef4444 100%); }
.icon.ticket { background: linear-gradient(135deg, #facc15 0%, #eab308 100%); }

.action-item span {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

/* Notice Bar */
.notice-bar {
  display: flex;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 14px 20px;
  gap: 12px;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0,0,0,0.02);
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.notice-tag {
  background: #fee2e2;
  color: #ef4444;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: bold;
}

.notice-text {
  flex: 1;
  color: #334155;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-bar .arrow {
  width: 18px;
  height: 18px;
  color: #cbd5e1;
}

/* Section Header styling (similar to original app's blue bar) */
.section-container {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.02);
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.blue-block {
  width: 4px;
  height: 18px;
  background: #3b9dfd;
  border-radius: 2px;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  color: #1e293b;
  font-weight: 600;
}

.view-more {
  color: #94a3b8;
  text-decoration: none;
  font-size: 14px;
  background: #f1f5f9;
  padding: 4px 12px;
  border-radius: 12px;
}

/* Product Grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.product-card {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #f1f5f9;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
  border-color: rgba(59, 157, 253, 0.2);
}

.image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 1:1 Aspect */
  background-color: #f8fafc;
}

.image-wrapper img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain; /* Like the miniprogram's style */
}

.product-info {
  padding: 16px;
}

.product-name {
  margin: 0 0 6px 0;
  font-size: 15px;
  font-weight: 500;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-brief {
  margin: 0 0 12px 0;
  font-size: 12px;
  color: #94a3b8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #ef4444;
}

.currency {
  font-size: 12px;
  margin-right: 2px;
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
</style>
