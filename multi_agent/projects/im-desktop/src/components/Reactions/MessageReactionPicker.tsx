import React, { useState } from 'react';
import { reactionService, QUICK_EMOJIS, type Reaction, type ReactionDTO } from '../../services/reactionService';
import './MessageReactionPicker.css';

interface Props {
  messageId: number;
  currentUserId: number;
  initialReactions?: Reaction[];
  onReactionsChanged?: (dto: ReactionDTO) => void;
}

const MessageReactionPicker: React.FC<Props> = ({ messageId, currentUserId, initialReactions = [], onReactionsChanged }) => {
  const [reactions, setReactions] = useState<Reaction[]>(initialReactions);
  const [showPicker, setShowPicker] = useState(false);

  const handleReact = async (emoji: string) => {
    const dto = await reactionService.toggleReaction(messageId, emoji, currentUserId);
    setReactions(dto.reactions);
    onReactionsChanged?.(dto);
    setShowPicker(false);
  };

  const totalCount = reactions.reduce((sum, r) => sum + r.count, 0);

  return (
    <div className="reaction-picker-wrapper">
      {totalCount > 0 && (
        <div className="reaction-bar">
          {reactions.filter(r => r.count > 0).map(r => (
            <button
              key={r.emoji}
              className={`reaction-chip ${r.userReacted ? 'reacted' : ''}`}
              onClick={() => handleReact(r.emoji)}
              title={`${r.emoji} ${r.count}人`}
            >
              <span className="reaction-emoji">{r.emoji}</span>
              <span className="reaction-count">{r.count}</span>
            </button>
          ))}
        </div>
      )}
      <button
        className="reaction-add-btn"
        onClick={(e) => { e.stopPropagation(); setShowPicker(!showPicker); }}
        title="添加反应"
      >
        +😃
      </button>
      {showPicker && (
        <div className="emoji-picker-popup" onClick={e => e.stopPropagation()}>
          <div className="emoji-grid">
            {QUICK_EMOJIS.map(emoji => (
              <button key={emoji} className="emoji-btn" onClick={() => handleReact(emoji)}>
                {emoji}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default MessageReactionPicker;
