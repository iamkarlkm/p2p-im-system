import 'dart:convert';
import 'dart:typed_data';
import 'package:crypto/crypto.dart';

enum AttestationStatus {
  pending,
  submitting,
  confirming,
  confirmed,
  failed,
}

enum BlockchainNetwork {
  ethereum,
  sepolia,
  polygon,
  bsc,
  arbitrum,
  optimism,
}

extension AttestationStatusExtension on AttestationStatus {
  String get name {
    switch (this) {
      case AttestationStatus.pending:
        return '待提交';
      case AttestationStatus.submitting:
        return '提交中';
      case AttestationStatus.confirming:
        return '确认中';
      case AttestationStatus.confirmed:
        return '已确认';
      case AttestationStatus.failed:
        return '失败';
    }
  }

  String get color {
    switch (this) {
      case AttestationStatus.pending:
        return '#9E9E9E';
      case AttestationStatus.submitting:
        return '#2196F3';
      case AttestationStatus.confirming:
        return '#FF9800';
      case AttestationStatus.confirmed:
        return '#4CAF50';
      case AttestationStatus.failed:
        return '#F44336';
    }
  }

  bool get isFinal => this == AttestationStatus.confirmed || this == AttestationStatus.failed;
}

extension BlockchainNetworkExtension on BlockchainNetwork {
  String get name {
    switch (this) {
      case BlockchainNetwork.ethereum:
        return 'Ethereum';
      case BlockchainNetwork.sepolia:
        return 'Sepolia Testnet';
      case BlockchainNetwork.polygon:
        return 'Polygon';
      case BlockchainNetwork.bsc:
        return 'BSC';
      case BlockchainNetwork.arbitrum:
        return 'Arbitrum';
      case BlockchainNetwork.optimism:
        return 'Optimism';
    }
  }

  String get chainId {
    switch (this) {
      case BlockchainNetwork.ethereum:
        return '1';
      case BlockchainNetwork.sepolia:
        return '11155111';
      case BlockchainNetwork.polygon:
        return '137';
      case BlockchainNetwork.bsc:
        return '56';
      case BlockchainNetwork.arbitrum:
        return '42161';
      case BlockchainNetwork.optimism:
        return '10';
    }
  }

  String get explorerUrl {
    switch (this) {
      case BlockchainNetwork.ethereum:
        return 'https://etherscan.io';
      case BlockchainNetwork.sepolia:
        return 'https://sepolia.etherscan.io';
      case BlockchainNetwork.polygon:
        return 'https://polygonscan.com';
      case BlockchainNetwork.bsc:
        return 'https://bscscan.com';
      case BlockchainNetwork.arbitrum:
        return 'https://arbiscan.io';
      case BlockchainNetwork.optimism:
        return 'https://optimistic.etherscan.io';
    }
  }

  String get rpcUrl {
    switch (this) {
      case BlockchainNetwork.ethereum:
        return 'https://eth.llamarpc.com';
      case BlockchainNetwork.sepolia:
        return 'https://rpc.sepolia.org';
      case BlockchainNetwork.polygon:
        return 'https://polygon.llamarpc.com';
      case BlockchainNetwork.bsc:
        return 'https://bsc-dataseed.binance.org';
      case BlockchainNetwork.arbitrum:
        return 'https://arb1.arbitrum.io/rpc';
      case BlockchainNetwork.optimism:
        return 'https://mainnet.optimism.io';
    }
  }
}

class MessageAttestation {
  final String id;
  final String messageId;
  final String messageContent;
  final String messageHash;
  final String? transactionHash;
  final String? blockHash;
  final int? blockNumber;
  final AttestationStatus status;
  final BlockchainNetwork network;
  final DateTime createdAt;
  final DateTime? confirmedAt;
  final String? merkleRoot;
  final String? merkleProof;
  final int? gasUsed;
  final String? gasPrice;
  final int confirmCount;
  final int retryCount;
  final String? failureReason;
  final Map<String, dynamic>? metadata;

  MessageAttestation({
    required this.id,
    required this.messageId,
    required this.messageContent,
    required this.messageHash,
    this.transactionHash,
    this.blockHash,
    this.blockNumber,
    required this.status,
    required this.network,
    required this.createdAt,
    this.confirmedAt,
    this.merkleRoot,
    this.merkleProof,
    this.gasUsed,
    this.gasPrice,
    this.confirmCount = 0,
    this.retryCount = 0,
    this.failureReason,
    this.metadata,
  });

