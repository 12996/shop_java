<template>
  <div id="app" :class="{'login-layout': isLogin}">
    
    <!-- Left Sidebar -->
    <aside class="sidebar" v-if="!isLogin">
      <div class="brand">
        <div class="logo">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
        </div>
        <h2 class="brand-name">商城系统</h2>
      </div>
      
      <nav class="nav-menu">
        <router-link to="/" class="nav-item">
          <span class="icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
          </span>
          <span class="text">首页 / Home</span>
        </router-link>
        
        <router-link to="/category" class="nav-item">
          <span class="icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
          </span>
          <span class="text">分类 / Category</span>
        </router-link>
        
        <router-link to="/basket" class="nav-item">
          <span class="icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path></svg>
          </span>
          <span class="text">购物车 / Cart</span>
        </router-link>
        
        <router-link to="/user" class="nav-item">
          <span class="icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
          </span>
          <span class="text">我的 / User</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <a href="#" @click.prevent="logout" class="logout-btn">退出登录</a>
      </div>
    </aside>

    <!-- Main Content Area -->
    <main class="main-content">
      <router-view></router-view>
    </main>

  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isLogin = computed(() => route.path === '/login')

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style>
/* Global Reset and Aesthetics */
body, html {
  margin: 0;
  padding: 0;
  font-family: 'Inter', 'Helvetica Neue', Helvetica, 'PingFang SC', sans-serif;
  background-color: #f6f8fb;
  color: #333;
}

#app {
  display: flex;
  min-height: 100vh;
  width: 100%;
}

#app.login-layout {
  display: block; /* For login page, let it be default */
}

/* Sidebar Styles */
.sidebar {
  width: 260px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 100;
  box-shadow: 4px 0 24px rgba(0,0,0,0.02);
}

.brand {
  padding: 40px 30px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  color: #3b9dfd;
  width: 32px;
  height: 32px;
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
  letter-spacing: 1px;
}

.nav-menu {
  flex: 1;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  text-decoration: none;
  color: #64748b;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.25s ease;
}

.nav-item .icon {
  margin-right: 14px;
  display: flex;
}

.nav-item .icon svg {
  width: 20px;
  height: 20px;
}

.nav-item:hover {
  background-color: #f1f5f9;
  color: #334155;
  transform: translateX(4px);
}

.nav-item.router-link-exact-active {
  background: linear-gradient(135deg, #4aa3ff 0%, #3594fb 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(53, 148, 251, 0.3);
}

.nav-item.router-link-exact-active:hover {
  transform: translateY(0); /* Lock hover effect locally */
}

.sidebar-footer {
  padding: 30px;
}

.logout-btn {
  color: #94a3b8;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.logout-btn:hover {
  color: #ef4444;
}

/* Main Content Styles */
.main-content {
  flex: 1;
  margin-left: 260px; /* Offset for sidebar */
  padding: 40px;
  overflow-y: auto;
}

#app.login-layout .main-content {
  margin-left: 0;
  padding: 0;
  overflow: visible;
}
</style>
