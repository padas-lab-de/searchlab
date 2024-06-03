"use client";

import Link from "next/link";
import { ModeToggle } from "./mode-toggle";
import { useTheme } from "next-themes";

export function Navbar() {
  const { theme } = useTheme();
  return (
    <header className="w-full flex fixed p-1 z-50 px-2 bg-transparent justify-between items-center">
      <div>
        <Link href="/" passHref onClick={() => location.reload()}>
          <img
            src={theme === "light" ? "/logo-black.png" : "/logo-white.png"}
            alt="Logo"
            className="w-36 h-11 mt-2 ml-5"
          />
        </Link>
      </div>
      <div>
        <ModeToggle />
      </div>
    </header>
  );
}