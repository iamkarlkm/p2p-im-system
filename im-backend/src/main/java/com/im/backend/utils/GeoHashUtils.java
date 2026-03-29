package com.im.backend.utils;

/**
 * GeoHash编码工具类
 * 实现基于Base32的GeoHash算法，用于空间索引和位置编码
 * 
 * GeoHash原理：
 * 1. 将经纬度分别进行二进制编码
 * 2. 交替合并经纬度二进制位
 * 3. 使用Base32编码转换为字符串
 * 4. 精度越高，网格越小（字符越多）
 * 
 * @author IM Development Team
 * @version 1.0
 */
public class GeoHashUtils {
    
    /** Base32编码字符集 */
    private static final String BASE32_CHARS = "0123456789bcdefghjkmnpqrstuvwxyz";
    
    /** Base32字符到索引映射 */
    private static final int[] BASE32_DECODE = new int[128];
    
    static {
        for (int i = 0; i < BASE32_CHARS.length(); i++) {
            BASE32_DECODE[BASE32_CHARS.charAt(i)] = i;
        }
    }
    
    /**
     * 编码经纬度为GeoHash字符串
     * 
     * @param latitude 纬度 (-90 ~ 90)
     * @param longitude 经度 (-180 ~ 180)
     * @param precision 精度 (1-12)
     * @return GeoHash字符串
     */
    public static String encode(double latitude, double longitude, int precision) {
        if (precision < 1 || precision > 12) {
            precision = 9;
        }
        
        // 经纬度范围
        double[] latRange = {-90.0, 90.0};
        double[] lonRange = {-180.0, 180.0};
        
        // 二进制位数组
        boolean[] bits = new boolean[precision * 5];
        int bitIndex = 0;
        
        // 交替编码经度和纬度
        while (bitIndex < bits.length) {
            // 编码经度（奇数位）
            if (bitIndex < bits.length) {
                double lonMid = (lonRange[0] + lonRange[1]) / 2;
                if (longitude >= lonMid) {
                    bits[bitIndex] = true;
                    lonRange[0] = lonMid;
                } else {
                    bits[bitIndex] = false;
                    lonRange[1] = lonMid;
                }
                bitIndex++;
            }
            
            // 编码纬度（偶数位）
            if (bitIndex < bits.length) {
                double latMid = (latRange[0] + latRange[1]) / 2;
                if (latitude >= latMid) {
                    bits[bitIndex] = true;
                    latRange[0] = latMid;
                } else {
                    bits[bitIndex] = false;
                    latRange[1] = latMid;
                }
                bitIndex++;
            }
        }
        
        // 转换为Base32编码
        StringBuilder geohash = new StringBuilder();
        for (int i = 0; i < bits.length; i += 5) {
            int value = 0;
            for (int j = 0; j < 5 && (i + j) < bits.length; j++) {
                value = (value << 1) | (bits[i + j] ? 1 : 0);
            }
            geohash.append(BASE32_CHARS.charAt(value));
        }
        
        return geohash.toString();
    }
    
    /**
     * 默认精度编码
     */
    public static String encode(double latitude, double longitude) {
        return encode(latitude, longitude, 9);
    }
    
    /**
     * 解码GeoHash为经纬度范围
     * 
     * @param geohash GeoHash字符串
     * @return [纬度最小值, 纬度最大值, 经度最小值, 经度最大值]
     */
    public static double[] decodeBounds(String geohash) {
        double[] latRange = {-90.0, 90.0};
        double[] lonRange = {-180.0, 180.0};
        
        boolean isEven = true;
        for (int i = 0; i < geohash.length(); i++) {
            int charValue = BASE32_DECODE[geohash.charAt(i)];
            
            for (int j = 4; j >= 0; j--) {
                boolean bit = ((charValue >> j) & 1) == 1;
                
                if (isEven) {
                    // 经度
                    double mid = (lonRange[0] + lonRange[1]) / 2;
                    if (bit) {
                        lonRange[0] = mid;
                    } else {
                        lonRange[1] = mid;
                    }
                } else {
                    // 纬度
                    double mid = (latRange[0] + latRange[1]) / 2;
                    if (bit) {
                        latRange[0] = mid;
                    } else {
                        latRange[1] = mid;
                    }
                }
                isEven = !isEven;
            }
        }
        
        return new double[]{latRange[0], latRange[1], lonRange[0], lonRange[1]};
    }
    
