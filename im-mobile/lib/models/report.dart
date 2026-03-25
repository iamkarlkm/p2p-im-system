class Report {
  final String reportId;
  final int reporterUserId;
  final String reporterUsername;
  final int reportedMessageId;
  final int reportedUserId;
  final int conversationId;
  final String conversationType;
  final String reportReason;
  final String reportCategory;
  final String description;
  final String status;
  final String reviewerNote;
  final DateTime createdAt;
  final DateTime? reviewedAt;

  Report({
    required this.reportId,
    required this.reporterUserId,
    required this.reporterUsername,
    required this.reportedMessageId,
    required this.reportedUserId,
    required this.conversationId,
    required this.conversationType,
    required this.reportReason,
    required this.reportCategory,
    required this.description,
    required this.status,
    required this.reviewerNote,
    required this.createdAt,
    this.reviewedAt,
  });

  factory Report.fromJson(Map<String, dynamic> json) {
    return Report(
      reportId: json['reportId'] ?? '',
      reporterUserId: json['reporterUserId'] ?? 0,
      reporterUsername: json['reporterUsername'] ?? '',
      reportedMessageId: json['reportedMessageId'] ?? 0,
      reportedUserId: json['reportedUserId'] ?? 0,
      conversationId: json['conversationId'] ?? 0,
      conversationType: json['conversationType'] ?? 'private',
      reportReason: json['reportReason'] ?? '',
      reportCategory: json['reportCategory'] ?? '',
      description: json['description'] ?? '',
      status: json['status'] ?? 'PENDING',
      reviewerNote: json['reviewerNote'] ?? '',
      createdAt: DateTime.tryParse(json['createdAt'] ?? '') ?? DateTime.now(),
      reviewedAt: json['reviewedAt'] != null ? DateTime.tryParse(json['reviewedAt']) : null,
    );
  }
}
