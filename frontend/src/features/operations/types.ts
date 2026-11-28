export interface DashboardSummary {
  from?: string
  to?: string
  saleLines: number
  unitsSold: number
  receiptLines: number
  unitsReceived: number
  inventoryRecords: number
  quantityOnHand: number
  quantityReserved: number
  quantityAvailable: number
}

export interface InventoryRecord {
  productId: string
  warehouseId: string
  warehouseCode: string
  warehouseName: string
  quantity: number
  reservedQuantity: number
  availableQuantity: number
  reorderPoint: number
  updatedAt: string
  version: number
}

export interface Warehouse {
  id: string
  code: string
  name: string
  address?: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface Supplier {
  id: string
  code: string
  name: string
  contactName?: string
  email?: string
  phone?: string
  address?: string
  status: 'ACTIVE' | 'INACTIVE'
  products: Array<{
    id: string
    productId: string
    supplierSku?: string
    unitPrice: number
    leadTimeDays: number
    active: boolean
  }>
  createdAt: string
  updatedAt: string
}

export interface PurchaseOrder {
  id: string
  supplierId: string
  warehouseId: string
  status: 'DRAFT' | 'SUBMITTED' | 'PARTIALLY_RECEIVED' | 'RECEIVED' | 'CANCELLED'
  items: Array<{
    id: string
    productId: string
    quantity: number
    receivedQuantity: number
    unitPrice: number
  }>
  total: number
  createdAt: string
}

export interface Customer {
  id: string
  name: string
  email?: string
  phone?: string
  address?: string
  active: boolean
}

export interface SalesOrder {
  id: string
  customerId: string
  warehouseId: string
  status: 'DRAFT' | 'COMPLETED' | 'CANCELLED'
  items: Array<{ id: string; productId: string; quantity: number; unitPrice: number }>
  total: number
  createdAt: string
}

export interface Invoice {
  id: string
  salesOrderId: string
  total: number
  paidAmount: number
  balance: number
  payments: Array<{
    id: string
    amount: number
    method: 'CASH' | 'CARD' | 'BANK_TRANSFER'
    reference?: string
    paidAt: string
  }>
  issuedAt: string
}

export interface Notification {
  id: string
  eventKey: string
  type: 'LOW_STOCK' | 'SALE_COMPLETED' | 'GOODS_RECEIVED'
  status: 'PENDING' | 'SENT' | 'FAILED'
  recipient: string
  subject: string
  details: string
  attempts: number
  createdAt: string
  sentAt?: string
}

export interface SalesReport {
  eventId: string
  salesOrderId: string
  productId: string
  warehouseId: string
  quantity: number
  occurredAt: string
}

export interface PurchaseReport {
  eventId: string
  receiptId: string
  purchaseOrderId: string
  productId: string
  warehouseId: string
  quantity: number
  occurredAt: string
}

export interface InventoryReport {
  id: string
  productId: string
  warehouseId: string
  quantity: number
  reservedQuantity: number
  availableQuantity: number
  movementType: string
  occurredAt: string
}
