package com.aliyun.kms.utils;

import java.util.Arrays;

public class ArrayUtils {
    private ArrayUtils() {
    }

    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (int index = 0; index < rest.length; ++index) {
            totalLength +=  rest[index].length;
        }

        byte[] result = Arrays.copyOf(first, totalLength);
        for (int index = 0,offset = first.length; index < rest.length; ++index) {
            byte[] array = rest[index];
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }
}
