import { useState, type FormEvent } from 'react'
import { Plus, SlidersHorizontal } from 'lucide-react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { DataTable, type Column } from '../../../components/common/DataTable'
import { Input } from '../../../components/common/Input'
import { LoadingSpinner } from '../../../components/common/LoadingSpinner'
import { Pagination } from '../../../components/common/Pagination'
import { useAuth } from '../../../hooks/useAuth'
import { productApi } from '../../products/api/productApi'
import { operationsApi } from '../api'
import type { InventoryRecord } from '../types'

export function InventoryPage() {
  const [page, setPage] = useState(0)
  const [warehouse, setWarehouse] = useState({ code: '', name: '', address: '', active: true })
  const [adjustment, setAdjustment] = useState({ productId: '', warehouseId: '', quantityDelta: 1, reason: 'Stock count adjustment', reorderPoint: 0 })
  const { isAdmin } = useAuth()
  const queryClient = useQueryClient()
  const inventory = useQuery({ queryKey: ['inventory', page], queryFn: () => operationsApi.inventory(page) })
  const warehouses = useQuery({ queryKey: ['warehouses'], queryFn: operationsApi.warehouses })
  const products = useQuery({ queryKey: ['products', 'options'], queryFn: () => productApi.list(0, 100) })
  const refresh = async () => {
    await queryClient.invalidateQueries({ queryKey: ['inventory'] })
    await queryClient.invalidateQueries({ queryKey: ['warehouses'] })
    await queryClient.invalidateQueries({ queryKey: ['dashboard'] })
  }
  const addWarehouse = useMutation({
    mutationFn: operationsApi.createWarehouse,
    onSuccess: async () => { await refresh(); setWarehouse({ code: '', name: '', address: '', active: true }); toast.success('Warehouse created') },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const adjust = useMutation({
    mutationFn: operationsApi.adjustStock,
    onSuccess: async () => { await refresh(); toast.success('Stock adjusted') },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const columns: Column<InventoryRecord>[] = [
    { header: 'Warehouse', cell: (row) => <div><strong>{row.warehouseName}</strong><small>{row.warehouseCode}</small></div> },
    { header: 'Product ID', cell: (row) => <code className="truncate-code">{row.productId}</code> },
    { header: 'On hand', cell: (row) => row.quantity.toLocaleString() },
    { header: 'Reserved', cell: (row) => row.reservedQuantity.toLocaleString() },
    { header: 'Available', cell: (row) => <strong className={row.availableQuantity <= row.reorderPoint ? 'danger' : 'success'}>{row.availableQuantity.toLocaleString()}</strong> },
    { header: 'Reorder at', cell: (row) => row.reorderPoint.toLocaleString() },
  ]

  if (inventory.isLoading) return <LoadingSpinner label="Loading inventory" />
  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Stock control</span><h1>Inventory</h1><p>Review balances and record controlled warehouse adjustments.</p></div></header>
      {isAdmin && <section className="split-grid">
        <form className="panel" onSubmit={(e: FormEvent) => { e.preventDefault(); addWarehouse.mutate(warehouse) }}>
          <header className="panel__header"><div><h2>New warehouse</h2><p>Add a stock location.</p></div></header>
          <div className="form-grid"><Input label="Code" value={warehouse.code} onChange={(e) => setWarehouse({ ...warehouse, code: e.target.value })} required /><Input label="Name" value={warehouse.name} onChange={(e) => setWarehouse({ ...warehouse, name: e.target.value })} required /></div>
          <Input label="Address" value={warehouse.address} onChange={(e) => setWarehouse({ ...warehouse, address: e.target.value })} />
          <Button type="submit" icon={<Plus size={17} />} loading={addWarehouse.isPending}>Create warehouse</Button>
        </form>
        <form className="panel" onSubmit={(e: FormEvent) => { e.preventDefault(); adjust.mutate(adjustment) }}>
          <header className="panel__header"><div><h2>Adjust stock</h2><p>Use a signed quantity for receipts or corrections.</p></div></header>
          <div className="form-grid">
            <label className="field"><span className="field__label">Product</span><select required value={adjustment.productId} onChange={(e) => setAdjustment({ ...adjustment, productId: e.target.value })}><option value="">Select product</option>{products.data?.content.map((p) => <option value={p.id} key={p.id}>{p.sku} · {p.name}</option>)}</select></label>
            <label className="field"><span className="field__label">Warehouse</span><select required value={adjustment.warehouseId} onChange={(e) => setAdjustment({ ...adjustment, warehouseId: e.target.value })}><option value="">Select warehouse</option>{warehouses.data?.filter((w) => w.active).map((w) => <option value={w.id} key={w.id}>{w.code} · {w.name}</option>)}</select></label>
            <Input label="Quantity change" type="number" value={adjustment.quantityDelta} onChange={(e) => setAdjustment({ ...adjustment, quantityDelta: Number(e.target.value) })} required />
            <Input label="Reorder point" type="number" min="0" value={adjustment.reorderPoint} onChange={(e) => setAdjustment({ ...adjustment, reorderPoint: Number(e.target.value) })} />
          </div>
          <Input label="Reason" value={adjustment.reason} onChange={(e) => setAdjustment({ ...adjustment, reason: e.target.value })} required />
          <Button type="submit" icon={<SlidersHorizontal size={17} />} loading={adjust.isPending}>Apply adjustment</Button>
        </form>
      </section>}
      <section className="panel">
        <header className="panel__header"><div><h2>Current balances</h2><p>{inventory.data?.totalElements ?? 0} product and warehouse records</p></div></header>
        {inventory.error ? <div className="error-state">{apiErrorMessage(inventory.error)}</div> : <DataTable columns={columns} rows={inventory.data?.content ?? []} rowKey={(row) => `${row.warehouseId}-${row.productId}`} />}
        {inventory.data && <Pagination page={inventory.data.number} totalPages={inventory.data.totalPages} totalElements={inventory.data.totalElements} onChange={setPage} />}
      </section>
    </>
  )
}
