import type React from "react"
import { Header } from "../components/header"
import { Footer } from "../components/footer"

export const metadata = {
  title: "Eyewear Shop - Customer Portal",
  description: "Your eyewear shop customer portal",
}

export default function CustomerLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="min-h-screen bg-background flex flex-col">
      <Header />
      <main className="flex-1 container mx-auto px-4 py-6">{children}</main>
      <Footer />
    </div>
  )
}
