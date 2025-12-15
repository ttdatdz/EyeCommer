"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getNotifications, markNotificationRead } from "@/lib/notifications"
import type { Notification } from "@/lib/notifications"

export default function NotificationsPage() {
  const [notifs, setNotifs] = useState<Notification[]>([])

  useEffect(() => {
    const userNotifications = getNotifications("cust_1", 20)
    setNotifs(userNotifications)
  }, [])

  const handleMarkRead = (id: string) => {
    markNotificationRead(id)
    setNotifs(notifs.map((n) => (n.id === id ? { ...n, read: true } : n)))
  }

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case "order":
        return "📦"
      case "shipping":
        return "🚚"
      case "delivery":
        return "✓"
      case "payment":
        return "💳"
      case "promotion":
        return "🎉"
      default:
        return "📬"
    }
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <Link href="/customer/dashboard">
            <Button variant="outline" className="mb-6 bg-transparent">
              Back
            </Button>
          </Link>

          <h1 className="text-3xl font-bold mb-2">Notifications</h1>
          <p className="text-muted-foreground mb-8">{notifs.length} notifications</p>

          {notifs.length === 0 ? (
            <Card>
              <CardContent className="p-8 text-center text-muted-foreground">No notifications yet</CardContent>
            </Card>
          ) : (
            <div className="space-y-3">
              {notifs.map((notif) => (
                <Card key={notif.id} className={notif.read ? "opacity-70" : ""}>
                  <CardContent className="p-4 flex gap-4 items-start">
                    <div className="text-2xl mt-1">{getNotificationIcon(notif.type)}</div>
                    <div className="flex-1">
                      <h3 className="font-semibold">{notif.title}</h3>
                      <p className="text-sm text-muted-foreground">{notif.message}</p>
                      <p className="text-xs text-muted-foreground mt-2">
                        {new Date(notif.timestamp).toLocaleDateString()}
                      </p>
                    </div>
                    {!notif.read && (
                      <Button size="sm" variant="outline" onClick={() => handleMarkRead(notif.id)}>
                        Mark Read
                      </Button>
                    )}
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>
      </main>

      <Footer />
    </div>
  )
}
