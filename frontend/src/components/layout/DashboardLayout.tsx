import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import { Footer } from './Footer'
import { Navbar } from './Navbar'
import { Sidebar } from './Sidebar'

export function DashboardLayout() {
  const [menuOpen, setMenuOpen] = useState(false)
  return (
    <div className="app-shell">
      <Sidebar open={menuOpen} onClose={() => setMenuOpen(false)} />
      <div className="app-shell__content">
        <Navbar onMenu={() => setMenuOpen(true)} />
        <main className="page"><Outlet /></main>
        <Footer />
      </div>
    </div>
  )
}
