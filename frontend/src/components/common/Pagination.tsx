import { ChevronLeft, ChevronRight } from 'lucide-react'
import { Button } from './Button'

interface PaginationProps {
  page: number
  totalPages: number
  totalElements?: number
  onChange: (page: number) => void
}

export function Pagination({
  page,
  totalPages,
  totalElements,
  onChange,
}: PaginationProps) {
  if (totalPages <= 1) return null
  return (
    <nav className="pagination" aria-label="Pagination">
      <span>
        Page {page + 1} of {totalPages}
        {totalElements !== undefined && ` · ${totalElements} records`}
      </span>
      <div>
        <Button
          variant="secondary"
          aria-label="Previous page"
          disabled={page === 0}
          onClick={() => onChange(page - 1)}
          icon={<ChevronLeft size={16} />}
        >
          Previous
        </Button>
        <Button
          variant="secondary"
          aria-label="Next page"
          disabled={page + 1 >= totalPages}
          onClick={() => onChange(page + 1)}
          icon={<ChevronRight size={16} />}
        >
          Next
        </Button>
      </div>
    </nav>
  )
}
