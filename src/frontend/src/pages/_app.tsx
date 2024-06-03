import { ThemeProvider } from "@/components/theme-provider";
import { Toaster } from "@/components/ui/toaster";
import Providers from "@/providers";
import "./globals.css";
import { cn } from "@/lib/utils";
import { Analytics } from "@vercel/analytics/react";
import { Navbar } from "@/components/nav";
import { Inter } from "next/font/google";
import Meta from "@/components/header";
import { Footer } from "@/components/footer";

const inter = Inter({
  subsets: ["latin"],
  display: "swap",
});



const metadata = {
  title: "SearchSim",
  description: "Conversational and Traditional Search User Study",
  metadataBase: new URL("https://searchsim.io"), 
  openGraph: {
    title: "Your Open Graph Title",
    description: "Your Open Graph Description",
  },
};

export default function MyApp({
  Component,
  pageProps,
}: {
  Component: React.ComponentType;
  pageProps: any;
}) {
  return (
    <>
      <Meta metadata={metadata} />
      <Providers>
        <ThemeProvider
          attribute="class"
          defaultTheme="dark"
          enableSystem
          disableTransitionOnChange
        >
          <Navbar />
          <div className="font-product-sans">
            <Component {...pageProps} />
          </div>
          <Toaster />
          <Footer />
          <Analytics />
        </ThemeProvider>
      </Providers>
    </>

  );
}