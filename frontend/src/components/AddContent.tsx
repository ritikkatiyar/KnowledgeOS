"use client";

import { useState, useRef } from "react";

export default function AddContent({ onAdded }: { onAdded: () => void }) {
  const [activeTab, setActiveTab] = useState<"link" | "file">("link");
  const [url, setUrl] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [dragActive, setDragActive] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleLinkSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!url) return;
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
      console.error("Failed to add link content", err);
    } finally {
      setLoading(false);
    }
  };

  const handleFileSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;
    setLoading(true);
    try {
      const formData = new FormData();
      formData.append("file", file);

      const res = await fetch("http://localhost:8081/api/content/upload", {
        method: "POST",
        body: formData,
      });

      if (res.ok) {
        setFile(null);
        if (fileInputRef.current) fileInputRef.current.value = "";
        onAdded();
      }
    } catch (err) {
      console.error("Failed to upload file", err);
    } finally {
      setLoading(false);
    }
  };

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      setFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  return (
    <div className="glass p-8 rounded-3xl mb-12 border border-white/10 shadow-2xl">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-extrabold gradient-text">Capture Knowledge</h2>
        <div className="flex bg-white/5 border border-white/10 p-1 rounded-xl">
          <button
            onClick={() => setActiveTab("link")}
            className={`px-4 py-1.5 rounded-lg text-sm font-semibold transition-all ${
              activeTab === "link"
                ? "bg-blue-600 text-white shadow-lg"
                : "text-gray-400 hover:text-white"
            }`}
          >
            Paste Link
          </button>
          <button
            onClick={() => setActiveTab("file")}
            className={`px-4 py-1.5 rounded-lg text-sm font-semibold transition-all ${
              activeTab === "file"
                ? "bg-blue-600 text-white shadow-lg"
                : "text-gray-400 hover:text-white"
            }`}
          >
            Upload File
          </button>
        </div>
      </div>

      {activeTab === "link" ? (
        <form onSubmit={handleLinkSubmit} className="flex gap-4">
          <input
            type="url"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            placeholder="Paste YouTube URL, blog link, or technical resource URL..."
            className="flex-1 bg-white/5 border border-white/10 rounded-xl px-4 py-3 focus:outline-none focus:border-blue-500 transition-all text-white placeholder-gray-500"
            required
          />
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 hover:bg-blue-500 disabled:bg-blue-800 text-white font-bold px-8 py-3 rounded-xl transition-all glow active:scale-95"
          >
            {loading ? "Processing..." : "Capture Link"}
          </button>
        </form>
      ) : (
        <form onSubmit={handleFileSubmit} className="space-y-4">
          <div
            onDragEnter={handleDrag}
            onDragOver={handleDrag}
            onDragLeave={handleDrag}
            onDrop={handleDrop}
            onClick={() => fileInputRef.current?.click()}
            className={`border-2 border-dashed rounded-2xl p-8 text-center cursor-pointer transition-all ${
              dragActive
                ? "border-blue-500 bg-blue-500/10"
                : file
                ? "border-green-500 bg-green-500/5"
                : "border-white/10 hover:border-white/20 bg-white/5"
            }`}
          >
            <input
              type="file"
              ref={fileInputRef}
              onChange={handleFileChange}
              accept="application/pdf,image/png,image/jpeg,video/mp4"
              className="hidden"
            />
            
            {file ? (
              <div className="space-y-2">
                <div className="text-4xl">📄</div>
                <p className="text-green-400 font-semibold">{file.name}</p>
                <p className="text-xs text-gray-500">{(file.size / (1024 * 1024)).toFixed(2)} MB</p>
              </div>
            ) : (
              <div className="space-y-2">
                <div className="text-4xl text-gray-400">📤</div>
                <p className="text-gray-300 font-medium">Drag & drop your file here, or click to browse</p>
                <p className="text-xs text-gray-500">Supports PDF, PNG, JPEG, and MP4 (Up to 20MB)</p>
              </div>
            )}
          </div>

          {file && (
            <div className="flex justify-end gap-3">
              <button
                type="button"
                onClick={() => setFile(null)}
                className="px-6 py-2.5 rounded-xl border border-white/10 text-gray-400 hover:text-white transition-all text-sm font-semibold"
              >
                Clear
              </button>
              <button
                type="submit"
                disabled={loading}
                className="bg-blue-600 hover:bg-blue-500 disabled:bg-blue-800 text-white font-bold px-8 py-2.5 rounded-xl transition-all glow text-sm active:scale-95"
              >
                {loading ? "Processing..." : "Summarize File"}
              </button>
            </div>
          )}
        </form>
      )}
    </div>
  );
}
