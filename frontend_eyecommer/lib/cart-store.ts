// Client-side cart management using in-memory store
// In production, use IndexedDB or sync with server

import type { CartItem, Product } from "./types"

interface CartStore {
  items: CartItem[]
  addItem: (product: Product, quantity: number) => void
  removeItem: (productId: string) => void
  updateQuantity: (productId: string, quantity: number) => void
  clear: () => void
  getItems: () => CartItem[]
  getTotal: () => number
}

const cartStore: CartStore = {
  items: [],
  addItem(product, quantity) {
    const existing = this.items.find((item) => item.productId === product.id)
    if (existing) {
      existing.quantity += quantity
    } else {
      this.items.push({
        id: Math.random().toString(),
        productId: product.id,
        product,
        quantity,
        addedAt: new Date(),
      })
    }
  },
  removeItem(productId) {
    this.items = this.items.filter((item) => item.productId !== productId)
  },
  updateQuantity(productId, quantity) {
    const item = this.items.find((item) => item.productId === productId)
    if (item) {
      item.quantity = quantity
    }
  },
  clear() {
    this.items = []
  },
  getItems() {
    return this.items
  },
  getTotal() {
    return this.items.reduce((total, item) => total + item.product.price * item.quantity, 0)
  },
}

export function useCart() {
  return cartStore
}

export function addToCart(product: Product, quantity: number) {
  cartStore.addItem(product, quantity)
}

export function removeFromCart(productId: string) {
  cartStore.removeItem(productId)
}

export function updateCartQuantity(productId: string, quantity: number) {
  if (quantity <= 0) {
    cartStore.removeItem(productId)
  } else {
    cartStore.updateQuantity(productId, quantity)
  }
}

export function getCart() {
  return cartStore.getItems()
}

export function getCartTotal() {
  return cartStore.getTotal()
}

export function clearCart() {
  cartStore.clear()
}
