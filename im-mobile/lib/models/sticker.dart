/// Sticker Model

class Sticker {
  final int id;
  final String stickerId;
  final String name;
  final String? description;
  final String imageUrl;
  final String thumbnailUrl;
  final String? animatedUrl;
  final int width;
  final int height;
  final int fileSizeBytes;
  final String fileFormat;
  final bool isAnimated;
  final int frameCount;
  final double? durationSeconds;
  final int frameRate;
  final String mimeType;
  final String checksum;
  final String checksumAlgorithm;
  final int sortOrder;
  final int totalUses;
  final int totalFavorites;
  final DateTime createdAt;
  final DateTime updatedAt;
  final bool isActive;
  final bool isDeleted;
  final int? packId;
  final StickerPack? pack;

  Sticker({
    required this.id,
    required this.stickerId,
    required this.name,
    this.description,
    required this.imageUrl,
    required this.thumbnailUrl,
    this.animatedUrl,
    required this.width,
    required this.height,
    required this.fileSizeBytes,
    required this.fileFormat,
    required this.isAnimated,
    required this.frameCount,
    this.durationSeconds,
    required this.frameRate,
    required this.mimeType,
    required this.checksum,
    required this.checksumAlgorithm,
    required this.sortOrder,
    required this.totalUses,
    required this.totalFavorites,
    required this.createdAt,
    required this.updatedAt,
    required this.isActive,
    required this.isDeleted,
    this.packId,
    this.pack,
  });

  factory Sticker.fromJson(Map<String, dynamic> json) {
    return Sticker(
      id: json['id'] as int,
      stickerId: json['stickerId'] as String,
      name: json['name'] as String,
      description: json['description'] as String?,
      imageUrl: json['imageUrl'] as String,
      thumbnailUrl: json['thumbnailUrl'] as String,
      animatedUrl: json['animatedUrl'] as String?,
      width: json['width'] as int,
      height: json['height'] as int,
      fileSizeBytes: json['fileSizeBytes'] as int,
      fileFormat: json['fileFormat'] as String,
      isAnimated: json['isAnimated'] as bool,
      frameCount: json['frameCount'] as int,
      durationSeconds: json['durationSeconds'] as double?,
      frameRate: json['frameRate'] as int,
      mimeType: json['mimeType'] as String,
      checksum: json['checksum'] as String,
      checksumAlgorithm: json['checksumAlgorithm'] as String,
      sortOrder: json['sortOrder'] as int,
      totalUses: json['totalUses'] as int,
      totalFavorites: json['totalFavorites'] as int,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      isActive: json['isActive'] as bool,
      isDeleted: json['isDeleted'] as bool,
      packId: json['packId'] as int?,
      pack: json['pack'] != null 
          ? StickerPack.fromJson(json['pack'] as Map<String, dynamic>) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'stickerId': stickerId,
      'name': name,
      if (description != null) 'description': description,
      'imageUrl': imageUrl,
      'thumbnailUrl': thumbnailUrl,
      if (animatedUrl != null) 'animatedUrl': animatedUrl,
      'width': width,
      'height': height,
      'fileSizeBytes': fileSizeBytes,
      'fileFormat': fileFormat,
      'isAnimated': isAnimated,
      'frameCount': frameCount,
      if (durationSeconds != null) 'durationSeconds': durationSeconds,
      'frameRate': frameRate,
      'mimeType': mimeType,
      'checksum': checksum,
      'checksumAlgorithm': checksumAlgorithm,
      'sortOrder': sortOrder,
      'totalUses': totalUses,
      'totalFavorites': totalFavorites,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'isActive': isActive,
      'isDeleted': isDeleted,
      if (packId != null) 'packId': packId,
      if (pack != null) 'pack': pack!.toJson(),
    };
  }

  Sticker copyWith({
    int? id,
    String? stickerId,
    String? name,
    String? description,
    String? imageUrl,
    String? thumbnailUrl,
    String? animatedUrl,
    int? width,
    int? height,
    int? fileSizeBytes,
    String? fileFormat,
    bool? isAnimated,
    int? frameCount,
    double? durationSeconds,
    int? frameRate,
    String? mimeType,
    String? checksum,
    String? checksumAlgorithm,
    int? sortOrder,
    int? totalUses,
    int? totalFavorites,
    DateTime? createdAt,
    DateTime? updatedAt,
    bool? isActive,
    bool? isDeleted,
    int? packId,
    StickerPack? pack,
  }) {
    return Sticker(
      id: id ?? this.id,
      stickerId: stickerId ?? this.stickerId,
      name: name ?? this.name,
      description: description ?? this.description,
      imageUrl: imageUrl ?? this.imageUrl,
      thumbnailUrl: thumbnailUrl ?? this.thumbnailUrl,
      animatedUrl: animatedUrl ?? this.animatedUrl,
      width: width ?? this.width,
      height: height ?? this.height,
      fileSizeBytes: fileSizeBytes ?? this.fileSizeBytes,
      fileFormat: fileFormat ?? this.fileFormat,
      isAnimated: isAnimated ?? this.isAnimated,
      frameCount: frameCount ?? this.frameCount,
      durationSeconds: durationSeconds ?? this.durationSeconds,
      frameRate: frameRate ?? this.frameRate,
      mimeType: mimeType ?? this.mimeType,
      checksum: checksum ?? this.checksum,
      checksumAlgorithm: checksumAlgorithm ?? this.checksumAlgorithm,
      sortOrder: sortOrder ?? this.sortOrder,
      totalUses: totalUses ?? this.totalUses,
      totalFavorites: totalFavorites ?? this.totalFavorites,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      isActive: isActive ?? this.isActive,
      isDeleted: isDeleted ?? this.isDeleted,
      packId: packId ?? this.packId,
      pack: pack ?? this.pack,
    );
  }

  bool get isValidForUse => isActive && !isDeleted && pack?.isAvailable == true;
  
  String getBestUrl({bool preferAnimated = true}) {
    if (preferAnimated && isAnimated && animatedUrl != null && animatedUrl!.isNotEmpty) {
      return animatedUrl!;
    }
    return imageUrl;
  }

  String get fileExtension {
    switch (fileFormat.toLowerCase()) {
      case 'png': return '.png';
      case 'jpg':
      case 'jpeg': return '.jpg';
      case 'gif': return '.gif';
      case 'webp': return '.webp';
      case 'apng': return '.apng';
      default: return '';
    }
  }

  String get displayName => name.isNotEmpty ? name : 'Sticker $stickerId';
  
  String get sizeDisplay {
    if (fileSizeBytes < 1024) {
      return '$fileSizeBytes B';
    } else if (fileSizeBytes < 1024 * 1024) {
      return '${(fileSizeBytes / 1024).toStringAsFixed(1)} KB';
    } else {
      return '${(fileSizeBytes / (1024 * 1024)).toStringAsFixed(1)} MB';
    }
  }

  String get dimensionsDisplay => '${width}x${height}';
  
  String get durationDisplay {
    if (!isAnimated || durationSeconds == null) return '';
    return '${durationSeconds!.toStringAsFixed(1)}s';
  }
}