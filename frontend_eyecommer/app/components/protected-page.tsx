"use client"

import type React from "react"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "../context/auth-context"

interface ProtectedPageProps {
  requiredRole: "admin" | "staff" | "customer"
  children: React.ReactNode
}

export function ProtectedPage({ requiredRole, children }: ProtectedPageProps) {
  const { user, loading, isAuthenticated } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (!loading && !isAuthenticated) {
      router.push("/login")
      return
    }

    if (!loading && user && user.role !== requiredRole) {
      router.push("/")
      return
    }
  }, [loading, isAuthenticated, user, requiredRole, router])

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <p className="text-muted-foreground">Đang tải...</p>
      </div>
    )
  }

  if (!isAuthenticated || user?.role !== requiredRole) {
    return null
  }

  return <>{children}</>
}
