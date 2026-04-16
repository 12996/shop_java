<template>
  <div class="product-page fade-in" v-if="prod">
    
    <!-- Top Layout: Images + Side Info -->
    <div class="prod-main">
      
      <!-- Left: Gallery -->
      <div class="gallery-col">
        <div class="main-image">
          <img :src="currentImg" alt="" />
        </div>
        <div class="thumbnail-list" v-if="imgs.length > 1">
           <div v-for="(img, idx) in imgs" :key="idx" 
                :class="['thumb', { active: currentImg === img }]"
                @click="currentImg = img">
             <img :src="img" alt="" />
           </div>
        </div>
      </div>
      
      <!-- Right: Details & SKU -->
      <div class="info-col">
        <div class="prod-header">
          <h1>{{ prod.prodName }}</h1>
          <p class="brief">{{ prod.brief }}</p>
          <div class="price-wrap">
             <span class="currency">¥</span>
             <span class="price-num">{{ defaultSku ? defaultSku.price : prod.price }}</span>
          </div>
        </div>
        
        <!-- SKU Selector -->
        <div class="sku-section" v-if="Object.keys(skuGroup).length > 0">
           <div class="sku-group" v-for="(options, key) in skuGroup" :key="key">
             <h4>{{ key }}</h4>
             <div class="sku-options">
               <button v-for="opt in options" :key="opt"
                       :class="['sku-btn', { active: selectedPropObj[key] === opt, disabled: !isPropAvailable(key, opt) }]"
                       @click="selectProp(key, opt)"
                       :disabled="!isPropAvailable(key, opt)">
                 {{ opt }}
               </button>
             </div>
           </div>
        </div>
        
        <!-- Actions -->
        <div class="actions">
          <div class="qty-control">
             <button @click="changeQty(-1)" :disabled="cartQty <= 1">-</button>
             <input type="text" v-model="cartQty" readonly />
             <button @click="changeQty(1)">+</button>
          </div>
          
          <button class="cart-btn" @click="addToCart" :disabled="!findSku">加入购物车</button>
          <button class="buy-btn" @click="buyNow" :disabled="!findSku">立即购买</button>
          <button class="fav-btn" @click="toggleFav">
             <svg v-if="!isCollection" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
             <svg v-else viewBox="0 0 24 24" fill="#ef4444" stroke="#ef4444" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
             <span>{{ isCollection ? '已收藏' : '收藏' }}</span>
          </button>
        </div>
        
      </div>
    </div>
    
    <!-- Bottom: HTML Content Description -->
    <div class="prod-desc">
      <h3>商品详情</h3>
      <div class="rich-text" v-html="formattedContent"></div>
    </div>
    
  </div>
  
  <div v-else-if="loading" class="loading-state">
     <p>加载中...</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../../utils/http'

const route = useRoute()
const router = useRouter()

const prodId = route.query.prodid
const prod = ref(null)
const loading = ref(true)
const imgs = ref([])
const currentImg = ref('')
const isCollection = ref(false)

const cartQty = ref(1)

// SKU structures
const skuList = ref([])
const skuGroup = ref({})
const allProperties = ref([])
const propKeys = ref([])
const selectedPropObj = ref({})
const defaultSku = ref(null)
const findSku = ref(true)

const formattedContent = computed(() => {
  if(!prod.value || !prod.value.content) return ''
  // simple formatting trick if needed
  return prod.value.content.replace(/<img/g, '<img style="max-width:100%;height:auto;"')
})

onMounted(() => {
  if (prodId) {
    loadProd()
    checkCollection()
  }
})

const loadProd = async () => {
  try {
    const res = await http.get('/prod/prodInfo', { params: { prodId } })
    if (res) {
      prod.value = res
      imgs.value = res.imgs ? res.imgs.split(',') : (res.pic ? [res.pic] : [])
      currentImg.value = imgs.value[0] || ''
      skuList.value = res.skuList || []
      
      initSkuLogic(skuList.value)
    }
  } catch(e) {
    console.error('Failed to load product', e)
  } finally {
    loading.value = false
  }
}

const checkCollection = async () => {
  try {
    // API throws 401 if unauth, so wrap safely
    const res = await http.get('/p/user/collection/isCollection', { params: { prodId } })
    isCollection.value = res
  } catch(e) {} // ignore unauth quietly
}

