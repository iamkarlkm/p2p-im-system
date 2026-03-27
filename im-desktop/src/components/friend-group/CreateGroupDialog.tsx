import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  Typography,
  Alert
} from '@mui/material';
import * as friendGroupService from '../../services/friendGroupService';

interface CreateGroupDialogProps {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const CreateGroupDialog: React.FC<CreateGroupDialogProps> = ({
  open,
  onClose,
  onSuccess
}) => {
  const [name, setName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleClose = () => {
    setName('');
    setError('');
    onClose();
  };

  const handleSubmit = async () => {
    const trimmedName = name.trim();
    
    if (!trimmedName) {
      setError('请输入分组名称');
      return;
    }

    if (trimmedName.length > 20) {
      setError('分组名称不能超过20个字符');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await friendGroupService.createFriendGroup({ name: trimmedName });
      onSuccess();
      handleClose();
    } catch (err: any) {
      setError(err.response?.data?.message || '创建分组失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="xs" fullWidth>
      <DialogTitle>新建好友分组</DialogTitle>
      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        <Box sx={{ mt: 1 }}>
          <TextField
            autoFocus
            fullWidth
            label="分组名称"
            placeholder="例如：家人、同事、好友"
            value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={loading}
            inputProps={{ maxLength: 20 }}
            helperText={`${name.length}/20`}
          />
        </Box>
        <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
          创建后可以拖拽好友到分组中，方便管理联系人
        </Typography>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} disabled={loading}>
          取消
        </Button>
        <Button 
          onClick={handleSubmit} 
          variant="contained" 
          disabled={loading || !name.trim()}
        >
          {loading ? '创建中...' : '创建'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
