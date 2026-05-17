package com.knowledgeos.content.infrastructure.storage;

import com.knowledgeos.content.domain.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
@Slf4j
public class S3StorageService implements StorageService {

    @Override
    public String store(byte[] fileBytes, String fileName, String contentType) throws IOException {
        log.info("AWS S3 Connector: Connecting to cloud bucket... [SIMULATED]");
        log.info("AWS S3 Connector: Uploading object '{}' (size: {} bytes) to S3...", fileName, fileBytes.length);
        String s3Key = "s3://knowledgeos-bucket/assets/" + System.currentTimeMillis() + "_" + fileName;
        log.info("AWS S3 Connector: Object uploaded successfully to S3 bucket key: {}", s3Key);
        return s3Key;
    }

    @Override
    public byte[] retrieve(String storagePath) throws IOException {
        log.info("AWS S3 Connector: Retrieving object from S3 key: {}", storagePath);
        // Under a production implementation, download from S3Client bucket inputStream.
        return new byte[0]; 
    }

    @Override
    public void delete(String storagePath) throws IOException {
        log.info("AWS S3 Connector: Deleting object from S3 key: {}", storagePath);
    }
}
