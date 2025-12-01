import type React from "react"
import { SidebarProvider, Sidebar, SidebarContent, SidebarHeader } from "../components/ui/sidebar"
import Link from "next/link"

export const metadata = {
  title: "Staff Portal",
  description: "Eyewear Shop Staff Portal",
}

export default function StaffLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <SidebarProvider>
      <div className="flex w-full">
        <Sidebar className="border-r">
          <SidebarHeader>
            <Link href="/staff" className="text-lg font-bold">
              Staff
            </Link>
          </SidebarHeader>
          <SidebarContent>
            <nav className="space-y-2">
              <Link href="/staff" className="block px-4 py-2 rounded hover:bg-accent">
                Dashboard
              </Link>
              <Link href="/staff/orders" className="block px-4 py-2 rounded hover:bg-accent">
                Orders
              </Link>
              <Link href="/staff/inventory" className="block px-4 py-2 rounded hover:bg-accent">
                Inventory
              </Link>
              <Link href="/staff/customers" className="block px-4 py-2 rounded hover:bg-accent">
                Customers
              </Link>
            </nav>
          </SidebarContent>
        </Sidebar>
        <main className="flex-1 p-6">{children}</main>
      </div>
    </SidebarProvider>
  )
}
