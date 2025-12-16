"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getCurrentUser, logout } from "@/lib/auth-store"
import { getOrdersByCustomer } from "@/lib/orders-store"
import type { Order } from "@/lib/types"

export default function CustomerDashboard() {
  const router = useRouter()
  const [user, setUser] = useState<any>(null)
  const [orders, setOrders] = useState<Order[]>([])

  useEffect(() => {
    const currentUser = getCurrentUser()
    if (!currentUser || currentUser.role !== "customer") {
      router.push("/login")
      return
    }
    setUser(currentUser)
    const userOrders = getOrdersByCustomer(currentUser.id)
    setOrders(userOrders)
  }, [router])

  const handleLogout = () => {
    logout()
    router.push("/")
  }

  if (!user) return null

  const getStatusColor = (status: string) => {
    switch (status) {
      case "pending":
        return "text-yellow-600"
      case "confirmed":
        return "text-blue-600"
      case "shipped":
        return "text-purple-600"
      case "delivered":
        return "text-green-600"
      default:
        return "text-gray-600"
    }
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold">My Dashboard</h1>
            <Button variant="outline" onClick={handleLogout}>
              Logout
            </Button>
          </div>

          {/* Profile Card */}
          <Card className="mb-8">
            <CardContent className="p-6">
              <div className="flex justify-between items-start">
                <div>
                  <h2 className="text-2xl font-bold mb-2">{user.name}</h2>
                  <p className="text-muted-foreground mb-4">{user.email}</p>
                  <p className="text-sm text-muted-foreground">Account Type: {user.role}</p>
                </div>
                <Button variant="outline">Edit Profile</Button>
              </div>
            </CardContent>
          </Card>

          {/* Quick Stats */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
            <Card>
              <CardContent className="p-6 text-center">
                <div className="text-3xl font-bold text-primary mb-1">{orders.length}</div>
                <div className="text-muted-foreground">Total Orders</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6 text-center">
                <div className="text-3xl font-bold text-accent mb-1">
                  ${orders.reduce((sum, o) => sum + o.total, 0).toFixed(2)}
                </div>
                <div className="text-muted-foreground">Total Spent</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6 text-center">
                <div className="text-3xl font-bold text-green-600 mb-1">
                  {orders.filter((o) => o.status === "delivered").length}
                </div>
                <div className="text-muted-foreground">Delivered</div>
              </CardContent>
            </Card>
          </div>

          {/* Orders */}
          <div>
            <h2 className="text-2xl font-bold mb-4">Order History</h2>

            {orders.length === 0 ? (
              <Card>
                <CardContent className="p-8 text-center text-muted-foreground">
                  <p>No orders yet. Start shopping!</p>
                  <Link href="/products">
                    <Button className="mt-4">Browse Products</Button>
                  </Link>
                </CardContent>
              </Card>
            ) : (
              <div className="space-y-4">
                {orders.map((order) => (
                  <Card key={order.id} className="hover:shadow-md transition-shadow">
                    <CardContent className="p-6">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <h3 className="font-semibold text-lg">{order.id}</h3>
                          <p className="text-sm text-muted-foreground">
                            {new Date(order.createdAt).toLocaleDateString()}
                          </p>
                        </div>
                        <span className={`font-semibold capitalize ${getStatusColor(order.status)}`}>
                          {order.status}
                        </span>
                      </div>

                      <div className="mb-4">
                        <p className="text-sm text-muted-foreground mb-2">{order.items.length} item(s)</p>
                        {order.items.slice(0, 2).map((item) => (
                          <p key={item.productId} className="text-sm">
                            {item.product.name} x{item.quantity}
                          </p>
                        ))}
                        {order.items.length > 2 && (
                          <p className="text-sm text-muted-foreground">+{order.items.length - 2} more</p>
                        )}
                      </div>

                      <div className="flex justify-between items-center pt-4 border-t border-border">
                        <div className="text-lg font-bold">${order.total.toFixed(2)}</div>
                        <Link href={`/orders/${order.id}`}>
                          <Button variant="outline" size="sm">
                            View Details
                          </Button>
                        </Link>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>

      <Footer />
    </div>
  )
}
