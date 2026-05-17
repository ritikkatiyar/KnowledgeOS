package com.knowledgeos.content.infrastructure.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgeos.content.domain.service.AiAnalysisService;
import com.knowledgeos.content.domain.service.AnalysisResult;
import com.knowledgeos.content.domain.service.StorageService;
import com.knowledgeos.content.entity.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiAnalysisService implements AiAnalysisService {

    private final ChatClient chatClient;
    private final StorageService storageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AnalysisResult analyzeText(String text) {
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
            """, text);

        String rawResponse = chatClient.prompt(new Prompt(prompt)).call().content();
        return parseResponse(rawResponse);
    }

    @Override
    public AnalysisResult analyzeMultimodal(Content content) {
        String prompt = """
            Analyze the following media, which is technical content (screenshot, diagram, code snippet, demonstration recording, architecture design, etc.).
            Provide a structured response in JSON format.
            JSON structure:
            {
              "title": "A concise and descriptive title of the concept, diagram, or code",
              "summary": "A high-quality summary explaining what the media depicts, its architecture, or what the code does (3-5 sentences)",
              "tags": ["tag1", "tag2", "tag3"]
            }
            """;

        byte[] fileBytes;
        try {
            fileBytes = storageService.retrieve(content.getStoragePath());
        } catch (Exception e) {
            log.error("Failed to retrieve file bytes from storage path: " + content.getStoragePath(), e);
            throw new RuntimeException("Failed to retrieve file bytes for multimodal analysis", e);
        }

        final byte[] finalBytes = fileBytes;

        String rawResponse = chatClient.prompt()
                .user(u -> u.text(prompt)
                            .media(MimeTypeUtils.parseMimeType(content.getFileType()), new ByteArrayResource(finalBytes)))
                .call()
                .content();

        return parseResponse(rawResponse);
    }

    private AnalysisResult parseResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return fallbackResult("No response received from AI", rawResponse);
        }

        String cleaned = cleanJsonString(rawResponse);
        try {
            Map<String, Object> map = objectMapper.readValue(cleaned, new TypeReference<Map<String, Object>>() {});
            
            String title = (String) map.get("title");
            String summary = (String) map.get("summary");
            
            List<String> tags = new ArrayList<>();
            Object tagsObj = map.get("tags");
            if (tagsObj instanceof List) {
                for (Object tag : (List<?>) tagsObj) {
                    if (tag != null) {
                        tags.add(tag.toString().trim());
                    }
                }
            }

            return AnalysisResult.builder()
                    .title(title != null ? title.trim() : "Untitled")
                    .summary(summary != null ? summary.trim() : "No summary generated.")
                    .tags(tags)
                    .build();

        } catch (Exception e) {
            log.warn("Failed to parse AI JSON response with Jackson. Fallback to Regex parser. Error: {}", e.getMessage());
            return parseResponseWithRegex(rawResponse);
        }
    }

    private String cleanJsonString(String raw) {
        String cleaned = raw.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
            if (cleaned.startsWith("json")) {
                cleaned = cleaned.substring(4);
            }
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    private AnalysisResult parseResponseWithRegex(String json) {
        try {
            String title = extractField(json, "title");
            String summary = extractField(json, "summary");
            String tagsStr = extractField(json, "tags");

            List<String> tags = new ArrayList<>();
            if (tagsStr != null) {
                String[] split = tagsStr.replaceAll("[\\[\\]\" ]", "").split(",");
                for (String t : split) {
                    if (!t.trim().isEmpty()) {
                        tags.add(t.trim());
                    }
                }
            }

            return AnalysisResult.builder()
                    .title(title != null ? title : "Untitled")
                    .summary(summary != null ? summary : "No summary generated.")
                    .tags(tags)
                    .build();
        } catch (Exception e) {
            return fallbackResult("AI Processing Output", json);
        }
    }

    private String extractField(String json, String field) {
        String pattern = "\"" + field + "\":\\s*\"(.*?)\"";
        Matcher matcher = Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private AnalysisResult fallbackResult(String defaultTitle, String rawText) {
        return AnalysisResult.builder()
                .title(defaultTitle)
                .summary(rawText != null ? rawText : "No content returned.")
                .tags(new ArrayList<>())
                .build();
    }
}