    /**
     * 获取GeoHash中心点坐标
     */
    public static double[] decode(String geohash) {
        double[] bounds = decodeBounds(geohash);
        double lat = (bounds[0] + bounds[1]) / 2;
        double lon = (bounds[2] + bounds[3]) / 2;
        return new double[]{lat, lon};
    }
    
    /**
     * 获取指定精度的GeoHash
     */
    public static String adjustPrecision(String geohash, int precision) {
        if (geohash == null || geohash.isEmpty()) {
            return "";
        }
        if (precision >= geohash.length()) {
            return geohash;
        }
        return geohash.substring(0, precision);
    }
    
    /**
     * 获取邻居GeoHash（8个方向）
     */
    public static String[] getNeighbors(String geohash) {
        String[] neighbors = new String[8];
        String[] directions = {"n", "ne", "e", "se", "s", "sw", "w", "nw"};
        
        for (int i = 0; i < directions.length; i++) {
            neighbors[i] = getNeighbor(geohash, directions[i]);
        }
        
        return neighbors;
    }
    
    /**
     * 获取单个方向的邻居
     */
    public static String getNeighbor(String geohash, String direction) {
        if (geohash == null || geohash.isEmpty()) {
            return "";
        }
        
        char lastChar = geohash.charAt(geohash.length() - 1);
        String parent = geohash.substring(0, geohash.length() - 1);
        
        int charIndex = BASE32_DECODE[lastChar];
        
        // 邻居计算逻辑（简化版）
        // 实际生产环境需要处理边界情况
        int[][] neighborTable = {
            {1, 0}, {2, 1}, {3, 2}, {4, 3}, {5, 4}, {6, 5}, {7, 6}, {8, 7}
        };
        
        // 根据方向调整
        switch (direction.toLowerCase()) {
            case "n": charIndex = (charIndex + 8) % 32; break;
            case "s": charIndex = (charIndex - 8 + 32) % 32; break;
            case "e": charIndex = (charIndex + 1) % 32; break;
            case "w": charIndex = (charIndex - 1 + 32) % 32; break;
            default: return geohash;
        }
        
        return parent + BASE32_CHARS.charAt(charIndex);
    }
    
    /**
     * 计算两个GeoHash之间的距离（米）
     * 使用Haversine公式
     */
    public static double calculateDistance(String geohash1, String geohash2) {
        double[] coord1 = decode(geohash1);
        double[] coord2 = decode(geohash2);
        
        return haversineDistance(coord1[0], coord1[1], coord2[0], coord2[1]);
    }
    
    /**
     * Haversine距离计算
     */
    private static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000; // 地球半径（米）
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 根据精度获取网格大小（米）
     */
    public static double getCellSize(int precision) {
        // 近似网格大小（米）
        double[] sizes = {
            5000000, 1250000, 156000, 39100, 4900, 1200, 150, 20, 2.4, 0.6, 0.07, 0.02
        };
        
        if (precision < 1 || precision > sizes.length) {
            return sizes[8]; // 默认9级精度
        }
        return sizes[precision - 1];
    }
    
    /**
     * 根据距离计算推荐精度
     */
    public static int calculatePrecision(double distanceMeters) {
        if (distanceMeters <= 5) return 9;
        if (distanceMeters <= 20) return 8;
        if (distanceMeters <= 150) return 7;
        if (distanceMeters <= 1200) return 6;
        if (distanceMeters <= 4900) return 5;
        if (distanceMeters <= 39100) return 4;
        return 3;
    }
}
