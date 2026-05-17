package com.knowledgeos.content.domain.service;

import java.io.IOException;

public interface ContentExtractor {
    /**
     * Checks if this strategy supports the given file type or source URL.
     */
    boolean supports(String fileType, String sourceUrl);

    /**
     * Extracts text content from raw bytes or source URL.
     */
    String extract(byte[] fileBytes, String sourceUrl) throws IOException;
}
