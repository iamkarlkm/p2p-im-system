/**
 * 投票卡片组件 (PollCard Component)
 * 桌面端 TypeScript 实现
 */

import { PollResult, PollOption, formatRemainingTime, formatVoteCount, getStatusLabel, getStatusColor } from './poll_service';

// ==================== 组件 Props ====================

export interface PollCardProps {
  poll: PollResult;
  currentUserId: string;
  onVote?: (pollId: string, optionIds: string[]) => void;
  onCancelVote?: (pollId: string) => void;
  onClose?: (pollId: string) => void;
  onDelete?: (pollId: string) => void;
  onAddOption?: (pollId: string, text: string) => void;
  onClick?: (pollId: string) => void;
}

export interface PollOptionProps {
  option: PollOption;
  poll: PollResult;
  isSelected: boolean;
  isCreator: boolean;
  onToggle: (optionId: string) => void;
}

// ==================== 投票选项行组件 ====================

export class PollOptionRow {
  private props: PollOptionProps;
  private element: HTMLElement | null = null;

  constructor(props: PollOptionProps) {
    this.props = props;
  }

  mount(container: HTMLElement): HTMLElement {
    const { option, poll, isSelected } = this.props;
    const isActive = poll.status === 'ACTIVE';

    const wrapper = document.createElement('div');
    wrapper.className = 'poll-option-row';
    wrapper.dataset.optionId = option.optionId;

    // 选项容器
    const inner = document.createElement('div');
    inner.className = 'poll-option-inner';

    // 选择指示器（单选框/多选框）
    const indicator = document.createElement('div');
    indicator.className = 'poll-option-indicator';
    if (poll.multiSelect) {
      indicator.classList.add('checkbox');
      if (isSelected) indicator.classList.add('checked');
    } else {
      indicator.classList.add('radio');
      if (isSelected) indicator.classList.add('checked');
    }

    // 选项文本
    const text = document.createElement('span');
    text.className = 'poll-option-text';
    text.textContent = option.optionText;

    // 结果栏（仅显示模式）
    const resultBar = document.createElement('div');
    resultBar.className = 'poll-option-result-bar';
    resultBar.style.width = `${option.percentage}%`;

    // 投票数和百分比
    const stats = document.createElement('div');
    stats.className = 'poll-option-stats';
    stats.textContent = `${formatVoteCount(option.voteCount)} (${option.percentage.toFixed(1)}%)`;

    inner.appendChild(indicator);
    inner.appendChild(text);
    if (!isActive || isSelected || !isActive) {
      inner.appendChild(resultBar);
    }
    inner.appendChild(stats);

    wrapper.appendChild(inner);

    // 投票者头像（公开投票时显示）
    if (!poll.anonymous && option.voterIds && option.voterIds.length > 0 && !isActive) {
      const voters = document.createElement('div');
      voters.className = 'poll-option-voters';
      const maxShow = 5;
      option.voterIds.slice(0, maxShow).forEach((voterId, i) => {
        const avatar = document.createElement('div');
        avatar.className = 'poll-voter-avatar';
        avatar.textContent = voterId.charAt(0).toUpperCase();
        avatar.style.zIndex = String(maxShow - i);
        voters.appendChild(avatar);
      });
      if (option.voterIds.length > maxShow) {
        const more = document.createElement('span');
        more.className = 'poll-voters-more';
        more.textContent = `+${option.voterIds.length - maxShow}`;
        voters.appendChild(more);
      }
      wrapper.appendChild(voters);
    }

    // 点击选择（仅活跃状态）
    if (isActive) {
      wrapper.classList.add('clickable');
      wrapper.addEventListener('click', () => {
        this.props.onToggle(option.optionId);
      });
    }

    this.element = wrapper;
    container.appendChild(wrapper);
    return wrapper;
  }

  update(props: PollOptionProps): void {
    this.props = props;
    if (this.element) {
      // 重新挂载以更新内容
      const parent = this.element.parentElement;
      if (parent) {
        this.element.remove();
        this.mount(parent);
      }
    }
  }

  destroy(): void {
    if (this.element) {
      this.element.remove();
      this.element = null;
    }
  }
}

// ==================== 投票卡片组件 ====================

export class PollCard {
  private props: PollCardProps;
  private element: HTMLElement | null = null;
  private optionRows: Map<string, PollOptionRow> = new Map();
  private selectedOptions: Set<string> = new Set();
  private addOptionInput: HTMLInputElement | null = null;

  constructor(props: PollCardProps) {
    this.props = props;
    this.selectedOptions = new Set(props.poll.votedOptionIds || []);
  }

