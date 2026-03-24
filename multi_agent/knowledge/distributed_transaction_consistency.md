# 分布式事务一致性：2PC / TCC / Saga 在 IM 系统中的应用

## 概述

分布式事务是指事务的参与者、支持事务的服务器、资源服务器以及事务管理器分别位于分布式系统的不同节点之上。在即时通讯（IM）系统中，由于采用微服务架构（用户服务、消息服务、好友服务、群组服务等各自独立），一条业务操作往往需要跨多个服务节点和多个数据库（MySQL、Redis、MongoDB），本地 ACID 事务无法覆盖跨服务的操作，分布式事务成为保障数据一致性的核心机制。

## 为什么 IM 系统需要分布式事务

IM 系统的典型跨服务操作场景包括：

1. **发送消息**：消息写入消息库 + 更新会话列表 + 更新未读计数 + 触发推送 → 至少 3 个服务
2. **添加好友**：好友关系写入 → 双向好友请求记录 → 通知服务 → 至少 2 个服务
3. **创建群组**：群组信息写入 → 群成员表写入（批量 N 条）→ 通知所有成员 → 至少 2 个服务
4. **用户注册**：用户表写入 → 初始化会话表 → 初始化未读计数 → 至少 2 个服务
5. **发送阅后即焚消息**：消息写入 → 焚毁计时器注册 → 到达时间触发删除 → 至少 2 个服务

这些场景如果不做事务保障，会导致数据不一致（如消息发出但未读计数未更新、群创建成功但部分成员未收到通知等）。

## 分布式事务理论基础

### CAP 定理

分布式系统最多只能同时满足以下三个特性中的两个：
- **C（Consistency）**：一致性，系统中所有节点的数据在同一时刻一致
- **A（Availability）**：可用性，每个请求都能在有限时间内获得响应
- **P（Partition tolerance）**：分区容错，网络分区时系统仍能运行

在 IM 系统中，通常选择 **CP**（一致性 + 分区容错），因为消息的可靠性和顺序性比短暂的可用性波动更重要。但对于非核心功能（如消息已读状态），可以适当牺牲强一致性换取高可用性。

### BASE 理论

分布式事务的核心理论：
- **Basically Available**：基本可用，允许系统出现故障时降级
- **Soft state**：软状态，数据状态可以在一段时间内不一致
- **Eventually consistent**：最终一致性，系统在一段时间后达到一致状态

IM 系统中，消息投递、好友关系等核心数据要求最终一致性，而非实时强一致。

## 1. 两阶段提交（2PC）

### 原理

2PC（Two-Phase Commit）是最经典的分布式事务协议，通过**协调者（Coordinator）**统一管理所有参与者的提交/回滚。

```
阶段一：Prepare（准备阶段）
┌──────────────┐
│  协调者       │
│  (Coordinator)│
└──────┬───────┘
       │ 向所有参与者发送 Prepare 请求
       ▼
  ┌─────────┐  ┌─────────┐  ┌─────────┐
  │参与者 A  │  │参与者 B  │  │参与者 C  │
  │准备成功  │  │准备成功  │  │准备成功  │
  │锁定资源  │  │锁定资源  │  │锁定资源  │
  └────┬────┘  └────┬────┘  └────┬────┘
       │            │            │
       │ 投票 YES   │ 投票 YES   │ 投票 YES
       ▼            ▼            ▼
       └────────────┴────────────┘
                     │
                     ▼
阶段二：Commit（提交阶段）
       ┌─────────────────────┐
       │  所有参与者投票 YES   │
       │  协调者发送 Commit    │
       └──────────┬──────────┘
                  │ 发送 Commit/Rollback
                  ▼
       ┌──────────────────────────┐
       │  所有参与者提交/回滚        │
       │  释放锁资源               │
       └──────────────────────────┘
```

### 事务流程

1. **Prepare 阶段**：
   - 协调者向所有参与者发送 Prepare 请求
   - 参与者执行本地事务（但不提交），锁定资源
   - 参与者返回 Vote-Commit 或 Vote-Abort

2. **Commit 阶段**：
   - 如果所有参与者返回 Vote-Commit：协调者发送 Commit，参与者提交事务
   - 如果任何一个参与者返回 Vote-Abort：协调者发送 Rollback，所有参与者回滚

### IM 系统的 2PC 场景

