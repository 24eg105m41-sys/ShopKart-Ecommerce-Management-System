import { useState } from 'react'
import {
  BarChart3,
  Boxes,
  CircleUserRound,
  LogOut,
  Menu,
  Package2,
  ShoppingCart,
  Tags,
  LayoutDashboard,
  X,
  Store,
} from 'lucide-react'
import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const navItems = [
  { label: 'Dashboard', to: '/dashboard', icon: LayoutDashboard, roles: ['ADMIN'] },
  { label: 'Products', to: '/products', icon: Package2, roles: ['ADMIN', 'HR', 'MERCHANT'] },
  { label: 'Categories', to: '/categories', icon: Tags, roles: ['ADMIN', 'HR', 'MERCHANT'] },
  { label: 'Orders', to: '/orders', icon: ShoppingCart, roles: ['ADMIN', 'HR', 'MERCHANT'] },
  { label: 'Inventory', to: '/inventory', icon: Boxes, roles: ['ADMIN', 'HR', 'MERCHANT'] },
  { label: 'Users', to: '/users', icon: CircleUserRound, roles: ['ADMIN', 'HR'] },
]

export default function Layout({ children, title }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [mobileNavOpen, setMobileNavOpen] = useState(false)

  const handleLogout = () => {
    setMobileNavOpen(false)
    logout()
    navigate('/login')
  }

  const visibleNavItems = navItems.filter(({ roles }) => roles.includes(user?.role))

  return (
    <div className="app-shell">
      {mobileNavOpen && (
        <button
          className="sidebar-backdrop"
          type="button"
          aria-label="Close navigation menu"
          onClick={() => setMobileNavOpen(false)}
        />
      )}

      <aside className={`sidebar ${mobileNavOpen ? 'sidebar-open' : ''}`}>
        <div>
          <div className="brand-wrap">
            <div className="brand-icon"><BarChart3 size={22} /></div>
            <div className="brand-copy">
              <div className="brand-name">ShopKart</div>
              <div className="brand-subtitle">E-Commerce Management</div>
            </div>
            <button
              className="mobile-close-btn"
              type="button"
              aria-label="Close navigation menu"
              onClick={() => setMobileNavOpen(false)}
            >
              <X size={19} />
            </button>
          </div>

          <div className="sidebar-section-label">WORKSPACE</div>
          <nav className="nav-menu" aria-label="Main navigation">
            {visibleNavItems.map(({ label, to, icon: Icon }) => (
              <NavLink
                key={to}
                to={to}
                onClick={() => setMobileNavOpen(false)}
                className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              >
                <Icon size={18} />
                <span>{label}</span>
              </NavLink>
            ))}
          </nav>
        </div>

        <div className="nav-footer">
          <div className="sidebar-help-card">
            <div className="sidebar-help-icon"><Store size={17} /></div>
            <div>
              <strong>ShopKart Admin</strong>
              <span>Manage your store</span>
            </div>
          </div>
          <div className="user-chip">
            <div className="user-avatar">{user?.name?.charAt(0)?.toUpperCase() || 'A'}</div>
            <div className="user-details">
              <strong>{user?.name || 'Admin'}</strong>
              <small>{user?.role || 'ADMIN'}</small>
            </div>
          </div>
          <button className="logout-btn" onClick={handleLogout} type="button">
            <LogOut size={16} />
            <span>Logout</span>
          </button>
        </div>
      </aside>

      <main className="main-panel">
        <header className="topbar">
          <div className="topbar-heading">
            <button
              className="mobile-menu-btn"
              type="button"
              aria-label="Open navigation menu"
              aria-expanded={mobileNavOpen}
              onClick={() => setMobileNavOpen(true)}
            >
              <Menu size={20} />
            </button>
            <div>
              <p className="eyebrow">SHOPKART / WORKSPACE</p>
              <h1>{title}</h1>
              <p className="topbar-description">Manage your store operations in one place.</p>
            </div>
          </div>
          <div className="topbar-right">
            <div className="topbar-status"><span className="status-dot" /> System online</div>
            <div className="topbar-user-avatar" title={user?.name || 'Admin'}>
              {user?.name?.charAt(0)?.toUpperCase() || 'A'}
            </div>
          </div>
        </header>
        <div className="page-content">{children}</div>
        <footer className="app-footer">
          <span>© {new Date().getFullYear()} ShopKart</span>
          <span>E-Commerce Management System</span>
        </footer>
      </main>
    </div>
  )
}