  mount(container: HTMLElement): HTMLElement {
    const { poll, currentUserId } = this.props;
    const isCreator = poll.creatorId === currentUserId;
    const isActive = poll.status === 'ACTIVE';
    const canVote = isActive && currentUserId !== poll.creatorId;

    // 外层卡片
    const card = document.createElement('div');
    card.className = 'poll-card';
    card.dataset.pollId = poll.pollId;
    if (poll.status === 'CLOSED') card.classList.add('poll-closed');
    if (poll.status === 'CANCELLED') card.classList.add('poll-cancelled');

    // 头部
    const header = document.createElement('div');
    header.className = 'poll-header';

    const title = document.createElement('div');
    title.className = 'poll-title';
    title.textContent = poll.question;
    header.appendChild(title);

    // 状态标签
    const statusBadge = document.createElement('span');
    statusBadge.className = 'poll-status-badge';
    statusBadge.textContent = getStatusLabel(poll.status);
    statusBadge.style.backgroundColor = getStatusColor(poll.status);
    header.appendChild(statusBadge);

    // 标签（匿名/多选）
    if (poll.anonymous) {
      const tag = document.createElement('span');
      tag.className = 'poll-tag';
      tag.textContent = '🔒 匿名';
      header.appendChild(tag);
    }
    if (poll.multiSelect) {
      const tag = document.createElement('span');
      tag.className = 'poll-tag';
      tag.textContent = '☑️ 多选';
      header.appendChild(tag);
    }

    card.appendChild(header);

    // 统计栏
    const statsBar = document.createElement('div');
    statsBar.className = 'poll-stats-bar';
    statsBar.textContent = `${poll.totalVotes} 票 · ${poll.totalParticipants} 人参与`;
    card.appendChild(statsBar);

    // 截止时间
    if (poll.deadline && poll.remainingSeconds && poll.remainingSeconds > 0) {
      const deadline = document.createElement('div');
      deadline.className = 'poll-deadline';
      deadline.textContent = `⏰ ${formatRemainingTime(poll.remainingSeconds)}`;
      card.appendChild(deadline);
    }

    // 选项列表
    const optionsContainer = document.createElement('div');
    optionsContainer.className = 'poll-options';
    poll.options.forEach(option => {
      const optionRow = new PollOptionRow({
        option,
        poll,
        isSelected: this.selectedOptions.has(option.optionId),
        isCreator,
        onToggle: (optionId) => this.toggleOption(optionId),
      });
      optionRow.mount(optionsContainer);
      this.optionRows.set(option.optionId, optionRow);
    });
    card.appendChild(optionsContainer);

    // 操作栏
    const actions = document.createElement('div');
    actions.className = 'poll-actions';

    // 投票按钮
    if (canVote) {
      const voteBtn = document.createElement('button');
      voteBtn.className = 'poll-btn poll-btn-vote';
      voteBtn.textContent = '投票';
      voteBtn.disabled = this.selectedOptions.size === 0;
      voteBtn.addEventListener('click', () => this.submitVote());
      actions.appendChild(voteBtn);

      const cancelBtn = document.createElement('button');
      cancelBtn.className = 'poll-btn poll-btn-cancel';
      cancelBtn.textContent = '取消投票';
      cancelBtn.addEventListener('click', () => this.cancelVote());
      actions.appendChild(cancelBtn);
    }

    // 创建者操作
    if (isCreator && isActive) {
      // 添加选项
      const addOptionContainer = document.createElement('div');
      addOptionContainer.className = 'poll-add-option';
      this.addOptionInput = document.createElement('input');
      this.addOptionInput.type = 'text';
      this.addOptionInput.placeholder = '添加新选项...';
      this.addOptionInput.maxLength = 200;
      addOptionContainer.appendChild(this.addOptionInput);

      const addBtn = document.createElement('button');
      addBtn.className = 'poll-btn poll-btn-add';
      addBtn.textContent = '添加';
      addBtn.addEventListener('click', () => this.addOption());
      addOptionContainer.appendChild(addBtn);
      actions.appendChild(addOptionContainer);

      // 结束投票
      const closeBtn = document.createElement('button');
      closeBtn.className = 'poll-btn poll-btn-close';
      closeBtn.textContent = '结束投票';
      closeBtn.addEventListener('click', () => this.closePoll());
      actions.appendChild(closeBtn);

      // 删除投票
      const deleteBtn = document.createElement('button');
      deleteBtn.className = 'poll-btn poll-btn-delete';
      deleteBtn.textContent = '删除';
      deleteBtn.addEventListener('click', () => this.deletePoll());
      actions.appendChild(deleteBtn);
    }

    // 创建者信息
    const creator = document.createElement('div');
    creator.className = 'poll-creator';
    creator.textContent = `由 ${poll.creatorId} 创建`;
    actions.appendChild(creator);

    card.appendChild(actions);

    // 点击整个卡片
    card.addEventListener('click', (e) => {
      if ((e.target as HTMLElement).closest('.poll-actions')) return;
      if (this.props.onClick) {
        this.props.onClick(poll.pollId);
      }
    });

    this.element = card;
    container.appendChild(card);
    return card;
  }

