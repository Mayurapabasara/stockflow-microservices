import axios, {
  AxiosError,
  type InternalAxiosRequestConfig,
} from 'axios'
import type { ApiProblem, AuthResponse } from '../types/api.types'
import { ENDPOINTS } from '../constants/endpoints'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '') ||
  'http://localhost:8080'

const ACCESS_TOKEN_KEY = 'stockflow.accessToken'
const REFRESH_TOKEN_KEY = 'stockflow.refreshToken'
const USER_KEY = 'stockflow.user'

export const readAccessToken = () => sessionStorage.getItem(ACCESS_TOKEN_KEY)
export const readRefreshToken = () => sessionStorage.getItem(REFRESH_TOKEN_KEY)

export function persistAuth(response: AuthResponse) {
  sessionStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken)
  sessionStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken)
  sessionStorage.setItem(USER_KEY, JSON.stringify(response.user))
  window.dispatchEvent(new CustomEvent('stockflow:auth-updated'))
}

export function readStoredUser() {
  const value = sessionStorage.getItem(USER_KEY)
  if (!value) return null
  try {
    return JSON.parse(value) as AuthResponse['user']
  } catch {
    clearAuth()
    return null
  }
}

export function clearAuth() {
  sessionStorage.removeItem(ACCESS_TOKEN_KEY)
  sessionStorage.removeItem(REFRESH_TOKEN_KEY)
  sessionStorage.removeItem(USER_KEY)
}

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15_000,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = readAccessToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

type RetryableRequest = InternalAxiosRequestConfig & { _retry?: boolean }
let refreshRequest: Promise<AuthResponse> | null = null

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const request = error.config as RetryableRequest | undefined
    const refreshToken = readRefreshToken()
    const isAuthRequest = request?.url?.startsWith('/api/auth/')

    if (
      error.response?.status !== 401 ||
      !request ||
      request._retry ||
      isAuthRequest ||
      !refreshToken
    ) {
      return Promise.reject(error)
    }

    request._retry = true
    try {
      refreshRequest ??= axios
        .post<AuthResponse>(
          `${API_BASE_URL}${ENDPOINTS.auth.refresh}`,
          { refreshToken },
          { timeout: 15_000 },
        )
        .then(({ data }) => data)
        .finally(() => {
          refreshRequest = null
        })
      const refreshed = await refreshRequest
      persistAuth(refreshed)
      request.headers.Authorization = `Bearer ${refreshed.accessToken}`
      return api(request)
    } catch (refreshError) {
      clearAuth()
      window.dispatchEvent(new CustomEvent('stockflow:auth-expired'))
      return Promise.reject(refreshError)
    }
  },
)

export function apiErrorMessage(error: unknown) {
  if (axios.isAxiosError<ApiProblem>(error)) {
    const data = error.response?.data
    if (data?.errors) return Object.values(data.errors).join(', ')
    return data?.detail || data?.message || error.message
  }
  return error instanceof Error ? error.message : 'An unexpected error occurred'
}

export { API_BASE_URL }
