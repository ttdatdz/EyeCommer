export function About() {
  return (
    <section id="about" className="w-full py-20 md:py-32 bg-muted/50">
      <div className="mx-auto max-w-7xl px-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          {/* Image */}
          <div className="relative h-96 rounded-lg overflow-hidden bg-muted order-2 md:order-1">
            <img src="/eyewear-store-interior-elegant.jpg" alt="Cửa hàng OPTICA" className="w-full h-full object-cover" />
          </div>

          {/* Content */}
          <div className="space-y-6 order-1 md:order-2">
            <div className="space-y-2">
              <p className="text-sm font-light tracking-widest text-accent uppercase">Về OPTICA</p>
              <h2 className="font-serif text-4xl font-light text-foreground">Tầm nhìn rõ, Cuộc sống rực rỡ</h2>
            </div>

            <p className="text-base font-light text-foreground/70 leading-relaxed">
              Tại OPTICA, chúng tôi tin rằng mỗi chiếc kính mắt không chỉ là một công cụ để nhìn rõ, mà còn là một phần
              của cá tính bạn. Với hơn 20 năm kinh nghiệm, chúng tôi tự hào mang đến những sản phẩm chất lượng cao từ
              các thương hiệu hàng đầu thế giới.
            </p>

            <div className="space-y-4 pt-2">
              <div className="flex gap-4">
                <div className="w-1 bg-accent"></div>
                <div>
                  <h3 className="font-serif text-lg font-light text-foreground">Chất lượng tuyệt vời</h3>
                  <p className="text-sm font-light text-foreground/60">Tất cả sản phẩm đều được kiểm tra kỹ lưỡng</p>
                </div>
              </div>
              <div className="flex gap-4">
                <div className="w-1 bg-accent"></div>
                <div>
                  <h3 className="font-serif text-lg font-light text-foreground">Hỗ trợ chuyên nghiệp</h3>
                  <p className="text-sm font-light text-foreground/60">Nhân viên giàu kinh nghiệm sẵn sàng tư vấn</p>
                </div>
              </div>
            </div>

            <button className="mt-6 px-8 py-3 bg-primary text-primary-foreground font-light hover:opacity-90 transition-opacity rounded-lg">
              Liên hệ với chúng tôi
            </button>
          </div>
        </div>
      </div>
    </section>
  )
}
