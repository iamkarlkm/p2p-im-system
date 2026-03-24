class SignalRequest {
  final String roomId;
  final int userId;
  final String signalType;
  final String? sdp;
  final String? sdpType;
  final String? candidate;
  final int? sdpMLineIndex;
  final String? sdpMid;
  final int? targetUserId;
  final String? callType;
  final String? action;

  SignalRequest({
    required this.roomId,
    required this.userId,
    required this.signalType,
    this.sdp,
    this.sdpType,
    this.candidate,
    this.sdpMLineIndex,
    this.sdpMid,
    this.targetUserId,
    this.callType,
    this.action,
  });

  Map<String, dynamic> toJson() => {
        'roomId': roomId,
        'userId': userId,
        'signalType': signalType,
        if (sdp != null) 'sdp': sdp,
        if (sdpType != null) 'sdpType': sdpType,
        if (candidate != null) 'candidate': candidate,
        if (sdpMLineIndex != null) 'sdpMLineIndex': sdpMLineIndex,
        if (sdpMid != null) 'sdpMid': sdpMid,
        if (targetUserId != null) 'targetUserId': targetUserId,
        if (callType != null) 'callType': callType,
        if (action != null) 'action': action,
      };

  factory SignalRequest.fromJson(Map<String, dynamic> json) => SignalRequest(
        roomId: json['roomId'],
        userId: json['userId'],
        signalType: json['signalType'],
        sdp: json['sdp'],
        sdpType: json['sdpType'],
        candidate: json['candidate'],
        sdpMLineIndex: json['sdpMLineIndex'],
        sdpMid: json['sdpMid'],
        targetUserId: json['targetUserId'],
        callType: json['callType'],
        action: json['action'],
      );
}

class SignalResponse {
  final String roomId;
  final int fromUserId;
  final int toUserId;
  final String signalType;
  final String? sdp;
  final String? sdpType;
  final String? candidate;
  final int? sdpMLineIndex;
  final String? sdpMid;
  final String? callType;
  final String? status;
  final String? message;
  final DateTime timestamp;
  final String? stunServers;
  final String? turnServers;
  final String? turnUsername;
  final String? turnCredential;

  SignalResponse({
    required this.roomId,
    required this.fromUserId,
    required this.toUserId,
    required this.signalType,
    this.sdp,
    this.sdpType,
    this.candidate,
    this.sdpMLineIndex,
    this.sdpMid,
    this.callType,
    this.status,
    this.message,
    required this.timestamp,
    this.stunServers,
    this.turnServers,
    this.turnUsername,
    this.turnCredential,
  });

  factory SignalResponse.fromJson(Map<String, dynamic> json) => SignalResponse(
        roomId: json['roomId'],
        fromUserId: json['fromUserId'],
        toUserId: json['toUserId'],
        signalType: json['signalType'],
        sdp: json['sdp'],
        sdpType: json['sdpType'],
        candidate: json['candidate'],
        sdpMLineIndex: json['sdpMLineIndex'],
        sdpMid: json['sdpMid'],
        callType: json['callType'],
        status: json['status'],
        message: json['message'],
        timestamp: DateTime.parse(json['timestamp']),
        stunServers: json['stunServers'],
        turnServers: json['turnServers'],
        turnUsername: json['turnUsername'],
        turnCredential: json['turnCredential'],
      );
}

enum CallStatus {
  initiating,
  ringing,
  accepted,
  connecting,
  connected,
  rejected,
  busy,
  noAnswer,
  cancelled,
  ended,
}

enum SignalType {
  offer('offer'),
  answer('answer'),
  iceCandidate('ice_candidate'),
  callInvite('call_invite'),
  callAccepted('call_accepted'),
  callRejected('call_rejected'),
  callCancelled('call_cancelled'),
  callEnded('call_ended'),
  ringing('ringing'),
  busy('busy'),
  noAnswer('no_answer');

  final String value;
  const SignalType(this.value);
}

class IceServer {
  final String urls;
  final String? username;
  final String? credential;

  IceServer({required this.urls, this.username, this.credential});

  Map<String, dynamic> toJson() => {
        'urls': urls,
        if (username != null) 'username': username,
        if (credential != null) 'credential': credential,
      };
}
