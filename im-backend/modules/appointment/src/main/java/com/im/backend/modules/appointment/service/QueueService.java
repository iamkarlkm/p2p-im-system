package com.im.backend.modules.appointment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.core.result.PageResult;
import com.im.backend.modules.appointment.dto.*;
import com.im.backend.modules.appointment.entity.QueueInfo;
import com.im.backend.modules.appointment.entity.QueueRecord;

import java.util.List;

/**
 * 排队叫号Service接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface QueueService extends IService<QueueInfo> {

    /**
     * 获取队列列表
     *
     * @param merchantId 商户ID
     * @param storeId 门店ID
     * @return 队列列表
     */
    List<QueueInfoResponse> getQueueList(Long merchantId, Long storeId);

    /**
     * 创建队列
     *
     * @param request 创建请求
     * @return 队列信息
     */
    QueueInfoResponse createQueue(CreateQueueRequest request);

    /**
     * 更新队列
     *
     * @param queueId 队列ID
     * @param request 更新请求
     * @return 队列信息
     */
    QueueInfoResponse updateQueue(Long queueId, UpdateQueueRequest request);

    /**
     * 删除队列
     *
     * @param queueId 队列ID
     * @return 是否成功
     */
    boolean deleteQueue(Long queueId);

    /**
     * 现场取号
     *
     * @param request 取号请求
     * @return 取号结果
     */
    TakeNumberResponse takeNumberOnsite(TakeNumberRequest request);

    /**
     * 在线取号
     *
     * @param request 取号请求
     * @return 取号结果
     */
    TakeNumberResponse takeNumberOnline(TakeNumberRequest request);

    /**
     * 预约取号
     *
     * @param appointmentId 预约ID
     * @return 取号结果
     */
    TakeNumberResponse takeNumberByAppointment(Long appointmentId);

    /**
     * 获取取号记录
     *
     * @param recordId 记录ID
     * @return 记录详情
     */
    QueueRecordDetailResponse getQueueRecord(Long recordId);

    /**
     * 获取用户排队记录
     *
     * @param userId 用户ID
     * @param status 状态(可选)
     * @return 记录列表
     */
    List<QueueRecordResponse> getUserQueueRecords(Long userId, Integer status);

    /**
     * 获取队列当前排队情况
     *
     * @param queueId 队列ID
     * @return 排队情况
     */
    QueueStatusResponse getQueueStatus(Long queueId);

    /**
     * 叫号
     *
     * @param queueId 队列ID
     * @param count 叫号数量
     * @return 被叫号码列表
     */
    List<QueueRecordResponse> callNumbers(Long queueId, Integer count);

    /**
     * 确认到店
     *
     * @param recordId 记录ID
     * @return 是否成功
     */
    boolean confirmArrival(Long recordId);

    /**
     * 开始服务
     *
     * @param recordId 记录ID
     * @param window 服务窗口
     * @return 是否成功
     */
    boolean startService(Long recordId, Integer window);

    /**
     * 完成服务
     *
     * @param recordId 记录ID
     * @return 是否成功
     */
    boolean completeService(Long recordId);

    /**
     * 标记过号
     *
     * @param recordId 记录ID
     * @param reason 原因
     * @return 是否成功
     */
    boolean markPassed(Long recordId, String reason);

    /**
     * 取消排队
     *
     * @param recordId 记录ID
     * @param reason 原因
     * @return 是否成功
     */
    boolean cancelQueue(Long recordId, String reason);

    /**
     * 重新取号(过号后)
     *
     * @param recordId 原记录ID
     * @return 新取号结果
     */
    TakeNumberResponse requeue(Long recordId);

    /**
     * 清空队列
     *
     * @param queueId 队列ID
     * @return 是否成功
     */
    boolean clearQueue(Long queueId);

    /**
     * 暂停/恢复队列
     *
     * @param queueId 队列ID
     * @param paused 是否暂停
     * @return 是否成功
     */
    boolean pauseQueue(Long queueId, Boolean paused);

    /**
     * 获取商户所有排队统计
     *
     * @param merchantId 商户ID
     * @return 统计数据
     */
    QueueStatisticsResponse getQueueStatistics(Long merchantId);

    /**
     * 获取实时排队进度
     *
     * @param recordId 记录ID
     * @return 进度信息
     */
    QueueProgressResponse getQueueProgress(Long recordId);

    /**
     * 预估等待时间
     *
     * @param queueId 队列ID
     * @param queueNumber 排队号码
     * @return 预估分钟数
     */
    int estimateWaitTime(Long queueId, Integer queueNumber);

    /**
     * 更新预估等待时间
     * 定时任务调用
     *
     * @param queueId 队列ID
     */
    void updateEstimatedWaitTime(Long queueId);

    /**
     * 检查并标记过号
     * 定时任务调用
     *
     * @return 标记数量
     */
    int checkAndMarkPassedNumbers();

    /**
     * 发送叫号提醒
     *
     * @param recordId 记录ID
     * @return 是否成功
     */
    boolean sendCallNotification(Long recordId);

    /**
     * 获取当前叫号信息
     *
     * @param queueId 队列ID
     * @return 当前叫号信息
     */
    CurrentCallResponse getCurrentCall(Long queueId);
}
