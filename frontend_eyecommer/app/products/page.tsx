"use client"

import { Suspense, useState, useEffect } from "react"
import Link from "next/link"
import { useSearchParams } from "next/navigation"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import { getProducts, type ProductResponse } from "@/lib/services/product"

function ProductsContent() {
  const searchParams = useSearchParams()
  const category = searchParams.get("category")
  const [products, setProducts] = useState<ProductResponse[]>([])
  const [sortBy, setSortBy] = useState("featured")
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    async function fetchProducts() {
      setLoading(true)
      setError("")
      try {
        // TODO: Add category filter when backend supports it
        const response = await getProducts({ pageNo: 0, pageSize: 100 })
        let items = response.data.items

        // Client-side sort
        if (sortBy === "price-low") {
          items = [...items].sort((a, b) => a.price - b.price)
        } else if (sortBy === "price-high") {
          items = [...items].sort((a, b) => b.price - a.price)
        }

        setProducts(items)
      } catch (err: any) {
        setError(err?.message || "Failed to load products")
      } finally {
        setLoading(false)
      }
    }

    fetchProducts()
  }, [category, sortBy])

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <h1 className="text-3xl font-bold mb-2">
            {category ? `${category.charAt(0).toUpperCase() + category.slice(1)} Eyewear` : "All Products"}
          </h1>
          <p className="text-muted-foreground mb-6">{products.length} products</p>

          <div className="flex gap-4 mb-8">
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="px-4 py-2 border border-border rounded bg-card text-foreground"
            >
              <option value="featured">Featured</option>
              <option value="price-low">Price: Low to High</option>
              <option value="price-high">Price: High to Low</option>
            </select>
          </div>

          {loading && <div className="text-center py-10">Loading products...</div>}
          {error && <div className="text-center py-10 text-red-600">{error}</div>}

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {products.map((product) => (
              <Link key={product.id} href={`/products/${product.id}`}>
                <Card className="h-full hover:shadow-lg transition-shadow cursor-pointer">
                  <CardContent className="p-4">
                    <div className="aspect-square bg-muted rounded mb-4 overflow-hidden">
                      <img
                        src={product.thumbnailUrl || "/placeholder.svg"}
                        alt={product.name}
                        className="w-full h-full object-cover hover:scale-105 transition-transform"
                      />
                    </div>
                    <h3 className="font-semibold mb-2 line-clamp-2">{product.name}</h3>
                    <div className="flex items-baseline gap-2 mb-3">
                      <span className="text-lg font-bold text-accent">
                        {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(product.price)}
                      </span>
                    </div>
                    {product.shortDescription && (
                      <p className="text-sm text-muted-foreground line-clamp-2">{product.shortDescription}</p>
                    )}
                  </CardContent>
                </Card>
              </Link>
            ))}
          </div>
        </div>
      </main>

      <Footer />
    </div>
  )
}

export default function ProductsPage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <ProductsContent />
    </Suspense>
  )
}
