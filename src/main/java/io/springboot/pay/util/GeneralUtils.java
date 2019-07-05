package io.springboot.pay.util;


import java.awt.Image;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

/**
 * @author KevinBlandy K神
 * @mender 王小明
 */
public class GeneralUtils {

    private static final int[] NUMBERS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    private static final String[] LETTERS = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    /**
     * 数组是否为空
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T[] t) {
        return t == null || t.length == 0;
    }

    /**
     * 字符串是否为空
     *
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    /**
     * Set集合是否为空
     *
     * @param set
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(Set<T> set) {
        return set == null || set.isEmpty();
    }

    /**
     * Collection是否为空
     *
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Map是否为空
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 获取32位无符号大写UUID
     *
     * @return
     */
    public static String getUpperCaseUUID() {
        return getLowerCaseUUID().toUpperCase();
    }

    /**
     * 获取32位无符号小写UUID
     *
     * @return
     */
    public static String getLowerCaseUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取0-9随机字符字符串
     *
     * @param length
     * @return
     */
    public static String getRandomNum(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int x = 0; x < length; x++) {
            sb.append(NUMBERS[random.nextInt(NUMBERS.length)]);
        }
        return sb.toString();
    }

    /**
     * 获取a-z随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomLetter(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int x = 0; x < length; x++) {
            sb.append(LETTERS[random.nextInt(LETTERS.length)]);
        }
        return sb.toString();
    }

    /**
     * 获取随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomStr(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int x = 0; x < length; x++) {
            if (x % 2 == 1) {
                sb.append(NUMBERS[random.nextInt(NUMBERS.length)]);
            } else {
                sb.append(LETTERS[random.nextInt(LETTERS.length)]);
            }
        }
        return sb.toString();
    }

    /**
     * 随机编号
     * 年月日开头默认24位
     *
     * @return yyyyMMdd + randomNum
     */
    public static String next() {
        String randomNum = getRandomNum(16);
        String yyMMdd = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return yyMMdd + randomNum;
    }

    /**
     * 获取数组中的随机默认值
     *
     * @param arr
     * @return
     */
    public static <T> T choose(T[] arr) {
        return arr[new Random().nextInt(arr.length)];
    }

    /**
     * 获取集合中的随机值
     *
     * @param list
     * @return
     */
    public static <T> T choose(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }


    /**
     * 对字符串进行Base64编码
     *
     * @param data
     * @return
     */
    public static String base64Encode(String data) {
        return new String(Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * 对指定Base64编码字符串进行解码
     *
     * @param data
     * @return
     */
    public static String base64Decode(String data) {
        return new String(Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * 当前操作系统是否是Windows操作系统
     *
     * @return
     */
    public static Boolean isWindows() {
        return System.getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    /**
     * 判断文件是否是图片文件
     *
     * @param inputStream
     * @return
     */
    public static boolean isImage(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        }
        Image image;
        try {
            image = ImageIO.read(inputStream);
            return !(image == null || image.getWidth(null) <= 0 || image.getHeight(null) <= 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 去除空格
     *
     * @param value
     * @return
     */
    public static String trim(String value) {
        return value.replaceAll("\\s*", "").replaceAll(" ", "");
    }

    /**
     * 删除文件
     *
     * @param files
     */
    public static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 判断是否是数字
     *
     * @param number
     * @return
     */
    public static boolean checkNumber(String number) {
        if (number == null) {
            return false;
        }
        Pattern p = Pattern.compile("^(0|\\+?[1-9][0-9]*)$");
        Matcher matcher = p.matcher(number);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 获取客户端真实的IP
     *
     * @param request
     * @return
     */
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Requested-For");
        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