  factory MessageAttestation.fromJson(Map<String, dynamic> json) {
    return MessageAttestation(
      id: json['id'] ?? '',
      messageId: json['messageId'] ?? '',
      messageContent: json['messageContent'] ?? '',
      messageHash: json['messageHash'] ?? '',
      transactionHash: json['transactionHash'],
      blockHash: json['blockHash'],
      blockNumber: json['blockNumber'],
      status: AttestationStatus.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['status'] ?? '').toUpperCase(),
        orElse: () => AttestationStatus.pending,
      ),
      network: BlockchainNetwork.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['network'] ?? '').toUpperCase(),
        orElse: () => BlockchainNetwork.ethereum,
      ),
      createdAt: DateTime.parse(json['createdAt'] ?? DateTime.now().toIso8601String()),
      confirmedAt: json['confirmedAt'] != null ? DateTime.parse(json['confirmedAt']) : null,
      merkleRoot: json['merkleRoot'],
      merkleProof: json['merkleProof'],
      gasUsed: json['gasUsed'],
      gasPrice: json['gasPrice'],
      confirmCount: json['confirmCount'] ?? 0,
      retryCount: json['retryCount'] ?? 0,
      failureReason: json['failureReason'],
      metadata: json['metadata'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'messageContent': messageContent,
      'messageHash': messageHash,
      'transactionHash': transactionHash,
      'blockHash': blockHash,
      'blockNumber': blockNumber,
      'status': status.name,
      'network': network.name,
      'createdAt': createdAt.toIso8601String(),
      'confirmedAt': confirmedAt?.toIso8601String(),
      'merkleRoot': merkleRoot,
      'merkleProof': merkleProof,
      'gasUsed': gasUsed,
      'gasPrice': gasPrice,
      'confirmCount': confirmCount,
      'retryCount': retryCount,
      'failureReason': failureReason,
      'metadata': metadata,
    };
  }

  String get explorerTransactionUrl {
    if (transactionHash == null) return '';
    return '${network.explorerUrl}/tx/$transactionHash';
  }

  String get explorerBlockUrl {
    if (blockNumber == null) return '';
    return '${network.explorerUrl}/block/$blockNumber';
  }

  Duration? get confirmationDuration {
    if (confirmedAt == null) return null;
    return confirmedAt!.difference(createdAt);
  }

  bool get isConfirmed => status == AttestationStatus.confirmed;

  static String calculateHash(String content) {
    final bytes = utf8.encode(content);
    final digest = sha256.convert(bytes);
    return '0x${digest.toString()}';
  }

  MessageAttestation copyWith({
    String? id,
    String? messageId,
    String? messageContent,
    String? messageHash,
    String? transactionHash,
    String? blockHash,
    int? blockNumber,
    AttestationStatus? status,
    BlockchainNetwork? network,
    DateTime? createdAt,
    DateTime? confirmedAt,
    String? merkleRoot,
    String? merkleProof,
    int? gasUsed,
    String? gasPrice,
    int? confirmCount,
    int? retryCount,
    String? failureReason,
    Map<String, dynamic>? metadata,
  }) {
    return MessageAttestation(
      id: id ?? this.id,
      messageId: messageId ?? this.messageId,
      messageContent: messageContent ?? this.messageContent,
      messageHash: messageHash ?? this.messageHash,
      transactionHash: transactionHash ?? this.transactionHash,
      blockHash: blockHash ?? this.blockHash,
      blockNumber: blockNumber ?? this.blockNumber,
      status: status ?? this.status,
      network: network ?? this.network,
      createdAt: createdAt ?? this.createdAt,
      confirmedAt: confirmedAt ?? this.confirmedAt,
      merkleRoot: merkleRoot ?? this.merkleRoot,
      merkleProof: merkleProof ?? this.merkleProof,
      gasUsed: gasUsed ?? this.gasUsed,
      gasPrice: gasPrice ?? this.gasPrice,
      confirmCount: confirmCount ?? this.confirmCount,
      retryCount: retryCount ?? this.retryCount,
      failureReason: failureReason ?? this.failureReason,
      metadata: metadata ?? this.metadata,
    );
  }
}