**用户注册流程（2PC 示例）**：
```
用户服务（User）     会话服务（Session）    通知服务（Notify）
     │                    │                  │
     │ ──── Prepare ────▶ │                  │
     │ ◀──── YES ─────── │                  │
     │ ──────────────────▶ ──── Prepare ──▶ │
     │ ◀────────────────────── YES ──────── │
     │ ────────────────────────────────────▶ │ ──── Prepare ──▶ │
     │ ◀────────────────────────────────────── YES ─────────── │
     │                                         │
     │  所有 YES ──── Commit ──▶ │ ──── Commit ──▶ │
     │ ◀── OK ─────── │ ◀─── OK ──────────── │
     │ ◀──────────────────────────────────────── OK ───────── │
```

### 2PC 的问题

| 问题 | 说明 | 影响 |
|------|------|------|
| **同步阻塞** | Prepare 阶段参与者锁定资源直至 Commit/Rollback | 性能瓶颈，长事务锁定 |
| **单点故障** | 协调者崩溃后参与者无限等待 | 需要额外的故障恢复机制 |
| **数据不一致** | 协调者发送 Commit 后崩溃，部分参与者未收到 | 导致部分提交部分回滚 |
| **无法处理并发 | 全局锁限制并发度 | IM 系统高并发下不可用 |

**IM 系统中 2PC 的适用性**：由于 IM 系统的高并发和低延迟要求，2PC 几乎不直接使用。但 **2PC 的思想**（先 prepare 再 commit）是其他分布式事务方案的基础。

### XA 协议

XA（eXtended Architecture）是 2PC 的工业标准实现，由 X/Open 组织定义。MySQL、PostgreSQL、Oracle 等主流数据库均支持 XA 事务。

**MySQL XA 事务示例**：
```sql
-- 开启 XA 事务
XA START 'tx_user_register';
INSERT INTO users (username, password) VALUES ('alice', 'hash');
XA END 'tx_user_register';
XA PREPARE 'tx_user_register';

-- 提交
XA COMMIT 'tx_user_register';

-- 或回滚
XA ROLLBACK 'tx_user_register';
```

**Seata XA 模式**：Seata 支持 XA 模式，通过 XA 协议保证强一致性，适用于对一致性要求极高的金融级 IM 系统（如支付相关的消息通知）。

## 2. TCC（Try-Confirm-Cancel）

### 原理

TCC（Try-Confirm-Cancel）是一种**业务层面的两阶段提交**，将分布式事务拆分为 **Try（预留资源）→ Confirm（确认执行）→ Cancel（取消回滚）** 三个阶段。与 2PC 不同，TCC 的 Prepare/Commit/Rollback 逻辑由业务代码实现，不依赖数据库的 XA 协议，因此可以用于跨异构系统的事务。

```
┌─────────────────────────────────────────────────────────┐
│                    TCC 事务流程                          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Try（预留资源）                                          │
│  ├─ 预留业务资源（冻结库存/预扣余额/预创建记录）              │
│  ├─ 检查业务前置条件（库存是否充足/余额是否足够）              │
│  └─ 返回成功则进入 Confirm，失败则进入 Cancel              │
│                                                         │
│  Confirm（确认执行）                                      │
│  ├─ 使用 Try 阶段预留的资源                               │
│  ├─ 确认执行业务操作                                     │
│  └─ 必须保证幂等性（重复调用 Confirm 必须成功）             │
│                                                         │
│  Cancel（取消回滚）                                       │
│  ├─ 释放 Try 阶段预留的资源                              │
│  ├─ 回滚业务操作                                         │
│  └─ 必须保证幂等性（重复调用 Cancel 必须成功）              │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### IM 系统的 TCC 场景

**场景 1：发送消息（消息存储 + 未读计数 + 推送）**

```java
// Try：检查资源，预分配消息 ID
@TwoPhaseBusiness
public class SendMessageTCCService {

    public void trySendMessage(Message message) {
        // 1. 预分配全局唯一消息序列号（利用 Redis INCR）
        long seq = redis.incr("msg:seq:" + message.getConversationId());
        message.setSeq(seq);
        
        // 2. 检查用户消息频率限制（是否被限流）
        boolean allowed = rateLimitService.check(message.getSenderId());
        if (!allowed) {
            throw new TccException("Rate limited");
        }
        
        // 3. 记录 Try 阶段状态（用于 Cancel 回滚）
        tccContext.setStatus("TRY_SUCCESS");
    }

