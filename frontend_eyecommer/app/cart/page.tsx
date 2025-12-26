"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getCart, removeFromCart, updateCartQuantity, getCartTotal } from "@/lib/cart-store"

export default function CartPage() {
  const router = useRouter()
  const [items, setItems] = useState<any[]>([])
  const [total, setTotal] = useState(0)

  useEffect(() => {
    const cartItems = getCart()
    setItems(cartItems)
    setTotal(getCartTotal())
  }, [])

  const handleRemove = (productId: string) => {
    removeFromCart(productId)
    setItems(getCart())
    setTotal(getCartTotal())
  }

  const handleQuantityChange = (productId: string, quantity: number) => {
    updateCartQuantity(productId, quantity)
    setItems(getCart())
    setTotal(getCartTotal())
  }

  const handleCheckout = () => {
    router.push("/checkout")
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <h1 className="text-3xl font-bold mb-8">Shopping Cart</h1>

          {items.length === 0 ? (
            <div className="text-center py-16">
              <p className="text-xl text-muted-foreground mb-6">Your cart is empty</p>
              <Link href="/products">
                <Button>Continue Shopping</Button>
              </Link>
            </div>
          ) : (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Cart Items */}
              <div className="lg:col-span-2">
                <div className="space-y-4">
                  {items.map((item) => (
                    <Card key={item.id}>
                      <CardContent className="p-4">
                        <div className="flex gap-4">
                          <div className="w-24 h-24 bg-muted rounded overflow-hidden">
                            <img
                              src={item.product.image || "/placeholder.svg"}
                              alt={item.product.name}
                              className="w-full h-full object-cover"
                            />
                          </div>
                          <div className="flex-1">
                            <Link href={`/products/${item.product.id}`}>
                              <h3 className="font-semibold hover:text-primary">{item.product.name}</h3>
                            </Link>
                            <p className="text-muted-foreground text-sm mb-3">${item.product.price.toFixed(2)} each</p>
                            <div className="flex items-center justify-between">
                              <div className="flex items-center border border-border rounded">
                                <button
                                  onClick={() => handleQuantityChange(item.productId, item.quantity - 1)}
                                  className="px-2 py-1"
                                >
                                  −
                                </button>
                                <span className="px-3 py-1">{item.quantity}</span>
                                <button
                                  onClick={() => handleQuantityChange(item.productId, item.quantity + 1)}
                                  className="px-2 py-1"
                                >
                                  +
                                </button>
                              </div>
                              <span className="font-semibold">${(item.product.price * item.quantity).toFixed(2)}</span>
                              <button
                                onClick={() => handleRemove(item.productId)}
                                className="text-red-600 hover:text-red-700 text-sm"
                              >
                                Remove
                              </button>
                            </div>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </div>

              {/* Order Summary */}
              <div>
                <Card>
                  <CardContent className="p-6">
                    <h2 className="font-semibold text-lg mb-4">Order Summary</h2>
                    <div className="space-y-2 mb-4 pb-4 border-b border-border">
                      <div className="flex justify-between text-muted-foreground">
                        <span>Subtotal</span>
                        <span>${total.toFixed(2)}</span>
                      </div>
                    </div>
                    <div className="flex justify-between font-semibold text-lg mb-6">
                      <span>Total</span>
                      <span>${total.toFixed(2)}</span>
                    </div>
                    <Button onClick={handleCheckout} className="w-full" size="lg">
                      Proceed to Checkout
                    </Button>
                  </CardContent>
                </Card>
              </div>
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  )
}
