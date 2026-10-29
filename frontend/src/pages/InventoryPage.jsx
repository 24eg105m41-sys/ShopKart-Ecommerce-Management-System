import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import api from '../services/api'

export default function InventoryPage() {
  const [inventory, setInventory] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const { data } = await api.get('/api/inventory')
        setInventory(data.data || [])
      } catch (err) {
        setError(err.response?.data?.message || 'Could not load inventory.')
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [])

  return (
    <Layout title="Inventory">
      {loading ? (
        <div className="loading-box">Loading inventory...</div>
      ) : (
        <>
        {error && <div className="error-box page-error">{error}</div>}
        <div className="table-panel">
          <table>
            <thead>
              <tr>
                <th>Product</th>
                <th>Current stock</th>
                <th>Available</th>
                <th>Reserved</th>
                <th>Threshold</th>
              </tr>
            </thead>
            <tbody>
              {inventory.map((item) => (
                <tr key={item.id}>
                  <td>{item.product?.name || '—'}</td>
                  <td>{item.currentStock}</td>
                  <td>{item.availableStock}</td>
                  <td>{item.reservedStock}</td>
                  <td>{item.lowStockThreshold}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        </>
      )}
    </Layout>
  )
}
