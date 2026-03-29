package com.im.backend.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单号生成工具
 */
public class OrderNoGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * 生成配送订单号
     * 格式: DL + 年月日时分秒 + 4位随机数
     */
    public static String generateDeliveryOrderNo() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String random = String.format("%04d", RANDOM.nextInt(10000));
        return "DL" + timestamp + random;
    }

    /**
     * 生成骑手编号
     * 格式: RD + 年月日 + 4位随机数
     */
    public static String generateRiderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", RANDOM.nextInt(10000));
        return "RD" + timestamp + random;
    }
}
