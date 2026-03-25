/// Sticker Pack Model

class StickerPack {
  final int id;
  final String packId;
  final String name;
  final String? description;
  final String author;
  final String? publisher;
  final String coverUrl;
  final String coverThumbnailUrl;
  final int totalStickers;
  final int totalDownloads;
  final int totalLikes;
  final double averageRating;
  final bool isOfficial;
  final bool isFeatured;
  final bool isFree;
  final double? price;
  final String? currency;
  final String category;
  final List<String> tags;
  final String licenseType;
  final String licenseUrl;
  final String fileFormat;
  final int totalSizeBytes;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? publishedAt;
  final bool isActive;
  final bool isDeleted;

  StickerPack({
    required this.id,
    required this.packId,
    required this.name,
    this.description,
    required this.author,
    this.publisher,
    required this.coverUrl,
    required this.coverThumbnailUrl,
    required this.totalStickers,
    required this.totalDownloads,
    required this.totalLikes,
    required this.averageRating,
    required this.isOfficial,
    required this.isFeatured,
    required this.isFree,
    this.price,
    this.currency,
    required this.category,
    required this.tags,
    required this.licenseType,
    required this.licenseUrl,
    required this.fileFormat,
    required this.totalSizeBytes,
    required this.createdAt,
    required this.updatedAt,
    this.publishedAt,
    required this.isActive,
    required this.isDeleted,
  });

  factory StickerPack.fromJson(Map<String, dynamic> json) {
    return StickerPack(
      id: json['id'] as int,
      packId: json['packId'] as String,
      name: json['name'] as String,
      description: json['description'] as String?,
      author: json['author'] as String,
      publisher: json['publisher'] as String?,
      coverUrl: json['coverUrl'] as String,
      coverThumbnailUrl: json['coverThumbnailUrl'] as String,
      totalStickers: json['totalStickers'] as int,
      totalDownloads: json['totalDownloads'] as int,
      totalLikes: json['totalLikes'] as int,
      averageRating: (json['averageRating'] as num).toDouble(),
      isOfficial: json['isOfficial'] as bool,
      isFeatured: json['isFeatured'] as bool,
      isFree: json['isFree'] as bool,
      price: json['price'] as double?,
      currency: json['currency'] as String?,
      category: json['category'] as String,
      tags: (json['tags'] as List<dynamic>?)?.map((e) => e as String).toList() ?? [],
      licenseType: json['licenseType'] as String,
      licenseUrl: json['licenseUrl'] as String,
      fileFormat: json['fileFormat'] as String,
      totalSizeBytes: json['totalSizeBytes'] as int,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      publishedAt: json['publishedAt'] != null 
          ? DateTime.parse(json['publishedAt'] as String) 
          : null,
      isActive: json['isActive'] as bool,
      isDeleted: json['isDeleted'] as bool,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'packId': packId,
      'name': name,
      if (description != null) 'description': description,
      'author': author,
      if (publisher != null) 'publisher': publisher,
      'coverUrl': coverUrl,
      'coverThumbnailUrl': coverThumbnailUrl,
      'totalStickers': totalStickers,
      'totalDownloads': totalDownloads,
      'totalLikes': totalLikes,
      'averageRating': averageRating,
      'isOfficial': isOfficial,
      'isFeatured': isFeatured,
      'isFree': isFree,
      if (price != null) 'price': price,
      if (currency != null) 'currency': currency,
      'category': category,
      'tags': tags,
      'licenseType': licenseType,
      'licenseUrl': licenseUrl,
      'fileFormat': fileFormat,
      'totalSizeBytes': totalSizeBytes,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      if (publishedAt != null) 'publishedAt': publishedAt!.toIso8601String(),
      'isActive': isActive,
      'isDeleted': isDeleted,
    };
  }

  StickerPack copyWith({
    int? id,
    String? packId,
    String? name,
    String? description,
    String? author,
    String? publisher,
    String? coverUrl,
    String? coverThumbnailUrl,
    int? totalStickers,
    int? totalDownloads,
    int? totalLikes,
    double? averageRating,
    bool? isOfficial,
    bool? isFeatured,
    bool? isFree,
    double? price,
    String? currency,
    String? category,
    List<String>? tags,
    String? licenseType,
    String? licenseUrl,
    String? fileFormat,
    int? totalSizeBytes,
    DateTime? createdAt,
    DateTime? updatedAt,
    DateTime? publishedAt,
    bool? isActive,
    bool? isDeleted,
  }) {
    return StickerPack(
      id: id ?? this.id,
      packId: packId ?? this.packId,
      name: name ?? this.name,
      description: description ?? this.description,
      author: author ?? this.author,
      publisher: publisher ?? this.publisher,
      coverUrl: coverUrl ?? this.coverUrl,
      coverThumbnailUrl: coverThumbnailUrl ?? this.coverThumbnailUrl,
      totalStickers: totalStickers ?? this.totalStickers,
      totalDownloads: totalDownloads ?? this.totalDownloads,
      totalLikes: totalLikes ?? this.totalLikes,
      averageRating: averageRating ?? this.averageRating,
      isOfficial: isOfficial ?? this.isOfficial,
      isFeatured: isFeatured ?? this.isFeatured,
      isFree: isFree ?? this.isFree,
      price: price ?? this.price,
      currency: currency ?? this.currency,
      category: category ?? this.category,
      tags: tags ?? this.tags,
      licenseType: licenseType ?? this.licenseType,
      licenseUrl: licenseUrl ?? this.licenseUrl,
      fileFormat: fileFormat ?? this.fileFormat,
      totalSizeBytes: totalSizeBytes ?? this.totalSizeBytes,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      publishedAt: publishedAt ?? this.publishedAt,
      isActive: isActive ?? this.isActive,
      isDeleted: isDeleted ?? this.isDeleted,
    );
  }

  bool get isPaid => !isFree;
  bool get hasPrice => price != null && price! > 0;
  bool get isAvailable => isActive && !isDeleted && publishedAt != null;
  
  String get displayPrice {
    if (isFree) return 'Free';
    if (price == null) return 'Unknown';
    return '${currency ?? '\$'}${price!.toStringAsFixed(2)}';
  }

  String get ratingDisplay => averageRating.toStringAsFixed(1);
}