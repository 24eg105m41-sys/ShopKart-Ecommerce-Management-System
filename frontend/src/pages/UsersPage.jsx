import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import api from '../services/api'

export default function UsersPage() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        const { data } = await api.get('/api/users', { params: { page: 0, size: 50 } })
        setUsers(data.data?.content || [])
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [])

  return (
    <Layout title="Users">
      {loading ? (
        <div className="loading-box">Loading users...</div>
      ) : (
        <div className="table-panel">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                  <td>{user.phone}</td>
                  <td><span className="badge neutral">{user.role}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </Layout>
  )
}
