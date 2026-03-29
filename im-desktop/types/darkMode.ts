/**
 * 暗黑模式配置类型定义
 */

/**
 * 主题模式枚举
 */
export enum ThemeMode {
  LIGHT = 'LIGHT',
  DARK = 'DARK',
  SYSTEM = 'SYSTEM',
  CUSTOM = 'CUSTOM'
}

/**
 * 平台枚举
 */
export enum Platform {
  DESKTOP = 'DESKTOP',
  MOBILE = 'MOBILE',
  WEB = 'WEB',
  TABLET = 'TABLET'
}

/**
 * 暗黑模式配置接口
 */
export interface DarkModeConfig {
  id: number;
  userId: string | null;
  themeMode: ThemeMode;
  isActive: boolean;
  configName: string | null;
  primaryColor: string | null;
  backgroundColor: string | null;
  textColor: string | null;
  secondaryTextColor: string | null;
  accentColor: string | null;
  controlColor: string | null;
  borderColor: string | null;
  hoverColor: string | null;
  useSystemColors: boolean | null;
  opacityLevel: number | null;
  fontScaleFactor: number | null;
  highContrast: boolean | null;
  reduceMotion: boolean | null;
  nightProtection: boolean | null;
  createdAt: string;
  updatedAt: string;
  lastSyncedAt: string | null;
  deviceId: string | null;
  platform: Platform | null;
  configVersion: number;
  autoSwitchEnabled: boolean | null;
  autoSwitchStart: string | null;
  autoSwitchEnd: string | null;
  metadata: string | null;
}

/**
 * 颜色配置接口
 */
export interface ColorConfig {
  primaryColor?: string;
  backgroundColor?: string;
  textColor?: string;
  secondaryTextColor?: string;
  accentColor?: string;
  controlColor?: string;
  borderColor?: string;
  hoverColor?: string;
}

/**
 * 主题颜色响应接口
 */
export interface ThemeColorsResponse {
  themeMode: string;
  primaryColor: string;
  backgroundColor: string;
  textColor: string;
  secondaryTextColor: string;
  accentColor: string;
  controlColor: string;
  borderColor: string;
  hoverColor: string;
  opacityLevel: string;
  fontScaleFactor: string;
  highContrast: string;
  reduceMotion: string;
  nightProtection: string;
  useSystemColors: string;
}

/**
 * 创建配置请求接口
 */
export interface CreateConfigRequest {
  userId: string;
  themeMode?: ThemeMode;
  platform?: Platform;
  deviceId?: string;
  configName?: string;
  customColors?: ColorConfig;
}

/**
 * 更新颜色请求接口
 */
export interface UpdateColorsRequest {
  userId: string;
  colors: ColorConfig;
}

/**
 * 切换主题请求接口
 */
export interface SwitchThemeRequest {
  userId: string;
  newThemeMode: ThemeMode;
}

/**
 * 激活配置请求接口
 */
export interface ActivateConfigRequest {
  userId: string;
  configId: number;
}

/**
 * 自动切换配置请求接口
 */
export interface AutoSwitchConfigRequest {
  userId: string;
  enabled: boolean;
  startTime?: string;
  endTime?: string;
}

/**
 * 同步配置请求接口
 */
export interface SyncConfigRequest {
  userId: string;
  configId: number;
  deviceId: string;
}

/**
 * API 响应基础接口
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  errorCode?: string;
  timestamp: string;
}

/**
 * 统计信息接口
 */
export interface DarkModeStatistics {
  themeModeCounts: Record<string, number>;
  platformCounts: Record<string, number>;
  highContrastCount: number;
  reduceMotionCount: number;
  nightProtectionCount: number;
  autoSwitchCount: number;
  totalConfigs: number;
}

/**
 * 默认主题颜色
 */
