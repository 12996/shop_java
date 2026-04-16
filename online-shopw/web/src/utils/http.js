import axios from 'axios'
import config from './config'

const http = axios.create({
  baseURL: config.domain,
  timeout: 10000
})

// Request interceptor
http.interceptors.request.use(
  requestConfig => {
    // Add token from localStorage if exists
    const token = localStorage.getItem('token')
    if (token) {
      requestConfig.headers['Authorization'] = token
    }
    return requestConfig
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor
http.interceptors.response.use(
  response => {
    const res = response.data
    // Handle standard generic successful wrapped responses
    if (res && res.code === '00000') {
      return res.data
    }
    
    // Handle A00004 Unauthorized
    if (res && res.code === 'A00004') {
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error('未登录或登录已失效'))
    }
    
    // Reject other error codes from backend
    if (res && typeof res.code === 'string' && res.code !== '00000') {
      return Promise.reject(new Error(res.msg || '操作失败'))
    }
    
    // Return full if not matching standard format
    return res
  },
  error => {
    // Handle 401 Unauthorized globally
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default http
