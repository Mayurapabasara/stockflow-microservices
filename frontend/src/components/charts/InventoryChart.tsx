import {
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
} from 'recharts'

export function InventoryChart({
  available,
  reserved,
}: {
  available: number
  reserved: number
}) {
  const data = [
    { name: 'Available', value: available },
    { name: 'Reserved', value: reserved },
  ]
  return (
    <div className="chart chart--pie" aria-label="Inventory availability chart">
      <ResponsiveContainer width="100%" height={270}>
        <PieChart>
          <Pie data={data} dataKey="value" nameKey="name" innerRadius={68} outerRadius={100} paddingAngle={3}>
            <Cell fill="var(--primary)" />
            <Cell fill="var(--warning)" />
          </Pie>
          <Tooltip />
        </PieChart>
      </ResponsiveContainer>
      <div className="chart-legend"><span><i className="dot dot--primary" />Available {available}</span><span><i className="dot dot--warning" />Reserved {reserved}</span></div>
    </div>
  )
}
