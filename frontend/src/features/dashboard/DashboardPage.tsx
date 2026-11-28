import { ArrowDownToLine, ArrowUpFromLine, Boxes, PackageSearch } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { apiErrorMessage } from '../../api/axiosInstance'
import { InventoryChart } from '../../components/charts/InventoryChart'
import { SalesChart } from '../../components/charts/SalesChart'
import { LoadingSpinner } from '../../components/common/LoadingSpinner'
import { operationsApi } from '../operations/api'

export function DashboardPage() {
  const summary = useQuery({ queryKey: ['dashboard'], queryFn: operationsApi.dashboard })
  const sales = useQuery({ queryKey: ['sales-report-chart'], queryFn: () => operationsApi.salesReport(100) })
  if (summary.isLoading) return <LoadingSpinner label="Loading dashboard" />
  if (!summary.data) return <div className="error-state">{apiErrorMessage(summary.error)}</div>

  const byDay = new Map<string, number>()
  sales.data?.content.forEach((item) => {
    const key = new Date(item.occurredAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })
    byDay.set(key, (byDay.get(key) ?? 0) + item.quantity)
  })
  const chartData = [...byDay.entries()].reverse().map(([label, units]) => ({ label, units }))
  const cards = [
    { label: 'Units on hand', value: summary.data.quantityOnHand, icon: Boxes, tone: 'blue' },
    { label: 'Available units', value: summary.data.quantityAvailable, icon: PackageSearch, tone: 'green' },
    { label: 'Units sold', value: summary.data.unitsSold, icon: ArrowUpFromLine, tone: 'violet' },
    { label: 'Units received', value: summary.data.unitsReceived, icon: ArrowDownToLine, tone: 'orange' },
  ]

  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Overview</span><h1>Operations dashboard</h1><p>Current inventory and event-backed movement totals.</p></div></header>
      <section className="metric-grid">
        {cards.map(({ label, value, icon: Icon, tone }) => (
          <article className="metric-card" key={label}>
            <span className={`metric-card__icon metric-card__icon--${tone}`}><Icon size={20} /></span>
            <span>{label}</span><strong>{value.toLocaleString()}</strong>
          </article>
        ))}
      </section>
      <section className="dashboard-grid">
        <article className="panel"><header className="panel__header"><div><h2>Sales volume</h2><p>Recent fulfilled line quantities</p></div></header>
          {chartData.length ? <SalesChart data={chartData} /> : <div className="empty-state">Sales events will appear here.</div>}
        </article>
        <article className="panel"><header className="panel__header"><div><h2>Inventory allocation</h2><p>Available and reserved quantities</p></div></header>
          <InventoryChart available={summary.data.quantityAvailable} reserved={summary.data.quantityReserved} />
        </article>
      </section>
      <section className="panel facts">
        <div><span>Tracked inventory records</span><strong>{summary.data.inventoryRecords}</strong></div>
        <div><span>Completed sale lines</span><strong>{summary.data.saleLines}</strong></div>
        <div><span>Purchase receipt lines</span><strong>{summary.data.receiptLines}</strong></div>
      </section>
    </>
  )
}
