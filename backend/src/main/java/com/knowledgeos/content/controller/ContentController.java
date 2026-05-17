package com.knowledgeos.content.controller;

import com.knowledgeos.content.entity.Content;
import com.knowledgeos.content.application.ContentApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // For development
public class ContentController {

    private final ContentApplicationService contentApplicationService;

    @PostMapping
    public ResponseEntity<Content> saveContent(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        String text = request.get("text");
        return ResponseEntity.ok(contentApplicationService.saveContent(url, text));
    }

    @PostMapping("/upload")
    public ResponseEntity<Content> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(contentApplicationService.saveFileContent(file));
    }

    @GetMapping
    public ResponseEntity<List<Content>> getAllContent() {
        return ResponseEntity.ok(contentApplicationService.getAllContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentApplicationService.getContentById(id));
    }
}
