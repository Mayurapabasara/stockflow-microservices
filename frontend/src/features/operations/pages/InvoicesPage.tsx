import { useState } from 'react'
import { Banknote } from 'lucide-react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { DataTable, type Column } from '../../../components/common/DataTable'
import { Input } from '../../../components/common/Input'
import { Modal } from '../../../components/common/Modal'
import { Pagination } from '../../../components/common/Pagination'
import { useAuth } from '../../../hooks/useAuth'
import { formatCurrency } from '../../../utils/formatCurrency'
import { formatDate } from '../../../utils/formatDate'
import { operationsApi } from '../api'
import type { Invoice } from '../types'

export function InvoicesPage() {
  const [page, setPage] = useState(0)
  const [selected, setSelected] = useState<Invoice | null>(null)
  const [payment, setPayment] = useState({ amount: 0, method: 'CASH', reference: '' })
  const { isAdmin } = useAuth()
  const queryClient = useQueryClient()
  const invoices = useQuery({ queryKey: ['invoices', page], queryFn: () => operationsApi.invoices(page) })
  const pay = useMutation({
    mutationFn: () => operationsApi.payInvoice(selected!.id, payment),
    onSuccess: async () => { await queryClient.invalidateQueries({ queryKey: ['invoices'] }); setSelected(null); toast.success('Payment recorded') },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const columns: Column<Invoice>[] = [
    { header: 'Invoice', cell: (row) => <div><code>{row.id.slice(0, 8)}</code><small>{formatDate(row.issuedAt)}</small></div> },
    { header: 'Order', cell: (row) => <code>{row.salesOrderId.slice(0, 8)}</code> },
    { header: 'Total', cell: (row) => formatCurrency(row.total) },
    { header: 'Paid', cell: (row) => formatCurrency(row.paidAmount) },
    { header: 'Balance', cell: (row) => <strong className={row.balance > 0 ? 'warning' : 'success'}>{formatCurrency(row.balance)}</strong> },
  ]
  if (isAdmin) columns.push({ header: 'Actions', cell: (row) => row.balance > 0 ? <Button variant="ghost" icon={<Banknote size={16} />} onClick={() => { setSelected(row); setPayment({ amount: row.balance, method: 'CASH', reference: '' }) }}>Record payment</Button> : <span className="badge badge--sent">PAID</span> })
  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Receivables</span><h1>Invoices</h1><p>Track balances and record payments against completed sales.</p></div></header>
      <section className="panel"><DataTable columns={columns} rows={invoices.data?.content ?? []} rowKey={(row) => row.id} emptyMessage="No invoices yet." />{invoices.data && <Pagination page={invoices.data.number} totalPages={invoices.data.totalPages} totalElements={invoices.data.totalElements} onChange={setPage} />}</section>
      <Modal open={Boolean(selected)} title="Record payment" onClose={() => setSelected(null)} footer={<><Button variant="secondary" onClick={() => setSelected(null)}>Cancel</Button><Button loading={pay.isPending} onClick={() => pay.mutate()}>Save payment</Button></>}>
        <div className="form-grid"><Input label="Amount" type="number" min="0.01" max={selected?.balance} step="0.01" value={payment.amount} onChange={(e) => setPayment({ ...payment, amount: Number(e.target.value) })} /><label className="field"><span className="field__label">Method</span><select value={payment.method} onChange={(e) => setPayment({ ...payment, method: e.target.value })}><option value="CASH">Cash</option><option value="CARD">Card</option><option value="BANK_TRANSFER">Bank transfer</option></select></label></div>
        <Input label="Reference" value={payment.reference} onChange={(e) => setPayment({ ...payment, reference: e.target.value })} />
      </Modal>
    </>
  )
}
