package com.im.backend.controller;

import com.im.backend.service.ReadReceiptService;
import com.im.backend.dto.ReadReceiptRequest;
import com.im.backend.dto.ReadReceiptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/read-receipt")
@RequiredArgsConstructor
public class ReadReceiptController {

    private final ReadReceiptService receiptService;

    @PostMapping("/mark")
    public ResponseEntity<ReadReceiptResponse> markAsRead(@RequestBody ReadReceiptRequest request) {
        ReadReceiptResponse response = receiptService.markAsRead(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-batch")
    public ResponseEntity<List<ReadReceiptResponse>> markBatchAsRead(@RequestBody ReadReceiptRequest request) {
        List<ReadReceiptResponse> responses = receiptService.markBatchAsRead(request);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ReadReceiptResponse>> getReadReceipts(
            @RequestParam String conversationId,
            @RequestParam String messageId) {
        List<ReadReceiptResponse> receipts = receiptService.getReadReceipts(conversationId, messageId);
        return ResponseEntity.ok(receipts);
    }
}
