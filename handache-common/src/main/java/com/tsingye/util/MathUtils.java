package com.tsingye.util;

import java.math.BigDecimal;

/**
 * simple math utils
 * Created by tsingye on 2017/1/22.
 */
public class MathUtils {

    private static final double EARTH_RADIUS = 6371e3; //地球半径，单位米

    /**
     * 保留指定位数的小数，对小数部分向下取整丢弃多余部分
     *
     * @param rawValue 原始值
     * @param level    要保留的小数位数
     * @return
     */
    public static double roundDown(double rawValue, int level) {
        return BigDecimal.valueOf(rawValue)
                         .setScale(level, BigDecimal.ROUND_DOWN)
                         .doubleValue();
    }

    /**
     * 保留指定位数的小数，对小数部分向上取整丢弃多余部分
     *
     * @param rawValue 原始值
     * @param level    要保留的小数位数
     * @return
     */
    public static double roundUp(double rawValue, int level) {
        return BigDecimal.valueOf(rawValue)
                         .setScale(level, BigDecimal.ROUND_UP)
                         .doubleValue();
    }

    /**
     * 保留指定位数的小数，对小数部分四舍五入取整丢弃多余部分
     *
     * @param rawValue 原始值
     * @param level    要保留的小数位数
     * @return
     */
    public static double roundHalfUp(double rawValue, int level) {
        return BigDecimal.valueOf(rawValue)
                         .setScale(level, BigDecimal.ROUND_HALF_UP)
                         .doubleValue();
    }

    /**
     * 计算WGS84坐标系下两个坐标之间的球面距离，此计算是仅计算两点位于WGS84球面坐标系的球面距离<br/>
     * Haversine formula: <br/>
     * a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2) <br/>
     * c = 2 ⋅ atan2( √a, √(1−a) ) <br/>
     * d = R ⋅ c <br/>
     * where φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km); <br/>
     * note that angles need to be in radians to pass to trig functions!
     *
     * @param lng1 第一个点的经度
     * @param lat1 第一个点的纬度
     * @param lng2 第二个点的经度
     * @param lat2 第二个点的纬度
     * @return 两GPS坐标点之间的球面距离，单位是米
     */
    public static double calcDistance(double lng1, double lat1, double lng2, double lat2) {
        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δφ = Math.toRadians(lat2 - lat1);
        double Δλ = Math.toRadians(lng2 - lng1);
        double a = Math.pow(Math.sin(Δφ / 2), 2) + Math.cos(φ1) * Math.cos(φ2) * Math.pow(Math.sin(Δλ / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
