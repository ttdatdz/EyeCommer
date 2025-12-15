"use client"

import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"

export default function OrderConfirmationPage({ params }: { params: { id: string } }) {
  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1 py-16">
        <div className="container mx-auto px-4 max-w-lg">
          <div className="text-center mb-8">
            <div className="text-6xl mb-4">✓</div>
            <h1 className="text-3xl font-bold mb-2">Order Confirmed!</h1>
            <p className="text-muted-foreground">Thank you for your purchase</p>
          </div>

          <Card className="mb-8">
            <CardContent className="p-8 text-center">
              <p className="text-muted-foreground mb-2">Order Number</p>
              <p className="text-2xl font-bold mb-8">{params.id}</p>

              <div className="space-y-3 text-left mb-8 pb-8 border-b border-border">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Status</span>
                  <span className="font-semibold text-green-600">Pending Confirmation</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Expected Delivery</span>
                  <span className="font-semibold">3-5 business days</span>
                </div>
              </div>

              <p className="text-sm text-muted-foreground mb-6">
                A confirmation email has been sent to your email address with order details and tracking information.
              </p>

              <div className="flex flex-col gap-3">
                <Link href={`/orders/${params.id}`}>
                  <Button className="w-full">Track Order</Button>
                </Link>
                <Link href="/products">
                  <Button variant="outline" className="w-full bg-transparent">
                    Continue Shopping
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
