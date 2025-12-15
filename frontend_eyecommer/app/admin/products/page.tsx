"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Header from "@/components/layout/header"
import Footer from "@/components/layout/footer"
import ProductForm from "@/components/forms/product-form"
import { getCurrentUser, logout } from "@/lib/auth-store"
import { getAllProducts, addProduct, updateProduct, deleteProduct } from "@/lib/admin-store"
import type { Product } from "@/lib/types"

export default function AdminProductsPage() {
  const router = useRouter()
  const [user, setUser] = useState<any>(null)
  const [products, setProducts] = useState<Product[]>([])
  const [showForm, setShowForm] = useState(false)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [searchQuery, setSearchQuery] = useState("")

  useEffect(() => {
    const currentUser = getCurrentUser()
    if (!currentUser || currentUser.role !== "admin") {
      router.push("/login")
      return
    }
    setUser(currentUser)
    setProducts(getAllProducts())
  }, [router])

  const handleLogout = () => {
    logout()
    router.push("/")
  }

  const handleAddProduct = (formData: Omit<Product, "id">) => {
    const newProduct = addProduct(formData)
    setProducts(getAllProducts())
    setShowForm(false)
    alert("Product added successfully!")
  }

  const handleUpdateProduct = (formData: Omit<Product, "id">) => {
    if (editingProduct) {
      updateProduct(editingProduct.id, formData)
      setProducts(getAllProducts())
      setEditingProduct(null)
      setShowForm(false)
      alert("Product updated successfully!")
    }
  }

  const handleDeleteProduct = (productId: string) => {
    if (confirm("Are you sure you want to delete this product?")) {
      deleteProduct(productId)
      setProducts(getAllProducts())
      alert("Product deleted successfully!")
    }
  }

  const handleEditClick = (product: Product) => {
    setEditingProduct(product)
    setShowForm(true)
  }

  const handleCancel = () => {
    setShowForm(false)
    setEditingProduct(null)
  }

  if (!user) return null

  const filteredProducts = products.filter(
    (p) =>
      p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.sku.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  return (
    <div className="min-h-screen flex flex-col bg-background">
      <Header />

      <main className="flex-1">
        <div className="container mx-auto px-4 py-8">
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold">Product Management</h1>
            <Button variant="outline" onClick={handleLogout}>
              Logout
            </Button>
          </div>

          {showForm ? (
            <div className="mb-8">
              <ProductForm
                product={editingProduct || undefined}
                onSubmit={editingProduct ? handleUpdateProduct : handleAddProduct}
                onCancel={handleCancel}
              />
            </div>
          ) : (
            <>
              <div className="flex gap-4 mb-6">
                <input
                  type="text"
                  placeholder="Search by name or SKU..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="flex-1 px-4 py-2 border border-border rounded-md"
                />
                <Button onClick={() => setShowForm(true)}>Add New Product</Button>
              </div>

              <Card>
                <CardContent className="p-0">
                  <div className="overflow-x-auto">
                    <table className="w-full">
                      <thead>
                        <tr className="border-b border-border bg-muted/50">
                          <th className="p-4 text-left font-semibold">Name</th>
                          <th className="p-4 text-left font-semibold">SKU</th>
                          <th className="p-4 text-left font-semibold">Category</th>
                          <th className="p-4 text-right font-semibold">Price</th>
                          <th className="p-4 text-left font-semibold">Stock</th>
                          <th className="p-4 text-left font-semibold">Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filteredProducts.map((product) => (
                          <tr key={product.id} className="border-b border-border hover:bg-muted/50">
                            <td className="p-4 font-semibold">{product.name}</td>
                            <td className="p-4 font-mono text-xs text-muted-foreground">{product.sku}</td>
                            <td className="p-4 capitalize">{product.category}</td>
                            <td className="p-4 text-right font-semibold">${product.price.toFixed(2)}</td>
                            <td className="p-4">
                              <span
                                className={`px-2 py-1 text-xs rounded font-medium ${
                                  product.stock > 20
                                    ? "bg-green-100 text-green-800"
                                    : product.stock > 0
                                      ? "bg-yellow-100 text-yellow-800"
                                      : "bg-red-100 text-red-800"
                                }`}
                              >
                                {product.stock}
                              </span>
                            </td>
                            <td className="p-4 flex gap-2">
                              <Button size="sm" variant="outline" onClick={() => handleEditClick(product)}>
                                Edit
                              </Button>
                              <Button
                                size="sm"
                                variant="outline"
                                className="text-red-600 hover:text-red-700 bg-transparent"
                                onClick={() => handleDeleteProduct(product.id)}
                              >
                                Delete
                              </Button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                  {filteredProducts.length === 0 && (
                    <div className="p-8 text-center text-muted-foreground">No products found</div>
                  )}
                </CardContent>
              </Card>
            </>
          )}
        </div>
      </main>

      <Footer />
    </div>
  )
}
