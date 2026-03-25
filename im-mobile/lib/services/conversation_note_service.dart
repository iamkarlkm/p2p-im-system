import 'package:dio/dio.dart';
import '../models/conversation_note.dart';

class ConversationNoteService {
  final Dio _dio;

  ConversationNoteService(this._dio);

  Future<Note> createNote(NoteRequest request) async {
    final response = await _dio.post('/notes', data: request.toJson());
    return Note.fromJson(response.data as Map<String, dynamic>);
  }

  Future<NotePage> getNotes(String conversationId, {int page = 0, int size = 20}) async {
    final response = await _dio.get(
      '/notes/conversation/$conversationId',
      queryParameters: {'page': page, 'size': size},
    );
    return NotePage.fromJson(response.data as Map<String, dynamic>);
  }

  Future<Note> updateNote(NoteRequest request) async {
    final response = await _dio.put('/notes', data: request.toJson());
    return Note.fromJson(response.data as Map<String, dynamic>);
  }

  Future<void> deleteNote(int noteId) async {
    await _dio.delete('/notes/$noteId');
  }

  Future<TagInfo> createTag(String name, {String? color}) async {
    final response = await _dio.post('/notes/tags', data: {
      'name': name,
      if (color != null) 'color': color,
    });
    return TagInfo.fromJson(response.data as Map<String, dynamic>);
  }

  Future<List<TagInfo>> getTags() async {
    final response = await _dio.get('/notes/tags');
    return (response.data as List<dynamic>)
        .map((e) => TagInfo.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<void> deleteTag(int tagId) async {
    await _dio.delete('/notes/tags/$tagId');
  }
}
