package com.sdk.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;


public class SignUtil {

    /**
     * 生成签名
     * @param map 签名串
     * @param appSecret 密钥
     * @return String
     */
    public static String getSign(Map<String, String> map,String appSecret) {

        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }

            }
			sb.append("key="+appSecret);
            result = sb.toString();

            //进行MD5加密
            result = DigestUtils.md5Hex(result).toUpperCase();

            result = HmacSHA256.sign(result,appSecret).toUpperCase();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

}
