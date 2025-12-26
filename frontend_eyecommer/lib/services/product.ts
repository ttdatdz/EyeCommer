import { API_BASE } from "./auth"

type Json = Record<string, any>

async function apiFetch(path: string, options: RequestInit = {}) {
  const url = `${API_BASE}${path}`
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  }

  let res: Response
  try {
    res = await fetch(url, { ...options, headers, credentials: 'include' })
  } catch (e: any) {
    const err: any = new Error('Failed to connect to API.')
    err.cause = e
    err.status = 0
    throw err
  }

  const text = await res.text()
  let data: Json | null = null
  try {
    data = text ? JSON.parse(text) : null
  } catch (e) {
    // not json
  }

  if (!res.ok) {
    const message = (data && (data.message || data.error)) || res.statusText
    const err: any = new Error(message)
    err.status = res.status
    err.data = data
    throw err
  }

  return data
}

export interface ProductResponse {
  id: number
  name: string
  description?: string
  price: number
  status: string
  thumbnailUrl?: string
  shortDescription?: string
  variantProducts?: any[]
  categories?: any[]
}

export interface ProductListResponse {
  pageNo: number
  pageSize: number
  totalPage: number
  items: ProductResponse[]
}

export async function getProducts(params?: { 
  pageNo?: number
  pageSize?: number
  sortBy?: string
  search?: string[]
}) {
  const searchParams = new URLSearchParams()
  if (params?.pageNo !== undefined) searchParams.append('pageNo', params.pageNo.toString())
  if (params?.pageSize !== undefined) searchParams.append('pageSize', params.pageSize.toString())
  if (params?.sortBy) searchParams.append('sortBy', params.sortBy)
  if (params?.search) {
    params.search.forEach(s => searchParams.append('search', s))
  }

  const query = searchParams.toString()
  const path = `/products${query ? `?${query}` : ''}`
  
  return apiFetch(path) as Promise<{ status: number; message: string; data: ProductListResponse }>
}

export async function getProductById(id: number | string) {
  return apiFetch(`/products/${id}`) as Promise<{ status: number; message: string; data: ProductResponse }>
}
