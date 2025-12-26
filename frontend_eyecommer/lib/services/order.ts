import { API_BASE } from './auth'

export interface CreateOrderRequest {
  address: string
  cartTotal: number
  shippingFee: number
  discount: number
  paymentMethod: 'vnpay' | 'cod'
  // Add more fields as needed
  firstName?: string
  lastName?: string
  email?: string
  phone?: string
  province?: string
  district?: string
  ward?: string
  service?: number
}

export interface CreateOrderResponse {
  status: number
  message: string
  data: {
    orderCode: string
    totalAmount: number
  }
}

export async function createOrder(orderData: CreateOrderRequest): Promise<CreateOrderResponse> {
  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null
  
  console.log('[Order Service] Creating order with data:', orderData)
  
  const response = await fetch(`${API_BASE}/orders/create`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    },
    body: JSON.stringify(orderData),
  })
  
  console.log('[Order Service] Response status:', response.status)
  
  const text = await response.text()
  console.log('[Order Service] Response text:', text)
  
  let data
  try {
    data = text ? JSON.parse(text) : null
  } catch (e) {
    console.error('[Order Service] JSON parse error:', e)
    throw new Error('Invalid response from server: ' + text.substring(0, 100))
  }
  
  if (!response.ok) {
    throw new Error(data?.message || 'Failed to create order')
  }
  
  return data
}

export async function getOrder(orderCode: string) {
  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null
  
  const response = await fetch(`${API_BASE}/orders/${orderCode}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    },
  })
  
  if (!response.ok) {
    throw new Error('Failed to get order')
  }
  
  return response.json()
}
