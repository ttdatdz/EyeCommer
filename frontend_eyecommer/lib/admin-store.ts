// lib/admin-store.ts
// Mock admin management functions for products, staff, and customers
// Used ONLY for frontend demo / UI state

import type { Product, Customer } from "./types"
import type { Staff } from "@/app/types/staff"
import { mockProducts } from "./store"

// ==================================================
// PRODUCT MANAGEMENT
// ==================================================

const products: Record<string, Product> = { ...mockProducts }

export function getAllProducts(): Product[] {
  return Object.values(products)
}

export function getProductById(id: string): Product | undefined {
  return products[id]
}

export function addProduct(product: Omit<Product, "id">): Product {
  const id = `product_${Date.now()}`
  const newProduct: Product = { ...product, id }
  products[id] = newProduct
  return newProduct
}

export function updateProduct(id: string, updates: Partial<Product>): Product | null {
  if (!products[id]) return null
  products[id] = { ...products[id], ...updates }
  return products[id]
}

export function deleteProduct(id: string): void {
  delete products[id]
}

// ==================================================
// STAFF MANAGEMENT (SYNC WITH app/types/staff.ts)
// ==================================================

const staffMembers: Record<string, Staff> = {
  staff_1: {
    id: "staff_1",
    name: "Jane Smith",
    email: "jane.smith@visionhub.com",
    phone: "555-1001",
    department: "Order Processing",
    hireDate: "2024-01-15",
    salary: 2500,
    role: "staff",
  },
  staff_2: {
    id: "staff_2",
    name: "Bob Johnson",
    email: "bob.johnson@visionhub.com",
    phone: "555-1002",
    department: "Warehouse",
    hireDate: "2024-02-20",
    salary: 2300,
    role: "staff",
  },
}

export function getAllStaff(): Staff[] {
  return Object.values(staffMembers)
}

export function getStaffById(id: string): Staff | undefined {
  return staffMembers[id]
}

export function addStaff(staff: Omit<Staff, "id">): Staff {
  const id = `staff_${Date.now()}`
  const newStaff: Staff = { ...staff, id }
  staffMembers[id] = newStaff
  return newStaff
}

export function updateStaff(id: string, updates: Partial<Staff>): Staff | null {
  if (!staffMembers[id]) return null
  staffMembers[id] = { ...staffMembers[id], ...updates }
  return staffMembers[id]
}

export function deleteStaff(id: string): void {
  delete staffMembers[id]
}

// ==================================================
// CUSTOMER MANAGEMENT
// ==================================================

interface CustomerRecord extends Customer {
  id: string
  email: string
  phone: string
  totalOrders: number
  totalSpent: number
}

const customers: Record<string, CustomerRecord> = {
  cust_1: {
    id: "cust_1",
    userId: "cust_1",
    email: "john.doe@email.com",
    phone: "555-0123",
    firstName: "John",
    lastName: "Doe",
    preferredEmail: "john.doe@email.com",
    addresses: [
      {
        name: "John Doe",
        phone: "555-0123",
        email: "john.doe@email.com",
        address: "123 Main St",
        city: "Ho Chi Minh City",
        state: "HCMC",
        postalCode: "70000",
        country: "Vietnam",
      },
    ],
    createdAt: new Date("2024-01-10"),
    totalOrders: 2,
    totalSpent: 294.98,
  },
  cust_2: {
    id: "cust_2",
    userId: "cust_2",
    email: "sarah.williams@email.com",
    phone: "555-0124",
    firstName: "Sarah",
    lastName: "Williams",
    preferredEmail: "sarah.williams@email.com",
    addresses: [
      {
        name: "Sarah Williams",
        phone: "555-0124",
        email: "sarah.williams@email.com",
        address: "456 Oak Ave",
        city: "Hanoi",
        state: "HA",
        postalCode: "10000",
        country: "Vietnam",
      },
    ],
    createdAt: new Date("2024-02-05"),
    totalOrders: 1,
    totalSpent: 119.99,
  },
}

export function getAllCustomers(): CustomerRecord[] {
  return Object.values(customers)
}

export function getCustomerById(id: string): CustomerRecord | undefined {
  return customers[id]
}

export function addCustomer(customer: Omit<CustomerRecord, "id" | "createdAt">): CustomerRecord {
  const id = `cust_${Date.now()}`
  const newCustomer: CustomerRecord = {
    ...customer,
    id,
    createdAt: new Date(),
  }
  customers[id] = newCustomer
  return newCustomer
}

export function updateCustomer(id: string, updates: Partial<CustomerRecord>): CustomerRecord | null {
  if (!customers[id]) return null
  customers[id] = { ...customers[id], ...updates }
  return customers[id]
}

export function deleteCustomer(id: string): void {
  delete customers[id]
}
