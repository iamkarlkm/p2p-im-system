package com.im.backend.modules.parking.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.parking.dto.*;
import com.im.backend.modules.parking.entity.ParkingLot;

import java.math.BigDecimal;
import java.util.List;

/**
 * 停车场服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface ParkingLotService extends IService<ParkingLot> {

    /**
     * 创建停车场
     *
     * @param dto 创建DTO
     * @return 停车场ID
     */
    Long createParkingLot(ParkingLotCreateDTO dto);

    /**
     * 更新停车场
     *
     * @param id  停车场ID
     * @param dto 更新DTO
     * @return 是否成功
     */
    boolean updateParkingLot(Long id, ParkingLotUpdateDTO dto);

    /**
     * 删除停车场
     *
     * @param id 停车场ID
     * @return 是否成功
     */
    boolean deleteParkingLot(Long id);

    /**
     * 获取停车场详情
     *
     * @param id 停车场ID
     * @return 详情VO
     */
    ParkingLotDetailVO getParkingLotDetail(Long id);

    /**
     * 分页查询停车场列表
     *
     * @param dto 查询DTO
     * @return 分页结果
     */
    Page<ParkingLotListVO> pageParkingLots(ParkingLotQueryDTO dto);

    /**
     * 搜索附近停车场
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param radius    搜索半径（米）
     * @param pageNum   页码
     * @param pageSize  每页大小
     * @return 分页结果
     */
    Page<ParkingLotNearbyVO> searchNearbyParkingLots(Double longitude, Double latitude,
                                                     Integer radius, Integer pageNum, Integer pageSize);

    /**
     * 智能推荐停车场
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param limit     限制数量
     * @return 推荐列表
     */
    List<ParkingLotRecommendVO> recommendParkingLots(Double longitude, Double latitude, Integer limit);

    /**
     * 根据目的地搜索周边停车场
     *
     * @param destLongitude 目的地经度
     * @param destLatitude  目的地纬度
     * @param radius        搜索半径
     * @return 停车场列表
     */
    List<ParkingLotNearbyVO> searchParkingLotsByDestination(Double destLongitude, Double destLatitude, Integer radius);

    /**
     * 更新停车场实时空位数据
     *
     * @param parkingLotId    停车场ID
     * @param availableSpaces 可用车位数
     * @return 是否成功
     */
    boolean updateRealTimeSpaces(Long parkingLotId, Integer availableSpaces);

    /**
     * 批量更新停车场空位数据
     *
     * @param spaceDataList 空位数据列表
     */
    void batchUpdateSpaces(List<ParkingSpaceUpdateDTO> spaceDataList);

    /**
     * 预测停车场空位
     *
     * @param parkingLotId 停车场ID
     * @param minutes      预测几分钟后
     * @return 预测空位数
     */
    Integer predictAvailableSpaces(Long parkingLotId, Integer minutes);

    /**
     * 计算停车费用
     *
     * @param parkingLotId    停车场ID
     * @param durationMinutes 停车时长（分钟）
     * @return 预计费用
     */
    BigDecimal calculateParkingFee(Long parkingLotId, Integer durationMinutes);

    /**
     * 比较停车场价格
     *
     * @param parkingLotIds 停车场ID列表
     * @return 价格对比结果
     */
    List<ParkingPriceCompareVO> compareParkingPrices(List<Long> parkingLotIds);

    /**
     * 获取停车场统计数据
     *
     * @param parkingLotId 停车场ID
     * @return 统计数据
     */
    ParkingStatisticsVO getParkingStatistics(Long parkingLotId);

    /**
     * 从第三方同步停车场数据
     *
     * @param dataSource   数据源
     * @param syncAll      是否全量同步
     * @param areaCode     区域编码
     * @return 同步数量
     */
    Integer syncFromThirdParty(String dataSource, Boolean syncAll, String areaCode);

    /**
     * 启用/禁用停车场
     *
     * @param parkingLotId 停车场ID
     * @param enabled      是否启用
     * @return 是否成功
     */
    boolean toggleParkingLotStatus(Long parkingLotId, Boolean enabled);

    /**
     * 获取热门停车场
     *
     * @param cityCode 城市编码
     * @param limit    限制数量
     * @return 热门列表
     */
    List<ParkingLotListVO> getHotParkingLots(String cityCode, Integer limit);

    /**
     * 搜索停车场
     *
     * @param keyword  关键词
     * @param cityCode 城市编码
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ParkingLotListVO> searchParkingLots(String keyword, String cityCode, Integer pageNum, Integer pageSize);
}
