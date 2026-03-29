/**
 * 快捷操作菜单状态管理 (MobX Store)
 */

import 'package:mobx/mobx.dart';
import '../models/message_quick_action_menu_model.dart';
import '../services/quick_action_menu_service.dart';

part 'quick_action_menu_store.g.dart';

class QuickActionMenuStore = _QuickActionMenuStore with _$QuickActionMenuStore;

abstract class _QuickActionMenuStore with Store {
  final QuickActionMenuService _service;

  _QuickActionMenuStore(this._service);

  // ============ 状态 ============
  
  @observable
  ObservableList<MessageQuickActionMenu> menus = ObservableList<MessageQuickActionMenu>();

  @observable
  MessageQuickActionMenu? currentMenu;

  @observable
  ObservableList<QuickActionItem> visibleItems = ObservableList<QuickActionItem>();

  @observable
  bool isLoading = false;

  @observable
  String? error;

  @observable
  bool isMenuOpen = false;

  @observable
  MenuPosition? menuPosition;

  @observable
  MenuContext? menuContext;

  @observable
  MenuType? currentMenuType;

  @observable
  QuickActionMenuConfig config = QuickActionMenuConfig.defaultConfig;

  // ============ 计算属性 ============

  @computed
  List<MessageQuickActionMenu> get enabledMenus {
    return menus.where((m) => m.isEnabled).toList();
  }

  @computed
  List<MessageQuickActionMenu> get defaultMenus {
    return menus.where((m) => m.isDefault && m.isEnabled).toList();
  }

  MessageQuickActionMenu? menuByType(MenuType type) {
    return menus.firstWhere(
      (m) => m.menuType == type && m.isDefault,
      orElse: () => null as MessageQuickActionMenu,
    );
  }

  // ============ 数据加载 ============

  @action
  Future<void> loadUserMenus(int userId) async {
    isLoading = true;
    error = null;
    try {
      final result = await _service.getUserMenus(userId);
      menus = ObservableList.of(result);
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> initializeDefaultMenus(int userId) async {
    isLoading = true;
    error = null;
    try {
      final result = await _service.initializeDefaultMenus(userId);
      menus = ObservableList.of(result);
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  // ============ 菜单显示 ============

  @action
  void showMenu({
    required MenuPosition position,
    required MenuContext context,
    required MenuType menuType,
  }) {
    final menu = menuByType(menuType);
    if (menu == null) {
      return;
    }

    final filtered = context.filterVisibleItems(menu.items);
    
    currentMenu = menu;
    visibleItems = ObservableList.of(filtered);
    menuPosition = position;
    menuContext = context;
    currentMenuType = menuType;
    isMenuOpen = true;
  }

  @action
  void closeMenu() {
    isMenuOpen = false;
    currentMenu = null;
    visibleItems = ObservableList<QuickActionItem>();
    menuPosition = null;
    menuContext = null;
    currentMenuType = null;
  }

  // ============ 操作执行 ============

  final Map<ActionType, Function(MenuContext, QuickActionItem)> _actionHandlers = {};

  void registerActionHandler(
    ActionType type,
    Function(MenuContext, QuickActionItem) handler,
  ) {
    _actionHandlers[type] = handler;
  }

  @action
  Future<bool> executeAction(QuickActionItem item) async {
    if (menuContext == null) return false;

    final handler = _actionHandlers[item.actionType];
    if (handler == null) {
      return false;
    }

    try {
      await handler(menuContext!, item);
      closeMenu();
      return true;
    } catch (e) {
      error = e.toString();
      return false;
    }
  }

  // ============ 菜单管理 ============

  @action
  Future<void> createMenu(Map<String, dynamic> data) async {
    isLoading = true;
    error = null;
    try {
      final menu = await _service.createMenu(data);
      menus.add(menu);
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> deleteMenu(int menuId) async {
    isLoading = true;
    error = null;
    try {
      await _service.deleteMenu(menuId);
      menus.removeWhere((m) => m.id == menuId);
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> setAsDefault(int menuId) async {
    isLoading = true;
    error = null;
    try {
      final menu = await _service.setAsDefault(menuId);
      // 更新同类型菜单的默认状态
      final updatedMenus = menus.map((m) {
        if (m.menuType == menu.menuType) {
          return m.copyWith(isDefault: m.id == menuId);
        }
        return m;
      }).toList();
      menus = ObservableList.of(updatedMenus);
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  // ============ 配置 ============

  @action
  void updateConfig(QuickActionMenuConfig newConfig) {
    config = newConfig;
  }

  @action
  void resetConfig() {
    config = QuickActionMenuConfig.defaultConfig;
  }
}
