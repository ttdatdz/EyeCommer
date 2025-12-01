export function Hero() {
  return (
    <section className="relative w-full overflow-hidden bg-linear-to-b from-amber-50 to-background">
      <div className="mx-auto max-w-7xl px-6 py-20 md:py-32 lg:py-40">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          {/* Text Content */}
          <div className="space-y-8">
            <div className="space-y-4">
              <p className="text-sm font-light tracking-widest text-accent uppercase">Khám phá bộ sưu tập mới</p>
              <h1 className="font-serif text-5xl md:text-6xl font-light leading-tight text-foreground text-balance">
                Nhìn rõ, Sống tươi
              </h1>
              <p className="text-lg font-light text-foreground/70 max-w-md">
                Những chiếc kính mắt được thiết kế tinh tế kết hợp với công nghệ hiện đại nhất để mang đến trải nghiệm
                thị giác tuyệt vời.
              </p>
            </div>

            {/* CTA Buttons */}
            <div className="flex gap-4 pt-4">
              <button className="px-8 py-3 bg-primary text-primary-foreground font-light hover:opacity-90 transition-opacity rounded-lg">
                Mua sắm ngay
              </button>
              <button className="px-8 py-3 border border-foreground text-foreground font-light hover:bg-foreground/5 transition-colors rounded-lg">
                Tìm hiểu thêm
              </button>
            </div>
          </div>

          {/* Hero Image */}
          <div className="relative h-96 md:h-full rounded-lg overflow-hidden bg-muted">
            <img src="/stylish-eyeglasses-on-gradient-background.jpg" alt="Bộ sưu tập kính mắt" className="w-full h-full object-cover" />
          </div>
        </div>
      </div>
    </section>
  )
}
