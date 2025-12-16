"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getOrderById } from "@/lib/orders-store"
import type { Order } from "@/lib/types"

export default function OrderDetailPage({ params }: { params: { id: string } }) {
  const [order, setOrder] = useState<Order | null>(null)

  useEffect(() => {
    const foundOrder = getOrderById(params.id)
    setOrder(foundOrder)
  }, [params.id])

  if (!order) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <Header />
        <main className="flex-1 container mx-auto px-4 py-8">
          <p>Order not found</p>
          <Link href="/customer/dashboard">
            <Button className="mt-4">Back to Dashboard</Button>
          </Link>
        </main>
        <Footer />
      </div>
    )
  }

  const getStatusSteps = () => {
    const steps = ["pending", "confirmed", "shipped", "delivered"]
    return steps.map((step) => ({
      step,
      completed: steps.indexOf(step) <= steps.indexOf(order.status),
    }))
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <Link href="/customer/dashboard">
            <Button variant="outline" className="mb-6 bg-transparent">
              Back to Dashboard
            </Button>
          </Link>

          <h1 className="text-3xl font-bold mb-8">Order {order.id}</h1>

          {/* Status Timeline */}
          <Card className="mb-8">
            <CardContent className="p-6">
              <h2 className="font-semibold mb-6">Order Status</h2>
              <div className="flex gap-4">
                {getStatusSteps().map((item, i) => (
                  <div key={item.step} className="flex-1">
                    <div className={`h-2 rounded mb-2 ${item.completed ? "bg-primary" : "bg-muted"}`} />
                    <p className="text-xs text-center text-muted-foreground capitalize">{item.step}</p>
                  </div>
                ))}
              </div>

              {order.trackingNumber && (
                <div className="mt-6 pt-6 border-t border-border">
                  <p className="text-sm text-muted-foreground mb-1">Tracking Number</p>
                  <p className="font-mono font-semibold">{order.trackingNumber}</p>
                </div>
              )}
            </CardContent>
          </Card>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Items */}
            <div className="lg:col-span-2">
              <Card>
                <CardContent className="p-6">
                  <h2 className="font-semibold mb-4">Order Items</h2>
                  <div className="space-y-4">
                    {order.items.map((item) => (
                      <div key={item.productId} className="flex gap-4 pb-4 border-b border-border last:border-b-0">
                        <div className="w-20 h-20 bg-muted rounded overflow-hidden">
                          <img
                            src={item.product.image || "/placeholder.svg"}
                            alt={item.product.name}
                            className="w-full h-full object-cover"
                          />
                        </div>
                        <div className="flex-1">
                          <h3 className="font-semibold">{item.product.name}</h3>
                          <p className="text-sm text-muted-foreground">SKU: {item.product.sku}</p>
                          <p className="text-sm mt-1">Quantity: {item.quantity}</p>
                        </div>
                        <div className="text-right">
                          <p className="font-semibold">${item.price.toFixed(2)}</p>
                          <p className="text-sm text-muted-foreground">${(item.price * item.quantity).toFixed(2)}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Summary */}
            <div>
              <Card className="sticky top-4">
                <CardContent className="p-6">
                  <h2 className="font-semibold mb-4">Order Summary</h2>

                  <div className="space-y-2 mb-4 pb-4 border-b border-border">
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Subtotal</span>
                      <span>${order.subtotal.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Shipping</span>
                      <span>${order.shipping.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Tax</span>
                      <span>${order.tax.toFixed(2)}</span>
                    </div>
                    {order.discount > 0 && (
                      <div className="flex justify-between text-sm text-green-600">
                        <span>Discount</span>
                        <span>-${order.discount.toFixed(2)}</span>
                      </div>
                    )}
                  </div>

                  <div className="flex justify-between font-semibold mb-6">
                    <span>Total</span>
                    <span>${order.total.toFixed(2)}</span>
                  </div>

                  <h3 className="font-semibold text-sm mb-3">Shipping Address</h3>
                  <div className="text-sm text-muted-foreground space-y-1">
                    <p>{order.shippingAddress.name}</p>
                    <p>{order.shippingAddress.address}</p>
                    <p>
                      {order.shippingAddress.city}, {order.shippingAddress.state}
                    </p>
                    <p>{order.shippingAddress.postalCode}</p>
                    <p>{order.shippingAddress.country}</p>
                  </div>

                  <div className="mt-6 pt-6 border-t border-border">
                    <p className="text-xs text-muted-foreground mb-1">Payment Method</p>
                    <p className="font-semibold capitalize mb-2">{order.paymentMethod}</p>
                    <div
                      className={`text-xs font-semibold ${
                        order.paymentStatus === "completed" ? "text-green-600" : "text-yellow-600"
                      }`}
                    >
                      {order.paymentStatus.charAt(0).toUpperCase() + order.paymentStatus.slice(1)}
                    </div>
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
