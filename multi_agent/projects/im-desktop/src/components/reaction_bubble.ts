import { reactionService, ReactionStats } from '../services/reaction_service';

const QUICK_REACTIONS = ['👍', '❤️', '😂', '😮', '😢', '🙏'];

export class ReactionBubble {
  private container: HTMLElement;
  private reactions: Map<string, HTMLElement> = new Map();
  private expanded = false;

  constructor(private messageId: string) {
    this.container = this.render();
    reactionService.onReactionUpdate((msgId, stats) => {
      if (msgId === this.messageId) this.updateStats(stats);
    });
  }

  private render(): HTMLElement {
    const el = document.createElement('div');
    el.className = 'reaction-bubble';
    el.innerHTML = `
      <div class="reaction-quick-bar">
        ${QUICK_REACTIONS.map(e => `<button class="reaction-btn" data-emoji="${e}">${e}</button>`).join('')}
      </div>
      <div class="reaction-stats-bar"></div>
    `;
    el.querySelectorAll('.reaction-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const emoji = btn.getAttribute('data-emoji')!;
        reactionService.addReaction(this.messageId, emoji).catch(console.error);
      });
    });
    return el;
  }

  private updateStats(stats: ReactionStats): void {
    const bar = this.container.querySelector('.reaction-stats-bar') as HTMLElement;
    bar.innerHTML = Object.entries(stats.counts)
      .map(([emoji, count]) => `<span class="reaction-chip">${emoji} ${count}</span>`)
      .join('');
  }

  getElement(): HTMLElement { return this.container; }
}
