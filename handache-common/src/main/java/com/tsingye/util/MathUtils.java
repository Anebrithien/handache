package com.tsingye.util;

import java.math.BigDecimal;

/**
 * simple math utils
 * Created by tsingye on 2017/1/22.
 */
public class MathUtils {

    private static final double EARTH_RADIUS = 6371e3; //地球半径，单位米

    private static final String HEXES = "0123456789abcdef";

    /**
     * convert long to byte array as 4 bytes unsigned long.
     * so the number should be less than 4294967295 and positive
     *
     * @param unsignedLong [0, 4294967295]
     * @return 4 Bytes length array
     */
    public static byte[] toByteArray(long unsignedLong) {
        if (unsignedLong < 0 || unsignedLong > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("the number " + unsignedLong + "is NOT in range [0, 4294967295]");
        }
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; --i) {
            result[i] = (byte) (unsignedLong & 0xFFL);
            unsignedLong >>= 8;
        }
        return result;
    }

    /**
     * convert long to byte array in little-endian as 4 bytes unsigned long.
     * so the number should be less than 4294967295 and positive
     *
     * @param unsignedLong [0, 4294967295]
     * @return 4 Bytes length array
     */
    public static byte[] toByteArrayLE(long unsignedLong) {
        if (unsignedLong < 0 || unsignedLong > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("the number " + unsignedLong + "is NOT in range [0, 4294967295]");
        }
        byte[] result = new byte[4];
        for (int i = 0; i < 4; ++i) {
            result[i] = (byte) (unsignedLong & 0xFFL);
            unsignedLong >>= 8;
        }
        return result;
    }

    /**
     * just a simple method to transform byte to hex
     *
     * @param b the byte
     * @return a lower case hex string that without "0x"
     */
    public static String toHexString(byte b) {
        char[] s = new char[2];
        s[0] = HEXES.charAt((b & 0xF0) >> 4);
        s[1] = HEXES.charAt((b & 0x0F));
        return new String(s);
    }

    /**
     * convert byte array to a long hex String
     *
     * @param bytes the byte array
     * @return the hex String
     */
    public static String toHexString(byte[] bytes) {
        int len = bytes.length;
        StringBuilder sb = new StringBuilder(2 * len);
        for (byte b : bytes) {
            sb.append(HEXES.charAt((b & 0xF0) >> 4))
              .append(HEXES.charAt(b & 0x0F));
        }
        return sb.toString();
    }

    /**
     * parse hexString like "fe13012501fa2e003f01011d0900000056b9591f45" to byte[]
     *
     * @param hexString the hexString, should be valid
     * @return the byte array
     */
    public static byte[] toByteArray(String hexString) {
        if (null == hexString || hexString.isEmpty()) {
            throw new IllegalArgumentException("do NOT use null or empty hexString to fool me!");
        }
        String str = hexString.trim().replace("0x", "").toLowerCase();
        int len = str.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("are you sure " + hexString + " is hexString?");
        }
        byte[] result = new byte[len / 2];
        String cursor;
        for (int i = 0; i < len; i += 2) {
            cursor = str.substring(i, i + 2);
            result[i / 2] = (byte) Integer.parseInt(cursor, 16);
        }
        return result;
    }

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
