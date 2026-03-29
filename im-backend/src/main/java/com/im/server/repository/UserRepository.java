package com.im.server.repository;

import com.im.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
    
    /**
     * 根据手机号查询用户
     */
    User findByPhone(String phone);
    
    /**
     * 根据邮箱查询用户
     */
    User findByEmail(String email);
}