export const DEFAULT_THEME_COLORS: Record<ThemeMode, ColorConfig> = {
  [ThemeMode.LIGHT]: {
    primaryColor: '#2196F3',
    backgroundColor: '#FFFFFF',
    textColor: '#212121',
    secondaryTextColor: '#757575',
    accentColor: '#FF4081',
    controlColor: '#E0E0E0',
    borderColor: '#BDBDBD',
    hoverColor: '#F5F5F5'
  },
  [ThemeMode.DARK]: {
    primaryColor: '#90CAF9',
    backgroundColor: '#121212',
    textColor: '#E0E0E0',
    secondaryTextColor: '#AAAAAA',
    accentColor: '#FF80AB',
    controlColor: '#424242',
    borderColor: '#616161',
    hoverColor: '#2A2A2A'
  },
  [ThemeMode.SYSTEM]: {
    primaryColor: '#2196F3',
    backgroundColor: '#FFFFFF',
    textColor: '#212121',
    secondaryTextColor: '#757575',
    accentColor: '#FF4081',
    controlColor: '#E0E0E0',
    borderColor: '#BDBDBD',
    hoverColor: '#F5F5F5'
  },
  [ThemeMode.CUSTOM]: {
    primaryColor: '#4CAF50',
    backgroundColor: '#FAFAFA',
    textColor: '#263238',
    secondaryTextColor: '#78909C',
    accentColor: '#FF9800',
    controlColor: '#CFD8DC',
    borderColor: '#B0BEC5',
    hoverColor: '#ECEFF1'
  }
};

/**
 * 检查颜色值是否有效
 */
export function isValidColor(color: string): boolean {
  return /^#([0-9A-F]{3}){1,2}$/i.test(color);
}

/**
 * 解析 HEX 颜色为 RGB
 */
export function hexToRgb(hex: string): { r: number; g: number; b: number } | null {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return result ? {
    r: parseInt(result[1], 16),
    g: parseInt(result[2], 16),
    b: parseInt(result[3], 16)
  } : null;
}

/**
 * 计算带透明度的颜色
 */
export function applyOpacity(hex: string, opacity: number): string {
  const rgb = hexToRgb(hex);
  if (!rgb) return hex;
  return `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${opacity})`;
}

/**
 * 生成 CSS 变量
 */
export function generateCssVariables(colors: ThemeColorsResponse, opacity: number = 1.0): string {
  return `
    :root {
      --theme-primary: ${colors.primaryColor};
      --theme-background: ${colors.backgroundColor};
      --theme-text: ${colors.textColor};
      --theme-text-secondary: ${colors.secondaryTextColor};
      --theme-accent: ${colors.accentColor};
      --theme-control: ${colors.controlColor};
      --theme-border: ${colors.borderColor};
      --theme-hover: ${colors.hoverColor};
      --theme-opacity: ${opacity};
      --theme-font-scale: ${colors.fontScaleFactor || '1.0'};
    }
  `.trim();
}

/**
 * 深灰色度检查
 */
export function isDarkTheme(themeMode: ThemeMode): boolean {
  return themeMode === ThemeMode.DARK || themeMode === ThemeMode.CUSTOM;
}

/**
 * 获取对比度等级
 */
export function getContrastRatio(color1: string, color2: string): number {
  const rgb1 = hexToRgb(color1);
  const rgb2 = hexToRgb(color2);
  
  if (!rgb1 || !rgb2) return 0;
  
  const luminance1 = (0.299 * rgb1.r + 0.587 * rgb1.g + 0.114 * rgb1.b) / 255;
  const luminance2 = (0.299 * rgb2.r + 0.587 * rgb2.g + 0.114 * rgb2.b) / 255;
  
  const lighter = Math.max(luminance1, luminance2);
  const darker = Math.min(luminance1, luminance2);
  
  return (lighter + 0.05) / (darker + 0.05);
}

/**
 * 检查是否满足 WCAG 可访问性标准
 */
export function isWcagCompliant(color1: string, color2: string, level: 'AA' | 'AAA' = 'AA'): boolean {
  const ratio = getContrastRatio(color1, color2);
  
  if (level === 'AAA') {
    return ratio >= 7.0; // 大号文本 4.5:1
  }
  return ratio >= 4.5; // AA 级 4.5:1
}
