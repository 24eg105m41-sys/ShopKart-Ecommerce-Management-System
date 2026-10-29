import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const initialForm = {
  name: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
}

export default function RegisterPage() {
  const navigate = useNavigate()
  const { register } = useAuth()
  const [form, setForm] = useState(initialForm)
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }))
    setError('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match.')
      return
    }
    setLoading(true)
    setError('')

    try {
      const payload = await register(form)
      navigate(payload.user?.role === 'ADMIN' ? '/dashboard' : '/products')
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <div className="auth-badge">ShopKart</div>
        <h1>Create account</h1>
        <p>Sign up to manage your storefront and orders.</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <label>
            <span>Full Name</span>
            <input name="name" type="text" value={form.name} onChange={handleChange} required />
          </label>

          <label>
            <span>Email</span>
            <input name="email" type="email" value={form.email} onChange={handleChange} required />
          </label>

          <label>
            <span>Phone</span>
            <input name="phone" type="tel" value={form.phone} onChange={handleChange} required />
          </label>

          <label>
            <span>Password</span>
            <div className="password-field">
              <input
                name="password"
                type={showPassword ? 'text' : 'password'}
                minLength={6}
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

          <label>
            <span>Confirm Password</span>
            <input
              name="confirmPassword"
              type={showPassword ? 'text' : 'password'}
              value={form.confirmPassword}
              onChange={handleChange}
              required
            />
          </label>

          {error && <div className="error-box">{error}</div>}

          <button type="submit" disabled={loading}>
            {loading ? 'Creating account...' : 'Create account'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Already have an account?{' '}
            <Link to="/login" className="auth-link">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
