import { API_BASE } from "./auth"

type Json = Record<string, any>

async function fetchJson(path: string, options: RequestInit = {}) {
  // Get token from localStorage
  const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null
  
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }
  
  // Add Authorization header if token exists
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  
  // Merge with any existing headers
  if (options.headers) {
    Object.assign(headers, options.headers)
  }
  
  const res = await fetch(`${API_BASE}${path}`, { 
    ...options, 
    headers 
  })
  
  const text = await res.text()
  let data: Json | null = null
  try {
    data = text ? JSON.parse(text) : null
  } catch (e) {
    // ignore
  }
  if (!res.ok) {
    const err: any = new Error((data && (data.message || data.error)) || res.statusText)
    err.status = res.status
    err.data = data
    throw err
  }
  return data
}

export function getProvinces() {
  return fetchJson('/ghn/provinces')
}

export function getDistricts(provinceId?: number) {
  const url = provinceId ? `/ghn/districts?province_id=${provinceId}` : '/ghn/districts'
  return fetchJson(url)
}

export function getWards(district_id: number) {
  return fetchJson(`/ghn/wards?district_id=${district_id}`)
}

export function getAvailableServices(toDistrict?: number) {
  const body = {
    shop_id: 198584,
    from_district: 1452,
    to_district: toDistrict || 1566
  }
  return fetchJson('/ghn/available-services', { method: 'POST', body: JSON.stringify(body) })
}

export function getLeadtime(body: { to_district_id: string; to_ward_code: string; service_id: string }) {
  return fetchJson('/ghn/leadtime', { 
    method: 'POST', 
    body: JSON.stringify({
      from_district_id: "1452",
      from_ward_code: "20515",
      ...body
    })
  })
}

export function getFee(body: { to_district_id: number; to_ward_code: string; service_id: number; weight: number; length: number; width: number; height: number }) {
  return fetchJson('/ghn/fee', { method: 'POST', body: JSON.stringify(body) })
}
