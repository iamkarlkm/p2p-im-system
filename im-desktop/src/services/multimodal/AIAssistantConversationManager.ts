import { EventEmitter } from 'events';
import { 
  AIAssistant, 
  AIConversation, 
  AIContextEntry, 
  AIResponse, 
  StreamChunk,
  AICapability,
  MultimodalMessage,
  MessageType,
  MessageStatus 
} from '../types/multimodal';

/**
 * AI助手对话管理器
 * 管理AI助手的对话历史、上下文和消息流
 */
export class AIAssistantConversationManager extends EventEmitter {
  private conversations: Map<string, AIConversation> = new Map();
  private assistants: Map<string, AIAssistant> = new Map();
  private activeConversationId: string | null = null;
  private readonly maxContextEntries: number = 50;
  private readonly maxContextAge: number = 30 * 60 * 1000; // 30分钟

  constructor() {
    super();
    this.initializeEventHandlers();
  }

  private initializeEventHandlers(): void {
    this.on('conversation:create', this.handleConversationCreate.bind(this));
    this.on('conversation:message', this.handleNewMessage.bind(this));
    this.on('conversation:stream', this.handleStreamChunk.bind(this));
  }

  /**
   * 注册AI助手
   */
  public registerAssistant(assistant: AIAssistant): void {
    this.assistants.set(assistant.id, assistant);
    this.emit('assistant:registered', assistant);
  }

  /**
   * 获取AI助手
   */
  public getAssistant(assistantId: string): AIAssistant | undefined {
    return this.assistants.get(assistantId);
  }

  /**
   * 获取所有AI助手
   */
  public getAllAssistants(): AIAssistant[] {
    return Array.from(this.assistants.values());
  }

  /**
   * 获取特定能力的AI助手
   */
  public getAssistantsByCapability(capability: AICapability): AIAssistant[] {
    return this.getAllAssistants().filter(
      assistant => assistant.capabilities.includes(capability)
    );
  }

  /**
   * 创建新对话
   */
  public createConversation(assistantId: string, title?: string): string {
    const assistant = this.assistants.get(assistantId);
    if (!assistant) {
      throw new Error(`AI助手不存在: ${assistantId}`);
    }

    const conversationId = this.generateConversationId();
    const now = Date.now();

    const conversation: AIConversation = {
      id: conversationId,
      assistantId,
      title: title || `与 ${assistant.name} 的对话`,
      messages: [],
      context: [],
      createdAt: now,
      updatedAt: now
    };

    this.conversations.set(conversationId, conversation);
    this.activeConversationId = conversationId;

    // 添加系统提示
    if (assistant.settings?.systemPrompt) {
      this.addContextEntry(conversationId, {
        role: 'system',
        content: assistant.settings.systemPrompt,
        timestamp: now
      });
    }

    this.emit('conversation:created', conversation);
    return conversationId;
  }

  /**
   * 处理对话创建
   */
  private handleConversationCreate(data: { assistantId: string; title?: string }): void {
    this.createConversation(data.assistantId, data.title);
  }

  /**
   * 获取对话
   */
  public getConversation(conversationId: string): AIConversation | undefined {
    return this.conversations.get(conversationId);
  }

  /**
   * 获取所有对话
   */
  public getAllConversations(): AIConversation[] {
    return Array.from(this.conversations.values())
      .sort((a, b) => b.updatedAt - a.updatedAt);
  }

  /**
   * 获取特定助手的对话
   */
  public getConversationsByAssistant(assistantId: string): AIConversation[] {
    return this.getAllConversations()
      .filter(c => c.assistantId === assistantId);
  }

  /**
   * 删除对话
   */
  public deleteConversation(conversationId: string): boolean {
    const deleted = this.conversations.delete(conversationId);
    if (deleted) {
      if (this.activeConversationId === conversationId) {
        this.activeConversationId = null;
      }
      this.emit('conversation:deleted', { conversationId });
    }
    return deleted;
  }