    // Confirm：正式写入消息存储
    public void confirmSendMessage(Message message) {
        // 1. 写入消息表
        messageMapper.insert(message);
        
        // 2. 更新会话列表（异步写入）
        conversationService.updateLastMessage(message);
        
        // 3. 触发推送服务
        pushService.triggerPush(message);
    }

    // Cancel：回滚资源
    public void cancelSendMessage(Message message) {
        // 1. 释放消息序列号（通过补偿机制，实际很少需要）
        // 2. 清理 Try 阶段预留的状态
        // 3. 通知发送方消息发送失败
    }
}
```

**场景 2：添加好友（好友关系 + 双向请求记录）**

```java
public class FriendRequestTCCService {

    // Try：预验证 + 创建待确认记录
    public boolean tryAddFriend(String fromUserId, String toUserId) {
        // 1. 检查双方是否已经是好友
        if (friendMapper.exists(fromUserId, toUserId)) {
            throw new TccException("Already friends");
        }
        
        // 2. 检查是否被拉黑
        if (blockMapper.isBlocked(toUserId, fromUserId)) {
            throw new TccException("Blocked by user");
        }
        
        // 3. 预创建好友请求记录（状态为 PENDING）
        FriendRequest req = new FriendRequest();
        req.setFromUserId(fromUserId);
        req.setToUserId(toUserId);
        req.setStatus(RequestStatus.PENDING);
        req.setTccStatus("TRY");
        friendRequestMapper.insert(req);
        
        return true;
    }

    // Confirm：确认好友关系
    public boolean confirmAddFriend(String fromUserId, String toUserId) {
        // 1. 更新请求状态为 CONFIRMED
        friendRequestMapper.updateStatus(fromUserId, toUserId, RequestStatus.CONFIRMED);
        
        // 2. 写入双向好友关系（2条记录）
        friendMapper.insertBidirectional(fromUserId, toUserId);
        
        // 3. 发送通知
        notificationService.sendFriendRequestAccepted(fromUserId, toUserId);
        
        return true;
    }

    // Cancel：回滚好友请求
    public boolean cancelAddFriend(String fromUserId, String toUserId) {
        // 1. 删除预创建的好友请求记录
        friendRequestMapper.deletePendingRequest(fromUserId, toUserId);
        
        // 2. 清理相关缓存
        redis.delete("friend:pending:" + fromUserId + ":" + toUserId);
        
        return true;
    }
}
```

**场景 3：群组创建（群信息 + 群成员 + 通知）**

```java
public class GroupCreateTCCService {

    public boolean tryCreateGroup(GroupCreateRequest request) {
        // 1. 生成群组 ID（雪花算法）
        long groupId = idGenerator.nextId();
        
        // 2. 检查群名是否合规（内容审核）
        moderationService.checkGroupName(request.getGroupName());
        
        // 3. 预锁定群名（防止重复创建）
        redis.setnx("group:name:lock:" + request.getGroupName(), groupId, 300);
        
        // 4. 预创建群成员记录（创建者）
        groupMemberMapper.insert(new GroupMember(groupId, request.getCreatorId(), "OWNER"));
        
        return true;
    }

    public boolean confirmCreateGroup(GroupCreateRequest request, long groupId) {
        // 1. 写入群组信息
        groupMapper.insert(new Group(groupId, request.getGroupName(), request.getCreatorId()));
        
        // 2. 批量写入其他群成员（如果初始成员 > 1）
        if (request.getInitialMembers() != null) {
            batchInsertMembers(groupId, request.getInitialMembers());
        }
        
        // 3. 通知所有成员
        notifyGroupCreated(groupId, request.getInitialMembers());
        
        return true;
    }

