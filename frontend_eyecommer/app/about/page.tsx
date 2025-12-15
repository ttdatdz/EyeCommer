"use client"

import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"

export default function AboutPage() {
  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-16">
          <h1 className="text-4xl font-bold mb-4 text-center">About VisionHub</h1>
          <p className="text-center text-muted-foreground max-w-2xl mx-auto mb-12">
            Your trusted destination for premium eyewear since 2020
          </p>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
            <Card>
              <CardContent className="p-8 text-center">
                <div className="text-4xl mb-4">100%</div>
                <h3 className="font-semibold mb-2">Authentic Products</h3>
                <p className="text-sm text-muted-foreground">
                  All items are 100% authentic and sourced from authorized distributors
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="p-8 text-center">
                <div className="text-4xl mb-4">24/7</div>
                <h3 className="font-semibold mb-2">Customer Support</h3>
                <p className="text-sm text-muted-foreground">
                  Our dedicated team is always ready to help with any questions
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="p-8 text-center">
                <div className="text-4xl mb-4">30</div>
                <h3 className="font-semibold mb-2">Day Returns</h3>
                <p className="text-sm text-muted-foreground">
                  Not satisfied? Full refund within 30 days, no questions asked
                </p>
              </CardContent>
            </Card>
          </div>

          <Card className="mb-8 bg-primary text-primary-foreground">
            <CardContent className="p-12 text-center">
              <h2 className="text-3xl font-bold mb-4">Our Mission</h2>
              <p className="text-lg mb-6 max-w-2xl mx-auto">
                To provide premium eyewear that combines style, quality, and affordability, helping everyone find the
                perfect frames for their unique vision and personality.
              </p>
            </CardContent>
          </Card>

          <div className="text-center">
            <Link href="/products">
              <Button size="lg">Shop Our Collection</Button>
            </Link>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  )
}
