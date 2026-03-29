/**
 * 消息翻译类型定义
 */

export interface MessageTranslation {
  id: number;
  messageId: number;
  userId: number;
  sourceLang: string;
  targetLang: string;
  originalContent: string;
  translatedContent: string;
  provider: string;
  model?: string;
  translatedAt: string;
  autoTranslated: boolean;
  durationMs?: number;
}

export interface TranslationRequest {
  messageId: number;
  sourceLang?: string;
  targetLang: string;
  text: string;
  userId: number;
  autoTranslate?: boolean;
}

export interface TranslationSettings {
  id?: number;
  userId: number;
  autoTranslate: boolean;
  preferredTargetLang: string;
  autoLangWhitelist?: string;
  provider: string;
  apiKey?: string;
  showOriginal: boolean;
}

export interface SupportedLanguage {
  code: string;
  name: string;
  nativeName: string;
}

export const SUPPORTED_LANGUAGES: SupportedLanguage[] = [
  { code: 'zh-CN', name: 'Chinese', nativeName: '中文' },
  { code: 'en', name: 'English', nativeName: 'English' },
  { code: 'ja', name: 'Japanese', nativeName: '日本語' },
  { code: 'ko', name: 'Korean', nativeName: '한국어' },
  { code: 'es', name: 'Spanish', nativeName: 'Español' },
  { code: 'fr', name: 'French', nativeName: 'Français' },
  { code: 'de', name: 'German', nativeName: 'Deutsch' },
  { code: 'ru', name: 'Russian', nativeName: 'Русский' },
  { code: 'ar', name: 'Arabic', nativeName: 'العربية' },
  { code: 'pt', name: 'Portuguese', nativeName: 'Português' },
  { code: 'it', name: 'Italian', nativeName: 'Italiano' },
  { code: 'vi', name: 'Vietnamese', nativeName: 'Tiếng Việt' },
  { code: 'th', name: 'Thai', nativeName: 'ไทย' },
  { code: 'id', name: 'Indonesian', nativeName: 'Bahasa Indonesia' },
];
