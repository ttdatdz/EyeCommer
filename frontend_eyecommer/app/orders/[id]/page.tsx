"use client"

import { use, useEffect, useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getOrderByCode, type OrderDetail } from "@/lib/services/orders"

export default function OrderDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const resolvedParams = use(params)
  const router = useRouter()
  const [order, setOrder] = useState<OrderDetail | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadOrder()
  }, [resolvedParams.id])

  const loadOrder = async () => {
    setLoading(true)
    try {
      const response = await getOrderByCode(resolvedParams.id)
      if (response.status === 200 && response.data) {
        setOrder(response.data)
      } else if (response.status === 401) {
        router.push("/login")
      }
    } catch (error) {
      console.error('Failed to load order:', error)
    } finally {
      setLoading(false)
    }
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount)
  }

  const getStatusColor = (status: string) => {
    const normalizedStatus = status?.toLowerCase() || ''
    switch (normalizedStatus) {
      case "pending":
        return "bg-yellow-500"
      case "confirmed":
      case "converted":
        return "bg-blue-500"
      case "shipped":
        return "bg-purple-500"
      case "delivered":
        return "bg-green-500"
      case "cancelled":
        return "bg-red-500"
      default:
        return "bg-gray-500"
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

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <Header />
        <main className="flex-1 container mx-auto px-4 py-8">
          <p className="text-center">Đang tải...</p>
        </main>
        <Footer />
      </div>
    )
  }

  if (!order) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <Header />
        <main className="flex-1 container mx-auto px-4 py-8">
          <p>Không tìm thấy đơn hàng</p>
          <Link href="/customer/dashboard">
            <Button className="mt-4">Quay lại trang chủ</Button>
          </Link>
        </main>
        <Footer />
      </div>
    )
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <Link href="/customer/dashboard">
            <Button variant="outline" className="mb-6 bg-transparent">
              ← Quay lại
            </Button>
          </Link>

          <h1 className="text-3xl font-bold mb-8">Chi tiết đơn hàng #{order.orderCode}</h1>

          {/* Status Card */}
          <Card className="mb-8">
            <CardContent className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div>
                  <p className="text-sm text-muted-foreground mb-1">Trạng thái đơn hàng</p>
                  <span className={`inline-block px-3 py-1 rounded-full text-white text-sm font-semibold ${getStatusColor(order.status)}`}>
                    {order.status}
                  </span>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground mb-1">Trạng thái thanh toán</p>
                  <span className={`font-semibold capitalize ${getPaymentStatusColor(order.paymentStatus)}`}>
                    {order.paymentStatus}
                  </span>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground mb-1">Phương thức thanh toán</p>
                  <p className="font-semibold">{order.paymentMethod}</p>
                </div>
              </div>

              <div className="mt-6 pt-6 border-t border-border">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-muted-foreground mb-1">Ngày đặt hàng</p>
                    <p className="font-semibold">{new Date(order.createdAt).toLocaleString('vi-VN')}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground mb-1">Địa chỉ giao hàng</p>
                    <p className="font-semibold">{order.addressDetail}</p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Items */}
            <div className="lg:col-span-2">
              <Card>
                <CardContent className="p-6">
                  <h2 className="font-semibold mb-4">Sản phẩm đã đặt</h2>
                  <div className="space-y-4">
                    {order.items && order.items.length > 0 ? (
                      order.items.map((item) => (
                        <div key={item.id} className="flex gap-4 pb-4 border-b border-border last:border-b-0">
                          <div className="w-20 h-20 bg-muted rounded overflow-hidden">
                            <img
                              src={item.imageUrl || "/placeholder.svg"}
                              alt={item.productName}
                              className="w-full h-full object-cover"
                            />
                          </div>
                          <div className="flex-1">
                            <h3 className="font-semibold">{item.productName}</h3>
                            <p className="text-sm text-muted-foreground">SKU: {item.variantName}</p>
                            <p className="text-sm mt-1">Số lượng: {item.quantity}</p>
                          </div>
                          <div className="text-right">
                            <p className="font-semibold">{formatCurrency(item.priceAtPurchase)}</p>
                            <p className="text-sm text-muted-foreground">Tổng: {formatCurrency(item.lineTotal)}</p>
                          </div>
                        </div>
                      ))
                    ) : (
                      <p className="text-center text-muted-foreground">Không có sản phẩm</p>
                    )}
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Summary */}
            <div>
              <Card>
                <CardContent className="p-6">
                  <h2 className="font-semibold mb-4">Tổng đơn hàng</h2>
                  <div className="space-y-3">
                    <div className="flex justify-between text-sm">
                      <span className="text-muted-foreground">Tạm tính</span>
                      <span className="font-medium">{formatCurrency(order.totalAmount)}</span>
                    </div>
                    <div className="flex justify-between text-sm pt-3 border-t border-border">
                      <span className="font-semibold">Tổng cộng</span>
                      <span className="font-bold text-lg">{formatCurrency(order.finalAmount)}</span>
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
