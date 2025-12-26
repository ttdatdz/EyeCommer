// Simple authentication store for demo
// In production, use a real auth system with database

interface User {
  id: string
  username: string
  email?: string
  password: string
  role: "customer" | "staff" | "admin"
  name: string
}

const users: Record<string, User> = {
  "customer": {
    id: "cust_1",
    username: "customer",
    email: "customer@example.com",
    password: "password", // Never store plain text in production!
    role: "customer",
    name: "John Doe",
  },
  "staff": {
    id: "staff_1",
    username: "staff",
    email: "staff@example.com",
    password: "password",
    role: "staff",
    name: "Jane Smith",
  },
  "admin": {
    id: "admin_1",
    username: "admin",
    email: "admin@example.com",
    password: "password",
    role: "admin",
    name: "Admin User",
  },
}

let currentUser: User | null = null

export function login(username: string, password: string) {
  const user = users[username]
  if (user && user.password === password) {
    currentUser = user
    localStorage.setItem("currentUser", JSON.stringify(user))
    return user
  }
  return null
}

export function logout() {
  currentUser = null
  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null
  localStorage.removeItem("currentUser")
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')

  // Try to notify server but don't block logout
  try {
    if (typeof window !== 'undefined') {
      fetch((process.env.NEXT_PUBLIC_API_BASE_URL || '/api') + '/auth/logout', {
        method: 'POST',
        headers: token ? { Authorization: `Bearer ${token}` } : undefined,
        credentials: 'include',
      }).catch(() => {})
    }
  } catch (e) {
    // ignore
  }
}

export function getCurrentUser() {
  if (typeof window !== "undefined") {
    const stored = localStorage.getItem("currentUser")
    if (stored) {
      return JSON.parse(stored)
    }
  }
  return currentUser
}

export function isAuthenticated() {
  return getCurrentUser() !== null
}
