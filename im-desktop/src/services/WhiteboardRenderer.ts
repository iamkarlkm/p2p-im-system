/**
 * 白板渲染引擎
 * 功能#56: 实时白板同步
 */

import { EventEmitter } from 'events';
import { 
  WhiteboardElement, 
  WhiteboardState, 
  CursorInfo,
  RealtimeWhiteboardService 
} from './RealtimeWhiteboardService';

export interface RenderOptions {
  showGrid: boolean;
  gridSize: number;
  showCursors: boolean;
  showSelection: boolean;
  antiAlias: boolean;
}

export class WhiteboardRenderer extends EventEmitter {
  private canvas: HTMLCanvasElement;
  private ctx: CanvasRenderingContext2D;
  private service: RealtimeWhiteboardService;
  private options: RenderOptions;
  private isDrawing: boolean = false;
  private currentTool: string = 'pen';
  private currentStroke: { x: number; y: number }[] = [];
  private lastMousePos: { x: number; y: number } | null = null;

  constructor(
    canvas: HTMLCanvasElement, 
    service: RealtimeWhiteboardService,
    options: Partial<RenderOptions> = {}
  ) {
    super();
    this.canvas = canvas;
    const ctx = canvas.getContext('2d');
    if (!ctx) {
      throw new Error('Failed to get canvas context');
    }
    this.ctx = ctx;
    this.service = service;
    this.options = {
      showGrid: true,
      gridSize: 20,
      showCursors: true,
      showSelection: true,
      antiAlias: true,
      ...options,
    };
    
    this.setupCanvas();
    this.setupEventListeners();
  }

  private setupCanvas(): void {
    // 设置canvas尺寸
    const rect = this.canvas.getBoundingClientRect();
    this.canvas.width = rect.width * window.devicePixelRatio;
    this.canvas.height = rect.height * window.devicePixelRatio;
    this.ctx.scale(window.devicePixelRatio, window.devicePixelRatio);
    
    if (this.options.antiAlias) {
      this.ctx.imageSmoothingEnabled = true;
      this.ctx.imageSmoothingQuality = 'high';
    }
  }

  private setupEventListeners(): void {
    this.service.on('state-changed', () => {
      this.render();
    });

    this.canvas.addEventListener('mousedown', this.handleMouseDown.bind(this));
    this.canvas.addEventListener('mousemove', this.handleMouseMove.bind(this));
    this.canvas.addEventListener('mouseup', this.handleMouseUp.bind(this));
    this.canvas.addEventListener('mouseleave', this.handleMouseLeave.bind(this));
    this.canvas.addEventListener('wheel', this.handleWheel.bind(this));

    // 触摸支持
    this.canvas.addEventListener('touchstart', this.handleTouchStart.bind(this));
    this.canvas.addEventListener('touchmove', this.handleTouchMove.bind(this));
    this.canvas.addEventListener('touchend', this.handleTouchEnd.bind(this));
  }

  public render(): void {
    const state = this.service.getState();
    const { width, height } = this.canvas.getBoundingClientRect();
    
    // 清空画布
    this.ctx.clearRect(0, 0, width, height);
    
    // 保存上下文
    this.ctx.save();
    
    // 应用视口变换
    this.ctx.translate(state.viewport.x, state.viewport.y);
    this.ctx.scale(state.viewport.zoom, state.viewport.zoom);
    
    // 绘制网格
    if (this.options.showGrid) {
      this.drawGrid(width, height);
    }
    
    // 绘制所有元素
    for (const element of state.elements) {
      this.drawElement(element);
    }
    
    // 绘制选中框
    if (this.options.showSelection) {
      for (const elementId of state.selectedElementIds) {
        const element = this.service.getElementById(elementId);
        if (element) {
          this.drawSelectionBox(element);
        }
      }
    }
    
    // 绘制远程光标
    if (this.options.showCursors) {
      for (const [userId, cursor] of state.cursors.entries()) {
        this.drawCursor(cursor);
      }
    }
    
    // 恢复上下文
    this.ctx.restore();
  }

  private drawGrid(width: number, height: number): void {
    const { gridSize } = this.options;
    this.ctx.strokeStyle = '#e0e0e0';
    this.ctx.lineWidth = 1;
    
    for (let x = 0; x < width; x += gridSize) {
      this.ctx.beginPath();
      this.ctx.moveTo(x, 0);
      this.ctx.lineTo(x, height);
      this.ctx.stroke();
    }
    
    for (let y = 0; y < height; y += gridSize) {
      this.ctx.beginPath();
      this.ctx.moveTo(0, y);
      this.ctx.lineTo(width, y);
      this.ctx.stroke();
    }
  }

