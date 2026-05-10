"use client";

import { useState } from "react";

export default function AddContent({ onAdded }: { onAdded: () => void }) {
  const [url, setUrl] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8081/api/content", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ url }),
      });
      if (res.ok) {
        setUrl("");
        onAdded();
      }
    } catch (err) {
      console.error("Failed to add content", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="glass p-8 rounded-3xl mb-12">
      <h2 className="text-2xl font-bold mb-6 gradient-text">Capture Knowledge</h2>
      <form onSubmit={handleSubmit} className="flex gap-4">
        <input
          type="url"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          placeholder="Paste YouTube URL, blog link, or resource URL..."
          className="flex-1 bg-white/5 border border-white/10 rounded-xl px-4 py-3 focus:outline-none focus:border-blue-500 transition-all"
          required
        />
        <button
          type="submit"
          disabled={loading}
          className="bg-blue-600 hover:bg-blue-500 disabled:bg-blue-800 text-white font-bold px-8 py-3 rounded-xl transition-all glow"
        >
          {loading ? "Processing..." : "Capture"}
        </button>
      </form>
    </div>
  );
}
