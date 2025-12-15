"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import CustomerForm from "@/components/forms/customer-form"
import { getCurrentUser, logout } from "@/lib/auth-store"
import { getAllCustomers, addCustomer, updateCustomer, deleteCustomer } from "@/lib/admin-store"
import type { Customer } from "@/app/types/customer"

export default function AdminCustomersPage() {
  const router = useRouter()
  const [user, setUser] = useState<any>(null)
  const [customers, setCustomers] = useState<Customer[]>([])
  const [showForm, setShowForm] = useState(false)
  const [editingCustomer, setEditingCustomer] = useState<Customer | null>(null)
  const [searchQuery, setSearchQuery] = useState("")

  useEffect(() => {
    const currentUser = getCurrentUser()
    if (!currentUser || currentUser.role !== "admin") {
      router.push("/login")
      return
    }
    setUser(currentUser)
    setCustomers(getAllCustomers())
  }, [router])

  const handleLogout = () => {
    logout()
    router.push("/")
  }

  const handleAddCustomer = (formData: any) => {
    addCustomer(formData)
    setCustomers(getAllCustomers())
    setShowForm(false)
    alert("Customer added successfully!")
  }

  const handleUpdateCustomer = (formData: any) => {
    if (!editingCustomer?.id) {
      alert("Invalid customer ID")
      return
    }

    updateCustomer(editingCustomer.id, formData)
    setCustomers(getAllCustomers())
    setEditingCustomer(null)
    setShowForm(false)
    alert("Customer updated successfully!")
  }


  const handleDeleteCustomer = (customerId: string) => {
    if (confirm("Are you sure you want to delete this customer?")) {
      deleteCustomer(customerId)
      setCustomers(getAllCustomers())
      alert("Customer deleted successfully!")
    }
  }

  const handleEditClick = (customer: Customer) => {
    setEditingCustomer(customer)
    setShowForm(true)
  }

  const handleCancel = () => {
    setShowForm(false)
    setEditingCustomer(null)
  }

  if (!user) return null

  const filteredCustomers = customers.filter(
    (c) =>
      `${c.firstName} ${c.lastName}`.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.phone.includes(searchQuery),
  )

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold">Customer Management</h1>
            <Button variant="outline" onClick={handleLogout}>
              Logout
            </Button>
          </div>

          {showForm ? (
            <div className="mb-8">
              <CustomerForm
                customer={editingCustomer || undefined}
                onSubmit={editingCustomer ? handleUpdateCustomer : handleAddCustomer}
                onCancel={handleCancel}
              />
            </div>
          ) : (
            <>
              <div className="flex gap-4 mb-6">
                <input
                  type="text"
                  placeholder="Search by name, email, or phone..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="flex-1 px-4 py-2 border border-border rounded-md"
                />
                <Button onClick={() => setShowForm(true)}>Add New Customer</Button>
              </div>

              <Card>
                <CardContent className="p-0">
                  <div className="overflow-x-auto">
                    <table className="w-full">
                      <thead>
                        <tr className="border-b border-border bg-muted/50">
                          <th className="p-4 text-left font-semibold">Name</th>
                          <th className="p-4 text-left font-semibold">Email</th>
                          <th className="p-4 text-left font-semibold">Phone</th>
                          <th className="p-4 text-center font-semibold">Orders</th>
                          <th className="p-4 text-right font-semibold">Total Spent</th>
                          <th className="p-4 text-left font-semibold">Joined</th>
                          <th className="p-4 text-left font-semibold">Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filteredCustomers.map((customer) => (
                          <tr key={customer.id} className="border-b border-border hover:bg-muted/50">
                            <td className="p-4 font-semibold">{`${customer.firstName} ${customer.lastName}`}</td>
                            <td className="p-4 text-sm">{customer.email}</td>
                            <td className="p-4 text-sm">{customer.phone}</td>
                            <td className="p-4 text-center">{customer.totalOrders}</td>
                            <td className="p-4 text-right font-semibold">
                              ${(customer.totalSpent ?? 0).toFixed(2)}
                            </td>
                            <td className="p-4 text-sm">
                              {customer.createdAt
                                ? new Date(customer.createdAt).toLocaleDateString()
                                : "-"}
                            </td>
                            <td className="p-4 flex gap-2">
                              <Button size="sm" variant="outline" onClick={() => handleEditClick(customer)}>
                                Edit
                              </Button>
                              <Button
                                size="sm"
                                variant="outline"
                                className="text-red-600 hover:text-red-700 bg-transparent"
                                onClick={() => {
                                  if (!customer.id) return
                                  handleDeleteCustomer(customer.id)
                                }}
                              >
                                Delete
                              </Button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                  {filteredCustomers.length === 0 && (
                    <div className="p-8 text-center text-muted-foreground">No customers found</div>
                  )}
                </CardContent>
              </Card>
            </>
          )}
        </div>
      </main>

      <Footer />
    </div>
  )
}
