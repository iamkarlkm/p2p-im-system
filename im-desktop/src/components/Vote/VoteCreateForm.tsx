import React, { useState } from 'react';
import { voteService, VoteCreateRequest } from '../../services/voteService';
import './VoteCreateForm.css';

interface VoteCreateFormProps {
  groupId: string;
  userId: string;
  messageId: string;
  onVoteCreated?: (vote: any) => void;
  onCancel?: () => void;
}

const VoteCreateForm: React.FC<VoteCreateFormProps> = ({
  groupId,
  userId,
  messageId,
  onVoteCreated,
  onCancel
}) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [options, setOptions] = useState<string[]>(['', '']);
  const [isAnonymous, setIsAnonymous] = useState(false);
  const [allowMultipleChoice, setAllowMultipleChoice] = useState(false);
  const [hasEndTime, setHasEndTime] = useState(false);
  const [endDate, setEndDate] = useState('');
  const [endTime, setEndTime] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  
  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};
    
    if (!title.trim()) {
      newErrors.title = '请输入投票标题';
    } else if (title.length > 500) {
      newErrors.title = '标题不能超过500字符';
    }
    
    if (description.length > 2000) {
      newErrors.description = '描述不能超过2000字符';
    }
    
    // 验证选项
    let hasEmptyOption = false;
    let hasValidOptions = 0;
    options.forEach((option, index) => {
      if (option.trim()) {
        hasValidOptions++;
      } else if (index < 2) {
        hasEmptyOption = true;
        newErrors[`option_${index}`] = '前两个选项不能为空';
      }
    });
    
    if (hasEmptyOption || hasValidOptions < 2) {
      if (!newErrors.options) {
        newErrors.options = '至少需要两个有效的选项';
      }
    }
    
    // 验证结束时间
    if (hasEndTime) {
      const endDateTime = new Date(`${endDate}T${endTime}`);
      const now = new Date();
      if (endDateTime <= now) {
        newErrors.endTime = '结束时间必须晚于当前时间';
      }
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };
  
  const handleOptionChange = (index: number, value: string) => {
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);
    
    // 清除该选项的错误
    if (errors[`option_${index}`]) {
      const newErrors = { ...errors };
      delete newErrors[`option_${index}`];
      setErrors(newErrors);
    }
  };
  
  const addOption = () => {
    if (options.length < 10) {
      setOptions([...options, '']);
    }
  };
  
  const removeOption = (index: number) => {
    if (options.length > 2) {
      const newOptions = options.filter((_, i) => i !== index);
      setOptions(newOptions);
      
      // 清除可能的错误
      if (errors[`option_${index}`]) {
        const newErrors = { ...errors };
        delete newErrors[`option_${index}`];
        setErrors(newErrors);
      }
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setIsSubmitting(true);
    
    try {
      // 构建结束时间
      let endDateTime: string | undefined;
      if (hasEndTime && endDate && endTime) {
        endDateTime = `${endDate}T${endTime}:00`;
      }
      
      // 过滤掉空的选项
      const validOptions = options.filter(option => option.trim());
      
      const request: VoteCreateRequest = {
        messageId,
        groupId,
        userId,
        title: title.trim(),
        description: description.trim() || undefined,
        options: validOptions,
        isAnonymous,
        allowMultipleChoice,
        endTime: endDateTime
      };
      
      const createdVote = await voteService.createVote(request);
      onVoteCreated?.(createdVote);
      
      // 重置表单
      resetForm();
    } catch (error) {
      console.error('创建投票失败:', error);
      alert('创建投票失败，请重试');
    } finally {
      setIsSubmitting(false);
    }
  };
  
  const resetForm = () => {
    setTitle('');
    setDescription('');
    setOptions(['', '']);
    setIsAnonymous(false);
    setAllowMultipleChoice(false);
    setHasEndTime(false);
    setEndDate('');
    setEndTime('');
    setErrors({});
  };
  
  const getMinDate = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };
  
  return (
    <form className="vote-create-form" onSubmit={handleSubmit}>
      <div className="form-header">
        <h3>创建新投票</h3>
        {onCancel && (
          <button type="button" className="btn-cancel" onClick={onCancel}>
            ×
          </button>
        )}
      </div>
      
      <div className="form-group">
        <label htmlFor="title">投票标题 *</label>
        <input
          id="title"
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="请输入投票标题"
          maxLength={500}
          className={errors.title ? 'input-error' : ''}
        />
        {errors.title && <span className="error-message">{errors.title}</span>}
        <div className="input-help">最大长度: 500字符</div>
      </div>
      
      <div className="form-group">
        <label htmlFor="description">投票描述 (可选)</label>
        <textarea
          id="description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="描述投票内容..."
          maxLength={2000}
          rows={3}
          className={errors.description ? 'input-error' : ''}
        />
        {errors.description && (
          <span className="error-message">{errors.description}</span>
        )}
        <div className="input-help">最大长度: 2000字符</div>
      </div>
      
      <div className="form-group">
        <label>投票选项 * (最少2个，最多10个)</label>
        {errors.options && (
          <span className="error-message">{errors.options}</span>
        )}
        
        <div className="options-list">
          {options.map((option, index) => (
            <div key={index} className="option-item">
              <input
                type="text"
                value={option}
                onChange={(e) => handleOptionChange(index, e.target.value)}
                placeholder={`选项 ${index + 1}`}
                maxLength={200}
                className={errors[`option_${index}`] ? 'input-error' : ''}
              />
              {options.length > 2 && (
                <button
                  type="button"
                  className="btn-remove-option"
                  onClick={() => removeOption(index)}
                  title="删除选项"
                >
                  ×
                </button>
              )}
            </div>
          ))}
        </div>
        
        <div className="options-actions">
          <button
            type="button"
            className="btn-add-option"
            onClick={addOption}
            disabled={options.length >= 10}
          >
            添加选项 ({options.length}/10)
          </button>
        </div>
      </div>
      
      <div className="form-row">
        <div className="form-group checkbox-group">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={isAnonymous}
              onChange={(e) => setIsAnonymous(e.target.checked)}
            />
            <span className="checkbox-text">匿名投票</span>
          </label>
          <div className="checkbox-help">投票者身份对其他人不可见</div>
        </div>
        
        <div className="form-group checkbox-group">
          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={allowMultipleChoice}
              onChange={(e) => setAllowMultipleChoice(e.target.checked)}
            />
            <span className="checkbox-text">允许多选</span>
          </label>
          <div className="checkbox-help">用户可以同时选择多个选项</div>
        </div>
      </div>
      
      <div className="form-group">
        <label className="checkbox-label">
          <input
            type="checkbox"
            checked={hasEndTime}
            onChange={(e) => setHasEndTime(e.target.checked)}
          />
          <span className="checkbox-text">设置结束时间</span>
        </label>
        
        {hasEndTime && (
          <div className="datetime-inputs">
            <div className="date-input">
              <label htmlFor="endDate">结束日期</label>
              <input
                id="endDate"
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                min={getMinDate()}
                className={errors.endTime ? 'input-error' : ''}
              />
            </div>
            <div className="time-input">
              <label htmlFor="endTime">结束时间</label>
              <input
                id="endTime"
                type="time"
                value={endTime}
                onChange={(e) => setEndTime(e.target.value)}
                className={errors.endTime ? 'input-error' : ''}
              />
            </div>
          </div>
        )}
        {errors.endTime && (
          <span className="error-message">{errors.endTime}</span>
        )}
      </div>
      
      <div className="form-actions">
        <button
          type="submit"
          className="btn-submit"
          disabled={isSubmitting}
        >
          {isSubmitting ? '创建中...' : '创建投票'}
        </button>
        {onCancel && (
          <button
            type="button"
            className="btn-cancel-form"
            onClick={onCancel}
            disabled={isSubmitting}
          >
            取消
          </button>
        )}
      </div>
    </form>
  );
};

export default VoteCreateForm;