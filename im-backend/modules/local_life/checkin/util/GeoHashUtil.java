package com.im.backend.modules.local_life.checkin.util;

/**
 * GeoHash工具类
 */
public class GeoHashUtil {
    
    private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
    
    /**
     * 将经纬度编码为GeoHash
     */
    public static String encode(double latitude, double longitude, int precision) {
        int[] bits = {16, 8, 4, 2, 1};
        double[] latRange = {-90.0, 90.0};
        double[] lonRange = {-180.0, 180.0};
        
        StringBuilder geohash = new StringBuilder();
        int bit = 0;
        int ch = 0;
        boolean even = true;
        
        while (geohash.length() < precision) {
            double mid;
            if (even) {
                mid = (lonRange[0] + lonRange[1]) / 2;
                if (longitude > mid) {
                    ch |= bits[bit];
                    lonRange[0] = mid;
                } else {
                    lonRange[1] = mid;
                }
            } else {
                mid = (latRange[0] + latRange[1]) / 2;
                if (latitude > mid) {
                    ch |= bits[bit];
                    latRange[0] = mid;
                } else {
                    latRange[1] = mid;
                }
            }
            
            even = !even;
            if (bit < 4) {
                bit++;
            } else {
                geohash.append(BASE32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }
        
        return geohash.toString();
    }
    
    /**
     * 计算两点间距离(米)
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // 地球半径(米)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 获取邻居GeoHash
     */
    public static String[] getNeighbors(String geohash) {
        // 简化实现，返回空数组
        return new String[8];
    }
}
