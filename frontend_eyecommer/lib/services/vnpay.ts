import { API_BASE } from './auth'

export interface VNPayPaymentRequest {
  orderCode: string
}

export interface VNPayPaymentResponse {
  status: number
  message: string
  data: {
    paymentUrl: string
  }
}

export async function createVNPayPayment(orderCode: string): Promise<VNPayPaymentResponse> {
  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null
  
  const response = await fetch(`${API_BASE}/vnpay/create-payment`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ orderCode }),
  })
  
  if (!response.ok) {
    throw new Error('Failed to create VNPay payment')
  }
  
  return response.json()
}
