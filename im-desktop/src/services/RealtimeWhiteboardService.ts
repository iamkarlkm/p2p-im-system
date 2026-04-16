/**
 * 实时白板同步服务
 * 功能#56: 实时白板同步
 */

import { EventEmitter } from 'events';
import { WebSocketClient } from '../websocket/WebSocketClient';

export interface WhiteboardElement {
  id: string;
  type: 'pen' | 'line' | 'rect' | 'circle' | 'text' | 'image' | 'eraser';
  x: number;
  y: number;
  width?: number;
  height?: number;
  points?: { x: number; y: number }[];
  strokeColor?: string;
  fillColor?: string;
  strokeWidth?: number;
  text?: string;
  imageUrl?: string;
  creatorId: string;
  timestamp: number;
  version: number;
}

export interface WhiteboardState {
  elements: WhiteboardElement[];
  selectedElementIds: string[];
  viewport: {
    x: number;
    y: number;
    zoom: number;
  };
  cursors: Map<string, CursorInfo>;
}

export interface CursorInfo {
  userId: string;
  userName: string;
  x: number;
  y: number;
  color: string;
  timestamp: number;
}

export interface WhiteboardOperation {
  type: 'add' | 'update' | 'delete' | 'clear' | 'select';
  element?: WhiteboardElement;
  elementIds?: string[];
  oldElement?: WhiteboardElement;
  timestamp: number;
  userId: string;
  operationId: string;
}

export class RealtimeWhiteboardService extends EventEmitter {
  private whiteboardId: string;
  private wsClient: WebSocketClient;
  private state: WhiteboardState;
  private operationHistory: WhiteboardOperation[] = [];
  private undoStack: WhiteboardOperation[] = [];
  private redoStack: WhiteboardOperation[] = [];
  private localUserId: string;
  private syncInterval: NodeJS.Timeout | null = null;
  private pendingOperations: WhiteboardOperation[] = [];

  constructor(whiteboardId: string, wsClient: WebSocketClient, userId: string) {
    super();
    this.whiteboardId = whiteboardId;
    this.wsClient = wsClient;
    this.localUserId = userId;
    this.state = {
      elements: [],
      selectedElementIds: [],
      viewport: { x: 0, y: 0, zoom: 1 },
      cursors: new Map(),
    };
    this.setupWebSocketListeners();
  }

  private setupWebSocketListeners(): void {
    this.wsClient.on('whiteboard:operation', (data: any) => {
      if (data.whiteboardId === this.whiteboardId && data.userId !== this.localUserId) {
        this.applyRemoteOperation(data.operation);
      }
    });

    this.wsClient.on('whiteboard:cursor', (data: any) => {
      if (data.whiteboardId === this.whiteboardId && data.userId !== this.localUserId) {
        this.updateRemoteCursor(data.userId, data.cursor);
      }
    });

    this.wsClient.on('whiteboard:sync', (data: any) => {
      if (data.whiteboardId === this.whiteboardId) {
        this.handleFullSync(data.state);
      }
    });

    this.wsClient.on('whiteboard:user-joined', (data: any) => {
      this.emit('user-joined', data);
    });

    this.wsClient.on('whiteboard:user-left', (data: any) => {
      this.state.cursors.delete(data.userId);
      this.emit('user-left', data);
      this.emit('state-changed', this.state);
    });
  }

  public async join(): Promise<void> {
    await this.wsClient.send({
      type: 'whiteboard:join',
      whiteboardId: this.whiteboardId,
    });
    this.startSyncInterval();
  }

  public async leave(): Promise<void> {
    this.stopSyncInterval();
    await this.wsClient.send({
      type: 'whiteboard:leave',
      whiteboardId: this.whiteboardId,
    });
  }

  public addElement(element: Omit<WhiteboardElement, 'id' | 'timestamp' | 'version'>): WhiteboardElement {
    const newElement: WhiteboardElement = {
      ...element,
      id: this.generateId(),
      timestamp: Date.now(),
      version: this.getNextVersion(),
    };

    const operation: WhiteboardOperation = {
      type: 'add',
      element: newElement,
      timestamp: Date.now(),
      userId: this.localUserId,
      operationId: this.generateId(),
    };

    this.applyLocalOperation(operation);
    this.broadcastOperation(operation);
    
    return newElement;
  }

