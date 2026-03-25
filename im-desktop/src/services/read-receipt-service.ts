// Read Receipt Service
import axios from 'axios';
import { ReadReceipt, ReadReceiptRequest } from '../types/read-receipt';

const API_BASE = 'http://localhost:8080/api/read-receipt';

export class ReadReceiptService {
  private userId: number;

  constructor(userId: number) {
    this.userId = userId;
  }

  async markAsRead(conversationId: string, messageId: string): Promise<ReadReceipt> {
    const request: ReadReceiptRequest = {
      userId: this.userId,
      conversationId,
      messageId,
    };
    const response = await axios.post(`${API_BASE}/mark`, request);
    return response.data;
  }

  async markBatchAsRead(conversationId: string, messageIds: string[]): Promise<ReadReceipt[]> {
    const request: ReadReceiptRequest = {
      userId: this.userId,
      conversationId,
      messageIds,
    };
    const response = await axios.post(`${API_BASE}/mark-batch`, request);
    return response.data;
  }

  async getReadReceipts(conversationId: string, messageId: string): Promise<ReadReceipt[]> {
    const response = await axios.get(`${API_BASE}/list`, {
      params: { conversationId, messageId },
    });
    return response.data;
  }
}
