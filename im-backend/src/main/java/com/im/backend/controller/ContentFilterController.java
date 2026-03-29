package com.im.backend.controller;

import com.im.backend.service.ContentFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/content-filter")
@RequiredArgsConstructor
public class ContentFilterController {
    
    private final ContentFilterService contentFilterService;
    
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkContent(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        
        ContentFilterService.FilterResult result = contentFilterService.filterContent(content);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("blocked", result.isBlocked());
        response.put("safe", result.isSafe());
        response.put("spamScore", result.getSpamScore());
        response.put("sensitiveWordsFound", result.getSensitiveWordsFound());
        response.put("categories", result.getCategories());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/sensitive-words/add")
    public ResponseEntity<Map<String, Object>> addSensitiveWords(@RequestBody Map<String, List<String>> request) {
        List<String> words = request.get("words");
        
        if (words == null || words.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Words list is required"));
        }
        
        contentFilterService.addSensitiveWords(words);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("added", words.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sensitive-words")
    public ResponseEntity<Map<String, Object>> getSensitiveWords() {
        Set<String> words = contentFilterService.getSensitiveWords();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("count", words.size());
        response.put("words", words);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/sensitive-words/remove")
    public ResponseEntity<Map<String, Object>> removeSensitiveWord(@RequestBody Map<String, String> request) {
        String word = request.get("word");
        
        if (word == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Word is required"));
        }
        
        boolean removed = contentFilterService.removeSensitiveWord(word);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("removed", removed);
        response.put("word", word);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getFilterStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSensitiveWords", contentFilterService.getSensitiveWords().size());
        stats.put("enabled", true);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("stats", stats);
        
        return ResponseEntity.ok(response);
    }
}
