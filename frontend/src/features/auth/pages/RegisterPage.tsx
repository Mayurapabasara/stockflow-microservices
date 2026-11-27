import { useState } from 'react'
import { AtSign, LockKeyhole, PackageCheck, UserRound } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { apiErrorMessage } from '../../../api/axiosInstance'
import { Button } from '../../../components/common/Button'
import { Input } from '../../../components/common/Input'
import { useAuth } from '../../../hooks/useAuth'

interface RegisterFields {
  username: string
  email: string
  password: string
}

export function RegisterPage() {
  const { register: createAccount, isAuthenticated } = useAuth()
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const form = useForm<RegisterFields>()
  if (isAuthenticated) return <Navigate to="/" replace />

  const submit = async (values: RegisterFields) => {
    setLoading(true)
    try {
      await createAccount(values)
      toast.success('Account created')
      navigate('/', { replace: true })
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
        <h1>Start with a reliable inventory foundation.</h1>
        <p>New registrations receive standard user access. An administrator controls write operations.</p>
      </section>
      <section className="auth-card">
        <div><span className="eyebrow">Create account</span><h2>Register</h2><p>Choose a username and a strong password.</p></div>
        <form onSubmit={form.handleSubmit(submit)}>
          <Input label="Username" icon={<UserRound size={18} />} error={form.formState.errors.username?.message} {...form.register('username', { required: 'Username is required', minLength: { value: 3, message: 'Use at least 3 characters' }, pattern: { value: /^[A-Za-z0-9._-]+$/, message: 'Use letters, numbers, dots, underscores or hyphens' } })} />
          <Input label="Email" type="email" icon={<AtSign size={18} />} error={form.formState.errors.email?.message} {...form.register('email', { required: 'Email is required' })} />
          <Input label="Password" type="password" autoComplete="new-password" icon={<LockKeyhole size={18} />} hint="12–72 characters" error={form.formState.errors.password?.message} {...form.register('password', { required: 'Password is required', minLength: { value: 12, message: 'Use at least 12 characters' }, maxLength: { value: 72, message: 'Use at most 72 characters' } })} />
          <Button type="submit" loading={loading}>Create account</Button>
        </form>
        <p className="auth-switch">Already registered? <Link to="/login">Sign in</Link></p>
      </section>
    </main>
  )
}
