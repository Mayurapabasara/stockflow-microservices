import { useState } from 'react'
import { NavLink, Route, Routes, useLocation } from 'react-router-dom'
import axios from 'axios'

const modules = [
  { label: 'Dashboard', path: '/', icon: '▦' },
  { label: 'Products', path: '/products', icon: '◫' },
  { label: 'Inventory', path: '/inventory', icon: '▤' },
  { label: 'Suppliers', path: '/suppliers', icon: '♧' },
  { label: 'Purchases', path: '/purchases', icon: '↙' },
  { label: 'Sales', path: '/sales', icon: '↗' },
  { label: 'Notifications', path: '/notifications', icon: '◉' },
  { label: 'Reports', path: '/reports', icon: '▥' },
]

const metrics = [
  { label: 'Total products', value: '1,284', change: '+12.8%', icon: '◫', color: 'blue' },
  { label: 'Stock value', value: '$48,294', change: '+8.2%', icon: '$', color: 'violet' },
  { label: 'Low stock items', value: '18', change: 'Needs attention', icon: '!', color: 'amber' },
  { label: 'Open orders', value: '36', change: '+4 this week', icon: '↗', color: 'green' },
]

const recentActivity = [
  { title: 'Purchase order received', detail: 'PO-2025-018 · Acme Supplies', time: '10 min ago', type: 'purchase' },
  { title: 'Stock level updated', detail: 'Wireless Mouse · +50 units', time: '42 min ago', type: 'stock' },
  { title: 'New sales order', detail: 'SO-2025-1042 · Northstar Co.', time: '1 hour ago', type: 'sales' },
  { title: 'Low stock alert', detail: 'USB-C Hub · 4 units remaining', time: '2 hours ago', type: 'alert' },
]

