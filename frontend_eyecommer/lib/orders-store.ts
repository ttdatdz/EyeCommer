// Mock orders store
import type { Order } from "./types"

const mockOrders: Record<string, Order> = {
  "ORD-001": {
    id: "ORD-001",
    customerId: "cust_1",
    items: [
      {
        productId: "aviator-black",
        product: {
          id: "aviator-black",
          name: "Classic Aviator - Black",
          sku: "AVT-001-BLK",
          category: "sunglasses",
          price: 99.99,
          description: "Timeless aviator sunglasses",
          image: "/black-aviator-sunglasses.jpg",
          stock: 45,
          rating: 4.5,
          reviews: 128,
        },
        quantity: 1,
        price: 99.99,
      },
    ],
    subtotal: 99.99,
    shipping: 10.0,
    tax: 10.0,
    discount: 0,
    total: 119.99,
    status: "shipped",
    paymentMethod: "cod",
    paymentStatus: "completed",
    shippingAddress: {
      name: "John Doe",
      phone: "555-0123",
      email: "john@example.com",
      address: "123 Main St",
      city: "Ho Chi Minh City",
      state: "HCMC",
      postalCode: "70000",
      country: "Vietnam",
    },
    trackingNumber: "TRACK123456",
    createdAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
    updatedAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
  },
  "ORD-002": {
    id: "ORD-002",
    customerId: "cust_1",
    items: [
      {
        productId: "prescription-blue",
        product: {
          id: "prescription-blue",
          name: "Prescription Blue Light - Modern",
          sku: "PRSC-002-BLU",
          category: "prescription",
          price: 149.99,
          description: "Modern prescription frames",
          image: "/modern-blue-prescription-glasses.jpg",
          stock: 120,
          rating: 4.7,
          reviews: 89,
        },
        quantity: 1,
        price: 149.99,
      },
    ],
    subtotal: 149.99,
    shipping: 10.0,
    tax: 15.0,
    discount: 0,
    total: 174.99,
    status: "pending",
    paymentMethod: "vnpay",
    paymentStatus: "completed",
    shippingAddress: {
      name: "John Doe",
      phone: "555-0123",
      email: "john@example.com",
      address: "123 Main St",
      city: "Ho Chi Minh City",
      state: "HCMC",
      postalCode: "70000",
      country: "Vietnam",
    },
    createdAt: new Date(),
    updatedAt: new Date(),
  },
}

export function getOrdersByCustomer(customerId: string) {
  return Object.values(mockOrders).filter((o) => o.customerId === customerId)
}

export function getOrderById(orderId: string) {
  return mockOrders[orderId]
}

export function getAllOrders() {
  return Object.values(mockOrders)
}

export function updateOrderStatus(orderId: string, status: Order["status"]) {
  const order = mockOrders[orderId]
  if (order) {
    order.status = status
    order.updatedAt = new Date()
  }
}