    public boolean cancelCreateGroup(GroupCreateRequest request, long groupId) {
        // 1. 删除预创建的群组记录
        groupMapper.deleteById(groupId);
        
        // 2. 删除所有群成员
        groupMemberMapper.deleteByGroupId(groupId);
        
        // 3. 释放群名锁
        redis.del("group:name:lock:" + request.getGroupName());
        
        return true;
    }
}
```

### TCC 的关键设计原则

1. **幂等性**：Try/Confirm/Cancel 三个阶段必须保证幂等。重复调用 Confirm 必须成功（因为资源已预留），重复调用 Cancel 也必须成功（因为资源已释放）。实现方式：每条 TCC 记录带唯一事务 ID，数据库层面使用唯一索引防止重复。
   ```java
   // 幂等性实现：使用事务 ID + 状态字段
   @Insert("INSERT INTO tcc_log (tx_id, phase, status) VALUES (#{txId}, #{phase}, #{status}) " +
           "ON DUPLICATE KEY UPDATE status=#{status}")
   ```

2. **空回滚**：Try 阶段未执行就执行了 Cancel（如网络超时导致 Try 未送达）。解决方案：TCC 服务记录 Try 执行状态，或通过事务日志判断。
   ```java
   // 空回滚防护
   if (!tccLogRepository.exists(txId, "TRY")) {
       // Try 未执行，空回滚，直接返回成功
       return true;
   }
   ```

3. **悬挂**：Cancel 先于 Confirm 执行（如网络延迟导致 Cancel 消息先到达）。解决方案：TCC 服务检测事务执行阶段，拒绝已执行 Confirm 的 Cancel 请求。

4. **TCC-Transaction 框架**：Java 中常用 hmily、ByteTCC、Apache ShardingSphere-Sidecar-TCC 等框架实现 TCC。Seata 也支持 TCC 模式。

## 3. Saga 模式

### 原理

Saga 模式（1987 年 Hector & Kenneth 论文提出）将长事务拆分为**一系列本地事务**，每个本地事务都有对应的**补偿事务（Compensating Transaction）**。与 TCC 不同，Saga **不锁定资源**，通过正向操作 + 补偿操作链式执行来实现最终一致性。

```
┌──────────────────────────────────────────────────────────────┐
│                    Saga 事务流程                             │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  正向操作链（Forward）：                                       │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌────────┐  │
│  │ Step 1   │ → │ Step 2   │ → │ Step 3   │ → │ Step N │  │
│  │ 创建群组  │   │ 写入成员  │   │ 发送通知  │   │ 更新统计 │  │
│  └────┬─────┘   └────┬─────┘   └────┬─────┘   └────┬───┘  │
│       │              │              │              │       │
│       ▼              ▼              ▼              ▼       │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌────────┐  │
│  │ 补偿1    │   │ 补偿2    │   │ 补偿3    │   │ 补偿N  │  │
│  │ 删除群组  │   │ 删除成员  │   │ 撤回通知  │   │ 还原统计 │  │
│  └──────────┘   └──────────┘   └──────────┘   └────────┘  │
│                                                              │
│  补偿触发：当任意 Step 失败时，逆序执行已成功的步骤的补偿事务    │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Saga 的两种协调方式

#### Choreography（编排式）

各服务通过事件（Domain Event）通信，每个服务订阅上游事件，执行本地事务，再发布事件触发下一步。没有中心化的协调者。

```
用户服务              群组服务              消息服务              推送服务
   │                    │                    │                    │
   │  UserCreated 事件  │                    │                    │
   │ ──────────────────▶│                    │                    │
   │                    │ 群组创建            │                    │
   │                    │ GroupCreated 事件   │                    │
   │                    │ ───────────────────▶│                    │
   │                    │                    │ 初始化会话          │
   │                    │                    │ NotifyMembers 事件  │
   │                    │                    │ ──────────────────▶│
   │                    │                    │                    │ 推送通知
   │                    │◀───────────────────│                    │
   │                    │   NotificationSent │                    │
   │◀───────────────────│                    │                    │
   │   GroupSetupDone   │                    │                    │
```

**IM 系统适用场景**：好友关系变更（双向记录创建 → 通知 → 统计更新），每个服务都是对等的事件参与者。

#### Orchestration（编排器式）

一个专门的 **Saga Orchestrator** 统一协调所有参与者的执行步骤，类似工作流引擎。

```
                    Saga Orchestrator（编排器）
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
          ▼                   ▼                   ▼
     用户服务            群组服务            推送服务
          │                   │                   │
          │ ◀─── 创建用户 ────│                   │
          │                   │                   │
          │ ──── 创建群组 ───▶│                   │
          │                   │                   │
          │                   │ ◀─── 初始化会话 ─│
          │                   │                   │
          │                   │ ──── 发送通知 ────▶│
          │                   │                   │
          │ ◀─────────────────┴───────────────────┘
          │              完成
```

**IM 系统适用场景**：群组创建（步骤多且有明确的先后依赖关系，需要中心化协调）。