const toggleFav = async () => {
  try {
    await http.post('/p/user/collection/addOrCancel', prodId, { headers: {'Content-Type': 'application/json'}})
    isCollection.value = !isCollection.value
  } catch(e) {
    if(e.response && e.response.status === 401) {
      router.push('/login')
    }
  }
}

// --- SKU LOGIC ---
const initSkuLogic = (list) => {
  if (!list || list.length === 0) return
  if (list.length === 1 && !list[0].properties) {
    defaultSku.value = list[0]
    return
  }
  
  let group = {}
  let aProps = []
  let keys = []
  let isDefaultFound = false
  
  for(let i = 0; i < list.length; i++) {
    // auto select first matching price (or just first one)
    if (!isDefaultFound && list[i].price === prod.value.price) {
      defaultSku.value = list[i]
      isDefaultFound = true
    }
    
    let propsStr = list[i].properties || ""
    aProps.push(propsStr)
    
    let pairs = propsStr.split(';').filter(p => p)
    for(let j = 0; j < pairs.length; j++) {
      let kv = pairs[j].split(':')
      let key = kv[0]
      let val = kv[1]
      
      if (isDefaultFound && list[i] === defaultSku.value) {
        if (!keys.includes(key)) keys.push(key)
        selectedPropObj.value[key] = val
      }
      
      if (!group[key]) {
        group[key] = [val]
      } else if (!group[key].includes(val)) {
        group[key].push(val)
      }
    }
  }
  
  // Safety fallback if no generic default mapped properly
  if (!isDefaultFound && list.length > 0) {
    defaultSku.value = list[0]
  }

  skuGroup.value = group
  allProperties.value = aProps
  propKeys.value = keys
  
  parseSelectedObjToVals()
}

const selectProp = (key, val) => {
  selectedPropObj.value[key] = val
  parseSelectedObjToVals()
}

const parseSelectedObjToVals = () => {
  let propsStr = ""
  for (let k in selectedPropObj.value) {
    propsStr += `${k}:${selectedPropObj.value[k]};`
  }
  if (propsStr.endsWith(';')) propsStr = propsStr.slice(0, -1)
  
  let found = false
  for(let i=0; i<skuList.value.length; i++) {
    // Exact or loose match depending on order, but the mini program backend formats standardly.
    // For safety, parse and compare maps, but we follow exact string matching like mini-program:
    let skuPropsStr = skuList.value[i].properties || ""
    
    // Sort both string pairs to guarantee match independent of order
    let p1 = propsStr.split(';').sort().join(';')
    let p2 = skuPropsStr.split(';').sort().join(';')
    
    if (p1 === p2) {
      found = true
      defaultSku.value = skuList.value[i]
      currentImg.value = defaultSku.value.pic || currentImg.value
      break;
    }
  }
  findSku.value = found
}

const isPropAvailable = (key, opt) => {
  // Simplified mockup check: always return true for now to avoid locking valid paths
  // A complete SKU cross-rel check could use `allProperties.value` mapping filtering
  return true
}

const changeQty = (amount) => {
  if (cartQty.value + amount > 0) cartQty.value += amount
}

const addToCart = async () => {
  try {
    await http.post('/p/shopCart/changeItem', {
      basketId: 0,
      count: cartQty.value,
      prodId: prodId,
      shopId: prod.value.shopId,
      skuId: defaultSku.value ? defaultSku.value.skuId : 0
    })
    alert('加入购物车成功')
  } catch(e) {
    console.error('Failed cart addition:', e)
  }
}

const buyNow = () => {
  // Save fake local storage for checkout
  localStorage.setItem("orderItem", JSON.stringify({
    prodId: prodId,
    skuId: defaultSku.value ? defaultSku.value.skuId : 0,
    prodCount: cartQty.value,
    shopId: prod.value.shopId
  }))
  alert('立即购买跳转未接入 - 因为之前仅写了 Cart 流转')
}
</script>

