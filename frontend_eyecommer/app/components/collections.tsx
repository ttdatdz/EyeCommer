export function Collections() {
  const collections = [
    {
      id: 1,
      name: "Sunglasses",
      description: "Bảo vệ và phong cách cho những ngày nắng đẹp",
      image: "/luxury-sunglasses.png",
    },
    {
      id: 2,
      name: "Eyeglasses",
      description: "Kính cận thiết kế hiện đại cho công việc hàng ngày",
      image: "/modern-eyeglasses-frames.jpg",
    },
    {
      id: 3,
      name: "Premium Collection",
      description: "Những sáng tạo đặc biệt từ các thương hiệu nổi tiếng",
      image: "/premium-designer-glasses.jpg",
    },
  ]

  return (
    <section id="collections" className="w-full py-20 md:py-32">
      <div className="mx-auto max-w-7xl px-6">
        <div className="text-center mb-16 space-y-4">
          <p className="text-sm font-light tracking-widest text-accent uppercase">Bộ sưu tập</p>
          <h2 className="font-serif text-4xl md:text-5xl font-light text-foreground">Khám phá những loại kính</h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {collections.map((collection) => (
            <div key={collection.id} className="group cursor-pointer overflow-hidden rounded-lg">
              <div className="relative h-64 overflow-hidden bg-muted rounded-lg mb-4">
                <img
                  src={collection.image || "/placeholder.svg"}
                  alt={collection.name}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                />
              </div>
              <div className="space-y-2">
                <h3 className="font-serif text-xl font-light text-foreground">{collection.name}</h3>
                <p className="text-sm font-light text-foreground/60">{collection.description}</p>
                <button className="pt-2 text-sm font-light text-accent hover:underline transition-all">
                  Xem bộ sưu tập →
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
