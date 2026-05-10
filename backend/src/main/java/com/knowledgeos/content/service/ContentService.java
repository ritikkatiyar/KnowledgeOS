package com.knowledgeos.content.service;

import com.knowledgeos.content.entity.Content;
import com.knowledgeos.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final ChatClient chatClient;

    public Content saveContent(String sourceUrl, String rawText) {
        Content content = Content.builder()
                .sourceUrl(sourceUrl)
                .rawText(rawText)
                .title("Processing...")
                .status(Content.Status.PENDING)
                .build();
        
        Content saved = contentRepository.save(content);
        processContentAsync(saved.getId());
        return saved;
    }

    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }

    public Content getContentById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));
    }

    @Async
    public void processContentAsync(Long contentId) {
        Content content = contentRepository.findById(contentId).orElse(null);
        if (content == null) return;

        try {
            content.setStatus(Content.Status.PROCESSING);
            contentRepository.save(content);

            // 1. Extract Text if URL is provided and rawText is empty
            if (content.getSourceUrl() != null && (content.getRawText() == null || content.getRawText().isEmpty())) {
                String extractedText = extractTextFromUrl(content.getSourceUrl());
                content.setRawText(extractedText);
            }

            // 2. AI Processing
            String prompt = String.format("""
                Analyze the following technical content and provide a structured response in JSON format.
                JSON structure:
                {
                  "title": "A concise and descriptive title",
                  "summary": "A high-quality summary (3-5 sentences)",
                  "tags": ["tag1", "tag2", "tag3"]
                }
                
                Content:
                %s
                """, content.getRawText());

            String responseJson = chatClient.prompt(new Prompt(prompt)).call().content();
            
            // Note: In a real app, use a JSON parser. For MVP, we'll do simple extraction or assume correct format.
            // For now, let's just parse it manually or use a simple regex if we don't want to add Jackson complexity here.
            // Actually, Spring AI can map to records, but let's keep it simple for now.
            
            parseAiResponse(content, responseJson);

            content.setStatus(Content.Status.COMPLETED);
            contentRepository.save(content);

        } catch (Exception e) {
            log.error("Error processing content {}: {}", contentId, e.getMessage());
            content.setStatus(Content.Status.ERROR);
            contentRepository.save(content);
        }
    }

    private String extractTextFromUrl(String url) throws IOException {
        return Jsoup.connect(url).get().text();
    }

    private void parseAiResponse(Content content, String json) {
        try {
            // Very basic manual parsing for MVP if we don't want to deal with JSON objects yet
            // In a real scenario, use ObjectMapper.
            // I'll use a regex to extract fields for simplicity in this script.
            
            String title = extractField(json, "title");
            String summary = extractField(json, "summary");
            String tagsStr = extractField(json, "tags");
            
            content.setTitle(title != null ? title : "Untitled");
            content.setSummary(summary != null ? summary : "No summary generated.");
            if (tagsStr != null) {
                List<String> tags = Arrays.asList(tagsStr.replaceAll("[\\[\\]\" ]", "").split(","));
                content.setTags(tags);
            }
        } catch (Exception e) {
            log.warn("Failed to parse AI response perfectly: {}", e.getMessage());
            content.setTitle("Processed Content " + content.getId());
            content.setSummary(json); // Fallback to raw response
        }
    }

    private String extractField(String json, String field) {
        String pattern = "\"" + field + "\":\\s*\"(.*?)\"";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        // For arrays like tags
        if (field.equals("tags")) {
            String tagPattern = "\"" + field + "\":\\s*\\[(.*?)\\]";
            matcher = java.util.regex.Pattern.compile(tagPattern).matcher(json);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