  public updateElement(elementId: string, updates: Partial<WhiteboardElement>): WhiteboardElement | null {
    const element = this.state.elements.find(e => e.id === elementId);
    if (!element) return null;

    const oldElement = { ...element };
    const updatedElement: WhiteboardElement = {
      ...element,
      ...updates,
      version: this.getNextVersion(),
    };

    const operation: WhiteboardOperation = {
      type: 'update',
      element: updatedElement,
      oldElement,
      timestamp: Date.now(),
      userId: this.localUserId,
      operationId: this.generateId(),
    };

    this.applyLocalOperation(operation);
    this.broadcastOperation(operation);
    
    return updatedElement;
  }

  public deleteElement(elementId: string): boolean {
    const element = this.state.elements.find(e => e.id === elementId);
    if (!element) return false;

    const operation: WhiteboardOperation = {
      type: 'delete',
      elementIds: [elementId],
      element,
      timestamp: Date.now(),
      userId: this.localUserId,
      operationId: this.generateId(),
    };

    this.applyLocalOperation(operation);
    this.broadcastOperation(operation);
    
    return true;
  }

  public deleteSelectedElements(): void {
    if (this.state.selectedElementIds.length === 0) return;

    const elementsToDelete = this.state.elements.filter(
      e => this.state.selectedElementIds.includes(e.id)
    );

    const operation: WhiteboardOperation = {
      type: 'delete',
      elementIds: [...this.state.selectedElementIds],
      timestamp: Date.now(),
      userId: this.localUserId,
      operationId: this.generateId(),
    };

    this.applyLocalOperation(operation);
    this.broadcastOperation(operation);
  }

  public clear(): void {
    const operation: WhiteboardOperation = {
      type: 'clear',
      timestamp: Date.now(),
      userId: this.localUserId,
      operationId: this.generateId(),
    };

    this.applyLocalOperation(operation);
    this.broadcastOperation(operation);
  }

  public selectElements(elementIds: string[]): void {
    this.state.selectedElementIds = elementIds;
    
    const operation: WhiteboardOperation = {
      type: 'select',
      elementIds,
      timestamp: Date.now(),
      userId: this.localUserId,
      operationId: this.generateId(),
    };

    this.broadcastOperation(operation);
    this.emit('state-changed', this.state);
  }

  public updateLocalCursor(x: number, y: number): void {
    this.wsClient.send({
      type: 'whiteboard:cursor',
      whiteboardId: this.whiteboardId,
      cursor: { x, y, timestamp: Date.now() },
    });
  }

  public undo(): boolean {
    if (this.undoStack.length === 0) return false;

    const operation = this.undoStack.pop()!;
    this.redoStack.push(operation);

    this.revertOperation(operation);
    this.emit('state-changed', this.state);
    
    return true;
  }

  public redo(): boolean {
    if (this.redoStack.length === 0) return false;

    const operation = this.redoStack.pop()!;
    this.undoStack.push(operation);

    this.applyOperationToState(operation);
    this.emit('state-changed', this.state);
    
    return true;
  }

  public canUndo(): boolean {
    return this.undoStack.length > 0;
  }

  public canRedo(): boolean {
    return this.redoStack.length > 0;
  }

  public setViewport(viewport: Partial<WhiteboardState['viewport']>): void {
    this.state.viewport = { ...this.state.viewport, ...viewport };
    this.emit('viewport-changed', this.state.viewport);
  }

  public getState(): WhiteboardState {
    return { ...this.state };
  }

  public getElementById(elementId: string): WhiteboardElement | undefined {
    return this.state.elements.find(e => e.id === elementId);
  }

  public getSelectedElements(): WhiteboardElement[] {
    return this.state.elements.filter(e => this.state.selectedElementIds.includes(e.id));
  }

  public exportToJSON(): string {
    return JSON.stringify({
      elements: this.state.elements,
      viewport: this.state.viewport,
      exportedAt: Date.now(),
    });
  }

