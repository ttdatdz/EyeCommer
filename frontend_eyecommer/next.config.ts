import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    // Proxy API requests to the backend to avoid CORS in the browser
    // Uses NEXT_PUBLIC_API_PROXY_TARGET if provided, else defaults to local Spring Boot
    const target = process.env.NEXT_PUBLIC_API_PROXY_TARGET || "http://localhost:8080";
    return [
      {
        source: "/api/:path*",
        destination: `${target}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;
