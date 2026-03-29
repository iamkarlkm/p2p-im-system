package com.im.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vote_messages")
public class VoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id", nullable = false)
    private Long messageId;
    
    @Column(name = "group_id")
    private Long groupId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    
    @Column(name = "description", length = 2000)
    private String description;
    
    @ElementCollection
    @CollectionTable(name = "vote_options", joinColumns = @JoinColumn(name = "vote_id"))
    @Column(name = "option_text")
    private List<String> options = new ArrayList<>();
    
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;
    
    @Column(name = "allow_multiple_choice", nullable = false)
    private Boolean allowMultipleChoice = false;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "total_votes", nullable = false)
    private Integer totalVotes = 0;
    
    @ElementCollection
    @CollectionTable(name = "vote_results", joinColumns = @JoinColumn(name = "vote_id"))
    private List<VoteResult> results = new ArrayList<>();
    
    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    
    public Boolean getAllowMultipleChoice() { return allowMultipleChoice; }
    public void setAllowMultipleChoice(Boolean allowMultipleChoice) { this.allowMultipleChoice = allowMultipleChoice; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public Integer getTotalVotes() { return totalVotes; }
    public void setTotalVotes(Integer totalVotes) { this.totalVotes = totalVotes; }
    
    public List<VoteResult> getResults() { return results; }
    public void setResults(List<VoteResult> results) { this.results = results; }
    
    public Boolean getIsClosed() { return isClosed; }
    public void setIsClosed(Boolean isClosed) { this.isClosed = isClosed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Embeddable
    public static class VoteResult {
        @Column(name = "option_index", nullable = false)
        private Integer optionIndex;
        
        @Column(name = "vote_count", nullable = false)
        private Integer voteCount = 0;
        
        @ElementCollection
        @CollectionTable(name = "vote_participants", joinColumns = {
            @JoinColumn(name = "vote_id"),
            @JoinColumn(name = "option_index")
        })
        @Column(name = "user_id")
        private List<Long> participantUserIds = new ArrayList<>();
        
        public Integer getOptionIndex() { return optionIndex; }
        public void setOptionIndex(Integer optionIndex) { this.optionIndex = optionIndex; }
        
        public Integer getVoteCount() { return voteCount; }
        public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }
        
        public List<Long> getParticipantUserIds() { return participantUserIds; }
        public void setParticipantUserIds(List<Long> participantUserIds) { this.participantUserIds = participantUserIds; }
    }
}