"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { logout } from "@/lib/auth-store"
import { getUserOrders, type OrderListItem } from "@/lib/services/orders"

export default function CustomerDashboard() {
  const router = useRouter()
  const [user, setUser] = useState<any>(null)
  const [orders, setOrders] = useState<OrderListItem[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      router.push("/login")
      return
    }

    // Load orders from API
    loadOrders()
  }, [router])

  const loadOrders = async () => {
    setLoading(true)
    try {
      const response = await getUserOrders()
      if (response.status === 200) {
        setOrders(response.data)
      } else if (response.status === 401) {
        router.push("/login")
      }
    } catch (error) {
      console.error('Failed to load orders:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    logout()
    router.push("/")
  }

  const getStatusColor = (status: string) => {
    const normalizedStatus = status?.toLowerCase() || ''
    switch (normalizedStatus) {
      case "pending":
        return "text-yellow-600"
      case "confirmed":
      case "converted":
        return "text-blue-600"
      case "shipped":
        return "text-purple-600"
      case "delivered":
        return "text-green-600"
      case "cancelled":
        return "text-red-600"
      default:
        return "text-gray-600"
    }
  }

  const getPaymentStatusColor = (status: string) => {
    const normalizedStatus = status?.toLowerCase() || ''
    switch (normalizedStatus) {
      case "paid":
        return "text-green-600"
      case "unpaid":
        return "text-yellow-600"
      case "failed":
        return "text-red-600"
      default:
        return "text-gray-600"
    }
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
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
                  <h2 className="text-2xl font-bold mb-2">Thông tin tài khoản</h2>
                  <p className="text-sm text-muted-foreground">Quản lý đơn hàng và thông tin cá nhân</p>
                </div>
                <Button variant="outline" onClick={handleLogout}>
                  Đăng xuất
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* Quick Stats */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
            <Card>
              <CardContent className="p-6 text-center">
                <div className="text-3xl font-bold text-primary mb-1">{orders.length}</div>
                <div className="text-muted-foreground">Tổng đơn hàng</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6 text-center">
                <div className="text-xl font-bold text-accent mb-1">
                  {formatCurrency(orders.reduce((sum, o) => sum + (o.finalAmount || 0), 0))}
                </div>
                <div className="text-muted-foreground">Tổng chi tiêu</div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6 text-center">
                <div className="text-3xl font-bold text-green-600 mb-1">
                  {orders.filter((o) => o.paymentStatus?.toLowerCase() === "paid").length}
                </div>
                <div className="text-muted-foreground">Đã thanh toán</div>
              </CardContent>
            </Card>
          </div>

          {/* Orders */}
          <div>
            <h2 className="text-2xl font-bold mb-4">Lịch sử đơn hàng</h2>

            {loading ? (
              <Card>
                <CardContent className="p-8 text-center text-muted-foreground">
                  <p>Đang tải...</p>
                </CardContent>
              </Card>
            ) : orders.length === 0 ? (
              <Card>
                <CardContent className="p-8 text-center text-muted-foreground">
                  <p>Chưa có đơn hàng nào. Bắt đầu mua sắm!</p>
                  <Link href="/products">
                    <Button className="mt-4">Xem sản phẩm</Button>
                  </Link>
                </CardContent>
              </Card>
            ) : (
              <div className="space-y-4">
                {orders.map((order) => (
                  <Card key={order.orderCode} className="hover:shadow-md transition-shadow">
                    <CardContent className="p-6">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <h3 className="font-semibold text-lg">#{order.orderCode}</h3>
                          <p className="text-sm text-muted-foreground">
                            {new Date(order.createdAt).toLocaleString('vi-VN')}
                          </p>
                        </div>
                        <div className="text-right">
                          <span className={`font-semibold capitalize block ${getStatusColor(order.status)}`}>
                            {order.status}
                          </span>
                          <span className={`text-sm capitalize ${getPaymentStatusColor(order.paymentStatus)}`}>
                            {order.paymentStatus}
                          </span>
                        </div>
                      </div>

                      <div className="mb-4">
                        <p className="text-sm text-muted-foreground mb-2">
                          Phương thức: {order.paymentMethod}
                        </p>
                      </div>

                      <div className="flex justify-between items-center pt-4 border-t border-border">
                        <div className="text-lg font-bold">{formatCurrency(order.finalAmount || order.totalAmount)}</div>
                        <Link href={`/orders/${order.orderCode}`}>
                          <Button variant="outline" size="sm">
                            Xem chi tiết
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