  /**
   * 设置活动对话
   */
  public setActiveConversation(conversationId: string | null): void {
    if (conversationId && !this.conversations.has(conversationId)) {
      throw new Error(`对话不存在: ${conversationId}`);
    }
    this.activeConversationId = conversationId;
    this.emit('conversation:active', { conversationId });
  }

  /**
   * 获取活动对话
   */
  public getActiveConversation(): AIConversation | undefined {
    return this.activeConversationId 
      ? this.conversations.get(this.activeConversationId)
      : undefined;
  }

  /**
   * 发送消息到AI助手
   */
  public async sendMessage(
    conversationId: string, 
    content: string,
    attachments?: any[]
  ): Promise<void> {
    const conversation = this.conversations.get(conversationId);
    if (!conversation) {
      throw new Error(`对话不存在: ${conversationId}`);
    }

    const assistant = this.assistants.get(conversation.assistantId);
    if (!assistant) {
      throw new Error(`AI助手不存在: ${conversation.assistantId}`);
    }

    const now = Date.now();

    // 创建用户消息
    const userMessage: MultimodalMessage = {
      id: this.generateMessageId(),
      conversationId,
      senderId: 'user',
      type: attachments && attachments.length > 0 ? MessageType.MIXED : MessageType.TEXT,
      content,
      attachments,
      status: MessageStatus.SENT,
      timestamp: now
    };

    conversation.messages.push(userMessage);
    conversation.updatedAt = now;

    // 添加上下文
    this.addContextEntry(conversationId, {
      role: 'user',
      content,
      timestamp: now,
      metadata: attachments ? { attachments } : undefined
    });

    this.emit('message:sent', userMessage);

    // 调用AI助手获取响应
    await this.callAIAssistant(conversationId, assistant);
  }

  /**
   * 处理新消息
   */
  private async handleNewMessage(data: { 
    conversationId: string; 
    content: string;
    attachments?: any[];
  }): Promise<void> {
    await this.sendMessage(data.conversationId, data.content, data.attachments);
  }

  /**
   * 调用AI助手
   */
  private async callAIAssistant(
    conversationId: string, 
    assistant: AIAssistant
  ): Promise<void> {
    const conversation = this.conversations.get(conversationId)!;
    const now = Date.now();

    // 创建AI响应消息
    const aiMessage: MultimodalMessage = {
      id: this.generateMessageId(),
      conversationId,
      senderId: assistant.id,
      senderName: assistant.name,
      senderAvatar: assistant.avatar,
      type: MessageType.TEXT,
      content: '',
      status: MessageStatus.SENDING,
      timestamp: now
    };

    conversation.messages.push(aiMessage);
    this.emit('message:generating', aiMessage);

    try {
      // 构建请求上下文
      const context = this.buildContext(conversation);

      // 模拟AI流式响应（实际实现中调用API）
      await this.streamAIResponse(conversationId, aiMessage.id, assistant, context);

    } catch (error) {
      aiMessage.status = MessageStatus.FAILED;
      this.emit('message:error', { 
        message: aiMessage, 
        error: error instanceof Error ? error.message : 'AI响应失败'
      });
    }
  }

  /**
   * 流式AI响应
   */
  private async streamAIResponse(
    conversationId: string,
    messageId: string,
    assistant: AIAssistant,
    context: AIContextEntry[]
  ): Promise<void> {
    // 模拟流式响应
    const responses = [
      '我正在处理您的请求...',
      '根据您的问题，',
      '我来为您详细解答。',
      '这是一个很好的问题！'
    ];

    let fullContent = '';

    for (let i = 0; i < responses.length; i++) {
      await new Promise(resolve => setTimeout(resolve, 300));

      const chunk: StreamChunk = {
        id: this.generateChunkId(),
        conversationId,
        content: responses[i],
        isComplete: i === responses.length - 1,
        timestamp: Date.now()
      };

      fullContent += responses[i];
      this.emit('stream:chunk', chunk);
    }

    // 完成响应
    const conversation = this.conversations.get(conversationId)!;
    const message = conversation.messages.find(m => m.id === messageId);
    
    if (message) {
      message.content = fullContent;
      message.status = MessageStatus.SENT;
      conversation.updatedAt = Date.now();

      // 添加上下文
      this.addContextEntry(conversationId, {
        role: 'assistant',
        content: fullContent,
        timestamp: Date.now()
      });

      this.emit('message:completed', message);
    }
  }

