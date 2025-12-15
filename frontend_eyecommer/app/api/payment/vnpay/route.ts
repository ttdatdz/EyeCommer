export async function POST(request: Request) {
  const { orderId, amount } = await request.json()

  // This is a mock VNPay integration
  // In production, integrate with actual VNPay API
  const vnpayUrl = process.env.VNPAY_URL || "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"

  const vnpParams = {
    vnp_Version: "2.1.0",
    vnp_Command: "pay",
    vnp_TmnCode: process.env.VNPAY_TMN_CODE || "TMNCODE",
    vnp_Amount: amount * 100, // VNPay expects amount in cents
    vnp_CurrCode: "VND",
    vnp_TxnRef: orderId,
    vnp_OrderInfo: `Order ${orderId}`,
    vnp_OrderType: "other",
    vnp_Locale: "vn",
    vnp_ReturnUrl: `${process.env.NEXT_PUBLIC_APP_URL || "http://localhost:3000"}/payment/callback`,
    vnp_CreateDate: new Date()
      .toISOString()
      .replace(/[-T:.Z]/g, "")
      .slice(0, 14),
  }

  // In production, sort params and create signature
  const redirectUrl = new URL(vnpayUrl)
  Object.entries(vnpParams).forEach(([key, value]) => {
    redirectUrl.searchParams.append(key, String(value))
  })

  return Response.json({ redirectUrl: redirectUrl.toString() })
}
