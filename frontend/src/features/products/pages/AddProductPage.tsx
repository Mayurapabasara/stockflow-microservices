import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { productApi } from '../api/productApi'
import { ProductForm } from '../components/ProductForm'
import type { ProductInput } from '../types/product.types'

export function AddProductPage() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const create = useMutation({
    mutationFn: productApi.create,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['products'] })
      toast.success('Product created')
      navigate('/products')
    },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Catalog</span><h1>Add product</h1><p>Create a product linked to an active category and brand.</p></div></header>
      <ProductForm submitLabel="Create product" loading={create.isPending} onSubmit={async (input: ProductInput) => { await create.mutateAsync(input) }} />
    </>
  )
}
