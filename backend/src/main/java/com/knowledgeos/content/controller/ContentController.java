package com.knowledgeos.content.controller;

import com.knowledgeos.content.entity.Content;
import com.knowledgeos.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // For development
public class ContentController {

    private final ContentService contentService;

    @PostMapping
    public ResponseEntity<Content> saveContent(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        String text = request.get("text");
        return ResponseEntity.ok(contentService.saveContent(url, text));
    }

    @GetMapping
    public ResponseEntity<List<Content>> getAllContent() {
        return ResponseEntity.ok(contentService.getAllContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }
}
