"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { login as loginApi } from "@/lib/services/auth"

export default function LoginPage() {
  const router = useRouter()
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")

    try {
      const res: any = await loginApi({ username, password })

      // Normalize response shapes:
      // - { accessToken, refreshToken, user, ... }
      // - { status, message, data: { accessToken, refreshToken, userId, ... } }
      // - user object directly
      const token = res?.token || res?.accessToken || res?.access_token || res?.data?.accessToken
      const refreshToken = res?.refreshToken || res?.data?.refreshToken
      const user = res?.user || (res?.data?.user ? res?.data?.user : (res?.username || res?.email ? res : null))
      const userId = res?.userId || res?.data?.userId

      if (token) {
        localStorage.setItem('accessToken', token) // Changed from 'token' to 'accessToken'
      }
      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken)
      }
      if (!user && userId) {
        // Minimal placeholder so UI recognizes a signed-in state until a proper profile is fetched
        // TODO: Fetch full user profile from /api/users/me or similar endpoint
        const minimalUser = { 
          id: userId, 
          username,
          role: 'customer' // Default assumption; backend should provide actual role
        }
        localStorage.setItem('currentUser', JSON.stringify(minimalUser))
        router.push('/customer/dashboard')
        return
      }

      if (user) {
        localStorage.setItem('currentUser', JSON.stringify(user))

        if (user.role === 'admin') {
          router.push('/admin/dashboard')
        } else if (user.role === 'staff') {
          router.push('/staff/dashboard')
        } else {
          router.push('/customer/dashboard')
        }
      } else {
        setError('Login succeeded but server did not return user data')
      }
    } catch (err: any) {
      setError(err?.message || 'Invalid username or password')
    }
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1 flex items-center justify-center py-16">
        <Card className="w-full max-w-md">
          <CardContent className="p-8">
            <h1 className="text-2xl font-bold mb-2">Sign In</h1>
            <p className="text-muted-foreground mb-6">Welcome back to VisionHub</p>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-semibold mb-2">Username</label>
                <input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="your username"
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
                Sign In
              </Button>
            </form>

            <div className="mt-6 pt-6 border-t border-border">
              <p className="text-xs text-muted-foreground mb-3">Demo Credentials:</p>
              <div className="space-y-1 text-xs text-muted-foreground">
                <p>Customer: customer / password</p>
                <p>Staff: staff / password</p>
                <p>Admin: admin / password</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </main>

      <Footer />
    </div>
  )
}
