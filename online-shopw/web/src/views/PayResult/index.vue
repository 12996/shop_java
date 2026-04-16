<template>
  <div class="pay-result-page fade-in">
    <div class="result-card glass-panel">
      <div class="icon-wrap success fade-in-up">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
          <polyline points="22 4 12 14.01 9 11.01"></polyline>
        </svg>
      </div>

      <h1 class="fade-in-up delay-1">订单提交成功！</h1>
      <p class="desc fade-in-up delay-2">您的订单 <span>{{ orderNum }}</span> 已经生成</p>
      
      <div class="actions fade-in-up delay-3">
        <button class="btn secondary" @click="goHome">返回首页</button>
        <button class="btn primary" @click="goOrder">查看订单</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const orderNum = ref('')

onMounted(() => {
  orderNum.value = route.query.orderNumbers || route.query.orderNum || '123456789'
  // clear cart tokens
  localStorage.removeItem('basketIds')
})

const goHome = () => router.push('/')
const goOrder = () => router.push('/user') // redirect to user center or order list
</script>

<style scoped>
.pay-result-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 100px);
  padding: 20px;
}

.result-card {
  width: 100%;
  max-width: 480px;
  background: white;
  padding: 60px 40px;
  border-radius: 24px;
  text-align: center;
  flex-direction: column;
  display: flex;
  align-items: center;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.icon-wrap {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #dcfce7;
  color: #22c55e;
  margin-bottom: 24px;
}

.icon-wrap svg {
  width: 40px;
  height: 40px;
}

h1 {
  font-size: 28px;
  color: #1e293b;
  margin: 0 0 12px 0;
}

.desc {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 40px 0;
}

.desc span {
  color: #3b82f6;
  font-weight: 500;
}

.actions {
  display: flex;
  gap: 16px;
  width: 100%;
}

.btn {
  flex: 1;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn.primary {
  background: linear-gradient(135deg, #4aa3ff 0%, #3b82f6 100%);
  color: white;
  box-shadow: 0 10px 20px rgba(59, 130, 246, 0.25);
}

.btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(59, 130, 246, 0.35);
}

.btn.secondary {
  background: #f8fafc;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn.secondary:hover {
  background: #f1f5f9;
  color: #1e293b;
}

/* Animations */
.fade-in { animation: fadeIn 0.4s ease-out; }
.fade-in-up { animation: fadeInUp 0.5s ease-out both; }
.delay-1 { animation-delay: 0.1s; }
.delay-2 { animation-delay: 0.2s; }
.delay-3 { animation-delay: 0.3s; }

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
