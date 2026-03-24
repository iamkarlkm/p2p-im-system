class CallRecord {
  final int id;
  final String callId;
  final String callerId;
  final String callerName;
  final String calleeId;
  final String calleeName;
  final String conversationId;
  final String callType;
  final String status;
  final DateTime startTime;
  final DateTime? answerTime;
  final DateTime? endTime;
  final int? duration;
  final bool? endedByCaller;

  CallRecord({
    required this.id,
    required this.callId,
    required this.callerId,
    required this.callerName,
    required this.calleeId,
    required this.calleeName,
    required this.conversationId,
    required this.callType,
    required this.status,
    required this.startTime,
    this.answerTime,
    this.endTime,
    this.duration,
    this.endedByCaller,
  });

  factory CallRecord.fromJson(Map<String, dynamic> json) {
    return CallRecord(
      id: json['id'] as int,
      callId: json['callId'] as String,
      callerId: json['callerId'] as String,
      callerName: json['callerName'] as String? ?? '',
      calleeId: json['calleeId'] as String,
      calleeName: json['calleeName'] as String? ?? '',
      conversationId: json['conversationId'] as String,
      callType: json['callType'] as String,
      status: json['status'] as String,
      startTime: DateTime.parse(json['startTime'] as String),
      answerTime: json['answerTime'] != null ? DateTime.tryParse(json['answerTime'] as String) : null,
      endTime: json['endTime'] != null ? DateTime.tryParse(json['endTime'] as String) : null,
      duration: json['duration'] as int?,
      endedByCaller: json['endedByCaller'] as bool?,
    );
  }
}
