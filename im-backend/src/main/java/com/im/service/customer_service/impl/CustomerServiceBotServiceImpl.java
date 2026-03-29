package com.im.service.customer_service.impl;

import com.im.dto.customer_service.BotChatRequest;
import com.im.dto.customer_service.BotChatResponse;
import com.im.service.customer_service.CustomerServiceBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 智能客服机器人服务实现
 * 功能 #319 - 智能客服与工单管理系统
 */
@Slf4j
@Service
public class CustomerServiceBotServiceImpl implements CustomerServiceBotService {
    
    private final Map<String, String> intentPatterns = new ConcurrentHashMap<>();
    private final Map<String, String> knowledgeBase = new ConcurrentHashMap<>();
    
    public CustomerServiceBotServiceImpl() {
        initIntentPatterns();
        initKnowledgeBase();
    }
    
    private void initIntentPatterns() {
        intentPatterns.put("订单查询", "订单|查询|在哪|什么时候到");
        intentPatterns.put("退款申请", "退款|退货|退钱|不满意");
        intentPatterns.put("投诉建议", "投诉|举报|不好|差评");
        intentPatterns.put("优惠券", "优惠券|红包|折扣|优惠码");
        intentPatterns.put("配送问题", "配送|快递|物流|送货");
        intentPatterns.put("账户问题", "账户|密码|登录|注册");
        intentPatterns.put("支付问题", "支付|付款|没钱|失败");
        intentPatterns.put("商家咨询", "商家|店铺|营业时间|电话");
    }
    
    private void initKnowledgeBase() {
        knowledgeBase.put("如何申请退款", "您可以在订单详情页点击【申请退款】按钮，填写退款原因后提交。退款将在1-3个工作日内处理完成。");
        knowledgeBase.put("配送时间", "一般情况下，订单会在30-60分钟内送达。高峰期可能稍有延迟，请您耐心等待。");
        knowledgeBase.put("优惠券使用", "在结算页面选择可用的优惠券，系统会自动计算优惠金额。每张优惠券都有使用期限，请注意查看。");
        knowledgeBase.put("如何联系商家", "您可以在订单详情页点击【联系商家】按钮，或直接拨打商家电话。");
        knowledgeBase.put("账户安全", "请定期修改密码，不要泄露验证码。如发现异常登录，请立即修改密码并联系客服。");
    }
    
    @Override
    public BotChatResponse chat(BotChatRequest request) {
        BotChatResponse response = new BotChatResponse();
        String message = request.getMessage();
        
        String intent = recognizeIntent(message);
        response.setIntentType(intent);
        response.setIntentName(intent);
        response.setConfidence(0.85);
        
        List<BotChatResponse.KnowledgeItem> knowledges = searchKnowledge(message, 3);
        response.setRelatedKnowledges(knowledges);
        response.setHitKnowledge(!knowledges.isEmpty());
        
        if (!knowledges.isEmpty()) {
            response.setReply(knowledges.get(0).getAnswer());
        } else {
            response.setReply("抱歉，我暂时无法回答您的问题。是否需要转接人工客服？");
        }
        
        response.setNeedTransfer(needTransferToHuman(message, intent));
        response.setSuggestedActions(getSuggestedReplies(message, intent));
        response.setContextId(request.getContextId());
        response.setSessionId(request.getSessionId());
        
        log.info("机器人对话: userId={}, intent={}", request.getUserId(), intent);
        
        return response;
    }
    
    @Override
    public String recognizeIntent(String message) {
        String lowerMsg = message.toLowerCase();
        for (Map.Entry<String, String> entry : intentPatterns.entrySet()) {
            String[] patterns = entry.getValue().split("\\|");
            for (String pattern : patterns) {
                if (lowerMsg.contains(pattern)) {
                    return entry.getKey();
                }
            }
        }
        return "其他咨询";
    }
    
    @Override
    public List<BotChatResponse.KnowledgeItem> searchKnowledge(String query, Integer limit) {
        List<BotChatResponse.KnowledgeItem> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lowerQuery.contains(entry.getKey()) || entry.getKey().contains(lowerQuery)) {
                BotChatResponse.KnowledgeItem item = new BotChatResponse.KnowledgeItem();
                item.setKnowledgeId((long) (entry.getKey().hashCode()));
                item.setQuestion(entry.getKey());
                item.setAnswer(entry.getValue());
                item.setRelevance(0.9);
                results.add(item);
                
                if (results.size() >= limit) break;
            }
        }
        
        return results;
    }
    
    @Override
    public Boolean needTransferToHuman(String message, String intentType) {
        return "投诉建议".equals(intentType) || 
               message.contains("人工") || 
               message.contains("客服") ||
               message.contains("找你们领导");
    }
    
    @Override
    public List<String> getSuggestedReplies(String message, String intentType) {
        List<String> suggestions = new ArrayList<>();
        
        switch (intentType) {
            case "订单查询":
                suggestions.add("查看我的订单");
                suggestions.add("订单什么时候到");
                break;
            case "退款申请":
                suggestions.add("如何申请退款");
                suggestions.add("退款进度查询");
                break;
            case "投诉建议":
                suggestions.add("转人工客服");
                suggestions.add("我要投诉");
                break;
            default:
                suggestions.add("转人工客服");
                suggestions.add("返回主菜单");
        }
        
        return suggestions;
    }
    
    @Override
    public void feedbackKnowledge(Long knowledgeId, Boolean isHelpful, Long userId) {
        log.info("知识反馈: knowledgeId={}, isHelpful={}, userId={}", knowledgeId, isHelpful, userId);
    }
}
