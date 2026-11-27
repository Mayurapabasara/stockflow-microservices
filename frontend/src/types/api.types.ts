export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

export interface User {
  id: string
  username: string
  email: string
  roles: string[]
  enabled: boolean
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: User
}

export interface ApiProblem {
  title?: string
  detail?: string
  message?: string
  errors?: Record<string, string>
}
