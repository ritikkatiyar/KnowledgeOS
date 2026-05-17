package com.knowledgeos.content.domain.service;

import java.io.IOException;

public interface StorageService {
    /**
     * Stores raw bytes and returns a clean, unique storage key/path.
     */
    String store(byte[] fileBytes, String fileName, String contentType) throws IOException;

    /**
     * Retrieves raw bytes using the storage key/path.
     */
    byte[] retrieve(String storagePath) throws IOException;

    /**
     * Deletes the stored file using the storage key/path.
     */
    void delete(String storagePath) throws IOException;
}
