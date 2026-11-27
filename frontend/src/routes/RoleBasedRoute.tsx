import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

export function RoleBasedRoute({
  roles,
  children,
}: {
  roles: string[]
  children: ReactNode
}) {
  const { user } = useAuth()
  return user?.roles.some((role) => roles.includes(role)) ? children : <Navigate to="/" replace />
}
