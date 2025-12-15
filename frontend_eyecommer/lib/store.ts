// Simple in-memory store for demo purposes
// In production, replace with database calls

export const mockProducts: Record<string, any> = {
  "aviator-black": {
    id: "aviator-black",
    name: "Classic Aviator - Black",
    sku: "AVT-001-BLK",
    category: "sunglasses",
    price: 99.99,
    originalPrice: 149.99,
    description: "Timeless aviator sunglasses with UV protection and premium frames.",
    image: "/black-aviator-sunglasses.jpg",
    images: ["/black-aviator-sunglasses-front.jpg", "/black-aviator-sunglasses-side.jpg"],
    color: "Black",
    material: "Titanium",
    stock: 45,
    rating: 4.5,
    reviews: 128,
    featured: true,
  },
  "prescription-blue": {
    id: "prescription-blue",
    name: "Prescription Blue Light - Modern",
    sku: "PRSC-002-BLU",
    category: "prescription",
    price: 149.99,
    description: "Modern prescription frames with blue light filtering lens technology.",
    image: "/modern-blue-prescription-glasses.jpg",
    images: ["/modern-blue-prescription-glasses-front.jpg"],
    color: "Blue",
    material: "Acetate",
    stock: 120,
    rating: 4.7,
    reviews: 89,
    featured: true,
  },
  "fashion-gold": {
    id: "fashion-gold",
    name: "Fashion Oversized - Gold",
    sku: "FASH-003-GLD",
    category: "fashion",
    price: 79.99,
    originalPrice: 119.99,
    description: "Trendy oversized frames with a luxurious gold finish.",
    image: "/gold-oversized-fashion-glasses.jpg",
    images: ["/gold-oversized-fashion-glasses-front.jpg"],
    color: "Gold",
    material: "Metal",
    stock: 65,
    rating: 4.3,
    reviews: 156,
    featured: true,
  },
  "aviator-brown": {
    id: "aviator-brown",
    name: "Classic Aviator - Brown",
    sku: "AVT-001-BRN",
    category: "sunglasses",
    price: 99.99,
    description: "Elegant brown-tinted aviator sunglasses for timeless style.",
    image: "/brown-aviator-sunglasses.jpg",
    images: [],
    color: "Brown",
    material: "Titanium",
    stock: 32,
    rating: 4.6,
    reviews: 95,
  },
  "cat-eye-black": {
    id: "cat-eye-black",
    name: "Retro Cat Eye - Black",
    sku: "CATE-004-BLK",
    category: "fashion",
    price: 89.99,
    originalPrice: 129.99,
    description: "Stylish retro cat-eye frames with a vintage aesthetic.",
    image: "/black-cat-eye-vintage-glasses.jpg",
    images: [],
    color: "Black",
    material: "Acetate",
    stock: 48,
    rating: 4.4,
    reviews: 112,
    featured: true,
  },
  "wayfarer-tortoise": {
    id: "wayfarer-tortoise",
    name: "Wayfarer - Tortoiseshell",
    sku: "WAY-005-TRT",
    category: "sunglasses",
    price: 119.99,
    description: "Classic wayfarer-style sunglasses with tortoiseshell pattern.",
    image: "/tortoiseshell-wayfarer-sunglasses.jpg",
    images: [],
    color: "Tortoiseshell",
    material: "Acetate",
    stock: 56,
    rating: 4.8,
    reviews: 203,
  },
  "round-rose": {
    id: "round-rose",
    name: "Round Vintage - Rose",
    sku: "RND-006-RSE",
    category: "fashion",
    price: 69.99,
    description: "Charming round frames with a rosy metallic finish.",
    image: "/rose-gold-round-vintage-glasses.jpg",
    images: [],
    color: "Rose Gold",
    material: "Metal",
    stock: 78,
    rating: 4.2,
    reviews: 67,
  },
  "sport-black": {
    id: "sport-black",
    name: "Sport Performance - Black",
    sku: "SPT-007-BLK",
    category: "sunglasses",
    price: 139.99,
    description: "High-performance sport sunglasses with anti-glare lens technology.",
    image: "/black-sport-performance-sunglasses.jpg",
    images: [],
    color: "Black",
    material: "Polycarbonate",
    stock: 34,
    rating: 4.6,
    reviews: 81,
  },
}

export function getProducts() {
  return Object.values(mockProducts)
}

export function getProductById(id: string) {
  return mockProducts[id]
}

export function getProductsByCategory(category: string) {
  return Object.values(mockProducts).filter((p) => p.category === category)
}

export function searchProducts(query: string) {
  const q = query.toLowerCase()
  return Object.values(mockProducts).filter(
    (p) => p.name.toLowerCase().includes(q) || p.description.toLowerCase().includes(q),
  )
}
