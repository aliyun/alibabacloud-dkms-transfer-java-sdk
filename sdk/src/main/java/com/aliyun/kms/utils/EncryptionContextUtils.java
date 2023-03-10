package com.aliyun.kms.utils;

import com.aliyun.tea.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class EncryptionContextUtils {
    private final static Gson gson = new GsonBuilder().create();
    private final static String SHA_256 = "SHA-256";

    private EncryptionContextUtils() {

    }

    public static byte[] sortAndEncode(String encryptionContext, Charset charset) {
        if (StringUtils.isEmpty(encryptionContext)) {
            return null;
        }
        Map<String, Object> map = gson.fromJson(encryptionContext, Map.class);
        if (map == null) {
            throw new IllegalArgumentException("param[EncryptionContext] is invalid");
        }
        List<String> keyList = new ArrayList<String>(map.keySet());
        Collections.sort(keyList);
        StringBuilder sb = new StringBuilder();
        for (String key : keyList) {
            sb.append(key).append("=").append(map.get(key)).append('&');
        }
        if (sb.indexOf("&") > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return getSha256(sb.toString(), charset);
    }


    /**
     * sha256加密
     *
     * @param str 要加密的字符串
     * @param charset 字符集编码
     * @return 加密后的字节数组
     */
    public static byte[] getSha256(String str, Charset charset) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(SHA_256);
            messageDigest.update(str.getBytes(charset));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
