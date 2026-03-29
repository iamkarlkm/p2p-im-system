import { EventEmitter } from 'events';
import { MultimodalMessage, MessageType, ProcessingStatus } from '../types/multimodal';

/**
 * 多模态消息处理器
 * 处理文本、图像、音频、视频消息的输入和输出
 */
export class MultimodalMessageProcessor extends EventEmitter {
  private supportedTypes: Set<MessageType> = new Set([
    MessageType.TEXT,
    MessageType.IMAGE,
    MessageType.AUDIO,
    MessageType.VIDEO,
    MessageType.FILE,
    MessageType.MIXED
  ]);

  private maxFileSize: number = 50 * 1024 * 1024; // 50MB
  private processingQueue: Map<string, ProcessingStatus> = new Map();

  constructor() {
    super();
    this.initializeEventHandlers();
  }

  private initializeEventHandlers(): void {
    this.on('message:received', this.handleMessageReceived.bind(this));
    this.on('message:sending', this.handleMessageSending.bind(this));
  }

  /**
   * 处理接收到的消息
   */
  private async handleMessageReceived(message: MultimodalMessage): Promise<void> {
    const status: ProcessingStatus = {
      messageId: message.id,
      type: message.type,
      stage: 'downloading',
      progress: 0,
      timestamp: Date.now()
    };

    this.processingQueue.set(message.id, status);
    this.emit('processing:start', status);

    try {
      // 验证消息类型
      if (!this.isSupportedType(message.type)) {
        throw new Error(`不支持的消息类型: ${message.type}`);
      }

      // 处理不同类型的内容
      switch (message.type) {
        case MessageType.IMAGE:
          await this.processImageMessage(message);
          break;
        case MessageType.AUDIO:
          await this.processAudioMessage(message);
          break;
        case MessageType.VIDEO:
          await this.processVideoMessage(message);
          break;
        case MessageType.MIXED:
          await this.processMixedMessage(message);
          break;
        default:
          await this.processTextMessage(message);
      }

      status.stage = 'completed';
      status.progress = 100;
      this.emit('processing:complete', status);

    } catch (error) {
      status.stage = 'error';
      status.error = error instanceof Error ? error.message : '未知错误';
      this.emit('processing:error', status);
    } finally {
      this.processingQueue.delete(message.id);
    }
  }

  /**
   * 处理发送消息
   */
  private async handleMessageSending(message: MultimodalMessage): Promise<void> {
    const status: ProcessingStatus = {
      messageId: message.id,
      type: message.type,
      stage: 'preparing',
      progress: 0,
      timestamp: Date.now()
    };

    this.processingQueue.set(message.id, status);
    this.emit('upload:start', status);

    try {
      // 压缩和优化媒体文件
      if (message.attachments && message.attachments.length > 0) {
        for (let i = 0; i < message.attachments.length; i++) {
          const attachment = message.attachments[i];
          status.stage = 'compressing';
          status.progress = ((i + 1) / message.attachments.length) * 50;
          
          if (attachment.size > this.maxFileSize) {
            attachment.data = await this.compressAttachment(attachment);
          }
        }
      }

      status.stage = 'uploading';
      status.progress = 75;
      this.emit('upload:progress', status);

      // 发送消息到服务器
      await this.sendToServer(message);

      status.stage = 'completed';
      status.progress = 100;
      this.emit('upload:complete', status);

    } catch (error) {
      status.stage = 'error';
      status.error = error instanceof Error ? error.message : '发送失败';
      this.emit('upload:error', status);
    } finally {
      this.processingQueue.delete(message.id);
    }
  }

  /**
   * 处理图像消息
   */
  private async processImageMessage(message: MultimodalMessage): Promise<void> {
    const status = this.processingQueue.get(message.id);
    if (!status) return;

    status.stage = 'rendering';
    status.progress = 50;
    this.emit('processing:progress', status);

    // 预加载图像
    if (message.attachments) {
      for (const attachment of message.attachments) {
        if (attachment.mimeType?.startsWith('image/')) {
          await this.preloadImage(attachment.url);
        }
      }
    }

    status.progress = 80;
    this.emit('processing:progress', status);
  }

  /**
   * 处理音频消息
   */
  private async processAudioMessage(message: MultimodalMessage): Promise<void> {
    const status = this.processingQueue.get(message.id);
    if (!status) return;

    status.stage = 'decoding';
    status.progress = 40;
    this.emit('processing:progress', status);

    // 生成音频波形预览
    if (message.attachments) {
      for (const attachment of message.attachments) {
        if (attachment.mimeType?.startsWith('audio/')) {
          attachment.waveform = await this.generateWaveform(attachment.url);
        }
      }
    }

    status.progress = 70;
    this.emit('processing:progress', status);
  }

