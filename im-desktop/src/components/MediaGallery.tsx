/**
 * 媒体库组件 - 对话共同媒体库 UI (Desktop)
 * 聊天中的图片/视频/文件汇总展示
 */

import React, { useState, useEffect } from 'react';
import { mediaLibraryService, MediaFile, MediaLibraryResponse } from '../services/mediaLibrary';
import './MediaGallery.css';

interface MediaGalleryProps {
  conversationId: string;
  onClose?: () => void;
}

type TabType = 'all' | 'image' | 'video' | 'document';

export const MediaGallery: React.FC<MediaGalleryProps> = ({ conversationId, onClose }) => {
  const [activeTab, setActiveTab] = useState<TabType>('all');
  const [media, setMedia] = useState<MediaFile[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [selectedFile, setSelectedFile] = useState<MediaFile | null>(null);
  const [previewOpen, setPreviewOpen] = useState(false);

  useEffect(() => {
    loadMedia();
  }, [conversationId, activeTab]);

  const loadMedia = async (pageNum: number = 0) => {
    setLoading(true);
    try {
      let response: MediaLibraryResponse;
      switch (activeTab) {
        case 'image':
          response = await mediaLibraryService.getImages(conversationId, pageNum);
          break;
        case 'video':
          response = await mediaLibraryService.getVideos(conversationId, pageNum);
          break;
        case 'document':
          response = await mediaLibraryService.getDocuments(conversationId, pageNum);
          break;
        default:
          response = await mediaLibraryService.getConversationMedia(conversationId, pageNum);
      }
      
      if (pageNum === 0) {
        setMedia(response.content);
      } else {
        setMedia(prev => [...prev, ...response.content]);
      }
      setHasMore(response.number < response.totalPages - 1);
      setPage(pageNum);
    } catch (error) {
      console.error('加载媒体失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const target = e.target as HTMLDivElement;
    if (target.scrollHeight - target.scrollTop - target.clientHeight < 100 && hasMore && !loading) {
      loadMedia(page + 1);
    }
  };

  const openPreview = (file: MediaFile) => {
    setSelectedFile(file);
    setPreviewOpen(true);
  };

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' });
  };

  const tabs: { key: TabType; label: string; icon: string }[] = [
    { key: 'all', label: '全部', icon: '📁' },
    { key: 'image', label: '图片', icon: '🖼️' },
    { key: 'video', label: '视频', icon: '🎬' },
    { key: 'document', label: '文件', icon: '📄' }
  ];

  return (
    <div className="media-gallery">
      <div className="media-gallery-header">
        <h3>媒体库</h3>
        <button className="close-btn" onClick={onClose}>×</button>
      </div>

      <div className="media-gallery-tabs">
        {tabs.map(tab => (
          <button
            key={tab.key}
            className={`tab-btn ${activeTab === tab.key ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.key)}
          >
            <span className="tab-icon">{tab.icon}</span>
            <span className="tab-label">{tab.label}</span>
          </button>
        ))}
      </div>

      <div className="media-gallery-content" onScroll={handleScroll}>
        {media.length === 0 && !loading ? (
          <div className="empty-state">
            <span className="empty-icon">📭</span>
            <p>暂无媒体文件</p>
          </div>
        ) : (
          <div className="media-grid">
            {media.map(file => (
              <div key={file.fileId} className="media-item" onClick={() => openPreview(file)}>
                {file.fileType === 'image' ? (
                  <img src={file.thumbnailUrl || file.downloadUrl} alt={file.fileName} />
                ) : (
                  <div className="file-preview">
                    <span className="file-icon">
                      {mediaLibraryService.getFileTypeIcon(file.fileType)}
                    </span>
                    <span className="file-name">{file.fileName}</span>
                  </div>
                )}
                <div className="media-item-overlay">
                  <span className="media-size">{mediaLibraryService.formatFileSize(file.fileSize)}</span>
                  <span className="media-date">{formatDate(file.uploadTime)}</span>
                </div>
              </div>
            ))}
          </div>
        )}
        {loading && <div className="loading">加载中...</div>}
      </div>

      {previewOpen && selectedFile && (
        <div className="media-preview-modal" onClick={() => setPreviewOpen(false)}>
          <div className="preview-content" onClick={e => e.stopPropagation()}>
            <button className="preview-close" onClick={() => setPreviewOpen(false)}>×</button>
            {selectedFile.fileType === 'image' ? (
              <img src={selectedFile.downloadUrl} alt={selectedFile.fileName} />
            ) : (
              <div className="file-info-preview">
                <span className="file-icon-large">
                  {mediaLibraryService.getFileTypeIcon(selectedFile.fileType)}
                </span>
                <h4>{selectedFile.fileName}</h4>
                <p>大小: {mediaLibraryService.formatFileSize(selectedFile.fileSize)}</p>
                <p>类型: {selectedFile.mimeType}</p>
                <p>上传时间: {new Date(selectedFile.uploadTime).toLocaleString()}</p>
                <a href={selectedFile.downloadUrl} download={selectedFile.fileName} className="download-btn">
                  下载文件
                </a>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
