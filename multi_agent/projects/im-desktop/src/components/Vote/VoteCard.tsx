import React, { useState, useEffect } from 'react';
import { Vote, VoteOption } from '../../services/voteService';
import { voteService } from '../../services/voteService';
import './VoteCard.css';

interface VoteCardProps {
  vote: Vote;
  currentUserId?: string;
  onVoteSubmitted?: (vote: Vote) => void;
  onVoteClosed?: (voteId: string) => void;
  showActions?: boolean;
}

const VoteCard: React.FC<VoteCardProps> = ({
  vote,
  currentUserId,
  onVoteSubmitted,
  onVoteClosed,
  showActions = true
}) => {
  const [selectedOptions, setSelectedOptions] = useState<number[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [hasVoted, setHasVoted] = useState(false);
  const [voteData, setVoteData] = useState<Vote>(vote);
  const [voteOptions, setVoteOptions] = useState<VoteOption[]>([]);
  
  useEffect(() => {
    updateVoteData(vote);
  }, [vote]);
  
  const updateVoteData = (newVote: Vote) => {
    setVoteData(newVote);
    const options = voteService.calculateVotePercentages(newVote);
    setVoteOptions(options);
    
    // 检查当前用户是否已投票
    if (currentUserId && !newVote.isAnonymous) {
      voteService.hasUserVoted(newVote.id, currentUserId)
        .then(result => setHasVoted(result))
        .catch(() => setHasVoted(false));
    }
  };
  
  const handleOptionToggle = (index: number) => {
    if (voteData.isClosed) return;
    
    if (voteData.allowMultipleChoice) {
      if (selectedOptions.includes(index)) {
        setSelectedOptions(selectedOptions.filter(i => i !== index));
      } else {
        setSelectedOptions([...selectedOptions, index]);
      }
    } else {
      setSelectedOptions([index]);
    }
  };
  
  const handleSubmitVote = async () => {
    if (selectedOptions.length === 0 || !currentUserId) {
      return;
    }
    
    setIsSubmitting(true);
    try {
      const request = {
        voteId: voteData.id,
        userId: currentUserId,
        selectedOptions
      };
      
      const updatedVote = await voteService.submitVote(voteData.id, request);
      updateVoteData(updatedVote);
      setSelectedOptions([]);
      onVoteSubmitted?.(updatedVote);
    } catch (error) {
      console.error('提交投票失败:', error);
      alert('提交投票失败，请重试');
    } finally {
      setIsSubmitting(false);
    }
  };
  
  const handleCloseVote = async () => {
    if (!confirm('确定要结束投票吗？投票结束后将无法再投票。')) {
      return;
    }
    
    try {
      await voteService.closeVote(voteData.id);
      const updatedVote = await voteService.getVote(voteData.id);
      updateVoteData(updatedVote);
      onVoteClosed?.(voteData.id);
    } catch (error) {
      console.error('关闭投票失败:', error);
      alert('关闭投票失败');
    }
  };
  
  const isExpired = voteService.isVoteExpired(voteData);
  const canVote = voteService.canSubmitVote(voteData, currentUserId);
  
  // 计算剩余时间
  const getRemainingTime = () => {
    if (!voteData.endTime) return null;
    
    const end = new Date(voteData.endTime);
    const now = new Date();
    const diffMs = end.getTime() - now.getTime();
    
    if (diffMs <= 0) return '已结束';
    
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    const diffHours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    
    if (diffDays > 0) return `${diffDays}天${diffHours}小时`;
    if (diffHours > 0) return `${diffHours}小时${diffMinutes}分钟`;
    return `${diffMinutes}分钟`;
  };
  
  const remainingTime = getRemainingTime();
  
  return (
    <div className={`vote-card ${voteData.isClosed || isExpired ? 'vote-closed' : ''}`}>
      <div className="vote-header">
        <h3 className="vote-title">{voteData.title}</h3>
        {voteData.description && (
          <p className="vote-description">{voteData.description}</p>
        )}
        
        <div className="vote-meta">
          <span className="vote-type">
            {voteData.isAnonymous ? '匿名投票' : '公开投票'}
            {' | '}
            {voteData.allowMultipleChoice ? '多选' : '单选'}
          </span>
          <span className="vote-stats">
            总票数: {voteData.totalVotes}
          </span>
          {voteData.endTime && (
            <span className="vote-time">
              剩余时间: {remainingTime}
            </span>
          )}
          {(voteData.isClosed || isExpired) && (
            <span className="vote-status-closed">已结束</span>
          )}
        </div>
      </div>
      
      <div className="vote-options">
        {voteOptions.map((option, index) => {
          const isSelected = selectedOptions.includes(index);
          const isVoted = hasVoted || (voteData.totalVotes > 0 && !canVote.canSubmit);
          
          return (
            <div
              key={index}
              className={`vote-option ${isSelected ? 'selected' : ''} ${isVoted ? 'voted' : ''}`}
              onClick={() => !isVoted && handleOptionToggle(index)}
            >
              <div className="option-content">
                <div className="option-text">
                  {option.text}
                </div>
                {isVoted && (
                  <div className="option-stats">
                    <span className="vote-count">{option.votes}票</span>
                    <span className="vote-percentage">({option.percentage}%)</span>
                  </div>
                )}
              </div>
              
              {isVoted && (
                <div className="vote-progress-bar">
                  <div
                    className="vote-progress-fill"
                    style={{ width: `${option.percentage}%` }}
                  />
                </div>
              )}
              
              {!isVoted && (
                <div className="option-checkbox">
                  {voteData.allowMultipleChoice ? (
                    <input
                      type="checkbox"
                      checked={isSelected}
                      onChange={() => {}}
                      readOnly
                    />
                  ) : (
                    <input
                      type="radio"
                      checked={isSelected}
                      onChange={() => {}}
                      readOnly
                    />
                  )}
                </div>
              )}
            </div>
          );
        })}
      </div>
      
      {showActions && !voteData.isClosed && !isExpired && (
        <div className="vote-actions">
          {canVote.canSubmit && currentUserId ? (
            <>
              <button
                className="btn btn-primary"
                onClick={handleSubmitVote}
                disabled={isSubmitting || selectedOptions.length === 0}
              >
                {isSubmitting ? '提交中...' : '提交投票'}
              </button>
              <button
                className="btn btn-secondary"
                onClick={handleCloseVote}
              >
                结束投票
              </button>
            </>
          ) : (
            <div className="vote-notice">
              {canVote.reason || '您暂时无法投票'}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default VoteCard;