import {
  Bell,
  Boxes,
  Building2,
  ChartNoAxesCombined,
  ClipboardList,
  LayoutDashboard,
  Package,
  ReceiptText,
  ShoppingCart,
  Tags,
  Truck,
  X,
} from 'lucide-react'
import { NavLink } from 'react-router-dom'

const links = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/products', label: 'Products', icon: Package },
  { to: '/catalog', label: 'Catalog setup', icon: Tags },
  { to: '/inventory', label: 'Inventory', icon: Boxes },
  { to: '/suppliers', label: 'Suppliers', icon: Truck },
  { to: '/purchases', label: 'Purchases', icon: ClipboardList },
  { to: '/sales', label: 'Sales', icon: ShoppingCart },
  { to: '/invoices', label: 'Invoices', icon: ReceiptText },
  { to: '/reports', label: 'Reports', icon: ChartNoAxesCombined },
  { to: '/notifications', label: 'Notifications', icon: Bell },
]

export function Sidebar({
  open,
  onClose,
}: {
  open: boolean
  onClose: () => void
}) {
  return (
    <>
      {open && <button className="sidebar-overlay" onClick={onClose} aria-label="Close navigation" />}
      <aside className={`sidebar ${open ? 'sidebar--open' : ''}`}>
        <div className="sidebar__brand">
          <span className="brand-mark"><Building2 size={22} /></span>
          <div><strong>StockFlow</strong><small>Operations</small></div>
          <button className="icon-button sidebar__close" onClick={onClose} aria-label="Close menu"><X /></button>
        </div>
        <nav>
          {links.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/'}
              onClick={onClose}
              className={({ isActive }) => isActive ? 'sidebar__link sidebar__link--active' : 'sidebar__link'}
            >
              <Icon size={19} /><span>{label}</span>
            </NavLink>
          ))}
        </nav>
      </aside>
    </>
  )
}