  private drawElement(element: WhiteboardElement): void {
    this.ctx.save();
    
    switch (element.type) {
      case 'pen':
      case 'eraser':
        this.drawPenElement(element);
        break;
      case 'line':
        this.drawLineElement(element);
        break;
      case 'rect':
        this.drawRectElement(element);
        break;
      case 'circle':
        this.drawCircleElement(element);
        break;
      case 'text':
        this.drawTextElement(element);
        break;
      case 'image':
        this.drawImageElement(element);
        break;
    }
    
    this.ctx.restore();
  }

  private drawPenElement(element: WhiteboardElement): void {
    if (!element.points || element.points.length < 2) return;
    
    this.ctx.beginPath();
    this.ctx.moveTo(element.points[0].x, element.points[0].y);
    
    for (let i = 1; i < element.points.length; i++) {
      this.ctx.lineTo(element.points[i].x, element.points[i].y);
    }
    
    this.ctx.strokeStyle = element.strokeColor || '#000000';
    this.ctx.lineWidth = element.strokeWidth || 2;
    this.ctx.lineCap = 'round';
    this.ctx.lineJoin = 'round';
    this.ctx.stroke();
  }

  private drawLineElement(element: WhiteboardElement): void {
    if (!element.points || element.points.length < 2) return;
    
    this.ctx.beginPath();
    this.ctx.moveTo(element.points[0].x, element.points[0].y);
    this.ctx.lineTo(element.points[1].x, element.points[1].y);
    
    this.ctx.strokeStyle = element.strokeColor || '#000000';
    this.ctx.lineWidth = element.strokeWidth || 2;
    this.ctx.stroke();
  }

  private drawRectElement(element: WhiteboardElement): void {
    if (element.width === undefined || element.height === undefined) return;
    
    this.ctx.fillStyle = element.fillColor || 'transparent';
    this.ctx.fillRect(element.x, element.y, element.width, element.height);
    
    this.ctx.strokeStyle = element.strokeColor || '#000000';
    this.ctx.lineWidth = element.strokeWidth || 2;
    this.ctx.strokeRect(element.x, element.y, element.width, element.height);
  }

  private drawCircleElement(element: WhiteboardElement): void {
    if (element.width === undefined) return;
    
    const radius = element.width / 2;
    this.ctx.beginPath();
    this.ctx.arc(element.x + radius, element.y + radius, radius, 0, Math.PI * 2);
    
    this.ctx.fillStyle = element.fillColor || 'transparent';
    this.ctx.fill();
    
    this.ctx.strokeStyle = element.strokeColor || '#000000';
    this.ctx.lineWidth = element.strokeWidth || 2;
    this.ctx.stroke();
  }

  private drawTextElement(element: WhiteboardElement): void {
    if (!element.text) return;
    
    this.ctx.font = `${element.strokeWidth || 16}px Arial`;
    this.ctx.fillStyle = element.strokeColor || '#000000';
    this.ctx.fillText(element.text, element.x, element.y + (element.strokeWidth || 16));
  }

  private drawImageElement(element: WhiteboardElement): void {
    // 图片绘制需要预加载，简化实现
    if (!element.imageUrl) return;
    
    const img = new Image();
    img.onload = () => {
      this.ctx.drawImage(
        img, 
        element.x, 
        element.y, 
        element.width || img.width, 
        element.height || img.height
      );
    };
    img.src = element.imageUrl;
  }

  private drawSelectionBox(element: WhiteboardElement): void {
    let x = element.x;
    let y = element.y;
    let width = element.width || 100;
    let height = element.height || 100;
    
    if (element.type === 'pen' && element.points) {
      const xs = element.points.map(p => p.x);
      const ys = element.points.map(p => p.y);
      x = Math.min(...xs);
      y = Math.min(...ys);
      width = Math.max(...xs) - x;
      height = Math.max(...ys) - y;
    }
    
    this.ctx.strokeStyle = '#0066ff';
    this.ctx.lineWidth = 1;
    this.ctx.setLineDash([5, 5]);
    this.ctx.strokeRect(x - 5, y - 5, width + 10, height + 10);
    this.ctx.setLineDash([]);
    
    // 绘制控制点
    this.ctx.fillStyle = '#0066ff';
    const handleSize = 8;
    const handles = [
      { x: x - 5, y: y - 5 },
      { x: x + width / 2, y: y - 5 },
      { x: x + width + 5 - handleSize, y: y - 5 },
      { x: x - 5, y: y + height / 2 },
      { x: x + width + 5 - handleSize, y: y + height / 2 },
      { x: x - 5, y: y + height + 5 - handleSize },
      { x: x + width / 2, y: y + height + 5 - handleSize },
      { x: x + width + 5 - handleSize, y: y + height + 5 - handleSize },
    ];
    
    for (const handle of handles) {
      this.ctx.fillRect(handle.x, handle.y, handleSize, handleSize);
    }
  }

