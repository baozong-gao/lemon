package com.gbz.lemon.util;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyUtil.class);
	
    //生成密钥库的类型（与密钥文件一致即可）
    public String keyType = "JKS";
	
	/**
	 * 获得密钥库
	 * @param keyPath   路径
	 * @param password   密码
	 * @return
	 */
	public KeyStore getKeyStore(String keyPath,String password){
		
		try {
			//生成密钥库
			KeyStore keyStore = KeyStore.getInstance(keyType);
			// 获得密钥库文件流  
	        FileInputStream is = new FileInputStream(keyPath);  
	        // 加载密钥库  
	        keyStore.load(is, password.toCharArray());  
	        // 关闭密钥库文件流  
	        is.close();  
	        
	        return keyStore; 
		} catch (KeyStoreException e) {
			logger.error("init keystore error.",e);
		} catch (FileNotFoundException e) {
			logger.error("read key file error.",e);
		} catch (Exception e) {
			logger.error("load key file error.",e);
		}
		
		return null;
	}
	
	/**
	 * 从密码库中得到私钥
	 *     可以直接从密钥库中获取私钥
	 * @param keyPath     密钥库文件
	 * @param password		密钥库密码
	 * @param alias			key别名
	 * @return
	 */
	public PrivateKey getPrivateKeyByKeyStore(String keyPath,String password,String alias){
		
		KeyStore keyStore = getKeyStore(keyPath,password);
		try {
			//第一种方式 
			KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));
			return pkEntry.getPrivateKey();
			//第二种方式
//			return (PrivateKey)keyStore.getKey(alias, password.toCharArray());
		} catch (Exception e) {
			logger.error("get private key error.",e);
		}
		return null;
	}
	/**
	 * 获取私钥对应的公钥
	 *      公钥不能直接获取，只能先获得私钥证书，在从证书中获取公钥
	 * 
	 * @param keyPath
	 * @param password
	 * @param alias
	 * @return
	 */
	public PublicKey getPublicKeyByPrivate(String keyPath,String password,String alias){
		KeyStore keyStore = getKeyStore(keyPath,password);
		try {
			//第一种方式 
			KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));
		    Certificate certificate = pkEntry.getCertificate();
		    return certificate.getPublicKey();
		} catch (Exception e) {
			logger.error("get public key error.",e);
		}
		return null;
	}
	
	/**
	 * 从密码库中得到公钥
	 *        公钥不能直接获取，只能先获得证书，在从证书中获取公钥
	 * @param keyPath     密钥库文件
	 * @param password		密钥库密码
	 * @param alias			key别名
	 * @return
	 */
	public PublicKey getPublicKeyByKeyStore(String keyPath,String password,String alias){
		
		KeyStore keyStore = getKeyStore(keyPath,password);
		try {
			//得到证书
			Certificate certificate = keyStore.getCertificate(alias);
			//从证书获得公钥
			return certificate.getPublicKey();
		} catch (Exception e) {
			logger.error("get public key error.",e);
		}
		return null;
	}
	
	/**
	 * 从证书文件（.cer）中获得公钥
	 * @param file
	 * @return
	 */
	public PublicKey getPublicKeyByFile(String file){
		CertificateFactory cf;
		try {
			cf = CertificateFactory.getInstance("X.509");
			FileInputStream fi = new FileInputStream(file);
			Certificate certificate = cf.generateCertificate(fi);
			return certificate.getPublicKey();
		} catch (Exception e) {
			logger.error("get public key error",e);
		}
		return null;
	}
	
	public Certificate getCertificateByPublic(String keyPath,String password,String alias){
		KeyStore keyStore = getKeyStore(keyPath,password);
		try {
			//得到证书
			return keyStore.getCertificate(alias);
		} catch (Exception e) {
			logger.error("get Certificate  error.",e);
		}
		return null;
	}
    	
	//验证
	public static void main(String[] args) throws Exception{
		String test = "gbz";
		KeyUtil keyUtil = new KeyUtil();
		PrivateKey privateKey = keyUtil.getPrivateKeyByKeyStore("E:\\zhengshu\\serverKeys.jks","123456","server");
		PublicKey publicKey = keyUtil.getPublicKeyByPrivate("E:\\zhengshu\\serverKeys.jks","123456","server");
		
		byte[] plainText = test.getBytes("UTF-8");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 用公钥进行加密，返回一个字节流
        byte[] cipherText = cipher.doFinal(plainText);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // 用私钥进行解密，返回一个字节流
        byte[] newPlainText = cipher.doFinal(cipherText);
        System.out.println(new String(newPlainText, "UTF-8"));
	}
	
}