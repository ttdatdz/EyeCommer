"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import type { Staff } from "@/app/types/staff"

type StaffFormData = Required<
  Pick<
    Staff,
    "name" | "email" | "phone" | "department" | "hireDate"
  >
> & {
  salary: number
}

interface StaffFormProps {
  staff?: Staff
  onSubmit: (data: StaffFormData) => void
  onCancel: () => void
  loading?: boolean
}

export default function StaffForm({ staff, onSubmit, onCancel, loading = false }: StaffFormProps) {
  const [formData, setFormData] = useState<StaffFormData>({
    name: staff?.name ?? "",
    email: staff?.email ?? "",
    phone: staff?.phone ?? "",
    department: staff?.department ?? "",
    hireDate: staff?.hireDate
      ? new Date(staff.hireDate).toISOString().split("T")[0]
      : "",
    salary: staff?.salary ?? 0,
  })

  const [errors, setErrors] = useState<Record<string, string>>({})

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: type === "number" ? Number.parseFloat(value) : value,
    }))
  }

  const validateForm = () => {
    const newErrors: Record<string, string> = {}

    if (!formData.name.trim()) newErrors.name = "Name is required"
    if (!formData.email.trim()) newErrors.email = "Email is required"
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) newErrors.email = "Invalid email format"
    if (!formData.phone.trim()) newErrors.phone = "Phone is required"
    if (!formData.department.trim()) newErrors.department = "Department is required"
    if (!formData.hireDate) newErrors.hireDate = "Hire date is required"

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (validateForm()) {
      onSubmit(formData)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{staff ? "Edit Staff Member" : "Add New Staff Member"}</CardTitle>
        <CardDescription>Fill in the staff details below</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Personal Info */}
          <div className="space-y-4">
            <h3 className="font-semibold">Personal Information</h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-1">Full Name</label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-border rounded-md"
                  placeholder="e.g., Jane Smith"
                />
                {errors.name && <p className="text-xs text-red-600 mt-1">{errors.name}</p>}
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Email</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-border rounded-md"
                  placeholder="jane@visionhub.com"
                />
                {errors.email && <p className="text-xs text-red-600 mt-1">{errors.email}</p>}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Phone</label>
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-border rounded-md"
                placeholder="555-0123"
              />
              {errors.phone && <p className="text-xs text-red-600 mt-1">{errors.phone}</p>}
            </div>
          </div>

          {/* Employment Info */}
          <div className="space-y-4">
            <h3 className="font-semibold">Employment Information</h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-1">Department</label>
                <select
                  name="department"
                  value={formData.department}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-border rounded-md"
                >
                  <option value="">Select Department</option>
                  <option value="Order Processing">Order Processing</option>
                  <option value="Warehouse">Warehouse</option>
                  <option value="Customer Service">Customer Service</option>
                  <option value="Quality Control">Quality Control</option>
                  <option value="Logistics">Logistics</option>
                </select>
                {errors.department && <p className="text-xs text-red-600 mt-1">{errors.department}</p>}
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Hire Date</label>
                <input
                  type="date"
                  name="hireDate"
                  value={formData.hireDate}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-border rounded-md"
                />
                {errors.hireDate && <p className="text-xs text-red-600 mt-1">{errors.hireDate}</p>}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Monthly Salary ($)</label>
              <input
                type="number"
                name="salary"
                value={formData.salary}
                onChange={handleChange}
                step="100"
                className="w-full px-3 py-2 border border-border rounded-md"
              />
            </div>
          </div>

          {/* Form Actions */}
          <div className="flex justify-end gap-3 pt-4 border-t border-border">
            <Button type="button" variant="outline" onClick={onCancel} disabled={loading}>
              Cancel
            </Button>
            <Button type="submit" disabled={loading}>
              {loading ? "Saving..." : staff ? "Update Staff" : "Add Staff Member"}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
