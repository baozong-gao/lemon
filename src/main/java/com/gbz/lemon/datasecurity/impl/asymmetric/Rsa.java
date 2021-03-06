package com.gbz.lemon.datasecurity.impl.asymmetric;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rsa {
	private static final Logger logger = LoggerFactory.getLogger(Rsa.class);

	// 私钥key
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	// 公钥key
	private static final String PUBLIC_KEY = "RSAPublicKey";
	// 加密算法
	private static final String KEY_ALGORITHM = "RSA";
	// 签名算法
	private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	// 密钥长度
	private static final int KEY_LENGTH = 1024;
	
	private static final int MAX_ENCRYPT_BLOCK = 117; 
	 
	private static final int MAX_DECRYPT_BLOCK = 128;  

	// 用来存放公钥密钥
	Map<String, Object> keyMap = new HashMap<String, Object>();

	private void initDefalutKey() {
		createKey(KEY_LENGTH);
	}

	public void initKey(int keySize) {
		createKey(keySize);
	}

	private void createKey(int keySize) {
		KeyPairGenerator keyPairGen;
		try {
			keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			keyPairGen.initialize(keySize);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			keyMap.put(PUBLIC_KEY, publicKey);
			keyMap.put(PRIVATE_KEY, privateKey);
		} catch (NoSuchAlgorithmException e) {
			logger.error("init key error", e);
		}
	}

	public void initKeyFromJks(String jksPath, String passwd, String priAlias,
			String pubAlias) {
		if (StringUtils.isBlank(jksPath)) {
			throw new NullPointerException("密钥库不存在：" + jksPath + ".");
		}
		try {
			KeyStore store = KeyStore.getInstance("JKS");
			FileInputStream fi = new FileInputStream(jksPath);
			if (StringUtils.isBlank(passwd)) {
				passwd = "";
			}
			store.load(fi, passwd.toCharArray());
			if(StringUtils.isNotBlank(priAlias)){
				RSAPrivateKey privateKey = (RSAPrivateKey)store.getKey(priAlias, passwd.toCharArray());
				keyMap.put(PRIVATE_KEY, privateKey);
				logger.info("加入私钥成功。");
			}
			if(StringUtils.isNotBlank(pubAlias)){
				Certificate certificate = store.getCertificate(pubAlias);
				RSAPublicKey pulbicKey = (RSAPublicKey)certificate.getPublicKey();
				keyMap.put(PUBLIC_KEY, pulbicKey);
				logger.info("加入公钥成功。");
			}
		} catch (Exception e) {
			logger.error("初始化密钥失败", e);
		}
	}
	
	public byte [] SignByPublicKey(byte [] data){
		try{
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, (Key) keyMap.get(PUBLIC_KEY)); 
			int inputLen = data.length;  
	        ByteArrayOutputStream out = new ByteArrayOutputStream();  
	        int offSet = 0;  
	        byte[] cache;  
	        int i = 0;  
	        // 对数据分段加密  
	        while (inputLen - offSet > 0) {  
	            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
	                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
	            } else {  
	                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
	            }  
	            out.write(cache, 0, cache.length);  
	            i++;  
	            offSet = i * MAX_ENCRYPT_BLOCK;  
	        }  
	        byte[] encryptedData = out.toByteArray();  
	        out.close();  
			return encryptedData;
		}catch(Exception e){
			logger.error("加密失败",e);
		}
		return null;
	}

	public byte [] nuSignByPrivateKey(byte [] data){
		try{
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, (Key) keyMap.get(PRIVATE_KEY)); 
			int inputLen = data.length;  
	        ByteArrayOutputStream out = new ByteArrayOutputStream();  
	        int offSet = 0;  
	        byte[] cache;  
	        int i = 0;  
	        // 对数据分段解密  
	        while (inputLen - offSet > 0) {  
	            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
	                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);  
	            } else {  
	                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
	            }  
	            out.write(cache, 0, cache.length);  
	            i++;  
	            offSet = i * MAX_DECRYPT_BLOCK;  
	        }  
	        byte[] decryptedData = out.toByteArray();  
	        out.close();  
			
			return decryptedData;
		}catch(Exception e){
			logger.error("解密失败",e);
		}
		return null;
	}
}
