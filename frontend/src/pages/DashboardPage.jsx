import { useEffect, useState } from 'react'
import { Area, AreaChart, Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { ArrowUpRight, DollarSign, Package, ShoppingCart, Users } from 'lucide-react'
import Layout from '../components/Layout'
import api from '../services/api'

export default function DashboardPage() {
  const [summary, setSummary] = useState(null)
  const [sales, setSales] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        const [summaryRes, salesRes] = await Promise.all([
          api.get('/api/admin/dashboard/summary'),
          api.get('/api/admin/dashboard/sales-overview'),
        ])
        setSummary(summaryRes.data.data)
        setSales(salesRes.data.data || [])
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [])

  const cards = summary
    ? [
        { label: 'Total Revenue', value: `₹${Number(summary.totalRevenue || 0).toLocaleString('en-IN')}`, icon: DollarSign, delta: '+12.4%' },
        { label: 'Orders', value: summary.totalOrders, icon: ShoppingCart, delta: '+8.2%' },
        { label: 'Products', value: summary.totalProducts, icon: Package, delta: '+5.1%' },
        { label: 'Customers', value: summary.totalCustomers, icon: Users, delta: '+18.7%' },
      ]
    : []

  return (
    <Layout title="Dashboard">
      {loading ? (
        <div className="loading-box">Loading dashboard...</div>
      ) : (
        <>
          <section className="stats-grid">
            {cards.map(({ label, value, icon: Icon, delta }) => (
              <div key={label} className="stat-card">
                <div className="stat-header">
                  <div className="stat-icon"><Icon size={20} /></div>
                  <span className="stat-trend"><ArrowUpRight size={14} /> {delta}</span>
                </div>
                <h3>{value}</h3>
                <p>{label}</p>
              </div>
            ))}
          </section>

          <section className="chart-grid">
            <div className="panel">
              <div className="panel-header">
                <h2>Sales overview</h2>
              </div>
              <div className="chart-wrap">
                <ResponsiveContainer width="100%" height={260}>
                  <AreaChart data={sales}>
                    <defs>
                      <linearGradient id="salesFill" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#4f46e5" stopOpacity={0.6} />
                        <stop offset="95%" stopColor="#4f46e5" stopOpacity={0.08} />
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip formatter={(value) => [`₹${Number(value).toLocaleString('en-IN')}`, 'Sales']} />
                    <Area type="monotone" dataKey="sales" stroke="#4f46e5" fill="url(#salesFill)" strokeWidth={3} />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>

            <div className="panel">
              <div className="panel-header">
                <h2>Order flow</h2>
              </div>
              <div className="chart-wrap">
                <ResponsiveContainer width="100%" height={260}>
                  <BarChart data={sales}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip formatter={(value) => [value, 'Orders']} />
                    <Bar dataKey="orders" fill="#14b8a6" radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
          </section>

          <section className="summary-grid">
            <div className="panel">
              <div className="panel-header"><h2>Order status</h2></div>
              <ul className="status-list">
                <li><span>Pending</span><strong>{summary?.pendingOrders ?? 0}</strong></li>
                <li><span>Delivered</span><strong>{summary?.deliveredOrders ?? 0}</strong></li>
                <li><span>Cancelled</span><strong>{summary?.cancelledOrders ?? 0}</strong></li>
                <li><span>Low stock</span><strong>{summary?.lowStockProducts ?? 0}</strong></li>
              </ul>
            </div>
            <div className="panel">
              <div className="panel-header"><h2>Quick stats</h2></div>
              <ul className="status-list">
                <li><span>Actual customers</span><strong>{summary?.totalCustomers ?? 0}</strong></li>
                <li><span>Products in stock</span><strong>{summary?.totalProducts ?? 0}</strong></li>
                <li><span>Revenue collected</span><strong>₹{Number(summary?.totalRevenue || 0).toLocaleString('en-IN')}</strong></li>
                <li><span>Total users</span><strong>{summary?.totalUsers ?? 0}</strong></li>
              </ul>
            </div>
          </section>
        </>
      )}
    </Layout>
  )
}
