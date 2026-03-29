package com.im.backend.modules.local_life.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local_life.checkin.entity.UserPointAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分账户Mapper
 */
@Mapper
public interface UserPointAccountMapper extends BaseMapper<UserPointAccount> {
}
