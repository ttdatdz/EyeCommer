"use client"

import type React from "react"
import { createContext, useContext, useState, useEffect } from "react"

export interface User {
  username: string
  role: "admin" | "staff" | "customer"
}

interface AuthContextType {
  user: User | null
  loading: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => void
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

// Predefined users with roles
const USERS_DB: Record<string, { password: string; role: "admin" | "staff" | "customer" }> = {
  admin: { password: "123456", role: "admin" },
  staff: { password: "123456", role: "staff" },
  customer: { password: "123456", role: "customer" },
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)

  // Check if user is already logged in on mount
  useEffect(() => {
    const storedUser = localStorage.getItem("user")
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser))
      } catch (error) {
        console.error("[v0] Error parsing stored user:", error)
      }
    }
    setLoading(false)
  }, [])

  const login = async (username: string, password: string) => {
    const userRecord = USERS_DB[username]

    if (!userRecord || userRecord.password !== password) {
      throw new Error("Sai tên đăng nhập hoặc mật khẩu")
    }

    const newUser: User = {
      username,
      role: userRecord.role,
    }

    setUser(newUser)
    localStorage.setItem("user", JSON.stringify(newUser))
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem("user")
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