  /**
   * 处理流式数据块
   */
  private handleStreamChunk(chunk: StreamChunk): void {
    const conversation = this.conversations.get(chunk.conversationId);
    if (!conversation) return;

    const message = conversation.messages.find(m => 
      m.id === chunk.conversationId || m.status === MessageStatus.SENDING
    );

    if (message) {
      message.content += chunk.content;
      this.emit('message:updated', message);
    }
  }

  /**
   * 构建AI上下文
   */
  private buildContext(conversation: AIConversation): AIContextEntry[] {
    // 清理过期上下文
    this.cleanupContext(conversation.id);

    return conversation.context.filter(entry => {
      // 过滤系统消息和最近的对话
      return entry.role !== 'system' || 
             Date.now() - entry.timestamp < this.maxContextAge;
    }).slice(-this.maxContextEntries);
  }

  /**
   * 添加上下文条目
   */
  private addContextEntry(
    conversationId: string, 
    entry: AIContextEntry
  ): void {
    const conversation = this.conversations.get(conversationId);
    if (!conversation) return;

    conversation.context.push(entry);

    // 限制上下文大小
    if (conversation.context.length > this.maxContextEntries) {
      conversation.context = conversation.context.slice(-this.maxContextEntries);
    }
  }

  /**
   * 清理过期上下文
   */
  private cleanupContext(conversationId: string): void {
    const conversation = this.conversations.get(conversationId);
    if (!conversation) return;

    const cutoff = Date.now() - this.maxContextAge;
    conversation.context = conversation.context.filter(
      entry => entry.timestamp > cutoff || entry.role === 'system'
    );
  }

  /**
   * 清空对话历史
   */
  public clearConversation(conversationId: string): void {
    const conversation = this.conversations.get(conversationId);
    if (!conversation) return;

    conversation.messages = [];
    conversation.context = conversation.context.filter(e => e.role === 'system');
    conversation.updatedAt = Date.now();

    this.emit('conversation:cleared', { conversationId });
  }

  /**
   * 重命名对话
   */
  public renameConversation(conversationId: string, newTitle: string): void {
    const conversation = this.conversations.get(conversationId);
    if (!conversation) return;

    conversation.title = newTitle;
    conversation.updatedAt = Date.now();

    this.emit('conversation:renamed', { conversationId, title: newTitle });
  }

  /**
   * 生成对话ID
   */
  private generateConversationId(): string {
    return `conv_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  /**
   * 生成消息ID
   */
  private generateMessageId(): string {
    return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  /**
   * 生成流数据块ID
   */
  private generateChunkId(): string {
    return `chunk_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  /**
   * 导出对话
   */
  public exportConversation(conversationId: string): string {
    const conversation = this.conversations.get(conversationId);
    if (!conversation) {
      throw new Error(`对话不存在: ${conversationId}`);
    }

    return JSON.stringify(conversation, null, 2);
  }

  /**
   * 导入对话
   */
  public importConversation(data: string): AIConversation {
    const conversation: AIConversation = JSON.parse(data);
    this.conversations.set(conversation.id, conversation);
    this.emit('conversation:imported', conversation);
    return conversation;
  }

  /**
   * 销毁管理器
   */
  public destroy(): void {
    this.conversations.clear();
    this.assistants.clear();
    this.removeAllListeners();
  }
}

export default AIAssistantConversationManager;
