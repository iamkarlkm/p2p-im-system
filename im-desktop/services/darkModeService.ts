/**
 * 暗黑模式配置服务
 * 提供暗黑模式配置的 API 调用和管理功能
 */

import {
  DarkModeConfig,
  ThemeMode,
  Platform,
  ColorConfig,
  ThemeColorsResponse,
  ApiResponse,
  DarkModeStatistics,
  DEFAULT_THEME_COLORS
} from '../types/darkMode';

const API_BASE_URL = '/api/v1/dark-mode';

/**
 * 暗黑模式服务类
 */
class DarkModeService {
  private static instance: DarkModeService;
  private currentConfig: DarkModeConfig | null = null;
  private userId: string | null = null;
  private deviceId: string | null = null;
  private platform: Platform = Platform.DESKTOP;

  private constructor() {
    // 从本地存储加载设备 ID
    this.deviceId = localStorage.getItem('darkmode_device_id');
    if (!this.deviceId) {
      this.deviceId = this.generateDeviceId();
      localStorage.setItem('darkmode_device_id', this.deviceId);
    }
    
    // 从本地存储加载用户 ID
    this.userId = localStorage.getItem('current_user_id');
    
    // 初始化平台
    this.platform = Platform.DESKTOP;
  }

  /**
   * 获取单例实例
   */
  public static getInstance(): DarkModeService {
    if (!DarkModeService.instance) {
      DarkModeService.instance = new DarkModeService();
    }
    return DarkModeService.instance;
  }

  /**
   * 设置用户 ID
   */
  public setUserId(userId: string | null): void {
    this.userId = userId;
    if (userId) {
      localStorage.setItem('current_user_id', userId);
    } else {
      localStorage.removeItem('current_user_id');
    }
  }

  /**
   * 获取设备 ID
   */
  public getDeviceId(): string {
    return this.deviceId || '';
  }

  /**
   * 生成设备 ID
   */
  private generateDeviceId(): string {
    return 'desktop_' + Math.random().toString(36).substring(2, 15);
  }

