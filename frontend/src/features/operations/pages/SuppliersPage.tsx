import { useState, type FormEvent } from 'react'
import { Plus } from 'lucide-react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { DataTable, type Column } from '../../../components/common/DataTable'
import { Input } from '../../../components/common/Input'
import { Pagination } from '../../../components/common/Pagination'
import { useAuth } from '../../../hooks/useAuth'
import { operationsApi } from '../api'
import type { Supplier } from '../types'

export function SuppliersPage() {
  const [page, setPage] = useState(0)
  const [form, setForm] = useState({ code: '', name: '', contactName: '', email: '', phone: '', address: '', status: 'ACTIVE' as const, products: [] as never[] })
  const { isAdmin } = useAuth()
  const queryClient = useQueryClient()
  const suppliers = useQuery({ queryKey: ['suppliers', page], queryFn: () => operationsApi.suppliers(page) })
  const create = useMutation({
    mutationFn: operationsApi.createSupplier,
    onSuccess: async () => { await queryClient.invalidateQueries({ queryKey: ['suppliers'] }); setForm({ code: '', name: '', contactName: '', email: '', phone: '', address: '', status: 'ACTIVE', products: [] }); toast.success('Supplier created') },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const columns: Column<Supplier>[] = [
    { header: 'Code', cell: (row) => <code>{row.code}</code> },
    { header: 'Supplier', cell: (row) => <div><strong>{row.name}</strong><small>{row.contactName || 'No contact name'}</small></div> },
    { header: 'Contact', cell: (row) => <div>{row.email || '—'}<small>{row.phone || '—'}</small></div> },
    { header: 'Products', cell: (row) => row.products.length },
    { header: 'Status', cell: (row) => <span className={`badge badge--${row.status.toLowerCase()}`}>{row.status}</span> },
  ]
  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Procurement</span><h1>Suppliers</h1><p>Maintain supplier contacts and sourcing relationships.</p></div></header>
      {isAdmin && <form className="panel" onSubmit={(e: FormEvent) => { e.preventDefault(); create.mutate(form) }}>
        <header className="panel__header"><div><h2>Add supplier</h2><p>Product sourcing terms can be maintained through the API after creation.</p></div></header>
        <div className="form-grid form-grid--3">
          <Input label="Code" required value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} />
          <Input label="Name" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          <Input label="Contact name" value={form.contactName} onChange={(e) => setForm({ ...form, contactName: e.target.value })} />
          <Input label="Email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
          <Input label="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
          <Input label="Address" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
        </div>
        <Button type="submit" icon={<Plus size={17} />} loading={create.isPending}>Create supplier</Button>
      </form>}
      <section className="panel"><DataTable columns={columns} rows={suppliers.data?.content ?? []} rowKey={(row) => row.id} emptyMessage="No suppliers yet." />{suppliers.data && <Pagination page={suppliers.data.number} totalPages={suppliers.data.totalPages} totalElements={suppliers.data.totalElements} onChange={setPage} />}</section>
    </>
  )
}
