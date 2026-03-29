package com.im.backend.service;

import com.im.backend.model.RichMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class RichContentService {

    private final Map<String, List<String>> supportedLanguages = new HashMap<>();
    private final Set<String> enabledFeatures = new HashSet<>();

    public RichContentService() {
        initSupportedLanguages();
        initEnabledFeatures();
    }

    private void initSupportedLanguages() {
        supportedLanguages.put("syntax", Arrays.asList(
            "javascript", "java", "python", "c", "cpp", "csharp", "go", 
            "rust", "ruby", "php", "swift", "kotlin", "typescript", "sql",
            "html", "css", "json", "yaml", "xml", "markdown", "bash", "shell"
        ));
        supportedLanguages.put("emoji", Arrays.asList(
            "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂",
            "🙂", "🙃", "😉", "😊", "😇", "🥰", "😍", "🤩",
            "😘", "😗", "😚", "😙", "🥲", "😋", "😛", "😜",
            "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔", "🤐",
            "👍", "👎", "👋", "🤚", "🖐️", "✋", "🖖", "👌",
            "🤌", "🤏", "✌️", "🤞", "🤟", "🤘", "🤙", "👈",
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍"
        ));
    }

    private void initEnabledFeatures() {
        enabledFeatures.add("bold");
        enabledFeatures.add("italic");
        enabledFeatures.add("code");
        enabledFeatures.add("code_block");
        enabledFeatures.add("link");
        enabledFeatures.add("mention");
        enabledFeatures.add("emoji");
        enabledFeatures.add("quote");
        enabledFeatures.add("list");
    }

    public RichMessageContent parseMessage(String content) {
        log.debug("Parsing rich message content: {}", content.substring(0, Math.min(50, content.length())));
        return RichMessageContent.parse(content);
    }

    public String renderToHtml(String content) {
        RichMessageContent parsed = parseMessage(content);
        return parsed.getHtmlContent();
    }

    public boolean isFeatureEnabled(String feature) {
        return enabledFeatures.contains(feature);
    }

    public void enableFeature(String feature) {
        enabledFeatures.add(feature);
        log.info("Enabled rich content feature: {}", feature);
    }

    public void disableFeature(String feature) {
        enabledFeatures.remove(feature);
        log.info("Disabled rich content feature: {}", feature);
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages.getOrDefault("syntax", Collections.emptyList());
    }

    public boolean isLanguageSupported(String language) {
        return supportedLanguages.getOrDefault("syntax", Collections.emptyList())
                .contains(language.toLowerCase());
    }

    public ValidationResult validateContent(String content) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        result.setWarnings(new ArrayList<>());
        result.setErrors(new ArrayList<>());

        if (content == null || content.isEmpty()) {
            result.setValid(false);
            result.getErrors().add("Content cannot be empty");
            return result;
        }

        if (content.length() > 10000) {
            result.setValid(false);
            result.getErrors().add("Content exceeds maximum length of 10000 characters");
            return result;
        }

        long boldCount = Pattern.compile("\\*\\*[^*]+\\*\\*").matcher(content).results().count();
        long italicCount = Pattern.compile("(?<!\\*)\\*[^*]+\\*(?!\\*)").matcher(content).results().count();
        long codeBlockCount = Pattern.compile("```[\\s\\S]*?```").matcher(content).results().count();

        if (boldCount > 50) {
            result.getWarnings().add("Content has " + boldCount + " bold sections, consider simplifying");
        }
        if (italicCount > 50) {
            result.getWarnings().add("Content has " + italicCount + " italic sections");
        }
        if (codeBlockCount > 10) {
            result.getWarnings().add("Content has " + codeBlockCount + " code blocks");
        }

        if (content.contains("<script") || content.contains("javascript:")) {
            result.setValid(false);
            result.getErrors().add("Potentially dangerous content detected");
        }

        return result;
    }

    public static class ValidationResult {
        private boolean valid;
        private List<String> warnings;
        private List<String> errors;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
}
