export interface Note {
  id: number;
  userId: string;
  conversationId: string;
  content: string;
  quotedMessageId?: string;
  quotedMessageContent?: string;
  tags: TagInfo[];
  createdAt: string;
  updatedAt: string;
}

export interface TagInfo {
  id: number;
  name: string;
  color: string;
}

export interface NotePage {
  items: Note[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

export interface NoteRequest {
  id?: number;
  conversationId: string;
  content: string;
  quotedMessageId?: string;
  quotedMessageContent?: string;
  tagIds?: number[];
}
