"use client"

import { ProtectedPage } from "../components/protected-page"

function StaffDashboardContent() {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Staff Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div className="bg-card border rounded-lg p-4">
          <h3 className="text-sm font-medium text-muted-foreground">Today's Orders</h3>
          <p className="text-2xl font-bold mt-2">8</p>
        </div>
        <div className="bg-card border rounded-lg p-4">
          <h3 className="text-sm font-medium text-muted-foreground">Pending Orders</h3>
          <p className="text-2xl font-bold mt-2">3</p>
        </div>
        <div className="bg-card border rounded-lg p-4">
          <h3 className="text-sm font-medium text-muted-foreground">Low Stock Items</h3>
          <p className="text-2xl font-bold mt-2">5</p>
        </div>
      </div>
    </div>
  )
}

export default function StaffDashboard() {
  return (
    <ProtectedPage requiredRole="staff">
      <StaffDashboardContent />
    </ProtectedPage>
  )
}
