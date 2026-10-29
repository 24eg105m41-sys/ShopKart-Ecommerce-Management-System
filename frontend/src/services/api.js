import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 15000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('shopkart_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('shopkart_token')
      localStorage.removeItem('shopkart_user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)

export default api
