import 'dart:async';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter_sound/flutter_sound.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:path_provider/path_provider.dart';

class VoiceService extends ChangeNotifier {
  FlutterSoundRecorder? _recorder;
  FlutterSoundPlayer? _player;
  
  bool _isRecording = false;
  bool _isPlaying = false;
  bool _isInitialized = false;
  String? _currentRecordingPath;
  String? _error;
  double _recordingLevel = 0.0;
  Timer? _levelTimer;
  Duration _recordingDuration = Duration.zero;
  Timer? _durationTimer;
  
  bool get isRecording => _isRecording;
  bool get isPlaying => _isPlaying;
  bool get isInitialized => _isInitialized;
  String? get error => _error;
  double get recordingLevel => _recordingLevel;
  Duration get recordingDuration => _recordingDuration;
  String? get currentRecordingPath => _currentRecordingPath;
  
  StreamSubscription? _recorderSubscription;
  StreamSubscription? _playerSubscription;

  Future<void> initialize() async {
    if (_isInitialized) return;
    
    try {
      _recorder = FlutterSoundRecorder();
      _player = FlutterSoundPlayer();
      
      final micStatus = await Permission.microphone.request();
      final storageStatus = await Permission.storage.request();
      
      if (micStatus != PermissionStatus.granted) {
        throw Exception('需要麦克风权限');
      }
      
      await _recorder!.openRecorder();
      await _player!.openPlayer();
      
      _isInitialized = true;
      notifyListeners();
    } catch (e) {
      _error = '初始化失败: $e';
      notifyListeners();
    }
  }

  Future<String?> startRecording() async {
    if (!_isInitialized) {
      await initialize();
    }
    
    if (_isRecording) return null;
    
    try {
      final directory = await getTemporaryDirectory();
      final fileName = 'voice_${DateTime.now().millisecondsSinceEpoch}.aac';
      _currentRecordingPath = '${directory.path}/$fileName';
      
      await _recorder!.startRecorder(
        toFile: _currentRecordingPath,
        codec: Codec.aacADTS,
        sampleRate: 44100,
        bitRate: 64000,
      );
      
      _isRecording = true;
      _recordingDuration = Duration.zero;
      _error = null;
      
      _durationTimer = Timer.periodic(const Duration(seconds: 1), (_) {
        _recordingDuration += const Duration(seconds: 1);
        notifyListeners();
      });
      
      _recorderSubscription = _recorder!.onProgress!.listen((event) {
        if (event.decibels != null) {
          _recordingLevel = (event.decibels! + 160) / 160;
          notifyListeners();
        }
      });
      
      notifyListeners();
      return _currentRecordingPath;
    } catch (e) {
      _error = '录音失败: $e';
      notifyListeners();
      return null;
    }
  }

  Future<String?> stopRecording() async {
    if (!_isRecording) return null;
    
    try {
      await _recorder!.stopRecorder();
      _recorderSubscription?.cancel();
      _durationTimer?.cancel();
      
      _isRecording = false;
      _recordingLevel = 0.0;
      notifyListeners();
      
      return _currentRecordingPath;
    } catch (e) {
      _error = '停止录音失败: $e';
      notifyListeners();
      return null;
    }
  }

  Future<void> cancelRecording() async {
    if (!_isRecording) return;
    
    await stopRecording();
    
    if (_currentRecordingPath != null) {
      final file = File(_currentRecordingPath!);
      if (await file.exists()) {
        await file.delete();
      }
    }
    
    _currentRecordingPath = null;
    notifyListeners();
  }

  Future<void> playRecording(String path) async {
    if (_isPlaying) {
      await stopPlaying();
    }
    
    try {
      await _player!.startPlayer(
        fromURI: path,
        whenFinished: () {
          _isPlaying = false;
          notifyListeners();
        },
      );
      
      _isPlaying = true;
      notifyListeners();
      
      _playerSubscription = _player!.onProgress!.listen((event) {
        notifyListeners();
      });
    } catch (e) {
      _error = '播放失败: $e';
      notifyListeners();
    }
  }

  Future<void> stopPlaying() async {
    if (!_isPlaying) return;
    
    try {
      await _player!.stopPlayer();
      _playerSubscription?.cancel();
      _isPlaying = false;
      notifyListeners();
    } catch (e) {
      _error = '停止播放失败: $e';
      notifyListeners();
    }
  }

  Future<void> playFromUrl(String url) async {
    if (_isPlaying) {
      await stopPlaying();
    }
    
    try {
      await _player!.startPlayer(
        fromURI: url,
        whenFinished: () {
          _isPlaying = false;
          notifyListeners();
        },
      );
      
      _isPlaying = true;
      notifyListeners();
    } catch (e) {
      _error = '播放网络音频失败: $e';
      notifyListeners();
    }
  }

  String get formattedDuration {
    final minutes = _recordingDuration.inMinutes.toString().padLeft(2, '0');
    final seconds = (_recordingDuration.inSeconds % 60).toString().padLeft(2, '0');
    return '$minutes:$seconds';
  }

  Future<void> deleteRecording(String path) async {
    try {
      final file = File(path);
      if (await file.exists()) {
        await file.delete();
      }
      
      if (_currentRecordingPath == path) {
        _currentRecordingPath = null;
      }
      notifyListeners();
    } catch (e) {
      _error = '删除录音失败: $e';
      notifyListeners();
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }

  @override
  void dispose() {
    _durationTimer?.cancel();
    _levelTimer?.cancel();
    _recorderSubscription?.cancel();
    _playerSubscription?.cancel();
    _recorder?.closeRecorder();
    _player?.closePlayer();
    super.dispose();
  }
}
