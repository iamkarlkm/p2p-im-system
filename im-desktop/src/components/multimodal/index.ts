/**
 * 多模态AI助手模块 - 组件导出
 */

// 类型定义
export * from '../types/multimodal';

// 服务
export { MultimodalMessageProcessor } from '../services/multimodal/MultimodalMessageProcessor';
export { AIAssistantConversationManager } from '../services/multimodal/AIAssistantConversationManager';

// 组件
export { default as MediaPreview } from './MediaPreview';
export { default as AIAssistantSelector } from './AIAssistantSelector';
export { default as MultimodalInput } from './MultimodalInput';
export { default as StreamingMessage, MessageBubble, MessageList } from './StreamingMessage';
export { default as AIConversationView } from './AIConversationView';
