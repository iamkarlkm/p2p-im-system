/**
 * 消息表情回应API
 * @module api/message-reaction-api
 */

import axios from 'axios';
import {
  MessageReaction,
  MessageReactionDTO,
  ReactionSummary,
  AddReactionRequest,
  RemoveReactionRequest,
  ToggleReactionRequest,
  BatchSummaryRequest,
  PageResponse,
  PopularEmoji,
} from '../types/message-reaction';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';
const REACTIONS_API = `${API_BASE_URL}/reactions`;

/**
 * 添加表情回应
 */
export const addReaction = async (
  request: AddReactionRequest
): Promise<MessageReaction> => {
  const response = await axios.post(`${REACTIONS_API}`, request);
  return response.data.data;
};

/**
 * 切换表情回应
 */
export const toggleReaction = async (
  request: ToggleReactionRequest
): Promise<MessageReaction | null> => {
  const response = await axios.post(`${REACTIONS_API}/toggle`, request);
  return response.data.data;
};

/**
 * 移除表情回应
 */
export const removeReaction = async (
  request: RemoveReactionRequest
): Promise<void> => {
  await axios.delete(
    `${REACTIONS_API}/message/${request.messageId}/user/${request.userId}/emoji/${encodeURIComponent(request.emojiCode)}`
  );
};

/**
 * 获取消息的表情回应汇总
 */
export const getReactionSummary = async (
  messageId: number,
  currentUserId: number
): Promise<ReactionSummary> => {
  const response = await axios.get(`${REACTIONS_API}/message/${messageId}/summary`, {
    params: { currentUserId },
  });
  return response.data.data;
};

/**
 * 获取消息的所有表情回应
 */
export const getReactionsByMessage = async (
  messageId: number
): Promise<MessageReaction[]> => {
  const response = await axios.get(`${REACTIONS_API}/message/${messageId}`);
  return response.data.data;
};

/**
 * 分页获取消息的表情回应
 */
export const getReactionsByMessagePaged = async (
  messageId: number,
  page: number = 0,
  size: number = 20
): Promise<PageResponse<MessageReaction>> => {
  const response = await axios.get(`${REACTIONS_API}/message/${messageId}/paged`, {
    params: { page, size },
  });
  return response.data.data;
};

/**
 * 获取用户对消息的回应
 */
export const getReactionsByMessageAndUser = async (
  messageId: number,
  userId: number
): Promise<MessageReaction[]> => {
  const response = await axios.get(`${REACTIONS_API}/message/${messageId}/user/${userId}`);
  return response.data.data;
};

/**
 * 删除用户对消息的所有回应
 */
export const removeAllReactionsByUser = async (
  messageId: number,
  userId: number
): Promise<void> => {
  await axios.delete(`${REACTIONS_API}/message/${messageId}/user/${userId}`);
};

/**
 * 批量获取消息的表情回应汇总
 */
export const getReactionSummaries = async (
  messageIds: number[],
  currentUserId: number
): Promise<ReactionSummary[]> => {
  const response = await axios.post(`${REACTIONS_API}/summaries`, messageIds, {
    params: { currentUserId },
  });
  return response.data.data;
};

/**
 * 获取会话中的热门表情
 */
export const getPopularEmojis = async (
  conversationId: number,
  limit: number = 10
): Promise<PopularEmoji[]> => {
  const response = await axios.get(`${REACTIONS_API}/conversation/${conversationId}/popular`, {
    params: { limit },
  });
  return response.data.data;
};

/**
 * 检查用户是否回应了消息
 */
export const hasUserReacted = async (
  messageId: number,
  userId: number
): Promise<boolean> => {
  const response = await axios.get(`${REACTIONS_API}/message/${messageId}/user/${userId}/has-reacted`);
  return response.data.data;
};
