import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import api from '../services/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('shopkart_user')
    return savedUser ? JSON.parse(savedUser) : null
  })
  const [token, setToken] = useState(() => localStorage.getItem('shopkart_token'))
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (token) {
      api.defaults.headers.common.Authorization = `Bearer ${token}`
    } else {
      delete api.defaults.headers.common.Authorization
    }
  }, [token])

  const login = async (credentials) => {
    setLoading(true)
    try {
      const { data } = await api.post('/api/auth/login', credentials)
      const payload = data.data
      localStorage.setItem('shopkart_token', payload.token)
      localStorage.setItem('shopkart_user', JSON.stringify(payload.user))
      setToken(payload.token)
      setUser(payload.user)
      return payload
    } finally {
      setLoading(false)
    }
  }

  const register = async (userData) => {
    setLoading(true)
    try {
      const { data } = await api.post('/api/auth/register', userData)
      const payload = data.data
      localStorage.setItem('shopkart_token', payload.token)
      localStorage.setItem('shopkart_user', JSON.stringify(payload.user))
      setToken(payload.token)
      setUser(payload.user)
      return payload
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    localStorage.removeItem('shopkart_token')
    localStorage.removeItem('shopkart_user')
    setToken(null)
    setUser(null)
  }

  const value = useMemo(
    () => ({ user, token, loading, login, register, logout, setUser }),
    [user, token, loading],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
