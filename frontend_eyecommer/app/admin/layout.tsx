import type React from "react"
import { SidebarProvider, Sidebar, SidebarContent, SidebarHeader } from "../components/ui/sidebar"
import Link from "next/link"

export const metadata = {
  title: "Admin Dashboard",
  description: "Eyewear Shop Admin Panel",
}

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <SidebarProvider>
      <div className="flex w-full">
        <Sidebar className="border-r">
          <SidebarHeader>
            <Link href="/admin" className="text-lg font-bold">
              Admin
            </Link>
          </SidebarHeader>
          <SidebarContent>
            <nav className="space-y-2">
              <Link href="/admin" className="block px-4 py-2 rounded hover:bg-accent">
                Dashboard
              </Link>
              <Link href="/admin/products" className="block px-4 py-2 rounded hover:bg-accent">
                Products
              </Link>
              <Link href="/admin/orders" className="block px-4 py-2 rounded hover:bg-accent">
                Orders
              </Link>
              <Link href="/admin/staff" className="block px-4 py-2 rounded hover:bg-accent">
                Staff Management
              </Link>
              <Link href="/admin/settings" className="block px-4 py-2 rounded hover:bg-accent">
                Settings
              </Link>
            </nav>
          </SidebarContent>
        </Sidebar>
        <main className="flex-1 p-6">{children}</main>
      </div>
    </SidebarProvider>
  )
}
