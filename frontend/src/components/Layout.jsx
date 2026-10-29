import { BarChart3, Boxes, CircleUserRound, LogOut, Package2, ShoppingCart, Tags, LayoutDashboard } from 'lucide-react'
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

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-wrap">
          <div className="brand-icon"><BarChart3 size={22} /></div>
          <div>
            <div className="brand-name">ShopKart</div>
            <div className="brand-subtitle">Management</div>
          </div>
        </div>

        <nav className="nav-menu">
          {navItems.filter(({ roles }) => roles.includes(user?.role)).map(({ label, to, icon: Icon }) => (
            <NavLink key={to} to={to} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <Icon size={18} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="nav-footer">
          <div className="user-chip">
            <div className="user-avatar">{user?.name?.charAt(0)?.toUpperCase() || 'A'}</div>
            <div>
              <strong>{user?.name || 'Admin'}</strong>
              <small>{user?.role || 'ADMIN'}</small>
            </div>
          </div>
          <button className="logout-btn" onClick={handleLogout}>
            <LogOut size={16} />
            Logout
          </button>
        </div>
      </aside>

      <main className="main-panel">
        <header className="topbar">
          <div>
            <p className="eyebrow">Overview</p>
            <h1>{title}</h1>
          </div>
          <div className="topbar-badge">Live dashboards</div>
        </header>
        {children}
      </main>
    </div>
  )
}
