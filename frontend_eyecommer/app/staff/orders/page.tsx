"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getAllOrders, confirmOrder, deleteOrder, type AdminOrderItem } from "@/lib/services/admin-orders"
import { Check, Trash2, Eye } from "lucide-react"
import Link from "next/link"

export default function StaffOrdersPage() {
  const router = useRouter()
  const [orders, setOrders] = useState<AdminOrderItem[]>([])
  const [loading, setLoading] = useState(true)
  const [actionLoading, setActionLoading] = useState<string | null>(null)

  useEffect(() => {
    const accessToken = localStorage.getItem('accessToken')
    if (!accessToken) {
      router.push("/login")
      return
    }
    loadOrders()
  }, [router])

  const loadOrders = async () => {
    try {
      setLoading(true)
      const data = await getAllOrders()
      setOrders(data)
    } catch (error) {
      console.error('Lỗi khi tải danh sách đơn hàng:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleConfirm = async (orderCode: string) => {
    try {
      setActionLoading(orderCode)
      await confirmOrder(orderCode)
      await loadOrders()
    } catch (error: any) {
      alert(error.message || 'Không thể xác nhận đơn hàng')
    } finally {
      setActionLoading(null)
    }
  }

  const handleDelete = async (orderCode: string) => {
    try {
      setActionLoading(orderCode)
      await deleteOrder(orderCode)
      await loadOrders()
    } catch (error: any) {
      alert(error.message || 'Không thể xóa đơn hàng')
    } finally {
      setActionLoading(null)
    }
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(amount)
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('vi-VN')
  }

  const getStatusBadge = (status: string) => {
    const variants: Record<string, "default" | "secondary" | "destructive" | "outline"> = {
      PENDING: "secondary",
      CONVERTED: "default",
      CANCELLED: "destructive",
    }
    const labels: Record<string, string> = {
      PENDING: "Chờ xử lý",
      CONVERTED: "Đã xác nhận",
      CANCELLED: "Đã hủy",
    }
    return (
      <Badge variant={variants[status] || "default"} className={status === 'CONVERTED' ? 'bg-green-600' : ''}>
        {labels[status] || status}
      </Badge>
    )
  }

  const getPaymentStatusBadge = (status: string) => {
    const variants: Record<string, "default" | "secondary" | "destructive" | "outline"> = {
      UNPAID: "secondary",
      PAID: "default",
      FAILED: "destructive",
    }
    const labels: Record<string, string> = {
      UNPAID: "Chưa thanh toán",
      PAID: "Đã thanh toán",
      FAILED: "Thanh toán thất bại",
    }
    return (
      <Badge variant={variants[status] || "default"} className={status === 'PAID' ? 'bg-green-600' : ''}>
        {labels[status] || status}
      </Badge>
    )
  }

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <Header />
        <main className="flex-1 flex items-center justify-center">
          <p>Đang tải...</p>
        </main>
        <Footer />
      </div>
    )
  }

  const totalOrders = orders.length
  const pendingOrders = orders.filter(o => o.status === 'PENDING').length
  const totalRevenue = orders.filter(o => o.paymentStatus === 'PAID').reduce((sum, o) => sum + o.finalAmount, 0)

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <div className="flex items-center justify-between mb-6">
            <h1 className="text-3xl font-bold">Quản lý đơn hàng</h1>
            <Link href="/staff/dashboard">
              <Button variant="outline">Quay lại Dashboard</Button>
            </Link>
          </div>

          {/* Statistics */}
          <div className="grid gap-4 md:grid-cols-3 mb-6">
            <Card>
              <CardHeader className="pb-3">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  Tổng đơn hàng
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{totalOrders}</div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="pb-3">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  Đơn chờ xử lý
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{pendingOrders}</div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className="pb-3">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  Tổng doanh thu
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{formatCurrency(totalRevenue)}</div>
              </CardContent>
            </Card>
          </div>

          {/* Orders Table */}
          <Card>
            <CardHeader>
              <CardTitle>Danh sách đơn hàng</CardTitle>
            </CardHeader>
            <CardContent>
              {orders.length === 0 ? (
                <p className="text-center text-muted-foreground py-8">Chưa có đơn hàng nào</p>
              ) : (
                <div className="overflow-x-auto">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Mã đơn</TableHead>
                        <TableHead>Khách hàng</TableHead>
                        <TableHead>Trạng thái</TableHead>
                        <TableHead>Thanh toán</TableHead>
                        <TableHead>Số tiền</TableHead>
                        <TableHead>Ngày tạo</TableHead>
                        <TableHead>Số SP</TableHead>
                        <TableHead className="text-right">Thao tác</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {orders.map((order) => (
                        <TableRow key={order.id}>
                          <TableCell className="font-medium">{order.orderCode}</TableCell>
                          <TableCell>{order.username}</TableCell>
                          <TableCell>{getStatusBadge(order.status)}</TableCell>
                          <TableCell>{getPaymentStatusBadge(order.paymentStatus)}</TableCell>
                          <TableCell>{formatCurrency(order.finalAmount)}</TableCell>
                          <TableCell>{formatDate(order.createdAt)}</TableCell>
                          <TableCell>{order.itemCount}</TableCell>
                          <TableCell className="text-right">
                            <div className="flex justify-end gap-2">
                              <Link href={`/orders/${order.orderCode}`}>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  title="Xem chi tiết"
                                >
                                  <Eye className="h-4 w-4" />
                                </Button>
                              </Link>
                              
                              {order.status === 'PENDING' && (
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  onClick={() => handleConfirm(order.orderCode)}
                                  disabled={actionLoading === order.orderCode}
                                  title="Xác nhận đơn hàng"
                                >
                                  <Check className="h-4 w-4 text-green-600" />
                                </Button>
                              )}
                              
                              <AlertDialog>
                                <AlertDialogTrigger asChild>
                                  <Button
                                    variant="ghost"
                                    size="sm"
                                    disabled={actionLoading === order.orderCode}
                                    title="Xóa đơn hàng"
                                  >
                                    <Trash2 className="h-4 w-4 text-red-600" />
                                  </Button>
                                </AlertDialogTrigger>
                                <AlertDialogContent>
                                  <AlertDialogHeader>
                                    <AlertDialogTitle>Xác nhận xóa đơn hàng</AlertDialogTitle>
                                    <AlertDialogDescription>
                                      Bạn có chắc chắn muốn xóa đơn hàng {order.orderCode}? Hành động này không thể hoàn tác.
                                    </AlertDialogDescription>
                                  </AlertDialogHeader>
                                  <AlertDialogFooter>
                                    <AlertDialogCancel>Hủy</AlertDialogCancel>
                                    <AlertDialogAction
                                      onClick={() => handleDelete(order.orderCode)}
                                      className="bg-red-600 hover:bg-red-700"
                                    >
                                      Xóa
                                    </AlertDialogAction>
                                  </AlertDialogFooter>
                                </AlertDialogContent>
                              </AlertDialog>
                            </div>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </main>

      <Footer />
    </div>
  )
}
