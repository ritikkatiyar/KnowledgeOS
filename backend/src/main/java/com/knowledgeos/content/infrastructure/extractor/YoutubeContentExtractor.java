package com.knowledgeos.content.infrastructure.extractor;

import com.knowledgeos.content.domain.service.ContentExtractor;
import io.github.thoroldvix.api.TranscriptContent;
import io.github.thoroldvix.api.TranscriptFormatter;
import io.github.thoroldvix.api.TranscriptFormatters;
import io.github.thoroldvix.api.YoutubeTranscriptApi;
import io.github.thoroldvix.api.TranscriptApiFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class YoutubeContentExtractor implements ContentExtractor {

    @Override
    public boolean supports(String fileType, String sourceUrl) {
        return sourceUrl != null && (sourceUrl.contains("youtube.com") || sourceUrl.contains("youtu.be"));
    }

    @Override
    public String extract(byte[] fileBytes, String sourceUrl) throws IOException {
        String videoId = extractYoutubeVideoId(sourceUrl);
        if (videoId == null) {
            throw new IllegalArgumentException("Invalid YouTube URL: " + sourceUrl);
        }
        try {
            YoutubeTranscriptApi api = TranscriptApiFactory.createDefault();
            TranscriptContent transcriptContent = api.getTranscript(videoId, "en");
            TranscriptFormatter formatter = TranscriptFormatters.textFormatter();
            return formatter.format(transcriptContent);
        } catch (Exception e) {
            log.error("Failed to extract YouTube transcript for video {}: {}", videoId, e.getMessage());
            throw new RuntimeException("Could not retrieve YouTube transcript. Please ensure the video has English subtitles.", e);
        }
    }

    private String extractYoutubeVideoId(String url) {
        String pattern = "(?i)(?:youtube\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";
        Matcher matcher = Pattern.compile(pattern).matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
