import { API_BASE } from './auth'

export interface OrderItem {
  id: number
  productId: number
  productName: string
  variantId: number
  variantName: string
  imageUrl: string
  priceAtPurchase: number
  quantity: number
  lineTotal: number
}

export interface OrderDetail {
  id: number
  orderCode: string
  status: string
  paymentStatus: string
  paymentMethod: string
  totalAmount: number
  finalAmount: number
  addressDetail: string
  createdAt: string
  updatedAt: string
  items: OrderItem[]
}

export interface OrderListItem {
  orderCode: string
  status: string
  paymentStatus: string
  totalAmount: number
  finalAmount: number
  paymentMethod: string
  createdAt: string
  itemCount: number
}

// Get all orders of current user
export async function getUserOrders(): Promise<{ status: number; message: string; data: OrderListItem[] }> {
  const token = localStorage.getItem('accessToken')
  
  if (!token) {
    return { status: 401, message: 'Not authenticated', data: [] }
  }

  try {
    const response = await fetch(`${API_BASE}/orders/my-orders`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    })

    const result = await response.json()
    return result
  } catch (error: any) {
    console.error('Get orders error:', error)
    return { status: 500, message: error.message || 'Failed to fetch orders', data: [] }
  }
}

// Get order detail by order code
export async function getOrderByCode(orderCode: string): Promise<{ status: number; message: string; data?: OrderDetail }> {
  const token = localStorage.getItem('accessToken')
  
  if (!token) {
    return { status: 401, message: 'Not authenticated' }
  }

  try {
    const response = await fetch(`${API_BASE}/orders/${orderCode}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
    })

    const result = await response.json()
    return result
  } catch (error: any) {
    console.error('Get order detail error:', error)
    return { status: 500, message: error.message || 'Failed to fetch order detail' }
  }
}
