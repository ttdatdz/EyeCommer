import type { NextRequest } from "next/server"
import { NextResponse } from "next/server"

export function middleware(request: NextRequest) {
  const userJson = request.cookies.get("user")?.value
  let user = null

  try {
    if (userJson) {
      user = JSON.parse(userJson)
    }
  } catch (error) {
    console.error("[v0] Error parsing user from middleware:", error)
  }

  // Get user from session/localStorage via request path
  // Note: For a more robust solution, you'd use server session management
  const pathname = request.nextUrl.pathname

  // Protect admin routes
  if (pathname.startsWith("/admin")) {
    // In a real app, you'd validate the session here
    // For now, we rely on client-side auth context
    return NextResponse.next()
  }

  // Protect staff routes
  if (pathname.startsWith("/staff")) {
    return NextResponse.next()
  }

  // Protect customer routes
  if (pathname.startsWith("/customer")) {
    return NextResponse.next()
  }

  return NextResponse.next()
}

export const config = {
  matcher: ["/admin/:path*", "/staff/:path*", "/customer/:path*"],
}
