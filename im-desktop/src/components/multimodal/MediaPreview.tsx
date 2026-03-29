import React, { useState, useEffect, useRef, useCallback } from 'react';
import { MessageAttachment, ProcessingStatus } from '../types/multimodal';

interface MediaPreviewProps {
  attachments: MessageAttachment[];
  onRemove?: (attachmentId: string) => void;
  onPreview?: (attachment: MessageAttachment) => void;
  processingStatus?: ProcessingStatus;
  className?: string;
}

interface ImagePreviewState {
  loaded: boolean;
  error: boolean;
  dimensions?: { width: number; height: number };
}

/**
 * 媒体预览组件
 * 支持图像、音频、视频和文件的预览
 */
export const MediaPreview: React.FC<MediaPreviewProps> = ({
  attachments,
  onRemove,
  onPreview,
  processingStatus,
  className = ''
}) => {
  const [imageStates, setImageStates] = useState<Map<string, ImagePreviewState>>(new Map());
  const [playingAudio, setPlayingAudio] = useState<string | null>(null);
  const audioRefs = useRef<Map<string, HTMLAudioElement>>(new Map());

  // 处理图像加载
  const handleImageLoad = useCallback((attachmentId: string, img: HTMLImageElement) => {
    setImageStates(prev => new Map(prev.set(attachmentId, {
      loaded: true,
      error: false,
      dimensions: { width: img.naturalWidth, height: img.naturalHeight }
    })));
  }, []);

  // 处理图像错误
  const handleImageError = useCallback((attachmentId: string) => {
    setImageStates(prev => new Map(prev.set(attachmentId, {
      loaded: false,
      error: true
    })));
  }, []);

  // 播放音频
  const playAudio = useCallback((attachmentId: string, url: string) => {
    // 停止当前播放
    if (playingAudio && playingAudio !== attachmentId) {
      const currentAudio = audioRefs.current.get(playingAudio);
      if (currentAudio) {
        currentAudio.pause();
        currentAudio.currentTime = 0;
      }
    }

    let audio = audioRefs.current.get(attachmentId);
    if (!audio) {
      audio = new Audio(url);
      audioRefs.current.set(attachmentId, audio);
      
      audio.onended = () => setPlayingAudio(null);
      audio.onerror = () => {
        setPlayingAudio(null);
        console.error('音频播放失败:', attachmentId);
      };
    }

    if (playingAudio === attachmentId) {
      audio.pause();
      setPlayingAudio(null);
    } else {
      audio.play().catch(err => {
        console.error('音频播放失败:', err);
        setPlayingAudio(null);
      });
      setPlayingAudio(attachmentId);
    }
  }, [playingAudio]);

  // 格式化文件大小
  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  // 格式化时长
  const formatDuration = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  // 获取文件图标
  const getFileIcon = (mimeType: string): string => {
    if (mimeType.startsWith('image/')) return '🖼️';
    if (mimeType.startsWith('audio/')) return '🎵';
    if (mimeType.startsWith('video/')) return '🎬';
    if (mimeType.includes('pdf')) return '📄';
    if (mimeType.includes('doc')) return '📝';
    if (mimeType.includes('xls')) return '📊';
    if (mimeType.includes('zip') || mimeType.includes('rar')) return '📦';
    return '📎';
  };

  // 渲染图像预览
  const renderImagePreview = (attachment: MessageAttachment) => {
    const state = imageStates.get(attachment.id);
    const isLoading = processingStatus?.messageId === attachment.id && 
                     processingStatus.progress < 100;

    return (
      <div 
        key={attachment.id}
        className="media-preview-item image-preview"
        onClick={() => onPreview?.(attachment)}
      >
        {isLoading && (
          <div className="upload-progress">
            <div 
              className="progress-bar"
              style={{ width: `${processingStatus?.progress || 0}%` }}
            />
            <span className="progress-text">{processingStatus?.progress || 0}%</span>
          </div>
        )}
        
        {!state?.error ? (
          <img
            src={attachment.thumbnail || attachment.url}
            alt={attachment.name}
            onLoad={(e) => handleImageLoad(attachment.id, e.currentTarget)}
            onError={() => handleImageError(attachment.id)}
            className={state?.loaded ? 'loaded' : 'loading'}
          />
        ) : (
          <div className="preview-error">
            <span>❌</span>
            <span>加载失败</span>
          </div>
        )}

        {state?.dimensions && (
          <div className="image-dimensions">
            {state.dimensions.width} × {state.dimensions.height}
          </div>
        )}

        {onRemove && (
          <button 
            className="remove-btn"
            onClick={(e) => {
              e.stopPropagation();
              onRemove(attachment.id);
            }}
          >
            ✕
          </button>
        )}
      </div>
    );
  };

  // 渲染音频预览
  const renderAudioPreview = (attachment: MessageAttachment) => {
    const isPlaying = playingAudio === attachment.id;
    const waveform = attachment.waveform || [];

    return (
      <div key={attachment.id} className="media-preview-item audio-preview">
        <div className="audio-player">
          <button 
            className={`play-btn ${isPlaying ? 'playing' : ''}`}
            onClick={() => playAudio(attachment.id, attachment.url)}
          >
            {isPlaying ? '⏸' : '▶'}
          </button>
          
          <div className="waveform">
            {waveform.length > 0 ? (
              waveform.map((height, index) => (
                <div
                  key={index}
                  className="waveform-bar"
                  style={{ height: `${height}%` }}
                />
              ))
            ) : (
              <div className="waveform-placeholder">
                <div className="bar" /><div className="bar" /><div className="bar" />
                <div className="bar" /><div className="bar" /><div className="bar" />
                <div className="bar" /><div className="bar" /><div className="bar" />
              </div>
            )}
          </div>

          {attachment.duration && (
            <span className="duration">{formatDuration(attachment.duration)}</span>
          )}
        </div>

        <div className="file-info">
          <span className="file-icon">🎵</span>
          <span className="file-name" title={attachment.name}>{attachment.name}</span>
          <span className="file-size">{formatFileSize(attachment.size)}</span>
        </div>

        {onRemove && (
          <button 
            className="remove-btn"
            onClick={() => onRemove(attachment.id)}
          >
            ✕
          </button>
        )}
      </div>
    );
  };

  // 渲染视频预览
  const renderVideoPreview = (attachment: MessageAttachment) => {
    return (
      <div 
        key={attachment.id} 
        className="media-preview-item video-preview"
        onClick={() => onPreview?.(attachment)}
      >
        <div className="video-thumbnail">
          {attachment.thumbnail ? (
            <img src={attachment.thumbnail} alt={attachment.name} />
          ) : (
            <div className="video-placeholder">
              <span>🎬</span>
            </div>
          )}
          <div className="play-overlay">
            <span className="play-icon">▶</span>
          </div>
          {attachment.duration && (
            <span className="video-duration">{formatDuration(attachment.duration)}</span>
          )}
        </div>

        <div className="file-info">
          <span className="file-name" title={attachment.name}>{attachment.name}</span>
          <span className="file-size">{formatFileSize(attachment.size)}</span>
        </div>

        {onRemove && (
          <button 
            className="remove-btn"
            onClick={(e) => {
              e.stopPropagation();
              onRemove(attachment.id);
            }}
          >
            ✕
          </button>
        )}
      </div>
    );
  };

  // 渲染文件预览
  const renderFilePreview = (attachment: MessageAttachment) => {
    return (
      <div key={attachment.id} className="media-preview-item file-preview">
        <div className="file-icon-large">{getFileIcon(attachment.mimeType)}</div>
        
        <div className="file-details">
          <span className="file-name" title={attachment.name}>{attachment.name}</span>
          <span className="file-size">{formatFileSize(attachment.size)}</span>
        </div>

        {onRemove && (
          <button 
            className="remove-btn"
            onClick={() => onRemove(attachment.id)}
          >
            ✕
          </button>
        )}
      </div>
    );
  };

  // 渲染单个附件
  const renderAttachment = (attachment: MessageAttachment) => {
    if (attachment.mimeType?.startsWith('image/')) {
      return renderImagePreview(attachment);
    }
    if (attachment.mimeType?.startsWith('audio/')) {
      return renderAudioPreview(attachment);
    }
    if (attachment.mimeType?.startsWith('video/')) {
      return renderVideoPreview(attachment);
    }
    return renderFilePreview(attachment);
  };

  // 清理音频资源
  useEffect(() => {
    return () => {
      audioRefs.current.forEach(audio => {
        audio.pause();
        audio.src = '';
      });
      audioRefs.current.clear();
    };
  }, []);

  if (attachments.length === 0) return null;

  return (
    <div className={`media-preview-container ${className}`}>
      <div className="media-preview-grid">
        {attachments.map(renderAttachment)}
      </div>
    </div>
  );
};

export default MediaPreview;
