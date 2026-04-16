package com.im.service.user.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.im.service.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问接口 - MyBatis-Plus
 * 对应数据库表: im_user
 * 
 * @author IM Team
 * @version 1.0
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {

    // ========== 基础查询方法 ==========

    /**
     * 根据用户名查询用户
     */
    default User findByUsername(String username) {
        return selectOne(new QueryWrapper<User>().eq("username", username).eq("deleted", false));
    }

    /**
     * 根据手机号查询用户
     */
    default User findByPhone(String phone) {
        return selectOne(new QueryWrapper<User>().eq("phone", phone).eq("deleted", false));
    }

    /**
     * 根据邮箱查询用户
     */
    default User findByEmail(String email) {
        return selectOne(new QueryWrapper<User>().eq("email", email).eq("deleted", false));
    }

    /**
     * 根据ID查询用户（排除已删除）
     */
    default User findById(Long id) {
        return selectOne(new QueryWrapper<User>().eq("id", id).eq("deleted", false));
    }

    // ========== 存在性检查 ==========

    /**
     * 检查用户名是否存在
     */
    default boolean existsByUsername(String username) {
        return selectCount(new QueryWrapper<User>().eq("username", username)) > 0;
    }

    /**
     * 检查手机号是否存在
     */
    default boolean existsByPhone(String phone) {
        return selectCount(new QueryWrapper<User>().eq("phone", phone)) > 0;
    }

    /**
     * 检查邮箱是否存在
     */
    default boolean existsByEmail(String email) {
        return selectCount(new QueryWrapper<User>().eq("email", email)) > 0;
    }

    // ========== 搜索查询 ==========

    /**
     * 搜索用户（按用户名、昵称、手机号、邮箱）
     */
    default List<User> searchUsers(String keyword) {
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("deleted", false)
                .eq("status", 1)  // 只搜索正常状态的用户
                .and(w -> w.like("username", keyword)
                           .or()
                           .like("nickname", keyword)
                           .or()
                           .like("phone", keyword)
                           .or()
                           .like("email", keyword))
                .last("LIMIT 20");  // 限制返回数量
        return selectList(wrapper);
    }

    /**
     * 按用户名模糊搜索
     */
    default List<User> searchByUsernameLike(String username) {
        return selectList(new QueryWrapper<User>()
                .like("username", username)
                .eq("deleted", false)
                .eq("status", 1)
                .last("LIMIT 20"));
    }

    /**
     * 按昵称模糊搜索
     */
    default List<User> searchByNicknameLike(String nickname) {
        return selectList(new QueryWrapper<User>()
                .like("nickname", nickname)
                .eq("deleted", false)
                .eq("status", 1)
                .last("LIMIT 20"));
    }

    /**
     * 通过手机号搜索用户（精确匹配）
     */
    default User findByPhoneExact(String phone) {
        return selectOne(new QueryWrapper<User>()
                .eq("phone", phone)
                .eq("deleted", false));
    }

    // ========== 在线状态查询 ==========

    /**
     * 查询在线用户列表
     */
    default List<User> findOnlineUsers() {
        return selectList(new QueryWrapper<User>()
                .eq("online_status", "ONLINE")
                .eq("deleted", false));
    }

    /**
     * 根据在线状态查询用户
     */
    default List<User> findByOnlineStatus(String status) {
        return selectList(new QueryWrapper<User>()
                .eq("online_status", status)
                .eq("deleted", false));
    }

    /**
     * 查询指定时间后未活跃的用户
     */
    default List<User> findInactiveUsersBefore(LocalDateTime time) {
        return selectList(new QueryWrapper<User>()
                .lt("last_online_at", time)
                .eq("online_status", "ONLINE")
                .eq("deleted", false));
    }

    // ========== 状态更新方法 ==========

    /**
     * 更新用户在线状态
     */
    @Update("UPDATE im_user SET online_status = #{status}, last_online_at = NOW(), updated_at = NOW() WHERE id = #{userId}")
    int updateOnlineStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 更新最后登录信息
     */
    @Update("UPDATE im_user SET last_login_at = NOW(), last_login_ip = #{ip}, last_login_device = #{device}, " +
            "online_status = 'ONLINE', last_online_at = NOW(), updated_at = NOW() WHERE id = #{userId}")
    int updateLastLogin(@Param("userId") Long userId, @Param("ip") String ip, @Param("device") String device);

    /**
     * 更新用户头像
     */
    @Update("UPDATE im_user SET avatar_url = #{avatarUrl}, updated_at = NOW() WHERE id = #{userId}")
    int updateAvatar(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);

    /**
     * 更新用户状态（启用/禁用）
     */
    @Update("UPDATE im_user SET status = #{status}, updated_at = NOW() WHERE id = #{userId}")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 增加登录失败次数
     */
    @Update("UPDATE im_user SET login_fail_count = login_fail_count + 1, updated_at = NOW() WHERE id = #{userId}")
    int incrementLoginFailCount(@Param("userId") Long userId);

    /**
     * 重置登录失败次数
     */
    @Update("UPDATE im_user SET login_fail_count = 0, login_lock_until = NULL, updated_at = NOW() WHERE id = #{userId}")
    int resetLoginFailCount(@Param("userId") Long userId);

    /**
     * 锁定账号
     */
    @Update("UPDATE im_user SET login_lock_until = #{lockUntil}, updated_at = NOW() WHERE id = #{userId}")
    int lockAccount(@Param("userId") Long userId, @Param("lockUntil") LocalDateTime lockUntil);

    /**
     * 更新密码
     */
    @Update("UPDATE im_user SET password_hash = #{passwordHash}, updated_at = NOW() WHERE id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

    // ========== 隐私设置更新 ==========

    /**
     * 更新添加好友权限
     */
    @Update("UPDATE im_user SET add_friend_permission = #{permission}, updated_at = NOW() WHERE id = #{userId}")
    int updateAddFriendPermission(@Param("userId") Long userId, @Param("permission") String permission);

    /**
     * 更新隐私设置
     */
    @Update("UPDATE im_user SET allow_search = #{allowSearch}, allow_phone_search = #{allowPhoneSearch}, " +
            "allow_email_search = #{allowEmailSearch}, online_status_visibility = #{onlineVisibility}, " +
            "last_seen_visibility = #{lastSeenVisibility}, profile_visibility = #{profileVisibility}, " +
            "updated_at = NOW() WHERE id = #{userId}")
    int updatePrivacySettings(@Param("userId") Long userId,
                              @Param("allowSearch") Boolean allowSearch,
                              @Param("allowPhoneSearch") Boolean allowPhoneSearch,
                              @Param("allowEmailSearch") Boolean allowEmailSearch,
                              @Param("onlineVisibility") String onlineVisibility,
                              @Param("lastSeenVisibility") String lastSeenVisibility,
                              @Param("profileVisibility") String profileVisibility);

    // ========== 批量查询 ==========

    /**
     * 根据ID列表批量查询用户
     */
    default List<User> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectList(new QueryWrapper<User>()
                .in("id", ids)
                .eq("deleted", false));
    }

    /**
     * 统计用户总数
     */
    default long countAll() {
        return selectCount(new QueryWrapper<User>().eq("deleted", false));
    }

    /**
     * 统计在线用户数
     */
    default long countOnline() {
        return selectCount(new QueryWrapper<User>().eq("online_status", "ONLINE").eq("deleted", false));
    }

    // ========== 删除相关 ==========

    /**
     * 逻辑删除用户
     */
    @Update("UPDATE im_user SET deleted = 1, status = 2, updated_at = NOW() WHERE id = #{userId}")
    int softDelete(@Param("userId") Long userId);

    /**
     * 恢复已删除用户
     */
    @Update("UPDATE im_user SET deleted = 0, status = 1, updated_at = NOW() WHERE id = #{userId}")
    int restore(@Param("userId") Long userId);
}
