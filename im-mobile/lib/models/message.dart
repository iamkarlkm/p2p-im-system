class Message {
  final String from;
  final String to;
  final String content;
  final int timestamp;
  final String msgId;
  final String? type;
  final int status;

  Message({
    required this.from,
    required this.to,
    required this.content,
    required this.timestamp,
    required this.msgId,
    this.type = 'chat',
    this.status = 0,
  });

  factory Message.fromJson(Map<String, dynamic> json) {
    return Message(
      from: json['from'] ?? '',
      to: json['to'] ?? '',
      content: json['content'] ?? '',
      timestamp: json['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      msgId: json['msgId'] ?? '',
      type: json['type'],
      status: json['status'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'from': from,
      'to': to,
      'content': content,
      'timestamp': timestamp,
      'msgId': msgId,
      'type': type,
      'status': status,
    };
  }

  Message copyWith({
    String? from,
    String? to,
    String? content,
    int? timestamp,
    String? msgId,
    String? type,
    int? status,
  }) {
    return Message(
      from: from ?? this.from,
      to: to ?? this.to,
      content: content ?? this.content,
      timestamp: timestamp ?? this.timestamp,
      msgId: msgId ?? this.msgId,
      type: type ?? this.type,
      status: status ?? this.status,
    );
  }
}
