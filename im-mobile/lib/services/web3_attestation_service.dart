import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../models/message_attestation.dart';

class Web3AttestationService extends ChangeNotifier {
  static final Web3AttestationService _instance = Web3AttestationService._internal();
  factory Web3AttestationService() => _instance;
  Web3AttestationService._internal();

  final String _baseUrl = 'https://api.im.example.com/v1';
  final Map<String, MessageAttestation> _attestations = {};
  final Map<String, Timer> _pollingTimers = {};
  final List<MessageAttestation> _recentAttestations = [];
  
  bool _isLoading = false;
  String? _error;
  AttestationStatistics? _statistics;

  List<MessageAttestation> get attestations => List.unmodifiable(_attestations.values);
  List<MessageAttestation> get recentAttestations => List.unmodifiable(_recentAttestations);
  bool get isLoading => _isLoading;
  String? get error => _error;
  AttestationStatistics? get statistics => _statistics;

  final _statusController = StreamController<MessageAttestation>.broadcast();
  Stream<MessageAttestation> get statusStream => _statusController.stream;

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }

  void _setError(String? value) {
    _error = value;
    if (value != null) notifyListeners();
  }

  Future<MessageAttestation?> createAttestation({
    required String messageId,
    required String messageContent,
    required BlockchainNetwork network,
    int? priority,
  }) async {
    try {
      _setLoading(true);
      _setError(null);

      final messageHash = MessageAttestation.calculateHash(messageContent);

      final response = await http.post(
        Uri.parse('$_baseUrl/attestations/create'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'messageId': messageId,
          'messageContent': messageContent,
          'messageHash': messageHash,
          'network': network.name.toUpperCase(),
          'priority': priority,
        }),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = jsonDecode(response.body);
        final attestation = MessageAttestation.fromJson(data['data'] ?? data);
        
        _attestations[attestation.id] = attestation;
        _addToRecent(attestation);
        
        if (!attestation.status.isFinal) {
          _startPolling(attestation.id);
        }
        
        _setLoading(false);
        notifyListeners();
        return attestation;
      } else {
        throw Exception('Failed to create attestation: ${response.statusCode}');
      }
    } catch (e) {
      _setError('创建存证失败: $e');
      _setLoading(false);
      return null;
    }
  }

  Future<MessageAttestation?> getAttestationById(String id) async {
    try {
      _setLoading(true);
      _setError(null);

      final response = await http.get(
        Uri.parse('$_baseUrl/attestations/$id'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final attestation = MessageAttestation.fromJson(data['data'] ?? data);
        
        _attestations[attestation.id] = attestation;
        _setLoading(false);
        notifyListeners();
        return attestation;
      } else if (response.statusCode == 404) {
        _setLoading(false);
        return null;
      } else {
        throw Exception('Failed to get attestation: ${response.statusCode}');
      }
    } catch (e) {
      _setError('获取存证失败: $e');
      _setLoading(false);
      return null;
    }
  }

  Future<MessageAttestation?> getAttestationByMessageId(String messageId) async {
    try {
      _setLoading(true);
      _setError(null);

      final response = await http.get(
        Uri.parse('$_baseUrl/attestations/message/$messageId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final attestation = MessageAttestation.fromJson(data['data'] ?? data);
        
        _attestations[attestation.id] = attestation;
        _setLoading(false);
        notifyListeners();
        return attestation;
      } else if (response.statusCode == 404) {
        _setLoading(false);
        return null;
      } else {
        throw Exception('Failed to get attestation: ${response.statusCode}');
      }
    } catch (e) {
      _setError('获取存证失败: $e');
      _setLoading(false);
      return null;
    }
  }

  Future<List<MessageAttestation>> listAttestations({
    AttestationFilter? filter,
    int page = 0,
    int size = 20,
  }) async {
    try {
      _setLoading(true);
      _setError(null);

      final queryParams = <String, String>{
        'page': page.toString(),
        'size': size.toString(),
      };
      
      if (filter != null) {
        queryParams.addAll(filter.toQueryParams().map((k, v) => MapEntry(k, v.toString())));
      }

      final uri = Uri.parse('$_baseUrl/attestations/list').replace(queryParameters: queryParams);
      
      final response = await http.get(
        uri,
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> items = data['data'] ?? data['items'] ?? [];
        
        final attestations = items.map((item) => MessageAttestation.fromJson(item)).toList();
        
        for (final attestation in attestations) {
          _attestations[attestation.id] = attestation;
          if (!attestation.status.isFinal) {
            _startPolling(attestation.id);
          }
        }
        
        _setLoading(false);
        notifyListeners();
        return attestations;
      } else {
        throw Exception('Failed to list attestations: ${response.statusCode}');
      }
    } catch (e) {
      _setError('获取存证列表失败: $e');
      _setLoading(false);
      return [];
    }
  }

  Future<VerificationResult?> verifyAttestation(String attestationId) async {
    try {
      _setLoading(true);
      _setError(null);

      final response = await http.post(
        Uri.parse('$_baseUrl/attestations/$attestationId/verify'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final result = VerificationResult.fromJson(data['data'] ?? data);
        
        _setLoading(false);
        notifyListeners();
        return result;
      } else {
        throw Exception('Failed to verify attestation: ${response.statusCode}');
      }
    } catch (e) {
      _setError('验证存证失败: $e');
      _setLoading(false);
      return null;
    }
  }

  Future<VerificationResult?> verifyLocal(String messageContent, String storedHash) async {
    try {
      final computedHash = MessageAttestation.calculateHash(messageContent);
      final isValid = computedHash.toLowerCase() == storedHash.toLowerCase();
      
      return VerificationResult(
        isValid: isValid,
        message: isValid ? '验证成功：哈希匹配' : '验证失败：哈希不匹配',
        computedHash: computedHash,
        storedHash: storedHash,
        verificationTime: DateTime.now(),
      );
    } catch (e) {
      return VerificationResult(
        isValid: false,
        message: '本地验证失败: $e',
        verificationTime: DateTime.now(),
      );
    }
  }

  Future<AttestationStatistics?> getStatistics() async {
    try {
      _setLoading(true);
      _setError(null);

      final response = await http.get(
        Uri.parse('$_baseUrl/attestations/statistics'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _statistics = AttestationStatistics.fromJson(data['data'] ?? data);
        
        _setLoading(false);
        notifyListeners();
        return _statistics;
      } else {
        throw Exception('Failed to get statistics: ${response.statusCode}');
      }
    } catch (e) {
      _setError('获取统计信息失败: $e');
      _setLoading(false);
      return null;
    }
  }

  Future<BatchAttestationResult?> createBatchAttestation({
    required List<String> messageIds,
    required BlockchainNetwork network,
    int? priority,
  }) async {
    try {
      _setLoading(true);
      _setError(null);

      final response = await http.post(
        Uri.parse('$_baseUrl/attestations/batch-create'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'messageIds': messageIds,
          'network': network.name.toUpperCase(),
          'priority': priority,
        }),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = jsonDecode(response.body);
        final result = BatchAttestationResult.fromJson(data['data'] ?? data);
        
        _setLoading(false);
        notifyListeners();
        return result;
      } else {
        throw Exception('Failed to create batch attestation: ${response.statusCode}');
      }
    } catch (e) {
      _setError('批量创建存证失败: $e');
      _setLoading(false);
      return null;
    }
  }

  void _startPolling(String attestationId) {
    _stopPolling(attestationId);
    
    final timer = Timer.periodic(const Duration(seconds: 10), (timer) async {
      await _pollAttestationStatus(attestationId);
    });
    
    _pollingTimers[attestationId] = timer;
  }

  void _stopPolling(String attestationId) {
    _pollingTimers[attestationId]?.cancel();
    _pollingTimers.remove(attestationId);
  }

  Future<void> _pollAttestationStatus(String attestationId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/attestations/$attestationId'),
        headers: {'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final attestation = MessageAttestation.fromJson(data['data'] ?? data);
        
        _attestations[attestationId] = attestation;
        
        final index = _recentAttestations.indexWhere((a) => a.id == attestationId);
        if (index >= 0) {
          _recentAttestations[index] = attestation;
        }
        
        _statusController.add(attestation);
        
        if (attestation.status.isFinal) {
          _stopPolling(attestationId);
        }
        
        notifyListeners();
      }
    } catch (e) {
      if (kDebugMode) {
        print('Polling error for $attestationId: $e');
      }
    }
  }

  void _addToRecent(MessageAttestation attestation) {
    _recentAttestations.removeWhere((a) => a.id == attestation.id);
    _recentAttestations.insert(0, attestation);
    
    if (_recentAttestations.length > 50) {
      _recentAttestations.removeLast();
    }
  }

  List<MessageAttestation> getAttestationsByStatus(AttestationStatus status) {
    return _attestations.values.where((a) => a.status == status).toList();
  }

  List<MessageAttestation> getAttestationsByNetwork(BlockchainNetwork network) {
    return _attestations.values.where((a) => a.network == network).toList();
  }

  List<MessageAttestation> searchAttestations(String query) {
    if (query.isEmpty) return [];
    
    final lowerQuery = query.toLowerCase();
    return _attestations.values.where((a) {
      return a.messageId.toLowerCase().contains(lowerQuery) ||
             a.messageHash.toLowerCase().contains(lowerQuery) ||
             a.transactionHash?.toLowerCase().contains(lowerQuery) == true ||
             a.messageContent.toLowerCase().contains(lowerQuery);
    }).toList();
  }

  Future<void> refreshAllPending() async {
    final pending = getAttestationsByStatus(AttestationStatus.pending);
    final submitting = getAttestationsByStatus(AttestationStatus.submitting);
    final confirming = getAttestationsByStatus(AttestationStatus.confirming);
    
    final toRefresh = [...pending, ...submitting, ...confirming];
    
    for (final attestation in toRefresh) {
      await _pollAttestationStatus(attestation.id);
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }

  void clearCache() {
    for (final timer in _pollingTimers.values) {
      timer.cancel();
    }
    _pollingTimers.clear();
    _attestations.clear();
    _recentAttestations.clear();
    _statistics = null;
    notifyListeners();
  }

  @override
  void dispose() {
    for (final timer in _pollingTimers.values) {
      timer.cancel();
    }
    _pollingTimers.clear();
    _statusController.close();
    super.dispose();
  }
}
