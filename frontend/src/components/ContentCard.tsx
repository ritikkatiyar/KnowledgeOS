"use client";

import { useState } from "react";

interface Content {
  id: number;
  title: string;
  summary: string;
  tags: string[];
  status: string;
  sourceUrl: string;
  rawText?: string;
  fileName?: string;
  fileType?: string;
}

export default function ContentCard({ content }: { content: Content }) {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      {/* Card Element */}
      <div 
        onClick={() => setIsOpen(true)}
        className="glass p-6 rounded-2xl hover:border-blue-500/50 hover:shadow-[0_0_30px_rgba(59,130,246,0.15)] transition-all duration-300 group cursor-pointer flex flex-col justify-between h-full"
      >
        <div>
          <div className="flex justify-between items-start gap-4 mb-4">
            <h3 className="text-xl font-bold group-hover:text-blue-400 transition-colors line-clamp-2">
              {content.title}
            </h3>
            <span className={`text-xs px-2.5 py-1 rounded-full font-semibold shrink-0 ${
              content.status === 'COMPLETED' ? 'bg-green-500/20 text-green-400' : 
              content.status === 'PROCESSING' ? 'bg-yellow-500/20 text-yellow-400' :
              'bg-red-500/20 text-red-400'
            }`}>
              {content.status}
            </span>
          </div>
          
          <p className="text-gray-400 text-sm mb-4 line-clamp-4 leading-relaxed">
            {content.summary}
          </p>
        </div>

        <div>
          <div className="flex flex-wrap gap-2 mb-4">
            {content.tags?.map((tag) => (
              <span key={tag} className="text-[10px] font-semibold uppercase tracking-wider bg-white/5 border border-white/5 px-2 py-0.5 rounded-md text-gray-400">
                {tag}
              </span>
            ))}
          </div>

          <div className="flex justify-between items-center text-xs">
            {content.fileName ? (
              <span className="text-gray-500 flex items-center gap-1 font-mono">
                📎 {content.fileName}
              </span>
            ) : content.sourceUrl ? (
              <a 
                href={content.sourceUrl} 
                target="_blank" 
                rel="noopener noreferrer"
                onClick={(e) => e.stopPropagation()}
                className="text-blue-500 hover:underline flex items-center gap-1"
              >
                View Source ↗
              </a>
            ) : (
              <span className="text-gray-600">Text Source</span>
            )}
            
            <span className="text-blue-400 group-hover:translate-x-1 transition-transform duration-300 font-semibold">
              Read More →
            </span>
          </div>
        </div>
      </div>

      {/* Details Modal */}
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 md:p-8 animate-fade-in">
          {/* Backdrop */}
          <div 
            onClick={() => setIsOpen(false)}
            className="absolute inset-0 bg-black/80 backdrop-blur-md"
          />

          {/* Modal Content */}
          <div className="relative glass w-full max-w-3xl max-h-[85vh] overflow-y-auto rounded-3xl p-8 border border-white/10 shadow-2xl z-10 scrollbar-thin scrollbar-thumb-white/10">
            <button
              onClick={() => setIsOpen(false)}
              className="absolute top-6 right-6 w-8 h-8 rounded-full bg-white/5 border border-white/10 flex items-center justify-center text-gray-400 hover:text-white hover:bg-white/10 transition-all text-lg font-bold"
            >
              ✕
            </button>

            <div className="mb-6 flex flex-wrap items-center gap-3">
              <span className={`text-xs px-2.5 py-1 rounded-full font-semibold ${
                content.status === 'COMPLETED' ? 'bg-green-500/20 text-green-400' : 
                content.status === 'PROCESSING' ? 'bg-yellow-500/20 text-yellow-400' :
                'bg-red-500/20 text-red-400'
              }`}>
                {content.status}
              </span>
              
              {content.fileName && (
                <span className="text-xs bg-white/5 border border-white/5 px-2.5 py-1 rounded-full text-gray-400 font-mono">
                  file: {content.fileName} ({content.fileType})
                </span>
              )}
            </div>

            <h2 className="text-3xl font-extrabold mb-6 gradient-text leading-tight pr-8">
              {content.title}
            </h2>

            <div className="space-y-6">
              <div>
                <h4 className="text-xs uppercase tracking-widest text-blue-500 font-bold mb-2">AI Summary</h4>
                <p className="text-gray-300 text-base leading-relaxed whitespace-pre-line">
                  {content.summary}
                </p>
              </div>

              {content.tags && content.tags.length > 0 && (
                <div>
                  <h4 className="text-xs uppercase tracking-widest text-blue-500 font-bold mb-2">Key Concepts</h4>
                  <div className="flex flex-wrap gap-2">
                    {content.tags.map((tag) => (
                      <span key={tag} className="text-xs font-semibold uppercase tracking-wider bg-blue-500/10 border border-blue-500/20 px-3 py-1 rounded-lg text-blue-400">
                        {tag}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {content.sourceUrl && (
                <div>
                  <h4 className="text-xs uppercase tracking-widest text-blue-500 font-bold mb-2">Origin</h4>
                  <a 
                    href={content.sourceUrl} 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="text-sm text-blue-400 hover:underline flex items-center gap-1 font-semibold"
                  >
                    {content.sourceUrl} ↗
                  </a>
                </div>
              )}

              {content.rawText && (
                <div className="border-t border-white/5 pt-6 mt-6">
                  <details className="group">
                    <summary className="text-xs uppercase tracking-widest text-gray-500 font-bold cursor-pointer select-none group-open:text-gray-300 transition-colors list-none flex items-center gap-2">
                      <span className="group-open:rotate-90 transition-transform duration-200">▶</span>
                      View Extracted Text / Transcript Source
                    </summary>
                    <div className="mt-4 p-4 rounded-xl bg-white/5 border border-white/5 text-xs font-mono text-gray-400 max-h-60 overflow-y-auto leading-relaxed whitespace-pre-wrap select-all">
                      {content.rawText}
                    </div>
                  </details>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
}
