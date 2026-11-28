import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { AuthProvider } from './context/AuthContext'
import { QueryProvider } from './providers/QueryProvider'
import { AppRoutes } from './routes/AppRoutes'
import './assets/styles/App.css'

function App() {
  return (
    <BrowserRouter>
      <QueryProvider>
        <AuthProvider>
          <AppRoutes />
          <Toaster position="top-right" toastOptions={{ duration: 4000 }} />
        </AuthProvider>
      </QueryProvider>
    </BrowserRouter>
  )
}

export default App