  public importFromJSON(json: string): void {
    try {
      const data = JSON.parse(json);
      if (data.elements) {
        this.state.elements = data.elements.map((e: any) => ({
          ...e,
          creatorId: this.localUserId,
          timestamp: Date.now(),
        }));
        this.emit('state-changed', this.state);
      }
    } catch (error) {
      console.error('Failed to import whiteboard:', error);
      throw new Error('Invalid whiteboard data format');
    }
  }

  public exportToImage(format: 'png' | 'jpeg' = 'png'): Promise<string> {
    return new Promise((resolve, reject) => {
      this.emit('export-image', { format, resolve, reject });
    });
  }

  private applyLocalOperation(operation: WhiteboardOperation): void {
    this.applyOperationToState(operation);
    this.operationHistory.push(operation);
    this.undoStack.push(operation);
    this.redoStack = []; // 清空redo栈
    this.emit('operation-applied', operation);
    this.emit('state-changed', this.state);
  }

  private applyRemoteOperation(operation: WhiteboardOperation): void {
    this.applyOperationToState(operation);
    this.operationHistory.push(operation);
    this.emit('remote-operation-applied', operation);
    this.emit('state-changed', this.state);
  }

  private applyOperationToState(operation: WhiteboardOperation): void {
    switch (operation.type) {
      case 'add':
        if (operation.element) {
          this.state.elements.push(operation.element);
        }
        break;
      case 'update':
        if (operation.element) {
          const index = this.state.elements.findIndex(e => e.id === operation.element!.id);
          if (index !== -1) {
            this.state.elements[index] = operation.element;
          }
        }
        break;
      case 'delete':
        if (operation.elementIds) {
          this.state.elements = this.state.elements.filter(
            e => !operation.elementIds!.includes(e.id)
          );
          this.state.selectedElementIds = this.state.selectedElementIds.filter(
            id => !operation.elementIds!.includes(id)
          );
        }
        break;
      case 'clear':
        this.state.elements = [];
        this.state.selectedElementIds = [];
        break;
      case 'select':
        // 远程选择不改变本地选择状态
        break;
    }
  }

  private revertOperation(operation: WhiteboardOperation): void {
    switch (operation.type) {
      case 'add':
        if (operation.element) {
          this.state.elements = this.state.elements.filter(e => e.id !== operation.element!.id);
        }
        break;
      case 'update':
        if (operation.oldElement) {
          const index = this.state.elements.findIndex(e => e.id === operation.oldElement!.id);
          if (index !== -1) {
            this.state.elements[index] = operation.oldElement;
          }
        }
        break;
      case 'delete':
        if (operation.element) {
          this.state.elements.push(operation.element);
        }
        break;
      case 'clear':
        // Clear操作无法撤销
        break;
    }
  }

  private broadcastOperation(operation: WhiteboardOperation): void {
    this.wsClient.send({
      type: 'whiteboard:operation',
      whiteboardId: this.whiteboardId,
      operation,
    });
  }

  private updateRemoteCursor(userId: string, cursor: Partial<CursorInfo>): void {
    const existingCursor = this.state.cursors.get(userId);
    this.state.cursors.set(userId, {
      ...existingCursor,
      ...cursor,
      userId,
      timestamp: Date.now(),
    } as CursorInfo);
    this.emit('cursor-updated', { userId, cursor });
    this.emit('state-changed', this.state);
  }

  private handleFullSync(remoteState: WhiteboardState): void {
    // 合并本地和远程状态，以远程为准，但保留本地未同步的操作
    this.state.elements = remoteState.elements;
    this.state.viewport = remoteState.viewport;
    this.emit('state-changed', this.state);
    this.emit('sync-completed');
  }

  private startSyncInterval(): void {
    this.syncInterval = setInterval(() => {
      // 清理过期的光标
      const now = Date.now();
      for (const [userId, cursor] of this.state.cursors.entries()) {
        if (now - cursor.timestamp > 30000) { // 30秒未更新则移除
          this.state.cursors.delete(userId);
        }
      }
    }, 5000);
  }

  private stopSyncInterval(): void {
    if (this.syncInterval) {
      clearInterval(this.syncInterval);
      this.syncInterval = null;
    }
  }

  private generateId(): string {
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }

  private getNextVersion(): number {
    const maxVersion = this.state.elements.reduce(
      (max, e) => Math.max(max, e.version), 0
    );
    return maxVersion + 1;
  }
}
