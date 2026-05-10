"use client";

import { useEffect, useState } from "react";
import AddContent from "@/components/AddContent";
import ContentCard from "@/components/ContentCard";

export default function Home() {
  const [contents, setContents] = useState([]);

  const fetchContent = async () => {
    try {
      const res = await fetch("http://localhost:8081/api/content");
      if (res.ok) {
        const data = await res.json();
        setContents(data);
      }
    } catch (err) {
      console.error("Failed to fetch content", err);
    }
  };

  useEffect(() => {
    fetchContent();
    // Poll for updates every 10 seconds for MVP processing status
    const interval = setInterval(fetchContent, 10000);
    return () => clearInterval(interval);
  }, []);

  return (
    <main className="min-h-screen p-8 md:p-24 max-w-7xl mx-auto">
      <header className="mb-16 flex justify-between items-end">
        <div>
          <h1 className="text-5xl font-extrabold mb-4 tracking-tight gradient-text">
            KnowledgeOS
          </h1>
          <p className="text-gray-400 text-lg">
            Your second brain for compounding technical knowledge.
          </p>
        </div>
        <div className="hidden md:block">
          <div className="text-right">
            <span className="text-3xl font-bold">{contents.length}</span>
            <p className="text-xs uppercase tracking-widest text-gray-500">Resources Captured</p>
          </div>
        </div>
      </header>

      <AddContent onAdded={fetchContent} />

      <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {contents.map((item: any) => (
          <ContentCard key={item.id} content={item} />
        ))}
        
        {contents.length === 0 && (
          <div className="col-span-full py-24 text-center glass rounded-3xl">
            <p className="text-gray-500 italic">No knowledge captured yet. Start by adding a URL above.</p>
          </div>
        )}
      </section>
    </main>
  );
}
