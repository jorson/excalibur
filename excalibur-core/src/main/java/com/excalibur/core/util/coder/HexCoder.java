package com.excalibur.core.util.coder;

/**
 * HexCoder
 * <p/>
 */
public class HexCoder {

    /**
     * byte数组转成十六进制字符串
     *
     * @param bytes
     * @return
     */
    public static final String encodeHex(byte bytes[]) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 16)
                buf.append("0");
            buf.append(Long.toString(bytes[i] & 0xff, 16).toUpperCase());
        }
        return buf.toString();
    }

    /**
     * 十六进制字符串转成byte数组
     * <p/>
     *
     * @param src
     * @return
     */
    public static final byte[] decodeHex(String src) {
        int size = src.length();
        byte[] data = new byte[size >> 1];
        for (int i = 0; i < size - 1; i += 2) {
            String d = src.substring(i, i + 2);
            data[i >> 1] = (byte) Integer.parseInt(d, 16);
        }
        return data;
    }

}