  /**
   * 处理视频消息
   */
  private async processVideoMessage(message: MultimodalMessage): Promise<void> {
    const status = this.processingQueue.get(message.id);
    if (!status) return;

    status.stage = 'extracting';
    status.progress = 30;
    this.emit('processing:progress', status);

    // 生成视频缩略图
    if (message.attachments) {
      for (const attachment of message.attachments) {
        if (attachment.mimeType?.startsWith('video/')) {
          attachment.thumbnail = await this.generateVideoThumbnail(attachment.url);
          attachment.duration = await this.getVideoDuration(attachment.url);
        }
      }
    }

    status.progress = 60;
    this.emit('processing:progress', status);
  }

  /**
   * 处理混合消息
   */
  private async processMixedMessage(message: MultimodalMessage): Promise<void> {
    const status = this.processingQueue.get(message.id);
    if (!status) return;

    status.stage = 'parsing';
    status.progress = 20;
    this.emit('processing:progress', status);

    // 分别处理不同类型的附件
    if (message.attachments) {
      for (const attachment of message.attachments) {
        if (attachment.mimeType?.startsWith('image/')) {
          await this.preloadImage(attachment.url);
        } else if (attachment.mimeType?.startsWith('audio/')) {
          attachment.waveform = await this.generateWaveform(attachment.url);
        } else if (attachment.mimeType?.startsWith('video/')) {
          attachment.thumbnail = await this.generateVideoThumbnail(attachment.url);
        }
      }
    }

    status.progress = 75;
    this.emit('processing:progress', status);
  }

  /**
   * 处理文本消息
   */
  private async processTextMessage(message: MultimodalMessage): Promise<void> {
    const status = this.processingQueue.get(message.id);
    if (!status) return;

    status.stage = 'formatting';
    status.progress = 50;
    this.emit('processing:progress', status);

    // 解析Markdown和特殊格式
    message.renderedContent = this.parseMarkdown(message.content);

    status.progress = 90;
    this.emit('processing:progress', status);
  }

  /**
   * 发送消息到服务器
   */
  private async sendToServer(message: MultimodalMessage): Promise<void> {
    // 实际实现中会调用API
    return new Promise((resolve) => {
      setTimeout(resolve, 500); // 模拟网络延迟
    });
  }

  /**
   * 压缩附件
   */
  private async compressAttachment(attachment: any): Promise<Buffer> {
    // 实际实现中会进行压缩
    return Buffer.alloc(0);
  }

  /**
   * 预加载图像
   */
  private preloadImage(url: string): Promise<void> {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.onload = () => resolve();
      img.onerror = reject;
      img.src = url;
    });
  }

  /**
   * 生成音频波形
   */
  private async generateWaveform(url: string): Promise<number[]> {
    // 实际实现中会分析音频数据
    return Array(100).fill(0).map(() => Math.random() * 100);
  }

  /**
   * 生成视频缩略图
   */
  private async generateVideoThumbnail(url: string): Promise<string> {
    // 实际实现中会提取视频帧
    return url + '?thumbnail=1';
  }

  /**
   * 获取视频时长
   */
  private async getVideoDuration(url: string): Promise<number> {
    return new Promise((resolve) => {
      const video = document.createElement('video');
      video.onloadedmetadata = () => resolve(video.duration);
      video.src = url;
    });
  }

  /**
   * 解析Markdown
   */
  private parseMarkdown(content: string): string {
    // 简化的Markdown解析
    return content
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/`(.*?)`/g, '<code>$1</code>');
  }

  /**
   * 检查是否支持的消息类型
   */
  private isSupportedType(type: MessageType): boolean {
    return this.supportedTypes.has(type);
  }

  /**
   * 获取处理状态
   */
  public getProcessingStatus(messageId: string): ProcessingStatus | undefined {
    return this.processingQueue.get(messageId);
  }

  /**
   * 设置最大文件大小
   */
  public setMaxFileSize(size: number): void {
    this.maxFileSize = size;
  }

  /**
   * 添加支持的消息类型
   */
  public addSupportedType(type: MessageType): void {
    this.supportedTypes.add(type);
  }

  /**
   * 处理接收消息
   */
  public receiveMessage(message: MultimodalMessage): void {
    this.emit('message:received', message);
  }

  /**
   * 发送消息
   */
  public sendMessage(message: MultimodalMessage): void {
    this.emit('message:sending', message);
  }

  /**
   * 销毁处理器
   */
  public destroy(): void {
    this.processingQueue.clear();
    this.removeAllListeners();
  }
}

export default MultimodalMessageProcessor;
