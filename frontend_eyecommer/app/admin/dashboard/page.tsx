"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getCurrentUser, logout } from "@/lib/auth-store"
import { getAllOrders } from "@/lib/orders-store"
import { getProducts } from "@/lib/store"
import Link from "next/link"
import type { Order, Product } from "@/lib/types"

export default function AdminDashboard() {
  const router = useRouter()
  const [user, setUser] = useState<any>(null)
  const [orders, setOrders] = useState<Order[]>([])
  const [products, setProducts] = useState<Product[]>([])
  const [activeTab, setActiveTab] = useState<"overview" | "products" | "inventory" | "staff" | "customers">("overview")

  useEffect(() => {
    const currentUser = getCurrentUser()
    if (!currentUser || currentUser.role !== "admin") {
      router.push("/login")
      return
    }
    setUser(currentUser)
    setOrders(getAllOrders())
    setProducts(getProducts())
  }, [router])

  const handleLogout = () => {
    logout()
    router.push("/")
  }

  if (!user) return null

  const totalRevenue = orders.reduce((sum, o) => sum + o.total, 0)
  const totalOrders = orders.length
  const completedOrders = orders.filter((o) => o.status === "delivered").length
  const lowStockProducts = products.filter((p) => p.stock < 20).length

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold">Admin Dashboard</h1>
            <Button variant="outline" onClick={handleLogout}>
              Logout
            </Button>
          </div>

          {/* Tabs */}
          <div className="flex gap-4 mb-6 border-b border-border">
            {["overview", "products", "inventory", "staff", "customers"].map((tab) => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab as any)}
                className={`px-4 py-2 font-semibold border-b-2 ${
                  activeTab === tab ? "border-primary text-primary" : "border-transparent text-muted-foreground"
                }`}
              >
                {tab.charAt(0).toUpperCase() + tab.slice(1)}
              </button>
            ))}
          </div>

          {/* Quick Links to Management Pages */}
          <div className="flex gap-4 mb-8">
            <Link href="/admin/products">
              <Button variant="outline">Manage Products</Button>
            </Link>
            <Link href="/admin/staff">
              <Button variant="outline">Manage Staff</Button>
            </Link>
            <Link href="/admin/customers">
              <Button variant="outline">Manage Customers</Button>
            </Link>
          </div>

          {activeTab === "overview" && (
            <>
              {/* KPI Cards */}
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
                <Card>
                  <CardContent className="p-6 text-center">
                    <div className="text-3xl font-bold text-primary mb-1">${totalRevenue.toFixed(2)}</div>
                    <div className="text-muted-foreground">Total Revenue</div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-6 text-center">
                    <div className="text-3xl font-bold text-blue-600 mb-1">{totalOrders}</div>
                    <div className="text-muted-foreground">Total Orders</div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-6 text-center">
                    <div className="text-3xl font-bold text-green-600 mb-1">{completedOrders}</div>
                    <div className="text-muted-foreground">Completed</div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-6 text-center">
                    <div className="text-3xl font-bold text-red-600 mb-1">{lowStockProducts}</div>
                    <div className="text-muted-foreground">Low Stock Items</div>
                  </CardContent>
                </Card>
              </div>

              {/* Recent Orders */}
              <Card>
                <CardContent className="p-6">
                  <h2 className="text-xl font-semibold mb-4">Recent Orders</h2>
                  <div className="overflow-x-auto">
                    <table className="w-full text-sm">
                      <thead>
                        <tr className="border-b border-border">
                          <th className="p-3 text-left">Order ID</th>
                          <th className="p-3 text-left">Customer</th>
                          <th className="p-3 text-right">Amount</th>
                          <th className="p-3 text-left">Status</th>
                          <th className="p-3 text-left">Payment</th>
                        </tr>
                      </thead>
                      <tbody>
                        {orders.slice(0, 5).map((order) => (
                          <tr key={order.id} className="border-b border-border">
                            <td className="p-3 font-semibold">{order.id}</td>
                            <td className="p-3">{order.shippingAddress.name}</td>
                            <td className="p-3 text-right font-semibold">${order.total.toFixed(2)}</td>
                            <td className="p-3">
                              <span className="text-xs px-2 py-1 bg-muted rounded capitalize">{order.status}</span>
                            </td>
                            <td className="p-3">
                              <span className="text-xs px-2 py-1 bg-green-100 text-green-800 rounded capitalize">
                                {order.paymentStatus}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </CardContent>
              </Card>
            </>
          )}

          {activeTab === "products" && (
            <Card>
              <CardContent className="p-6">
                <div className="flex justify-between items-center mb-4">
                  <h2 className="text-xl font-semibold">Products</h2>
                  <Button>Add Product</Button>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="border-b border-border">
                        <th className="p-3 text-left">Name</th>
                        <th className="p-3 text-left">SKU</th>
                        <th className="p-3 text-left">Category</th>
                        <th className="p-3 text-right">Price</th>
                        <th className="p-3 text-left">Stock</th>
                        <th className="p-3 text-left">Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {products.map((product) => (
                        <tr key={product.id} className="border-b border-border">
                          <td className="p-3">{product.name}</td>
                          <td className="p-3 font-mono text-xs">{product.sku}</td>
                          <td className="p-3 capitalize">{product.category}</td>
                          <td className="p-3 text-right font-semibold">${product.price.toFixed(2)}</td>
                          <td className="p-3">
                            <span
                              className={`px-2 py-1 text-xs rounded ${
                                product.stock > 20
                                  ? "bg-green-100 text-green-800"
                                  : product.stock > 0
                                    ? "bg-yellow-100 text-yellow-800"
                                    : "bg-red-100 text-red-800"
                              }`}
                            >
                              {product.stock}
                            </span>
                          </td>
                          <td className="p-3">
                            <Button variant="outline" size="sm">
                              Edit
                            </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === "inventory" && (
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-semibold mb-4">Low Stock Items</h2>
                <div className="space-y-3">
                  {products
                    .filter((p) => p.stock < 20)
                    .map((product) => (
                      <div key={product.id} className="flex justify-between items-center p-4 bg-muted/50 rounded">
                        <div>
                          <h3 className="font-semibold">{product.name}</h3>
                          <p className="text-sm text-muted-foreground">{product.sku}</p>
                        </div>
                        <div className="text-right">
                          <div className="text-lg font-bold">{product.stock}</div>
                          <Button size="sm" variant="outline">
                            Reorder
                          </Button>
                        </div>
                      </div>
                    ))}
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === "staff" && (
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-semibold mb-4">Staff Management</h2>
                <div className="space-y-3">{/* Placeholder for Staff Management Content */}</div>
              </CardContent>
            </Card>
          )}

          {activeTab === "customers" && (
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-semibold mb-4">Customers Management</h2>
                <div className="space-y-3">{/* Placeholder for Customers Management Content */}</div>
              </CardContent>
            </Card>
          )}
        </div>
      </main>

      <Footer />
    </div>
  )
}
