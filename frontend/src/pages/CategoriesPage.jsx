import { useEffect, useState } from 'react'
import { Pencil, Plus, Trash2, X } from 'lucide-react'
import Layout from '../components/Layout'
import api from '../services/api'

const emptyCategory = { name: '', description: '', status: 'ACTIVE' }

export default function CategoriesPage() {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState(emptyCategory)
  const [editingId, setEditingId] = useState(null)
  const [formOpen, setFormOpen] = useState(false)
  const [error, setError] = useState('')

  const loadCategories = async () => {
    const { data } = await api.get('/api/categories', { params: { page: 0, size: 100 } })
    setCategories(data.data?.content || [])
  }

  useEffect(() => {
    loadCategories().catch((err) => setError(err.response?.data?.message || 'Could not load categories.')).finally(() => setLoading(false))
  }, [])

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    try {
      if (editingId) await api.put(`/api/categories/${editingId}`, form)
      else await api.post('/api/categories', form)
      setForm(emptyCategory)
      setEditingId(null)
      setFormOpen(false)
      await loadCategories()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not save category.')
    }
  }

  const startEdit = (category) => {
    setForm({ name: category.name || '', description: category.description || '', status: category.status || 'ACTIVE' })
    setEditingId(category.id)
    setFormOpen(true)
    setError('')
  }

  const deleteCategory = async (category) => {
    if (!window.confirm(`Delete ${category.name}?`)) return
    try {
      await api.delete(`/api/categories/${category.id}`)
      await loadCategories()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not delete category. It may still contain products.')
    }
  }

  const closeForm = () => {
    setForm(emptyCategory)
    setEditingId(null)
    setFormOpen(false)
  }

  return (
    <Layout title="Categories">
      <div className="toolbar">
        <span className="toolbar-copy">Manage product categories</span>
        <button className="action-btn" onClick={() => { closeForm(); setFormOpen(true) }}><Plus size={16} /> Add category</button>
      </div>
      {error && <div className="error-box page-error">{error}</div>}

      {formOpen && (
        <form className="management-form" onSubmit={handleSubmit}>
          <div className="management-form-heading"><h2>{editingId ? 'Edit category' : 'New category'}</h2><button type="button" className="icon-btn" aria-label="Close form" onClick={closeForm}><X size={18} /></button></div>
          <div className="management-form-grid">
            <label>Category name<input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label>
            <label>Status<select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}><option value="ACTIVE">Active</option><option value="INACTIVE">Inactive</option></select></label>
            <label className="wide-field">Description<textarea rows="2" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></label>
          </div>
          <div className="form-actions"><button type="button" className="secondary-btn" onClick={closeForm}>Cancel</button><button type="submit" className="action-btn">{editingId ? 'Save changes' : 'Create category'}</button></div>
        </form>
      )}

      {loading ? <div className="loading-box">Loading categories...</div> : (
        <div className="table-panel"><table>
          <thead><tr><th>Name</th><th>Description</th><th>Status</th><th>Actions</th></tr></thead>
          <tbody>
            {categories.map((category) => (
              <tr key={category.id}>
                <td>{category.name}</td><td>{category.description || '—'}</td>
                <td><span className={`badge ${category.status === 'ACTIVE' ? 'success' : 'neutral'}`}>{category.status || 'ACTIVE'}</span></td>
                <td><div className="row-actions"><button className="icon-btn" aria-label={`Edit ${category.name}`} onClick={() => startEdit(category)}><Pencil size={16} /></button><button className="icon-btn danger-icon" aria-label={`Delete ${category.name}`} onClick={() => deleteCategory(category)}><Trash2 size={16} /></button></div></td>
              </tr>
            ))}
            {!categories.length && <tr><td colSpan="4" className="empty-cell">No categories found.</td></tr>}
          </tbody>
        </table></div>
      )}
    </Layout>
  )
}
