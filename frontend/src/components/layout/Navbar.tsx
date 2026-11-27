import { LogOut, Menu, ShieldCheck } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'
import { Button } from '../common/Button'

export function Navbar({ onMenu }: { onMenu: () => void }) {
  const { user, isAdmin, logout } = useAuth()
  return (
    <header className="navbar">
      <button className="icon-button navbar__menu" onClick={onMenu} aria-label="Open menu"><Menu /></button>
      <div className="navbar__title">
        <span className="eyebrow">Inventory management</span>
        <strong>StockFlow workspace</strong>
      </div>
      <div className="navbar__user">
        <span className="avatar">{user?.username.slice(0, 2).toUpperCase()}</span>
        <div><strong>{user?.username}</strong><small>{isAdmin ? 'Administrator' : 'User'}</small></div>
        {isAdmin && <ShieldCheck size={18} className="success" aria-label="Administrator" />}
        <Button variant="ghost" icon={<LogOut size={17} />} onClick={() => void logout()}>Sign out</Button>
      </div>
    </header>
  )
}
