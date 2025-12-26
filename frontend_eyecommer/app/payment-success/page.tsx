"use client"

import { useEffect, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { clearCart } from "@/lib/cart-store"

export default function PaymentSuccessPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const [orderCode, setOrderCode] = useState<string>("")

  useEffect(() => {
    const code = searchParams.get("orderCode")
    if (code) {
      setOrderCode(code)
      // Clear cart after successful payment
      clearCart()
    }
  }, [searchParams])

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1 flex items-center justify-center py-16">
        <Card className="w-full max-w-md">
          <CardContent className="p-8 text-center">
            <div className="mb-6">
              <svg
                className="mx-auto h-16 w-16 text-green-500"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>

            <h1 className="text-2xl font-bold mb-2 text-green-600">
              Thanh toán thành công!
            </h1>
            
            {orderCode && (
              <p className="text-muted-foreground mb-6">
                Mã đơn hàng: <span className="font-semibold">{orderCode}</span>
              </p>
            )}

            <p className="text-muted-foreground mb-8">
              Cảm ơn bạn đã đặt hàng. Đơn hàng của bạn đang được xử lý.
            </p>

            <div className="flex flex-col gap-3">
              <Button onClick={() => router.push(`/orders/${orderCode}`)}>
                Xem chi tiết đơn hàng
              </Button>
              <Button variant="outline" onClick={() => router.push("/products")}>
                Tiếp tục mua sắm
              </Button>
            </div>
          </CardContent>
        </Card>
      </main>

      <Footer />
    </div>
  )
}
