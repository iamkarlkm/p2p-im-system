# 代码迁移计划

**总计**: 2458 个 Java 文件

## 模块分配

### im-common-dto (364 个文件)

- com/im/backend/common/dto/ApiResponse.java
- com/im/backend/common/dto/PageResult.java
- com/im/backend/dto/AddGroupMemberRequest.java
- com/im/backend/dto/AnnouncementCreateRequest.java
- com/im/backend/dto/AnnouncementDTO.java
- com/im/backend/dto/AnnouncementResponseDTO.java
- com/im/backend/dto/AtMentionRequest.java
- com/im/backend/dto/AtMentionResponse.java
- com/im/backend/dto/BackupRequest.java
- com/im/backend/dto/BackupResponse.java
- com/im/backend/dto/BatchMoveFriendRequest.java
- com/im/backend/dto/BatchOperationRequest.java
- com/im/backend/dto/BatchOperationResponse.java
- com/im/backend/dto/CallRecordDTO.java
- com/im/backend/dto/ContactCardRequest.java
- com/im/backend/dto/ContactCardResponse.java
- com/im/backend/dto/ContactPinnedRequest.java
- com/im/backend/dto/ContextAwareReplyDTO.java
- com/im/backend/dto/CreateQuoteReplyRequest.java
- com/im/backend/dto/DeliveryOrderRequest.java
- ... 还有 344 个文件

### im-common-entity (357 个文件)

- com/im/ai/model/AiMessage.java
- com/im/ai/model/AiResponse.java
- com/im/ai/model/AiSessionContext.java
- com/im/ai/model/ChatRequest.java
- com/im/ai/model/EntryType.java
- com/im/ai/model/IntentResult.java
- com/im/ai/model/IntentType.java
- com/im/ai/model/KnowledgeEntry.java
- com/im/ai/model/MessageRole.java
- com/im/ai/model/ResponseType.java
- com/im/ai/model/SessionState.java
- com/im/ai/model/SlotFillingState.java
- com/im/backend/common/entity/BaseEntity.java
- com/im/backend/entity/AdaptiveContentClassificationConfigEntity.java
- com/im/backend/entity/AiMeetingMinutesEntity.java
- com/im/backend/entity/AnnouncementEntity.java
- com/im/backend/entity/AnnouncementReadRecordEntity.java
- com/im/backend/entity/AtMention.java
- com/im/backend/entity/AtMentionSettings.java
- com/im/backend/entity/BackupRecord.java
- ... 还有 337 个文件

### im-infra-db (40 个文件)

- com/im/backend/common/Result.java
- com/im/backend/common/exception/BusinessException.java
- com/im/backend/common/exception/GlobalExceptionHandler.java
- com/im/backend/common/repository/BaseRepository.java
- com/im/backend/common/service/BaseService.java
- com/im/backend/common/util/GeoHashUtil.java
- com/im/backend/common/util/GeoUtils.java
- com/im/backend/common/util/OrderNoGenerator.java
- com/im/backend/config/ApolloConfigListener.java
- com/im/backend/config/DataSourceConfig.java
- com/im/backend/config/FileStorageConfig.java
- com/im/backend/config/GrayReleaseConfig.java
- com/im/backend/config/GrayReleaseRuleManager.java
- com/im/backend/config/KafkaAdminConfig.java
- com/im/backend/config/KafkaConfig.java
- com/im/backend/config/KafkaConsumerConfig.java
- com/im/backend/config/KafkaProducerConfig.java
- com/im/backend/config/MinIOConfig.java
- com/im/backend/config/PrometheusConfig.java
- com/im/backend/config/RabbitMQConfig.java
- ... 还有 20 个文件

### im-infra-security (75 个文件)

- com/im/ai/AnomalyDetectionService.java
- com/im/ai/anomaly/AIAnomalyDetectionService.java
- com/im/ai/anomaly/MultidimensionalAnomalyService.java
- com/im/ai/behavior/UserBehaviorAnalysisService.java
- com/im/ai/capacity/CapacityPlanningService.java
- com/im/ai/controller/AiAssistantController.java
- com/im/ai/funnel/ConversionFunnelService.java
- com/im/ai/recovery/AutomatedRecoveryService.java
- com/im/ai/service/AiAssistantService.java
- com/im/ai/service/IntentClassifier.java
- com/im/ai/service/KnowledgeBaseService.java
- com/im/backend/modules/ai/mapper/AIAnomalyDetectionConfigMapper.java
- com/im/backend/modules/ai/mapper/AIAnomalyDetectionRecordMapper.java
- com/im/backend/security/differentialprivacy/controller/DifferentialPrivacyConfigController.java
- com/im/backend/security/differentialprivacy/controller/PrivacyBudgetController.java
- com/im/backend/security/differentialprivacy/controller/PrivacyImpactController.java
- com/im/backend/security/differentialprivacy/repository/DifferentialPrivacyConfigRepository.java
- com/im/backend/security/differentialprivacy/repository/PrivacyBudgetRepository.java
- com/im/backend/security/differentialprivacy/repository/PrivacyImpactRepository.java
- com/im/backend/security/differentialprivacy/service/DifferentialPrivacyConfigService.java
- ... 还有 55 个文件

