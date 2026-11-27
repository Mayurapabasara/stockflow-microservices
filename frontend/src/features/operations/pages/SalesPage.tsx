import { useState, type FormEvent } from 'react'
import { CheckCircle2, Plus, ShoppingCart, XCircle } from 'lucide-react'
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
import type { SalesOrder } from '../types'

export function SalesPage() {
  const [page, setPage] = useState(0)
  const [customer, setCustomer] = useState({ name: '', email: '', phone: '', address: '', active: true })
  const [order, setOrder] = useState({ customerId: '', warehouseId: '', productId: '', quantity: 1, unitPrice: 0 })
  const { isAdmin } = useAuth()
  const queryClient = useQueryClient()
  const sales = useQuery({ queryKey: ['sales', page], queryFn: () => operationsApi.sales(page) })
  const customers = useQuery({ queryKey: ['customers', 'options'], queryFn: () => operationsApi.customers(0, 100) })
  const warehouses = useQuery({ queryKey: ['warehouses'], queryFn: operationsApi.warehouses })
  const products = useQuery({ queryKey: ['products', 'options'], queryFn: () => productApi.list(0, 100) })
  const refresh = async () => { await queryClient.invalidateQueries({ queryKey: ['sales'] }); await queryClient.invalidateQueries({ queryKey: ['customers'] }); await queryClient.invalidateQueries({ queryKey: ['dashboard'] }) }
  const addCustomer = useMutation({
    mutationFn: operationsApi.createCustomer,
    onSuccess: async () => { await refresh(); setCustomer({ name: '', email: '', phone: '', address: '', active: true }); toast.success('Customer created') },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const create = useMutation({
    mutationFn: () => operationsApi.createSale({ customerId: order.customerId, warehouseId: order.warehouseId, items: [{ productId: order.productId, quantity: order.quantity, unitPrice: order.unitPrice }] }),
    onSuccess: async () => { await refresh(); toast.success('Draft sale created') },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const action = useMutation({
    mutationFn: ({ type, id }: { type: 'complete' | 'cancel'; id: string }) => type === 'complete' ? operationsApi.completeSale(id) : operationsApi.cancelSale(id),
    onSuccess: async (_, value) => { await refresh(); await queryClient.invalidateQueries({ queryKey: ['invoices'] }); toast.success(`Sale ${value.type}d`) },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const columns: Column<SalesOrder>[] = [
    { header: 'Order', cell: (row) => <div><code>{row.id.slice(0, 8)}</code><small>{formatDate(row.createdAt)}</small></div> },
    { header: 'Customer', cell: (row) => customers.data?.content.find((c) => c.id === row.customerId)?.name || row.customerId.slice(0, 8) },
    { header: 'Lines', cell: (row) => row.items.length },
    { header: 'Total', cell: (row) => formatCurrency(row.total) },
    { header: 'Status', cell: (row) => <span className={`badge badge--${row.status.toLowerCase()}`}>{row.status}</span> },
  ]
  if (isAdmin) columns.push({ header: 'Actions', cell: (row) => row.status === 'DRAFT' ? <div className="row-actions"><Button variant="ghost" icon={<CheckCircle2 size={15} />} onClick={() => action.mutate({ type: 'complete', id: row.id })}>Complete</Button><Button variant="ghost" icon={<XCircle size={15} />} onClick={() => action.mutate({ type: 'cancel', id: row.id })}>Cancel</Button></div> : '—' })

  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Order desk</span><h1>Sales</h1><p>Create customer orders and complete them into invoices.</p></div></header>
      {isAdmin && <section className="split-grid">
        <form className="panel" onSubmit={(e: FormEvent) => { e.preventDefault(); addCustomer.mutate(customer) }}>
          <header className="panel__header"><div><h2>New customer</h2><p>Add a customer before creating an order.</p></div></header>
          <div className="form-grid"><Input label="Name" required value={customer.name} onChange={(e) => setCustomer({ ...customer, name: e.target.value })} /><Input label="Email" type="email" value={customer.email} onChange={(e) => setCustomer({ ...customer, email: e.target.value })} /><Input label="Phone" value={customer.phone} onChange={(e) => setCustomer({ ...customer, phone: e.target.value })} /><Input label="Address" value={customer.address} onChange={(e) => setCustomer({ ...customer, address: e.target.value })} /></div>
          <Button type="submit" icon={<Plus size={17} />} loading={addCustomer.isPending}>Create customer</Button>
        </form>
        <form className="panel" onSubmit={(e: FormEvent) => { e.preventDefault(); create.mutate() }}>
          <header className="panel__header"><div><h2>New sales order</h2><p>Create a draft with its first product line.</p></div></header>
          <div className="form-grid">
            <label className="field"><span className="field__label">Customer</span><select required value={order.customerId} onChange={(e) => setOrder({ ...order, customerId: e.target.value })}><option value="">Select customer</option>{customers.data?.content.filter((c) => c.active).map((c) => <option value={c.id} key={c.id}>{c.name}</option>)}</select></label>
            <label className="field"><span className="field__label">Warehouse</span><select required value={order.warehouseId} onChange={(e) => setOrder({ ...order, warehouseId: e.target.value })}><option value="">Select warehouse</option>{warehouses.data?.filter((w) => w.active).map((w) => <option value={w.id} key={w.id}>{w.code} · {w.name}</option>)}</select></label>
            <label className="field"><span className="field__label">Product</span><select required value={order.productId} onChange={(e) => { const product = products.data?.content.find((p) => p.id === e.target.value); setOrder({ ...order, productId: e.target.value, unitPrice: product?.price ?? 0 }) }}><option value="">Select product</option>{products.data?.content.filter((p) => p.status === 'ACTIVE').map((p) => <option value={p.id} key={p.id}>{p.sku} · {p.name}</option>)}</select></label>
            <Input label="Quantity" type="number" min="1" value={order.quantity} onChange={(e) => setOrder({ ...order, quantity: Number(e.target.value) })} />
            <Input label="Unit price" type="number" min="0" step="0.01" value={order.unitPrice} onChange={(e) => setOrder({ ...order, unitPrice: Number(e.target.value) })} />
          </div>
          <Button type="submit" icon={<ShoppingCart size={17} />} loading={create.isPending}>Create order</Button>
        </form>
      </section>}
      <section className="panel"><DataTable columns={columns} rows={sales.data?.content ?? []} rowKey={(row) => row.id} emptyMessage="No sales orders yet." />{sales.data && <Pagination page={sales.data.number} totalPages={sales.data.totalPages} totalElements={sales.data.totalElements} onChange={setPage} />}</section>
    </>
  )
}
