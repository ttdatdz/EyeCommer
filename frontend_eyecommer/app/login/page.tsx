"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { login } from "@/lib/auth-store"

export default function LoginPage() {
  const router = useRouter()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const user = login(email, password)

    if (user) {
      if (user.role === "admin") {
        router.push("/admin/dashboard")
      } else if (user.role === "staff") {
        router.push("/staff/dashboard")
      } else {
        router.push("/customer/dashboard")
      }
    } else {
      setError("Invalid email or password")
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
                Sign In
              </Button>
            </form>

            <div className="mt-6 pt-6 border-t border-border">
              <p className="text-xs text-muted-foreground mb-3">Demo Credentials:</p>
              <div className="space-y-1 text-xs text-muted-foreground">
                <p>Customer: customer@example.com / password</p>
                <p>Staff: staff@example.com / password</p>
                <p>Admin: admin@example.com / password</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </main>

      <Footer />
    </div>
  )
}
