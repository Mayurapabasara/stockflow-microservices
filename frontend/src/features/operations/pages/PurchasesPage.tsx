import { useState, type FormEvent } from 'react'
import { CheckCircle2, PackagePlus, Send, XCircle } from 'lucide-react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { DataTable, type Column } from '../../../components/common/DataTable'
import { Input } from '../../../components/common/Input'
import { Pagination } from '../../../components/common/Pagination'
import { useAuth } from '../../../hooks/useAuth'
import { formatCurrency } from '../../../utils/formatCurrency'
import { formatDate } from '../../../utils/formatDate'
import { productApi } from '../../products/api/productApi'
import { operationsApi } from '../api'
import type { PurchaseOrder } from '../types'

export function PurchasesPage() {
  const [page, setPage] = useState(0)
  const [form, setForm] = useState({ supplierId: '', warehouseId: '', productId: '', quantity: 1, unitPrice: 0 })
  const { isAdmin } = useAuth()
  const queryClient = useQueryClient()
  const orders = useQuery({ queryKey: ['purchases', page], queryFn: () => operationsApi.purchases(page) })
  const suppliers = useQuery({ queryKey: ['supplier-options'], queryFn: () => operationsApi.suppliers(0, 100) })
  const warehouses = useQuery({ queryKey: ['warehouses'], queryFn: operationsApi.warehouses })
  const products = useQuery({ queryKey: ['products', 'options'], queryFn: () => productApi.list(0, 100) })
  const refresh = () => queryClient.invalidateQueries({ queryKey: ['purchases'] })
  const create = useMutation({
    mutationFn: () => operationsApi.createPurchase({ supplierId: form.supplierId, warehouseId: form.warehouseId, items: [{ productId: form.productId, quantity: form.quantity, unitPrice: form.unitPrice }] }),
    onSuccess: async () => { await refresh(); toast.success('Draft purchase order created') },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const action = useMutation({
    mutationFn: ({ type, order }: { type: 'submit' | 'cancel' | 'receive'; order: PurchaseOrder }) => {
      if (type === 'submit') return operationsApi.submitPurchase(order.id)
      if (type === 'cancel') return operationsApi.cancelPurchase(order.id)
      return operationsApi.receivePurchase(order.id, order.items.filter((item) => item.quantity > item.receivedQuantity).map((item) => ({ purchaseOrderItemId: item.id, quantity: item.quantity - item.receivedQuantity })))
    },
    onSuccess: async (_, variables) => { await refresh(); toast.success(variables.type === 'receive' ? 'Goods received' : `Order ${variables.type}ed`) },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const columns: Column<PurchaseOrder>[] = [
    { header: 'Order', cell: (row) => <div><code>{row.id.slice(0, 8)}</code><small>{formatDate(row.createdAt)}</small></div> },
    { header: 'Supplier', cell: (row) => suppliers.data?.content.find((s) => s.id === row.supplierId)?.name || row.supplierId.slice(0, 8) },
    { header: 'Lines', cell: (row) => row.items.length },
    { header: 'Total', cell: (row) => formatCurrency(row.total) },
    { header: 'Status', cell: (row) => <span className={`badge badge--${row.status.toLowerCase().replace('_', '-')}`}>{row.status.replace('_', ' ')}</span> },
  ]
  if (isAdmin) columns.push({ header: 'Actions', cell: (row) => <div className="row-actions">
    {row.status === 'DRAFT' && <><Button variant="ghost" icon={<Send size={15} />} onClick={() => action.mutate({ type: 'submit', order: row })}>Submit</Button><Button variant="ghost" icon={<XCircle size={15} />} onClick={() => action.mutate({ type: 'cancel', order: row })}>Cancel</Button></>}
    {(row.status === 'SUBMITTED' || row.status === 'PARTIALLY_RECEIVED') && <Button variant="ghost" icon={<CheckCircle2 size={15} />} onClick={() => action.mutate({ type: 'receive', order: row })}>Receive outstanding</Button>}
  </div> })

  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Procurement</span><h1>Purchase orders</h1><p>Create, submit, and receive supplier orders.</p></div></header>
      {isAdmin && <form className="panel" onSubmit={(e: FormEvent) => { e.preventDefault(); create.mutate() }}>
        <header className="panel__header"><div><h2>New draft order</h2><p>Add the first product line. More complex orders remain available through the API.</p></div></header>
        <div className="form-grid form-grid--3">
          <label className="field"><span className="field__label">Supplier</span><select required value={form.supplierId} onChange={(e) => setForm({ ...form, supplierId: e.target.value })}><option value="">Select supplier</option>{suppliers.data?.content.filter((s) => s.status === 'ACTIVE').map((s) => <option value={s.id} key={s.id}>{s.code} · {s.name}</option>)}</select></label>
          <label className="field"><span className="field__label">Warehouse</span><select required value={form.warehouseId} onChange={(e) => setForm({ ...form, warehouseId: e.target.value })}><option value="">Select warehouse</option>{warehouses.data?.filter((w) => w.active).map((w) => <option value={w.id} key={w.id}>{w.code} · {w.name}</option>)}</select></label>
          <label className="field"><span className="field__label">Product</span><select required value={form.productId} onChange={(e) => { const product = products.data?.content.find((p) => p.id === e.target.value); setForm({ ...form, productId: e.target.value, unitPrice: product?.price ?? form.unitPrice }) }}><option value="">Select product</option>{products.data?.content.filter((p) => p.status === 'ACTIVE').map((p) => <option value={p.id} key={p.id}>{p.sku} · {p.name}</option>)}</select></label>
          <Input label="Quantity" type="number" min="1" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: Number(e.target.value) })} />
          <Input label="Unit price" type="number" min="0" step="0.01" value={form.unitPrice} onChange={(e) => setForm({ ...form, unitPrice: Number(e.target.value) })} />
        </div>
        <Button type="submit" icon={<PackagePlus size={17} />} loading={create.isPending}>Create order</Button>
      </form>}
      <section className="panel"><DataTable columns={columns} rows={orders.data?.content ?? []} rowKey={(row) => row.id} emptyMessage="No purchase orders yet." />{orders.data && <Pagination page={orders.data.number} totalPages={orders.data.totalPages} totalElements={orders.data.totalElements} onChange={setPage} />}</section>
    </>
  )
}
