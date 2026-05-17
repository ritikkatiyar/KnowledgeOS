package com.knowledgeos.content.infrastructure.extractor;

import com.knowledgeos.content.domain.service.ContentExtractor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WebPageContentExtractor implements ContentExtractor {

    @Override
    public boolean supports(String fileType, String sourceUrl) {
        // Only supports URLs that are not YouTube videos and where raw files are not uploaded
        return fileType == null && sourceUrl != null && !sourceUrl.contains("youtube.com") && !sourceUrl.contains("youtu.be");
    }

    @Override
    public String extract(byte[] fileBytes, String sourceUrl) throws IOException {
        if (sourceUrl == null || sourceUrl.isEmpty()) {
            throw new IllegalArgumentException("Source URL is null or empty.");
        }
        return Jsoup.connect(sourceUrl).get().text();
    }
}
