import 'package:flutter/material.dart';
import '../../models/user.dart';

/// 多选好友列表项组件
class FriendMultiSelectItem extends StatelessWidget {
  final User user;
  final bool isSelected;
  final VoidCallback onToggle;
  final bool showCheckbox;

  const FriendMultiSelectItem({
    Key? key,
    required this.user,
    required this.isSelected,
    required this.onToggle,
    this.showCheckbox = true,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onToggle,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        decoration: BoxDecoration(
          color: isSelected ? Colors.blue.withOpacity(0.1) : Colors.transparent,
          border: Border(
            bottom: BorderSide(color: Colors.grey[200]!),
          ),
        ),
        child: Row(
          children: [
            // 复选框
            if (showCheckbox)
              Container(
                width: 24,
                height: 24,
                margin: const EdgeInsets.only(right: 12),
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  border: Border.all(
                    color: isSelected ? Colors.blue : Colors.grey[400]!,
                    width: 2,
                  ),
                  color: isSelected ? Colors.blue : Colors.transparent,
                ),
                child: isSelected
                    ? const Icon(Icons.check, size: 16, color: Colors.white)
                    : null,
              ),
            
            // 头像
            Stack(
              children: [
                CircleAvatar(
                  radius: 24,
                  backgroundImage: user.avatar != null
                      ? NetworkImage(user.avatar!)
                      : null,
                  backgroundColor: Colors.grey[300],
                  child: user.avatar == null
                      ? Text(
                          (user.remark ?? user.nickname).characters.first,
                          style: const TextStyle(fontSize: 18),
                        )
                      : null,
                ),
                if (user.isOnline)
                  Positioned(
                    right: 0,
                    bottom: 0,
                    child: Container(
                      width: 12,
                      height: 12,
                      decoration: BoxDecoration(
                        color: Colors.green,
                        shape: BoxShape.circle,
                        border: Border.all(color: Colors.white, width: 2),
                      ),
                    ),
                  ),
              ],
            ),
            
            const SizedBox(width: 12),
            
            // 用户信息
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(
                        child: Text(
                          user.remark ?? user.nickname,
                          style: const TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w500,
                          ),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      if (user.isStarred)
                        const Padding(
                          padding: EdgeInsets.only(left: 4),
                          child: Icon(Icons.star, size: 16, color: Colors.amber),
                        ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      if (user.remark != null)
                        Text(
                          '昵称: ${user.nickname}',
                          style: TextStyle(
                            fontSize: 13,
                            color: Colors.grey[600],
                          ),
                        ),
                      if (user.currentGroupName != null) ...[
                        const SizedBox(width: 8),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                          decoration: BoxDecoration(
                            color: Colors.grey[200],
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            user.currentGroupName!,
                            style: TextStyle(
                              fontSize: 11,
                              color: Colors.grey[700],
                            ),
                          ),
                        ),
                      ],
                    ],
                  ),
                ],
              ),
            ),
            
            // 选择指示器
            if (isSelected)
              const Icon(Icons.check_circle, color: Colors.blue),
          ],
        ),
      ),
    );
  }
}

/// 多选分组头部组件
class MultiSelectHeader extends StatelessWidget {
  final bool isAllSelected;
  final int selectedCount;
  final int totalCount;
  final VoidCallback onToggleAll;
  final String? title;

  const MultiSelectHeader({
    Key? key,
    required this.isAllSelected,
    required this.selectedCount,
    required this.totalCount,
    required this.onToggleAll,
    this.title,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.grey[100],
        border: Border(
          bottom: BorderSide(color: Colors.grey[300]!),
        ),
      ),
      child: Row(
        children: [
          GestureDetector(
            onTap: onToggleAll,
            child: Row(
              children: [
                Container(
                  width: 22,
                  height: 22,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(4),
                    border: Border.all(
                      color: isAllSelected ? Colors.blue : Colors.grey[400]!,
                    ),
                    color: isAllSelected ? Colors.blue : Colors.transparent,
                  ),
                  child: isAllSelected
                      ? const Icon(Icons.check, size: 16, color: Colors.white)
                      : null,
                ),
                const SizedBox(width: 8),
                Text(
                  isAllSelected ? '取消全选' : '全选',
                  style: TextStyle(
                    fontSize: 14,
                    color: isAllSelected ? Colors.blue : Colors.black87,
                  ),
                ),
              ],
            ),
          ),
          const Spacer(),
          Text(
            '已选 $selectedCount / $totalCount',
            style: TextStyle(
              fontSize: 14,
              color: selectedCount > 0 ? Colors.blue : Colors.grey[600],
              fontWeight: selectedCount > 0 ? FontWeight.w500 : FontWeight.normal,
            ),
          ),
        ],
      ),
    );
  }
}

/// 底部批量操作栏
class BatchActionBar extends StatelessWidget {
  final int selectedCount;
  final List<BatchAction> actions;
  final bool isProcessing;
  final String? processingText;

  const BatchActionBar({
    Key? key,
    required this.selectedCount,
    required this.actions,
    this.isProcessing = false,
    this.processingText,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 8,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (isProcessing)
              Column(
                children: [
                  LinearProgressIndicator(
                    backgroundColor: Colors.grey[200],
                  ),
                  const SizedBox(height: 12),
                  Text(processingText ?? '处理中...'),
                ],
              )
            else
              Row(
                children: [
                  Expanded(
                    flex: 2,
                    child: Text(
                      '已选择 $selectedCount 项',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w500,
                        color: selectedCount > 0 ? Colors.blue : Colors.grey,
                      ),
                    ),
                  ),
                  ...actions.map((action) {
                    return Padding(
                      padding: const EdgeInsets.only(left: 8),
                      child: ElevatedButton.icon(
                        onPressed: selectedCount > 0 && !action.isDestructive
                            ? action.onPressed
                            : null,
                        icon: Icon(action.icon, size: 18),
                        label: Text(action.label),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: action.isDestructive
                              ? Colors.red
                              : Colors.blue,
                          foregroundColor: Colors.white,
                        ),
                      ),
                    );
                  }),
                ],
              ),
          ],
        ),
      ),
    );
  }
}

/// 批量操作按钮数据
class BatchAction {
  final String label;
  final IconData icon;
  final VoidCallback onPressed;
  final bool isDestructive;

  const BatchAction({
    required this.label,
    required this.icon,
    required this.onPressed,
    this.isDestructive = false,
  });
}