  private drawCursor(cursor: CursorInfo): void {
    // 绘制光标
    this.ctx.fillStyle = cursor.color;
    this.ctx.beginPath();
    this.ctx.moveTo(cursor.x, cursor.y);
    this.ctx.lineTo(cursor.x + 12, cursor.y + 8);
    this.ctx.lineTo(cursor.x + 5, cursor.y + 12);
    this.ctx.closePath();
    this.ctx.fill();
    
    // 绘制用户名标签
    this.ctx.fillStyle = cursor.color;
    this.ctx.font = '12px Arial';
    this.ctx.fillText(cursor.userName, cursor.x + 15, cursor.y + 15);
  }

  private handleMouseDown(e: MouseEvent): void {
    const pos = this.getMousePosition(e);
    this.isDrawing = true;
    this.currentStroke = [pos];
    this.lastMousePos = pos;
    
    this.emit('draw-start', { position: pos, tool: this.currentTool });
  }

  private handleMouseMove(e: MouseEvent): void {
    const pos = this.getMousePosition(e);
    
    // 发送光标位置
    this.service.updateLocalCursor(pos.x, pos.y);
    
    if (!this.isDrawing || !this.lastMousePos) return;
    
    this.currentStroke.push(pos);
    
    if (this.currentTool === 'pen' || this.currentTool === 'eraser') {
      this.render();
      
      // 绘制当前笔画
      this.ctx.save();
      this.ctx.beginPath();
      this.ctx.moveTo(this.lastMousePos.x, this.lastMousePos.y);
      this.ctx.lineTo(pos.x, pos.y);
      this.ctx.strokeStyle = this.currentTool === 'eraser' ? '#ffffff' : '#000000';
      this.ctx.lineWidth = this.currentTool === 'eraser' ? 20 : 2;
      this.ctx.lineCap = 'round';
      this.ctx.stroke();
      this.ctx.restore();
    }
    
    this.lastMousePos = pos;
    this.emit('draw-move', { position: pos });
  }

  private handleMouseUp(e: MouseEvent): void {
    if (!this.isDrawing) return;
    
    this.isDrawing = false;
    
    if (this.currentStroke.length > 1) {
      // 保存笔画
      this.service.addElement({
        type: this.currentTool === 'eraser' ? 'eraser' : 'pen',
        x: this.currentStroke[0].x,
        y: this.currentStroke[0].y,
        points: [...this.currentStroke],
        strokeColor: '#000000',
        strokeWidth: this.currentTool === 'eraser' ? 20 : 2,
        creatorId: '', // 由service填充
      });
    }
    
    this.currentStroke = [];
    this.lastMousePos = null;
    this.emit('draw-end');
  }

  private handleMouseLeave(): void {
    if (this.isDrawing) {
      this.handleMouseUp(new MouseEvent('mouseup'));
    }
  }

  private handleWheel(e: WheelEvent): void {
    e.preventDefault();
    
    const zoomFactor = e.deltaY > 0 ? 0.9 : 1.1;
    const state = this.service.getState();
    const newZoom = Math.max(0.1, Math.min(5, state.viewport.zoom * zoomFactor));
    
    this.service.setViewport({ zoom: newZoom });
  }

  private handleTouchStart(e: TouchEvent): void {
    e.preventDefault();
    const touch = e.touches[0];
    const mouseEvent = new MouseEvent('mousedown', {
      clientX: touch.clientX,
      clientY: touch.clientY,
    });
    this.handleMouseDown(mouseEvent);
  }

  private handleTouchMove(e: TouchEvent): void {
    e.preventDefault();
    const touch = e.touches[0];
    const mouseEvent = new MouseEvent('mousemove', {
      clientX: touch.clientX,
      clientY: touch.clientY,
    });
    this.handleMouseMove(mouseEvent);
  }

  private handleTouchEnd(e: TouchEvent): void {
    e.preventDefault();
    this.handleMouseUp(new MouseEvent('mouseup'));
  }

  private getMousePosition(e: MouseEvent): { x: number; y: number } {
    const rect = this.canvas.getBoundingClientRect();
    const state = this.service.getState();
    
    return {
      x: (e.clientX - rect.left - state.viewport.x) / state.viewport.zoom,
      y: (e.clientY - rect.top - state.viewport.y) / state.viewport.zoom,
    };
  }

  public setTool(tool: string): void {
    this.currentTool = tool;
    this.emit('tool-changed', tool);
  }

  public getTool(): string {
    return this.currentTool;
  }

  public setOptions(options: Partial<RenderOptions>): void {
    this.options = { ...this.options, ...options };
    this.render();
  }

  public destroy(): void {
    this.stopSyncInterval?.();
    this.removeAllListeners();
  }
}
