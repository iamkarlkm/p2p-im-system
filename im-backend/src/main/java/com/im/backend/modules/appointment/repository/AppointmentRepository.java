package com.im.backend.modules.appointment.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.appointment.entity.Appointment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 预约数据访问层
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Repository
public interface AppointmentRepository extends BaseMapper<Appointment> {

    /**
     * 根据预约号查询
     */
    @Select("SELECT * FROM im_appointment WHERE appointment_no = #{appointmentNo} AND deleted = 0")
    Appointment findByAppointmentNo(@Param("appointmentNo") String appointmentNo);

    /**
     * 查询用户的预约列表
     */
    @Select("SELECT * FROM im_appointment WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Appointment> findByUserId(@Param("userId") Long userId);

    /**
     * 查询商户的预约列表
     */
    @Select("SELECT * FROM im_appointment WHERE merchant_id = #{merchantId} AND deleted = 0 ORDER BY appointment_date, start_time")
    List<Appointment> findByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询指定日期的预约
     */
    @Select("SELECT * FROM im_appointment WHERE merchant_id = #{merchantId} AND appointment_date = #{date} AND deleted = 0 ORDER BY start_time")
    List<Appointment> findByMerchantAndDate(@Param("merchantId") Long merchantId, @Param("date") LocalDate date);

    /**
     * 查询时间冲突的预约
     */
    @Select("SELECT * FROM im_appointment WHERE merchant_id = #{merchantId} " +
            "AND appointment_date = #{date} AND status IN ('PENDING', 'CONFIRMED', 'IN_SERVICE') " +
            "AND ((start_time < #{endTime} AND end_time > #{startTime}) OR (start_time = #{startTime})) " +
            "AND deleted = 0")
    List<Appointment> findConflictingAppointments(@Param("merchantId") Long merchantId,
                                                   @Param("date") LocalDate date,
                                                   @Param("startTime") LocalTime startTime,
                                                   @Param("endTime") LocalTime endTime);

    /**
     * 查询服务人员的预约
     */
    @Select("SELECT * FROM im_appointment WHERE staff_id = #{staffId} AND appointment_date = #{date} " +
            "AND status IN ('PENDING', 'CONFIRMED') AND deleted = 0 ORDER BY start_time")
    List<Appointment> findByStaffAndDate(@Param("staffId") Long staffId, @Param("date") LocalDate date);

    /**
     * 更新预约状态
     */
    @Update("UPDATE im_appointment SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 统计商户某日的预约数
     */
    @Select("SELECT COUNT(*) FROM im_appointment WHERE merchant_id = #{merchantId} " +
            "AND appointment_date = #{date} AND deleted = 0")
    int countByMerchantAndDate(@Param("merchantId") Long merchantId, @Param("date") LocalDate date);

    /**
     * 统计用户待确认的预约数
     */
    @Select("SELECT COUNT(*) FROM im_appointment WHERE user_id = #{userId} " +
            "AND status = 'PENDING' AND deleted = 0")
    int countPendingByUser(@Param("userId") Long userId);

    /**
     * 查询即将到期的预约
     */
    @Select("SELECT * FROM im_appointment WHERE status = 'CONFIRMED' " +
            "AND appointment_date = CURDATE() AND start_time BETWEEN CURTIME() AND DATE_ADD(CURTIME(), INTERVAL 30 MINUTE) " +
            "AND remind_status = 'NONE' AND deleted = 0")
    List<Appointment> findExpiringAppointments();
}
