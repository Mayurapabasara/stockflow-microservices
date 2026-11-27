import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useNavigate, useParams } from 'react-router-dom'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { LoadingSpinner } from '../../../components/common/LoadingSpinner'
import { productApi } from '../api/productApi'
import { ProductForm } from '../components/ProductForm'
import type { ProductInput } from '../types/product.types'

export function EditProductPage() {
  const { id = '' } = useParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const product = useQuery({ queryKey: ['product', id], queryFn: () => productApi.get(id), enabled: Boolean(id) })
  const update = useMutation({
    mutationFn: (input: ProductInput) => productApi.update(id, input),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['products'] })
      toast.success('Product updated')
      navigate('/products')
    },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  if (product.isLoading) return <LoadingSpinner label="Loading product" />
  if (!product.data) return <div className="error-state">{apiErrorMessage(product.error)}</div>
  const initial: ProductInput = {
    sku: product.data.sku,
    name: product.data.name,
    description: product.data.description,
    price: product.data.price,
    status: product.data.status,
    categoryId: product.data.category.id,
    brandId: product.data.brand.id,
  }
  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Catalog</span><h1>Edit product</h1><p>Update {product.data.name}.</p></div></header>
      <ProductForm initial={initial} submitLabel="Save changes" loading={update.isPending} onSubmit={async (input) => { await update.mutateAsync(input) }} />
    </>
  )
}
