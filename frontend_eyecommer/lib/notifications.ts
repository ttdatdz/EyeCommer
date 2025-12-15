// Notification service for email, SMS, and in-app notifications

export interface Notification {
  id: string
  type: "order" | "shipping" | "delivery" | "payment" | "promotion"
  title: string
  message: string
  timestamp: Date
  read: boolean
}

const notifications: Notification[] = [
  {
    id: "notif_1",
    type: "order",
    title: "Order Confirmed",
    message: "Your order ORD-001 has been confirmed and is being prepared for shipment.",
    timestamp: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
    read: true,
  },
  {
    id: "notif_2",
    type: "shipping",
    title: "Order Shipped",
    message: "Your order ORD-001 has been shipped. Tracking number: TRACK123456",
    timestamp: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
    read: true,
  },
]

export function getNotifications(userId: string, limit = 10) {
  return notifications.slice(0, limit)
}

export function markNotificationRead(notificationId: string) {
  const notif = notifications.find((n) => n.id === notificationId)
  if (notif) {
    notif.read = true
  }
}

export async function sendEmailNotification(email: string, subject: string, message: string, type: string) {
  // In production, integrate with email service (SendGrid, Resend, etc.)
  console.log(`[Email] To: ${email}, Subject: ${subject}`)
  console.log(`[Email] ${message}`)

  // Example with Resend (Vercel's email service)
  // const { data, error } = await resend.emails.send({
  //   from: 'orders@visionhub.com',
  //   to: email,
  //   subject: subject,
  //   html: message,
  // })
  // return !error
}

export async function sendSMSNotification(phone: string, message: string) {
  // In production, integrate with SMS service (Twilio, etc.)
  console.log(`[SMS] To: ${phone}`)
  console.log(`[SMS] ${message}`)
  return true
}

export async function notifyOrderConfirmed(orderId: string, email: string) {
  const subject = `Order ${orderId} Confirmed - VisionHub`
  const message = `Your order has been confirmed and will be shipped soon.`
  return sendEmailNotification(email, subject, message, "order_confirmed")
}

export async function notifyOrderShipped(orderId: string, email: string, trackingNumber?: string) {
  const subject = `Order ${orderId} Shipped - VisionHub`
  const message = `Your order has been shipped. ${trackingNumber ? `Tracking: ${trackingNumber}` : ""}`
  return sendEmailNotification(email, subject, message, "order_shipped")
}

export async function notifyOrderDelivered(orderId: string, email: string) {
  const subject = `Order ${orderId} Delivered - VisionHub`
  const message = `Your order has been delivered. Thank you for your purchase!`
  return sendEmailNotification(email, subject, message, "order_delivered")
}

export async function notifyPaymentReceived(orderId: string, email: string, amount: number) {
  const subject = `Payment Received - Order ${orderId}`
  const message = `Payment of $${amount.toFixed(2)} has been received for order ${orderId}.`
  return sendEmailNotification(email, subject, message, "payment_received")
}
