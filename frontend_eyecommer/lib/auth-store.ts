// Simple authentication store for demo
// In production, use a real auth system with database

interface User {
  id: string
  email: string
  password: string
  role: "customer" | "staff" | "admin"
  name: string
}

const users: Record<string, User> = {
  "customer@example.com": {
    id: "cust_1",
    email: "customer@example.com",
    password: "password", // Never store plain text in production!
    role: "customer",
    name: "John Doe",
  },
  "staff@example.com": {
    id: "staff_1",
    email: "staff@example.com",
    password: "password",
    role: "staff",
    name: "Jane Smith",
  },
  "admin@example.com": {
    id: "admin_1",
    email: "admin@example.com",
    password: "password",
    role: "admin",
    name: "Admin User",
  },
}

let currentUser: User | null = null

export function login(email: string, password: string) {
  const user = users[email]
  if (user && user.password === password) {
    currentUser = user
    localStorage.setItem("currentUser", JSON.stringify(user))
    return user
  }
  return null
}

export function logout() {
  currentUser = null
  localStorage.removeItem("currentUser")
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
