package com.knowledgeos.content.application;

import com.knowledgeos.content.domain.service.AiAnalysisService;
import com.knowledgeos.content.domain.service.AnalysisResult;
import com.knowledgeos.content.domain.service.ContentExtractor;
import com.knowledgeos.content.domain.service.StorageService;
import com.knowledgeos.content.entity.Content;
import com.knowledgeos.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentApplicationService {

    private final ContentRepository contentRepository;
    private final List<ContentExtractor> extractors;
    private final AiAnalysisService aiAnalysisService;
    private final StorageService storageService;
    private final TaskExecutor taskExecutor;

    public Content saveContent(String sourceUrl, String rawText) {
        Content content = Content.builder()
                .sourceUrl(sourceUrl)
                .rawText(rawText)
                .title("Processing...")
                .status(Content.Status.PENDING)
                .build();
        
        Content saved = contentRepository.save(content);
        taskExecutor.execute(() -> processContentInternal(saved.getId()));
        return saved;
    }

    public Content saveFileContent(MultipartFile file) throws IOException {
        // Stream the file bytes to the pluggable storage adapter
        String path = storageService.store(file.getBytes(), file.getOriginalFilename(), file.getContentType());

        Content content = Content.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .storagePath(path)
                .title("Processing File...")
                .status(Content.Status.PENDING)
                .build();

        Content saved = contentRepository.save(content);
        taskExecutor.execute(() -> processContentInternal(saved.getId()));
        return saved;
    }

    public List<Content> getAllContent() {
        return contentRepository.findAll();
    }

    public Content getContentById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));
    }

    public void processContentInternal(Long contentId) {
        Content content = contentRepository.findById(contentId).orElse(null);
        if (content == null) return;

        try {
            content.setStatus(Content.Status.PROCESSING);
            contentRepository.save(content);

            AnalysisResult analysis;

            if (content.getStoragePath() != null && !content.getStoragePath().trim().isEmpty()) {
                // Fetch the raw bytes on-the-fly from the pluggable storage port
                byte[] bytes = storageService.retrieve(content.getStoragePath());

                // Check if it is standard static media or can be extracted
                if (content.getFileType() != null && 
                    (content.getFileType().startsWith("image/") || "video/mp4".equalsIgnoreCase(content.getFileType()))) {
                    analysis = aiAnalysisService.analyzeMultimodal(content);
                } else {
                    // Extract text using strategy registry
                    ContentExtractor extractor = findExtractor(content.getFileType(), content.getSourceUrl());
                    String extractedText = extractor.extract(bytes, content.getSourceUrl());
                    content.setRawText(extractedText);
                    analysis = aiAnalysisService.analyzeText(content.getRawText());
                }
            } else {
                // Raw text or URL ingestion using strategy registry
                if (content.getSourceUrl() != null && (content.getRawText() == null || content.getRawText().isEmpty())) {
                    ContentExtractor extractor = findExtractor(content.getFileType(), content.getSourceUrl());
                    String extractedText = extractor.extract(null, content.getSourceUrl());
                    content.setRawText(extractedText);
                }
                analysis = aiAnalysisService.analyzeText(content.getRawText());
            }

            // Apply analyzed invariants back to aggregate root
            content.setTitle(analysis.getTitle());
            content.setSummary(analysis.getSummary());
            content.setTags(analysis.getTags());
            content.setStatus(Content.Status.COMPLETED);
            contentRepository.save(content);

        } catch (Exception e) {
            log.error("Error processing content in application service " + contentId, e);
            content.setStatus(Content.Status.ERROR);
            contentRepository.save(content);
        }
    }

    private ContentExtractor findExtractor(String fileType, String sourceUrl) {
        return extractors.stream()
                .filter(e -> e.supports(fileType, sourceUrl))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unsupported ingestion format. FileType: %s, SourceUrl: %s", fileType, sourceUrl)));
    }
}