**Seata Saga 编排器示例**：
```json
{
  "Name": "createGroupSaga",
  "Comment": "创建群组的 Saga 编排器",
  "StartState": "CreateGroupState",
  "States": {
    "CreateGroupState": {
      "Type": "ServiceTask",
      "ServiceName": "group-service",
      "ServiceMethod": "createGroup",
      "CompensateState": "CancelGroupState",
      "Next": "AddMembersState",
      "Status": {
        "Output": {
          "groupId": "$.output.groupId"
        }
      }
    },
    "AddMembersState": {
      "Type": "ServiceTask",
      "ServiceName": "group-service",
      "ServiceMethod": "addMembers",
      "CompensateState": "RemoveMembersState",
      "Next": "NotifyMembersState",
      "InputParameters": {
        "groupId": "$.State._ctx.groupId"
      }
    },
    "NotifyMembersState": {
      "Type": "ServiceTask",
      "ServiceName": "notify-service",
      "ServiceMethod": "sendGroupCreatedNotifications",
      "CompensateState": "RecallNotificationsState",
      "Next": "Succeed"
    },
    "CancelGroupState": {
      "Type": "CompensateSubMachine",
      "CompensateType": "Rollback"
    },
    "RemoveMembersState": { "Type": "CompensateSubMachine" },
    "RecallNotificationsState": { "Type": "CompensateSubMachine" }
  }
}
```

### Saga vs TCC 对比

| 维度 | Saga | TCC |
|------|------|-----|
| **资源锁定** | 不锁定，并发度高 | Try 阶段锁定资源，保证隔离性 |
| **实现复杂度** | 低（只需定义正向+补偿操作） | 高（需实现 Try/Confirm/Cancel 三个接口） |
| **适用场景** | 长流程、异步、高吞吐 | 强一致性、快速失败 |
| **补偿执行时机** | 失败时逆序补偿 | Cancel 即时释放 |
| **数据一致性** | 最终一致（允许中间状态可见） | 阶段隔离（Try 阶段资源预留后其他事务不可见） |
| **性能** | 高（一阶段提交，无锁） | 中（Try 阶段需预留资源） |
| **隔离性保证** | 无（需业务侧使用乐观锁/版本号防护） | 有（Try 阶段资源被预留） |
| **适用技术** | Seata Saga、Eventuate Tram | Seata TCC、hmily、ByteTCC |

### IM 系统 Saga 场景

**消息归档 Saga（异步批量处理）**：
```
步骤 1：标记消息为"待归档"（update status = ARCHIVING）
步骤 2：将消息写入归档存储（MongoDB / 对象存储）
步骤 3：删除原始消息记录
步骤 4：更新归档统计

补偿（逆序）：
补偿 3：恢复消息记录（从归档存储回写）
补偿 2：删除归档数据
补偿 1：更新状态为 NORMAL
```

**好友关系变更 Saga**：
```
步骤 1：创建双向好友记录
步骤 2：删除待确认的好友请求
步骤 3：更新双方的联系人列表缓存
步骤 4：发送好友确认通知

补偿：
补偿 3：清除缓存
补偿 2：恢复好友请求状态
补偿 1：删除双向好友记录
```

## 4. Apache Seata 分布式事务平台

### 概述

Apache Seata（Simple Extensible Autonomous Transaction Architecture）是阿里巴巴开源的分布式事务解决方案，目前是 Apache 顶级项目。Seata 提供 **AT、TCC、Saga、XA** 四种事务模式，覆盖从强一致到最终一致的全场景。

### Seata 架构

```
┌─────────────────────────────────────────────────────────┐
│                      Application                        │
│                   (Seata Client)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐  │
│  │ TM (事务管理) │  │  RM (资源管理)│  │ RM (资源管理) │  │
│  │ 发起事务请求  │  │  注册分支事务  │  │  注册分支事务  │  │
│  └──────┬──────┘  └──────┬──────┘  └──────┬───────┘  │
└─────────┼─────────────────┼─────────────────┼───────────┘
          │                 │                 │
          │    TC (事务协调者 Transaction Coordinator)    │
          │              ┌───┴───┐                        │
          │              │ Seata │                        │
          │              │Server │                        │
          │              └───┬───┘                        │
          │                  │                            │
          │        ┌─────────┼─────────┐                 │
          │        │         │         │                 │
          │    Session    Lock      Store                 │
          │    Cluster   Manager   (DB/Redis)            │
          └────────────┴─────────┴─────────────────────────┘
```

**核心组件**：
- **TC（Transaction Coordinator）**：事务协调者，独立部署，维护全局事务状态和分支事务状态，管理全局锁
- **TM（Transaction Manager）**：事务管理器，应用侧发起全局事务，控制事务边界
- **RM（Resource Manager）**：资源管理器，管理分支事务处理，与 TC 交互注册分支事务和上报状态

