"use client"

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import Header from '@/components/layout/header'
import Footer from '@/components/layout/footer'
import { register as registerApi } from '@/lib/services/auth'

export default function RegisterPage() {
  const router = useRouter()
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      const res: any = await registerApi({ username, email, password, role: 'user' })
      const token = res?.token || res?.accessToken || res?.access_token
      const user = res?.user || (res?.email ? res : null)

      if (token) localStorage.setItem('token', token)
      if (user) localStorage.setItem('currentUser', JSON.stringify(user))

      // Redirect based on role if provided, otherwise go home
      if (user && user.role === 'admin') router.push('/admin/dashboard')
      else if (user && user.role === 'staff') router.push('/staff/dashboard')
      else router.push('/customer/dashboard')
    } catch (err: any) {
      setError(err?.message || 'Registration failed')
    }
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />
      <main className="flex-1 flex items-center justify-center py-16">
        <Card className="w-full max-w-md">
          <CardContent className="p-8">
            <h1 className="text-2xl font-bold mb-2">Register</h1>
            <p className="text-muted-foreground mb-6">Create a new account</p>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-semibold mb-2">Username</label>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Your username"
                  className="w-full px-4 py-2 border border-border rounded bg-card text-foreground"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-semibold mb-2">Email</label>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="your@email.com"
                  className="w-full px-4 py-2 border border-border rounded bg-card text-foreground"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-semibold mb-2">Password</label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full px-4 py-2 border border-border rounded bg-card text-foreground"
                  required
                />
              </div>

              {error && <div className="p-3 bg-red-100 text-red-800 rounded text-sm">{error}</div>}

              <Button type="submit" className="w-full">
                Register
              </Button>
            </form>
          </CardContent>
        </Card>
      </main>
      <Footer />
    </div>
  )
}
