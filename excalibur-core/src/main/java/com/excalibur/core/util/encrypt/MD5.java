package com.excalibur.core.util.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static String toMd5(String text) {
        return toMd5(text.getBytes());
    }

    public static String toMd5(byte[] bytes) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static final String Encrypt_MD5_Pwd(String pwd) {
        final byte[] data_key = "fdjf,jkgfkl".getBytes();
        byte[] data_pwd = pwd.getBytes();

        int size_key = data_key.length;
        int size_pwd = data_pwd.length;
        byte[] data = new byte[size_pwd + 4 + size_key];

        // Task: data = data_pwd + data_fk + data_key
        final byte[] data_fk = new byte[]{(byte) 163, (byte) 172, (byte) 161, (byte) 163};
        System.arraycopy(data_pwd, 0, data, 0, size_pwd);
        System.arraycopy(data_fk, 0, data, size_pwd, data_fk.length);
        System.arraycopy(data_key, 0, data, size_pwd + data_fk.length, size_key);

        // use byte array to MD5
        String value = toMd5(data).toLowerCase();
        return value;
    }

    private static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex).append(separator);
        }
        return hexString.toString();
    }
}
