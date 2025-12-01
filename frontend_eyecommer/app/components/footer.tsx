import { Mail, Phone, MapPin } from "lucide-react"

export function Footer() {
  return (
    <footer id="contact" className="w-full bg-foreground text-background pt-16 pb-8">
      <div className="mx-auto max-w-7xl px-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-12">
          {/* Brand */}
          <div className="space-y-4">
            <h3 className="font-serif text-2xl font-light">OPTICA</h3>
            <p className="text-sm font-light opacity-70">Khám phá thế giới của những chiếc kính mắt tinh tế</p>
          </div>

          {/* Quick Links */}
          <div className="space-y-4">
            <h4 className="font-serif text-lg font-light">Nhanh chóng</h4>
            <ul className="space-y-2 text-sm font-light opacity-70">
              <li>
                <a href="#" className="hover:opacity-100 transition-opacity">
                  Bộ sưu tập
                </a>
              </li>
              <li>
                <a href="#" className="hover:opacity-100 transition-opacity">
                  Về chúng tôi
                </a>
              </li>
              <li>
                <a href="#" className="hover:opacity-100 transition-opacity">
                  Blog
                </a>
              </li>
            </ul>
          </div>

          {/* Support */}
          <div className="space-y-4">
            <h4 className="font-serif text-lg font-light">Hỗ trợ</h4>
            <ul className="space-y-2 text-sm font-light opacity-70">
              <li>
                <a href="#" className="hover:opacity-100 transition-opacity">
                  Câu hỏi thường gặp
                </a>
              </li>
              <li>
                <a href="#" className="hover:opacity-100 transition-opacity">
                  Chính sách đổi trả
                </a>
              </li>
              <li>
                <a href="#" className="hover:opacity-100 transition-opacity">
                  Liên hệ
                </a>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div className="space-y-4">
            <h4 className="font-serif text-lg font-light">Liên hệ</h4>
            <div className="space-y-3 text-sm font-light opacity-70">
              <div className="flex items-center gap-2">
                <Phone className="w-4 h-4" />
                <a href="tel:+84123456789" className="hover:opacity-100 transition-opacity">
                  +84 123 456 789
                </a>
              </div>
              <div className="flex items-center gap-2">
                <Mail className="w-4 h-4" />
                <a href="mailto:hello@optica.com" className="hover:opacity-100 transition-opacity">
                  hello@optica.com
                </a>
              </div>
              <div className="flex items-start gap-2">
                <MapPin className="w-4 h-4 mt-1" />
                <span>123 Đường Trang, TP. Hồ Chí Minh</span>
              </div>
            </div>
          </div>
        </div>

        <div className="border-t border-background/20 pt-8 flex flex-col md:flex-row items-center justify-between text-xs font-light opacity-60">
          <p>&copy; 2025 OPTICA. Tất cả quyền được bảo lưu.</p>
          <div className="flex gap-6 mt-4 md:mt-0">
            <a href="#" className="hover:opacity-100 transition-opacity">
              Chính sách bảo mật
            </a>
            <a href="#" className="hover:opacity-100 transition-opacity">
              Điều khoản sử dụng
            </a>
          </div>
        </div>
      </div>
    </footer>
  )
}
