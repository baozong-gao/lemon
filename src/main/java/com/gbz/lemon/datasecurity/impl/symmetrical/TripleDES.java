package com.gbz.lemon.datasecurity.impl.symmetrical;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TripleDES{

	private static final Logger logger = LoggerFactory.getLogger(TripleDES.class);
	//密码,最长24位
	public String key = null;
	//偏移量，最长8位
	public String keyIv = null;
	private SecretKeySpec deskey;
	private IvParameterSpec ivParam;
	
	public String DesType = "TripleDES/CBC/PKCS5Padding";
	
	public TripleDES(String key,String keyIv,String DesType){
		this.key = key;
		this.keyIv = keyIv;
		if(DesType != null){
			this.DesType = DesType;
		}
		logger.debug("init key {}, keyiv {}, destype {}.",new Object[]{this.key,this.keyIv,this.DesType});
	}
	
	
	public void initKey() throws UnsupportedEncodingException{
		if(key == null ){
			logger.error("init key error, key is null.");
			throw new RuntimeException();
		}
		byte[] key2 = build3DesKey(key,24);
		
		if(keyIv == null){
			logger.warn("key iv is null, use Before 8 from key .");
			keyIv = key.substring(0, key.length()>8?8:key.length());
		}
		byte[] keyiv = build3DesKey(keyIv,8);
		
		deskey = new SecretKeySpec(key2, "DESede");
        ivParam = new IvParameterSpec(keyiv);
	}
	
	/*
     * 根据字符串生成密钥字节数组
     * @param keyStr 密钥字符串
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte [] build3DesKey(String keyStr ,int size) throws UnsupportedEncodingException{
        byte[] key = new byte[size];    //声明字节数组，默认里面都是0
        byte[] temp = keyStr.getBytes("UTF-8");    //将字符串转成字节数组
       
        /*
         * 执行数组拷贝
         * System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
         */
        if(key .length > temp.length){
            //如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
            System. arraycopy(temp, 0, key, 0, temp.length );
        } else{
            //如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
            System. arraycopy(temp, 0, key, 0, key.length );
        }
        return key ;
    }

    
    public  byte[] TripleDES(byte[] sourceBuf,int type) throws Exception {
    	
    	initKey();
    	
    	byte[] cipherByte;
       // 使用DES对称加密算法的CBC模式加密
       Cipher encrypt = Cipher.getInstance(DesType);
       encrypt.init(type, deskey, ivParam);
       cipherByte = encrypt.doFinal(sourceBuf, 0, sourceBuf.length);
       // 返回加密后的字节数组
       return cipherByte;
  }
    
	public byte [] sign(byte [] data) {
		try {
			byte[] tripleDES = TripleDES(data,Cipher.ENCRYPT_MODE);
			return tripleDES;
		} catch (Exception e) {
			logger.error("sign 3des error.",e);
		}
		return null;
	}

	public String nuSign(byte [] signData) {
		try {
			byte[] tripleDES = TripleDES(signData,Cipher.DECRYPT_MODE);
			return new String(tripleDES);
		} catch (Exception e) {
			logger.error("sign 3des error.",e);
		}
		return null;
	}

}