class AttestationFilter {
  final AttestationStatus? status;
  final BlockchainNetwork? network;
  final DateTime? startDate;
  final DateTime? endDate;
  final String? searchQuery;

  AttestationFilter({
    this.status,
    this.network,
    this.startDate,
    this.endDate,
    this.searchQuery,
  });

  Map<String, dynamic> toQueryParams() {
    final params = <String, dynamic>{};
    if (status != null) params['status'] = status!.name.toUpperCase();
    if (network != null) params['network'] = network!.name.toUpperCase();
    if (startDate != null) params['startDate'] = startDate!.toIso8601String();
    if (endDate != null) params['endDate'] = endDate!.toIso8601String();
    if (searchQuery != null && searchQuery!.isNotEmpty) params['query'] = searchQuery;
    return params;
  }
}

class AttestationStatistics {
  final int totalCount;
  final int confirmedCount;
  final int pendingCount;
  final int failedCount;
  final Map<BlockchainNetwork, int> networkDistribution;
  final double averageConfirmationTime;

  AttestationStatistics({
    required this.totalCount,
    required this.confirmedCount,
    required this.pendingCount,
    required this.failedCount,
    required this.networkDistribution,
    required this.averageConfirmationTime,
  });

  factory AttestationStatistics.fromJson(Map<String, dynamic> json) {
    final networkDist = <BlockchainNetwork, int>{};
    if (json['networkDistribution'] != null) {
      (json['networkDistribution'] as Map).forEach((key, value) {
        final network = BlockchainNetwork.values.firstWhere(
          (e) => e.name.toUpperCase() == key.toString().toUpperCase(),
          orElse: () => BlockchainNetwork.ethereum,
        );
        networkDist[network] = value as int;
      });
    }

    return AttestationStatistics(
      totalCount: json['totalCount'] ?? 0,
      confirmedCount: json['confirmedCount'] ?? 0,
      pendingCount: json['pendingCount'] ?? 0,
      failedCount: json['failedCount'] ?? 0,
      networkDistribution: networkDist,
      averageConfirmationTime: (json['averageConfirmationTime'] ?? 0).toDouble(),
    );
  }

  double get confirmationRate => totalCount > 0 ? confirmedCount / totalCount * 100 : 0;

  double get failureRate => totalCount > 0 ? failedCount / totalCount * 100 : 0;
}

class VerificationResult {
  final bool isValid;
  final String message;
  final String? computedHash;
  final String? storedHash;
  final DateTime? verificationTime;
  final Map<String, dynamic>? details;

  VerificationResult({
    required this.isValid,
    required this.message,
    this.computedHash,
    this.storedHash,
    this.verificationTime,
    this.details,
  });

  factory VerificationResult.fromJson(Map<String, dynamic> json) {
    return VerificationResult(
      isValid: json['valid'] ?? false,
      message: json['message'] ?? '',
      computedHash: json['computedHash'],
      storedHash: json['storedHash'],
      verificationTime: json['verificationTime'] != null 
          ? DateTime.parse(json['verificationTime']) 
          : null,
      details: json['details'],
    );
  }
}

class BatchAttestationRequest {
  final List<String> messageIds;
  final BlockchainNetwork network;
  final int? priority;

  BatchAttestationRequest({
    required this.messageIds,
    required this.network,
    this.priority,
  });

  Map<String, dynamic> toJson() {
    return {
      'messageIds': messageIds,
      'network': network.name.toUpperCase(),
      'priority': priority,
    };
  }
}

class BatchAttestationResult {
  final int totalRequested;
  final int successCount;
  final int failedCount;
  final List<String> successIds;
  final List<String> failedIds;

  BatchAttestationResult({
    required this.totalRequested,
    required this.successCount,
    required this.failedCount,
    required this.successIds,
    required this.failedIds,
  });

  factory BatchAttestationResult.fromJson(Map<String, dynamic> json) {
    return BatchAttestationResult(
      totalRequested: json['totalRequested'] ?? 0,
      successCount: json['successCount'] ?? 0,
      failedCount: json['failedCount'] ?? 0,
      successIds: List<String>.from(json['successIds'] ?? []),
      failedIds: List<String>.from(json['failedIds'] ?? []),
    );
  }
}
