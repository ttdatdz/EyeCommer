"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getProductById, getProducts } from "@/lib/store"
import { addToCart } from "@/lib/cart-store"
import type { Product } from "@/lib/types"

export default function ProductDetailPage({ params }: { params: { id: string } }) {
  const [product, setProduct] = useState<Product | null>(null)
  const [quantity, setQuantity] = useState(1)
  const [relatedProducts, setRelatedProducts] = useState<Product[]>([])
  const [showNotification, setShowNotification] = useState(false)

  useEffect(() => {
    const prod = getProductById(params.id)
    if (prod) {
      setProduct(prod)
      const related = getProducts()
        .filter((p) => p.category === prod.category && p.id !== prod.id)
        .slice(0, 4)
      setRelatedProducts(related)
    }
  }, [params.id])

  const handleAddToCart = () => {
    if (product) {
      addToCart(product, quantity)
      setShowNotification(true)
      setTimeout(() => setShowNotification(false), 2000)
    }
  }

  if (!product) {
    return <div>Loading...</div>
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
                  src={product.image || "/placeholder.svg"}
                  alt={product.name}
                  className="w-full h-full object-cover"
                />
              </div>
              {product.images && product.images.length > 0 && (
                <div className="flex gap-3">
                  {product.images.map((img, i) => (
                    <div key={i} className="w-20 h-20 bg-muted rounded cursor-pointer">
                      <img
                        src={img || "/placeholder.svg"}
                        alt={`${product.name} ${i + 1}`}
                        className="w-full h-full object-cover"
                      />
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Details */}
            <div>
              <h1 className="text-3xl font-bold mb-3">{product.name}</h1>

              <div className="flex items-center gap-3 mb-6">
                <div className="flex items-center gap-1">
                  <span className="text-yellow-500">★★★★★</span>
                  <span className="font-semibold">{product.rating}</span>
                </div>
                <span className="text-muted-foreground">({product.reviews} reviews)</span>
              </div>

              <div className="flex items-baseline gap-3 mb-6">
                <span className="text-3xl font-bold text-accent">${product.price.toFixed(2)}</span>
                {product.originalPrice && (
                  <span className="text-lg text-muted-foreground line-through">
                    ${product.originalPrice.toFixed(2)}
                  </span>
                )}
              </div>

              <p className="text-muted-foreground mb-6 leading-relaxed">{product.description}</p>

              {/* Specs */}
              <div className="space-y-3 mb-8">
                <div className="flex justify-between">
                  <span className="font-semibold">Color:</span>
                  <span className="text-muted-foreground">{product.color}</span>
                </div>
                <div className="flex justify-between">
                  <span className="font-semibold">Material:</span>
                  <span className="text-muted-foreground">{product.material}</span>
                </div>
                <div className="flex justify-between">
                  <span className="font-semibold">SKU:</span>
                  <span className="text-muted-foreground">{product.sku}</span>
                </div>
                <div className="flex justify-between">
                  <span className="font-semibold">Stock:</span>
                  <span className={product.stock > 0 ? "text-green-600" : "text-red-600"}>
                    {product.stock > 0 ? `${product.stock} Available` : "Out of Stock"}
                  </span>
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
                <Button onClick={handleAddToCart} disabled={product.stock <= 0} className="flex-1" size="lg">
                  Add to Cart
                </Button>
              </div>

              {showNotification && (
                <div className="mt-4 p-3 bg-green-100 text-green-800 rounded">Added to cart successfully!</div>
              )}
            </div>
          </div>

          {/* Related Products */}
          {relatedProducts.length > 0 && (
            <section className="mb-16">
              <h2 className="text-2xl font-bold mb-8">Related Products</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {relatedProducts.map((prod) => (
                  <Link key={prod.id} href={`/products/${prod.id}`}>
                    <Card className="h-full hover:shadow-lg transition-shadow cursor-pointer">
                      <CardContent className="p-4">
                        <div className="aspect-square bg-muted rounded mb-4 overflow-hidden">
                          <img
                            src={prod.image || "/placeholder.svg"}
                            alt={prod.name}
                            className="w-full h-full object-cover"
                          />
                        </div>
                        <h3 className="font-semibold mb-2 line-clamp-2">{prod.name}</h3>
                        <div className="flex items-baseline gap-2">
                          <span className="text-lg font-bold text-accent">${prod.price.toFixed(2)}</span>
                          {prod.originalPrice && (
                            <span className="text-sm text-muted-foreground line-through">
                              ${prod.originalPrice.toFixed(2)}
                            </span>
                          )}
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
