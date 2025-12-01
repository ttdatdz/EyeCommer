"use client"

import { ProtectedPage } from "../components/protected-page"

function CustomerDashboardContent() {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">My Account</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-card border rounded-lg p-6">
          <h2 className="text-xl font-semibold mb-4">Profile Information</h2>
          <p className="text-muted-foreground">View and edit your profile details here.</p>
        </div>
        <div className="bg-card border rounded-lg p-6">
          <h2 className="text-xl font-semibold mb-4">Order History</h2>
          <p className="text-muted-foreground">View your purchase history here.</p>
        </div>
      </div>
    </div>
  )
}

export default function CustomerDashboard() {
  return (
    <ProtectedPage requiredRole="customer">
      <CustomerDashboardContent />
    </ProtectedPage>
  )
}
