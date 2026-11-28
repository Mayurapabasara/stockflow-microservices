export function formatCurrency(
  value: number | string,
  currency = 'USD',
  locale = 'en-US',
) {
  const amount = typeof value === 'string' ? Number(value) : value
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
  }).format(Number.isFinite(amount) ? amount : 0)
}
