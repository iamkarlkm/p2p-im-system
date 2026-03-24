package com.im.backend.controller;

import com.im.backend.model.RichMessageContent;
import com.im.backend.service.RichContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/rich-content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RichContentController {

    private final RichContentService richContentService;

    @PostMapping("/parse")
    public ResponseEntity<RichMessageContent> parseContent(@RequestBody ParseRequest request) {
        log.info("Parsing rich content, length: {}", request.getContent().length());
        try {
            RichMessageContent result = richContentService.parseMessage(request.getContent());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error parsing rich content", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/render")
    public ResponseEntity<RenderResponse> renderContent(@RequestBody RenderRequest request) {
        log.debug("Rendering content to HTML");
        try {
            String html = richContentService.renderToHtml(request.getContent());
            return ResponseEntity.ok(new RenderResponse(html, true));
        } catch (Exception e) {
            log.error("Error rendering content", e);
            return ResponseEntity.ok(new RenderResponse("Error rendering content", false));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<RichContentService.ValidationResult> validateContent(@RequestBody ValidateRequest request) {
        RichContentService.ValidationResult result = richContentService.validateContent(request.getContent());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/features")
    public ResponseEntity<FeaturesResponse> getFeatures() {
        List<Map<String, Object>> features = new ArrayList<>();
        
        String[] featureNames = {"bold", "italic", "code", "code_block", "link", "mention", "emoji", "quote", "list"};
        String[] featureDescs = {
            "Bold text with **text** or __text__",
            "Italic text with *text* or _text_",
            "Inline code with `code`",
            "Code blocks with ```language```",
            "Links with [text](url)",
            "Mentions with @username",
            "Emoji support :emoji:",
            "Block quotes with > quote",
            "Lists with - item or 1. item"
        };
        
        for (int i = 0; i < featureNames.length; i++) {
            Map<String, Object> feature = new HashMap<>();
            feature.put("name", featureNames[i]);
            feature.put("description", featureDescs[i]);
            feature.put("enabled", richContentService.isFeatureEnabled(featureNames[i]));
            features.add(feature);
        }
        
        return ResponseEntity.ok(new FeaturesResponse(features, richContentService.getSupportedLanguages()));
    }

    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> previewContent(@RequestBody PreviewRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            RichMessageContent parsed = richContentService.parseMessage(request.getContent());
            RichContentService.ValidationResult validation = richContentService.validateContent(request.getContent());
            
            response.put("raw", request.getContent());
            response.put("html", parsed.getHtmlContent());
            response.put("valid", validation.isValid());
            response.put("warnings", validation.getWarnings());
            response.put("errors", validation.getErrors());
            response.put("markdownGuide", RichMessageContent.supportedMarkdown());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating preview", e);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public static class ParseRequest {
        private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class RenderRequest {
        private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class RenderResponse {
        private String html;
        private boolean success;
        
        public RenderResponse(String html, boolean success) {
            this.html = html;
            this.success = success;
        }
        
        public String getHtml() { return html; }
        public boolean isSuccess() { return success; }
    }

    public static class ValidateRequest {
        private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class FeaturesResponse {
        private List<Map<String, Object>> features;
        private List<String> supportedLanguages;
        
        public FeaturesResponse(List<Map<String, Object>> features, List<String> supportedLanguages) {
            this.features = features;
            this.supportedLanguages = supportedLanguages;
        }
        
        public List<Map<String, Object>> getFeatures() { return features; }
        public List<String> getSupportedLanguages() { return supportedLanguages; }
    }

    public static class PreviewRequest {
        private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
