import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [form, setForm] = useState({ email: 'admin@shopkart.com', password: 'Admin@123' })
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }))
    setError('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const payload = await login(form)
      navigate(payload.user?.role === 'ADMIN' ? '/dashboard' : '/products')
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <div className="auth-badge">ShopKart</div>
        <h1>Welcome back</h1>
        <p>Sign in to continue to your marketplace control center.</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <label>
            <span>Email</span>
            <input name="email" type="email" value={form.email} onChange={handleChange} required />
          </label>
          <label>
            <span>Password</span>
            <div className="password-field">
              <input
                name="password"
                type={showPassword ? 'text' : 'password'}
                value={form.password}
                onChange={handleChange}
                required
              />
              <label className="toggle-password">
                <input
                  type="checkbox"
                  checked={showPassword}
                  onChange={(e) => setShowPassword(e.target.checked)}
                />
                Show
              </label>
            </div>
          </label>
          {error && <div className="error-box">{error}</div>}
          <button type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign in'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            New here?{' '}
            <Link to="/register" className="auth-link">Create an account</Link>
          </p>
          <div className="demo-credentials">
            <strong>Demo credentials</strong>
            <span>Admin: admin@shopkart.com / Admin@123</span>
            <span>HR: hr@shopkart.com / Hr@123</span>
          </div>
        </div>
      </div>
    </div>
  )
}