### im-service-admin (836 个文件)

- com/im/algorithm/GroupRelationScorer.java
- com/im/algorithm/InterestTagScorer.java
- com/im/algorithm/MutualFriendScorer.java
- com/im/algorithm/RecommendationEngine.java
- com/im/anomaly/AnomalyAlert.java
- com/im/anomaly/AnomalyDetectionEngine.java
- com/im/backend/ImBackendApplication.java
- com/im/backend/consumer/KafkaMessageConsumer.java
- com/im/backend/controller/AdaptiveContentClassificationController.java
- com/im/backend/controller/APMController.java
- com/im/backend/controller/AtMentionController.java
- com/im/backend/controller/BackupController.java
- com/im/backend/controller/BatchOperationStats.java
- com/im/backend/controller/BlockchainVerificationController.java
- com/im/backend/controller/CacheHealthIndicator.java
- com/im/backend/controller/CollaborationAIController.java
- com/im/backend/controller/ContactCardController.java
- com/im/backend/controller/ContactPinnedController.java
- com/im/backend/controller/ContentFilterController.java
- com/im/backend/controller/ContextAwareReplyController.java
- ... 还有 816 个文件

### im-service-group (47 个文件)

- com/im/backend/modules/group/GroupRole.java
- com/im/backend/modules/group/GroupStatus.java
- com/im/backend/modules/group/JoinGroupRequest.java
- com/im/backend/modules/group/UpdateGroupRequest.java
- com/im/backend/modules/group/controller/CheckinController.java
- com/im/backend/modules/group/controller/ConsumerGroupController.java
- com/im/backend/modules/group/controller/CustomerServiceController.java
- com/im/backend/modules/group/controller/DeadLetterController.java
- com/im/backend/modules/group/controller/GroupAnnouncementController.java
- com/im/backend/modules/group/controller/NavigationController.java
- com/im/backend/modules/group/controller/SmartDispatchController.java
- com/im/backend/modules/group/dto/CheckinRecordDTO.java
- com/im/backend/modules/group/dto/CreateActivityRequest.java
- com/im/backend/modules/group/dto/GroupMemberResponse.java
- com/im/backend/modules/group/dto/GroupResponse.java
- com/im/backend/modules/group/dto/PointTransactionDTO.java
- com/im/backend/modules/group/dto/SemanticSearchRequest.java
- com/im/backend/modules/group/dto/SemanticSearchResponse.java
- com/im/backend/modules/group/dto/TicketResponse.java
- com/im/backend/modules/group/entity/ConsumerGroup.java
- ... 还有 27 个文件

### im-service-local (335 个文件)

- com/im/backend/modules/local/appstore/controller/AppstoreController.java
- com/im/backend/modules/local/appstore/entity/MiniappStoreItem.java
- com/im/backend/modules/local/appstore/repository/MiniappStoreItemMapper.java
- com/im/backend/modules/local/checkin/repository/PoiCheckinRecordMapper.java
- com/im/backend/modules/local/config/IntelligentAssistantProperties.java
- com/im/backend/modules/local/controller/CouponController.java
- com/im/backend/modules/local/controller/DispatchController.java
- com/im/backend/modules/local/controller/IntelligentAssistantController.java
- com/im/backend/modules/local/controller/LocationSharingController.java
- com/im/backend/modules/local/controller/MapStreamController.java
- com/im/backend/modules/local/controller/MerchantAnalyticsController.java
- com/im/backend/modules/local/controller/MerchantBiController.java
- com/im/backend/modules/local/controller/MerchantOperationController.java
- com/im/backend/modules/local/controller/MerchantReputationController.java
- com/im/backend/modules/local/controller/MerchantReviewController.java
- com/im/backend/modules/local/controller/MiniProgramController.java
- com/im/backend/modules/local/controller/OrderDeliveryTrackingController.java
- com/im/backend/modules/local/controller/PersonalizedRecommendationController.java
- com/im/backend/modules/local/customer/service/repository/CustomerServiceTicketMapper.java
- com/im/backend/modules/local/customer/service/service/ICustomerService.java
- ... 还有 315 个文件

### im-service-message (182 个文件)

- com/im/backend/modules/message/MessageArchiveJob.java
- com/im/backend/modules/message/MessageCleanupJob.java
- com/im/backend/modules/message/MessageQuery.java
- com/im/backend/modules/message/MessageStoreService.java
- com/im/backend/modules/message/controller/AIAnomalyDetectionController.java
- com/im/backend/modules/message/controller/AnnouncementController.java
- com/im/backend/modules/message/controller/CallRecordController.java
- com/im/backend/modules/message/controller/ChatbotController.java
- com/im/backend/modules/message/controller/CollabQualityController.java
- com/im/backend/modules/message/controller/DelayMessageController.java
- com/im/backend/modules/message/controller/DeliveryController.java
- com/im/backend/modules/message/controller/FileController.java
- com/im/backend/modules/message/controller/LoadBalanceController.java
- com/im/backend/modules/message/controller/MessageDraftController.java
- com/im/backend/modules/message/controller/MessageEditController.java
- com/im/backend/modules/message/controller/MessageExpirationController.java
- com/im/backend/modules/message/controller/MessageForwardController.java
- com/im/backend/modules/message/controller/MessageReactionController.java
- com/im/backend/modules/message/controller/MessageSearchController.java
- com/im/backend/modules/message/controller/MessageTraceController.java
- ... 还有 162 个文件

