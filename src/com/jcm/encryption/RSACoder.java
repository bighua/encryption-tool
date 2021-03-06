package com.jcm.encryption;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import com.jcm.Util;

public class RSACoder {   
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 用私钥对信息生成数字签名 
     *   
     * @param data 
     *            加密数据 
     * @param privateKey 
     *            私钥 
     *   
     * @return 
     * @throws Exception 
     */ 
    public static String sign(byte[] data, String privateKey) throws Exception {   
        // 解密由base64编码的私钥   
        byte[] keyBytes = Base64.decodeBase64(privateKey);

        // 构造PKCS8EncodedKeySpec对象   
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法   
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取私钥匙对象   
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名   
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);

        return Base64.encodeBase64String(signature.sign());
    }   

    /**
     * 校验数字签名 
     *   
     * @param data 
     *            加密数据 
     * @param publicKey 
     *            公钥 
     * @param sign 
     *            数字签名 
     *   
     * @return 校验成功返回true 失败返回false 
     * @throws Exception 
     *   
     */ 
    public static boolean verify(byte[] data, String publicKey, String sign)   
            throws Exception {   

        // 解密由base64编码的公钥   
        byte[] keyBytes = Base64.decodeBase64(publicKey);

        // 构造X509EncodedKeySpec对象   
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法   
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取公钥匙对象   
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);

        // 验证签名是否正常   
        return signature.verify(Base64.decodeBase64(sign));
    }   

    /**
     * 解密<br> 
     * 用私钥解密 
     *   
     * @param data 
     * @param key 
     * @return 
     * @throws Exception 
     */ 
    public static byte[] decryptByPrivateKey(byte[] data, String key)   
            throws Exception {   
        // 对密钥解密   
        byte[] keyBytes = Base64.decodeBase64(key);

        // 取得私钥   
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密   
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }   
    
    /**
     * 解密 <br> 
     * 用私钥解密 
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data) throws Exception {
        return new String(decryptByPrivateKey(Hex.decodeHex(data.toCharArray()), getPrivateKey()));
    }

    /** 
     * 解密<br> 
     * 用公钥解密 
     *   
     * @param data 
     * @param key 
     * @return 
     * @throws Exception 
     */ 
    public static byte[] decryptByPublicKey(byte[] data, String key)   
            throws Exception {   
        // 对密钥解密   
        byte[] keyBytes = Base64.decodeBase64(key);

        // 取得公钥   
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密   
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }   
    
    /**
     * 解密 <br> 
     * 用公钥解密 
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String data) throws Exception {
        return new String(decryptByPrivateKey(Hex.decodeHex(data.toCharArray()), getPublicKey()));
    }

    /** 
     * 加密<br> 
     * 用公钥加密 
     *   
     * @param data 
     * @param key 
     * @return 
     * @throws Exception 
     */ 
    public static byte[] encryptByPublicKey(byte[] data, String key)   
            throws Exception {   
        // 对公钥解密   
        byte[] keyBytes = Base64.decodeBase64(key);

        // 取得公钥   
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密   
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }   
    
    /**
     * 加密<br> 
     * 用公钥加密 
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(byte[] data) throws Exception {
        return Hex.encodeHexStr(encryptByPublicKey(data, getPublicKey()));
    }

    /**
     * 加密<br> 
     * 用私钥加密 
     *   
     * @param data 
     * @param key 
     * @return 
     * @throws Exception 
     */ 
    public static byte[] encryptByPrivateKey(byte[] data, String key)   
            throws Exception {   
        // 对密钥解密   
        byte[] keyBytes = Base64.decodeBase64(key);

        // 取得私钥   
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据加密   
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }   
    
    /**
     * 加密<br> 
     * 用私钥加密 
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(byte[] data) throws Exception {
        return Hex.encodeHexStr(encryptByPrivateKey(data, getPrivateKey()));
    }

    /**
     * 取得私钥 
     *   
     * @param keyMap 
     * @return 
     * @throws Exception 
     */ 
    public static String getPrivateKey(Map<String, Object> keyMap)   
            throws Exception {   
        Key key = (Key) keyMap.get(PRIVATE_KEY);

        return Base64.encodeBase64String(key.getEncoded());
    }   

    /**
     * 取得公钥 
     *   
     * @param keyMap 
     * @return 
     * @throws Exception 
     */ 
    public static String getPublicKey(Map<String, Object> keyMap)   
            throws Exception {   
        Key key = (Key) keyMap.get(PUBLIC_KEY);

        return Base64.encodeBase64String(key.getEncoded());
    }   
    
    /**
     * 从配置文件中取得生成的公钥
     * @return
     */
    public static String getPublicKey() {
    	return Util.RSA_KEY.getProperty(PUBLIC_KEY);
    }
    
    /**
     * 从配置文件中取得生成的私钥
     * @return
     */
    public static String getPrivateKey() {
    	return Util.RSA_KEY.getProperty(PRIVATE_KEY);
    }

    /**
     * 初始化密钥 
     *   
     * @return 
     * @throws Exception 
     */ 
    public static Map<String, Object> initKey() throws Exception {   
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥   
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥   
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
    
    public static void main(String[] args) {
    	try {
//			Map<String, Object> keys = RSACoder.initKey();
//			System.out.println(getPublicKey(keys));
//			System.out.println(getPrivateKey(keys));
    		String data = RSACoder.encryptByPublicKey("90e2f088bf96d938fcc34c869abcf".getBytes());
            System.err.println("加密后:" + data);
            String dData = RSACoder.decryptByPrivateKey(data);
			System.out.println(dData);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
} 
