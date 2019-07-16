package com.ucloud.library.netanalysis.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by joshua on 2018/12/14 17:38.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public class Encryptor {
    public static final String TAG = "Encryptor";
    public static final String RSA = "RSA";
    
    /**
     * RSA 公钥加密
     *
     * @param source 加密原文
     * @param key    加密公钥
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encryptRSA(byte[] source, PublicKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(RSA + "/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(source);
    }
    
    public static byte[] decryptRSA(byte[] content, PrivateKey key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(RSA + "/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(content);
    }
    
    /**
     * 过滤RSA秘钥对字符串
     *
     * @param key 秘钥对原文
     * @return 过滤BEGIN和END后的实际公钥
     */
    public static String filterRsaKey(String key) {
        if (TextUtils.isEmpty(key))
            throw new IllegalArgumentException("Key is empty!");
        
        StringReader sr = new StringReader(key);
        BufferedReader br = new BufferedReader(sr);
        StringBuffer res = new StringBuffer();
        try {
            String tmp;
            while ((tmp = br.readLine()) != null) {
                if (tmp.contains("-BEGIN"))
                    continue;
                if (tmp.contains("-END"))
                    continue;
                
                res.append(res.length() > 0 ? ("\n" + tmp) : tmp);
            }
        } catch (IOException e) {
            JLog.E(TAG, "filterRsaKey occur error: " + e.getMessage());
        } finally {
            BaseUtil.closeAllCloseable(br, sr);
            
            return res.toString();
        }
    }
    
    /**
     * 通过公钥byte[](publicKey.getEncoded())将公钥还原，适用于RSA算法
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
    
    /**
     * 使用N、e值还原公钥
     *
     * @param modulus
     * @param publicExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(String modulus, String publicExponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
    
    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static PublicKey getPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
        return getPublicKey(buffer);
    }
    
    /**
     * 从文件中输入流中加载公钥
     *
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public static PublicKey getPublicKey(InputStream in) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            return getPublicKey(readKey(in));
        } catch (IOException e) {
            throw new IOException("公钥数据流读取错误", e);
        } catch (NullPointerException e) {
            throw new NullPointerException("公钥输入流为空");
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("无此算法", e);
        } catch (InvalidKeySpecException e) {
            throw new InvalidKeySpecException("公钥非法", e);
        }
    }
    
    public static PrivateKey getPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.decode(privateKeyStr, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    
    /**
     * 读取密钥信息
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static String readKey(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) == '-') {
                continue;
            } else {
                sb.append(readLine);
                sb.append('\r');
            }
        }
        
        return sb.toString();
    }
}