### Seata AT 模式（自动补偿）

AT 模式是 Seata 最核心的模式，**零代码入侵**，通过代理 JDBC 数据源自动完成分布式事务。

**工作原理**：
1. 一阶段：解析 SQL → 生成前后镜像 → 执行业务 SQL → 写入 UNDO LOG → 提交本地事务 + 注册分支
2. 二阶段：提交（异步删除 UNDO LOG）| 回滚（根据 UNDO LOG 生成反向 SQL 并执行）

**IM 系统 AT 模式示例（Spring Boot + MyBatis-Plus）**：
```java
// 引入 seata-spring-boot-starter 后，业务代码无需修改
// Seata 自动代理数据源，实现分布式事务

@Service
public class MessageService {
    
    @GlobalTransactional(rollbackFor = Exception.class)  // 开启全局事务
    public void sendMessage(Long senderId, Long receiverId, String content) {
        // 1. 写入消息表（AT 自动生成 UNDO LOG）
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setStatus(MessageStatus.SENT);
        messageMapper.insert(message);
        
        // 2. 更新会话列表（跨服务调用，Seata 自动传播 XID）
        conversationService.updateLastMessage(senderId, receiverId, message);
        
        // 3. 更新未读计数（远程调用）
        unreadCountService.increment(receiverId);
        
        // 4. 如果任何步骤失败，全局回滚
        // 5. 所有步骤成功，全局提交
    }
}
```

**IM 系统适用性**：AT 模式最适合 IM 系统中的**强一致性场景**（如消息 + 会话 + 未读计数三表一致性更新），代码零侵入，自动补偿。但 AT 模式依赖本地 ACID 事务，不适用于 Redis、MongoDB 等非关系型存储。

### Seata 四种模式对比

| 模式 | 事务类型 | 隔离性 | 代码侵入 | 性能 | 适用场景 |
|------|---------|--------|---------|------|---------|
| **AT** | 最终一致 | 行级（全局锁） | 零侵入 | 高 | 关系型数据库，跨库操作 |
| **TCC** | 最终一致 | 业务级（资源预留） | 中（需实现三接口） | 高 | 异构系统，跨语言 |
| **Saga** | 最终一致 | 无 | 低（仅正向+补偿） | 最高 | 长流程，异步高吞吐 |
| **XA** | 强一致 | 行级（X锁） | 低 | 中 | 金融级，对一致性要求极高 |

### IM 系统技术选型建议

```
IM 分布式事务技术选型决策树：

场景类型
├─ 强一致性要求（金融消息/红包）
│   └─ 优先 XA 模式（Seata XA）
│   └─ 次选 AT 模式（Seata AT，MySQL InnoDB）
│
├─ 跨异构系统（MySQL + Redis + MongoDB）
│   └─ 必须使用 TCC 模式（Seata TCC）
│   └─ Redis 用 Redisson 分布式锁 + TCC 预留
│   └─ MongoDB 用 Saga 补偿
│
├─ 长流程异步（消息归档/批量处理）
│   └─ 必须 Saga 模式（Seata Saga）
│   └─ 使用编排器管理复杂流程
│
└─ 普通 CRUD 跨库（会话 + 消息 + 计数）
    └─ AT 模式（首选，零侵入）
    └─ 次选 TCC 模式
```

## 5. 幂等性保障

幂等性是分布式事务的必备能力。无论使用哪种分布式事务方案，都必须保证**同一操作重复执行结果一致**。

### 幂等实现方式

**1. 数据库唯一索引（最常用）**
```sql
-- 消息发送记录表，msg_id + operation_type 唯一索引
CREATE TABLE message_idempotent (
    msg_id VARCHAR(64) NOT NULL,
    operation_type VARCHAR(32) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (msg_id, operation_type),
    INDEX idx_status (status)
);
```

**2. 分布式锁（防并发重复提交）**
```java
// 使用 Redis 分布式锁保证幂等
public boolean sendMessage(Message message) {
    String lockKey = "lock:msg:send:" + message.getUniqueId();
    String lockId = redis.setnx(lockKey, "1", 30, TimeUnit.SECONDS);
    if (lockId == null) {
        throw new DuplicateOperationException("Operation in progress");
    }
    try {
        // 执行业务逻辑
        return doSendMessage(message);
    } finally {
        redis.del(lockKey);
    }
}
```

