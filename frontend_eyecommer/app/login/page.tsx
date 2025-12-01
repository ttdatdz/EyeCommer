"use client"

import type React from "react"
import { useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { useAuth } from "../context/auth-context"

export default function LoginPage() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const { login } = useAuth()
  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setIsLoading(true)

    try {
      await login(username, password)

      // Redirect based on role
      const userRecord = { admin: "admin", staff: "staff", customer: "customer" }
      const role = Object.entries({ admin: "admin", staff: "staff", customer: "customer" }).find(
        ([key]) => key === username,
      )?.[1]

      if (username === "admin") {
        router.push("/admin")
      } else if (username === "staff") {
        router.push("/staff")
      } else if (username === "customer") {
        router.push("/customer")
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Đã xảy ra lỗi")
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="bg-card border border-border rounded-lg p-8">
          {/* Header */}
          <div className="mb-8">
            <Link
              href="/"
              className="font-serif text-2xl font-light tracking-wide text-foreground hover:text-accent transition-colors"
            >
              OPTICA
            </Link>
            <h1 className="text-2xl font-serif font-light text-foreground mt-4">Đăng Nhập</h1>
            <p className="text-sm text-muted-foreground mt-2">Nhập thông tin đăng nhập của bạn để tiếp tục</p>
          </div>

          {/* Demo Credentials */}
          <div className="bg-muted p-4 rounded-md mb-6 text-xs text-muted-foreground">
            <p className="font-semibold mb-2">Tài khoản Demo:</p>
            <p>Admin: admin / 123456</p>
            <p>Staff: staff / 123456</p>
            <p>Customer: customer / 123456</p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="p-3 bg-destructive/10 border border-destructive/30 rounded text-sm text-destructive">
                {error}
              </div>
            )}

            {/* Username */}
            <div>
              <label className="block text-sm font-medium text-foreground mb-2">Tên Đăng Nhập</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="admin, staff hoặc customer"
                className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent transition"
                disabled={isLoading}
              />
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-foreground mb-2">Mật Khẩu</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="123456"
                className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent transition"
                disabled={isLoading}
              />
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-primary text-primary-foreground py-2 rounded-lg font-medium hover:bg-primary/90 transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isLoading ? "Đang đăng nhập..." : "Đăng Nhập"}
            </button>
          </form>

          {/* Back to Home */}
          <div className="mt-6 text-center">
            <p className="text-sm text-muted-foreground">
              Quay lại{" "}
              <Link href="/" className="text-accent hover:underline">
                trang chủ
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
