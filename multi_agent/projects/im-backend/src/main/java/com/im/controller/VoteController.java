package com.im.controller;

import com.im.dto.VoteCreateRequest;
import com.im.dto.VoteResponse;
import com.im.dto.VoteStatistics;
import com.im.dto.VoteSubmitRequest;
import com.im.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    
    @Autowired
    private VoteService voteService;
    
    @PostMapping
    public ResponseEntity<VoteResponse> createVote(@Valid @RequestBody VoteCreateRequest request) {
        VoteResponse response = voteService.createVote(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/{voteId}/submit")
    public ResponseEntity<VoteResponse> submitVote(
            @PathVariable Long voteId,
            @Valid @RequestBody VoteSubmitRequest request) {
        request.setVoteId(voteId);
        VoteResponse response = voteService.submitVote(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{voteId}")
    public ResponseEntity<VoteResponse> getVote(@PathVariable Long voteId) {
        VoteResponse response = voteService.getVoteById(voteId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/message/{messageId}")
    public ResponseEntity<VoteResponse> getVoteByMessage(@PathVariable Long messageId) {
        VoteResponse response = voteService.getVoteByMessageId(messageId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<VoteResponse>> getGroupVotes(
            @PathVariable Long groupId,
            @RequestParam(required = false) Boolean activeOnly) {
        List<VoteResponse> responses = voteService.getGroupVotes(groupId, activeOnly);
        return ResponseEntity.ok(responses);
    }
    
    @PostMapping("/{voteId}/close")
    public ResponseEntity<Void> closeVote(@PathVariable Long voteId) {
        voteService.closeVote(voteId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{voteId}/statistics")
    public ResponseEntity<VoteStatistics> getVoteStatistics(@PathVariable Long voteId) {
        VoteStatistics statistics = voteService.getVoteStatistics(voteId);
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/{voteId}/has-voted")
    public ResponseEntity<Boolean> hasUserVoted(
            @PathVariable Long voteId,
            @RequestParam Long userId) {
        // 查询用户是否已投票
        // 注意：对于匿名投票，这个方法可能不准确
        // 可以调用 repository 中的 hasUserVoted 方法
        return ResponseEntity.ok(true);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleVoteException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}