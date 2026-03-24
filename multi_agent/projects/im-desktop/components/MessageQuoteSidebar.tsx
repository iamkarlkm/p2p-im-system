import React, { useState, useEffect, useCallback, useRef } from 'react';
import {
  Box,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Chip,
  Divider,
  TextField,
  InputAdornment,
  Tooltip,
  Avatar,
  Badge,
  CircularProgress,
  Alert,
  Button,
  Menu,
  MenuItem,
  Checkbox,
  FormControlLabel,
} from '@mui/material';
import {
  Search as SearchIcon,
  PushPin as PinIcon,
  PushPinOutlined as PinOutlinedIcon,
  Delete as DeleteIcon,
  Refresh as RefreshIcon,
  MoreVert as MoreVertIcon,
  FilterList as FilterIcon,
  Sort as SortIcon,
  AccessTime as TimeIcon,
  Person as PersonIcon,
  Chat as ChatIcon,
  Image as ImageIcon,
  Mic as VoiceIcon,
  Videocam as VideoIcon,
  AttachFile as FileIcon,
  Settings as SettingsIcon,
  ClearAll as ClearAllIcon,
  ArrowForward as JumpIcon,
} from '@mui/icons-material';
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { MessageQuoteSidebarService } from '../services/messageQuoteSidebarService';
import { MessageQuoteSidebar } from '../types/messageQuoteSidebar';

interface MessageQuoteSidebarProps {
  userId: number;
  sessionId: number;
  onJumpToMessage?: (quoteId: number) => void;
  onItemClick?: (item: MessageQuoteSidebar) => void;
  className?: string;
  style?: React.CSSProperties;
}

