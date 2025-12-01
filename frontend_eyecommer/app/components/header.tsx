"use client"

import Link from "next/link"
import { useRouter } from "next/navigation"
import { ShoppingBag, Menu, LogOut, LogIn, User } from "lucide-react"
import { useAuth } from "../context/auth-context"
import { useState } from "react"

export function Header() {
  const { user, logout, isAuthenticated, loading } = useAuth()
  const router = useRouter()
  const [showMenu, setShowMenu] = useState(false)

  const handleLogout = () => {
    logout()
    router.push("/")
  }

  const handleDashboard = () => {
    if (user?.role === "admin") {
      router.push("/admin")
    } else if (user?.role === "staff") {
      router.push("/staff")
    } else if (user?.role === "customer") {
      router.push("/customer")
    }
  }

  return (
    <header className="sticky top-0 z-50 border-b border-border bg-background/95 backdrop-blur">
      <div className="flex items-center justify-between px-6 py-4 md:px-8">
        {/* Logo */}
        <Link href="/" className="font-serif text-2xl font-light tracking-wide text-foreground">
          OPTICA
        </Link>

        {/* Navigation */}
        <nav className="hidden md:flex items-center gap-8">
          <Link href="/#collections" className="text-sm font-light hover:text-accent transition-colors">
            Bộ sưu tập
          </Link>
          <Link href="/#about" className="text-sm font-light hover:text-accent transition-colors">
            Về chúng tôi
          </Link>
          <Link href="/#contact" className="text-sm font-light hover:text-accent transition-colors">
            Liên hệ
          </Link>
        </nav>

        {/* Cart & Auth & Menu */}
        <div className="flex items-center gap-4">
          {!loading && (
            <>
              {isAuthenticated && user ? (
                <>
                  {/* User Profile Button */}
                  <button
                    onClick={handleDashboard}
                    className="hidden md:flex items-center gap-2 px-4 py-2 text-sm font-light hover:text-accent transition-colors"
                    title={`${user.role} - ${user.username}`}
                  >
                    <User className="w-4 h-4" />
                    <span>{user.username}</span>
                  </button>

                  {/* Logout Button */}
                  <button
                    onClick={handleLogout}
                    className="hidden md:flex items-center gap-2 px-4 py-2 text-sm font-light text-accent hover:text-accent/80 transition-colors"
                  >
                    <LogOut className="w-4 h-4" />
                    <span>Đăng xuất</span>
                  </button>
                </>
              ) : (
                /* Login Button */
                <Link
                  href="/login"
                  className="hidden md:flex items-center gap-2 px-4 py-2 text-sm font-light hover:text-accent transition-colors"
                >
                  <LogIn className="w-4 h-4" />
                  <span>Đăng nhập</span>
                </Link>
              )}
            </>
          )}

          {/* Cart Icon */}
          <button className="p-2 hover:bg-muted rounded-lg transition-colors">
            <ShoppingBag className="w-5 h-5" />
          </button>

          {/* Mobile Menu */}
          <button
            onClick={() => setShowMenu(!showMenu)}
            className="md:hidden p-2 hover:bg-muted rounded-lg transition-colors"
          >
            <Menu className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {showMenu && (
        <div className="md:hidden border-t border-border px-6 py-4 space-y-4">
          <Link href="/#collections" className="block text-sm font-light hover:text-accent transition-colors">
            Bộ sưu tập
          </Link>
          <Link href="/#about" className="block text-sm font-light hover:text-accent transition-colors">
            Về chúng tôi
          </Link>
          <Link href="/#contact" className="block text-sm font-light hover:text-accent transition-colors">
            Liên hệ
          </Link>
          {!loading && (
            <>
              {isAuthenticated && user ? (
                <>
                  <button
                    onClick={handleDashboard}
                    className="block w-full text-left px-4 py-2 text-sm font-light hover:text-accent transition-colors"
                  >
                    Dashboard ({user.username})
                  </button>
                  <button
                    onClick={handleLogout}
                    className="block w-full text-left px-4 py-2 text-sm font-light text-accent hover:text-accent/80 transition-colors"
                  >
                    Đăng xuất
                  </button>
                </>
              ) : (
                <Link href="/login" className="block px-4 py-2 text-sm font-light hover:text-accent transition-colors">
                  Đăng nhập
                </Link>
              )}
            </>
          )}
        </div>
      )}
    </header>
  )
}
