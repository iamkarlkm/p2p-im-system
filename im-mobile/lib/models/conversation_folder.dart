class ConversationFolder {
  final int id;
  final int userId;
  final String name;
  final String? icon;
  final String? color;
  final int sortOrder;
  final bool isCollapsed;
  final DateTime createdAt;
  final DateTime? updatedAt;

  ConversationFolder({
    required this.id,
    required this.userId,
    required this.name,
    this.icon,
    this.color,
    required this.sortOrder,
    required this.isCollapsed,
    required this.createdAt,
    this.updatedAt,
  });

  factory ConversationFolder.fromJson(Map<String, dynamic> json) {
    return ConversationFolder(
      id: json['id'],
      userId: json['userId'],
      name: json['name'],
      icon: json['icon'],
      color: json['color'],
      sortOrder: json['sortOrder'] ?? 0,
      isCollapsed: json['isCollapsed'] ?? false,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
    );
  }
}
