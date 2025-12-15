"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { getCart } from "@/lib/cart-store"
import { getCurrentUser } from "@/lib/auth-store"

export default function Header() {
  const [cartCount, setCartCount] = useState(0)
  const [user, setUser] = useState<any>(null)

  useEffect(() => {
    const items = getCart()
    setCartCount(items.length)
    const currentUser = getCurrentUser()
    setUser(currentUser)
  }, [])

  return (
    <header className="bg-card border-b border-border sticky top-0 z-50">
      <div className="container mx-auto px-4 py-4 flex items-center justify-between">
        <Link href="/" className="text-2xl font-bold text-primary">
          VisionHub
        </Link>

        <nav className="hidden md:flex gap-8">
          <Link href="/" className="text-foreground hover:text-primary transition">
            Home
          </Link>
          <Link href="/products" className="text-foreground hover:text-primary transition">
            Products
          </Link>
          <Link href="/about" className="text-foreground hover:text-primary transition">
            About
          </Link>
          <Link href="/contact" className="text-foreground hover:text-primary transition">
            Contact
          </Link>
        </nav>

        <div className="flex items-center gap-4">
          {user && (
            <Link href="/notifications" className="relative">
              <Button variant="ghost">🔔</Button>
            </Link>
          )}

          <Link href="/cart" className="relative">
            <Button variant="ghost">
              🛒 Cart
              {cartCount > 0 && (
                <span className="absolute -top-2 -right-2 bg-accent text-accent-foreground text-xs rounded-full w-5 h-5 flex items-center justify-center">
                  {cartCount}
                </span>
              )}
            </Button>
          </Link>

          {user ? (
            <Link href={`/${user.role}/dashboard`}>
              <Button variant="outline">{user.name}</Button>
            </Link>
          ) : (
            <div className="flex gap-2">
              <Link href="/register">
                <Button variant="ghost">Register</Button>
              </Link>
              <Link href="/login">
                <Button variant="outline">Login</Button>
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}
