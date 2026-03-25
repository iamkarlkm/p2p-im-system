import React, { useState, useCallback } from 'react';
import {
  IconButton,
  Tooltip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  ListItemSecondaryAction,
  Avatar,
  Chip,
  Typography,
  Box,
  TextField,
  InputAdornment,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  OpenInNew as JumpIcon,
  Search as SearchIcon,
  PushPin as PinIcon,
  AccessTime as TimeIcon,
  Close as CloseIcon,
} from '@mui/icons-material';
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { MessageQuoteSidebarService } from '../services/messageQuoteSidebarService';
import { MessageQuoteSidebar } from '../types/messageQuoteSidebar';

interface QuoteJumpButtonProps {
  userId: number;
  sessionId: number;
  onJumpToMessage: (quoteId: number) => void;
  className?: string;
  color?: 'default' | 'primary' | 'secondary';
  size?: 'small' | 'medium' | 'large';
}

/**
 * 引用消息快速跳转按钮组件
 * 点击后弹出对话框，显示所有引用消息，支持快速跳转
 */
const QuoteJumpButton: React.FC<QuoteJumpButtonProps> = ({
  userId,
  sessionId,
  onJumpToMessage,
  className,
  color = 'default',
  size = 'medium',
}) => {
  // 状态管理
  const [open, setOpen] = useState<boolean>(false);
  const [items, setItems] = useState<MessageQuoteSidebar[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');

  // 引用服务
  const sidebarService = MessageQuoteSidebarService.getInstance();

  // 打开对话框时加载数据
  const handleOpen = useCallback(async () => {
    setOpen(true);
    setLoading(true);
    setError(null);

    try {
      const response = await sidebarService.getUserSidebarItems(userId, sessionId);
      if (response.success && response.data) {
        setItems(response.data);
      } else {
        setError(response.message || '加载失败');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '加载失败');
      console.error('Error loading sidebar items:', err);
    } finally {
      setLoading(false);
    }
  }, [userId, sessionId, sidebarService]);

  // 关闭对话框
  const handleClose = () => {
    setOpen(false);
    setSearchQuery('');
    setError(null);
  };

  // 处理跳转
  const handleJump = (item: MessageQuoteSidebar) => {
    onJumpToMessage(item.quoteId);
    handleClose();
  };

  // 处理搜索
  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(event.target.value);
  };

  // 过滤搜索结果
  const filteredItems = searchQuery.trim()
    ? items.filter(item =>
        item.previewContent?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.senderNickname?.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : items;

  // 获取消息类型图标颜色
  const getMessageTypeColor = (type: string) => {
    switch (type?.toLowerCase()) {
      case 'image': return '#4caf50';
      case 'voice': return '#ff9800';
      case 'video': return '#f44336';
      case 'file': return '#9c27b0';
      case 'system': return '#607d8b';
      default: return '#2196f3';
    }
  };

  return (
    <>
      {/* 跳转按钮 */}
      <Tooltip title="查看引用消息列表">
        <IconButton
          className={className}
          color={color}
          size={size}
          onClick={handleOpen}
        >
          <JumpIcon />
        </IconButton>
      </Tooltip>

      {/* 对话框 */}
      <Dialog
        open={open}
        onClose={handleClose}
        maxWidth="sm"
        fullWidth
        PaperProps={{
          sx: {
            height: '80vh',
            display: 'flex',
            flexDirection: 'column',
          },
        }}
      >
        {/* 标题栏 */}
        <DialogTitle>
          <Box display="flex" justifyContent="space-between" alignItems="center">
            <Typography variant="h6">引用消息列表</Typography>
            <IconButton size="small" onClick={handleClose}>
              <CloseIcon />
            </IconButton>
          </Box>

          {/* 搜索框 */}
          <TextField
            fullWidth
            placeholder="搜索引用消息..."
            value={searchQuery}
            onChange={handleSearchChange}
            size="small"
            sx={{ mt: 2 }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon fontSize="small" />
                </InputAdornment>
              ),
            }}
          />
        </DialogTitle>

        {/* 内容区域 */}
        <DialogContent dividers sx={{ flex: 1, overflow: 'auto' }}>
          {loading && items.length === 0 ? (
            <Box display="flex" justifyContent="center" alignItems="center" p={4}>
              <CircularProgress />
            </Box>
          ) : error ? (
            <Alert severity="error">{error}</Alert>
          ) : filteredItems.length === 0 ? (
            <Box
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
              p={4}
            >
              <JumpIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 2 }} />
              <Typography variant="body1" color="text.secondary">
                {searchQuery ? '未找到匹配的引用消息' : '暂无引用消息'}
              </Typography>
              {!searchQuery && (
                <Typography variant="body2" color="text.secondary" mt={1}>
                  在聊天中引用消息后，它们会出现在这里
                </Typography>
              )}
            </Box>
          ) : (
            <List>
              {filteredItems.map((item) => (
                <ListItem
                  key={item.id}
                  button
                  onClick={() => handleJump(item)}
                  sx={{
                    '&:hover': {
                      bgcolor: 'action.hover',
                    },
                  }}
                >
                  {/* 头像/图标 */}
                  <ListItemAvatar>
                    <Avatar
                      sx={{
                        bgcolor: getMessageTypeColor(item.messageType),
                        width: 40,
                        height: 40,
                      }}
                    >
                      {item.isPinned && <PinIcon fontSize="small" />}
                    </Avatar>
                  </ListItemAvatar>

                  {/* 主要内容 */}
                  <ListItemText
                    primary={
                      <Box display="flex" alignItems="center" gap={1}>
                        <Typography variant="subtitle2">
                          {item.senderNickname || '未知用户'}
                        </Typography>
                        {item.isPinned && (
                          <Chip
                            label="已固定"
                            size="small"
                            color="primary"
                            sx={{ height: 16, fontSize: '0.65rem' }}
                          />
                        )}
                      </Box>
                    }
                    secondary={
                      <>
                        <Typography
                          variant="body2"
                          color="text.primary"
                          noWrap
                          sx={{ mb: 0.5 }}
                        >
                          {item.previewContent || '无预览内容'}
                        </Typography>
                        <Box display="flex" alignItems="center" gap={1}>
                          <TimeIcon fontSize="inherit" sx={{ fontSize: 12 }} />
                          <Typography variant="caption" color="text.secondary">
                            {formatDistanceToNow(new Date(item.lastViewedAt), {
                              addSuffix: true,
                              locale: zhCN,
                            })}
                          </Typography>
                        </Box>
                      </>
                    }
                  />

                  {/* 跳转按钮 */}
                  <ListItemSecondaryAction>
                    <Tooltip title="跳转到消息">
                      <IconButton
                        size="small"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleJump(item);
                        }}
                        edge="end"
                      >
                        <JumpIcon />
                      </IconButton>
                    </Tooltip>
                  </ListItemSecondaryAction>
                </ListItem>
              ))}
            </List>
          )}
        </DialogContent>

        {/* 底部操作栏 */}
        <DialogActions>
          <Box display="flex" justifyContent="space-between" alignItems="center" width="100%" px={2}>
            <Typography variant="body2" color="text.secondary">
              共 {filteredItems.length} 条引用消息
            </Typography>
            <Button onClick={handleClose} color="primary">
              关闭
            </Button>
          </Box>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default QuoteJumpButton;
