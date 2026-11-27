import { api } from '../../api/axiosInstance'
import { ENDPOINTS } from '../../constants/endpoints'
import type { PageResponse } from '../../types/api.types'
import type {
  Customer,
  DashboardSummary,
  InventoryRecord,
  InventoryReport,
  Invoice,
  Notification,
  PurchaseOrder,
  PurchaseReport,
  SalesOrder,
  SalesReport,
  Supplier,
  Warehouse,
} from './types'

const page = <T>(url: string, pageNumber = 0, size = 10, sort = 'createdAt,desc') =>
  api.get<PageResponse<T>>(url, {
    params: { page: pageNumber, size, sort },
  }).then(({ data }) => data)

export const operationsApi = {
  dashboard: () =>
    api.get<DashboardSummary>(`${ENDPOINTS.reports}/dashboard`).then(({ data }) => data),
  salesReport: (size = 100) =>
    api.get<PageResponse<SalesReport>>(`${ENDPOINTS.reports}/sales`, {
      params: { page: 0, size, sort: 'occurredAt,desc' },
    }).then(({ data }) => data),
  inventory: (pageNumber = 0) => page<InventoryRecord>(ENDPOINTS.inventory, pageNumber, 10, 'updatedAt,desc'),
  warehouses: () => api.get<Warehouse[]>(ENDPOINTS.warehouses).then(({ data }) => data),
  createWarehouse: (input: Pick<Warehouse, 'code' | 'name' | 'address' | 'active'>) =>
    api.post<Warehouse>(ENDPOINTS.warehouses, input).then(({ data }) => data),
  adjustStock: (input: {
    productId: string
    warehouseId: string
    quantityDelta: number
    referenceId?: string
    reason: string
    reorderPoint?: number
  }) => api.post(`${ENDPOINTS.inventory}/adjustments`, input),
  suppliers: (pageNumber = 0, size = 10) => page<Supplier>(ENDPOINTS.suppliers, pageNumber, size),
  createSupplier: (input: {
    code: string
    name: string
    contactName?: string
    email?: string
    phone?: string
    address?: string
    status: 'ACTIVE' | 'INACTIVE'
    products: never[]
  }) => api.post<Supplier>(ENDPOINTS.suppliers, input).then(({ data }) => data),
  purchases: (pageNumber = 0, size = 10) => page<PurchaseOrder>(ENDPOINTS.purchases, pageNumber, size),
  createPurchase: (input: {
    supplierId: string
    warehouseId: string
    items: Array<{ productId: string; quantity: number; unitPrice: number }>
  }) => api.post<PurchaseOrder>(ENDPOINTS.purchases, input).then(({ data }) => data),
  submitPurchase: (id: string) => api.post(`${ENDPOINTS.purchases}/${id}/submit`),
  cancelPurchase: (id: string) => api.post(`${ENDPOINTS.purchases}/${id}/cancel`),
  receivePurchase: (purchaseOrderId: string, items: Array<{ purchaseOrderItemId: string; quantity: number }>) =>
    api.post(ENDPOINTS.receipts, { purchaseOrderId, items }),
  customers: (pageNumber = 0, size = 10) => page<Customer>(ENDPOINTS.customers, pageNumber, size, 'name,asc'),
  createCustomer: (input: Omit<Customer, 'id'>) =>
    api.post<Customer>(ENDPOINTS.customers, input).then(({ data }) => data),
  sales: (pageNumber = 0, size = 10) => page<SalesOrder>(ENDPOINTS.sales, pageNumber, size),
  createSale: (input: {
    customerId: string
    warehouseId: string
    items: Array<{ productId: string; quantity: number; unitPrice: number }>
  }) => api.post<SalesOrder>(ENDPOINTS.sales, input).then(({ data }) => data),
  completeSale: (id: string) => api.post(`${ENDPOINTS.sales}/${id}/complete`),
  cancelSale: (id: string) => api.post(`${ENDPOINTS.sales}/${id}/cancel`),
  invoices: (pageNumber = 0) => page<Invoice>(ENDPOINTS.invoices, pageNumber, 10, 'issuedAt,desc'),
  payInvoice: (id: string, input: { amount: number; method: string; reference?: string }) =>
    api.post(`${ENDPOINTS.invoices}/${id}/payments`, input),
  notifications: (pageNumber = 0) => page<Notification>(ENDPOINTS.notifications, pageNumber),
  retryNotification: (id: string) => api.post(`${ENDPOINTS.notifications}/${id}/retry`),
  report: <T>(name: 'sales' | 'purchases' | 'inventory', pageNumber = 0) =>
    api.get<PageResponse<T>>(`${ENDPOINTS.reports}/${name}`, {
      params: { page: pageNumber, size: 10, sort: 'occurredAt,desc' },
    }).then(({ data }) => data),
  downloadReport: async (name: 'sales' | 'purchases' | 'inventory') => {
    const { data } = await api.get<Blob>(`${ENDPOINTS.reports}/${name}/export`, {
      responseType: 'blob',
    })
    const url = URL.createObjectURL(data)
    const link = document.createElement('a')
    link.href = url
    link.download = `${name}-report.csv`
    link.click()
    URL.revokeObjectURL(url)
  },
}

export type { InventoryReport, PurchaseReport, SalesReport }