<style scoped>
.fade-in { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.product-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

/* Prod Main split */
.prod-main {
  display: flex;
  gap: 60px;
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
  border: 1px solid rgba(226, 232, 240, 0.6);
  min-height: 500px;
}

.gallery-col {
  width: 480px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.main-image {
  width: 100%;
  padding-top: 100%;
  position: relative;
  background: #f8fafc;
  border-radius: 16px;
  overflow: hidden;
}
.main-image img {
  position: absolute; top:0; left:0; width:100%; height:100%; object-fit:contain;
}

.thumbnail-list {
  display: flex;
  gap: 12px;
  overflow-x: auto;
}

.thumb {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  background: #f8fafc;
  opacity: 0.6;
  transition: all 0.3s;
}
.thumb.active { border-color: #3b9dfd; opacity: 1; }
.thumb img { width: 100%; height: 100%; object-fit: cover; }

.info-col {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.prod-header h1 { margin: 0 0 10px 0; font-size: 28px; color: #1e293b; font-weight: 700; line-height: 1.4; }
.prod-header .brief { margin: 0 0 20px 0; font-size: 16px; color: #64748b; }
.price-wrap { color: #ef4444; margin-bottom: 30px; }
.currency { font-size: 18px; margin-right: 4px; }
.price-num { font-size: 38px; font-weight: 800; }

.sku-section { margin-bottom: 30px; display: flex; flex-direction: column; gap: 20px; }
.sku-group h4 { margin: 0 0 10px 0; font-size: 15px; color: #64748b; }
.sku-options { display: flex; flex-wrap: wrap; gap: 12px; }
.sku-btn {
  background: #f8fafc; border: 1px solid #e2e8f0; padding: 10px 24px; border-radius: 8px; cursor: pointer;
  font-size: 14px; color: #334155; transition: all 0.2s;
}
.sku-btn:hover:not(.disabled) { background: #f1f5f9; }
.sku-btn.active { background: #e0f2fe; border-color: #3b9dfd; color: #0284c7; font-weight: 600; }
.sku-btn.disabled { opacity: 0.4; cursor: not-allowed; text-decoration: line-through; }

.actions { display: flex; gap: 16px; align-items: center; padding-top: 30px; border-top: 1px dashed #e2e8f0; }
.qty-control { display: flex; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; height: 50px; }
.qty-control button { width: 40px; background: #f8fafc; border: none; cursor: pointer; font-size: 18px; color: #64748b; }
.qty-control button:hover { background: #e2e8f0; }
.qty-control input { width: 50px; border: none; text-align: center; border-left: 1px solid #e2e8f0; border-right: 1px solid #e2e8f0; font-weight: 600; font-size: 16px; }

.cart-btn, .buy-btn { height: 50px; padding: 0 40px; border-radius: 25px; font-size: 16px; font-weight: 600; cursor: pointer; transition: transform 0.2s; border: none; }
.cart-btn { background: #e0f2fe; color: #0284c7; }
.cart-btn:hover:not(:disabled) { background: #bae6fd; transform: translateY(-2px); }
.buy-btn { background: linear-gradient(135deg, #3b9dfd 0%, #2563eb 100%); color: white; box-shadow: 0 8px 20px rgba(59, 157, 253, 0.3); }
.buy-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 24px rgba(59, 157, 253, 0.4); }
.cart-btn:disabled, .buy-btn:disabled { opacity: 0.5; cursor: not-allowed; transform: none !important; }

.fav-btn { height: 50px; display: flex; align-items: center; gap: 8px; background: none; border: none; color: #64748b; cursor: pointer; padding: 0 20px; font-size: 15px; }
.fav-btn svg { width: 22px; height: 22px; transition: transform 0.2s; }
.fav-btn:hover svg { transform: scale(1.1); }

/* Bottom Specs/Rich text */
.prod-desc { background: white; padding: 40px; border-radius: 20px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); min-height: 400px; border: 1px solid rgba(226, 232, 240, 0.6); }
.prod-desc h3 { margin: 0 0 30px 0; font-size: 20px; color: #1e293b; padding-bottom: 20px; border-bottom: 1px solid #f1f5f9; }
.rich-text { line-height: 1.8; color: #475569; overflow: hidden; }
/* Global scoping for injected rich-text imgs */
:deep(.rich-text img) { max-width: 100%; height: auto !important; border-radius: 12px; margin: 10px 0; display: block; }
</style>
