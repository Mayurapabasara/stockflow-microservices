import { useState, type FormEvent } from 'react'
import { Plus, Trash2 } from 'lucide-react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { DataTable, type Column } from '../../../components/common/DataTable'
import { Input } from '../../../components/common/Input'
import { useAuth } from '../../../hooks/useAuth'
import { productApi } from '../api/productApi'
import { useCatalogOptions } from '../hooks/useProducts'
import type { Brand, CatalogInput, Category } from '../types/product.types'

export function CatalogPage() {
  const { categories, brands } = useCatalogOptions()
  const { isAdmin } = useAuth()
  const queryClient = useQueryClient()
  const [kind, setKind] = useState<'category' | 'brand'>('category')
  const [input, setInput] = useState<CatalogInput>({ name: '', description: '', active: true })
  const create = useMutation({
    mutationFn: () => kind === 'category' ? productApi.createCategory(input) : productApi.createBrand(input),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: [kind === 'category' ? 'categories' : 'brands'] })
      setInput({ name: '', description: '', active: true })
      toast.success(`${kind === 'category' ? 'Category' : 'Brand'} created`)
    },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const remove = useMutation({
    mutationFn: ({ id, type }: { id: string; type: 'category' | 'brand' }) =>
      type === 'category' ? productApi.removeCategory(id) : productApi.removeBrand(id),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['categories'] })
      await queryClient.invalidateQueries({ queryKey: ['brands'] })
      toast.success('Catalog entry deleted')
    },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const columns = (type: 'category' | 'brand'): Column<Category | Brand>[] => [
    { header: 'Name', cell: (row) => <strong>{row.name}</strong> },
    { header: 'Description', cell: (row) => row.description || '—' },
    { header: 'Status', cell: (row) => <span className={`badge badge--${row.active ? 'active' : 'inactive'}`}>{row.active ? 'ACTIVE' : 'INACTIVE'}</span> },
    ...(isAdmin ? [{ header: 'Actions', cell: (row: Category | Brand) => <Button variant="ghost" icon={<Trash2 size={16} />} onClick={() => remove.mutate({ id: row.id, type })}>Delete</Button> }] : []),
  ]
  const submit = (event: FormEvent) => {
    event.preventDefault()
    if (!input.name.trim()) return
    create.mutate()
  }
  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Catalog</span><h1>Categories & brands</h1><p>Create the reference data required by products.</p></div></header>
      {isAdmin && <form className="panel inline-form" onSubmit={submit}>
        <label className="field"><span className="field__label">Type</span><select value={kind} onChange={(e) => setKind(e.target.value as 'category' | 'brand')}><option value="category">Category</option><option value="brand">Brand</option></select></label>
        <Input label="Name" value={input.name} onChange={(e) => setInput({ ...input, name: e.target.value })} />
        <Input label="Description" value={input.description} onChange={(e) => setInput({ ...input, description: e.target.value })} />
        <Button type="submit" icon={<Plus size={17} />} loading={create.isPending}>Add {kind}</Button>
      </form>}
      <section className="split-grid">
        <article className="panel"><header className="panel__header"><h2>Categories</h2></header><DataTable columns={columns('category')} rows={categories.data ?? []} rowKey={(row) => row.id} /></article>
        <article className="panel"><header className="panel__header"><h2>Brands</h2></header><DataTable columns={columns('brand')} rows={brands.data ?? []} rowKey={(row) => row.id} /></article>
      </section>
    </>
  )
}
