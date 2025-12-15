export interface Product {
  id: string
  name: string
  sku: string
  category: "prescription" | "sunglasses" | "fashion" | "accessories"
  price: number
  originalPrice?: number
  description: string
  image: string
  images?: string[]
  color?: string
  material?: string
  stock: number
  rating: number
  reviews: number
  featured?: boolean
}

export interface CartItem {
  id: string
  productId: string
  product: Product
  quantity: number
  addedAt: Date
}

export interface Order {
  id: string
  customerId: string
  items: OrderItem[]
  subtotal: number
  shipping: number
  tax: number
  discount: number
  total: number
  status: "pending" | "confirmed" | "shipped" | "delivered" | "cancelled"
  paymentMethod: "vnpay" | "cod"
  paymentStatus: "pending" | "completed" | "failed"
  shippingAddress: ShippingAddress
  trackingNumber?: string
  createdAt: Date
  updatedAt: Date
}

export interface OrderItem {
  productId: string
  product: Product
  quantity: number
  price: number
}

export interface ShippingAddress {
  name: string
  phone: string
  email: string
  address: string
  city: string
  state: string
  postalCode: string
  country: string
}

export interface User {
  id: string
  email: string
  phone?: string
  password: string
  role: "customer" | "staff" | "admin"
  createdAt: Date
  updatedAt: Date
}

export interface Customer {
  userId: string
  firstName: string
  lastName: string
  phone: string
  preferredEmail: string
  addresses: ShippingAddress[]
  createdAt: Date
}
