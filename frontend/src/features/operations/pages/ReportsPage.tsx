import { useState } from 'react'
import { Download } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { DataTable, type Column } from '../../../components/common/DataTable'
import { Pagination } from '../../../components/common/Pagination'
import type { PageResponse } from '../../../types/api.types'
import { formatDate } from '../../../utils/formatDate'
import { operationsApi, type InventoryReport, type PurchaseReport, type SalesReport } from '../api'

type ReportName = 'sales' | 'purchases' | 'inventory'
type ReportRow = SalesReport | PurchaseReport | InventoryReport

export function ReportsPage() {
  const [name, setName] = useState<ReportName>('sales')
  const [page, setPage] = useState(0)
  const report = useQuery<PageResponse<ReportRow>>({
    queryKey: ['report', name, page],
    queryFn: () => operationsApi.report<ReportRow>(name, page),
  })
  const rows = report.data?.content ?? []
  const common: Column<ReportRow>[] = [
    { header: 'Product', cell: (row) => <code className="truncate-code">{row.productId}</code> },
    { header: 'Warehouse', cell: (row) => <code className="truncate-code">{row.warehouseId}</code> },
    { header: 'Occurred', cell: (row) => formatDate(row.occurredAt) },
  ]
  const columns = name === 'inventory'
    ? [...common.slice(0, 2), { header: 'On hand', cell: (row: ReportRow) => row.quantity }, { header: 'Available', cell: (row: ReportRow) => 'availableQuantity' in row ? row.availableQuantity : '—' }, common[2]]
    : [...common.slice(0, 2), { header: 'Units', cell: (row: ReportRow) => row.quantity }, common[2]]

  const download = async () => {
    try { await operationsApi.downloadReport(name); toast.success('CSV downloaded') }
    catch (error) { toast.error(apiErrorMessage(error)) }
  }
  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Analytics</span><h1>Operational reports</h1><p>Inspect event-backed quantities or download a CSV extract.</p></div><Button icon={<Download size={17} />} onClick={() => void download()}>Export CSV</Button></header>
      <div className="tabs">{(['sales', 'purchases', 'inventory'] as ReportName[]).map((item) => <button key={item} className={name === item ? 'tab tab--active' : 'tab'} onClick={() => { setName(item); setPage(0) }}>{item[0].toUpperCase() + item.slice(1)}</button>)}</div>
      <section className="panel">
        <DataTable columns={columns} rows={rows} rowKey={(row) => 'eventId' in row ? row.eventId : row.id} emptyMessage="No report rows yet. Kafka events populate this view." />
        {report.data && <Pagination page={report.data.number} totalPages={report.data.totalPages} totalElements={report.data.totalElements} onChange={setPage} />}
      </section>
    </>
  )
}
