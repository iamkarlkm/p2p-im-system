import React, { useState, useEffect } from 'react';
import { 
  Box, 
  List, 
  ListItem, 
  ListItemText, 
  IconButton,
  Button,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Typography,
  Menu,
  MenuItem,
  Badge
} from '@mui/material';
import {
  Add as AddIcon,
  MoreVert as MoreVertIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  DragIndicator as DragIcon
} from '@mui/icons-material';
import type { FriendGroup, FriendGroupWithMembers } from '../../types/friendGroup';
import * as friendGroupService from '../../services/friendGroupService';

interface FriendGroupListProps {
  selectedGroupId?: string;
  onSelectGroup: (group: FriendGroupWithMembers) => void;
  onCreateGroup: () => void;
}

export const FriendGroupList: React.FC<FriendGroupListProps> = ({
  selectedGroupId,
  onSelectGroup,
  onCreateGroup
}) => {
  const [groups, setGroups] = useState<FriendGroup[]>([]);
  const [loading, setLoading] = useState(false);
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);
  const [selectedMenuGroup, setSelectedMenuGroup] = useState<FriendGroup | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editName, setEditName] = useState('');
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  useEffect(() => {
    loadGroups();
  }, []);

  const loadGroups = async () => {
    setLoading(true);
    try {
      const data = await friendGroupService.getFriendGroups();
      setGroups(data.sort((a, b) => a.sortOrder - b.sortOrder));
    } catch (error) {
      console.error('加载分组失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleGroupClick = async (group: FriendGroup) => {
    try {
      const detail = await friendGroupService.getFriendGroupWithMembers(group.id);
      onSelectGroup(detail);
    } catch (error) {
      console.error('加载分组详情失败:', error);
    }
  };

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>, group: FriendGroup) => {
    event.stopPropagation();
    setMenuAnchor(event.currentTarget);
    setSelectedMenuGroup(group);
  };

  const handleMenuClose = () => {
    setMenuAnchor(null);
    setSelectedMenuGroup(null);
  };

  const handleEditClick = () => {
    if (selectedMenuGroup) {
      setEditName(selectedMenuGroup.name);
      setEditDialogOpen(true);
    }
    handleMenuClose();
  };

  const handleEditSave = async () => {
    if (selectedMenuGroup && editName.trim()) {
      try {
        await friendGroupService.updateFriendGroup(selectedMenuGroup.id, { name: editName.trim() });
        loadGroups();
      } catch (error) {
        console.error('更新分组失败:', error);
      }
    }
    setEditDialogOpen(false);
  };

  const handleDeleteClick = () => {
    setDeleteDialogOpen(true);
    handleMenuClose();
  };

  const handleDeleteConfirm = async () => {
    if (selectedMenuGroup) {
      try {
        await friendGroupService.deleteFriendGroup(selectedMenuGroup.id);
        loadGroups();
      } catch (error) {
        console.error('删除分组失败:', error);
      }
    }
    setDeleteDialogOpen(false);
  };

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Typography variant="h6">好友分组</Typography>
        <Button
          variant="contained"
          size="small"
          startIcon={<AddIcon />}
          onClick={onCreateGroup}
        >
          新建分组
        </Button>
      </Box>

      <List sx={{ flex: 1, overflow: 'auto' }}>
        {groups.map((group) => (
          <ListItem
            key={group.id}
            button
            selected={selectedGroupId === group.id}
            onClick={() => handleGroupClick(group)}
            sx={{
              '&.Mui-selected': {
                backgroundColor: 'primary.light',
                color: 'primary.contrastText',
              },
            }}
          >
            <DragIcon sx={{ mr: 1, color: 'text.secondary', fontSize: 18 }} />
            <ListItemText
              primary={
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Typography variant="body1">{group.name}</Typography>
                  {group.isDefault && (
                    <Typography variant="caption" sx={{ opacity: 0.7 }}>(默认)</Typography>
                  )}
                </Box>
              }
              secondary={`${group.memberCount} 位好友`}
            />
            <Badge badgeContent={group.memberCount} color="primary" sx={{ mr: 2 }} />
            <IconButton
              size="small"
              onClick={(e) => handleMenuOpen(e, group)}
              sx={{ opacity: 0.6, '&:hover': { opacity: 1 } }}
            >
              <MoreVertIcon />
            </IconButton>
          </ListItem>
        ))}
      </List>

      <Menu
        anchorEl={menuAnchor}
        open={Boolean(menuAnchor)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={handleEditClick}>
          <EditIcon fontSize="small" sx={{ mr: 1 }} />
          重命名
        </MenuItem>
        <MenuItem onClick={handleDeleteClick} disabled={selectedMenuGroup?.isDefault}>
          <DeleteIcon fontSize="small" sx={{ mr: 1 }} />
          删除
        </MenuItem>
      </Menu>

      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle>重命名分组</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            fullWidth
            label="分组名称"
            value={editName}
            onChange={(e) => setEditName(e.target.value)}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>取消</Button>
          <Button onClick={handleEditSave} variant="contained">保存</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)} maxWidth="xs">
        <DialogTitle>确认删除</DialogTitle>
        <DialogContent>
          <Typography>确定要删除分组 "{selectedMenuGroup?.name}" 吗？好友将被移至默认分组。</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>取消</Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">删除</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
