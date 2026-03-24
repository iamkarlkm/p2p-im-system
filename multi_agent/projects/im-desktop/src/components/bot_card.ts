import { Bot } from '../services/bot_service';

export class BotCard {
  private bot: Bot;
  private element: HTMLElement;

  constructor(bot: Bot) {
    this.bot = bot;
    this.element = this.createElement();
  }

  private createElement(): HTMLElement {
    const card = document.createElement('div');
    card.className = 'bot-card';
    card.innerHTML = `
      <div class="bot-avatar">
        ${this.bot.avatarUrl 
          ? `<img src="${this.bot.avatarUrl}" alt="${this.bot.name}" />`
          : `<div class="bot-avatar-placeholder">${this.bot.name.charAt(0)}</div>`
        }
      </div>
      <div class="bot-info">
        <h3 class="bot-name">${this.bot.name}</h3>
        <p class="bot-description">${this.bot.description || 'No description'}</p>
        <div class="bot-meta">
          <span class="bot-type">${this.getBotTypeLabel()}</span>
          <span class="bot-status ${this.bot.enabled ? 'enabled' : 'disabled'}">
            ${this.bot.enabled ? '🟢 Online' : '🔴 Offline'}
          </span>
        </div>
        ${this.bot.botType === 'AI' ? `
          <div class="bot-ai-info">
            <span class="ai-provider">${this.bot.aiProvider || 'N/A'}</span>
            <span class="ai-model">${this.bot.aiModel || 'N/A'}</span>
          </div>
        ` : ''}
      </div>
      <div class="bot-actions">
        <button class="btn-edit" data-action="edit">Edit</button>
        <button class="btn-${this.bot.enabled ? 'disable' : 'enable'}" data-action="${this.bot.enabled ? 'disable' : 'enable'}">
          ${this.bot.enabled ? 'Disable' : 'Enable'}
        </button>
        <button class="btn-delete" data-action="delete">Delete</button>
      </div>
    `;

    this.attachEventListeners(card);
    return card;
  }

  private getBotTypeLabel(): string {
    switch (this.bot.botType) {
      case 'AI': return '🤖 AI Bot';
      case 'WEBHOOK': return '🔗 Webhook Bot';
      case 'SCRIPTED': return '📝 Scripted Bot';
      default: return '❓ Unknown';
    }
  }

  private attachEventListeners(card: HTMLElement): void {
    const buttons = card.querySelectorAll('button');
    buttons.forEach(button => {
      button.addEventListener('click', (e) => {
        const action = (e.target as HTMLElement).dataset.action;
        this.handleAction(action);
      });
    });
  }

  private handleAction(action?: string): void {
    if (!action) return;

    switch (action) {
      case 'edit':
        this.dispatchEvent('edit', this.bot);
        break;
      case 'enable':
      case 'disable':
        this.dispatchEvent('toggle', this.bot);
        break;
      case 'delete':
        if (confirm(`Are you sure you want to delete ${this.bot.name}?`)) {
          this.dispatchEvent('delete', this.bot);
        }
        break;
    }
  }

  private dispatchEvent(name: string, detail: any): void {
    window.dispatchEvent(new CustomEvent(`bot:${name}`, { detail }));
  }

  public getElement(): HTMLElement {
    return this.element;
  }

  public update(bot: Bot): void {
    this.bot = bot;
    const newElement = this.createElement();
    this.element.replaceWith(newElement);
    this.element = newElement;
  }
}

export class BotList {
  private container: HTMLElement;
  private bots: Map<string, BotCard> = new Map();

  constructor(containerId: string) {
    const container = document.getElementById(containerId);
    if (!container) {
      throw new Error(`Container not found: ${containerId}`);
    }
    this.container = container;
    this.init();
  }

  private async init(): Promise<void> {
    await this.loadBots();
    this.setupEventListeners();
  }

  private async loadBots(): Promise<void> {
    try {
      const { botService } = await import('../services/bot_service');
      const bots = await botService.getBots();
      this.renderBots(bots);
    } catch (error) {
      console.error('Failed to load bots:', error);
      this.showError('Failed to load bots');
    }
  }

  private renderBots(bots: Bot[]): void {
    this.container.innerHTML = '';
    bots.forEach(bot => {
      const card = new BotCard(bot);
      this.bots.set(bot.id, card);
      this.container.appendChild(card.getElement());
    });
  }

  private setupEventListeners(): void {
    window.addEventListener('bot:edit', ((e: CustomEvent) => {
      this.handleEdit(e.detail);
    }) as EventListener);

    window.addEventListener('bot:toggle', ((e: CustomEvent) => {
      this.handleToggle(e.detail);
    }) as EventListener);

    window.addEventListener('bot:delete', ((e: CustomEvent) => {
      this.handleDelete(e.detail);
    }) as EventListener);
  }

  private handleEdit(bot: Bot): void {
    console.log('Edit bot:', bot);
  }

  private async handleToggle(bot: Bot): Promise<void> {
    try {
      const { botService } = await import('../services/bot_service');
      const updatedBot = bot.enabled 
        ? await botService.disableBot(bot.id)
        : await botService.enableBot(bot.id);
      const card = this.bots.get(bot.id);
      if (card) {
        card.update(updatedBot);
      }
    } catch (error) {
      console.error('Failed to toggle bot:', error);
      this.showError('Failed to toggle bot status');
    }
  }

  private async handleDelete(bot: Bot): Promise<void> {
    try {
      const { botService } = await import('../services/bot_service');
      await botService.deleteBot(bot.id);
      const card = this.bots.get(bot.id);
      if (card) {
        card.getElement().remove();
        this.bots.delete(bot.id);
      }
    } catch (error) {
      console.error('Failed to delete bot:', error);
      this.showError('Failed to delete bot');
    }
  }

  private showError(message: string): void {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    this.container.appendChild(errorDiv);
    setTimeout(() => errorDiv.remove(), 3000);
  }

  public addBot(bot: Bot): void {
    const card = new BotCard(bot);
    this.bots.set(bot.id, card);
    this.container.appendChild(card.getElement());
  }

  public removeBot(botId: string): void {
    const card = this.bots.get(botId);
    if (card) {
      card.getElement().remove();
      this.bots.delete(botId);
    }
  }

  public refresh(): Promise<void> {
    return this.loadBots();
  }
}
