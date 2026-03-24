/// Sticker Statistics Models

class StickerStats {
  final String packId;
  final int totalDownloads;
  final int totalLikes;
  final double averageRating;
  final int totalStickers;
  final DateTime createdAt;
  final DateTime updatedAt;
  final int totalStickerUses;
  final int totalStickerFavorites;
  final List<TopSticker> topStickers;

  StickerStats({
    required this.packId,
    required this.totalDownloads,
    required this.totalLikes,
    required this.averageRating,
    required this.totalStickers,
    required this.createdAt,
    required this.updatedAt,
    required this.totalStickerUses,
    required this.totalStickerFavorites,
    required this.topStickers,
  });

  factory StickerStats.fromJson(Map<String, dynamic> json) {
    return StickerStats(
      packId: json['pack_id'] as String,
      totalDownloads: json['total_downloads'] as int,
      totalLikes: json['total_likes'] as int,
      averageRating: (json['average_rating'] as num).toDouble(),
      totalStickers: json['total_stickers'] as int,
      createdAt: DateTime.parse(json['created_at'] as String),
      updatedAt: DateTime.parse(json['updated_at'] as String),
      totalStickerUses: json['total_sticker_uses'] as int,
      totalStickerFavorites: json['total_sticker_favorites'] as int,
      topStickers: (json['top_stickers'] as List<dynamic>)
          .map((e) => TopSticker.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'pack_id': packId,
      'total_downloads': totalDownloads,
      'total_likes': totalLikes,
      'average_rating': averageRating,
      'total_stickers': totalStickers,
      'created_at': createdAt.toIso8601String(),
      'updated_at': updatedAt.toIso8601String(),
      'total_sticker_uses': totalStickerUses,
      'total_sticker_favorites': totalStickerFavorites,
      'top_stickers': topStickers.map((e) => e.toJson()).toList(),
    };
  }
}

class TopSticker {
  final String stickerId;
  final String name;
  final int totalUses;
  final int totalFavorites;

  TopSticker({
    required this.stickerId,
    required this.name,
    required this.totalUses,
    required this.totalFavorites,
  });

  factory TopSticker.fromJson(Map<String, dynamic> json) {
    return TopSticker(
      stickerId: json['sticker_id'] as String,
      name: json['name'] as String,
      totalUses: json['total_uses'] as int,
      totalFavorites: json['total_favorites'] as int,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'sticker_id': stickerId,
      'name': name,
      'total_uses': totalUses,
      'total_favorites': totalFavorites,
    };
  }
}

class StickerSystemStats {
  final int totalPacks;
  final int officialPacks;
  final int featuredPacks;
  final int freePacks;
  final int pendingPacks;
  final int totalDownloads;
  final int totalLikes;
  final double averageRating;
  final int totalStickers;
  final int animatedStickers;
  final int totalStickerUses;
  final int totalStickerFavorites;
  final Map<String, int> categoryDistribution;
  final Map<String, int> formatDistribution;

  StickerSystemStats({
    required this.totalPacks,
    required this.officialPacks,
    required this.featuredPacks,
    required this.freePacks,
    required this.pendingPacks,
    required this.totalDownloads,
    required this.totalLikes,
    required this.averageRating,
    required this.totalStickers,
    required this.animatedStickers,
    required this.totalStickerUses,
    required this.totalStickerFavorites,
    required this.categoryDistribution,
    required this.formatDistribution,
  });

  factory StickerSystemStats.fromJson(Map<String, dynamic> json) {
    return StickerSystemStats(
      totalPacks: json['total_packs'] as int,
      officialPacks: json['official_packs'] as int,
      featuredPacks: json['featured_packs'] as int,
      freePacks: json['free_packs'] as int,
      pendingPacks: json['pending_packs'] as int,
      totalDownloads: json['total_downloads'] as int,
      totalLikes: json['total_likes'] as int,
      averageRating: (json['average_rating'] as num).toDouble(),
      totalStickers: json['total_stickers'] as int,
      animatedStickers: json['animated_stickers'] as int,
      totalStickerUses: json['total_sticker_uses'] as int,
      totalStickerFavorites: json['total_sticker_favorites'] as int,
      categoryDistribution: (json['category_distribution'] as Map<String, dynamic>)
          .map((k, v) => MapEntry(k, v as int)),
      formatDistribution: (json['format_distribution'] as Map<String, dynamic>)
          .map((k, v) => MapEntry(k, v as int)),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'total_packs': totalPacks,
      'official_packs': officialPacks,
      'featured_packs': featuredPacks,
      'free_packs': freePacks,
      'pending_packs': pendingPacks,
      'total_downloads': totalDownloads,
      'total_likes': totalLikes,
      'average_rating': averageRating,
      'total_stickers': totalStickers,
      'animated_stickers': animatedStickers,
      'total_sticker_uses': totalStickerUses,
      'total_sticker_favorites': totalStickerFavorites,
      'category_distribution': categoryDistribution,
      'format_distribution': formatDistribution,
    };
  }
}