package com.im.service.local.controller;

import com.im.service.local.entity.Merchant;
import com.im.service.local.entity.MerchantReview;
import com.im.service.local.service.LocalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/local")
public class LocalController {

    private final LocalService localService;

    public LocalController(LocalService localService) {
        this.localService = localService;
    }

    // ========== 商家接口 ==========

    @PostMapping("/merchants")
    public ResponseEntity<Merchant> createMerchant(@RequestBody Merchant merchant) {
        Merchant created = localService.createMerchant(merchant);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/merchants/{merchantId}")
    public ResponseEntity<Merchant> getMerchant(@PathVariable String merchantId) {
        return localService.getMerchant(merchantId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/merchants/category/{categoryId}")
    public ResponseEntity<List<Merchant>> getMerchantsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Merchant> merchants = localService.getMerchantsByCategory(categoryId, page, size);
        return ResponseEntity.ok(merchants);
    }

    @GetMapping("/merchants/search")
    public ResponseEntity<List<Merchant>> searchMerchants(@RequestParam String keyword) {
        List<Merchant> merchants = localService.searchMerchants(keyword);
        return ResponseEntity.ok(merchants);
    }

    @GetMapping("/merchants/nearby")
    public ResponseEntity<List<Merchant>> getNearbyMerchants(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "5") double radius) {
        List<Merchant> merchants = localService.getNearbyMerchants(lat, lng, radius);
        return ResponseEntity.ok(merchants);
    }

    // ========== 评价接口 ==========

    @PostMapping("/merchants/{merchantId}/reviews")
    public ResponseEntity<MerchantReview> createReview(
            @PathVariable String merchantId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") String userId) {
        Integer rating = (Integer) request.get("rating");
        String content = (String) request.get("content");
        String images = (String) request.get("images");
        MerchantReview review = localService.createReview(merchantId, userId, rating, content, images);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/merchants/{merchantId}/reviews")
    public ResponseEntity<List<MerchantReview>> getMerchantReviews(
            @PathVariable String merchantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MerchantReview> reviews = localService.getMerchantReviews(merchantId, page, size);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<Map<String, Object>> likeReview(@PathVariable Long reviewId) {
        boolean success = localService.likeReview(reviewId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("reviewId", reviewId);
        return ResponseEntity.ok(result);
    }
}
