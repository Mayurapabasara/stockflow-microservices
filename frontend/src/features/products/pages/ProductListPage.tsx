import { useState } from 'react'
import { Plus } from 'lucide-react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { ConfirmDialog } from '../../../components/common/ConfirmDialog'
import { LoadingSpinner } from '../../../components/common/LoadingSpinner'
import { Pagination } from '../../../components/common/Pagination'
import { useAuth } from '../../../hooks/useAuth'
import { productApi } from '../api/productApi'
import { ProductTable } from '../components/ProductTable'
import { useProducts } from '../hooks/useProducts'
import type { Product } from '../types/product.types'

export function ProductListPage() {
  const [page, setPage] = useState(0)
  const [selected, setSelected] = useState<Product | null>(null)
  const { data, isLoading, error } = useProducts(page)
  const { isAdmin } = useAuth()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const remove = useMutation({
    mutationFn: productApi.remove,
    onSuccess: async () => {
      toast.success('Product deleted')
      setSelected(null)
      await queryClient.invalidateQueries({ queryKey: ['products'] })
    },
    onError: (reason) => toast.error(apiErrorMessage(reason)),
  })

  return (
    <>
      <header className="page-header">
        <div><span className="eyebrow">Catalog</span><h1>Products</h1><p>Manage product identity, price, category and lifecycle status.</p></div>
        {isAdmin && <Button icon={<Plus size={18} />} onClick={() => navigate('/products/new')}>Add product</Button>}
      </header>
      {isLoading ? <LoadingSpinner label="Loading products" /> : error ? (
        <div className="error-state">{apiErrorMessage(error)}</div>
      ) : (
        <section className="panel">
          <ProductTable products={data?.content ?? []} isAdmin={isAdmin} onDelete={setSelected} />
          {data && <Pagination page={data.number} totalPages={data.totalPages} totalElements={data.totalElements} onChange={setPage} />}
        </section>
      )}
      <ConfirmDialog
        open={Boolean(selected)}
        title="Delete product"
        message={`Delete ${selected?.name ?? 'this product'}? This cannot be undone.`}
        loading={remove.isPending}
        onClose={() => setSelected(null)}
        onConfirm={() => selected && remove.mutate(selected.id)}
        confirmLabel="Delete"
      />
    </>
  )
}
