<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="login-header">
        <h2 class="sub-title">Online-shop</h2>
        <h1 class="main-title">用户登录</h1>
      </div>
      
      <form class="login-form" @submit.prevent="handleLogin">
        
        <div class="input-group">
          <span class="icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
          </span>
          <input 
            type="text" 
            v-model="username" 
            placeholder="请输入用户名"
            autocomplete="off"
            required
          />
        </div>
        
        <div class="input-group">
          <span class="icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>
          </span>
          <input 
            :type="passwordVisible ? 'text' : 'password'" 
            v-model="password" 
            placeholder="请输入密码"
            required
          />
          <span class="icon-eye" @click="togglePassword">
            <svg v-if="!passwordVisible" viewBox="0 0 24 24" fill="none" class="eye-icon" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" class="eye-icon" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>
          </span>
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>

        <div class="footer-links">
          <span>还没有账号？</span>
          <a href="#" class="register-link" @click.prevent="goToRegister">立即注册</a>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { encryptPassword } from '../../utils/crypto'
import http from '../../utils/http'

const username = ref('')
const password = ref('')
const passwordVisible = ref(false)
const loading = ref(false)
const router = useRouter()

const togglePassword = () => {
  passwordVisible.value = !passwordVisible.value
}

const handleLogin = async () => {
  if (!username.value || !password.value) return
  
  loading.value = true
  try {
    const encryptedPwd = encryptPassword(password.value)
    
    // Call the true endpoint
    const res = await http.post('/login', { 
      userName: username.value, 
      passWord: encryptedPwd 
    })
    
    // Store token. Based on mall4m, backend returns accessToken
    localStorage.setItem('token', res.accessToken || res)
    
    alert('登录成功！')
    router.push('/')
  } catch (err) {
    console.error('Login failed: ', err)
    alert('账号或密码不正确: ' + (err.message || '系统错误'))
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
/* Page Layout */
.login-wrapper {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f6f8fb; /* light grey/blue background as in original image */
  z-index: 1000;
}

/* White Card */
.login-card {
  background: #ffffff;
  width: 90%;
  max-width: 480px;
  border-radius: 12px;
  padding: 50px 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); /* slight soft shadow */
}

/* Typography matching image */
.login-header {
  text-align: center;
  margin-bottom: 40px;
}
.sub-title {
  font-size: 16px;
  color: #8c8c8c;
  margin: 0 0 10px 0;
  font-weight: 500;
  letter-spacing: 1px;
}
.main-title {
  font-size: 32px;
  color: #3b9dfd; /* Bright blue */
  margin: 0;
  font-weight: 700;
  letter-spacing: 2px;
}

/* Inputs form layout */
.login-form {
  display: flex;
  flex-direction: column;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  margin-bottom: 25px;
  background-color: #ffffff;
  transition: all 0.3s ease;
}

.input-group:focus-within {
  border-color: #3b9dfd;
  box-shadow: 0 0 0 2px rgba(59, 157, 253, 0.1);
}

.icon {
  display: flex;
  padding-left: 15px;
  color: #c0c0c0;
}
.icon svg {
  width: 20px;
  height: 20px;
}

.input-group input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 16px 15px;
  font-size: 16px;
  color: #333;
  outline: none;
}
.input-group input::placeholder {
  color: #b0b0b0;
}

.icon-eye {
  display: flex;
  padding: 0 15px;
  cursor: pointer;
  color: #c0c0c0;
}
.icon-eye:hover {
  color: #8c8c8c;
}
.eye-icon {
  width: 20px;
  height: 20px;
}

/* Login Button */
.submit-btn {
  background-color: #4aa3ff; /* Deep colorful blue */
  background: linear-gradient(135deg, #5db0ff 0%, #3594fb 100%);
  color: white;
  border: none;
  padding: 16px;
  font-size: 18px;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 10px;
  font-weight: 600;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(53, 148, 251, 0.4);
}
.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(53, 148, 251, 0.5);
  background: linear-gradient(135deg, #64b5ff 0%, #3e9bfb 100%);
}
.submit-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(53, 148, 251, 0.4);
}
.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Footer links */
.footer-links {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #999;
}
.register-link {
  color: #3b9dfd;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s;
}
.register-link:hover {
  color: #2a81da;
  text-decoration: underline;
}

/* Responsive adjust */
@media (max-width: 480px) {
  .login-card {
    padding: 40px 25px;
    width: 95%;
  }
}
</style>
