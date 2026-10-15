
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Attach the ShopKart authentication token to requests.
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('shopkart_token')

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => Promise.reject(error),
)

// Handle authentication errors.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('shopkart_token')
      localStorage.removeItem('shopkart_user')

      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    return Promise.reject(error)
  },
)

export default api
