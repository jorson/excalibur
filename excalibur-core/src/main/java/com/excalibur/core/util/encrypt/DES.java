package com.excalibur.core.util.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * DES对称加密算法
 * <p/>
 * 工作模式: CBC (需要设置初始化向量)
 * 填充方式: PKCS5Padding
 *
 * @author yangz
 */
public class DES {
    /**
     * 密钥算法
     * java支持56位密钥，bouncycastle支持64位
     */
    public static final String KEY_ALGORITHM = "DES";

    /**
     * 加密/解密算法/工作模式/填充方式
     */
    public static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";

    /**
     * 转换密钥
     *
     * @param key 二进制密钥
     * @return Key 密钥
     */
    public static Key toKey(byte[] key) {
        return new SecretKeySpec(key, "DES");
    }

    /**
     * 加密数据
     *
     * @param iv   初始化向量
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密后的数据
     */
    public static byte[] encrypt(String iv, String data, String key) throws GeneralSecurityException {
        return encrypt(iv.getBytes(), data.getBytes(), key.getBytes());
    }

    /**
     * 加密数据
     *
     * @param iv   初始化向量
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密后的数据
     */
    public static byte[] encrypt(byte[] iv, byte[] data, byte[] key) throws GeneralSecurityException {
        //还原密钥
        Key k = toKey(key);
        //实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, k, new IvParameterSpec(iv));
        //执行操作
        return cipher.doFinal(data);
    }

    /**
     * 解密数据
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密后的数据
     */
    public static byte[] decrypt(String iv, String data, String key) throws GeneralSecurityException {
        return decrypt(iv.getBytes(), data.getBytes(), key.getBytes());
    }

    /**
     * 解密数据
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密后的数据
     */
    public static byte[] decrypt(byte[] iv, byte[] data, byte[] key) throws GeneralSecurityException {
        //欢迎密钥
        Key k = toKey(key);
        //实例化
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
        //执行操作
        return cipher.doFinal(data);
    }

}