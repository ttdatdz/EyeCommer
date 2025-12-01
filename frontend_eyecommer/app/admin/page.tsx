"use client"

import { ProtectedPage } from "../components/protected-page"

function AdminDashboardContent() {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-card border rounded-lg p-4">
          <h3 className="text-sm font-medium text-muted-foreground">Total Products</h3>
          <p className="text-2xl font-bold mt-2">128</p>
        </div>
        <div className="bg-card border rounded-lg p-4">
          <h3 className="text-sm font-medium text-muted-foreground">Total Orders</h3>
          <p className="text-2xl font-bold mt-2">45</p>
        </div>
        <div className="bg-card border rounded-lg p-4">
          <h3 className="text-sm font-medium text-muted-foreground">Staff Members</h3>
          <p className="text-2xl font-bold mt-2">12</p>
        </div>
        <div className="bg-card border rounded-lg p-4">
          <h3 className="text-sm font-medium text-muted-foreground">Revenue</h3>
          <p className="text-2xl font-bold mt-2">$125K</p>
        </div>
      </div>
    </div>
  )
}

export default function AdminDashboard() {
  return (
    <ProtectedPage requiredRole="admin">
      <AdminDashboardContent />
    </ProtectedPage>
  )
}