**3. 状态机 + 版本号**
```java
// 好友请求状态机（必须按顺序流转）
public enum FriendRequestStatus {
    PENDING,    // 待处理
    ACCEPTED,   // 已接受（终态）
    REJECTED,   // 已拒绝（终态）
    EXPIRED     // 已过期（终态）
}

// 通过版本号乐观锁防止重复处理
@Update("UPDATE friend_request SET status=#{newStatus}, version=version+1 " +
        "WHERE id=#{id} AND status=#{currentStatus} AND version=#{version}")
int updateStatusWithVersion(Long id, String currentStatus, String newStatus, Long version);
```

## 6. IM 系统分布式事务最佳实践

### 实践 1：消息发送的最终一致性方案

消息发送是 IM 系统的核心场景，强依赖分布式事务。但经过架构设计，可以**避免分布式事务**：

```
传统方案（强事务）：消息 + 会话 + 未读计数 → 2PC/TCC
优化方案（最终一致）：
1. 消息写入消息库（本地事务，本地提交）
2. 通过消息队列（Kafka/RocketMQ）异步更新会话和未读计数
3. 消费端幂等消费，保证最终一致
4. 消息投递采用"至少一次"语义 + 去重（消息 ID）
```

**优化后的架构**：
```
┌─────────────┐     ┌──────────────────┐
│  消息服务    │────▶│  消息库 (MySQL)  │ 本地事务
└──────┬──────┘     └──────────────────┘
       │
       │ 发布消息事件（消息 ID + 发送者 + 接收者）
       ▼
┌─────────────┐
│  Kafka      │ 消息主题：message:sent
│  (可靠投递)  │
└──────┬──────┘
       │
       ├──▶ 消费组1：会话更新服务（更新 last_message_id）
       │     幂等：UPDATE ... WHERE msg_id = #{msgId}
       │
       ├──▶ 消费组2：未读计数服务（increment unread_count）
       │     幂等：UPDATE ... WHERE msg_id = #{msgId}
       │
       └──▶ 消费组3：推送服务（触发离线推送）
             幂等：消息 ID 已读标记
```

### 实践 2：好友关系的 Saga 编排

好友添加涉及双向记录，使用 Saga 的 Choreography 模式：

```java
// 事件驱动的好友 Saga
public class FriendSagaService {

    // Step 1：发起好友请求（写入 PENDING 状态）
    @Transactional
    public FriendRequest createPendingRequest(String fromUserId, String toUserId) {
        // 检查黑名单等前置条件
        checkBlockStatus(fromUserId, toUserId);
        
        FriendRequest req = new FriendRequest();
        req.setFromUserId(fromUserId);
        req.setToUserId(toUserId);
        req.setStatus(PENDING);
        req.setCreatedAt(now());
        return friendRequestRepository.save(req);
    }

    // Step 2：接受好友请求（写入双向好友关系）
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        FriendRequest req = friendRequestRepository.findById(requestId);
        
        // 写入双向好友关系
        friendRepository.save(new Friend(req.getFromUserId(), req.getToUserId()));
        friendRepository.save(new Friend(req.getToUserId(), req.getFromUserId()));
        
        // 更新请求状态
        req.setStatus(ACCEPTED);
        friendRequestRepository.save(req);
    }

    // Step 3：发送通知
    @Async
    public void sendFriendAcceptedNotification(Long requestId) {
        FriendRequest req = friendRequestRepository.findById(requestId);
        pushService.pushNotification(req.getFromUserId(), 
            "用户 " + req.getToUserId() + " 同意了您的好友请求");
    }

    // 补偿操作（逆序）
    public void reverseAccept(Long requestId) {
        FriendRequest req = friendRequestRepository.findById(requestId);
        friendRepository.delete(req.getFromUserId(), req.getToUserId());
        friendRepository.delete(req.getToUserId(), req.getFromUserId());
    }
}
```

### 实践 3：群组操作的 TCC 保障

群组创建涉及多个关键步骤，使用 TCC 保证强一致性：

