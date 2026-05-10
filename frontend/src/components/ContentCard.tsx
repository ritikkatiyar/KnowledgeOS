"use client";

interface Content {
  id: number;
  title: string;
  summary: string;
  tags: string[];
  status: string;
  sourceUrl: string;
}

export default function ContentCard({ content }: { content: Content }) {
  return (
    <div className="glass p-6 rounded-2xl hover:border-blue-500/50 transition-all duration-300 group">
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-xl font-bold group-hover:text-blue-400 transition-colors">
          {content.title}
        </h3>
        <span className={`text-xs px-2 py-1 rounded-full ${
          content.status === 'COMPLETED' ? 'bg-green-500/20 text-green-400' : 
          content.status === 'PROCESSING' ? 'bg-yellow-500/20 text-yellow-400' :
          'bg-red-500/20 text-red-400'
        }`}>
          {content.status}
        </span>
      </div>
      <p className="text-gray-400 text-sm mb-4 line-clamp-3">
        {content.summary}
      </p>
      <div className="flex flex-wrap gap-2 mb-4">
        {content.tags?.map((tag) => (
          <span key={tag} className="text-[10px] uppercase tracking-wider bg-white/5 px-2 py-1 rounded">
            {tag}
          </span>
        ))}
      </div>
      {content.sourceUrl && (
        <a 
          href={content.sourceUrl} 
          target="_blank" 
          rel="noopener noreferrer"
          className="text-xs text-blue-500 hover:underline flex items-center gap-1"
        >
          View Source ↗
        </a>
      )}
    </div>
  );
}