  private toggleOption(optionId: string): void {
    if (!this.props.poll.multiSelect) {
      // 单选：替换选择
      this.selectedOptions.clear();
      if (!this.selectedOptions.has(optionId)) {
        this.selectedOptions.add(optionId);
      }
    } else {
      // 多选：切换选择
      if (this.selectedOptions.has(optionId)) {
        this.selectedOptions.delete(optionId);
      } else {
        this.selectedOptions.add(optionId);
      }
    }
    this.updateOptions();
  }

  private updateOptions(): void {
    const { poll } = this.props;
    const isActive = poll.status === 'ACTIVE';
    poll.options.forEach(option => {
      const row = this.optionRows.get(option.optionId);
      if (row) {
        row.update({
          option,
          poll,
          isSelected: this.selectedOptions.has(option.optionId),
          isCreator: poll.creatorId === this.props.currentUserId,
          onToggle: (optionId) => this.toggleOption(optionId),
        });
      }
    });
  }

  private submitVote(): void {
    if (this.selectedOptions.size === 0) return;
    if (this.props.onVote) {
      this.props.onVote(this.props.poll.pollId, Array.from(this.selectedOptions));
    }
  }

  private cancelVote(): void {
    if (this.props.onCancelVote) {
      this.props.onCancelVote(this.props.poll.pollId);
    }
  }

  private closePoll(): void {
    if (this.props.onClose) {
      this.props.onClose(this.props.poll.pollId);
    }
  }

  private deletePoll(): void {
    if (this.props.onDelete) {
      this.props.onDelete(this.props.poll.pollId);
    }
  }

  private addOption(): void {
    if (!this.addOptionInput) return;
    const text = this.addOptionInput.value.trim();
    if (!text) return;
    if (this.props.onAddOption) {
      this.props.onAddOption(this.props.poll.pollId, text);
    }
    this.addOptionInput.value = '';
  }

  update(props: PollCardProps): void {
    this.props = props;
    if (this.element) {
      const parent = this.element.parentElement;
      if (parent) {
        this.element.remove();
        this.optionRows.clear();
        this.mount(parent);
      }
    }
  }

  destroy(): void {
    this.optionRows.forEach(row => row.destroy());
    this.optionRows.clear();
    if (this.element) {
      this.element.remove();
      this.element = null;
    }
  }
}

// ==================== 创建投票对话框 ====================

export interface CreatePollDialogProps {
  groupId: string;
  onSubmit: (data: {
    question: string;
    options: string[];
    anonymous: boolean;
    multiSelect: boolean;
    deadlineMinutes?: number;
  }) => void;
  onCancel: () => void;
}

export class CreatePollDialog {
  private props: CreatePollDialogProps;
  private element: HTMLElement | null = null;
  private options: string[] = ['', ''];
  private optionInputs: HTMLInputElement[] = [];

  constructor(props: CreatePollDialogProps) {
    this.props = props;
  }

