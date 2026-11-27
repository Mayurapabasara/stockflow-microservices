import { useState } from 'react'
import { RotateCcw } from 'lucide-react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { DataTable, type Column } from '../../../components/common/DataTable'
import { Pagination } from '../../../components/common/Pagination'
import { formatDate } from '../../../utils/formatDate'
import { operationsApi } from '../api'
import type { Notification } from '../types'

export function NotificationsPage() {
  const [page, setPage] = useState(0)
  const queryClient = useQueryClient()
  const notifications = useQuery({ queryKey: ['notifications', page], queryFn: () => operationsApi.notifications(page) })
  const retry = useMutation({
    mutationFn: operationsApi.retryNotification,
    onSuccess: async () => { await queryClient.invalidateQueries({ queryKey: ['notifications'] }); toast.success('Notification queued for retry') },
    onError: (error) => toast.error(apiErrorMessage(error)),
  })
  const columns: Column<Notification>[] = [
    { header: 'Notification', cell: (row) => <div><strong>{row.subject}</strong><small>{row.details}</small></div> },
    { header: 'Type', cell: (row) => row.type.replaceAll('_', ' ') },
    { header: 'Recipient', cell: (row) => row.recipient },
    { header: 'Status', cell: (row) => <span className={`badge badge--${row.status.toLowerCase()}`}>{row.status}</span> },
    { header: 'Attempts', cell: (row) => row.attempts },
    { header: 'Created', cell: (row) => formatDate(row.createdAt) },
    { header: 'Actions', cell: (row) => row.status === 'FAILED' ? <Button variant="ghost" icon={<RotateCcw size={15} />} onClick={() => retry.mutate(row.id)}>Retry</Button> : '—' },
  ]
  return (
    <>
      <header className="page-header"><div><span className="eyebrow">Communications</span><h1>Notifications</h1><p>Review persisted event notifications and retry failed mail.</p></div></header>
      <section className="panel"><DataTable columns={columns} rows={notifications.data?.content ?? []} rowKey={(row) => row.id} emptyMessage="No notifications yet." />{notifications.data && <Pagination page={notifications.data.number} totalPages={notifications.data.totalPages} totalElements={notifications.data.totalElements} onChange={setPage} />}</section>
    </>
  )
}
