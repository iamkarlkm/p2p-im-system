package com.im.service.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.service.auth.entity.RefreshToken;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 刷新 Token 数据访问层
 * 
 * 功能特性：
 * 1. 基础的 CRUD 操作（继承 BaseMapper）
 * 2. 根据 Token 字符串查询
 * 3. 根据用户ID和设备ID查询
 * 4. 批量删除过期或已使用的 Token
 * 5. 查询用户的所有 Token
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Repository
@Mapper
public interface RefreshTokenRepository extends BaseMapper<RefreshToken> {

    // ==================== 根据 Token 查询 ====================

    /**
     * 根据 Token 字符串查询
     *
     * @param token Refresh Token 字符串
     * @return Optional<RefreshToken>
     */
    @Select("SELECT * FROM im_refresh_token WHERE token = #{token}")
    Optional<RefreshToken> findByToken(String token);

    /**
     * 根据 Token ID（JTI）查询
     *
     * @param id Token ID
     * @return Optional<RefreshToken>
     */
    @Select("SELECT * FROM im_refresh_token WHERE id = #{id}")
    Optional<RefreshToken> findById(String id);

    // ==================== 根据用户查询 ====================

    /**
     * 根据用户ID查询所有 Refresh Token
     *
     * @param userId 用户ID
     * @return RefreshToken 列表
     */
    @Select("SELECT * FROM im_refresh_token WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<RefreshToken> findAllByUserId(Long userId);

    /**
     * 根据用户ID和设备ID查询 Refresh Token
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return Optional<RefreshToken>
     */
    @Select("SELECT * FROM im_refresh_token WHERE user_id = #{userId} AND device_id = #{deviceId}")
    Optional<RefreshToken> findByUserIdAndDeviceId(@Param("userId") Long userId, 
                                                   @Param("deviceId") String deviceId);

    /**
     * 根据用户ID查询所有未过期且未使用的 Token
     *
     * @param userId 用户ID
     * @return RefreshToken 列表
     */
    @Select("SELECT * FROM im_refresh_token WHERE user_id = #{userId} " +
            "AND expiry_date > NOW() AND used = 0 AND revoked = 0 " +
            "ORDER BY created_at DESC")
    List<RefreshToken> findValidTokensByUserId(Long userId);

    // ==================== 根据设备查询 ====================

    /**
     * 根据设备ID查询所有 Token
     *
     * @param deviceId 设备ID
     * @return RefreshToken 列表
     */
    @Select("SELECT * FROM im_refresh_token WHERE device_id = #{deviceId}")
    List<RefreshToken> findAllByDeviceId(String deviceId);

    // ==================== 删除操作 ====================

    /**
     * 根据用户ID和设备ID删除 Refresh Token
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM im_refresh_token WHERE user_id = #{userId} AND device_id = #{deviceId}")
    int deleteByUserIdAndDeviceId(@Param("userId") Long userId, @Param("deviceId") String deviceId);

    /**
     * 根据用户ID删除所有 Refresh Token
     *
     * @param userId 用户ID
     * @return 删除的行数
     */
    @Delete("DELETE FROM im_refresh_token WHERE user_id = #{userId}")
    int deleteAllByUserId(Long userId);

    /**
     * 根据 Token 删除
     *
     * @param token Token 字符串
     * @return 删除的行数
     */
    @Delete("DELETE FROM im_refresh_token WHERE token = #{token}")
    int deleteByToken(String token);

    /**
     * 删除过期的 Token
     *
     * @param expiryDate 过期时间点
     * @return 删除的行数
     */
    @Delete("DELETE FROM im_refresh_token WHERE expiry_date < #{expiryDate}")
    int deleteAllExpiredBefore(Instant expiryDate);

    /**
     * 删除已使用的 Token
     *
     * @return 删除的行数
     */
    @Delete("DELETE FROM im_refresh_token WHERE used = 1")
    int deleteAllUsed();

    /**
     * 删除已撤销的 Token
     *
     * @return 删除的行数
     */
    @Delete("DELETE FROM im_refresh_token WHERE revoked = 1")
    int deleteAllRevoked();

    // ==================== 统计查询 ====================

    /**
     * 统计用户的 Token 数量
     *
     * @param userId 用户ID
     * @return Token 数量
     */
    @Select("SELECT COUNT(*) FROM im_refresh_token WHERE user_id = #{userId}")
    Long countByUserId(Long userId);

    /**
     * 统计用户的有效 Token 数量
     *
     * @param userId 用户ID
     * @return 有效 Token 数量
     */
    @Select("SELECT COUNT(*) FROM im_refresh_token WHERE user_id = #{userId} " +
            "AND expiry_date > NOW() AND used = 0 AND revoked = 0")
    Long countValidByUserId(Long userId);

    /**
     * 统计所有 Token 数量
     *
     * @return Token 数量
     */
    @Select("SELECT COUNT(*) FROM im_refresh_token")
    Long countAll();

    /**
     * 统计过期 Token 数量
     *
     * @return 过期 Token 数量
     */
    @Select("SELECT COUNT(*) FROM im_refresh_token WHERE expiry_date < NOW()")
    Long countExpired();

    // ==================== 状态更新 ====================

    /**
     * 标记 Token 为已使用
     *
     * @param id Token ID
     * @return 更新的行数
     */
    @Update("UPDATE im_refresh_token SET used = 1, updated_at = NOW() WHERE id = #{id}")
    int markAsUsed(String id);

    /**
     * 撤销 Token
     *
     * @param id Token ID
     * @return 更新的行数
     */
    @Update("UPDATE im_refresh_token SET revoked = 1, updated_at = NOW() WHERE id = #{id}")
    int revokeById(String id);

    /**
     * 撤销用户的所有 Token
     *
     * @param userId 用户ID
     * @return 更新的行数
     */
    @Update("UPDATE im_refresh_token SET revoked = 1, updated_at = NOW() WHERE user_id = #{userId}")
    int revokeAllByUserId(Long userId);

    // ==================== 批量操作 ====================

    /**
     * 批量插入 Refresh Token
     * 用于初始化或迁移数据
     *
     * @param tokens Token 列表
     * @return 插入的行数
     */
    @Insert("<script>" +
            "INSERT INTO im_refresh_token (id, user_id, username, token, device_id, device_type, " +
            "device_name, ip_address, expiry_date, used, revoked, created_at, updated_at) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.id}, #{item.userId}, #{item.username}, #{item.token}, #{item.deviceId}, " +
            "#{item.deviceType}, #{item.deviceName}, #{item.ipAddress}, #{item.expiryDate}, " +
            "#{item.used}, #{item.revoked}, #{item.createdAt}, #{item.updatedAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<RefreshToken> tokens);

    /**
     * 清理所有过期和已使用的 Token
     * 建议定期执行（如每天凌晨）
     *
     * @return 删除的总行数
     */
    @Delete("DELETE FROM im_refresh_token WHERE expiry_date < NOW() OR used = 1")
    int cleanupExpiredAndUsed();
}
