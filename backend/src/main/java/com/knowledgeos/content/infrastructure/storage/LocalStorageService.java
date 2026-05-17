package com.knowledgeos.content.infrastructure.storage;

import com.knowledgeos.content.domain.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
@Slf4j
public class LocalStorageService implements StorageService {

    private final Path storageDirectory;

    public LocalStorageService() {
        // Resolve a local folder named "storage" inside the project workspace directory
        this.storageDirectory = Paths.get("storage").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageDirectory);
            log.info("Initialized local file system storage directory at: {}", this.storageDirectory);
        } catch (IOException e) {
            log.error("Could not initialize local storage folder directory!", e);
            throw new RuntimeException("Could not create local storage directory.", e);
        }
    }

    @Override
    public String store(byte[] fileBytes, String fileName, String contentType) throws IOException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("File content bytes cannot be null or empty.");
        }
        
        // Generate a unique filename prefix to avoid collisions
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String safeName = fileName != null ? fileName.replaceAll("[^a-zA-Z0-9.-]", "_") : "file";
        String uniqueFileName = uniqueId + "_" + safeName;
        
        Path targetPath = this.storageDirectory.resolve(uniqueFileName);
        Files.write(targetPath, fileBytes);
        
        log.info("Stored file successfully in local file system at: {}", targetPath);
        
        // Return the clean relative key/path (e.g. "storage/unique_file.pdf")
        return "storage/" + uniqueFileName;
    }

    @Override
    public byte[] retrieve(String storagePath) throws IOException {
        if (storagePath == null || storagePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Storage path reference is null or empty.");
        }
        
        // Extract the filename from the storage path (e.g., "storage/1234_file.pdf" -> "1234_file.pdf")
        String fileName = storagePath.replace("storage/", "");
        Path targetPath = this.storageDirectory.resolve(fileName).normalize();
        
        if (!targetPath.startsWith(this.storageDirectory)) {
            throw new SecurityException("Directory traversal attack detected! Invalid path target: " + storagePath);
        }
        
        if (!Files.exists(targetPath)) {
            throw new IOException("File not found at storage path: " + targetPath);
        }
        
        return Files.readAllBytes(targetPath);
    }

    @Override
    public void delete(String storagePath) throws IOException {
        if (storagePath == null || storagePath.trim().isEmpty()) {
            return;
        }
        
        String fileName = storagePath.replace("storage/", "");
        Path targetPath = this.storageDirectory.resolve(fileName).normalize();
        
        if (!targetPath.startsWith(this.storageDirectory)) {
            throw new SecurityException("Directory traversal attack detected!");
        }
        
        if (Files.exists(targetPath)) {
            Files.delete(targetPath);
            log.info("Deleted file successfully from local filesystem: {}", targetPath);
        }
    }
}
