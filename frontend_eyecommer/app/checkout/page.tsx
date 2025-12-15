"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getCart, getCartTotal, clearCart } from "@/lib/cart-store"

export default function CheckoutPage() {
  const router = useRouter()
  const [step, setStep] = useState<"shipping" | "payment">("shipping")
  const [cartItems, setCartItems] = useState<any[]>([])
  const [cartTotal, setCartTotal] = useState(0)

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    address: "",
    city: "",
    state: "",
    postalCode: "",
    country: "Vietnam",
  })

  const [paymentMethod, setPaymentMethod] = useState<"vnpay" | "cod">("cod")
  const [voucherCode, setVoucherCode] = useState("")

  useEffect(() => {
    const items = getCart()
    setCartItems(items)
    setCartTotal(getCartTotal())
  }, [])

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleNextStep = (e: React.FormEvent) => {
    e.preventDefault()
    if (step === "shipping") {
      setStep("payment")
    }
  }

  const handlePlaceOrder = (e: React.FormEvent) => {
    e.preventDefault()

    // Simulate order creation
    const orderId = Math.random().toString(36).substr(2, 9).toUpperCase()

    clearCart()

    // Show success and redirect
    alert(`Order placed successfully! Order ID: ${orderId}`)
    router.push(`/order-confirmation/${orderId}`)
  }

  const shipping = 10.0
  const tax = cartTotal * 0.1
  const discount = voucherCode === "SAVE20" ? cartTotal * 0.2 : 0
  const finalTotal = cartTotal + shipping + tax - discount

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <h1 className="text-3xl font-bold mb-8">Checkout</h1>

          {/* Progress */}
          <div className="flex gap-8 mb-8">
            <div className={`flex-1 ${step === "shipping" ? "text-primary font-semibold" : "text-muted-foreground"}`}>
              1. Shipping Details
            </div>
            <div className={`flex-1 ${step === "payment" ? "text-primary font-semibold" : "text-muted-foreground"}`}>
              2. Payment
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Form */}
            <div className="lg:col-span-2">
              {step === "shipping" ? (
                <form onSubmit={handleNextStep}>
                  <Card>
                    <CardContent className="p-6">
                      <h2 className="text-xl font-semibold mb-6">Shipping Address</h2>

                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <input
                          type="text"
                          name="firstName"
                          placeholder="First Name"
                          value={formData.firstName}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                        <input
                          type="text"
                          name="lastName"
                          placeholder="Last Name"
                          value={formData.lastName}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                      </div>

                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <input
                          type="email"
                          name="email"
                          placeholder="Email"
                          value={formData.email}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                        <input
                          type="tel"
                          name="phone"
                          placeholder="Phone"
                          value={formData.phone}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                      </div>

                      <input
                        type="text"
                        name="address"
                        placeholder="Street Address"
                        value={formData.address}
                        onChange={handleInputChange}
                        required
                        className="w-full px-4 py-2 border border-border rounded bg-card text-foreground mb-4"
                      />

                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <input
                          type="text"
                          name="city"
                          placeholder="City"
                          value={formData.city}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                        <input
                          type="text"
                          name="state"
                          placeholder="State/Province"
                          value={formData.state}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                      </div>

                      <div className="grid grid-cols-2 gap-4 mb-6">
                        <input
                          type="text"
                          name="postalCode"
                          placeholder="Postal Code"
                          value={formData.postalCode}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                        <select
                          name="country"
                          value={formData.country}
                          onChange={handleInputChange}
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        >
                          <option>Vietnam</option>
                          <option>Thailand</option>
                          <option>Singapore</option>
                          <option>Malaysia</option>
                        </select>
                      </div>

                      <Button type="submit" className="w-full" size="lg">
                        Continue to Payment
                      </Button>
                    </CardContent>
                  </Card>
                </form>
              ) : (
                <form onSubmit={handlePlaceOrder}>
                  <Card>
                    <CardContent className="p-6">
                      <h2 className="text-xl font-semibold mb-6">Payment Method</h2>

                      <div className="space-y-3 mb-8">
                        <label className="flex items-center p-4 border border-border rounded cursor-pointer hover:bg-muted">
                          <input
                            type="radio"
                            name="payment"
                            value="cod"
                            checked={paymentMethod === "cod"}
                            onChange={(e) => setPaymentMethod(e.target.value as "cod")}
                            className="mr-3"
                          />
                          <div>
                            <div className="font-semibold">Cash on Delivery</div>
                            <div className="text-sm text-muted-foreground">Pay when you receive your order</div>
                          </div>
                        </label>

                        <label className="flex items-center p-4 border border-border rounded cursor-pointer hover:bg-muted">
                          <input
                            type="radio"
                            name="payment"
                            value="vnpay"
                            checked={paymentMethod === "vnpay"}
                            onChange={(e) => setPaymentMethod(e.target.value as "vnpay")}
                            className="mr-3"
                          />
                          <div>
                            <div className="font-semibold">VNPay</div>
                            <div className="text-sm text-muted-foreground">Secure online payment</div>
                          </div>
                        </label>
                      </div>

                      <div className="flex gap-3 mb-6">
                        <Button type="button" variant="outline" onClick={() => setStep("shipping")}>
                          Back
                        </Button>
                        <Button type="submit" className="flex-1" size="lg">
                          Place Order
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                </form>
              )}
            </div>

            {/* Order Summary */}
            <div>
              <Card className="sticky top-4">
                <CardContent className="p-6">
                  <h2 className="font-semibold text-lg mb-4">Order Summary</h2>

                  <div className="space-y-3 mb-4 pb-4 border-b border-border max-h-64 overflow-y-auto">
                    {cartItems.map((item) => (
                      <div key={item.id} className="flex justify-between text-sm">
                        <span className="text-muted-foreground">
                          {item.product.name} x{item.quantity}
                        </span>
                        <span>${(item.product.price * item.quantity).toFixed(2)}</span>
                      </div>
                    ))}
                  </div>

                  {step === "payment" && (
                    <div className="mb-4 pb-4 border-b border-border">
                      <div className="flex gap-2 mb-2">
                        <input
                          type="text"
                          placeholder="Voucher code"
                          value={voucherCode}
                          onChange={(e) => setVoucherCode(e.target.value.toUpperCase())}
                          className="flex-1 px-3 py-2 border border-border rounded text-sm bg-card text-foreground"
                        />
                        <Button type="button" variant="outline" size="sm">
                          Apply
                        </Button>
                      </div>
                      <p className="text-xs text-muted-foreground">Try: SAVE20</p>
                    </div>
                  )}

                  <div className="space-y-2 mb-4 pb-4 border-b border-border text-sm">
                    <div className="flex justify-between text-muted-foreground">
                      <span>Subtotal</span>
                      <span>${cartTotal.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between text-muted-foreground">
                      <span>Shipping</span>
                      <span>${shipping.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between text-muted-foreground">
                      <span>Tax</span>
                      <span>${tax.toFixed(2)}</span>
                    </div>
                    {discount > 0 && (
                      <div className="flex justify-between text-green-600">
                        <span>Discount</span>
                        <span>-${discount.toFixed(2)}</span>
                      </div>
                    )}
                  </div>

                  <div className="flex justify-between font-semibold text-lg">
                    <span>Total</span>
                    <span>${finalTotal.toFixed(2)}</span>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  )
}
