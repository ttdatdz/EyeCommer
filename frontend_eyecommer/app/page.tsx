import { Hero } from "./components/hero"
import { Collections } from "./components/collections"
import { About } from "./components/about"
import { Footer } from "./components/footer"
import { Header } from "./components/header"
export default function Home() {
  return (
    <div className="min-h-screen bg-background">
      <Header />
      <Hero />
      <Collections />
      <About />
      <Footer />
    </div>
  )
}
