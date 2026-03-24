// Message Reply Chain Types for im-desktop

export interface ReplyChain {
  id: number;
  conversationId: number;
  rootMessageId: number;
  parentMessageId: number;
  userId: number;
  userNickname: string;
  depth: number;
  branchPath: string;
  isBranchNode: boolean;
  isRoot: boolean;
  isLeaf: boolean;
  createdAt: string;
  updatedAt: string;
  branchNodes: ReplyChainNode[];
  context?: MessageContext;
}

export interface ReplyChainNode {
  id: number;
  messageId: number;
  userId: number;
  userNickname: string;
  contentPreview: string;
  messageType: string;
  positionInBranch: number;
  createdAt: string;
}

export interface MessageContext {
  messageId: number;
  content: string;
  senderName: string;
  messageType: string;
  timestamp: string;
  thumbnailUrl?: string;
}

export interface ReplyChainRequest {
  conversationId: number;
  rootMessageId: number;
  parentMessageId: number;
  depth?: number;
  branchPath?: string;
}

export interface BranchTree {
  root: ReplyChain;
  branches: ReplyChain[];
  totalNodes: number;
  maxDepth: number;
}

export interface ReplyChainState {
  activeChain: ReplyChain | null;
  conversationChains: Map<number, ReplyChain[]>;
  branchView: BranchTree | null;
  expandedNodes: Set<number>;
  loading: boolean;
  error: string | null;
}
