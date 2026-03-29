/**
 * 快捷操作菜单 API 封装
 * RESTful API 调用
 */

import {
  MessageQuickActionMenu,
  MenuType,
  CreateMenuRequest,
  UpdateMenuRequest,
  MenuListResponse,
  MenuResponse
} from '../types/quick-action-menu';

const API_BASE = '/api/quick-action-menu';

class QuickActionMenuApi {
  
  // ============ 菜单 CRUD ============
  
  /**
   * 创建菜单
   */
  async createMenu(data: CreateMenuRequest): Promise<MessageQuickActionMenu> {
    const response = await fetch(API_BASE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    if (!response.ok) throw new Error('创建菜单失败');
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  /**
   * 更新菜单
   */
  async updateMenu(menuId: number, data: UpdateMenuRequest): Promise<MessageQuickActionMenu> {
    const response = await fetch(`${API_BASE}/${menuId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    if (!response.ok) throw new Error('更新菜单失败');
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  /**
   * 删除菜单
   */
  async deleteMenu(menuId: number): Promise<void> {
    const response = await fetch(`${API_BASE}/${menuId}`, {
      method: 'DELETE'
    });
    if (!response.ok) throw new Error('删除菜单失败');
  }
  
  /**
   * 获取菜单详情
   */
  async getMenu(menuId: number): Promise<MessageQuickActionMenu> {
    const response = await fetch(`${API_BASE}/${menuId}`);
    if (!response.ok) throw new Error('获取菜单失败');
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  // ============ 用户菜单 ============
  
  /**
   * 获取用户的所有菜单
   */
  async getUserMenus(userId: number): Promise<MessageQuickActionMenu[]> {
    const response = await fetch(`${API_BASE}/user/${userId}`);
    if (!response.ok) throw new Error('获取用户菜单失败');
    const result: MenuListResponse = await response.json();
    return result.data;
  }
  
  /**
   * 获取用户特定类型的菜单
   */
  async getUserMenusByType(userId: number, menuType: MenuType): Promise<MessageQuickActionMenu[]> {
    const response = await fetch(`${API_BASE}/user/${userId}/type/${menuType}`);
    if (!response.ok) throw new Error('获取菜单失败');
    const result: MenuListResponse = await response.json();
    return result.data;
  }
  
  /**
   * 获取用户的默认菜单
   */
  async getDefaultMenus(userId: number): Promise<MessageQuickActionMenu[]> {
    const response = await fetch(`${API_BASE}/user/${userId}/default`);
    if (!response.ok) throw new Error('获取默认菜单失败');
    const result: MenuListResponse = await response.json();
    return result.data;
  }
  
  /**
   * 获取特定类型的默认菜单
   */
  async getDefaultMenuByType(userId: number, menuType: MenuType): Promise<MessageQuickActionMenu | null> {
    const response = await fetch(`${API_BASE}/user/${userId}/default/type/${menuType}`);
    if (!response.ok) return null;
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  /**
   * 初始化用户默认菜单
   */
  async initializeDefaultMenus(userId: number): Promise<MessageQuickActionMenu[]> {
    const response = await fetch(`${API_BASE}/user/${userId}/initialize`, {
      method: 'POST'
    });
    if (!response.ok) throw new Error('初始化菜单失败');
    const result: MenuListResponse = await response.json();
    return result.data;
  }
  
  // ============ 菜单操作 ============
  
  /**
   * 设置菜单为默认
   */
  async setAsDefault(menuId: number): Promise<MessageQuickActionMenu> {
    const response = await fetch(`${API_BASE}/${menuId}/set-default`, {
      method: 'POST'
    });
    if (!response.ok) throw new Error('设置默认菜单失败');
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  /**
   * 启用菜单
   */
  async enableMenu(menuId: number): Promise<MessageQuickActionMenu> {
    const response = await fetch(`${API_BASE}/${menuId}/enable`, {
      method: 'POST'
    });
    if (!response.ok) throw new Error('启用菜单失败');
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  /**
   * 禁用菜单
   */
  async disableMenu(menuId: number): Promise<MessageQuickActionMenu> {
    const response = await fetch(`${API_BASE}/${menuId}/disable`, {
      method: 'POST'
    });
    if (!response.ok) throw new Error('禁用菜单失败');
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  /**
   * 更新菜单排序
   */
  async updateSortOrder(menuId: number, sortOrder: number): Promise<void> {
    const response = await fetch(`${API_BASE}/${menuId}/sort/${sortOrder}`, {
      method: 'POST'
    });
    if (!response.ok) throw new Error('更新排序失败');
  }
  
  /**
   * 复制菜单
   */
  async duplicateMenu(menuId: number, newName: string): Promise<MessageQuickActionMenu> {
    const response = await fetch(`${API_BASE}/${menuId}/duplicate?newName=${encodeURIComponent(newName)}`, {
      method: 'POST'
    });
    if (!response.ok) throw new Error('复制菜单失败');
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  // ============ 会话菜单 ============
  
  /**
   * 获取会话特定的菜单
   */
  async getConversationMenus(userId: number, conversationId: number): Promise<MessageQuickActionMenu[]> {
    const response = await fetch(`${API_BASE}/user/${userId}/conversation/${conversationId}`);
    if (!response.ok) throw new Error('获取会话菜单失败');
    const result: MenuListResponse = await response.json();
    return result.data;
  }
  
  /**
   * 为会话设置自定义菜单
   */
  async setConversationMenu(userId: number, conversationId: number, data: CreateMenuRequest): Promise<MessageQuickActionMenu> {
    const response = await fetch(`${API_BASE}/user/${userId}/conversation/${conversationId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    if (!response.ok) throw new Error('设置会话菜单失败');
    const result: MenuResponse = await response.json();
    return result.data;
  }
  
  /**
   * 重置会话菜单为默认
   */
  async resetConversationMenu(userId: number, conversationId: number): Promise<void> {
    const response = await fetch(`${API_BASE}/user/${userId}/conversation/${conversationId}/reset`, {
      method: 'POST'
    });
    if (!response.ok) throw new Error('重置菜单失败');
  }
}

export const quickActionMenuApi = new QuickActionMenuApi();
export default quickActionMenuApi;
