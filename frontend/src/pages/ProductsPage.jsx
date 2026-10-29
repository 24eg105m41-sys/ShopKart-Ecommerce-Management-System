import { useEffect, useState } from 'react'
import { Pencil, Plus, Trash2, X } from 'lucide-react'
import Layout from '../components/Layout'
import { useAuth } from '../context/AuthContext'
import api from '../services/api'

const emptyProduct = {
  name: '', description: '', sku: '', price: '', discount: '0', imageUrl: '',
  categoryId: '', stock: '0', status: 'ACTIVE',
}

export default function ProductsPage() {
  const { user } = useAuth()
  const canManageProducts = ['ADMIN', 'MERCHANT'].includes(user?.role)
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const [form, setForm] = useState(emptyProduct)
  const [editingId, setEditingId] = useState(null)
  const [formOpen, setFormOpen] = useState(false)
  const [error, setError] = useState('')

  const loadProducts = async () => {
    const { data } = await api.get('/api/products', {
      params: { search: search || undefined, page, size: 50 },
    })
    setProducts(data.data?.content || [])
  }

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      try {
        const [productsResponse, categoriesResponse] = await Promise.all([
          api.get('/api/products', { params: { search: search || undefined, page, size: 50 } }),
          api.get('/api/categories', { params: { page: 0, size: 100 } }),
        ])
        setProducts(productsResponse.data.data?.content || [])
        setCategories(categoriesResponse.data.data?.content || [])
      } catch (err) {
        setError(err.response?.data?.message || 'Could not load products.')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [search, page])

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    const payload = {
      ...form,
      price: Number(form.price),
      discount: Number(form.discount || 0),
      categoryId: Number(form.categoryId),
      stock: Number(form.stock || 0),
    }
    try {
      if (editingId) await api.put(`/api/products/${editingId}`, payload)
      else await api.post('/api/products', payload)
      setForm(emptyProduct)
      setEditingId(null)
      setFormOpen(false)
      await loadProducts()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not save product.')
    }
  }

  const startEdit = (product) => {
    setForm({
      name: product.name || '', description: product.description || '', sku: product.sku || '',
      price: String(product.price ?? ''), discount: String(product.discount ?? 0),
      imageUrl: product.imageUrl || '', categoryId: String(product.categoryId ?? ''),
      stock: String(product.stock ?? 0), status: product.status || 'ACTIVE',
    })
    setEditingId(product.id)
    setFormOpen(true)
    setError('')
  }

  const deleteProduct = async (product) => {
    if (!window.confirm(`Delete ${product.name}?`)) return
    try {
      await api.delete(`/api/products/${product.id}`)
      await loadProducts()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not delete product.')
    }
  }

  const closeForm = () => {
    setForm(emptyProduct)
    setEditingId(null)
    setFormOpen(false)
    setError('')
  }

  return (
    <Layout title="Products">
      <div className="toolbar">
        <input value={search} onChange={(e) => { setPage(0); setSearch(e.target.value) }} placeholder="Search products..." />
        {canManageProducts && (
          <button className="action-btn" onClick={() => { closeForm(); setFormOpen(true) }}>
            <Plus size={16} /> Add product
          </button>
        )}
      </div>

      {error && <div className="error-box page-error">{error}</div>}

      {formOpen && canManageProducts && (
        <form className="management-form" onSubmit={handleSubmit}>
          <div className="management-form-heading">
            <h2>{editingId ? 'Edit product' : 'New product'}</h2>
            <button type="button" className="icon-btn" aria-label="Close form" onClick={closeForm}><X size={18} /></button>
          </div>
          <div className="management-form-grid">
            <label>Product name<input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label>
            <label>SKU<input required value={form.sku} onChange={(e) => setForm({ ...form, sku: e.target.value })} /></label>
            <label>Category<select required value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })}><option value="">Choose category</option>{categories.map((category) => <option key={category.id} value={category.id}>{category.name}</option>)}</select></label>
            <label>Price<input required type="number" min="0.01" step="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} /></label>
            <label>Discount<input type="number" min="0" step="0.01" value={form.discount} onChange={(e) => setForm({ ...form, discount: e.target.value })} /></label>
            <label>Stock<input type="number" min="0" step="1" value={form.stock} onChange={(e) => setForm({ ...form, stock: e.target.value })} /></label>
            <label>Status<select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}><option value="ACTIVE">Active</option><option value="INACTIVE">Inactive</option></select></label>
            <label>Image URL<input type="url" value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} /></label>
            <label className="wide-field">Description<textarea rows="2" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></label>
          </div>
          <div className="form-actions"><button type="button" className="secondary-btn" onClick={closeForm}>Cancel</button><button type="submit" className="action-btn">{editingId ? 'Save changes' : 'Create product'}</button></div>
        </form>
      )}

      {loading ? (
        <div className="loading-box">Loading products...</div>
      ) : (
        <div className="table-panel">
          <table>
            <thead><tr><th>Product</th><th>Category</th><th>Price</th><th>Stock</th><th>Status</th>{canManageProducts && <th>Actions</th>}</tr></thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td><div className="product-cell"><img src={product.imageUrl || 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=200&q=80'} alt={product.name} /><div><strong>{product.name}</strong><small>{product.sku}</small></div></div></td>
                  <td>{product.categoryName || '—'}</td>
                  <td>₹{Number(product.price || 0).toLocaleString('en-IN')}</td>
                  <td>{product.stock ?? 0}</td>
                  <td><span className={`badge ${product.status === 'ACTIVE' ? 'success' : 'neutral'}`}>{product.status || 'ACTIVE'}</span></td>
                  {canManageProducts && <td><div className="row-actions"><button className="icon-btn" aria-label={`Edit ${product.name}`} onClick={() => startEdit(product)}><Pencil size={16} /></button><button className="icon-btn danger-icon" aria-label={`Delete ${product.name}`} onClick={() => deleteProduct(product)}><Trash2 size={16} /></button></div></td>}
                </tr>
              ))}
              {!products.length && <tr><td colSpan={canManageProducts ? 6 : 5} className="empty-cell">No products found.</td></tr>}
            </tbody>
          </table>
        </div>
      )}
    </Layout>
  )
}
