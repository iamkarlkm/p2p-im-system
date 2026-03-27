package com.im.backend.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 语言代码工具类
 */
public class LanguageCodeUtil {

    // ISO 639-1 语言代码映射
    private static final Map<String, String> LANGUAGE_NAMES = new HashMap<>();
    private static final Map<String, String> LANGUAGE_CODES = new HashMap<>();

    static {
        // 初始化语言名称
        LANGUAGE_NAMES.put("zh", "简体中文");
        LANGUAGE_NAMES.put("zh-TW", "繁體中文");
        LANGUAGE_NAMES.put("en", "English");
        LANGUAGE_NAMES.put("ja", "日本語");
        LANGUAGE_NAMES.put("ko", "한국어");
        LANGUAGE_NAMES.put("fr", "Français");
        LANGUAGE_NAMES.put("de", "Deutsch");
        LANGUAGE_NAMES.put("es", "Español");
        LANGUAGE_NAMES.put("ru", "Русский");
        LANGUAGE_NAMES.put("ar", "العربية");
        LANGUAGE_NAMES.put("pt", "Português");
        LANGUAGE_NAMES.put("it", "Italiano");
        LANGUAGE_NAMES.put("th", "ไทย");
        LANGUAGE_NAMES.put("vi", "Tiếng Việt");
        LANGUAGE_NAMES.put("tr", "Türkçe");
        LANGUAGE_NAMES.put("pl", "Polski");
        LANGUAGE_NAMES.put("nl", "Nederlands");
        LANGUAGE_NAMES.put("sv", "Svenska");
        LANGUAGE_NAMES.put("cs", "Čeština");
        LANGUAGE_NAMES.put("el", "Ελληνικά");
        LANGUAGE_NAMES.put("he", "עברית");
        LANGUAGE_NAMES.put("hi", "हिन्दी");
        LANGUAGE_NAMES.put("id", "Bahasa Indonesia");
        LANGUAGE_NAMES.put("ms", "Bahasa Melayu");
        LANGUAGE_NAMES.put("uk", "Українська");

        // 反向映射
        LANGUAGE_NAMES.forEach((code, name) -> LANGUAGE_CODES.put(name.toLowerCase(), code));
    }

    /**
     * 获取语言名称
     */
    public static String getLanguageName(String code) {
        return LANGUAGE_NAMES.getOrDefault(code, code);
    }

    /**
     * 获取语言代码
     */
    public static String getLanguageCode(String name) {
        return LANGUAGE_CODES.getOrDefault(name.toLowerCase(), name);
    }

    /**
     * 检查是否是有效的语言代码
     */
    public static boolean isValidLanguageCode(String code) {
        return LANGUAGE_NAMES.containsKey(code);
    }

    /**
     * 获取所有支持的语言
     */
    public static Map<String, String> getAllLanguages() {
        return new HashMap<>(LANGUAGE_NAMES);
    }

    /**
     * 标准化语言代码
     */
    public static String normalizeLanguageCode(String code) {
        if (code == null || code.isEmpty()) {
            return "auto";
        }
        code = code.toLowerCase();
        // 处理特殊情况
        switch (code) {
            case "zh-cn":
            case "zh_cn":
            case "chs":
                return "zh";
            case "zh-tw":
            case "zh_hk":
            case "cht":
                return "zh-TW";
            case "en-us":
            case "en-gb":
                return "en";
            case "jp":
                return "ja";
            default:
                return code;
        }
    }

    /**
     * 获取常用语言代码列表
     */
    public static String[] getCommonLanguageCodes() {
        return new String[]{"zh", "en", "ja", "ko", "fr", "de", "es", "ru", "ar", "pt"};
    }
}
