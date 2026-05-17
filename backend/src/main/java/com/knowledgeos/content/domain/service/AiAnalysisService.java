package com.knowledgeos.content.domain.service;

import com.knowledgeos.content.entity.Content;

public interface AiAnalysisService {
    /**
     * Analyzes plain text content (e.g. from websites or YouTube subtitles)
     * and returns structured domain AnalysisResult containing title, summary, and tags.
     */
    AnalysisResult analyzeText(String text);

    /**
     * Analyzes multimodal static media (e.g. images or raw videos)
     * and returns structured domain AnalysisResult containing title, summary, and tags.
     */
    AnalysisResult analyzeMultimodal(Content content);
}
