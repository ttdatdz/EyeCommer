"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import StaffForm from "@/components/forms/staff-form"
import { getCurrentUser, logout } from "@/lib/auth-store"
import { getAllStaff, addStaff, updateStaff, deleteStaff } from "@/lib/admin-store"
import type { Staff } from "@/app/types/staff"

export default function AdminStaffPage() {
  const router = useRouter()
  const [user, setUser] = useState<any>(null)
  const [staffMembers, setStaffMembers] = useState<Staff[]>([])
  const [showForm, setShowForm] = useState(false)
  const [editingStaff, setEditingStaff] = useState<Staff | null>(null)
  const [searchQuery, setSearchQuery] = useState("")

  useEffect(() => {
    const currentUser = getCurrentUser()
    if (!currentUser || currentUser.role !== "admin") {
      router.push("/login")
      return
    }
    setUser(currentUser)
    setStaffMembers(getAllStaff())
  }, [router])

  const handleLogout = () => {
    logout()
    router.push("/")
  }

  const handleAddStaff = (formData: any) => {
    addStaff(formData)
    setStaffMembers(getAllStaff())
    setShowForm(false)
    alert("Staff member added successfully!")
  }
  const handleUpdateStaff = (formData: any) => {
    if (!editingStaff?.id) {
      alert("Invalid customer ID")
      return
    }

    updateStaff(editingStaff.id, formData)
    setStaffMembers(getAllStaff())
    setEditingStaff(null)
    setShowForm(false)
    alert("Staff member updated successfully!")
  }

  const handleDeleteStaff = (staffId: string) => {
    if (confirm("Are you sure you want to delete this staff member?")) {
      deleteStaff(staffId)
      setStaffMembers(getAllStaff())
      alert("Staff member deleted successfully!")
    }
  }

  const handleEditClick = (staff: Staff) => {
    setEditingStaff(staff)
    setShowForm(true)
  }

  const handleCancel = () => {
    setShowForm(false)
    setEditingStaff(null)
  }

  if (!user) return null

  const filteredStaff = staffMembers.filter(
    (s) =>
      s.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.department.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold">Staff Management</h1>
            <Button variant="outline" onClick={handleLogout}>
              Logout
            </Button>
          </div>

          {showForm ? (
            <div className="mb-8">
              <StaffForm
                staff={editingStaff || undefined}
                onSubmit={editingStaff ? handleUpdateStaff : handleAddStaff}
                onCancel={handleCancel}
              />
            </div>
          ) : (
            <>
              <div className="flex gap-4 mb-6">
                <input
                  type="text"
                  placeholder="Search by name, email, or department..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="flex-1 px-4 py-2 border border-border rounded-md"
                />
                <Button onClick={() => setShowForm(true)}>Add New Staff Member</Button>
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
                          <th className="p-4 text-left font-semibold">Department</th>
                          <th className="p-4 text-left font-semibold">Hire Date</th>
                          <th className="p-4 text-right font-semibold">Salary</th>
                          <th className="p-4 text-left font-semibold">Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filteredStaff.map((staff) => (
                          <tr key={staff.id} className="border-b border-border hover:bg-muted/50">
                            <td className="p-4 font-semibold">{staff.name}</td>
                            <td className="p-4 text-sm">{staff.email}</td>
                            <td className="p-4 text-sm">{staff.phone}</td>
                            <td className="p-4 text-sm">{staff.department}</td>
                            <td className="p-4 text-sm">{new Date(staff.hireDate).toLocaleDateString()}</td>
                            <td className="p-4 text-right font-semibold">
                              {staff.salary ? `$${staff.salary.toLocaleString()}` : "-"}
                            </td>
                            <td className="p-4 flex gap-2">
                              <Button size="sm" variant="outline" onClick={() => handleEditClick(staff)}>
                                Edit
                              </Button>
                              <Button
                                size="sm"
                                variant="outline"
                                className="text-red-600 hover:text-red-700 bg-transparent"
                                onClick={() => handleDeleteStaff(staff.id)}
                              >
                                Delete
                              </Button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                  {filteredStaff.length === 0 && (
                    <div className="p-8 text-center text-muted-foreground">No staff members found</div>
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
