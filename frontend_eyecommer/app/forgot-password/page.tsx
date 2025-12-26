"use client"

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import Header from '@/components/layout/header'
import Footer from '@/components/layout/footer'
import { forgotPassword as forgotPasswordApi } from '@/lib/services/auth'

export default function ForgotPasswordPage() {
  const router = useRouter()
  const [email, setEmail] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setMessage('')
    try {
      await forgotPasswordApi(email)
      setMessage('If your email exists, a reset link has been sent.')
    } catch (err: any) {
      setError(err?.message || 'Request failed')
    }
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />
      <main className="flex-1 flex items-center justify-center py-16">
        <Card className="w-full max-w-md">
          <CardContent className="p-8">
            <h1 className="text-2xl font-bold mb-2">Forgot Password</h1>
            <p className="text-muted-foreground mb-6">Enter your email to receive reset instructions</p>

            <form onSubmit={handleSubmit} className="space-y-4">
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

              {error && <div className="p-3 bg-red-100 text-red-800 rounded text-sm">{error}</div>}
              {message && <div className="p-3 bg-green-100 text-green-800 rounded text-sm">{message}</div>}

              <Button type="submit" className="w-full">
                Send reset link
              </Button>
            </form>
          </CardContent>
        </Card>
      </main>
      <Footer />
    </div>
  )
}
