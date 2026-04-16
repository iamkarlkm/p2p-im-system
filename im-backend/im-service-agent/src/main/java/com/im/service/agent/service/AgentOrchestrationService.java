package com.im.service.agent.service;

import com.im.service.agent.dto.*;
import com.im.service.agent.entity.*;
import com.im.service.agent.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 多Agent协作编排服务
 * 功能#349: Agent任务分解、分配与协作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentOrchestrationService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final AgentRegistryRepository agentRegistryRepository;
    private final AgentTaskRepository taskRepository;
    private final AgentMessageRepository messageRepository;

    // 内存中存储活跃工作流执行
    private final Map<String, WorkflowExecutionContext> activeExecutions = new ConcurrentHashMap<>();

    // ========== 工作流管理 ==========

    /**
     * 创建编排工作流
     */
    @Transactional
    public WorkflowResponse createWorkflow(Long merchantId, WorkflowCreateRequest request) {
        log.info("创建Agent编排工作流: {}", request.getWorkflowName());

        // 创建工作流
        Workflow workflow = new Workflow();
        workflow.setWorkflowId(UUID.randomUUID().toString());
        workflow.setMerchantId(merchantId);
        workflow.setName(request.getWorkflowName());
        workflow.setDescription(request.getDescription());
        workflow.setStatus("DRAFT");
        workflow.setCreateTime(LocalDateTime.now());

        // 解析并保存工作流定义
        List<WorkflowStep> steps = parseWorkflowDefinition(request.getDefinition());
        workflow.setSteps(steps);

        workflowRepository.save(workflow);

        WorkflowResponse response = new WorkflowResponse();
        response.setWorkflowId(workflow.getWorkflowId());
        response.setName(workflow.getName());
        response.setStatus(workflow.getStatus());
        response.setStepCount(steps.size());
        response.setCreateTime(workflow.getCreateTime());

        return response;
    }

    /**
     * 启动工作流执行
     */
    @Transactional
    public WorkflowExecutionResponse executeWorkflow(Long merchantId, String workflowId, Map<String, Object> params) {
        log.info("启动工作流执行: workflowId={}", workflowId);

        Workflow workflow = workflowRepository.findByWorkflowId(workflowId)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));

        if (!workflow.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权执行此工作流");
        }

        // 创建工作流执行实例
        String executionId = UUID.randomUUID().toString();
        WorkflowExecution execution = new WorkflowExecution();
        execution.setExecutionId(executionId);
        execution.setWorkflowId(workflowId);
        execution.setMerchantId(merchantId);
        execution.setStatus("RUNNING");
        execution.setStartTime(LocalDateTime.now());
        execution.setParams(params);
        executionRepository.save(execution);

        // 创建执行上下文
        WorkflowExecutionContext context = new WorkflowExecutionContext();
        context.setExecutionId(executionId);
        context.setWorkflowId(workflowId);
        context.setCurrentStepIndex(0);
        context.setStatus("RUNNING");
        context.setStartTime(LocalDateTime.now());
        activeExecutions.put(executionId, context);

        // 启动异步执行
        executeWorkflowAsync(context, workflow, params);

        WorkflowExecutionResponse response = new WorkflowExecutionResponse();
        response.setExecutionId(executionId);
        response.setWorkflowId(workflowId);
        response.setStatus("RUNNING");
        response.setStartTime(LocalDateTime.now());
        response.setMessage("工作流已启动");

        return response;
    }

    /**
     * 获取工作流状态
     */
    public WorkflowStatusResponse getWorkflowStatus(Long merchantId, String workflowId) {
        Workflow workflow = workflowRepository.findByWorkflowId(workflowId)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));

        if (!workflow.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权查看此工作流");
        }

        // 获取最新执行状态
        List<WorkflowExecution> executions = executionRepository.findByWorkflowIdOrderByStartTimeDesc(workflowId);

        WorkflowStatusResponse response = new WorkflowStatusResponse();
        response.setWorkflowId(workflowId);
        response.setName(workflow.getName());
        response.setStatus(workflow.getStatus());

        if (!executions.isEmpty()) {
            WorkflowExecution latest = executions.get(0);
            response.setLastExecutionId(latest.getExecutionId());
            response.setLastExecutionStatus(latest.getStatus());
            response.setLastExecutionTime(latest.getStartTime());
        }

        response.setTotalExecutions(executions.size());
        response.setSuccessCount((int) executions.stream().filter(e -> "COMPLETED".equals(e.getStatus())).count());
        response.setFailureCount((int) executions.stream().filter(e -> "FAILED".equals(e.getStatus())).count());

        return response;
    }

    /**
     * 取消工作流执行
     */
    @Transactional
    public void cancelWorkflow(Long merchantId, String executionId) {
        WorkflowExecution execution = executionRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new RuntimeException("工作流执行不存在"));

        if (!execution.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权取消此工作流");
        }

        execution.setStatus("CANCELLED");
        execution.setEndTime(LocalDateTime.now());
        executionRepository.save(execution);

        // 从活跃执行中移除
        WorkflowExecutionContext context = activeExecutions.get(executionId);
        if (context != null) {
            context.setStatus("CANCELLED");
            activeExecutions.remove(executionId);
        }

        log.info("工作流执行已取消: executionId={}", executionId);
    }

    // ========== 任务分解与分配 ==========

    /**
     * 分解复杂任务
     */
    public TaskDecompositionResponse decomposeTask(Long merchantId, TaskDecompositionRequest request) {
        log.info("分解任务: {}", request.getTaskDescription());

        // 使用LLM或规则引擎分解任务
        List<SubTask> subTasks = decomposeWithLLM(request.getTaskDescription(), request.getConstraints());

        TaskDecompositionResponse response = new TaskDecompositionResponse();
        response.setOriginalTask(request.getTaskDescription());
        response.setSubTasks(subTasks);
        response.setDecompositionStrategy("HIERARCHICAL");
        response.setEstimatedTotalTime(calculateTotalTime(subTasks));

        return response;
    }

    /**
     * 分配子任务给Agent
     */
    @Transactional
    public TaskAssignmentResponse assignTask(Long merchantId, String taskId, TaskAssignmentRequest request) {
        log.info("分配任务 {} 给Agent {}", taskId, request.getAgentId());

        // 查找Agent
        AgentRegistry agent = agentRegistryRepository.findByAgentId(request.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent不存在"));

        // 检查Agent能力匹配
        if (!agentHasCapability(agent, request.getRequiredCapability())) {
            throw new RuntimeException("Agent不具备所需能力");
        }

        // 创建任务分配
        AgentTask task = new AgentTask();
        task.setTaskId(taskId);
        task.setMerchantId(merchantId);
        task.setAssignedAgentId(request.getAgentId());
        task.setTaskType(request.getTaskType());
        task.setPriority(request.getPriority());
        task.setStatus("ASSIGNED");
        task.setCreateTime(LocalDateTime.now());
        task.setDeadline(request.getDeadline());
        taskRepository.save(task);

        // 发送ACL消息通知Agent
        sendACLMessage(merchantId, "ORCHESTRATOR", request.getAgentId(), 
                "TASK_ASSIGNED", buildTaskMessage(task));

        TaskAssignmentResponse response = new TaskAssignmentResponse();
        response.setTaskId(taskId);
        response.setAssignedAgentId(request.getAgentId());
        response.setAgentName(agent.getAgentName());
        response.setStatus("ASSIGNED");
        response.setExpectedCompletion(calculateExpectedCompletion(task));

        return response;
    }

    // ========== Agent通讯（ACL协议） ==========

    /**
     * 发送Agent间消息（ACL - Agent Communication Language）
     */
    @Transactional
    public AgentMessageResponse sendAgentMessage(Long merchantId, AgentMessageRequest request) {
        log.debug("Agent {} 发送 {} 消息给 Agent {}", 
                request.getSenderAgentId(), request.getMessageType(), request.getReceiverAgentId());

        // 构建ACL消息
        ACLMessage aclMessage = new ACLMessage();
        aclMessage.setMessageId(UUID.randomUUID().toString());
        aclMessage.setSenderId(request.getSenderAgentId());
        aclMessage.setReceiverId(request.getReceiverAgentId());
        aclMessage.setMessageType(request.getMessageType()); // INFORM, REQUEST, PROPOSE, ACCEPT, REJECT, etc.
        aclMessage.setContent(request.getContent());
        aclMessage.setOntology(request.getOntology());
        aclMessage.setProtocol(request.getProtocol());
        aclMessage.setConversationId(request.getConversationId());
        aclMessage.setReplyWith(request.getReplyWith());
        aclMessage.setInReplyTo(request.getInReplyTo());
        aclMessage.setTimestamp(LocalDateTime.now());

        messageRepository.save(aclMessage);

        // 如果是同步请求，等待响应
        if ("REQUEST".equals(request.getMessageType()) && request.isSynchronous()) {
            // 实际实现需要等待响应
            aclMessage.setStatus("PENDING_RESPONSE");
        } else {
            aclMessage.setStatus("DELIVERED");
        }

        AgentMessageResponse response = new AgentMessageResponse();
        response.setMessageId(aclMessage.getMessageId());
        response.setStatus(aclMessage.getStatus());
        response.setTimestamp(aclMessage.getTimestamp());

        return response;
    }

    // ========== Agent注册与发现 ==========

    /**
     * 注册Agent能力
     */
    @Transactional
    public AgentRegistrationResponse registerAgent(Long merchantId, AgentRegistrationRequest request) {
        log.info("注册Agent: {}", request.getAgentName());

        AgentRegistry agent = new AgentRegistry();
        agent.setAgentId(UUID.randomUUID().toString());
        agent.setMerchantId(merchantId);
        agent.setAgentName(request.getAgentName());
        agent.setAgentType(request.getAgentType());
        agent.setCapabilities(request.getCapabilities());
        agent.setEndpoint(request.getEndpoint());
        agent.setStatus("ACTIVE");
        agent.setRegisterTime(LocalDateTime.now());
        agent.setVersion(request.getVersion());

        agentRegistryRepository.save(agent);

        AgentRegistrationResponse response = new AgentRegistrationResponse();
        response.setAgentId(agent.getAgentId());
        response.setAgentName(agent.getAgentName());
        response.setStatus("REGISTERED");
        response.setRegisterTime(agent.getRegisterTime());

        return response;
    }

    /**
     * 发现可用Agent
     */
    public List<AgentCapabilityResponse> discoverAgents(Long merchantId, String capability) {
        List<AgentRegistry> agents;
        
        if (capability != null && !capability.isEmpty()) {
            agents = agentRegistryRepository.findByMerchantIdAndCapability(merchantId, capability);
        } else {
            agents = agentRegistryRepository.findByMerchantIdAndStatus(merchantId, "ACTIVE");
        }

        return agents.stream()
                .map(this::convertToCapabilityResponse)
                .collect(Collectors.toList());
    }

    // ========== 冲突检测与仲裁 ==========

    /**
     * 检测冲突
     */
    public ConflictDetectionResponse detectConflicts(Long merchantId, List<String> agentIds) {
        log.info("检测Agent冲突: {}", agentIds);

        List<Conflict> conflicts = new ArrayList<>();

        // 检查资源竞争
        List<Conflict> resourceConflicts = checkResourceConflicts(merchantId, agentIds);
        conflicts.addAll(resourceConflicts);

        // 检查决策冲突
        List<Conflict> decisionConflicts = checkDecisionConflicts(merchantId, agentIds);
        conflicts.addAll(decisionConflicts);

        // 检查目标冲突
        List<Conflict> goalConflicts = checkGoalConflicts(merchantId, agentIds);
        conflicts.addAll(goalConflicts);

        ConflictDetectionResponse response = new ConflictDetectionResponse();
        response.setConflictCount(conflicts.size());
        response.setConflicts(conflicts);
        response.setSeverityLevel(calculateSeverityLevel(conflicts));
        response.setRecommendation(generateConflictRecommendation(conflicts));

        return response;
    }

    /**
     * 仲裁决策
     */
    @Transactional
    public ArbitrationResponse arbitrateConflict(Long merchantId, ArbitrationRequest request) {
        log.info("仲裁冲突: {}", request.getConflictId());

        // 获取冲突详情
        Conflict conflict = getConflictById(request.getConflictId());

        // 根据仲裁策略做出决策
        ArbitrationDecision decision;
        switch (request.getStrategy()) {
            case "PRIORITY":
                decision = arbitrateByPriority(conflict);
                break;
            case "FIFO":
                decision = arbitrateByFIFO(conflict);
                break;
            case "MERIT":
                decision = arbitrateByMerit(conflict);
                break;
            case "HUMAN":
                decision = request.getHumanDecision();
                break;
            default:
                decision = arbitrateByPriority(conflict);
        }

        // 应用决策
        applyArbitrationDecision(conflict, decision);

        ArbitrationResponse response = new ArbitrationResponse();
        response.setConflictId(request.getConflictId());
        response.setDecision(decision);
        response.setAppliedAt(LocalDateTime.now());
        response.setStatus("RESOLVED");

        return response;
    }

    // ========== 可视化编排工具 ==========

    /**
     * 获取编排可视化数据
     */
    public WorkflowVisualizationResponse getWorkflowVisualization(Long merchantId, String workflowId) {
        Workflow workflow = workflowRepository.findByWorkflowId(workflowId)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));

        if (!workflow.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权查看此工作流");
        }

        // 构建可视化数据
        List<NodeVisualization> nodes = workflow.getSteps().stream()
                .map(this::convertToNode)
                .collect(Collectors.toList());

        List<EdgeVisualization> edges = buildEdges(workflow.getSteps());

        WorkflowVisualizationResponse response = new WorkflowVisualizationResponse();
        response.setWorkflowId(workflowId);
        response.setWorkflowName(workflow.getName());
        response.setNodes(nodes);
        response.setEdges(edges);
        response.setLayout("DAG");

        return response;
    }

    // ========== 私有方法 ==========

    private void executeWorkflowAsync(WorkflowExecutionContext context, Workflow workflow, Map<String, Object> params) {
        // 异步执行工作流
        // 实际实现应该使用线程池或分布式任务调度
        new Thread(() -> {
            try {
                List<WorkflowStep> steps = workflow.getSteps();
                for (int i = 0; i < steps.size(); i++) {
                    if ("CANCELLED".equals(context.getStatus())) {
                        break;
                    }

                    context.setCurrentStepIndex(i);
                    WorkflowStep step = steps.get(i);
                    
                    // 执行步骤
                    executeStep(step, params);
                }

                // 完成工作流
                context.setStatus("COMPLETED");
                updateExecutionStatus(context.getExecutionId(), "COMPLETED", null);

            } catch (Exception e) {
                log.error("工作流执行失败", e);
                context.setStatus("FAILED");
                context.setErrorMessage(e.getMessage());
                updateExecutionStatus(context.getExecutionId(), "FAILED", e.getMessage());
            } finally {
                activeExecutions.remove(context.getExecutionId());
            }
        }).start();
    }

    private void executeStep(WorkflowStep step, Map<String, Object> params) {
        // 执行具体步骤逻辑
        log.info("执行工作流步骤: {} - {}", step.getStepId(), step.getAction());
        // 实际实现根据步骤类型执行不同操作
    }

    private void updateExecutionStatus(String executionId, String status, String errorMessage) {
        WorkflowExecution execution = executionRepository.findByExecutionId(executionId)
                .orElse(null);
        if (execution != null) {
            execution.setStatus(status);
            execution.setEndTime(LocalDateTime.now());
            execution.setErrorMessage(errorMessage);
            executionRepository.save(execution);
        }
    }

    private List<WorkflowStep> parseWorkflowDefinition(String definition) {
        // 解析工作流定义（JSON/YAML）
        List<WorkflowStep> steps = new ArrayList<>();
        // 简化实现
        return steps;
    }

    private List<SubTask> decomposeWithLLM(String taskDescription, List<String> constraints) {
        // 使用LLM分解任务
        List<SubTask> subTasks = new ArrayList<>();
        
        // 简化实现：模拟分解结果
        SubTask task1 = new SubTask();
        task1.setSubTaskId(UUID.randomUUID().toString());
        task1.setDescription("数据收集");
        task1.setEstimatedTimeMinutes(30);
        task1.setRequiredCapability("DATA_COLLECTION");
        subTasks.add(task1);

        SubTask task2 = new SubTask();
        task2.setSubTaskId(UUID.randomUUID().toString());
        task2.setDescription("数据分析");
        task2.setEstimatedTimeMinutes(45);
        task2.setRequiredCapability("DATA_ANALYSIS");
        subTasks.add(task2);

        SubTask task3 = new SubTask();
        task3.setSubTaskId(UUID.randomUUID().toString());
        task3.setDescription("生成报告");
        task3.setEstimatedTimeMinutes(20);
        task3.setRequiredCapability("REPORT_GENERATION");
        subTasks.add(task3);

        return subTasks;
    }

    private int calculateTotalTime(List<SubTask> subTasks) {
        return subTasks.stream().mapToInt(SubTask::getEstimatedTimeMinutes).sum();
    }

    private boolean agentHasCapability(AgentRegistry agent, String requiredCapability) {
        return agent.getCapabilities().contains(requiredCapability);
    }

    private LocalDateTime calculateExpectedCompletion(AgentTask task) {
        // 根据任务优先级和Agent能力估算完成时间
        return LocalDateTime.now().plusHours(1);
    }

    private void sendACLMessage(Long merchantId, String senderId, String receiverId, 
                                String performative, String content) {
        AgentMessageRequest request = new AgentMessageRequest();
        request.setSenderAgentId(senderId);
        request.setReceiverAgentId(receiverId);
        request.setMessageType(performative);
        request.setContent(content);
        request.setConversationId(UUID.randomUUID().toString());
        request.setSynchronous(false);
        
        sendAgentMessage(merchantId, request);
    }

    private String buildTaskMessage(AgentTask task) {
        return String.format("新任务分配: %s, 截止时间: %s", task.getTaskType(), task.getDeadline());
    }

    private AgentCapabilityResponse convertToCapabilityResponse(AgentRegistry agent) {
        AgentCapabilityResponse response = new AgentCapabilityResponse();
        response.setAgentId(agent.getAgentId());
        response.setAgentName(agent.getAgentName());
        response.setAgentType(agent.getAgentType());
        response.setCapabilities(agent.getCapabilities());
        response.setStatus(agent.getStatus());
        return response;
    }

    private List<Conflict> checkResourceConflicts(Long merchantId, List<String> agentIds) {
        List<Conflict> conflicts = new ArrayList<>();
        // 检查资源竞争
        return conflicts;
    }

    private List<Conflict> checkDecisionConflicts(Long merchantId, List<String> agentIds) {
        List<Conflict> conflicts = new ArrayList<>();
        // 检查决策冲突
        return conflicts;
    }

    private List<Conflict> checkGoalConflicts(Long merchantId, List<String> agentIds) {
        List<Conflict> conflicts = new ArrayList<>();
        // 检查目标冲突
        return conflicts;
    }

    private String calculateSeverityLevel(List<Conflict> conflicts) {
        if (conflicts.isEmpty()) return "NONE";
        if (conflicts.size() > 5) return "HIGH";
        if (conflicts.size() > 2) return "MEDIUM";
        return "LOW";
    }

    private String generateConflictRecommendation(List<Conflict> conflicts) {
        if (conflicts.isEmpty()) return "无冲突，系统运行正常";
        return "建议进行仲裁或调整Agent任务分配";
    }

    private Conflict getConflictById(String conflictId) {
        // 从数据库获取冲突
        return new Conflict();
    }

    private ArbitrationDecision arbitrateByPriority(Conflict conflict) {
        ArbitrationDecision decision = new ArbitrationDecision();
        decision.setWinnerId("higher_priority_agent");
        decision.setReason("基于优先级仲裁");
        return decision;
    }

    private ArbitrationDecision arbitrateByFIFO(Conflict conflict) {
        ArbitrationDecision decision = new ArbitrationDecision();
        decision.setWinnerId("first_request_agent");
        decision.setReason("基于先到先服务原则");
        return decision;
    }

    private ArbitrationDecision arbitrateByMerit(Conflict conflict) {
        ArbitrationDecision decision = new ArbitrationDecision();
        decision.setWinnerId("higher_merit_agent");
        decision.setReason("基于绩效评估");
        return decision;
    }

    private void applyArbitrationDecision(Conflict conflict, ArbitrationDecision decision) {
        // 应用仲裁决策
        log.info("应用仲裁决策: conflict={}, winner={}", conflict.getConflictId(), decision.getWinnerId());
    }

    private NodeVisualization convertToNode(WorkflowStep step) {
        NodeVisualization node = new NodeVisualization();
        node.setId(step.getStepId());
        node.setLabel(step.getAction());
        node.setType(step.getStepType());
        return node;
    }

    private List<EdgeVisualization> buildEdges(List<WorkflowStep> steps) {
        List<EdgeVisualization> edges = new ArrayList<>();
        for (int i = 0; i < steps.size() - 1; i++) {
            EdgeVisualization edge = new EdgeVisualization();
            edge.setSource(steps.get(i).getStepId());
            edge.setTarget(steps.get(i + 1).getStepId());
            edges.add(edge);
        }
        return edges;
    }
}
