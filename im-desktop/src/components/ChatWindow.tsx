import React from 'react';

interface ChatWindowProps {
  messages: string[];
  currentUser: string;
}

export const ChatWindow: React.FC<ChatWindowProps> = ({ messages, currentUser }) => {
  return (
    <div className="chat-window">
      <div className="message-list">
        {messages.map((msg, index) => (
          <div key={index} className="message">{msg}</div>
        ))}
      </div>
    </div>
  );
};