### im-service-push (39 个文件)

- com/im/ai/alert/AlertIntelligenceService.java
- com/im/alert/AlertManager.java
- com/im/alert/AlertManagerService.java
- com/im/alert/AlertRuleEngine.java
- com/im/backend/modules/alert/AlertCorrelationService.java
- com/im/backend/modules/alert/AlertNoiseReductionController.java
- com/im/backend/modules/alert/IntelligentAlertSuppressor.java
- com/im/backend/modules/alert/controller/SmartAlertController.java
- com/im/backend/modules/alert/entity/SmartAlert.java
- com/im/backend/modules/alert/repository/SmartAlertMapper.java
- com/im/backend/modules/alert/service/ISmartAlertService.java
- com/im/backend/modules/alert/service/impl/SmartAlertServiceImpl.java
- com/im/backend/modules/push/PushNotification.java
- com/im/backend/modules/push/PushResult.java
- com/im/backend/modules/push/PushToken.java
- com/im/backend/modules/push/PushTokenRepository.java
- com/im/backend/modules/push/PushType.java
- com/im/backend/modules/push/dto/RegisterTokenRequest.java
- com/im/backend/modules/push/dto/SendPushRequest.java
- com/im/backend/modules/push/mapper/NotificationMapper.java
- ... 还有 19 个文件

### im-service-storage (5 个文件)

- com/im/web3/storage/DecentralizedStorageController.java
- com/im/web3/storage/DecentralizedStorageService.java
- com/im/web3/storage/IPFSClient.java
- com/im/web3/storage/StorageNode.java
- com/im/web3/storage/StorageNodeManager.java

### im-service-user (146 个文件)

- com/im/auth/AuthenticationService.java
- com/im/auth/controller/BiometricAuthController.java
- com/im/auth/dto/BiometricRegistrationRequest.java
- com/im/auth/dto/ChangePasswordRequest.java
- com/im/auth/dto/RefreshTokenRequest.java
- com/im/auth/dto/TokenResponse.java
- com/im/auth/dto/TokenValidationResponse.java
- com/im/auth/dto/UserInfoResponse.java
- com/im/auth/entity/BiometricAuthEntity.java
- com/im/auth/repository/BiometricAuthRepository.java
- com/im/auth/service/BiometricAuthService.java
- com/im/auth/util/PasswordEncoder.java
- com/im/backend/modules/auth/controller/AppointmentController.java
- com/im/backend/modules/auth/controller/SemanticSearchController.java
- com/im/backend/modules/auth/controller/UserGrowthController.java
- com/im/backend/modules/auth/controller/UserProfileController.java
- com/im/backend/modules/auth/controller/UserStatusController.java
- com/im/backend/modules/auth/controller/WebRTCSignalingController.java
- com/im/backend/modules/auth/dto/ApiResponse.java
- com/im/backend/modules/auth/dto/AuthResponse.java
- ... 还有 126 个文件

### im-service-websocket (32 个文件)

- com/im/backend/modules/websocket/controller/CreateGeofenceRequest.java
- com/im/backend/modules/websocket/controller/GeofenceGroupScenario.java
- com/im/backend/modules/websocket/controller/GeofenceTriggerController.java
- com/im/backend/modules/websocket/controller/GeofenceTriggerRule.java
- com/im/backend/modules/websocket/controller/UserGeofenceState.java
- com/im/backend/modules/websocket/controller/WebSocketController.java
- com/im/backend/modules/websocket/entity/BotWebSocketHandler.java
- com/im/backend/modules/websocket/entity/RiskScore.java
- com/im/backend/modules/websocket/entity/WebhookConfig.java
- com/im/backend/modules/websocket/entity/WebhookEvent.java
- com/im/backend/modules/websocket/entity/WebSocketConfig.java
- com/im/backend/modules/websocket/entity/WebSocketConnectionManager.java
- com/im/backend/modules/websocket/entity/WebSocketServer.java
- com/im/backend/modules/websocket/repository/BotRepository.java
- com/im/backend/modules/websocket/repository/DeviceRepository.java
- com/im/backend/modules/websocket/service/CollaborationWebSocketService.java
- com/im/backend/modules/websocket/service/MultimodalAIAssistantService.java
- com/im/backend/modules/websocket/service/WebSocketSessionService.java
- com/im/backend/websocket/TranslationWebSocketHandler.java
- com/im/backend/websocket/loadbalance/ConsistentHashRouter.java
- ... 还有 12 个文件

