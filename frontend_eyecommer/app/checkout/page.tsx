"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getCart, getCartTotal, clearCart } from "@/lib/cart-store"
import { getProvinces, getDistricts, getWards, getAvailableServices, getFee, getLeadtime } from "@/lib/services/ghn"
import { getCurrentUser } from "@/lib/auth-store"
import { createVNPayPayment } from "@/lib/services/vnpay"
import { createOrder } from "@/lib/services/order"

export default function CheckoutPage() {
  const router = useRouter()
  const [step, setStep] = useState<"shipping" | "payment">("shipping")
  const [cartItems, setCartItems] = useState<any[]>([])
  const [cartTotal, setCartTotal] = useState(0)
  const [isLoading, setIsLoading] = useState(true)

  // Check if user is logged in
  useEffect(() => {
    // Check auth
    const user = getCurrentUser()
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null
    
    console.log('[Checkout] User:', user, 'Token:', token ? 'exists' : 'null')
    
    if (!user || !token) {
      alert('Vui lòng đăng nhập để tiếp tục thanh toán')
      router.push('/login')
      return
    }
    
    // Load cart
    const items = getCart()
    if (items.length === 0) {
      alert('Giỏ hàng trống')
      router.push('/products')
      return
    }
    
    setCartItems(items)
    setCartTotal(getCartTotal())
    setIsLoading(false)
  }, [])

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    address: "",
    city: "",
    state: "",
    postalCode: "",
    country: "Vietnam",
  })

  // GHN/shipping state
  const [provinces, setProvinces] = useState<any[]>([])
  const [districts, setDistricts] = useState<any[]>([])
  const [wards, setWards] = useState<any[]>([])
  const [services, setServices] = useState<any[]>([])
  const [selectedProvince, setSelectedProvince] = useState<number | "">("")
  const [selectedDistrict, setSelectedDistrict] = useState<number | "">("")
  const [selectedWard, setSelectedWard] = useState<string | "">("")
  const [selectedService, setSelectedService] = useState<number | "">("")
  const [shippingFee, setShippingFee] = useState<number | null>(null)
  const [leadtime, setLeadtime] = useState<number | null>(null)
  const [loadingShip, setLoadingShip] = useState(false)

  const [paymentMethod, setPaymentMethod] = useState<"vnpay" | "cod">("cod")
  const [voucherCode, setVoucherCode] = useState("")

  useEffect(() => {
    getProvinces().then((res) => setProvinces(res?.data ?? [])).catch(() => {})
  }, [])

  const handleCalculateShipping = async () => {
    if (!selectedDistrict || !selectedWard || !selectedService) {
      alert('Vui lòng chọn quận/huyện, phường/xã và loại dịch vụ')
      return
    }

    setLoadingShip(true)
    try {
      const weight = cartItems.reduce((s, i) => s + (i.product.weight || 1000) * i.quantity, 0)
      const feeRes = await getFee({
        to_district_id: Number(selectedDistrict),
        to_ward_code: selectedWard,
        service_id: Number(selectedService),
        weight,
        length: 20,
        width: 20,
        height: 10,
      })
      setShippingFee((feeRes && feeRes.data && (feeRes.data.total ?? feeRes.data.service_fee)) || 0)

      // Leadtime
      const leadRes = await getLeadtime({
        to_district_id: String(selectedDistrict),
        to_ward_code: selectedWard,
        service_id: String(selectedService),
      })
      setLeadtime(leadRes && leadRes.data && leadRes.data.leadtime ? leadRes.data.leadtime : null)
    } catch (e) {
      console.error(e)
      alert('Không thể tính phí vận chuyển')
    } finally {
      setLoadingShip(false)
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleNextStep = (e: React.FormEvent) => {
    e.preventDefault()
    if (step === "shipping") {
      if (shippingFee === null) {
        alert('Vui lòng tính phí vận chuyển trước khi tiếp tục')
        return
      }
      setStep("payment")
    }
  }

  const handlePlaceOrder = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      // Create order first
      const orderData = {
        address: `${formData.address}, ${selectedWard}, ${selectedDistrict}, ${selectedProvince}`,
        cartTotal: cartTotal,
        shippingFee: shippingFee || 0,
        discount: discount,
        paymentMethod: paymentMethod,
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone,
        items: cartItems.map((item) => ({
          productId: item.product.id,
          variantId: item.product.id, // TODO: use actual variant ID when available
          quantity: item.quantity,
          price: item.product.price,
        })),
      }

      console.log('[Checkout] Creating order:', orderData)
      
      const orderResponse = await createOrder(orderData)
      
      if (orderResponse.status !== 200) {
        alert('Không thể tạo đơn hàng: ' + orderResponse.message)
        return
      }

      const orderCode = orderResponse.data.orderCode

      if (paymentMethod === 'vnpay') {
        // VNPay payment flow
        console.log('[Checkout] Creating VNPay payment for order:', orderCode)
        
        const paymentResponse = await createVNPayPayment(orderCode)
        
        if (paymentResponse.status === 200 && paymentResponse.data.paymentUrl) {
          // Redirect to VNPay
          window.location.href = paymentResponse.data.paymentUrl
        } else {
          alert('Không thể tạo thanh toán VNPay: ' + paymentResponse.message)
        }
      } else {
        // COD payment flow
        clearCart()
        alert(`Đặt hàng thành công! Mã đơn hàng: ${orderCode}`)
        router.push(`/order-confirmation/${orderCode}`)
      }
    } catch (error: any) {
      console.error('Order error:', error)
      alert('Có lỗi xảy ra khi đặt hàng: ' + (error?.message || 'Unknown error'))
    }
  }

  const discount = voucherCode === "SAVE20" ? cartTotal * 0.2 : 0
  const finalTotal = cartTotal + (shippingFee || 0) - discount

  if (isLoading) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <Header />
        <main className="flex-1 flex items-center justify-center">
          <div className="text-center">
            <p className="text-lg">Đang tải...</p>
          </div>
        </main>
        <Footer />
      </div>
    )
  }

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <h1 className="text-3xl font-bold mb-8">Checkout</h1>

          {/* Progress */}
          <div className="flex gap-8 mb-8">
            <div className={`flex-1 ${step === "shipping" ? "text-primary font-semibold" : "text-muted-foreground"}`}>
              1. Shipping Details
            </div>
            <div className={`flex-1 ${step === "payment" ? "text-primary font-semibold" : "text-muted-foreground"}`}>
              2. Payment
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Form */}
            <div className="lg:col-span-2">
              {step === "shipping" ? (
                <form onSubmit={handleNextStep}>
                  <Card>
                    <CardContent className="p-6">
                      <h2 className="text-xl font-semibold mb-6">Shipping Address</h2>

                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <input
                          type="text"
                          name="firstName"
                          placeholder="First Name"
                          value={formData.firstName}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                        <input
                          type="text"
                          name="lastName"
                          placeholder="Last Name"
                          value={formData.lastName}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                      </div>

                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <input
                          type="email"
                          name="email"
                          placeholder="Email"
                          value={formData.email}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                        <input
                          type="tel"
                          name="phone"
                          placeholder="Phone"
                          value={formData.phone}
                          onChange={handleInputChange}
                          required
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                        />
                      </div>

                      <input
                        type="text"
                        name="address"
                        placeholder="Street Address"
                        value={formData.address}
                        onChange={handleInputChange}
                        required
                        className="w-full px-4 py-2 border border-border rounded bg-card text-foreground mb-4"
                      />

                      {/* GHN selectors */}
                      <div className="grid grid-cols-3 gap-4 mb-4">
                        <select
                          value={selectedProvince}
                          onChange={(e) => {
                            const val = e.target.value
                            console.log('[Checkout] Province selected:', val)
                            setSelectedProvince(val ? Number(val) : "")
                            setSelectedDistrict("")
                            setSelectedWard("")
                            setDistricts([])
                            setWards([])
                            if (val) {
                              console.log('[Checkout] Fetching districts for province:', val)
                              getDistricts(Number(val))
                                .then((res) => {
                                  console.log('[Checkout] Districts response:', res)
                                  setDistricts(res?.data ?? [])
                                })
                                .catch((err) => {
                                  console.error('[Checkout] Districts error:', err)
                                  alert('Không thể tải danh sách quận/huyện: ' + (err?.message || 'Unknown error'))
                                })
                            }
                          }}
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                          required
                        >
                          <option value="">Chọn Tỉnh/Thành phố</option>
                          {provinces.map((p) => (
                            <option key={p.ProvinceID} value={p.ProvinceID}>
                              {p.ProvinceName}
                            </option>
                          ))}
                        </select>

                        <select
                          value={selectedDistrict}
                          onChange={(e) => {
                            const val = e.target.value
                            setSelectedDistrict(val ? Number(val) : "")
                            setSelectedWard("")
                            setSelectedService("")
                            setWards([])
                            setServices([])
                            if (val) {
                              getWards(Number(val)).then((res) => setWards(res?.data ?? [])).catch(() => {})
                              getAvailableServices(Number(val)).then((res) => setServices(res?.data ?? [])).catch(() => {})
                            }
                          }}
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                          required
                        >
                          <option value="">Chọn Quận/Huyện</option>
                          {districts.map((d) => (
                            <option key={d.DistrictID} value={d.DistrictID}>
                              {d.DistrictName}
                            </option>
                          ))}
                        </select>

                        <select
                          value={selectedWard}
                          onChange={(e) => setSelectedWard(e.target.value)}
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                          required
                        >
                          <option value="">Chọn Phường/Xã</option>
                          {wards.map((w) => (
                            <option key={w.WardCode} value={w.WardCode}>
                              {w.WardName}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div className="grid grid-cols-2 gap-4 mb-4">
                        <select
                          value={selectedService}
                          onChange={(e) => setSelectedService(Number(e.target.value))}
                          className="px-4 py-2 border border-border rounded bg-card text-foreground"
                          required
                        >
                          <option value="">Chọn dịch vụ vận chuyển</option>
                          {services.map((s) => (
                            <option key={s.service_id} value={s.service_id}>
                              {s.short_name}
                            </option>
                          ))}
                        </select>

                        <Button type="button" onClick={handleCalculateShipping} disabled={!selectedService || !selectedDistrict || !selectedWard}>
                          Tính phí vận chuyển
                        </Button>
                      </div>

                      {loadingShip && <p className="text-sm text-muted-foreground">Đang tính phí vận chuyển...</p>}
                      {shippingFee !== null && (
                        <div className="mt-4 p-3 bg-muted rounded">
                          <div className="flex justify-between mb-2">
                            <span className="text-sm">Phí vận chuyển</span>
                            <span className="font-semibold">
                              {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(shippingFee)}
                            </span>
                          </div>
                          {leadtime && (
                            <p className="text-sm text-muted-foreground">
                              Dự kiến giao: {new Date(leadtime * 1000).toLocaleDateString('vi-VN', { 
                                weekday: 'short', 
                                year: 'numeric', 
                                month: 'short', 
                                day: 'numeric' 
                              })}
                            </p>
                          )}
                        </div>
                      )}

                      <div className="flex gap-3 mt-6">
                        <Button type="submit" variant="outline">
                          Tiếp tục thanh toán
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                </form>
              ) : (
                <form onSubmit={handlePlaceOrder}>
                  <Card>
                    <CardContent className="p-6">
                      <h2 className="text-xl font-semibold mb-6">Payment Method</h2>

                      <div className="space-y-3 mb-8">
                        <label className="flex items-center p-4 border border-border rounded cursor-pointer hover:bg-muted">
                          <input
                            type="radio"
                            name="payment"
                            value="cod"
                            checked={paymentMethod === "cod"}
                            onChange={(e) => setPaymentMethod(e.target.value as "cod")}
                            className="mr-3"
                          />
                          <div>
                            <div className="font-semibold">Cash on Delivery</div>
                            <div className="text-sm text-muted-foreground">Pay when you receive your order</div>
                          </div>
                        </label>

                        <label className="flex items-center p-4 border border-border rounded cursor-pointer hover:bg-muted">
                          <input
                            type="radio"
                            name="payment"
                            value="vnpay"
                            checked={paymentMethod === "vnpay"}
                            onChange={(e) => setPaymentMethod(e.target.value as "vnpay")}
                            className="mr-3"
                          />
                          <div>
                            <div className="font-semibold">VNPay</div>
                            <div className="text-sm text-muted-foreground">Secure online payment</div>
                          </div>
                        </label>
                      </div>

                      <div className="flex gap-3 mb-6">
                        <Button type="button" variant="outline" onClick={() => setStep("shipping")}>
                          Back
                        </Button>
                        <Button type="submit" className="flex-1" size="lg">
                          Place Order
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                </form>
              )}
            </div>

            {/* Order Summary */}
            <div>
              <Card className="sticky top-4">
                <CardContent className="p-6">
                  <h2 className="font-semibold text-lg mb-4">Order Summary</h2>

                  <div className="space-y-3 mb-4 pb-4 border-b border-border max-h-64 overflow-y-auto">
                    {cartItems.map((item) => (
                      <div key={item.id} className="flex justify-between text-sm">
                        <span className="text-muted-foreground">
                          {item.product.name} x{item.quantity}
                        </span>
                        <span>${(item.product.price * item.quantity).toFixed(2)}</span>
                      </div>
                    ))}
                  </div>

                  {step === "payment" && (
                    <div className="mb-4 pb-4 border-b border-border">
                      <div className="flex gap-2 mb-2">
                        <input
                          type="text"
                          placeholder="Voucher code"
                          value={voucherCode}
                          onChange={(e) => setVoucherCode(e.target.value.toUpperCase())}
                          className="flex-1 px-3 py-2 border border-border rounded text-sm bg-card text-foreground"
                        />
                        <Button type="button" variant="outline" size="sm">
                          Apply
                        </Button>
                      </div>
                      <p className="text-xs text-muted-foreground">Try: SAVE20</p>
                    </div>
                  )}

                  <div className="space-y-2 mb-4 pb-4 border-b border-border text-sm">
                    <div className="flex justify-between text-muted-foreground">
                      <span>Subtotal</span>
                      <span>${cartTotal.toFixed(2)}</span>
                    </div>

                    {shippingFee !== null && (
                      <div className="flex justify-between text-muted-foreground">
                        <span>Shipping</span>
                        <span>${shippingFee.toFixed(2)}</span>
                      </div>
                    )}

                    {discount > 0 && (
                      <div className="flex justify-between text-green-600">
                        <span>Discount</span>
                        <span>-${discount.toFixed(2)}</span>
                      </div>
                    )}
                  </div>

                  <div className="flex justify-between font-semibold text-lg">
                    <span>Total</span>
                    <span>${finalTotal.toFixed(2)}</span>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  )
}
