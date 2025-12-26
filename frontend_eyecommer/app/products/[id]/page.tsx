"use client"

import React, { useState, useEffect } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getProductById, getProducts, type ProductResponse } from "@/lib/services/product"
import { addToCart } from "@/lib/cart-store"

export default function ProductDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const unwrappedParams = React.use(params)
  const [product, setProduct] = useState<ProductResponse | null>(null)
  const [quantity, setQuantity] = useState(1)
  const [relatedProducts, setRelatedProducts] = useState<ProductResponse[]>([])
  const [showNotification, setShowNotification] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    async function fetchProduct() {
      setLoading(true)
      setError("")
      
      console.log('[Product Detail] params.id:', unwrappedParams.id, 'type:', typeof unwrappedParams.id)
      
      // Validate ID is a number
      const productId = parseInt(unwrappedParams.id, 10)
      console.log('[Product Detail] parsed productId:', productId)
      
      if (isNaN(productId) || productId <= 0) {
        setError(`Invalid product ID: ${unwrappedParams.id}`)
        setLoading(false)
        return
      }
      
      try {
        console.log('[Product Detail] Fetching product:', productId)
        const response = await getProductById(productId)
        console.log('[Product Detail] Response:', response)
        setProduct(response.data)
        
        // Fetch related products (all products for now, can filter by category later)
        const productsResponse = await getProducts({ pageNo: 0, pageSize: 8 })
        const related = productsResponse.data.items
          .filter((p) => p.id !== response.data.id)
          .slice(0, 4)
        setRelatedProducts(related)
      } catch (err: any) {
        console.error('[Product Detail] Error fetching product:', err)
        setError(err?.message || "Failed to load product")
      } finally {
        setLoading(false)
      }
    }

    fetchProduct()
  }, [unwrappedParams.id])

  const handleAddToCart = () => {
    if (product) {
      // Convert ProductResponse to Product format for cart
      const cartProduct = {
        id: product.id.toString(),
        name: product.name,
        price: product.price,
        image: product.thumbnailUrl || "",
        description: product.description || product.shortDescription || "",
        category: "fashion" as const, // Default category, can enhance later
        rating: 4.5,
        reviews: 0,
        stock: 100, // TODO: get from backend when available
        sku: `PROD-${product.id}`,
        material: "",
        color: "",
      }
      addToCart(cartProduct, quantity)
      setShowNotification(true)
      setTimeout(() => setShowNotification(false), 2000)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <Header />
        <main className="flex-1 flex items-center justify-center">
          <div className="text-center py-20">Loading product...</div>
        </main>
        <Footer />
      </div>
    )
  }

  if (error || !product) {
    return (
      <div className="min-h-screen flex flex-col bg-background">
        <Header />
        <main className="flex-1 flex items-center justify-center">
          <div className="text-center py-20">
            <p className="text-red-600 mb-4">{error || "Product not found"}</p>
            <Link href="/products">
              <Button>Back to Products</Button>
            </Link>
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
          {/* Breadcrumb */}
          <div className="flex items-center gap-2 mb-8 text-sm text-muted-foreground">
            <Link href="/">Home</Link>
            <span>/</span>
            <Link href="/products">Products</Link>
            <span>/</span>
            <span>{product.name}</span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-16">
            {/* Images */}
            <div>
              <div className="bg-muted rounded-lg aspect-square mb-4 overflow-hidden">
                <img
                  src={product.thumbnailUrl || "/placeholder.svg"}
                  alt={product.name}
                  className="w-full h-full object-cover"
                />
              </div>
            </div>

            {/* Details */}
            <div>
              <h1 className="text-3xl font-bold mb-3">{product.name}</h1>

              <div className="flex items-baseline gap-3 mb-6">
                <span className="text-3xl font-bold text-accent">
                  {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.price)}
                </span>
              </div>

              <p className="text-muted-foreground mb-6 leading-relaxed">
                {product.description || product.shortDescription}
              </p>

              {/* Specs */}
              <div className="space-y-3 mb-8">
                <div className="flex justify-between">
                  <span className="font-semibold">Status:</span>
                  <span className={product.status === 'ACTIVE' ? "text-green-600" : "text-gray-600"}>
                    {product.status}
                  </span>
                </div>
                {product.categories && product.categories.length > 0 && (
                  <div className="flex justify-between">
                    <span className="font-semibold">Categories:</span>
                    <span className="text-muted-foreground">
                      {product.categories.map((c: any) => c.name || c).join(", ")}
                    </span>
                  </div>
                )}
                <div className="flex justify-between">
                  <span className="font-semibold">Product ID:</span>
                  <span className="text-muted-foreground">#{product.id}</span>
                </div>
              </div>

              {/* Add to Cart */}
              <div className="flex items-center gap-4">
                <div className="flex items-center border border-border rounded">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="px-3 py-2 text-muted-foreground hover:bg-muted"
                  >
                    −
                  </button>
                  <span className="px-4 py-2">{quantity}</span>
                  <button
                    onClick={() => setQuantity(quantity + 1)}
                    className="px-3 py-2 text-muted-foreground hover:bg-muted"
                  >
                    +
                  </button>
                </div>
                <Button 
                  onClick={handleAddToCart} 
                  disabled={product.status !== 'ACTIVE'} 
                  className="flex-1" 
                  size="lg"
                >
                  Add to Cart
                </Button>
              </div>

              {showNotification && (
                <div className="mt-4 p-3 bg-green-100 text-green-800 rounded">
                  Added {quantity} item(s) to cart successfully!
                </div>
              )}
            </div>
          </div>

          {/* Related Products */}
          {relatedProducts.length > 0 && (
            <section className="mb-16">
              <h2 className="text-2xl font-bold mb-8">You May Also Like</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {relatedProducts.map((prod) => (
                  <Link key={prod.id} href={`/products/${prod.id}`}>
                    <Card className="h-full hover:shadow-lg transition-shadow cursor-pointer">
                      <CardContent className="p-4">
                        <div className="aspect-square bg-muted rounded mb-4 overflow-hidden">
                          <img
                            src={prod.thumbnailUrl || "/placeholder.svg"}
                            alt={prod.name}
                            className="w-full h-full object-cover"
                          />
                        </div>
                        <h3 className="font-semibold mb-2 line-clamp-2">{prod.name}</h3>
                        <div className="flex items-baseline gap-2">
                          <span className="text-lg font-bold text-accent">
                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(prod.price)}
                          </span>
                        </div>
                      </CardContent>
                    </Card>
                  </Link>
                ))}
              </div>
            </section>
          )}
        </div>
      </main>

      <Footer />
    </div>
  )
}
