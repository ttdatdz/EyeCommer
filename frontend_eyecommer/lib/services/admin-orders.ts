const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || '';

export interface AdminOrderItem {
  id: number;
  orderCode: string;
  username: string;
  status: string;
  paymentStatus: string;
  paymentMethod: string;
  totalAmount: number;
  finalAmount: number;
  addressDetail: string;
  createdAt: string;
  itemCount: number;
}

export interface AdminOrdersResponse {
  status: number;
  message: string;
  data: AdminOrderItem[];
}

export interface OrderActionResponse {
  status: number;
  message: string;
  data: {
    orderCode: string;
    status?: string;
  };
}

export async function getAllOrders(): Promise<AdminOrderItem[]> {
  const accessToken = localStorage.getItem('accessToken');
  
  if (!accessToken) {
    throw new Error('Không tìm thấy token');
  }

  const response = await fetch(`${API_BASE}/api/orders/admin/all`, {
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Không thể lấy danh sách đơn hàng');
  }

  const result: AdminOrdersResponse = await response.json();
  return result.data;
}

export async function confirmOrder(orderCode: string): Promise<void> {
  const accessToken = localStorage.getItem('accessToken');
  
  if (!accessToken) {
    throw new Error('Không tìm thấy token');
  }

  const response = await fetch(`${API_BASE}/api/orders/admin/confirm/${orderCode}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Không thể xác nhận đơn hàng');
  }
}

export async function deleteOrder(orderCode: string): Promise<void> {
  const accessToken = localStorage.getItem('accessToken');
  
  if (!accessToken) {
    throw new Error('Không tìm thấy token');
  }

  const response = await fetch(`${API_BASE}/api/orders/admin/delete/${orderCode}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Không thể xóa đơn hàng');
  }
}
