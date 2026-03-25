import 'package:dio/dio.dart';
import '../models/shared_media.dart';

class SharedMediaService {
  final Dio _dio;

  SharedMediaService(this._dio);

  Future<MediaPage> getSharedMedia({
    required String conversationId,
    String? mediaType,
    int page = 0,
    int size = 20,
    String? senderId,
    int? startTime,
    int? endTime,
  }) async {
    final queryParams = <String, dynamic>{
      'page': page,
      'size': size,
    };
    if (mediaType != null) queryParams['mediaType'] = mediaType;
    if (senderId != null) queryParams['senderId'] = senderId;
    if (startTime != null) queryParams['startTime'] = startTime;
    if (endTime != null) queryParams['endTime'] = endTime;

    final response = await _dio.get(
      '/media/conversation/$conversationId',
      queryParameters: queryParams,
    );
    return MediaPage.fromJson(response.data as Map<String, dynamic>);
  }

  Future<List<SharedMedia>> getMediaTimeline(
      String conversationId, {int page = 0, int size = 20}) async {
    final response = await _dio.get(
      '/media/timeline/$conversationId',
      queryParameters: {'page': page, 'size': size},
    );
    return (response.data as List<dynamic>)
        .map((e) => SharedMedia.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<MediaStatistics> getMediaStatistics(String conversationId) async {
    final response = await _dio.get('/media/statistics/$conversationId');
    return MediaStatistics.fromJson(response.data as Map<String, dynamic>);
  }

  Future<List<LinkPreview>> getSharedLinks(String conversationId,
      {int page = 0, int size = 20}) async {
    final response = await _dio.get(
      '/media/links/$conversationId',
      queryParameters: {'page': page, 'size': size},
    );
    return (response.data as List<dynamic>)
        .map((e) => LinkPreview.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<MediaPage> getAlbumMedia(
      String conversationId, String albumType,
      {int page = 0, int size = 50}) async {
    final response = await _dio.get(
      '/media/album/$conversationId',
      queryParameters: {'albumType': albumType, 'page': page, 'size': size},
    );
    return MediaPage.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteMedia(int mediaId) async {
    await _dio.delete('/media/$mediaId');
  }

  String formatFileSize(int bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    if (bytes < 1024 * 1024 * 1024) {
      return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
    }
    return '${(bytes / (1024 * 1024 * 1024)).toStringAsFixed(2)} GB';
  }

  String getMediaTypeIcon(MediaType type) {
    switch (type) {
      case MediaType.IMAGE: return '🖼️';
      case MediaType.VIDEO: return '🎬';
      case MediaType.AUDIO: return '🎵';
      case MediaType.FILE: return '📎';
      case MediaType.LINK: return '🔗';
      case MediaType.VOICE: return '🎤';
    }
  }
}
