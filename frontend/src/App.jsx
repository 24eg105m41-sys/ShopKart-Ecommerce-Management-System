import { Navigate, Route, Routes } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import ProductsPage from './pages/ProductsPage'
import CategoriesPage from './pages/CategoriesPage'
import UsersPage from './pages/UsersPage'
import InventoryPage from './pages/InventoryPage'
import OrdersPage from './pages/OrdersPage'
import { useAuth } from './context/AuthContext'

function ProtectedRoute({ children, roles }) {
  const { user } = useAuth()
  if (!user) {
    return <Navigate to="/login" replace />
  }
  if (roles && !roles.includes(user.role)) {
    return <Navigate to="/products" replace />
  }
  return children
}

export default function App() {
  const { user } = useAuth()
  const homePath = user?.role === 'ADMIN' ? '/dashboard' : '/products'

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/" element={<Navigate to={homePath} replace />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute roles={['ADMIN']}>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/products"
        element={
          <ProtectedRoute roles={['ADMIN', 'HR', 'MERCHANT']}>
            <ProductsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/categories"
        element={
          <ProtectedRoute roles={['ADMIN', 'HR', 'MERCHANT']}>
            <CategoriesPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/users"
        element={
          <ProtectedRoute roles={['ADMIN', 'HR']}>
            <UsersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/inventory"
        element={
          <ProtectedRoute roles={['ADMIN', 'HR', 'MERCHANT']}>
            <InventoryPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/orders"
        element={
          <ProtectedRoute roles={['ADMIN', 'HR', 'MERCHANT']}>
            <OrdersPage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to={homePath} replace />} />
    </Routes>
  )
}
