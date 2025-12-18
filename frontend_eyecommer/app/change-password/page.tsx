import { Suspense } from "react"
import ChangePasswordClient from "./ChangePasswordClient"

export default function Page() {
  return (
    <Suspense fallback={<div className="text-center py-10">Loading...</div>}>
      <ChangePasswordClient />
    </Suspense>
  )
}