```java
public class GroupTCCService {

    @TwoPhaseBusiness
    public Group createGroup(GroupCreateRequest request) {
        // Try 阶段：资源预留
        Group group = new Group();
        group.setId(idGenerator.nextGroupId());
        group.setName(request.getName());
        group.setCreatorId(request.getCreatorId());
        group.setStatus(GroupStatus.CREATING);
        
        // 预占群名
        redis.setnx("group:name:" + request.getName(), group.getId(), 3600);
        
        // 预写群主成员
        groupMemberRepository.save(new GroupMember(group.getId(), request.getCreatorId(), Role.OWNER));
        
        return group;
    }

    public boolean commitCreateGroup(GroupCreateRequest request, Group group) {
        // Confirm 阶段：正式创建
        group.setStatus(GroupStatus.ACTIVE);
        groupRepository.save(group);
        
        // 批量写入其他初始成员
        if (!request.getInitialMembers().isEmpty()) {
            batchSaveMembers(group.getId(), request.getInitialMembers());
        }
        
        // 发送创建成功通知
        notificationService.sendGroupCreated(group.getId(), request.getInitialMembers());
        
        return true;
    }

    public boolean rollbackCreateGroup(GroupCreateRequest request, Group group) {
        // Cancel 阶段：释放资源
        groupRepository.deleteById(group.getId());
        groupMemberRepository.deleteByGroupId(group.getId());
        redis.del("group:name:" + request.getName());
        
        return true;
    }
}
```

### 实践 4：Seata AT + TCC 混合模式

对于 IM 系统中不同一致性要求的操作，混合使用 AT 和 TCC：

```
Seata AT 模式（MySQL 跨库操作）：
├─ 消息 + 会话 + 未读计数（全部 MySQL）
│   └─ Seata AT，全局事务，一条 SQL 链式调用
│
└─ 消息 + 消息索引（MySQL + Elasticsearch）
    └─ MySQL 部分用 AT，ES 部分用 TCC 预留

Seata TCC 模式（异构系统）：
├─ 消息发送 + Redis 计数 + 推送通知
│   └─ Seata TCC，Try 预留 Redis 计数器 + 推送消息
│
└─ 用户注册 + 初始化 Redis 会话
    └─ Seata TCC，Try 预初始化 Redis Session
```

## 7. 监控与故障处理

### 分布式事务监控指标

| 指标 | 说明 | 告警阈值 |
|------|------|---------|
| 全局事务成功率 | 成功提交的全事务数 / 总事务数 | < 99.5% |
| 分支事务失败率 | 失败的分支事务 / 总分支事务 | > 1% |
| 平均事务耗时 | 全局事务从开启到提交的平均时间 | > 500ms |
| 事务堆积数 | 未完成的全局事务数 | > 1000 |
| 补偿事务触发率 | 触发补偿的全事务 / 总事务数 | > 5% |
| TC Server CPU | 事务协调者负载 | > 70% |
| 全局锁等待时间 | 获取全局锁的平均等待时间 | > 100ms |

### 常见故障处理

**1. 事务悬挂（挂起）**
- 原因：Try 超时后 Cancel 被调用，但 Try 其实成功了
- 处理：TC 提供"事务状态回查"接口，定期扫描超时未完成的事务，调用业务侧查询实际状态并处理

**2. 补偿死循环**
- 原因：补偿逻辑本身失败，触发重试，再次失败
- 处理：补偿设置最大重试次数 + 人工干预告警

**3. TC 协调者宕机**
- 处理：Seata Server 使用集群部署（默认高可用），Session 数据存储在 DB/Redis 中
- 恢复：重启后从 Session Cluster 恢复未完成的全域事务

**4. 分支事务部分成功**
- 原因：部分参与者提交成功，部分失败
- 处理：AT 模式下自动回滚所有已提交的分支；TCC/Saga 模式下执行补偿链

## 技术来源

- Apache Seata 官方文档（https://seata.io/zh-cn/docs/overview/what-is-seata）
- microservices.io Saga Pattern（https://microservices.io/patterns/data/saga.html）
- Chris Richardson《Microservices Patterns》第四章
- Eventuate Tram Sagas（https://github.com/eventuate-tram/eventuate-tram-sagas）
- 分布式事务：2PC / TCC / Saga 对比（阿里云 / InfoQ）

## 下一步学习方向

1. **Sealed Sender / 密封发送者（元数据最小化）** — Signal Protocol 元数据保护
2. **群组加密（Sender Keys 方案）** — WhatsApp/ Signal 群组加密协议
3. **微信/Telegram/WhatsApp 技术架构深度分析** — 大厂实践
4. **大规模 WebSocket 集群 + 负载均衡** — 连接管理与路由
5. **服务网格（Service Mesh）与 IM 微服务治理** — Istio/Envoy 在 IM 中的应用
6. **数据库全文搜索（Elasticsearch）与消息检索** — 大规模消息搜索
7. **多设备同步协议与冲突处理** — OT / CRDT 在 IM 中的应用
