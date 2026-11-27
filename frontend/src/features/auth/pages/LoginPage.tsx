import { useState } from 'react'
import { LockKeyhole, PackageCheck, UserRound } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { Input } from '../../../components/common/Input'
import { useAuth } from '../../../hooks/useAuth'

interface LoginFields {
  usernameOrEmail: string
  password: string
}

export function LoginPage() {
  const { login, isAuthenticated } = useAuth()
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()
  const { register, handleSubmit, formState: { errors } } = useForm<LoginFields>()
  if (isAuthenticated) return <Navigate to="/" replace />

  const submit = async (values: LoginFields) => {
    setLoading(true)
    try {
      await login(values)
      navigate((location.state as { from?: string } | null)?.from || '/', { replace: true })
    } catch (error) {
      toast.error(apiErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-brand">
        <span className="brand-mark brand-mark--large"><PackageCheck /></span>
        <span className="eyebrow">StockFlow</span>
        <h1>One clear view of every stock movement.</h1>
        <p>Manage the catalog, purchasing, sales, warehouses, suppliers, and operational reports from a single workspace.</p>
      </section>
      <section className="auth-card">
        <div><span className="eyebrow">Welcome back</span><h2>Sign in</h2><p>Use your StockFlow account to continue.</p></div>
        <form onSubmit={handleSubmit(submit)}>
          <Input label="Username or email" autoComplete="username" icon={<UserRound size={18} />} error={errors.usernameOrEmail?.message} {...register('usernameOrEmail', { required: 'Username or email is required' })} />
          <Input label="Password" type="password" autoComplete="current-password" icon={<LockKeyhole size={18} />} error={errors.password?.message} {...register('password', { required: 'Password is required' })} />
          <Button type="submit" loading={loading}>Sign in</Button>
        </form>
        <p className="auth-switch">New to StockFlow? <Link to="/register">Create an account</Link></p>
      </section>
    </main>
  )
}
