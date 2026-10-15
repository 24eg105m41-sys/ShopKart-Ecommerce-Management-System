import { useEffect, useState } from 'react'
import { Plus, X } from 'lucide-react'
import Layout from '../components/Layout'
import api from '../services/api'

const emptyOrder = { productId: '', quantity: '1', street: '', city: '', state: '', postalCode: '', country: 'India' }
const orderStatuses = ['PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED']

export default function OrdersPage() {
  const [orders, setOrders] = useState([])
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState(emptyOrder)
  const [formOpen, setFormOpen] = useState(false)
  const [error, setError] = useState('')

  const loadOrders = async () => {
    const { data } = await api.get('/api/orders', { params: { page: 0, size: 50 } })
    setOrders(data.data?.content || [])
  }

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      try {
        const [ordersResponse, productsResponse] = await Promise.all([
          api.get('/api/orders', { params: { page: 0, size: 50 } }),
          api.get('/api/products', { params: { page: 0, size: 100 } }),
        ])
        setOrders(ordersResponse.data.data?.content || [])
        setProducts(productsResponse.data.data?.content || [])
      } catch (err) {
        setError(err.response?.data?.message || 'Could not load orders.')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const createOrder = async (event) => {
    event.preventDefault()
    setError('')
    try {
      await api.post('/api/orders', {
        ...form,
        items: [{ productId: Number(form.productId), quantity: Number(form.quantity) }],
      })
      setForm(emptyOrder)
      setFormOpen(false)
      await loadOrders()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not create order.')
    }
  }

  const changeStatus = async (orderId, status) => {
    setError('')
    try {
      await api.patch(`/api/orders/${orderId}/status`, { status })
      await loadOrders()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not update order status.')
    }
  }

  return (
    <Layout title="Orders">
      <div className="toolbar">
        <span className="toolbar-copy">Create orders and update order status</span>
        <button className="action-btn" onClick={() => { setFormOpen(true); setError('') }}><Plus size={16} /> Add order</button>
      </div>
      {error && <div className="error-box page-error">{error}</div>}

      {formOpen && (
        <form className="management-form" onSubmit={createOrder}>
          <div className="management-form-heading"><h2>New order</h2><button type="button" className="icon-btn" aria-label="Close form" onClick={() => setFormOpen(false)}><X size={18} /></button></div>
          <div className="management-form-grid">
            <label>Product<select required value={form.productId} onChange={(e) => setForm({ ...form, productId: e.target.value })}><option value="">Choose product</option>{products.filter((product) => product.status === 'ACTIVE' && product.stock > 0).map((product) => <option key={product.id} value={product.id}>{product.name} · ₹{Number(product.finalPrice ?? product.price).toLocaleString('en-IN')} · stock {product.stock}</option>)}</select></label>
            <label>Quantity<input required type="number" min="1" step="1" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} /></label>
            <label>Street<input required value={form.street} onChange={(e) => setForm({ ...form, street: e.target.value })} /></label>
            <label>City<input required value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} /></label>
            <label>State<input required value={form.state} onChange={(e) => setForm({ ...form, state: e.target.value })} /></label>
            <label>Postal code<input required value={form.postalCode} onChange={(e) => setForm({ ...form, postalCode: e.target.value })} /></label>
            <label>Country<input required value={form.country} onChange={(e) => setForm({ ...form, country: e.target.value })} /></label>
          </div>
          <div className="form-actions"><button type="button" className="secondary-btn" onClick={() => setFormOpen(false)}>Cancel</button><button type="submit" className="action-btn">Create order</button></div>
        </form>
      )}

      {loading ? <div className="loading-box">Loading orders...</div> : (
        <div className="table-panel"><table>
          <thead><tr><th>Order #</th><th>Customer</th><th>Total</th><th>Status</th><th>Payment</th></tr></thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td>{order.orderNumber}</td><td>{order.customerName || '—'}</td>
                <td>₹{Number(order.totalAmount || 0).toLocaleString('en-IN')}</td>
                <td><select className="status-select" aria-label={`Status for ${order.orderNumber}`} value={order.status} onChange={(e) => changeStatus(order.id, e.target.value)}>{orderStatuses.map((status) => <option key={status} value={status}>{status}</option>)}</select></td>
                <td>{order.paymentStatus}</td>
              </tr>
            ))}
            {!orders.length && <tr><td colSpan="5" className="empty-cell">No orders found.</td></tr>}
          </tbody>
        </table></div>
      )}
    </Layout>
  )
}
