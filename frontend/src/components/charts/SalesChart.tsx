import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

export interface SalesPoint {
  label: string
  units: number
}

export function SalesChart({ data }: { data: SalesPoint[] }) {
  return (
    <div className="chart" aria-label="Sales volume chart">
      <ResponsiveContainer width="100%" height={270}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border)" />
          <XAxis dataKey="label" tickLine={false} axisLine={false} />
          <YAxis allowDecimals={false} tickLine={false} axisLine={false} />
          <Tooltip cursor={{ fill: 'var(--surface-soft)' }} />
          <Bar dataKey="units" fill="var(--primary)" radius={[6, 6, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}