  mount(container: HTMLElement): HTMLElement {
    const overlay = document.createElement('div');
    overlay.className = 'poll-dialog-overlay';

    const dialog = document.createElement('div');
    dialog.className = 'poll-dialog';

    // 标题
    const title = document.createElement('h3');
    title.className = 'poll-dialog-title';
    title.textContent = '创建投票';
    dialog.appendChild(title);

    // 问题输入
    const qLabel = document.createElement('label');
    qLabel.className = 'poll-dialog-label';
    qLabel.textContent = '投票问题';
    dialog.appendChild(qLabel);

    const qInput = document.createElement('input');
    qInput.type = 'text';
    qInput.className = 'poll-dialog-input';
    qInput.placeholder = '例如：周末去哪里聚餐？';
    qInput.maxLength = 500;
    dialog.appendChild(qInput);

    // 选项输入
    const optLabel = document.createElement('label');
    optLabel.className = 'poll-dialog-label';
    optLabel.textContent = '投票选项（2-10个）';
    dialog.appendChild(optLabel);

    const optionsContainer = document.createElement('div');
    optionsContainer.className = 'poll-dialog-options';
    this.renderOptionInputs(optionsContainer);
    dialog.appendChild(optionsContainer);

    // 添加选项按钮
    const addOptBtn = document.createElement('button');
    addOptBtn.className = 'poll-dialog-btn-add-option';
    addOptBtn.textContent = '+ 添加选项';
    addOptBtn.addEventListener('click', () => {
      if (this.options.length < 10) {
        this.options.push('');
        this.renderOptionInputs(optionsContainer);
      }
    });
    dialog.appendChild(addOptBtn);

    // 设置区
    const settings = document.createElement('div');
    settings.className = 'poll-dialog-settings';

    const anonLabel = document.createElement('label');
    anonLabel.className = 'poll-dialog-check';
    const anonCheck = document.createElement('input');
    anonCheck.type = 'checkbox';
    anonLabel.appendChild(anonCheck);
    anonLabel.appendChild(document.createTextNode(' 匿名投票（隐藏投票者）'));
    settings.appendChild(anonLabel);

    const multiLabel = document.createElement('label');
    multiLabel.className = 'poll-dialog-check';
    const multiCheck = document.createElement('input');
    multiCheck.type = 'checkbox';
    multiLabel.appendChild(multiCheck);
    multiLabel.appendChild(document.createTextNode(' 多选'));
    settings.appendChild(multiLabel);

    dialog.appendChild(settings);

    // 截止时间
    const deadlineLabel = document.createElement('label');
    deadlineLabel.className = 'poll-dialog-label';
    deadlineLabel.textContent = '截止时间（可选）';
    dialog.appendChild(deadlineLabel);

    const deadlineSelect = document.createElement('select');
    deadlineSelect.className = 'poll-dialog-select';
    deadlineSelect.innerHTML = `
      <option value="">无截止</option>
      <option value="60">1小时后</option>
      <option value="360">6小时后</option>
      <option value="1440">24小时后</option>
      <option value="10080">7天后</option>
    `;
    dialog.appendChild(deadlineSelect);

    // 按钮
    const btnRow = document.createElement('div');
    btnRow.className = 'poll-dialog-buttons';

    const cancelBtn = document.createElement('button');
    cancelBtn.className = 'poll-dialog-btn poll-dialog-btn-cancel';
    cancelBtn.textContent = '取消';
    cancelBtn.addEventListener('click', () => this.props.onCancel());
    btnRow.appendChild(cancelBtn);

    const submitBtn = document.createElement('button');
    submitBtn.className = 'poll-dialog-btn poll-dialog-btn-submit';
    submitBtn.textContent = '创建投票';
    submitBtn.addEventListener('click', () => {
      const question = qInput.value.trim();
      const validOptions = this.optionInputs.map(i => i.value.trim()).filter(v => v.length > 0);

      if (!question) {
        alert('请输入投票问题');
        return;
      }
      if (validOptions.length < 2) {
        alert('至少需要2个选项');
        return;
      }

      this.props.onSubmit({
        question,
        options: validOptions,
        anonymous: anonCheck.checked,
        multiSelect: multiCheck.checked,
        deadlineMinutes: deadlineSelect.value ? parseInt(deadlineSelect.value) : undefined,
      });
    });
    btnRow.appendChild(submitBtn);
    dialog.appendChild(btnRow);

    overlay.appendChild(dialog);
    this.element = overlay;
    container.appendChild(overlay);

    // 点击遮罩关闭
    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) this.props.onCancel();
    });

    return overlay;
  }

  private renderOptionInputs(container: HTMLElement): void {
    container.innerHTML = '';
    this.optionInputs = [];
    this.options.forEach((opt, i) => {
      const row = document.createElement('div');
      row.className = 'poll-dialog-option-row';

      const input = document.createElement('input');
      input.type = 'text';
      input.className = 'poll-dialog-input';
      input.placeholder = `选项 ${i + 1}`;
      input.maxLength = 200;
      input.value = opt;
      input.addEventListener('input', (e) => {
        this.options[i] = (e.target as HTMLInputElement).value;
      });
      this.optionInputs.push(input);
      row.appendChild(input);

      if (this.options.length > 2) {
        const removeBtn = document.createElement('button');
        removeBtn.className = 'poll-dialog-btn-remove';
        removeBtn.textContent = '×';
        removeBtn.addEventListener('click', () => {
          this.options.splice(i, 1);
          this.renderOptionInputs(container);
        });
        row.appendChild(removeBtn);
      }

      container.appendChild(row);
    });
  }

  destroy(): void {
    if (this.element) {
      this.element.remove();
      this.element = null;
    }
  }
}
