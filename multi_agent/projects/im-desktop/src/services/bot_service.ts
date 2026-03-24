export interface Bot {
  id: string;
  name: string;
  description: string;
  avatarUrl: string;
  ownerId: string;
  botType: 'AI' | 'WEBHOOK' | 'SCRIPTED';
  aiProvider?: 'OPENAI' | 'CLAUDE' | 'GEMINI' | 'CUSTOM';
  aiModel?: string;
  webhookUrl?: string;
  slashCommands: string[];
  enabled: boolean;
  globalEnabled: boolean;
  allowedGroupIds: string[];
  config?: BotConfig;
  createdAt: string;
  updatedAt: string;
}

export interface BotConfig {
  maxTokens: number;
  temperature: number;
  systemPrompt: string;
  maxHistoryMessages: number;
  responseTimeoutSeconds: number;
  streamEnabled: boolean;
  apiKey: string;
  apiEndpoint: string;
  retryAttempts: number;
  rateLimitPerMinute: number;
}

export interface BotMessage {
  id: string;
  botId: string;
  senderId: string;
  content: string;
  messageType: string;
  timestamp: string;
  metadata?: Record<string, any>;
}

export interface SlashCommand {
  name: string;
  description: string;
  usage: string;
}

class BotService {
  private baseUrl = '/api/bots';
  private wsUrl = '/ws/bots';

  async getBots(): Promise<Bot[]> {
    const response = await fetch(this.baseUrl);
    if (!response.ok) {
      throw new Error('Failed to fetch bots');
    }
    return response.json();
  }

  async getBot(botId: string): Promise<Bot> {
    const response = await fetch(`${this.baseUrl}/${botId}`);
    if (!response.ok) {
      throw new Error('Failed to fetch bot');
    }
    return response.json();
  }

  async createBot(bot: Partial<Bot>): Promise<Bot> {
    const response = await fetch(this.baseUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': this.getCurrentUserId(),
      },
      body: JSON.stringify(bot),
    });
    if (!response.ok) {
      throw new Error('Failed to create bot');
    }
    return response.json();
  }

  async updateBot(botId: string, bot: Partial<Bot>): Promise<Bot> {
    const response = await fetch(`${this.baseUrl}/${botId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(bot),
    });
    if (!response.ok) {
      throw new Error('Failed to update bot');
    }
    return response.json();
  }

  async deleteBot(botId: string): Promise<void> {
    const response = await fetch(`${this.baseUrl}/${botId}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error('Failed to delete bot');
    }
  }

  async sendMessage(botId: string, userId: string, message: string): Promise<string> {
    const response = await fetch(`${this.baseUrl}/${botId}/message`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userId, message }),
    });
    if (!response.ok) {
      throw new Error('Failed to send message');
    }
    const data = await response.json();
    return data.response;
  }

  async getSlashCommands(botId: string): Promise<SlashCommand[]> {
    const response = await fetch(`${this.baseUrl}/${botId}/commands`);
    if (!response.ok) {
      throw new Error('Failed to fetch commands');
    }
    return response.json();
  }

  async enableBot(botId: string): Promise<Bot> {
    const response = await fetch(`${this.baseUrl}/${botId}/enable`, {
      method: 'POST',
    });
    if (!response.ok) {
      throw new Error('Failed to enable bot');
    }
    return response.json();
  }

  async disableBot(botId: string): Promise<Bot> {
    const response = await fetch(`${this.baseUrl}/${botId}/disable`, {
      method: 'POST',
    });
    if (!response.ok) {
      throw new Error('Failed to disable bot');
    }
    return response.json();
  }

  private getCurrentUserId(): string {
    return localStorage.getItem('userId') || '';
  }

  connectWebSocket(botId: string, onMessage: (msg: BotMessage) => void): WebSocket {
    const ws = new WebSocket(`${this.wsUrl}/${botId}`);
    ws.onmessage = (event) => {
      const message: BotMessage = JSON.parse(event.data);
      onMessage(message);
    };
    return ws;
  }
}

export const botService = new BotService();
