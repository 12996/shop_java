import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home/index.vue')
  },
  {
    path: '/category',
    name: 'Category',
    component: () => import('../views/Category/index.vue')
  },
  {
    path: '/basket',
    name: 'Cart',
    component: () => import('../views/Cart/index.vue')
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('../views/User/index.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login/index.vue')
  },
  {
    path: '/classify',
    name: 'Classify',
    component: () => import('../views/Classify/index.vue')
  },
  {
    path: '/coupon',
    name: 'Coupon',
    component: () => import('../views/Coupon/index.vue')
  },
  {
    path: '/product',
    name: 'Product',
    component: () => import('../views/Product/index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register/index.vue')
  },
  {
    path: '/submit-order',
    name: 'SubmitOrder',
    component: () => import('../views/SubmitOrder/index.vue')
  },
  {
    path: '/pay-result',
    name: 'PayResult',
    component: () => import('../views/PayResult/index.vue')
  },
  {
    path: '/my-coupon',
    name: 'MyCoupon',
    component: () => import('../views/MyCoupon/index.vue')
  },
  {
    path: '/order-list',
    name: 'OrderList',
    component: () => import('../views/OrderList/index.vue')
  },
  {
    path: '/address',
    name: 'Address',
    component: () => import('../views/Address/index.vue')
  },
  {
    path: '/edit-address',
    name: 'EditAddress',
    component: () => import('../views/EditAddress/index.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
