/**
 * 群公告模型 - 移动端 Flutter
 */

enum AnnouncementType { normal, important, pinned }

class Announcement {
    final String announcementId;
    final String groupId;
    final String authorId;
    final String authorName;
    final String title;
    final String content;
    final AnnouncementType type;
    final bool pinned;
    final bool edited;
    final DateTime createdAt;
    final DateTime? updatedAt;
    final DateTime? pinnedAt;
    final int viewCount;
    final int confirmCount;
    final Set<String> confirmedUserIds;
    final bool deleted;

    Announcement({
        required this.announcementId,
        required this.groupId,
        required this.authorId,
        required this.authorName,
        required this.title,
        required this.content,
        required this.type,
        this.pinned = false,
        this.edited = false,
        required this.createdAt,
        this.updatedAt,
        this.pinnedAt,
        this.viewCount = 0,
        this.confirmCount = 0,
        this.confirmedUserIds = const {},
        this.deleted = false,
    });

    factory Announcement.fromJson(Map<String, dynamic> json) {
        return Announcement(
            announcementId: json['announcementId'] ?? '',
            groupId: json['groupId'] ?? '',
            authorId: json['authorId'] ?? '',
            authorName: json['authorName'] ?? '',
            title: json['title'] ?? '',
            content: json['content'] ?? '',
            type: _parseType(json['type']),
            pinned: json['pinned'] ?? false,
            edited: json['edited'] ?? false,
            createdAt: _parseDateTime(json['createdAt']),
            updatedAt: _parseDateTimeNullable(json['updatedAt']),
            pinnedAt: _parseDateTimeNullable(json['pinnedAt']),
            viewCount: json['viewCount'] ?? 0,
            confirmCount: json['confirmCount'] ?? 0,
            confirmedUserIds: (json['confirmedUserIds'] as List<dynamic>?)
                    ?.map((e) => e.toString())
                    .toSet() ??
                {},
            deleted: json['deleted'] ?? false,
        );
    }

    Map<String, dynamic> toJson() {
        return {
            'announcementId': announcementId,
            'groupId': groupId,
            'authorId': authorId,
            'authorName': authorName,
            'title': title,
            'content': content,
            'type': type.name.toUpperCase(),
            'pinned': pinned,
            'edited': edited,
            'createdAt': createdAt.toIso8601String(),
            'updatedAt': updatedAt?.toIso8601String(),
            'pinnedAt': pinnedAt?.to8601String(),
            'viewCount': viewCount,
            'confirmCount': confirmCount,
            'confirmedUserIds': confirmedUserIds.toList(),
            'deleted': deleted,
        };
    }

    Announcement copyWith({
        String? announcementId,
        String? groupId,
        String? authorId,
        String? authorName,
        String? title,
        String? content,
        AnnouncementType? type,
        bool? pinned,
        bool? edited,
        DateTime? createdAt,
        DateTime? updatedAt,
        DateTime? pinnedAt,
        int? viewCount,
        int? confirmCount,
        Set<String>? confirmedUserIds,
        bool? deleted,
    }) {
        return Announcement(
            announcementId: announcementId ?? this.announcementId,
            groupId: groupId ?? this.groupId,
            authorId: authorId ?? this.authorId,
            authorName: authorName ?? this.authorName,
            title: title ?? this.title,
            content: content ?? this.content,
            type: type ?? this.type,
            pinned: pinned ?? this.pinned,
            edited: edited ?? this.edited,
            createdAt: createdAt ?? this.createdAt,
            updatedAt: updatedAt ?? this.updatedAt,
            pinnedAt: pinnedAt ?? this.pinnedAt,
            viewCount: viewCount ?? this.viewCount,
            confirmCount: confirmCount ?? this.confirmCount,
            confirmedUserIds: confirmedUserIds ?? this.confirmedUserIds,
            deleted: deleted ?? this.deleted,
        );
    }

    bool hasConfirmed(String userId) => confirmedUserIds.contains(userId);

    String get typeLabel {
        switch (type) {
            case AnnouncementType.important:
                return '重要';
            case AnnouncementType.pinned:
                return '置顶';
            default:
                return '公告';
        }
    }
}

AnnouncementType _parseType(String? type) {
    switch (type?.toUpperCase()) {
        case 'IMPORTANT':
            return AnnouncementType.important;
        case 'PINNED':
            return AnnouncementType.pinned;
        default:
            return AnnouncementType.normal;
    }
}

DateTime _parseDateTime(String? s) {
    if (s == null) return DateTime.now();
    return DateTime.tryParse(s) ?? DateTime.now();
}

DateTime? _parseDateTimeNullable(String? s) {
    if (s == null) return null;
    return DateTime.tryParse(s);
}

class CreateAnnouncementRequest {
    final String groupId;
    final String authorId;
    final String authorName;
    final String title;
    final String content;
    final String type;

    CreateAnnouncementRequest({
        required this.groupId,
        required this.authorId,
        required this.authorName,
        required this.title,
        required this.content,
        this.type = 'NORMAL',
    });

    Map<String, dynamic> toJson() {
        return {
            'groupId': groupId,
            'authorId': authorId,
            'authorName': authorName,
            'title': title,
            'content': content,
            'type': type,
        };
    }
}

class PagedAnnouncements {
    final List<Announcement> announcements;
    final int total;
    final int page;
    final int pageSize;
    final int totalPages;

    PagedAnnouncements({
        required this.announcements,
        required this.total,
        required this.page,
        required this.pageSize,
        required this.totalPages,
    });

    factory PagedAnnouncements.fromJson(Map<String, dynamic> json) {
        return PagedAnnouncements(
            announcements: (json['announcements'] as List<dynamic>?)
                    ?.map((e) => Announcement.fromJson(e as Map<String, dynamic>))
                    .toList() ??
                [],
            total: json['total'] ?? 0,
            page: json['page'] ?? 1,
            pageSize: json['pageSize'] ?? 20,
            totalPages: json['totalPages'] ?? 1,
        );
    }
}

class AnnouncementStatistics {
    final int total;
    final int pinned;
    final int important;
    final int totalViews;

    AnnouncementStatistics({
        required this.total,
        required this.pinned,
        required this.important,
        required this.totalViews,
    });

    factory AnnouncementStatistics.fromJson(Map<String, dynamic> json) {
        return AnnouncementStatistics(
            total: json['total'] ?? 0,
            pinned: json['pinned'] ?? 0,
            important: json['important'] ?? 0,
            totalViews: json['totalViews'] ?? 0,
        );
    }
}
