// src/types/customer.ts
export interface Customer {
  id?: string
  firstName: string
  lastName: string
  email: string
  phone: string

  // address
  address?: string
  city?: string
  state?: string
  postalCode?: string
  country?: string

  // admin stats
  totalOrders?: number
  totalSpent?: number
  createdAt?: Date
}
