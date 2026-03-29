package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 零信任策略实体
 * 定义访问控制策略和规则
 */
@Entity
@Table(name = "zero_trust_policies", indexes = {
    @Index(name = "idx_policy_name", columnList = "name"),
    @Index(name = "idx_policy_priority", columnList = "priority"),
    @Index(name = "idx_policy_enabled", columnList = "enabled"),
    @Index(name = "idx_policy_resource", columnList = "resourceType,resourceId")
})
public class ZeroTrustPolicyEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer priority;  // 优先级，数字越小优先级越高

    @Column(nullable = false)
    private Boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyEffect effect;  // ALLOW or DENY

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;

    private String resourceId;  // 特定资源ID，null表示通配

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyCondition> conditions = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyAction> actions = new ArrayList<>();

    @Column(name = "require_mfa")
    private Boolean requireMFA = false;

    @Column(name = "require_approval")
    private Boolean requireApproval = false;

    @Column(name = "session_timeout")
    private Integer sessionTimeoutMinutes = 30;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "version")
    private Integer version = 1;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        version++;
    }

    // 检查策略是否匹配资源
    public boolean matchesResource(ResourceType type, String resourceId) {
        if (this.resourceType != type) {
            return false;
        }
        if (this.resourceId != null && !this.resourceId.equals(resourceId)) {
            return false;
        }
        return true;
    }

    // 添加条件
    public void addCondition(PolicyCondition condition) {
        conditions.add(condition);
        condition.setPolicy(this);
    }

    // 添加动作
    public void addAction(PolicyAction action) {
        actions.add(action);
        action.setPolicy(this);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public PolicyEffect getEffect() { return effect; }
    public void setEffect(PolicyEffect effect) { this.effect = effect; }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public List<PolicyCondition> getConditions() { return conditions; }
    public void setConditions(List<PolicyCondition> conditions) { this.conditions = conditions; }

    public List<PolicyAction> getActions() { return actions; }
    public void setActions(List<PolicyAction> actions) { this.actions = actions; }

    public Boolean getRequireMFA() { return requireMFA; }
    public void setRequireMFA(Boolean requireMFA) { this.requireMFA = requireMFA; }

    public Boolean getRequireApproval() { return requireApproval; }
    public void setRequireApproval(Boolean requireApproval) { this.requireApproval = requireApproval; }

    public Integer getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
    public void setSessionTimeoutMinutes(Integer sessionTimeoutMinutes) { this.sessionTimeoutMinutes = sessionTimeoutMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    // 策略效果枚举
    public enum PolicyEffect {
        ALLOW,    // 允许
        DENY      // 拒绝
    }

    // 资源类型枚举
    public enum ResourceType {
        API_ENDPOINT,
        MESSAGE,
        FILE,
        CONTACT,
        GROUP,
        CHANNEL,
        ADMIN_PANEL,
        CONFIGURATION,
        DATABASE,
        SERVICE,
        ALL
    }

    // 策略条件实体
    @Entity
    @Table(name = "policy_conditions")
    public static class PolicyCondition {
        @Id
        private String id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "policy_id")
        private ZeroTrustPolicyEntity policy;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ConditionType type;

        @Column(nullable = false)
        private String attribute;  // 属性名

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Operator operator;  // 操作符

        @Column(nullable = false, length = 500)
        private String value;  // 比较值

        @PrePersist
        public void prePersist() {
            if (id == null) {
                id = UUID.randomUUID().toString();
            }
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public ZeroTrustPolicyEntity getPolicy() { return policy; }
        public void setPolicy(ZeroTrustPolicyEntity policy) { this.policy = policy; }

        public ConditionType getType() { return type; }
        public void setType(ConditionType type) { this.type = type; }

        public String getAttribute() { return attribute; }
        public void setAttribute(String attribute) { this.attribute = attribute; }

        public Operator getOperator() { return operator; }
        public void setOperator(Operator operator) { this.operator = operator; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        // 条件类型枚举
        public enum ConditionType {
            USER,           // 用户属性
            DEVICE,         // 设备属性
            NETWORK,        // 网络属性
            LOCATION,       // 位置属性
            TIME,           // 时间属性
            BEHAVIOR,       // 行为属性
            CONTEXT         // 上下文属性
        }

        // 操作符枚举
        public enum Operator {
            EQUALS,         // 等于
            NOT_EQUALS,     // 不等于
            GREATER_THAN,   // 大于
            LESS_THAN,      // 小于
            CONTAINS,       // 包含
            NOT_CONTAINS,   // 不包含
            IN,             // 在列表中
            NOT_IN,         // 不在列表中
            REGEX           // 正则匹配
        }
    }

    // 策略动作实体
    @Entity
    @Table(name = "policy_actions")
    public static class PolicyAction {
        @Id
        private String id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "policy_id")
        private ZeroTrustPolicyEntity policy;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ActionType type;

        @Column(length = 1000)
        private String parameters;  // JSON格式的参数

        @PrePersist
        public void prePersist() {
            if (id == null) {
                id = UUID.randomUUID().toString();
            }
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public ZeroTrustPolicyEntity getPolicy() { return policy; }
        public void setPolicy(ZeroTrustPolicyEntity policy) { this.policy = policy; }

        public ActionType getType() { return type; }
        public void setType(ActionType type) { this.type = type; }

        public String getParameters() { return parameters; }
        public void setParameters(String parameters) { this.parameters = parameters; }

        // 动作类型枚举
        public enum ActionType {
            ALLOW_ACCESS,       // 允许访问
            DENY_ACCESS,        // 拒绝访问
            REQUIRE_MFA,        // 要求MFA
            REQUIRE_APPROVAL,   // 要求审批
            LOG_ACCESS,         // 记录访问
            ALERT_ADMIN,        // 告警管理员
            QUARANTINE,         // 隔离
            RATE_LIMIT,         // 限流
            REDIRECT            // 重定向
        }
    }
}
