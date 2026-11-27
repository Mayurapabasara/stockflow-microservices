import { Navigate, Route, Routes } from 'react-router-dom'
import { DashboardLayout } from '../components/layout/DashboardLayout'
import { DashboardPage } from '../features/dashboard/DashboardPage'
import { LoginPage } from '../features/auth/pages/LoginPage'
import { RegisterPage } from '../features/auth/pages/RegisterPage'
import { AddProductPage } from '../features/products/pages/AddProductPage'
import { CatalogPage } from '../features/products/pages/CatalogPage'
import { EditProductPage } from '../features/products/pages/EditProductPage'
import { ProductListPage } from '../features/products/pages/ProductListPage'
import { InventoryPage } from '../features/operations/pages/InventoryPage'
import { InvoicesPage } from '../features/operations/pages/InvoicesPage'
import { NotificationsPage } from '../features/operations/pages/NotificationsPage'
import { PurchasesPage } from '../features/operations/pages/PurchasesPage'
import { ReportsPage } from '../features/operations/pages/ReportsPage'
import { SalesPage } from '../features/operations/pages/SalesPage'
import { SuppliersPage } from '../features/operations/pages/SuppliersPage'
import { ProtectedRoute } from './ProtectedRoute'
import { RoleBasedRoute } from './RoleBasedRoute'

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<DashboardLayout />}>
          <Route index element={<DashboardPage />} />
          <Route path="products" element={<ProductListPage />} />
          <Route path="products/new" element={<RoleBasedRoute roles={['ADMIN']}><AddProductPage /></RoleBasedRoute>} />
          <Route path="products/:id/edit" element={<RoleBasedRoute roles={['ADMIN']}><EditProductPage /></RoleBasedRoute>} />
          <Route path="catalog" element={<CatalogPage />} />
          <Route path="inventory" element={<InventoryPage />} />
          <Route path="suppliers" element={<SuppliersPage />} />
          <Route path="purchases" element={<PurchasesPage />} />
          <Route path="sales" element={<SalesPage />} />
          <Route path="invoices" element={<InvoicesPage />} />
          <Route path="reports" element={<ReportsPage />} />
          <Route path="notifications" element={<RoleBasedRoute roles={['ADMIN']}><NotificationsPage /></RoleBasedRoute>} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