function Sidebar() {
  return (
    <aside className="sidebar">
      <a className="brand" href="/" aria-label="StockFlow home">
        <span className="brand-mark">S</span>
        <span>stockflow<span className="brand-dot">.</span></span>
      </a>
      <p className="nav-caption">WORKSPACE</p>
      <nav className="module-nav" aria-label="Main navigation">
        {modules.map((item) => (
          <NavLink key={item.path} to={item.path} end={item.path === '/'} className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <span className="nav-icon" aria-hidden="true">{item.icon}</span>
            <span>{item.label}</span>
            {item.label === 'Notifications' && <span className="notification-count">3</span>}
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-bottom">
        <div className="plan-card">
          <span className="plan-kicker">GET STARTED</span>
          <p>Your workspace is ready to grow.</p>
          <div className="plan-progress"><span /></div>
          <small>Setup progress <strong>3 of 5</strong></small>
        </div>
        <button className="help-link" type="button"><span>?</span> Help &amp; support</button>
        <div className="user-card">
          <div className="avatar">JD</div>
          <div><strong>Jamie Davis</strong><small>Workspace admin</small></div>
          <span className="user-menu">···</span>
        </div>
      </div>
    </aside>
  )
}

function Topbar({ title }) {
  return (
    <header className="topbar">
      <div className="breadcrumb">Workspace <span>/</span> <strong>{title}</strong></div>
      <div className="topbar-actions">
        <label className="search-box">
          <span aria-hidden="true">⌕</span>
          <input aria-label="Search" placeholder="Search anything..." />
          <kbd>⌘ K</kbd>
        </label>
        <button className="icon-button" aria-label="Notifications">♧<i /></button>
        <div className="top-avatar">JD</div>
      </div>
    </header>
  )
}

function Dashboard() {
  const today = new Intl.DateTimeFormat('en-US', {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date())

  return (
    <>
      <div className="page-heading">
        <div><p className="eyebrow">{today.toUpperCase()} · PREVIEW DATA</p><h1>Good morning, Jamie <span>✦</span></h1><p className="subheading">Here&apos;s what&apos;s happening with your inventory today.</p></div>
        <button className="primary-button" type="button"><span>＋</span> Add product</button>
      </div>
      <section className="metric-grid" aria-label="Business overview">
        {metrics.map((metric) => (
          <article className="metric-card" key={metric.label}>
            <div className="metric-top"><span>{metric.label}</span><span className={`metric-icon ${metric.color}`}>{metric.icon}</span></div>
            <div className="metric-value">{metric.value}</div>
            <div className={`metric-change ${metric.color === 'amber' ? 'neutral' : ''}`}>{metric.color === 'blue' || metric.color === 'violet' ? '↗ ' : ''}{metric.change}</div>
          </article>
        ))}
      </section>
      <section className="dashboard-grid">
        <article className="panel inventory-panel">
          <div className="panel-heading"><div><h2>Inventory overview</h2><p>Stock levels across your catalog</p></div><button className="select-button" type="button">Last 7 days⌄</button></div>
          <div className="chart-legend"><span><i className="legend-blue" /> In stock</span><span><i className="legend-pale" /> Low stock</span></div>
          <div className="chart" role="img" aria-label="Inventory levels are generally steady over the last seven days">
            <div className="chart-y-labels"><span>1,500</span><span>1,000</span><span>500</span><span>0</span></div>
            <div className="chart-plot">
              <div className="chart-grid"><i /><i /><i /><i /></div>
              <svg viewBox="0 0 640 180" preserveAspectRatio="none" aria-hidden="true">
                <defs><linearGradient id="fill" x1="0" x2="0" y1="0" y2="1"><stop offset="0%" stopColor="#246bfe" stopOpacity=".17" /><stop offset="100%" stopColor="#246bfe" stopOpacity="0" /></linearGradient></defs>
                <path d="M0 105 C35 98 48 109 80 91 S129 96 160 77 S205 83 240 72 S287 81 320 60 S366 68 400 56 S448 66 480 43 S528 58 560 34 S607 43 640 24 L640 180 L0 180Z" fill="url(#fill)" />
                <path d="M0 105 C35 98 48 109 80 91 S129 96 160 77 S205 83 240 72 S287 81 320 60 S366 68 400 56 S448 66 480 43 S528 58 560 34 S607 43 640 24" fill="none" stroke="#246bfe" strokeWidth="3" vectorEffect="non-scaling-stroke" />
              </svg>
              <div className="chart-x-labels"><span>May 13</span><span>May 14</span><span>May 15</span><span>May 16</span><span>May 17</span><span>May 18</span><span>May 19</span></div>
            </div>
          </div>
        </article>
        <article className="panel activity-panel">
          <div className="panel-heading"><div><h2>Recent activity</h2><p>Latest updates from your workspace</p></div><button className="more-button" aria-label="More activity options" type="button">···</button></div>
          <div className="activity-list">
            {recentActivity.map((activity) => (
              <div className="activity-item" key={activity.title}>
                <div className={`activity-icon ${activity.type}`}>{activity.type === 'purchase' ? '↙' : activity.type === 'stock' ? '▤' : activity.type === 'sales' ? '↗' : '!'}</div>
                <div className="activity-copy"><strong>{activity.title}</strong><span>{activity.detail}</span></div>
                <time>{activity.time}</time>
              </div>
            ))}
          </div>
          <a className="activity-footer" href="/reports">View all activity <span>→</span></a>
        </article>
      </section>
      <section className="panel products-panel">
        <div className="panel-heading"><div><h2>Top products</h2><p>Your best-performing products this month</p></div><button className="text-button" type="button">View all products <span>→</span></button></div>
        <div className="product-placeholder"><div className="placeholder-box">◫</div><div><strong>Connect your Product Service</strong><p>Live product data will appear here when the API is available.</p></div></div>
      </section>
    </>
  )
}

function ModulePage({ title }) {
  const [status, setStatus] = useState('Product Service is ready to check.')

  async function checkProductApi() {
    setStatus('Checking Product Service…')
    try {
      const { data } = await axios.get('/api/products')
      setStatus(`${data.length} products returned by Product Service`)
    } catch {
      setStatus('Product Service is not connected yet. Start the backend to load live data.')
    }
  }

  return (
    <section className="panel module-placeholder">
      <div className="placeholder-box">{modules.find((item) => item.label === title)?.icon}</div>
      <p className="eyebrow">STOCKFLOW MODULE</p>
      <h1>{title}</h1>
      {title === 'Products' ? (
        <>
          <p>The Product Service API is scaffolded. Connect the dashboard to manage your catalog.</p>
          <button className="primary-button" type="button" onClick={checkProductApi}>Check product API</button>
          <p className="api-status" role="status">{status}</p>
        </>
      ) : <p>This module is a placeholder for a future StockFlow service.</p>}
    </section>
  )
}

function App() {
  const location = useLocation()
  const active = modules.find((item) => item.path === location.pathname)
  const title = active?.label ?? 'Dashboard'

  return (
    <div className="app-shell">
      <Sidebar />
      <main className="main-area">
        <Topbar title={title} />
        <div className="page-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            {modules.filter((item) => item.path !== '/').map((item) => (
              <Route key={item.path} path={item.path} element={<ModulePage title={item.label} />} />
            ))}
          </Routes>
        </div>
      </main>
    </div>
  )
}

export default App
