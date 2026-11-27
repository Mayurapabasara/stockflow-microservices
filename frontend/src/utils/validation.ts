export const required = (value?: string | null) =>
  Boolean(value?.trim())

export const validEmail = (value?: string | null) =>
  !value || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)

export const validUuid = (value?: string | null) =>
  Boolean(
    value &&
      /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(
        value,
      ),
  )
