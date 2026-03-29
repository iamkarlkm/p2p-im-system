package com.im.backend.modules.geofence.service.impl;

import com.im.backend.modules.geofence.entity.ArrivalRecord;
import com.im.backend.modules.geofence.entity.ArrivalServicePush;
import com.im.backend.modules.geofence.enums.CustomerTag;
import com.im.backend.modules.geofence.repository.ArrivalRecordMapper;
import com.im.backend.modules.geofence.repository.ArrivalServicePushMapper;
import com.im.backend.modules.geofence.service.IPersonalizedArrivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalizedArrivalServiceImpl implements IPersonalizedArrivalService {

    private final ArrivalRecordMapper arrivalRecordMapper;
    private final ArrivalServicePushMapper pushMapper;

    @Override
    @Transactional
    public void onUserArrival(ArrivalRecord arrivalRecord) {
        log.info("处理用户到店个性化服务: userId={}, storeId={}, customerTag={}", 
                arrivalRecord.getUserId(), arrivalRecord.getStoreId(), arrivalRecord.getCustomerTag());

        // 推送欢迎消息
        pushWelcomeMessage(arrivalRecord.getId(), arrivalRecord.getUserId(), 
                arrivalRecord.getMerchantId(), arrivalRecord.getCustomerTag());

        // 根据客户标签推送优惠券
        if (!CustomerTag.NEW.getCode().equals(arrivalRecord.getCustomerTag())) {
            pushCoupon(arrivalRecord.getId(), arrivalRecord.getUserId(), 
                    arrivalRecord.getMerchantId(), arrivalRecord.getCustomerTag());
        }

        // VIP客户发送优先接待提醒
        if (CustomerTag.VIP.getCode().equals(arrivalRecord.getCustomerTag())) {
            sendPriorityAlertToMerchant(arrivalRecord.getId(), arrivalRecord.getMerchantId(), 
                    arrivalRecord.getStoreId(), arrivalRecord.getCustomerTag());
        }

        // 推送个性化推荐
        pushRecommendations(arrivalRecord.getId(), arrivalRecord.getUserId(), arrivalRecord.getMerchantId());

        // 标记服务已推送
        arrivalRecordMapper.updateServicePushed(arrivalRecord.getId());
    }

    @Override
    public void pushWelcomeMessage(Long arrivalRecordId, Long userId, Long merchantId, String memberLevel) {
        String template = getWelcomeMessageTemplate(merchantId, null, memberLevel);
        String message = template.replace("{memberLevel}", memberLevel != null ? memberLevel : "会员");

        ArrivalServicePush push = new ArrivalServicePush();
        push.setArrivalRecordId(arrivalRecordId);
        push.setUserId(userId);
        push.setMerchantId(merchantId);
        push.setServiceType("WELCOME");
        push.setContent(message);
        push.setPushChannel("APP_PUSH");
        push.setPushStatus("PENDING");

        pushMapper.insert(push);
        log.info("创建欢迎消息推送: userId={}, message={}", userId, message);
    }

    @Override
    public void pushCoupon(Long arrivalRecordId, Long userId, Long merchantId, String customerTag) {
        String couponDesc = "";
        BigDecimal amount = BigDecimal.ZERO;

        if (CustomerTag.VIP.getCode().equals(customerTag)) {
            couponDesc = "VIP专属8折券";
            amount = new BigDecimal("20");
        } else if (CustomerTag.OLD.getCode().equals(customerTag)) {
            couponDesc = "老客回馈9折券";
            amount = new BigDecimal("10");
        } else if (CustomerTag.SILENT.getCode().equals(customerTag)) {
            couponDesc = "好久不见85折券";
            amount = new BigDecimal("15");
        } else if (CustomerTag.LOST.getCode().equals(customerTag)) {
            couponDesc = "专属回归7折券";
            amount = new BigDecimal("30");
        }

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            ArrivalServicePush push = new ArrivalServicePush();
            push.setArrivalRecordId(arrivalRecordId);
            push.setUserId(userId);
            push.setMerchantId(merchantId);
            push.setServiceType("COUPON");
            push.setContent(couponDesc);
            push.setCouponAmount(amount);
            push.setPushChannel("APP_PUSH");
            push.setPushStatus("PENDING");

            pushMapper.insert(push);
            log.info("创建优惠券推送: userId={}, coupon={}", userId, couponDesc);
        }
    }

    @Override
    public void pushRecommendations(Long arrivalRecordId, Long userId, Long merchantId) {
        String recommendations = "根据您的喜好推荐: 招牌菜品、热销套餐";

        ArrivalServicePush push = new ArrivalServicePush();
        push.setArrivalRecordId(arrivalRecordId);
        push.setUserId(userId);
        push.setMerchantId(merchantId);
        push.setServiceType("RECOMMEND");
        push.setContent(recommendations);
        push.setPushChannel("APP_PUSH");
        push.setPushStatus("PENDING");

        pushMapper.insert(push);
        log.info("创建推荐推送: userId={}", userId);
    }

    @Override
    public void sendPriorityAlertToMerchant(Long arrivalRecordId, Long merchantId, Long storeId, String memberLevel) {
        String alert = String.format("VIP客户到店提醒: %s级会员已到店,请优先接待", memberLevel);
        
        // TODO: 调用消息服务发送给商家
        log.info("发送VIP优先接待提醒: merchantId={}, storeId={}, alert={}", merchantId, storeId, alert);
    }

    @Override
    public String getWelcomeMessageTemplate(Long merchantId, Long storeId, String customerTag) {
        if (CustomerTag.NEW.getCode().equals(customerTag)) {
            return "🎉 欢迎首次光临!成为会员享受专属优惠~";
        } else if (CustomerTag.VIP.getCode().equals(customerTag)) {
            return "👑 尊贵的{memberLevel},欢迎再次光临!专属服务已为您准备就绪~";
        } else if (CustomerTag.SILENT.getCode().equals(customerTag)) {
            return "😊 好久不见!欢迎回来,我们很想您~";
        } else if (CustomerTag.LOST.getCode().equals(customerTag)) {
            return "🎁 欢迎回来!专属回归礼已备好~";
        }
        return "👋 欢迎光临!祝您用餐愉快~";
    }
}
