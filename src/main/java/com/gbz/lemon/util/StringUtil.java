package com.gbz.lemon.util;

public class StringUtil {

	
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
}
