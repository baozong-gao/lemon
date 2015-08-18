package com.gbz.lemon.datasecurity.impl.asymmetric;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

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
	//密钥长度
	private static final int KEY_LENGTH = 1024;

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
			Map<String, Object> keyMap = new HashMap<String, Object>(2);
			keyMap.put(PUBLIC_KEY, publicKey);
			keyMap.put(PRIVATE_KEY, privateKey);
		} catch (NoSuchAlgorithmException e) {
			logger.error("init key error", e);
		}
	}
	
	
	

}
