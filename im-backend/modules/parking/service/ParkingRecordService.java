package com.im.backend.modules.parking.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.parking.dto.*;
import com.im.backend.modules.parking.entity.ParkingRecord;

import java.util.List;

/**
 * 停车记录服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface ParkingRecordService extends IService<ParkingRecord> {

    /**
     * 创建停车记录（入场）
     *
     * @param dto 入场DTO
     * @return 停车记录ID
     */
    Long createParkingRecord(ParkingEntryDTO dto);

    /**
     * 记录车辆入场
     *
     * @param dto 入场信息
     * @return 停车记录
     */
    ParkingRecord recordEntry(ParkingEntryDTO dto);

    /**
     * 记录车辆出场
     *
     * @param dto 出场信息
     * @return 停车记录
     */
    ParkingRecord recordExit(ParkingExitDTO dto);

    /**
     * 完成停车记录
     *
     * @param recordId   记录ID
     * @param exitMethod 出场方式
     * @return 是否成功
     */
    boolean completeParking(Long recordId, Integer exitMethod);

    /**
     * 获取停车记录详情
     *
     * @param recordId 记录ID
     * @return 详情VO
     */
    ParkingRecordDetailVO getParkingRecordDetail(Long recordId);

    /**
     * 查询用户当前停车记录
     *
     * @param userId 用户ID
     * @return 当前停车记录，没有则返回null
     */
    ParkingRecordDetailVO getCurrentParkingRecord(Long userId);

    /**
     * 分页查询用户停车记录
     *
     * @param userId   用户ID
     * @param status   状态（可选）
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ParkingRecordListVO> pageUserParkingRecords(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取用户停车历史
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 历史记录列表
     */
    List<ParkingRecordListVO> getUserParkingHistory(Long userId, Integer limit);

    /**
     * 根据车牌号查询停车记录
     *
     * @param plateNumber 车牌号
     * @param status      状态（可选）
     * @return 停车记录列表
     */
    List<ParkingRecord> findByPlateNumber(String plateNumber, Integer status);

    /**
     * 标记停车位置
     *
     * @param recordId 记录ID
     * @param dto      位置信息
     * @return 是否成功
     */
    boolean markParkingLocation(Long recordId, ParkingLocationMarkDTO dto);

    /**
     * 上传停车位置照片
     *
     * @param recordId  记录ID
     * @param photoUrl  照片URL
     * @param longitude 经度
     * @param latitude  纬度
     * @return 是否成功
     */
    boolean uploadParkingPhoto(Long recordId, String photoUrl, Double longitude, Double latitude);

    /**
     * 更新停车记录支付信息
     *
     * @param recordId      记录ID
     * @param paymentInfo   支付信息
     * @return 是否成功
     */
    boolean updatePaymentInfo(Long recordId, ParkingPaymentInfoDTO paymentInfo);

    /**
     * 取消停车记录
     *
     * @param recordId 记录ID
     * @param reason   取消原因
     * @return 是否成功
     */
    boolean cancelParkingRecord(Long recordId, String reason);

    /**
     * 获取停车记录统计
     *
     * @param userId    用户ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 统计数据
     */
    ParkingRecordStatisticsVO getParkingStatistics(Long userId, String startDate, String endDate);

    /**
     * 获取用户停车概览
     *
     * @param userId 用户ID
     * @return 概览数据
     */
    UserParkingOverviewVO getUserParkingOverview(Long userId);

    /**
     * 自动识别入场（地理围栏触发）
     *
     * @param userId        用户ID
     * @param parkingLotId  停车场ID
     * @param plateNumber   车牌号
     * @param longitude     经度
     * @param latitude      纬度
     * @return 停车记录ID
     */
    Long autoRecordEntry(Long userId, Long parkingLotId, String plateNumber,
                         Double longitude, Double latitude);

    /**
     * 自动识别出场（地理围栏触发）
     *
     * @param userId       用户ID
     * @param parkingLotId 停车场ID
     * @param longitude    经度
     * @param latitude     纬度
     * @return 是否成功
     */
    boolean autoRecordExit(Long userId, Long parkingLotId, Double longitude, Double latitude);

    /**
     * 延长停车记录（用于无感支付场景）
     *
     * @param recordId      记录ID
     * @param extendMinutes 延长分钟数
     * @return 是否成功
     */
    boolean extendParkingTime(Long recordId, Integer extendMinutes);

    /**
     * 根据停车记录创建支付订单
     *
     * @param recordId 记录ID
     * @return 订单ID
     */
    Long createPaymentOrder(Long recordId);
}
