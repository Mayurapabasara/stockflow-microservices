import {
  useCallback,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { api, clearAuth, persistAuth, readStoredUser } from '../api/axiosInstance'
import { ENDPOINTS } from '../constants/endpoints'
import type { AuthResponse, User } from '../types/api.types'
import { AuthStateContext, type LoginInput, type RegisterInput } from './AuthStateContext'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(() => readStoredUser())

  useEffect(() => {
    const update = () => setUser(readStoredUser())
    const expire = () => setUser(null)
    window.addEventListener('stockflow:auth-updated', update)
    window.addEventListener('stockflow:auth-expired', expire)
    return () => {
      window.removeEventListener('stockflow:auth-updated', update)
      window.removeEventListener('stockflow:auth-expired', expire)
    }
  }, [])

  const login = useCallback(async (input: LoginInput) => {
    const { data } = await api.post<AuthResponse>(ENDPOINTS.auth.login, input)
    persistAuth(data)
    setUser(data.user)
  }, [])

  const register = useCallback(async (input: RegisterInput) => {
    const { data } = await api.post<AuthResponse>(ENDPOINTS.auth.register, input)
    persistAuth(data)
    setUser(data.user)
  }, [])

  const logout = useCallback(async () => {
    const refreshToken = sessionStorage.getItem('stockflow.refreshToken')
    try {
      if (refreshToken) {
        await api.post(ENDPOINTS.auth.logout, { refreshToken })
      }
    } finally {
      clearAuth()
      setUser(null)
    }
  }, [])

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: Boolean(user),
      isAdmin: Boolean(user?.roles.includes('ADMIN')),
      login,
      register,
      logout,
    }),
    [user, login, register, logout],
  )

  return <AuthStateContext.Provider value={value}>{children}</AuthStateContext.Provider>
}
