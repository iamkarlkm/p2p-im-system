package com.im.backend.model;

/**
 * 语言对模型
 */
public class LanguagePair {
    private String sourceLanguage;
    private String targetLanguage;
    private boolean supported;
    private double accuracy;

    public LanguagePair() {}

    public LanguagePair(String sourceLanguage, String targetLanguage) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

    // Getters and Setters
    public String getSourceLanguage() { return sourceLanguage; }
    public void setSourceLanguage(String sourceLanguage) { this.sourceLanguage = sourceLanguage; }

    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }

    public boolean isSupported() { return supported; }
    public void setSupported(boolean supported) { this.supported = supported; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    @Override
    public String toString() {
        return sourceLanguage + " -> " + targetLanguage;
    }
}
