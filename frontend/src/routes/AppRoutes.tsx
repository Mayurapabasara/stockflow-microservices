import { lazy, Suspense } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { DashboardLayout } from '../components/layout/DashboardLayout'
import { LoadingSpinner } from '../components/common/LoadingSpinner'
import { ProtectedRoute } from './ProtectedRoute'
import { RoleBasedRoute } from './RoleBasedRoute'

const DashboardPage = lazy(() => import('../features/dashboard/DashboardPage').then((module) => ({ default: module.DashboardPage })))
const LoginPage = lazy(() => import('../features/auth/pages/LoginPage').then((module) => ({ default: module.LoginPage })))
const RegisterPage = lazy(() => import('../features/auth/pages/RegisterPage').then((module) => ({ default: module.RegisterPage })))
const AddProductPage = lazy(() => import('../features/products/pages/AddProductPage').then((module) => ({ default: module.AddProductPage })))
const CatalogPage = lazy(() => import('../features/products/pages/CatalogPage').then((module) => ({ default: module.CatalogPage })))
const EditProductPage = lazy(() => import('../features/products/pages/EditProductPage').then((module) => ({ default: module.EditProductPage })))
const ProductListPage = lazy(() => import('../features/products/pages/ProductListPage').then((module) => ({ default: module.ProductListPage })))
const InventoryPage = lazy(() => import('../features/operations/pages/InventoryPage').then((module) => ({ default: module.InventoryPage })))
const InvoicesPage = lazy(() => import('../features/operations/pages/InvoicesPage').then((module) => ({ default: module.InvoicesPage })))
const NotificationsPage = lazy(() => import('../features/operations/pages/NotificationsPage').then((module) => ({ default: module.NotificationsPage })))
const PurchasesPage = lazy(() => import('../features/operations/pages/PurchasesPage').then((module) => ({ default: module.PurchasesPage })))
const ReportsPage = lazy(() => import('../features/operations/pages/ReportsPage').then((module) => ({ default: module.ReportsPage })))
const SalesPage = lazy(() => import('../features/operations/pages/SalesPage').then((module) => ({ default: module.SalesPage })))
const SuppliersPage = lazy(() => import('../features/operations/pages/SuppliersPage').then((module) => ({ default: module.SuppliersPage })))

export function AppRoutes() {
  return (
    <Suspense fallback={<LoadingSpinner label="Loading StockFlow…" />}>
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
    </Suspense>
  )
}
