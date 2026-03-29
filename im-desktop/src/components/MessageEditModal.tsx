/**
 * 消息编辑弹窗组件
 * 
 * @module components/MessageEditModal
 * @since 2026-03-27
 */

import React, { useState, useEffect, useCallback } from 'react';
import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  Button,
  Textarea,
  Text,
  Flex,
  Box,
  Badge,
  Alert,
  AlertIcon,
  Select,
  FormControl,
  FormLabel,
  HStack,
  VStack,
  Divider,
  Tooltip
} from '@chakra-ui/react';
import { Edit, Clock, Hash, Save, X, RotateCcw } from 'lucide-react';
import type { EditModalProps } from '../types/message-edit';
import { EditType, EDIT_TYPE_LABELS, EDIT_TYPE_COLORS, DEFAULT_EDITOR_CONFIG } from '../types/message-edit';
import { useMessageEdit } from '../stores/message-edit-store';

export const MessageEditModal: React.FC<EditModalProps> = ({
  isOpen,
  messageId,
  originalContent,
  currentContent,
  editCount,
  maxEditCount,
  remainingEdits,
  onClose,
  onSubmit
}) => {
  const {
    editedContent,
    editReason,
    editType,
    isSubmitting,
    error,
    updateContent,
    updateReason,
    updateType,
    submit,
    cancel
  } = useMessageEdit();

  const [charCount, setCharCount] = useState(0);
  const config = DEFAULT_EDITOR_CONFIG;

  // 初始化
  useEffect(() => {
    if (isOpen && currentContent) {
      updateContent(currentContent);
      setCharCount(currentContent.length);
    }
  }, [isOpen, currentContent, updateContent]);

  // 内容变化处理
  const handleContentChange = useCallback((e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const newContent = e.target.value;
    if (newContent.length <= config.maxLength) {
      updateContent(newContent);
      setCharCount(newContent.length);
    }
  }, [updateContent, config.maxLength]);

  // 原因变化处理
  const handleReasonChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    updateReason(e.target.value);
  }, [updateReason]);

  // 类型变化处理
  const handleTypeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    updateType(e.target.value as EditType);
  }, [updateType]);

  // 提交处理
  const handleSubmit = useCallback(async () => {
    if (editedContent.trim().length < config.minLength) {
      return;
    }

    const request = {
      messageId,
      originalContent: currentContent,
      editedContent: editedContent.trim(),
      editReason: editReason.trim() || undefined,
      editType
    };

    await onSubmit(request);
  }, [editedContent, editReason, editType, messageId, currentContent, onSubmit, config.minLength]);

  // 关闭处理
  const handleClose = useCallback(() => {
    cancel();
    onClose();
  }, [cancel, onClose]);

  // 是否可提交
  const canSubmit = editedContent.trim().length >= config.minLength && 
                    editedContent.trim() !== currentContent && 
                    !isSubmitting;

  // 字符数颜色
  const getCharCountColor = () => {
    const ratio = charCount / config.maxLength;
    if (ratio > 0.9) return 'red.500';
    if (ratio > 0.7) return 'orange.500';
    return 'gray.500';
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="xl">
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>
          <Flex align="center" gap={2}>
            <Edit size={20} />
            <Text>编辑消息</Text>
            <Badge colorScheme="blue" variant="subtle">
              第 {editCount + 1} 次编辑
            </Badge>
          </Flex>
        </ModalHeader>
        <ModalCloseButton />

        <ModalBody>
          <VStack spacing={4} align="stretch">
            {/* 编辑限制提示 */}
            <Alert status="info" size="sm" borderRadius="md">
              <AlertIcon />
              <Flex justify="space-between" width="100%" align="center">
                <Text fontSize="sm">
                  剩余可编辑次数: <strong>{remainingEdits}</strong> / {maxEditCount}
                </Text>
                <HStack spacing={1}>
                  <Clock size={14} />
                  <Text fontSize="sm">30分钟内有效</Text>
                </HStack>
              </Flex>
            </Alert>

            {/* 错误提示 */}
            {error && (
              <Alert status="error" size="sm" borderRadius="md">
                <AlertIcon />
                <Text fontSize="sm">{error}</Text>
              </Alert>
            )}

            {/* 原内容显示 */}
            <Box>
              <Text fontSize="sm" fontWeight="medium" color="gray.600" mb={1}>
                当前内容
              </Text>
              <Box
                p={3}
                bg="gray.50"
                borderRadius="md"
                border="1px solid"
                borderColor="gray.200"
                maxH="100px"
                overflowY="auto"
              >
                <Text fontSize="sm" color="gray.600">
                  {currentContent}
                </Text>
              </Box>
            </Box>

            <Divider />

            {/* 编辑类型 */}
            <FormControl size="sm">
              <FormLabel fontSize="sm">编辑类型</FormLabel>
              <Select
                size="sm"
                value={editType}
                onChange={handleTypeChange}
                maxW="200px"
              >
                {config.allowedEditTypes.map((type) => (
                  <option key={type} value={type}>
                    {EDIT_TYPE_LABELS[type]}
                  </option>
                ))}
              </Select>
            </FormControl>

            {/* 编辑内容 */}
            <FormControl isRequired>
              <FormLabel fontSize="sm">新内容</FormLabel>
              <Textarea
                value={editedContent}
                onChange={handleContentChange}
                placeholder="输入新的消息内容..."
                minH="120px"
                resize="vertical"
                focusBorderColor="blue.400"
              />
              {config.showCharacterCount && (
                <Flex justify="flex-end" mt={1}>
                  <Text fontSize="xs" color={getCharCountColor()}>
                    {charCount} / {config.maxLength}
                  </Text>
                </Flex>
              )}
            </FormControl>

            {/* 编辑原因 */}
            {config.allowEditReason && (
              <FormControl>
                <FormLabel fontSize="sm">
                  编辑原因
                  {config.editReasonRequired && (
                    <Text as="span" color="red.500"> *</Text>
                  )}
                </FormLabel>
                <input
                  type="text"
                  value={editReason}
                  onChange={handleReasonChange}
                  placeholder="简要说明编辑原因（可选）"
                  style={{
                    width: '100%',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    border: '1px solid #E2E8F0',
                    fontSize: '14px'
                  }}
                />
              </FormControl>
            )}
          </VStack>
        </ModalBody>

        <ModalFooter>
          <HStack spacing={2}>
            <Button
              variant="ghost"
              onClick={handleClose}
              leftIcon={<X size={16} />}
              isDisabled={isSubmitting}
            >
              取消
            </Button>
            <Button
              colorScheme="blue"
              onClick={handleSubmit}
              isLoading={isSubmitting}
              isDisabled={!canSubmit}
              leftIcon={<Save size={16} />}
            >
              保存编辑
            </Button>
          </HStack>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default MessageEditModal;
