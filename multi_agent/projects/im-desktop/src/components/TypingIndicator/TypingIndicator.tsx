import React, { useEffect, useState } from 'react';
import './TypingIndicator.css';
import { typingService, TypingUser } from '../../services/typingService';

interface TypingIndicatorProps {
  conversationId: string;
  conversationType: string;
  currentUserId: string;
}

const TypingIndicator: React.FC<TypingIndicatorProps> = ({
  conversationId,
  conversationType,
  currentUserId,
}) => {
  const [typingUsers, setTypingUsers] = useState<TypingUser[]>([]);

  useEffect(() => {
    // 启动时拉一次
    typingService.fetchTypingStatus(conversationId).then(users => {
      setTypingUsers(users.filter(u => u.userId !== currentUserId));
    });

    // 订阅Typing变化
    const unsubscribe = typingService.onTypingChanged(conversationId, (users) => {
      setTypingUsers(users.filter(u => u.userId !== currentUserId));
    });

    return () => unsubscribe();
  }, [conversationId, currentUserId]);

  if (typingUsers.length === 0) return null;

  const displayText = (() => {
    const names = typingUsers.map(u => u.userName || u.userId);
    if (names.length === 1) return `${names[0]} 正在输入...`;
    if (names.length === 2) return `${names[0]} 和 ${names[1]} 正在输入...`;
    return `${names[0]}、${names[1]} 等${names.length}人正在输入...`;
  })();

  return (
    <div className="typing-indicator" role="status" aria-live="polite">
      <div className="typing-dots">
        <span className="dot"></span>
        <span className="dot"></span>
        <span className="dot"></span>
      </div>
      <span className="typing-text">{displayText}</span>
    </div>
  );
};

export default TypingIndicator;
