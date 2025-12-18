"use client"

import { useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { changePassword as changePasswordApi } from "@/lib/services/auth"

export default function ChangePasswordClient() {
  const router = useRouter()
  const search = useSearchParams()
  const tokenFromQuery = search.get("token") ?? undefined

  const [oldPassword, setOldPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [error, setError] = useState("")
  const [message, setMessage] = useState("")

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    setMessage("")
    try {
      await changePasswordApi({
        oldPassword: tokenFromQuery ? undefined : oldPassword,
        newPassword,
        token: tokenFromQuery,
      })
      setMessage("Password changed successfully. You can now log in with the new password.")
    } catch (err: any) {
      setError(err?.message || "Change password failed")
    }
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />
      <main className="flex-1 flex items-center justify-center py-16">
        <Card className="w-full max-w-md">
          <CardContent className="p-8">
            <h1 className="text-2xl font-bold mb-2">Change Password</h1>
            <p className="text-muted-foreground mb-6">
              Set a new password for your account
            </p>

            <form onSubmit={handleSubmit} className="space-y-4">
              {!tokenFromQuery && (
                <div>
                  <label className="block text-sm font-semibold mb-2">
                    Old Password
                  </label>
                  <input
                    type="password"
                    value={oldPassword}
                    onChange={(e) => setOldPassword(e.target.value)}
                    className="w-full px-4 py-2 border border-border rounded bg-card text-foreground"
                    required
                  />
                </div>
              )}

              <div>
                <label className="block text-sm font-semibold mb-2">
                  New Password
                </label>
                <input
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="w-full px-4 py-2 border border-border rounded bg-card text-foreground"
                  required
                />
              </div>

              {error && (
                <div className="p-3 bg-red-100 text-red-800 rounded text-sm">
                  {error}
                </div>
              )}
              {message && (
                <div className="p-3 bg-green-100 text-green-800 rounded text-sm">
                  {message}
                </div>
              )}

              <Button type="submit" className="w-full">
                Change Password
              </Button>
            </form>
          </CardContent>
        </Card>
      </main>
      <Footer />
    </div>
  )
}
