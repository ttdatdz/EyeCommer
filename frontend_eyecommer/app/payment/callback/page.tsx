"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"

export default function PaymentCallbackPage() {
  const searchParams = useSearchParams()
  const [status, setStatus] = useState<"success" | "failed" | "unknown">("unknown")

  useEffect(() => {
    const responseCode = searchParams.get("vnp_ResponseCode")
    if (responseCode === "00") {
      setStatus("success")
    } else {
      setStatus("failed")
    }
  }, [searchParams])

  if (status === "success") {
    const orderId = searchParams.get("vnp_TxnRef")
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <Header />
        <main className="flex-1 py-16">
          <div className="container mx-auto px-4 max-w-lg">
            <div className="text-center mb-8">
              <div className="text-6xl mb-4">✓</div>
              <h1 className="text-3xl font-bold mb-2">Payment Successful!</h1>
              <p className="text-muted-foreground">Your payment has been processed</p>
            </div>

            <Card>
              <CardContent className="p-8 text-center">
                <p className="text-muted-foreground mb-6">
                  Your order will be prepared for shipment. You'll receive a confirmation email shortly.
                </p>
                <div className="flex flex-col gap-3">
                  <Link href={`/orders/${orderId}`}>
                    <Button className="w-full">View Order</Button>
                  </Link>
                  <Link href="/customer/dashboard">
                    <Button variant="outline" className="w-full bg-transparent">
                      Back to Dashboard
                    </Button>
                  </Link>
                </div>
              </CardContent>
            </Card>
          </div>
        </main>
        <Footer />
      </div>
    )
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />
      <main className="flex-1 py-16">
        <div className="container mx-auto px-4 max-w-lg">
          <div className="text-center mb-8">
            <div className="text-6xl mb-4">✗</div>
            <h1 className="text-3xl font-bold mb-2">Payment Failed</h1>
            <p className="text-muted-foreground">Something went wrong with your payment</p>
          </div>

          <Card>
            <CardContent className="p-8 text-center">
              <p className="text-muted-foreground mb-6">Please try again or contact support for assistance.</p>
              <Link href="/checkout">
                <Button className="w-full">Retry Payment</Button>
              </Link>
            </CardContent>
          </Card>
        </div>
      </main>
      <Footer />
    </div>
  )
}
