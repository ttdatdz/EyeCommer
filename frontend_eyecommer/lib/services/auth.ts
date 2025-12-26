// Prefer relative base to leverage Next.js rewrites and avoid CORS
export const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || '/api'

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
    // Normalize network errors to a clearer message
    const err: any = new Error('Failed to connect to API. Please ensure the backend is running.')
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

export type LoginPayload = { username: string; password: string }
export type RegisterPayload = { username: string; email: string; password: string; role?: string }

export async function login(payload: LoginPayload) {
  return apiFetch('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function register(payload: RegisterPayload) {
  return apiFetch('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function logout() {
  // Try to call API logout but don't fail logout locally if API fails
  try {
    await apiFetch('/auth/logout', { method: 'POST' })
  } catch (e) {
    // ignore
  }
  // clear client-side storage should be handled by caller
}

export async function forgotPassword(email: string) {
  return apiFetch('/auth/forgot-password', {
    method: 'POST',
    body: JSON.stringify({ email }),
  })
}

export async function changePassword(payload: { oldPassword?: string; newPassword: string; token?: string }) {
  const headers: Record<string, string> = {}
  if (payload.token) headers['Authorization'] = `Bearer ${payload.token}`

  return apiFetch('/auth/change-password', {
    method: 'POST',
    headers,
    body: JSON.stringify(payload),
  })
}
