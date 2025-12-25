"use client"

import { useEffect, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"

export default function PaymentFailPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const [orderCode, setOrderCode] = useState<string>("")
  const [message, setMessage] = useState<string>("")

  useEffect(() => {
    const code = searchParams.get("orderCode")
    const msg = searchParams.get("message")
    
    if (code) setOrderCode(code)
    if (msg) setMessage(msg)
  }, [searchParams])

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1 flex items-center justify-center py-16">
        <Card className="w-full max-w-md">
          <CardContent className="p-8 text-center">
            <div className="mb-6">
              <svg
                className="mx-auto h-16 w-16 text-red-500"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            </div>

            <h1 className="text-2xl font-bold mb-2 text-red-600">
              Thanh toán thất bại
            </h1>
            
            {message && (
              <p className="text-muted-foreground mb-4">
                {message}
              </p>
            )}

            {orderCode && (
              <p className="text-muted-foreground mb-6">
                Mã đơn hàng: <span className="font-semibold">{orderCode}</span>
              </p>
            )}

            <p className="text-muted-foreground mb-8">
              Đơn hàng của bạn chưa được thanh toán. Vui lòng thử lại.
            </p>

            <div className="flex flex-col gap-3">
              <Button onClick={() => router.push("/checkout")}>
                Thử lại
              </Button>
              <Button variant="outline" onClick={() => router.push("/cart")}>
                Quay lại giỏ hàng
              </Button>
            </div>
          </CardContent>
        </Card>
      </main>

      <Footer />
    </div>
  )
}
