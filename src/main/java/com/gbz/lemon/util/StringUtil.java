package com.gbz.lemon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {

	private static final Logger log = LoggerFactory.getLogger(StringUtil.class);
	/**
	 * 判断字符是否为空的
	 * @param s
	 * @return "" null  返回true
	 */
	public static boolean isEmpty(String s){
		
		if(s == null){
			return true;
		}else if("".equals(s.trim())){
			return true;
		}
		return false;
	}
	/**
	 * string 转为byte 数组，只处理单字节
	 * @param src
	 * @return
	 */
	public static byte [] stringTobyteArray(String src){
		log.debug("数据从string --> byte array 开始。。。");
		int length = src.length();
		byte [] data = new byte [length] ;
		for(int i = 0;i<length;i++){
			char charAt = src.charAt(i);
			if(charAt == '1'){
				data[i] = 1;
			}else{
				data[i] = 0;
			}
//			log.debug("数据{}为{}",i,data[i]);
		}
		log.debug("string --> byte array 结果：{}",data);
		return data;
	}
}
