import { createContext } from 'react'
import type { User } from '../types/api.types'

export interface LoginInput {
  usernameOrEmail: string
  password: string
}

export interface RegisterInput {
  username: string
  email: string
  password: string
}

export interface AuthContextValue {
  user: User | null
  isAuthenticated: boolean
  isAdmin: boolean
  login: (input: LoginInput) => Promise<void>
  register: (input: RegisterInput) => Promise<void>
  logout: () => Promise<void>
}

export const AuthStateContext = createContext<AuthContextValue | undefined>(undefined)
