import { useForm } from 'react-hook-form'
import { Button } from '../../../components/common/Button'
import { Input } from '../../../components/common/Input'
import { useCatalogOptions } from '../hooks/useProducts'
import type { ProductInput } from '../types/product.types'

interface ProductFormProps {
  initial?: ProductInput
  loading?: boolean
  onSubmit: (input: ProductInput) => Promise<void>
  submitLabel: string
}

const empty: ProductInput = {
  sku: '',
  name: '',
  description: '',
  price: 0,
  status: 'ACTIVE',
  categoryId: '',
  brandId: '',
}

export function ProductForm({
  initial = empty,
  loading,
  onSubmit,
  submitLabel,
}: ProductFormProps) {
  const { categories, brands } = useCatalogOptions()
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProductInput>({ defaultValues: initial })

  return (
    <form className="form-card" onSubmit={handleSubmit(onSubmit)}>
      <div className="form-grid">
        <Input
          label="SKU"
          error={errors.sku?.message}
          {...register('sku', {
            required: 'SKU is required',
            pattern: {
              value: /^[A-Za-z0-9._-]+$/,
              message: 'Use letters, numbers, dots, underscores or hyphens',
            },
          })}
        />
        <Input
          label="Product name"
          error={errors.name?.message}
          {...register('name', { required: 'Name is required' })}
        />
        <Input
          label="Unit price"
          type="number"
          min="0"
          step="0.01"
          error={errors.price?.message}
          {...register('price', {
            valueAsNumber: true,
            min: { value: 0, message: 'Price cannot be negative' },
          })}
        />
        <label className="field">
          <span className="field__label">Status</span>
          <select {...register('status')}>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
            <option value="DISCONTINUED">Discontinued</option>
          </select>
        </label>
        <label className="field">
          <span className="field__label">Category</span>
          <select
            {...register('categoryId', { required: 'Category is required' })}
          >
            <option value="">Select category</option>
            {categories.data?.filter((item) => item.active).map((item) => (
              <option key={item.id} value={item.id}>{item.name}</option>
            ))}
          </select>
          {errors.categoryId && <span className="field__error">{errors.categoryId.message}</span>}
        </label>
        <label className="field">
          <span className="field__label">Brand</span>
          <select {...register('brandId', { required: 'Brand is required' })}>
            <option value="">Select brand</option>
            {brands.data?.filter((item) => item.active).map((item) => (
              <option key={item.id} value={item.id}>{item.name}</option>
            ))}
          </select>
          {errors.brandId && <span className="field__error">{errors.brandId.message}</span>}
        </label>
      </div>
      <label className="field">
        <span className="field__label">Description</span>
        <textarea rows={4} {...register('description')} />
      </label>
      {(!categories.data?.length || !brands.data?.length) && (
        <p className="notice">Create at least one active category and brand in Catalog setup before saving a product.</p>
      )}
      <div className="form-actions">
        <Button type="submit" loading={loading}>{submitLabel}</Button>
      </div>
    </form>
  )
}
