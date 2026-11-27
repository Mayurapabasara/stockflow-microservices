import { Edit3, Trash2 } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { Button } from '../../../components/common/Button'
import { DataTable, type Column } from '../../../components/common/DataTable'
import { formatCurrency } from '../../../utils/formatCurrency'
import type { Product } from '../types/product.types'

export function ProductTable({
  products,
  isAdmin,
  onDelete,
}: {
  products: Product[]
  isAdmin: boolean
  onDelete: (product: Product) => void
}) {
  const navigate = useNavigate()
  const columns: Column<Product>[] = [
    { header: 'SKU', cell: (row) => <code>{row.sku}</code> },
    { header: 'Product', cell: (row) => <div><strong>{row.name}</strong><small>{row.category.name} · {row.brand.name}</small></div> },
    { header: 'Price', cell: (row) => formatCurrency(row.price) },
    { header: 'Status', cell: (row) => <span className={`badge badge--${row.status.toLowerCase()}`}>{row.status}</span> },
  ]
  if (isAdmin) {
    columns.push({
      header: 'Actions',
      className: 'actions-cell',
      cell: (row) => (
        <div className="row-actions">
          <Button variant="ghost" icon={<Edit3 size={16} />} onClick={() => navigate(`/products/${row.id}/edit`)}>Edit</Button>
          <Button variant="ghost" icon={<Trash2 size={16} />} onClick={() => onDelete(row)}>Delete</Button>
        </div>
      ),
    })
  }
  return <DataTable columns={columns} rows={products} rowKey={(row) => row.id} emptyMessage="No products yet." />
}
