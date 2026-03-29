import { botService, Bot, BotMessage } from '../services/bot_service';

export class BotChat {
  private bot: Bot;
  private messages: BotMessage[] = [];
  private container: HTMLElement;
  private inputElement: HTMLInputElement;
  private sendButton: HTMLButtonElement;
  private messagesContainer: HTMLElement;
  private ws: WebSocket | null = null;
  private typing: boolean = false;

  constructor(botId: string, containerId: string) {
    this.container = document.getElementById(containerId) as HTMLElement;
    if (!this.container) {
      throw new Error(`Container not found: ${containerId}`);
    }
    
    this.bot = {} as Bot;
    this.init(botId);
  }

  private async init(botId: string): Promise<void> {
    try {
      this.bot = await botService.getBot(botId);
      this.render();
      this.connectWebSocket();
      this.setupEventListeners();
    } catch (error) {
      console.error('Failed to initialize bot chat:', error);
      this.showError('Failed to load bot');
    }
  }

  private render(): void {
    this.container.innerHTML = `
      <div class="bot-chat">
        <div class="chat-header">
          <div class="bot-info">
            <div class="bot-avatar">
              ${this.bot.avatarUrl 
                ? `<img src="${this.bot.avatarUrl}" alt="${this.bot.name}" />`
                : `<div class="bot-avatar-placeholder">${this.bot.name.charAt(0)}</div>`
              }
            </div>
            <div class="bot-details">
              <h3>${this.bot.name}</h3>
              <p>${this.bot.description || 'Bot Chat'}</p>
            </div>
          </div>
          <div class="chat-actions">
            <button class="btn-commands" data-action="commands">Commands</button>
          </div>
        </div>
        <div class="chat-messages" id="chat-messages"></div>
        <div class="chat-input-container">
          <input type="text" id="chat-input" placeholder="Type a message..." />
          <button class="btn-send" id="send-button">Send</button>
        </div>
        <div class="typing-indicator" id="typing-indicator" style="display: none;">
          <span>${this.bot.name} is typing...</span>
        </div>
      </div>
    `;

    this.messagesContainer = document.getElementById('chat-messages') as HTMLElement;
    this.inputElement = document.getElementById('chat-input') as HTMLInputElement;
    this.sendButton = document.getElementById('send-button') as HTMLButtonElement;
  }

  private setupEventListeners(): void {
    this.sendButton.addEventListener('click', () => this.sendMessage());
    
    this.inputElement.addEventListener('keypress', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        this.sendMessage();
      }
    });

    document.addEventListener('click', (e) => {
      const target = e.target as HTMLElement;
      if (target.dataset.action === 'commands') {
        this.showCommands();
      }
    });
  }

  private async sendMessage(): Promise<void> {
    const content = this.inputElement.value.trim();
    if (!content || this.typing) return;

    this.addMessage({
      id: Date.now().toString(),
      botId: this.bot.id,
      senderId: this.getCurrentUserId(),
      content,
      messageType: 'text',
      timestamp: new Date().toISOString(),
    });

    this.inputElement.value = '';
    this.setTyping(true);

    try {
      const response = await botService.sendMessage(this.bot.id, this.getCurrentUserId(), content);
      this.addMessage({
        id: (Date.now() + 1).toString(),
        botId: this.bot.id,
        senderId: this.bot.id,
        content: response,
        messageType: 'text',
        timestamp: new Date().toISOString(),
      });
    } catch (error) {
      console.error('Failed to send message:', error);
      this.showError('Failed to send message');
    } finally {
      this.setTyping(false);
    }
  }

  private addMessage(message: BotMessage): void {
    this.messages.push(message);
    const messageEl = document.createElement('div');
    messageEl.className = `message ${message.senderId === this.bot.id ? 'bot-message' : 'user-message'}`;
    messageEl.innerHTML = `
      <div class="message-content">${this.escapeHtml(message.content)}</div>
      <div class="message-time">${this.formatTime(message.timestamp)}</div>
    `;
    this.messagesContainer.appendChild(messageEl);
    this.scrollToBottom();
  }

  private setTyping(typing: boolean): void {
    this.typing = typing;
    const indicator = document.getElementById('typing-indicator') as HTMLElement;
    if (indicator) {
      indicator.style.display = typing ? 'block' : 'none';
    }
  }

  private connectWebSocket(): void {
    this.ws = botService.connectWebSocket(this.bot.id, (message) => {
      this.addMessage(message);
      this.setTyping(false);
    });
  }

  private showCommands(): void {
    alert('Available commands:\n/help - Show help\n/status - Show status\n/info - Show info');
  }

  private showError(message: string): void {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    this.messagesContainer.appendChild(errorDiv);
    setTimeout(() => errorDiv.remove(), 3000);
  }

  private scrollToBottom(): void {
    this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
  }

  private formatTime(timestamp: string): string {
    const date = new Date(timestamp);
    return date.toLocaleTimeString();
  }

  private escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  private getCurrentUserId(): string {
    return localStorage.getItem('userId') || 'anonymous';
  }

  public disconnect(): void {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }

  public clearMessages(): void {
    this.messages = [];
    this.messagesContainer.innerHTML = '';
  }

  public getMessages(): BotMessage[] {
    return [...this.messages];
  }
}

export class BotChatManager {
  private chats: Map<string, BotChat> = new Map();

  public openChat(botId: string, containerId: string): BotChat {
    if (this.chats.has(botId)) {
      return this.chats.get(botId)!;
    }
    const chat = new BotChat(botId, containerId);
    this.chats.set(botId, chat);
    return chat;
  }

  public closeChat(botId: string): void {
    const chat = this.chats.get(botId);
    if (chat) {
      chat.disconnect();
      this.chats.delete(botId);
    }
  }

  public getChat(botId: string): BotChat | undefined {
    return this.chats.get(botId);
  }
}
