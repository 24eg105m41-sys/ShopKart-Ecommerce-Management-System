import axios from 'axios'

const configuredApiUrl =
  import.meta.env.VITE_API_URL ||
  (import.meta.env.DEV
    ? 'http://localhost:8080'
    : 'https://shopkart-ecommerce-management-system-1.onrender.com/api')

const baseURL = configuredApiUrl.replace(/\/+$/, '').replace(/\/api$/i, '')

const api = axios.create({
  baseURL,
  timeout: 15000,
})

api.interceptors.request.use((config) => {
  const isAuthEndpoint =
    config.url &&
    (config.url.endsWith('/api/auth/login') ||
      config.url.endsWith('/api/auth/register'))

  if (!isAuthEndpoint) {
    const token = localStorage.getItem('shopkart_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
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
