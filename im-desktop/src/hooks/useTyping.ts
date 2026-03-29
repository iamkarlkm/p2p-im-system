import { useEffect, useRef, useCallback } from 'react';
import { typingService } from '../services/typingService';

interface UseTypingOptions {
  conversationId: string;
  conversationType: string;
  enabled?: boolean;
}

export function useTypingInput({ conversationId, conversationType, enabled = true }: UseTypingOptions) {
  const inputRef = useRef<HTMLInputElement | HTMLTextAreaElement>(null);

  const handleInput = useCallback(() => {
    if (!enabled || !conversationId) return;
    typingService.startTyping(conversationId, conversationType);
  }, [conversationId, conversationType, enabled]);

  const handleBlur = useCallback(() => {
    if (!enabled || !conversationId) return;
    typingService.stopTyping(conversationId, conversationType);
  }, [conversationId, conversationType, enabled]);

  useEffect(() => {
    const el = inputRef.current;
    if (!el || !enabled) return;

    el.addEventListener('input', handleInput);
    el.addEventListener('blur', handleBlur);

    return () => {
      el.removeEventListener('input', handleInput);
      el.removeEventListener('blur', handleBlur);
      // 组件卸载时也停止typing
      if (conversationId) {
        typingService.stopTyping(conversationId, conversationType);
      }
    };
  }, [handleInput, handleBlur, enabled, conversationId, conversationType]);

  return { inputRef };
}