  /**
   * 通用 API 请求方法
   */
  private async request<T>(
    endpoint: string,
    method: 'GET' | 'POST' | 'PUT' | 'DELETE' = 'GET',
    params?: Record<string, string | number | boolean>,
    body?: any
  ): Promise<ApiResponse<T>> {
    const url = new URL(`${API_BASE_URL}${endpoint}`, window.location.origin);
    
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        url.searchParams.append(key, value.toString());
      });
    }

    const options: RequestInit = {
      method,
      headers: {
        'Content-Type': 'application/json',
      },
    };

    if (body && method !== 'GET') {
      options.body = JSON.stringify(body);
    }

    try {
      const response = await fetch(url.toString(), options);
      const data = await response.json();
      
      if (!response.ok) {
        throw new Error(data.message || `HTTP ${response.status}`);
      }
      
      return data as ApiResponse<T>;
    } catch (error) {
      console.error('API 请求失败:', error);
      throw error;
    }
  }

  /**
   * 获取活跃配置
   */
  public async getActiveConfig(): Promise<DarkModeConfig | null> {
    if (!this.userId) {
      console.warn('未设置用户 ID');
      return null;
    }

    try {
      const response = await this.request<DarkModeConfig>('/active', 'GET', {
        userId: this.userId
      });
      
      if (response.success && response.data) {
        this.currentConfig = response.data;
        return response.data;
      }
      return null;
    } catch (error) {
      console.error('获取活跃配置失败:', error);
      return null;
    }
  }

  /**
   * 获取当前配置（优先本地缓存）
   */
  public getCurrentConfig(): DarkModeConfig | null {
    return this.currentConfig;
  }

  /**
   * 获取所有配置
   */
  public async getUserConfigs(): Promise<DarkModeConfig[]> {
    if (!this.userId) {
      console.warn('未设置用户 ID');
      return [];
    }

    try {
      const response = await this.request<DarkModeConfig[]>('/list', 'GET', {
        userId: this.userId
      });
      
      return response.success && response.data ? response.data : [];
    } catch (error) {
      console.error('获取配置列表失败:', error);
      return [];
    }
  }

  /**
   * 创建默认配置
   */
  public async createDefaultConfig(): Promise<DarkModeConfig | null> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request<DarkModeConfig>('/create-default', 'POST', {
        userId: this.userId,
        platform: this.platform,
        deviceId: this.deviceId || undefined
      });
      
      if (response.success && response.data) {
        this.currentConfig = response.data;
        return response.data;
      }
      return null;
    } catch (error) {
      console.error('创建默认配置失败:', error);
      throw error;
    }
  }

  /**
   * 创建自定义配置
   */
  public async createCustomConfig(
    themeMode: ThemeMode,
    customColors?: ColorConfig
  ): Promise<DarkModeConfig | null> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request<DarkModeConfig>('/create-custom', 'POST', {
        userId: this.userId,
        themeMode,
        platform: this.platform,
        deviceId: this.deviceId || undefined
      }, customColors);
      
      if (response.success && response.data) {
        return response.data;
      }
      return null;
    } catch (error) {
      console.error('创建自定义配置失败:', error);
      throw error;
    }
  }

  /**
   * 切换主题模式
   */
  public async switchThemeMode(newThemeMode: ThemeMode): Promise<DarkModeConfig | null> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request<DarkModeConfig>('/switch-theme', 'PUT', {
        userId: this.userId,
        newThemeMode
      });
      
      if (response.success && response.data) {
        this.currentConfig = response.data;
        await this.applyThemeToDocument(response.data);
        return response.data;
      }
      return null;
    } catch (error) {
      console.error('切换主题模式失败:', error);
      throw error;
    }
  }

  /**
   * 激活指定配置
   */
  public async activateConfig(configId: number): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/activate', 'PUT', {
        userId: this.userId,
        configId
      });
      
      if (response.success) {
        await this.getActiveConfig();
        return true;
      }
      return false;
    } catch (error) {
      console.error('激活配置失败:', error);
      return false;
    }
  }

  /**
   * 更新自定义颜色
   */
  public async updateCustomColors(colors: ColorConfig): Promise<DarkModeConfig | null> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request<DarkModeConfig>('/update-colors', 'PUT', {
        userId: this.userId
      }, colors);
      
      if (response.success && response.data) {
        this.currentConfig = response.data;
        await this.applyThemeToDocument(response.data);
        return response.data;
      }
      return null;
    } catch (error) {
      console.error('更新颜色失败:', error);
      throw error;
    }
  }

  /**
   * 切换高对比度模式
   */
  public async toggleHighContrast(enabled: boolean): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/toggle-high-contrast', 'PUT', {
        userId: this.userId,
        enabled
      });
      
      if (response.success) {
        document.body.classList.toggle('high-contrast', enabled);
        return true;
      }
      return false;
    } catch (error) {
      console.error('切换高对比度失败:', error);
      return false;
    }
  }

  /**
   * 切换减少动画
   */
  public async toggleReduceMotion(enabled: boolean): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/toggle-reduce-motion', 'PUT', {
        userId: this.userId,
        enabled
      });
      
      if (response.success) {
        document.body.classList.toggle('reduce-motion', enabled);
        return true;
      }
      return false;
    } catch (error) {
      console.error('切换减少动画失败:', error);
      return false;
    }
  }

  /**
   * 切换夜间保护
   */
  public async toggleNightProtection(enabled: boolean): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/toggle-night-protection', 'PUT', {
        userId: this.userId,
        enabled
      });
      
      if (response.success) {
        document.body.classList.toggle('night-protection', enabled);
        return true;
      }
      return false;
    } catch (error) {
      console.error('切换夜间保护失败:', error);
      return false;
    }
  }

  /**
   * 配置自动切换
   */
  public async configureAutoSwitch(
    enabled: boolean,
    startTime?: string,
    endTime?: string
  ): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/configure-auto-switch', 'PUT', {
        userId: this.userId,
        enabled,
        startTime: startTime || '',
        endTime: endTime || ''
      });
      
      return response.success || false;
    } catch (error) {
      console.error('配置自动切换失败:', error);
      return false;
    }
  }

  /**
   * 检查是否应该自动切换到暗黑模式
   */
  public async checkAutoSwitch(): Promise<boolean> {
    if (!this.userId) {
      return false;
    }

    try {
      const response = await this.request<{ shouldSwitchToDark: boolean }>('/check-auto-switch', 'GET', {
        userId: this.userId
      });
      
      return response.success && response.data?.shouldSwitchToDark || false;
    } catch (error) {
      console.error('检查自动切换失败:', error);
      return false;
    }
  }

  /**
   * 更新透明度
   */
  public async updateOpacity(opacityLevel: number): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/update-opacity', 'PUT', {
        userId: this.userId,
        opacityLevel
      });
      
      if (response.success) {
        document.documentElement.style.setProperty('--theme-opacity', opacityLevel.toString());
        return true;
      }
      return false;
    } catch (error) {
      console.error('更新透明度失败:', error);
      return false;
    }
  }

  /**
   * 更新字体缩放
   */
  public async updateFontScale(fontScaleFactor: number): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/update-font-scale', 'PUT', {
        userId: this.userId,
        fontScaleFactor
      });
      
      if (response.success) {
        document.documentElement.style.setProperty('--theme-font-scale', fontScaleFactor.toString());
        return true;
      }
      return false;
    } catch (error) {
      console.error('更新字体缩放失败:', error);
      return false;
    }
  }

  /**
   * 同步配置到设备
   */
  public async syncToDevice(configId: number, deviceId: string): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/sync-to-device', 'POST', {
        userId: this.userId,
        configId,
        deviceId
      });
      
      return response.success || false;
    } catch (error) {
      console.error('同步配置失败:', error);
      return false;
    }
  }

  /**
   * 获取当前主题颜色
   */
  public async getCurrentThemeColors(): Promise<ThemeColorsResponse | null> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request<ThemeColorsResponse>('/current-colors', 'GET', {
        userId: this.userId
      });
      
      return response.success && response.data ? response.data : null;
    } catch (error) {
      console.error('获取主题颜色失败:', error);
      return null;
    }
  }

  /**
   * 删除配置
   */
  public async deleteConfig(configId: number): Promise<boolean> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request('/delete', 'DELETE', {
        userId: this.userId,
        configId
      });
      
      return response.success || false;
    } catch (error) {
      console.error('删除配置失败:', error);
      return false;
    }
  }

  /**
   * 获取统计信息
   */
  public async getStatistics(): Promise<DarkModeStatistics | null> {
    try {
      const response = await this.request<DarkModeStatistics>('/statistics', 'GET');
      return response.success && response.data ? response.data : null;
    } catch (error) {
      console.error('获取统计信息失败:', error);
      return null;
    }
  }

  /**
   * 应用主题到文档
   */
  private async applyThemeToDocument(config: DarkModeConfig): Promise<void> {
    const colors = DEFAULT_THEME_COLORS[config.themeMode];
    
    // 设置 CSS 变量
    if (colors.primaryColor) {
      document.documentElement.style.setProperty('--theme-primary', colors.primaryColor);
    }
    if (colors.backgroundColor) {
      document.documentElement.style.setProperty('--theme-background', colors.backgroundColor);
    }
    if (colors.textColor) {
      document.documentElement.style.setProperty('--theme-text', colors.textColor);
    }
    if (colors.secondaryTextColor) {
      document.documentElement.style.setProperty('--theme-text-secondary', colors.secondaryTextColor);
    }
    if (colors.accentColor) {
      document.documentElement.style.setProperty('--theme-accent', colors.accentColor);
    }
    
    // 应用主题类
    document.body.classList.remove('theme-light', 'theme-dark', 'theme-custom');
    if (config.themeMode === ThemeMode.DARK) {
      document.body.classList.add('theme-dark');
    } else if (config.themeMode === ThemeMode.CUSTOM) {
      document.body.classList.add('theme-custom');
    } else {
      document.body.classList.add('theme-light');
    }
    
    // 应用特殊模式
    document.body.classList.toggle('high-contrast', config.highContrast || false);
    document.body.classList.toggle('reduce-motion', config.reduceMotion || false);
    document.body.classList.toggle('night-protection', config.nightProtection || false);
    
    // 应用透明度和字体缩放
    if (config.opacityLevel) {
      document.documentElement.style.setProperty('--theme-opacity', config.opacityLevel.toString());
    }
    if (config.fontScaleFactor) {
      document.documentElement.style.setProperty('--theme-font-scale', config.fontScaleFactor.toString());
    }
    
    // 保存到本地存储
    localStorage.setItem('darkmode_theme', config.themeMode);
    localStorage.setItem('darkmode_config', JSON.stringify(config));
  }

  /**
   * 初始化主题
   */
  public async initialize(): Promise<void> {
    // 从本地存储恢复主题
    const savedTheme = localStorage.getItem('darkmode_theme');
    const savedConfig = localStorage.getItem('darkmode_config');
    
    if (savedConfig) {
      try {
        this.currentConfig = JSON.parse(savedConfig);
        if (this.currentConfig) {
          await this.applyThemeToDocument(this.currentConfig);
        }
      } catch (error) {
        console.error('解析保存的配置失败:', error);
      }
    } else if (savedTheme) {
      // 如果没有配置，使用保存的主题模式
      const themeMode = savedTheme as ThemeMode;
      if (themeMode && this.userId) {
        await this.switchThemeMode(themeMode);
      }
    } else {
      // 默认跟随系统
      if (this.userId) {
        await this.switchThemeMode(ThemeMode.SYSTEM);
      }
    }
    
    // 监听系统主题变化
    if (window.matchMedia) {
      window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', async (e) => {
        if (this.currentConfig?.themeMode === ThemeMode.SYSTEM) {
          await this.applySystemTheme(e.matches);
        }
      });
    }
  }

  /**
   * 应用系统主题
   */
  private async applySystemTheme(isDark: boolean): Promise<void> {
    const theme = isDark ? ThemeMode.DARK : ThemeMode.LIGHT;
    const colors = DEFAULT_THEME_COLORS[theme];
    
    document.documentElement.style.setProperty('--theme-primary', colors.primaryColor || '');
    document.documentElement.style.setProperty('--theme-background', colors.backgroundColor || '');
    document.documentElement.style.setProperty('--theme-text', colors.textColor || '');
    document.documentElement.style.setProperty('--theme-text-secondary', colors.secondaryTextColor || '');
    document.documentElement.style.setProperty('--theme-accent', colors.accentColor || '');
    
    document.body.classList.remove('theme-light', 'theme-dark');
    document.body.classList.add(isDark ? 'theme-dark' : 'theme-light');
    
    localStorage.setItem('darkmode_system_theme', isDark ? 'dark' : 'light');
  }

  /**
   * 导出配置
   */
  public async exportConfig(configId: number): Promise<string | null> {
    if (!this.userId) {
      throw new Error('未设置用户 ID');
    }

    try {
      const response = await this.request<string>('/export', 'GET', {
        userId: this.userId,
        configId
      });
      
      return response.success && response.data ? response.data : null;
    } catch (error) {
      console.error('导出配置失败:', error);
      return null;
    }
  }

  /**
   * 清理配置
   */
  public async cleanup(): Promise<number> {
    try {
      const response = await this.request<{ cleanedCount: number }>('/cleanup', 'POST');
      return response.success && response.data ? response.data.cleanedCount : 0;
    } catch (error) {
      console.error('清理配置失败:', error);
      return 0;
    }
  }
}

// 导出单例
export const darkModeService = DarkModeService.getInstance();
export default darkModeService;