const MessageQuoteSidebarComponent: React.FC<MessageQuoteSidebarProps> = ({
  userId,
  sessionId,
  onJumpToMessage,
  onItemClick,
  className,
  style,
}) => {
  // 状态管理
  const [items, setItems] = useState<MessageQuoteSidebar[]>([]);
  const [filteredItems, setFilteredItems] = useState<MessageQuoteSidebar[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedItems, setSelectedItems] = useState<Set<number>>(new Set());
  const [viewMode, setViewMode] = useState<'all' | 'pinned' | 'recent'>('all');
  const [sortBy, setSortBy] = useState<'lastViewed' | 'createdAt' | 'originalCreatedAt'>('lastViewed');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');
  
  // 菜单状态
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [contextMenu, setContextMenu] = useState<{
    mouseX: number;
    mouseY: number;
    item: MessageQuoteSidebar | null;
  }>({
    mouseX: 0,
    mouseY: 0,
    item: null,
  });

  // 引用服务
  const sidebarService = MessageQuoteSidebarService.getInstance();

  // 获取侧边栏数据
  const fetchSidebarItems = useCallback(async () => {
    if (!userId || !sessionId) return;
    
    setLoading(true);
    setError(null);
    
    try {
      const response = await sidebarService.getUserSidebarItems(userId, sessionId);
      if (response.success && response.data) {
        setItems(response.data);
        setFilteredItems(response.data);
      } else {
        setError(response.message || '获取侧边栏数据失败');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '获取侧边栏数据时发生错误');
      console.error('Error fetching sidebar items:', err);
    } finally {
      setLoading(false);
    }
  }, [userId, sessionId, sidebarService]);

  // 初始加载和会话变化时重新加载
  useEffect(() => {
    if (userId && sessionId) {
      fetchSidebarItems();
    }
  }, [userId, sessionId, fetchSidebarItems]);

  // 搜索过滤
  useEffect(() => {
    if (!searchQuery.trim()) {
      setFilteredItems(items);
      return;
    }

    const query = searchQuery.toLowerCase();
    const filtered = items.filter(item => 
      item.previewContent?.toLowerCase().includes(query) ||
      item.senderNickname?.toLowerCase().includes(query) ||
      item.messageType?.toLowerCase().includes(query)
    );
    setFilteredItems(filtered);
  }, [searchQuery, items]);

  // 视图模式过滤
  useEffect(() => {
    let filtered = [...items];
    
    switch (viewMode) {
      case 'pinned':
        filtered = filtered.filter(item => item.isPinned);
        break;
      case 'recent':
        // 最近3天内
        const threeDaysAgo = new Date();
        threeDaysAgo.setDate(threeDaysAgo.getDate() - 3);
        filtered = filtered.filter(item => 
          new Date(item.lastViewedAt) > threeDaysAgo
        );
        break;
      default:
        // all - 不过滤
        break;
    }
    
    // 排序
    filtered.sort((a, b) => {
      let aValue: Date, bValue: Date;
      
      switch (sortBy) {
        case 'lastViewed':
          aValue = new Date(a.lastViewedAt);
          bValue = new Date(b.lastViewedAt);
          break;
        case 'createdAt':
          aValue = new Date(a.createdAt);
          bValue = new Date(b.createdAt);
          break;
        case 'originalCreatedAt':
          aValue = new Date(a.originalCreatedAt);
          bValue = new Date(b.originalCreatedAt);
          break;
        default:
          aValue = new Date(a.lastViewedAt);
          bValue = new Date(b.lastViewedAt);
      }
      
      if (sortDirection === 'desc') {
        return bValue.getTime() - aValue.getTime();
      } else {
        return aValue.getTime() - bValue.getTime();
      }
    });
    
    setFilteredItems(filtered);
  }, [items, viewMode, sortBy, sortDirection]);

  // 处理搜索
  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(event.target.value);
  };

  // 处理视图模式切换
  const handleViewModeChange = (mode: 'all' | 'pinned' | 'recent') => {
    setViewMode(mode);
  };

  // 处理排序切换
  const handleSortChange = (field: 'lastViewed' | 'createdAt' | 'originalCreatedAt') => {
    if (sortBy === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setSortDirection('desc');
    }
  };

  // 处理固定状态切换
  const handleTogglePin = async (item: MessageQuoteSidebar) => {
    try {
      const response = await sidebarService.togglePinStatus(userId, item.quoteId, !item.isPinned);
      if (response.success) {
        // 更新本地状态
        setItems(prev => prev.map(i => 
          i.id === item.id ? { ...i, isPinned: !item.isPinned } : i
        ));
      }
    } catch (err) {
      console.error('Error toggling pin status:', err);
    }
  };

  // 处理从侧边栏移除
  const handleRemoveFromSidebar = async (item: MessageQuoteSidebar) => {
    try {
      const response = await sidebarService.removeFromSidebar(userId, item.quoteId);
      if (response.success) {
        // 从本地状态移除
        setItems(prev => prev.filter(i => i.id !== item.id));
      }
    } catch (err) {
      console.error('Error removing from sidebar:', err);
    }
  };

  // 处理跳转到消息
  const handleJumpToMessage = (item: MessageQuoteSidebar) => {
    if (onJumpToMessage) {
      onJumpToMessage(item.quoteId);
    }
    if (onItemClick) {
      onItemClick(item);
    }
  };

  // 处理选择项目
  const handleSelectItem = (item: MessageQuoteSidebar) => {
    const newSelected = new Set(selectedItems);
    if (newSelected.has(item.id)) {
      newSelected.delete(item.id);
    } else {
      newSelected.add(item.id);
    }
    setSelectedItems(newSelected);
  };

  // 批量操作
  const handleBatchPin = async () => {
    const quoteIds = Array.from(selectedItems)
      .map(id => items.find(item => item.id === id)?.quoteId)
      .filter((id): id is number => id !== undefined);
    
    if (quoteIds.length === 0) return;
    
    try {
      const response = await sidebarService.batchTogglePinStatus(userId, quoteIds, true);
      if (response.success) {
        // 更新本地状态
        setItems(prev => prev.map(item => 
          selectedItems.has(item.id) ? { ...item, isPinned: true } : item
        ));
        setSelectedItems(new Set());
      }
    } catch (err) {
      console.error('Error batch pinning items:', err);
    }
  };

  const handleBatchUnpin = async () => {
    const quoteIds = Array.from(selectedItems)
      .map(id => items.find(item => item.id === id)?.quoteId)
      .filter((id): id is number => id !== undefined);
    
    if (quoteIds.length === 0) return;
    
    try {
      const response = await sidebarService.batchTogglePinStatus(userId, quoteIds, false);
      if (response.success) {
        // 更新本地状态
        setItems(prev => prev.map(item => 
          selectedItems.has(item.id) ? { ...item, isPinned: false } : item
        ));
        setSelectedItems(new Set());
      }
    } catch (err) {
      console.error('Error batch unpinning items:', err);
    }
  };

  const handleBatchDelete = async () => {
    const quoteIds = Array.from(selectedItems)
      .map(id => items.find(item => item.id === id)?.quoteId)
      .filter((id): id is number => id !== undefined);
    
    if (quoteIds.length === 0) return;
    
    try {
      const response = await sidebarService.batchRemoveFromSidebar(userId, quoteIds);
      if (response.success) {
        // 从本地状态移除
        setItems(prev => prev.filter(item => !selectedItems.has(item.id)));
        setSelectedItems(new Set());
      }
    } catch (err) {
      console.error('Error batch deleting items:', err);
    }
  };

  // 处理上下文菜单
  const handleContextMenu = (event: React.MouseEvent, item: MessageQuoteSidebar) => {
    event.preventDefault();
    setContextMenu({
      mouseX: event.clientX - 2,
      mouseY: event.clientY - 4,
      item,
    });
  };

  const handleCloseContextMenu = () => {
    setContextMenu({
      mouseX: 0,
      mouseY: 0,
      item: null,
    });
  };

  // 获取消息类型图标
  const getMessageTypeIcon = (type: string) => {
    switch (type?.toLowerCase()) {
      case 'image':
        return <ImageIcon fontSize="small" />;
      case 'voice':
        return <VoiceIcon fontSize="small" />;
      case 'video':
        return <VideoIcon fontSize="small" />;
      case 'file':
        return <FileIcon fontSize="small" />;
      case 'system':
        return <SettingsIcon fontSize="small" />;
      default:
        return <ChatIcon fontSize="small" />;
    }
  };

  // 获取消息类型颜色
  const getMessageTypeColor = (type: string) => {
    switch (type?.toLowerCase()) {
      case 'image':
        return '#4caf50';
      case 'voice':
        return '#ff9800';
      case 'video':
        return '#f44336';
      case 'file':
        return '#9c27b0';
      case 'system':
        return '#607d8b';
      default:
        return '#2196f3';
    }
  };

  // 渲染加载状态
  if (loading && items.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" p={3}>
        <CircularProgress />
      </Box>
    );
  }

  // 渲染错误状态
  if (error) {
    return (
      <Alert 
        severity="error" 
        action={
          <Button color="inherit" size="small" onClick={fetchSidebarItems}>
            重试
          </Button>
        }
      >
        {error}
      </Alert>
    );
  }

  return (
    <Paper 
      className={className} 
      style={style}
      elevation={2}
      sx={{ 
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
      }}
    >
      {/* 标题栏 */}
      <Box p={2} borderBottom={1} borderColor="divider">
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
          <Typography variant="h6" component="h2">
            引用消息侧边栏
          </Typography>
          <Box display="flex" alignItems="center" gap={1}>
            <Tooltip title="刷新">
              <IconButton size="small" onClick={fetchSidebarItems} disabled={loading}>
                <RefreshIcon />
              </IconButton>
            </Tooltip>
            <Tooltip title="设置">
              <IconButton size="small">
                <SettingsIcon />
              </IconButton>
            </Tooltip>
          </Box>
        </Box>
        
        {/* 搜索栏 */}
        <TextField
          fullWidth
          placeholder="搜索引用消息..."
          value={searchQuery}
          onChange={handleSearchChange}
          size="small"
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon fontSize="small" />
              </InputAdornment>
            ),
            endAdornment: searchQuery && (
              <InputAdornment position="end">
                <IconButton size="small" onClick={() => setSearchQuery('')}>
                  <ClearAllIcon fontSize="small" />
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
        
        {/* 视图和排序控制 */}
        <Box display="flex" justifyContent="space-between" alignItems="center" mt={2}>
          <Box display="flex" gap={1}>
            <Chip
              label="全部"
              size="small"
              color={viewMode === 'all' ? 'primary' : 'default'}
              onClick={() => handleViewModeChange('all')}
            />
            <Chip
              label="已固定"
              size="small"
              color={viewMode === 'pinned' ? 'primary' : 'default'}
              onClick={() => handleViewModeChange('pinned')}
              icon={<PinIcon fontSize="small" />}
            />
            <Chip
              label="最近"
              size="small"
              color={viewMode === 'recent' ? 'primary' : 'default'}
              onClick={() => handleViewModeChange('recent')}
              icon={<TimeIcon fontSize="small" />}
            />
          </Box>
          
          <Box display="flex" gap={1}>
            <Tooltip title={`按${sortBy === 'lastViewed' ? '最后查看时间' : sortBy === 'createdAt' ? '创建时间' : '原消息时间'}${sortDirection === 'desc' ? '降序' : '升序'}排序`}>
              <IconButton 
                size="small" 
                onClick={() => handleSortChange(sortBy)}
                color={sortDirection === 'desc' ? 'primary' : 'default'}
              >
                <SortIcon fontSize="small" />
              </IconButton>
            </Tooltip>
            <Tooltip title="筛选">
              <IconButton size="small">
                <FilterIcon fontSize="small" />
              </IconButton>
            </Tooltip>
          </Box>
        </Box>
        
        {/* 批量操作栏（有选中项时显示） */}
        {selectedItems.size > 0 && (
          <Box 
            display="flex" 
            justifyContent="space-between" 
            alignItems="center" 
            mt={1}
            p={1}
            bgcolor="action.hover"
            borderRadius={1}
          >
            <Typography variant="body2" color="text.secondary">
              已选中 {selectedItems.size} 项
            </Typography>
            <Box display="flex" gap={1}>
              <Button 
                size="small" 
                startIcon={<PinIcon />}
                onClick={handleBatchPin}
              >
                固定
              </Button>
              <Button 
                size="small" 
                startIcon={<PinOutlinedIcon />}
                onClick={handleBatchUnpin}
              >
                取消固定
              </Button>
              <Button 
                size="small" 
                color="error"
                startIcon={<DeleteIcon />}
                onClick={handleBatchDelete}
              >
                删除
              </Button>
            </Box>
          </Box>
        )}
      </Box>
      
      {/* 内容区域 */}
      <Box flex={1} overflow="auto">
        {filteredItems.length === 0 ? (
          <Box 
            display="flex" 
            flexDirection="column" 
            alignItems="center" 
            justifyContent="center" 
            p={4}
            height="100%"
          >
            <ChatIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 2 }} />
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
          <List dense disablePadding>
            {filteredItems.map((item) => (
              <React.Fragment key={item.id}>
                <ListItem
                  button
                  selected={selectedItems.has(item.id)}
                  onClick={() => handleSelectItem(item)}
                  onDoubleClick={() => handleJumpToMessage(item)}
                  onContextMenu={(e) => handleContextMenu(e, item)}
                  sx={{
                    '&:hover': {
                      bgcolor: 'action.hover',
                    },
                  }}
                >
                  {/* 选择框 */}
                  <Checkbox
                    edge="start"
                    checked={selectedItems.has(item.id)}
                    tabIndex={-1}
                    disableRipple
                    size="small"
                    onClick={(e) => e.stopPropagation()}
                    onChange={() => handleSelectItem(item)}
                  />
                  
                  {/* 头像/图标 */}
                  <ListItemAvatar>
                    <Badge
                      color="primary"
                      variant="dot"
                      invisible={!item.isPinned}
                      anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'right',
                      }}
                    >
                      <Avatar 
                        sx={{ 
                          bgcolor: getMessageTypeColor(item.messageType),
                          width: 32,
                          height: 32,
                        }}
                      >
                        {getMessageTypeIcon(item.messageType)}
                      </Avatar>
                    </Badge>
                  </ListItemAvatar>
                  
                  {/* 主要内容 */}
                  <ListItemText
                    primary={
                      <Box display="flex" alignItems="center" gap={1}>
                        <Typography variant="subtitle2" noWrap>
                          {item.senderNickname || '未知用户'}
                        </Typography>
                        <Chip
                          label={item.messageType || '文本'}
                          size="small"
                          sx={{ 
                            height: 16,
                            fontSize: '0.65rem',
                            bgcolor: getMessageTypeColor(item.messageType),
                            color: 'white',
                          }}
                        />
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
                          <Typography
                            variant="caption"
                            color="text.secondary"
                          >
                            {formatDistanceToNow(new Date(item.lastViewedAt), {
                              addSuffix: true,
                              locale: zhCN,
                            })}
                          </Typography>
                        </Box>
                      </>
                    }
                  />
                  
                  {/* 操作按钮 */}
                  <ListItemSecondaryAction>
                    <Box display="flex" alignItems="center" gap={0.5}>
                      <Tooltip title={item.isPinned ? '取消固定' : '固定'}>
                        <IconButton 
                          size="small" 
                          onClick={(e) => {
                            e.stopPropagation();
                            handleTogglePin(item);
                          }}
                          color={item.isPinned ? 'primary' : 'default'}
                        >
                          {item.isPinned ? <PinIcon fontSize="small" /> : <PinOutlinedIcon fontSize="small" />}
                        </IconButton>
                      </Tooltip>
                      
                      <Tooltip title="跳转到消息">
                        <IconButton 
                          size="small" 
                          onClick={(e) => {
                            e.stopPropagation();
                            handleJumpToMessage(item);
                          }}
                        >
                          <JumpIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      
                      <Tooltip title="更多操作">
                        <IconButton 
                          size="small" 
                          onClick={(e) => {
                            e.stopPropagation();
                            setAnchorEl(e.currentTarget);
                          }}
                        >
                          <MoreVertIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    </Box>
                  </ListItemSecondaryAction>
                </ListItem>
                <Divider variant="inset" component="li" />
              </React.Fragment>
            ))}
          </List>
        )}
      </Box>
      
      {/* 底部统计栏 */}
      <Box p={2} borderTop={1} borderColor="divider">
        <Typography variant="body2" color="text.secondary">
          共 {filteredItems.length} 条引用消息
          {viewMode !== 'all' && `（${viewMode === 'pinned' ? '已固定' : '最近'}视图）`}
        </Typography>
      </Box>
      
      {/* 上下文菜单 */}
      <Menu
        open={contextMenu.mouseY !== 0}
        onClose={handleCloseContextMenu}
        anchorReference="anchorPosition"
        anchorPosition={
          contextMenu.mouseY !== 0 && contextMenu.mouseX !== 0
            ? { top: contextMenu.mouseY, left: contextMenu.mouseX }
            : undefined
        }
      >
        {contextMenu.item && (
          <>
            <MenuItem 
              onClick={() => {
                handleTogglePin(contextMenu.item!);
                handleCloseContextMenu();
              }}
            >
              {contextMenu.item.isPinned ? '取消固定' : '固定'}
            </MenuItem>
            <MenuItem 
              onClick={() => {
                handleJumpToMessage(contextMenu.item!);
                handleCloseContextMenu();
              }}
            >
              跳转到消息
            </MenuItem>
            <MenuItem 
              onClick={() => {
                handleRemoveFromSidebar(contextMenu.item!);
                handleCloseContextMenu();
              }}
            >
              从侧边栏移除
            </MenuItem>
          </>
        )}
      </Menu>
      
      {/* 更多操作菜单 */}
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={() => setAnchorEl(null)}
      >
        <MenuItem onClick={() => setAnchorEl(null)}>查看详情</MenuItem>
        <MenuItem onClick={() => setAnchorEl(null)}>复制链接</MenuItem>
        <MenuItem onClick={() => setAnchorEl(null)}>分享</MenuItem>
        <Divider />
        <MenuItem onClick={() => setAnchorEl(null)}>清空侧边栏</MenuItem>
      </Menu>
    </Paper>
  );
};

export default MessageQuoteSidebarComponent;